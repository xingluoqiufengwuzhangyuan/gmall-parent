package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/24 21:19
 * @Version 1.8
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 获取用户地址
     * @param userId
     * @return
     */
    @GetMapping("inner/findUserAddressListByUserId/{userId}")
    public List<UserAddress> findUserAddressListByUserId(@PathVariable("userId") String userId){
        return userAddressService.findUserAddressListByUserId(userId);
    }

}
