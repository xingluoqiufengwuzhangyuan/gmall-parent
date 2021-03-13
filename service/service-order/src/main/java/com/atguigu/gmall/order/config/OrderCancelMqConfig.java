package com.atguigu.gmall.order.config;

import com.atguigu.gmall.common.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/3 16:40
 * @Version 1.8
 */
@Configuration
public class OrderCancelMqConfig {

    @Bean
    public Queue delayQueue() {
        return new Queue(MqConst.QUEUE_ORDER_CANCEL,true,false,false,null);
    }

    @Bean
    public CustomExchange delayExchange() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-delayed-type", "direct");
        return new CustomExchange(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL, "x-delayed-message", true, false, map);
    }

    @Bean
    public Binding delayBinding1() {
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(MqConst.ROUTING_ORDER_CANCEL).noargs();

    }
}
