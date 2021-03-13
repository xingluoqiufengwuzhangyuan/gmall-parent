package com.atguigu.gmall.payment.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.enums.PaymentType;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.payment.service.AlipayService;
import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/3 21:05
 * @Version 1.8
 */
@Controller
@RequestMapping("/api/payment/alipay")
public class AlipayController {

    @Resource
    private AlipayService alipayService;

    @Resource
    private PaymentService paymentService;

    @RequestMapping("submit/{orderId}")
    @ResponseBody
    public String submitOrder(@PathVariable(value = "orderId") Long orderId, HttpServletResponse response) {
        String form = "";
        try {
            form = alipayService.createAlipay(orderId);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return form;
    }

    @RequestMapping("callback/return")
    public String callBack() {
        // 同步回调给用户展示信息
        return "redirect:" + AlipayConfig.return_order_url;
    }

    @PostMapping("callback/notify")
    @ResponseBody
    public String callbackNotify(@RequestParam Map<String, String> paramMap) {
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(paramMap, AlipayConfig.alipay_public_key, AlipayConfig.charset);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (signVerified) {
//            获取out_trade_no与交易状态trade_status
            String tradeStatus = paramMap.get("trade_status");
            String outTradeNo = paramMap.get("out_trade_no");
            PaymentInfo paymentInfo = paymentService.getPaymentInfo(outTradeNo, PaymentType.ALIPAY.name());

            //如果能通过out_trade_no找到paymentInfo,即认为一致
            if (paymentInfo == null) {
                return "failure";
            }
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                if ("PAID".equals(paymentInfo.getPaymentStatus()) || "CLOSED".equals(paymentInfo.getPaymentStatus())) {
                    return "failure";
                }
                paymentService.paySuccess(outTradeNo, PaymentType.ALIPAY.name(), paramMap);
                return "success";
            } else {
                return "failure";
            }
        } else {
            return "failure";
        }
    }

    @GetMapping("refund/{orderId}")
    @ResponseBody
    public Result refund(@PathVariable Long orderId) {
        Boolean flag= alipayService.refund(orderId);
        return Result.ok(flag);
    }

    @GetMapping("closePay/{orderId}")
    @ResponseBody
    public Boolean closePay(@PathVariable Long orderId){
        Boolean aBoolean = alipayService.closePay(orderId);
        return aBoolean;
    }

    @RequestMapping("checkPayment/{orderId}")
    @ResponseBody
    public Boolean checkPayment(@PathVariable Long orderId){
        // 调用退款接口
        boolean flag = alipayService.checkPayment(orderId);
        return flag;
    }

    @GetMapping("getPaymentInfo/{outTradeNo}")
    @ResponseBody
    public PaymentInfo getPaymentInfo(@PathVariable String outTradeNo){
        PaymentInfo paymentInfo = paymentService.getPaymentInfo(outTradeNo, PaymentType.ALIPAY.name());
        if (null!=paymentInfo){
            return paymentInfo;
        }
        return null;
    }

}

