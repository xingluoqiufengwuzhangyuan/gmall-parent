package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/27 22:12
 * @Version 1.8
 */
@RestController
@RequestMapping("api/cart")
public class CartApiController {

    @Resource
    private CartService cartService;

    @PostMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("skuNum") Integer skuNum,
                            HttpServletRequest request) {
        // 如何获取userId
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)) {
            // 获取临时用户Id
            userId = AuthContextHolder.getUserTempId(request);
        }
        cartService.addToCart(skuId, userId, skuNum);
        return Result.ok();
    }

    @GetMapping("cartList")
    public Result cartList(HttpServletRequest request) {
        // 获取用户Id
        String userId = AuthContextHolder.getUserId(request);
        // 获取临时用户Id
        String userTempId = AuthContextHolder.getUserTempId(request);
        List<CartInfo> cartInfoList = cartService.getCartList(userId, userTempId);
        return Result.ok(cartInfoList);
    }

    @GetMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable Long skuId,
                            @PathVariable Integer isChecked,
                            HttpServletRequest request
    ) {
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)) {
            userId = AuthContextHolder.getUserTempId(request);
        }
        cartService.checkCart(userId, skuId, isChecked);
        return Result.ok();
    }

    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable Long skuId,
                             HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)) {
            userId = AuthContextHolder.getUserTempId(request);
        }
        cartService.deleteCart(userId, skuId);
        return Result.ok();
    }

    @GetMapping("getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable(value = "userId") String userId) {
        return cartService.getCartCheckedList(userId);
    }

    @GetMapping("loadCartCache/{userId}")
    public Result loadCartCache(@PathVariable("userId") String userId) {
        cartService.loadCartCache(userId);
        return Result.ok();
    }

}
