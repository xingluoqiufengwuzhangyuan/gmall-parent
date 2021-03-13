package com.atguigu.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/1/30 20:37
 * @Version 1.8
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"com.atguigu.gmall"})
public class ServiceProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class, args);
    }
}
