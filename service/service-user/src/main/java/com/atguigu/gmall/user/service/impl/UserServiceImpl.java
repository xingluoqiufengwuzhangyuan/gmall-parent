package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/24 21:22
 * @Version 1.8
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo login(UserInfo userInfo) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        byte[] bytes = userInfo.getPasswd().getBytes();
        String newPasswd = DigestUtils.md5DigestAsHex(bytes);
        userInfoQueryWrapper.
                eq("login_name",userInfo.getLoginName()).
                eq("passwd",newPasswd);
        UserInfo userInfo1 = userInfoMapper.selectOne(userInfoQueryWrapper);
        if (userInfo1 != null) {
            return userInfo1;
        }
        return null;
    }
}
