package com.atguigu.gmall.order.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.client.impl.OrderDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/1 22:19
 * @Version 1.8
 */
@FeignClient(value = "service-order", fallback = OrderDegradeFeignClient.class)
public interface OrderFeignClient {

    @GetMapping("/api/order/auth/trade")
    Result<Map<String, Object>> trade();

    @GetMapping("/api/order/inner/getOrderInfo/{orderId}")
    public OrderInfo getOrderInfo(@PathVariable(value = "orderId") Long orderId);


}
