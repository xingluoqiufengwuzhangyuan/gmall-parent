package com.atguigu.gmall.task.scheduled;

import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.service.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/11 15:55
 * @Version 1.8
 */
@EnableScheduling
@Slf4j
@Component
public class ScheduledTask {
    @Resource
    private RabbitService rabbitService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void task1() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,  MqConst.ROUTING_TASK_1, "");
    }

}
