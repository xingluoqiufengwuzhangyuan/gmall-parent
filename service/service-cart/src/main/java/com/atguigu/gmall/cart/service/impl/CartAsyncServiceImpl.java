package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartAsyncService;
import com.atguigu.gmall.model.cart.CartInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/27 21:24
 * @Version 1.8
 */
@Service
public class CartAsyncServiceImpl implements CartAsyncService {

    @Resource
    private CartInfoMapper cartInfoMapper;

    @Async
    @Override
    public void saveCartInfo(CartInfo cartInfo) {
        cartInfoMapper.insert(cartInfo);
    }

    @Async
    @Override
    public void updateCartInfo(CartInfo cartInfo) {
        QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
        cartInfoQueryWrapper.eq("sku_id", cartInfo.getSkuId())
                .eq("user_id", cartInfo.getUserId());
        cartInfoMapper.update(cartInfo, cartInfoQueryWrapper);
    }

    @Async
    @Override
    public void deleteCartInfo(String tempUserId) {
        cartInfoMapper.delete(new QueryWrapper<CartInfo>().eq("user_id", tempUserId));
    }

    @Async
    @Override
    public void checkCart(String userId, Long skuId, Integer isChecked) {
        QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
        cartInfoQueryWrapper.eq("user_id", userId)
                .eq("sku_id", skuId);
        CartInfo cartInfo = new CartInfo();
        cartInfo.setIsChecked(isChecked);


        cartInfoMapper.update(cartInfo, cartInfoQueryWrapper);
    }

    @Async
    @Override
    public void deleteCart(String userId, Long skuId) {
        QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
        cartInfoQueryWrapper.eq("user_id", userId)
                .eq("sku_id", skuId);
        cartInfoMapper.delete(cartInfoQueryWrapper);
    }
}
