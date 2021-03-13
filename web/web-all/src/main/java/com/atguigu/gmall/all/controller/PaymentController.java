package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/3 20:12
 * @Version 1.8
 */
@Controller
public class PaymentController {

    @Resource
    private OrderFeignClient orderFeignClient;

    @GetMapping("pay.html")
    public String success(@RequestParam("orderId") Long orderId,
                          Model model) {
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        model.addAttribute("orderInfo",orderInfo);
        return "payment/pay";
    }

    @GetMapping("pay/success.html")
    public String success() {
        return "payment/success";
    }

}
