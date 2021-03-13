package com.atguigu.gmall.user.client.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/1 22:04
 * @Version 1.8
 */
@Component
public class UserDegradeFeignClient implements UserFeignClient {
    @Override
    public List<UserAddress> findUserAddressListByUserId(String userId) {
        return null;
    }
}
