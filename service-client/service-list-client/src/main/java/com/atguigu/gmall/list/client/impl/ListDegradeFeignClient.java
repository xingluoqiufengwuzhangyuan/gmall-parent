package com.atguigu.gmall.list.client.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/22 21:00
 * @Version 1.8
 */
@Component
@Slf4j
public class ListDegradeFeignClient implements ListFeignClient {
    @Override
    public Result incrHotScore(Long skuId) {
        Result<Object> objectResult = new Result<>();
        objectResult.setCode(1000);
        System.err.println("hotScore调用失败");
        return objectResult;
    }

    @Override
    public Result list(SearchParam listParam) {
        return null;
    }

    @Override
    public Result upperGoods(Long skuId) {
        return null;
    }

    @Override
    public Result lowerGoods(Long skuId) {
        return null;
    }
}
