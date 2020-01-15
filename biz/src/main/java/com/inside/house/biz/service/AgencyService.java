package com.inside.house.biz.service;

import com.inside.house.biz.mapper.AgencyMapper;
import com.inside.house.biz.mapper.UserMapper;
import com.inside.house.common.model.User;
import com.inside.house.common.page.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgencyService {

    @Autowired
    private AgencyMapper agencyMapper;
    @Value("${file.prefix}")
    private String imgPrefix;

    public User getAgentDetail(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setType(2);
        List<User> list = agencyMapper.selectAgent(user, PageParams.build(1, 1));
        setImg(list);
        if (!list.isEmpty())
            return list.get(0);
        return null;
    }

    private void setImg(List<User> list) {
        list.forEach(i -> {
            i.setAvatar(imgPrefix + i.getAvatar());
        });
    }
}
