package com.inside.house.web.controller;

import com.inside.house.biz.service.UserService;
import com.inside.house.common.constants.CommonConstants;
import com.inside.house.common.model.User;
import com.inside.house.common.result.ResultMsg;
import com.inside.house.common.utils.HashUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    @RequestMapping("accounts/signin")
    public String signin(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // 拦截前的目标页
        String target = request.getParameter("target");
        if (username == null || password == null) {
            request.setAttribute("target", target);
            return "/user/accounts/signin";
        }
        User user = userService.auth(username, password);
        if (user == null) {
            return "redirect:/accounts/signin?" + "target=" + target +
                    "&username=" + username + "&" + ResultMsg.errorMsg("用户名或密码错误").asUrlParams();
        } else {
            HttpSession session = request.getSession(true);
            session.setAttribute(CommonConstants.USER_ATTRIBUTE, user);
            //session.setAttribute(CommonConstants.PLAIN_USER_ATTRIBUTE, user);
            return StringUtils.isNoneBlank(target) ? "redirect:" + target : "redirect:/index";
        }
    }

    @RequestMapping("accounts/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.invalidate();
        return "redirect:/index";
    }

    @RequestMapping("accounts/profile")
    public String profile(HttpServletRequest request, User updateUser, ModelMap modelMap) {
        if (updateUser.getEmail() == null) {
            return "/user/accounts/profile";
        }
        userService.updateUser(updateUser, updateUser.getEmail());
        User query = new User();
        query.setEmail(updateUser.getEmail());
        List<User> users = userService.getUserByQuery(query);
        request.getSession().setAttribute(CommonConstants.USER_ATTRIBUTE, users.get(0));
        return "redirect:/accounts/profile?" + ResultMsg.successMsg("更新成功").asUrlParams();
    }

    @RequestMapping("accounts/changePassword")
    public String changePassword(String email, String password, String newPassword, String confirmPassword, ModelMap modelMap) {
        User user = userService.auth(email, password);
        if (user == null || !confirmPassword.equals(newPassword))
            return "redirect:/accounts/profile?" + ResultMsg.errorMsg("密码错误").asUrlParams();
        User updateUser = new User();
        updateUser.setPasswd(HashUtils.encryptPassword(newPassword));
        userService.updateUser(updateUser, updateUser.getEmail());
        return "redirect:/accounts/profile?" + ResultMsg.successMsg("密码更新成功").asUrlParams();
    }
}
