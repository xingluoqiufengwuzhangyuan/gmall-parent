package com.atguigu.gmall.payment.service;

import com.alipay.api.AlipayApiException;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/3 21:07
 * @Version 1.8
 */
public interface AlipayService {
    String createAlipay(Long orderId) throws AlipayApiException;

    Boolean refund(Long orderId);

    Boolean closePay(Long orderId);

    Boolean checkPayment(Long orderId);
}
