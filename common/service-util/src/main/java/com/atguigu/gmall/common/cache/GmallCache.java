package com.atguigu.gmall.common.cache;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/19 20:10
 * @Version 1.8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {
    String prefix() default "cache";
}
