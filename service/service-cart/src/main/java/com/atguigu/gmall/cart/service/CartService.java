package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/27 20:43
 * @Version 1.8
 */
public interface CartService {

    void addToCart(Long skuId, String userId, Integer skuNum);

    List<CartInfo> getCartList(String userId, String userTempId);


    void checkCart(String userId, Long skuId,Integer isChecked);

    void deleteCart(String userId, Long skuId);

    List<CartInfo> getCartCheckedList(String userId);

    List<CartInfo> loadCartCache(String userId);
}
