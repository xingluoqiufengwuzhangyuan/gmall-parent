package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/28 18:31
 * @Version 1.8
 */
@Controller
public class CartController {

    @Resource
    private CartFeignClient cartFeignClient;

    @Resource
    private ProductFeignClient productFeignClient;

    @GetMapping("addCart.html")
    public String addCart(HttpServletRequest request) {
        String skuNum = request.getParameter("skuNum");
        String skuId = request.getParameter("skuId");

        SkuInfo skuInfo = productFeignClient.getSkuInfo(Long.parseLong(skuId));
        request.setAttribute("skuInfo", skuInfo);
        request.setAttribute("skuNum", skuNum);
        cartFeignClient.addToCart(Long.parseLong(skuId), Integer.parseInt(skuNum));
        return "cart/addCart";
    }

    @RequestMapping("cart.html")
    public String cartList() {

        return "cart/index";
    }
}

