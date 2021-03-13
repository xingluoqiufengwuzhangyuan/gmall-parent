package com.atguigu.gmall.order.client.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/1 22:20
 * @Version 1.8
 */
@Component
public class OrderDegradeFeignClient implements OrderFeignClient {

    @Override
    public Result<Map<String, Object>> trade() {
        return Result.fail();
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        return null;
    }
}
