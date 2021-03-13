package com.atguigu.gmall.item.service;

import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/4 0:57
 * @Version 1.8
 */
public interface ItemService {
    Map<String, Object> getBySkuId(Long skuId);

}
