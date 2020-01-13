package com.inside.house.web;

import com.inside.house.biz.service.UserService;
import com.inside.house.common.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthTests {

    @Autowired
    private UserService userService;

    @Test
    public void testAuth() {
        User user = userService.auth("363644935@qq.com","z55182182");
        assert user !=null;
    }
}
