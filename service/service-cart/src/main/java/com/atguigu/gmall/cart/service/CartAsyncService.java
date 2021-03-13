package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/27 21:23
 * @Version 1.8
 */
public interface CartAsyncService {

    void saveCartInfo(CartInfo cartInfo);

    void updateCartInfo(CartInfo cartInfo);

    void deleteCartInfo(String tempUserId);

    void checkCart(String userId, Long skuId, Integer isChecked);

    void deleteCart(String userId, Long skuId);
}
