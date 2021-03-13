package com.atguigu.gmall.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/3 20:00
 * @Version 1.8
 */
@SpringBootApplication
@ComponentScan({"com.atguigu.gmall"})
@EnableFeignClients(basePackages = {"com.atguigu.gmall"})
@EnableDiscoveryClient
public class ServicePaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServicePaymentApplication.class, args);
    }
}
