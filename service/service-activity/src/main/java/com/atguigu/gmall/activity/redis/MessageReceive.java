package com.atguigu.gmall.activity.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/11 16:31
 * @Version 1.8
 */
@Component
public class MessageReceive {

    /**
     * 接收消息的方法
     */
    public void receiveMessage(String message) {
        System.out.println("----------收到消息了message：" + message);
        if (!StringUtils.isEmpty(message)) {
            /*
             消息格式
                skuId:0 表示没有商品
                skuId:1 表示有商品
             */
            // 因为传递过来的数据为 “”6:1””
            message = message.replaceAll("\"", "");
            String[] split = StringUtils.split(message, ":");
            if (split == null || split.length == 2) {
                CacheHelper.put(split[0], split[1]);
            }
        }
    }
}



