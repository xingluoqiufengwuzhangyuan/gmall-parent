package com.atguigu.gmall.mq.receiver;

import com.atguigu.gmall.mq.config.DeadLetterMqConfig;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/2 21:42
 * @Version 1.8
 */
@Component
public class DeadLetterReceiver {

    @RabbitListener(queues = DeadLetterMqConfig.queue_dead_2)
    @SneakyThrows
    public void getMsg(String msg, Message message, Channel channel){
        //  获取到当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(simpleDateFormat.format(new Date())+"接收的消息：\t"+msg);

        //  确认消息
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @SneakyThrows
    @RabbitListener(bindings=@QueueBinding(
            value = @Queue(value = "queue.confirm",autoDelete = "false"),
            exchange = @Exchange(value = "exchange.confirm",autoDelete = "false"),
            key = {"routing.confirm"}))
    public void process(Message message, Channel channel) {
        System.out.println("RabbitListener:" + new String(message.getBody()));
    }
    }
