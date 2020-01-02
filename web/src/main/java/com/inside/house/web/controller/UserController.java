package com.inside.house.web.controller;

import com.inside.house.biz.service.UserService;
import com.inside.house.common.model.User;
import com.inside.house.common.result.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("accounts/register")
    public String accountsRegister(User account, ModelMap modelMap) {
        //GET请求
        if (account == null || account.getName() == null)
            return "/user/accounts/register";
        //POST请求
        ResultMsg resultMsg = UserHelper.validate(account);
        if (resultMsg.isSuccess() && userService.addAccount(account)) {
            modelMap.put("email", account.getEmail());
            return "/user/accounts/registerSubmit";
        } else {
            return "redirect:/accounts/register?" + resultMsg.asUrlParams();
        }
    }

    @RequestMapping("accounts/verify")
    public String verify(String key) {
        boolean result = userService.enable(key);
        if (result)
            return "redirect:/index?" + ResultMsg.successMsg("激活成功").asUrlParams();
        else
            return "redirect:/accounts/register?" + ResultMsg.errorMsg("激活失败");
    }

}
