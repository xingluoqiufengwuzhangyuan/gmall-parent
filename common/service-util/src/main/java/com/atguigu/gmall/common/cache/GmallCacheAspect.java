package com.atguigu.gmall.common.cache;

import ch.qos.logback.core.joran.conditional.ElseAction;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.RedisConst;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/19 20:13
 * @Version 1.8
 */
@Component
@Aspect
public class GmallCacheAspect {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @SneakyThrows
    @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint) {
        Object object = new Object();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class);
        String prefix = annotation.prefix();
        Object[] args = joinPoint.getArgs();
        String skuKey = prefix + Arrays.asList(args).toString();

        try {

            //        查找缓存是否有相关数据
            object = cacheHit(skuKey, signature);
            if (object == null) {
                String lockKey = prefix + ":lock";
                RLock lock = redissonClient.getLock(lockKey);

                boolean result = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                if (result) {
                    try {
                         object = joinPoint.proceed(joinPoint.getArgs());
                        if (object == null) {
                            Object o = new Object();
                            redisTemplate.opsForValue().set(skuKey, JSON.toJSONString(o),RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                            return o;
                        }
                        redisTemplate.opsForValue().set(skuKey,JSON.toJSONString(object),RedisConst.SKUKEY_TEMPORARY_TIMEOUT);
                        return object;
                    } finally {
                        lock.unlock();
                    }
                } else {
                    Thread.sleep(1000);
                    cacheAroundAdvice(joinPoint);
                }
            } else {

                return object;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return joinPoint.proceed(joinPoint.getArgs());
    }

    private Object cacheHit(String skuKey, MethodSignature signature) {
        String o = (String) redisTemplate.opsForValue().get(skuKey);
        if (!StringUtils.isEmpty(o)) {
            return JSON.parseObject(o, signature.getReturnType());
        }
        return null;
    }
}
