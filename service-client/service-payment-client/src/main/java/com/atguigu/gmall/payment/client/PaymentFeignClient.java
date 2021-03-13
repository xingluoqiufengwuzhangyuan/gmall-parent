package com.atguigu.gmall.payment.client;

import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.client.impl.PaymentFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/7 16:42
 * @Version 1.8
 */
@FeignClient(value = "service-payment",fallback = PaymentFeignClientImpl.class)
public interface PaymentFeignClient {

    @GetMapping("api/payment/alipay/closePay/{orderId}")
    Boolean closePay(@PathVariable Long orderId);

    @GetMapping("api/payment/alipay/checkPayment/{orderId}")
    Boolean checkPayment(@PathVariable Long orderId);

    @GetMapping("api/payment/alipay/getPaymentInfo/{outTradeNo}")
    PaymentInfo getPaymentInfo(@PathVariable String outTradeNo);

}

