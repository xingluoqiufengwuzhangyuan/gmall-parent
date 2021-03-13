package com.atguigu.gmall.activity.redis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/11 16:32
 * @Version 1.8
 */
public class CacheHelper {
    private final static Map<String, Object> cacheMap = new ConcurrentHashMap<String, Object>();

    /**
     * 加入缓存
     *
     * @param key
     * @param cacheObject
     */
    public static void put(String key, Object cacheObject) {
        cacheMap.put(key, cacheObject);
    }

    /**
     * 获取缓存
     * @param key
     * @return
     */
    public static Object get(String key) {
        return cacheMap.get(key);
    }

    /**
     * 清除缓存
     *
     * @param key
     * @return
     */
    public static void remove(String key) {
        cacheMap.remove(key);
    }

    public static synchronized void removeAll() {
        cacheMap.clear();
    }

}
