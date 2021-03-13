package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/4 1:02
 * @Version 1.8
 */
@RestController
@RequestMapping("api/item")
public class ItemController {
    @Resource
    private ItemService itemService;

    @GetMapping("{skuId}")
    public Result getItem(@PathVariable Long skuId) {
        Map<String, Object> result = itemService.getBySkuId(skuId);
        return Result.ok(result);
    }


}
