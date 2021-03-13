package com.atguigu.gmall.order.receiver;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.payment.client.PaymentFeignClient;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/3 17:00
 * @Version 1.8
 */
@Component
public class OrderReceiver {

    @Resource
    private OrderService orderService;

    @Resource
    private RabbitService rabbitService;

    @Resource
    private PaymentFeignClient paymentFeignClient;


    @RabbitListener(queues = MqConst.QUEUE_ORDER_CANCEL)
    public void orderCancel(Long orderId, Message message, Channel channel) throws IOException {

        // 判断订单Id 是否为空！
        // 这里不止要关闭电商平台的交易记录，还需要关闭支付宝的交易记录。
        if (null!=orderId){
            // 为了防止重复消息这个消息。判断订单状态
            // 通过订单Id 来获取订单对象 select * from orderInfo where id = orderId
            OrderInfo orderInfo = orderService.getById(orderId);
            // 涉及到关闭orderInfo ,paymentInfo ,aliPay
            // 订单状态是未支付
            if (null!= orderInfo && orderInfo.getOrderStatus().equals(ProcessStatus.UNPAID.getOrderStatus().name())){
                // 关闭过期订单
                // orderService.execExpiredOrder(orderId);
                // 订单创建时就是未付款，判断是否有交易记录产生
                PaymentInfo paymentInfo = paymentFeignClient.getPaymentInfo(orderInfo.getOutTradeNo());
                if (null!=paymentInfo && paymentInfo.getPaymentStatus().equals(PaymentStatus.UNPAID.name())){
                    // 先查看是否有交易记录 {用户是否扫了二维码}
                    Boolean aBoolean = paymentFeignClient.checkPayment(orderId);
                    if(aBoolean){
                        // 有交易记录 ，关闭支付宝 防止用户在过期时间到的哪一个瞬间，付款。
                        Boolean flag = paymentFeignClient.closePay(orderId);
                        if (flag){
                            // 用户未付款 ，开始关闭订单，关闭交易记录 2:表示要关闭交易记录paymentInfo 中有数据
                            orderService.execExpiredOrder(orderId,"2");
                        }else{
                            // 用户已经付款
                            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY,
                                    MqConst.ROUTING_PAYMENT_PAY,orderId);
                        }
                    }else{
                        // 在支付宝中没有交易记录，但是在电商中有交易记录
                        orderService.execExpiredOrder(orderId,"2");
                    }
                }else {
                    // 也就是说在paymentInfo 中根本没有交易记录。
                    orderService.execExpiredOrder(orderId,"1");
                }
            }
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_PAYMENT_PAY, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_PAYMENT_PAY, durable = "true", autoDelete = "false"),
            key = {MqConst.ROUTING_PAYMENT_PAY}
    ))
    public void updateOrderStatus(Long orderId, Message message, Channel channel) throws IOException {
        try {
            if (orderId != null) {
                OrderInfo orderInfo = orderService.getById(orderId);
                if (orderInfo != null && "UNPAID".equals(orderInfo.getOrderStatus())) {
                    orderService.updateOrderStatus(orderId, ProcessStatus.PAID);
                    orderService.sendOrderStatus(orderId);
                }
            }
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            e.printStackTrace();
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @SneakyThrows
    @RabbitListener(queues = MqConst.QUEUE_WARE_ORDER)
    public void updateOrders1(String jsonResult, Message message, Channel channel) {
        //  将这个Json 字符串转换为Map
        Map map = JSON.parseObject(jsonResult, Map.class);
        //  获取订单Id
        String orderId = (String) map.get("orderId");

        //  获取减库存结果：
        String status = (String) map.get("status");
        //  判断减库存状态
        if ("DEDUCTED".equals(status)) {
            //  表示减库存成功
            orderService.updateOrderStatus(Long.parseLong(orderId), ProcessStatus.WAITING_DELEVER);

        } else {
            //  表示减库存失败
            orderService.updateOrderStatus(Long.parseLong(orderId), ProcessStatus.STOCK_EXCEPTION);
            //  记录当前哪个订单异常，补货。 人工客服！
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
