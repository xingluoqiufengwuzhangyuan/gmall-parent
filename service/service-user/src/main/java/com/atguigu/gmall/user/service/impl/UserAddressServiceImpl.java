package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.service.UserAddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/1 21:56
 * @Version 1.8
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Override
    public List<UserAddress> findUserAddressListByUserId(String userId) {
            // 操作哪个数据库表，则就使用哪个表对应的mapper！
            // new Example() ; 你操作的哪个表，则对应的传入表的实体类！
            // select * from userAddress where userId = ？;
            QueryWrapper<UserAddress> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            List<UserAddress> userAddressList = userAddressMapper.selectList(queryWrapper);
            return userAddressList;

    }
}
