package com.inside.house.biz.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Lists;
import com.inside.house.biz.mapper.UserMapper;
import com.inside.house.common.model.User;
import com.inside.house.common.utils.BeanHelper;
import com.inside.house.common.utils.HashUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private MailService mailService;
    @Value("${file.prefix}")
    private String imgPrefix;


    public List<User> getUsers() {
        return userMapper.selectUsers();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addAccount(User account) {
        account.setPasswd(HashUtils.encryptPassword(account.getPasswd()));
        List<String> imgList = fileService.getImgPath(Lists.newArrayList(account.getAvatarFile()));
        if (!imgList.isEmpty())
            account.setAvatar(imgList.get(0));
        BeanHelper.setDefaultProp(account, User.class);
        BeanHelper.onInsert(account);
        account.setEnable(0);
        userMapper.insert(account);
        mailService.registerNotify(account.getEmail());
        return true;
    }


    public boolean enable(String key) {
        return mailService.enable(key);
    }

    public User auth(String username, String password) {
        User user = new User();
        user.setEmail(username);
        user.setPasswd(HashUtils.encryptPassword(password));
        user.setEnable(1);
        List<User> users = getUserByQuery(user);
        if (!users.isEmpty())
            return users.get(0);
        return null;
    }

    private List<User> getUserByQuery(User user) {
        List<User> users = userMapper.selectUsersByQuery(user);
        users.forEach(u -> {
            u.setAvatar(imgPrefix + u.getAvatar());
        });
        return users;
    }
}
