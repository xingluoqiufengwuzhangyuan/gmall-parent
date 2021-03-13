package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/24 22:37
 * @Version 1.8
 */
@Controller
public class PassportController {
    @RequestMapping("login.html")
    public String login(HttpServletRequest request) {
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl",originUrl);
        return "login";
    }
}
