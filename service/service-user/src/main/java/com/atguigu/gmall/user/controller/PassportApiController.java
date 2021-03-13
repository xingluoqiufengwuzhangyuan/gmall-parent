package com.atguigu.gmall.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import com.atguigu.gmall.user.service.impl.UserServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/24 21:33
 * @Version 1.8
 */
@RestController
@RequestMapping("/api/user/passport")
public class PassportApiController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @PostMapping("login")
    public Result login(@RequestBody UserInfo userInfo, HttpServletRequest request) {
        UserInfo login = userService.login(userInfo);
        if (login != null) {
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            String token = UUID.randomUUID().toString();
            stringObjectHashMap.put("token", token);
            String nickName = login.getNickName();
            stringObjectHashMap.put("nickname", nickName);
            String userKey = RedisConst.USER_LOGIN_KEY_PREFIX + token;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", login.getId().toString());
            jsonObject.put("ip", IpUtil.getIpAddress(request));
            redisTemplate.opsForValue().set(userKey, jsonObject.toJSONString(), RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);
            return Result.ok(stringObjectHashMap);
        } else {
            return Result.fail().message("登录失败");
        }
    }

    @GetMapping("logout")
    public Result logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        String userKey = RedisConst.USER_LOGIN_KEY_PREFIX + token;
        redisTemplate.delete(userKey);
        return Result.ok();
    }
}
