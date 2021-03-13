package com.atguigu.gmall.mq.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.mq.config.DeadLetterMqConfig;
import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/2 21:08
 * @Version 1.8
 */
@RestController
@RequestMapping("/mq")
@Slf4j
public class MqController {

    @Resource
    private RabbitService rabbitService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("sendConfirm")
    public Result sendConfirm() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < 8; i++) {

            rabbitService.sendMessage("exchange.confirm", "routing.confirm", sdf.format(new Date())+"--"+i);
        }
        return Result.ok();
    }

    @GetMapping("sendDeadLettle")
    public Result sendDeadLettle() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.rabbitTemplate.convertAndSend(DeadLetterMqConfig.exchange_dead, DeadLetterMqConfig.routing_dead_1, "ok");
        System.out.println("消息发送了:\t"+sdf.format(new Date()) + " Delay sent.");
        return Result.ok();
    }
}


