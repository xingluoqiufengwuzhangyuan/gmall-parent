package com.atguigu.gmall.item.client.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.stereotype.Component;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/4 23:26
 * @Version 1.8
 */
@Component
public class ItemDegradeFeignClient implements ItemFeignClient {
    @Override
    public Result getItem(Long skuId) {
        return Result.fail();
    }
}
