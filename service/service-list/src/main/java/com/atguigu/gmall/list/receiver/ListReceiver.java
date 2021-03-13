package com.atguigu.gmall.list.receiver;

import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.list.service.SearchService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/2 21:23
 * @Version 1.8
 */
@Component
public class ListReceiver {

    @Resource
    private RabbitService rabbitService;

    @Resource
    private SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_GOODS_LOWER, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_GOODS, durable = "true"),
            key = {MqConst.ROUTING_GOODS_LOWER}
    ))
    public void lowerGoods(Long skuId, Message message, Channel channel) throws IOException {
        if (skuId != null) {
            searchService.lowerGoods(skuId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_GOODS_UPPER, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_GOODS, durable = "true"),
            key = {MqConst.QUEUE_GOODS_UPPER}
    ))
    public void upperGoods(Long skuId, Message message, Channel channel) throws IOException {
        if (skuId != null) {
            searchService.upperGoods(skuId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
