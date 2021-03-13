package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/1 21:54
 * @Version 1.8
 */
public interface UserAddressService {
    List<UserAddress> findUserAddressListByUserId(String userId);
}
