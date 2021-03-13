package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/4 23:28
 * @Version 1.8
 */
@Api("商品详情控制器")
@Controller
public class ItemController {

    @Resource
    private ItemFeignClient itemFeignClient;

    @RequestMapping("{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model){
        // 通过skuId 查询skuInfo
        Result<Map> result = itemFeignClient.getItem(skuId);
        model.addAllAttributes(result.getData());
        return "item/index";
    }

}
