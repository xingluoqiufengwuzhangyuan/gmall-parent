package com.atguigu.gmall.payment.service;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/3 20:22
 * @Version 1.8
 */
public interface PaymentService {

    void savePaymentInfo(OrderInfo orderInfo,String paymentType);


    PaymentInfo getPaymentInfo(String outTradeNo, String name);

    void paySuccess(String outTradeNo, String name, Map<String, String> paramMap);

    public void updatePaymentInfo(String outTradeNo, String name, PaymentInfo paymentInfo);

    void closePayment(Long orderId);
}
