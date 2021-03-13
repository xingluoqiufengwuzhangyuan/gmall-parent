package com.atguigu.gmall.activity.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: XLQFWZY
 * @Date: 2021/3/11 16:45
 * @Version 1.8
 */
public interface SeckillGoodsService {
    List<SeckillGoods> findAll();


    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    SeckillGoods getSeckillGoods(Long id);

}
