package com.inside.house.biz.service;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.inside.house.biz.mapper.UserMapper;
import com.inside.house.common.model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${domain.name}")
    private String domainName;
    @Autowired
    private UserMapper userMapper;
    @Value("${spring.mail.username}")
    private String from;
    private final Cache<String, String> registerCache = CacheBuilder.newBuilder().
            maximumSize(100).
            expireAfterAccess(15, TimeUnit.MINUTES).
            removalListener(new RemovalListener<String, String>(){
                public void onRemoval(RemovalNotification<String, String> notification){
                    String email = notification.getValue();
                    User user = new User();
                    user.setEmail(email);
                    List<User> targetUser = userMapper.selectUsersByQuery(user);
                    if (!targetUser.isEmpty() && Objects.equal(targetUser.get(0).getEnable(), 0)) {
                        userMapper.delete(email);// 代码优化: 在删除前首先判断用户是否已经被激活，对于未激活的用户进行移除操作
                    }
                }
            }).build();

    public void sendMail(String title, String verifyUrl, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setText(verifyUrl);
        message.setSubject(title);
        mailSender.send(message);
    }

    @Async
    public void registerNotify(String email) {
        String randomKey = RandomStringUtils.randomAlphabetic(10);
        registerCache.put(randomKey, email);
        String verifyUrl = "http://" + domainName + "/accounts/verify?key="+randomKey;
        sendMail("房产平台激活邮箱", verifyUrl, email);
    }

    public boolean enable(String key) {
        String email = registerCache.getIfPresent(key);
        if (StringUtils.isBlank(email))
            return false;
        User updateUser = new User();
        updateUser.setEmail(email);
        updateUser.setEnable(1);
        userMapper.update(updateUser);
        registerCache.invalidate(key);
        return true;
    }
}
