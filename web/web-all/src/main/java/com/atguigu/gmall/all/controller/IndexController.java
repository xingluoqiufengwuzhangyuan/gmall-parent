package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/22 0:00
 * @Version 1.8
 */
@Controller
public class IndexController {
    @Resource
    ProductFeignClient productFeignClient;

    @GetMapping({"/", "index.html"})
    public String index(HttpServletRequest request) {
        Result result = productFeignClient.getBaseCategoryList();
        request.setAttribute("list", result.getData());
        return "index/index";
    }
}
