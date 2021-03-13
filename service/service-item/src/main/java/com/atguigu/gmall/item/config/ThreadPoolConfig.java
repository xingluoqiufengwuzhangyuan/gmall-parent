package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/22 21:09
 * @Version 1.8
 */
@Configuration
public class ThreadPoolConfig {

    //  制作线程池
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        //  new ThreadPoolExecutor
        //  核心线程池数跟你的硬件有关系！
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                10,
                50,
                5L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5)

        );
        return threadPoolExecutor;
    }
}