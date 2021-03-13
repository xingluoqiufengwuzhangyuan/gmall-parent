package com.atguigu.gmall.payment.service.impl;

import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.PaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/3 20:23
 * @Version 1.8
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private PaymentInfoMapper paymentInfoMapper;


    @Resource
    private RabbitService rabbitService;

    @Override
    public void savePaymentInfo(OrderInfo orderInfo, String paymentType) {
        QueryWrapper<PaymentInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
        paymentInfoQueryWrapper.eq("order_id", orderInfo.getId())
                .eq("payment_type", paymentType);
        Integer count = paymentInfoMapper.selectCount(paymentInfoQueryWrapper);
        if (count > 0) {
            return;
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
        paymentInfo.setSubject(orderInfo.getTradeBody());
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());

        paymentInfoMapper.insert(paymentInfo);

    }

    @Override
    public PaymentInfo getPaymentInfo(String outTradeNo, String name) {
        QueryWrapper<PaymentInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
        paymentInfoQueryWrapper.eq("out_trade_no", outTradeNo)
                .eq("payment_type", name);
        return paymentInfoMapper.selectOne(paymentInfoQueryWrapper);

    }

    @Override
    public void paySuccess(String outTradeNo, String name, Map<String, String> paramMap) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setTradeNo(paramMap.get("trade_no"));
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(paramMap.toString());
        paymentInfo.setPaymentStatus(ProcessStatus.PAID.name());

        updatePaymentInfo(outTradeNo, name, paymentInfo);
        PaymentInfo paymentInfo1 = getPaymentInfo(outTradeNo, name);

        if ("CLOSED".equals(paymentInfo1.getPaymentStatus()) || "PAID".equals(paymentInfo1.getPaymentStatus())) {

            return;
        }
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY, MqConst.ROUTING_PAYMENT_PAY, paymentInfo1.getOrderId());
    }

    @Override
    public void updatePaymentInfo(String outTradeNo, String name, PaymentInfo paymentInfo) {
        QueryWrapper<PaymentInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
        paymentInfoQueryWrapper.eq("out_trade_no", outTradeNo)
                .eq("payment_type", name);
        paymentInfoMapper.update(paymentInfo, paymentInfoQueryWrapper);
    }

    @Override
    public void closePayment(Long orderId) {
        // 设置关闭交易记录的条件  118
        QueryWrapper<PaymentInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
        paymentInfoQueryWrapper.eq("order_id",orderId);
        // 如果当前的交易记录不存在，则不更新交易记录
        Integer count = paymentInfoMapper.selectCount(paymentInfoQueryWrapper);
        if (null == count || count.intValue()==0) return;
        // 在关闭支付宝交易之前。还需要关闭paymentInfo
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus(PaymentStatus.ClOSED.name());
        paymentInfoMapper.update(paymentInfo,paymentInfoQueryWrapper);


    }

}
