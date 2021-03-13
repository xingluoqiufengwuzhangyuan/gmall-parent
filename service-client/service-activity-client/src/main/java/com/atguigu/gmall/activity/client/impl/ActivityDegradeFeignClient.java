package com.atguigu.gmall.activity.client.impl;

import com.atguigu.gmall.activity.client.ActivityFeignClient;
import com.atguigu.gmall.common.result.Result;

/**
 * @Created by IntelliJ IDEA.
 * @Author: XLQFWZY
 * @Date: 2021/3/11 17:17
 * @Version 1.8
 */
public class ActivityDegradeFeignClient implements ActivityFeignClient {
    @Override
    public Result findAll() {
        return Result.fail();
    }

    @Override
    public Result getSeckillGoods(Long skuId) {
        return Result.fail();
    }
}
