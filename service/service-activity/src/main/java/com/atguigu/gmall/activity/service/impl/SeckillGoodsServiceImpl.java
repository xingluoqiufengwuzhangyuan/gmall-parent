package com.atguigu.gmall.activity.service.impl;

import com.atguigu.gmall.activity.service.SeckillGoodsService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: XLQFWZY
 * @Date: 2021/3/11 16:45
 * @Version 1.8
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public List<SeckillGoods> findAll() {
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).values();
        return seckillGoodsList;
    }

    @Override
    public SeckillGoods getSeckillGoods(Long id) {
        SeckillGoods seckillGoods =(SeckillGoods) redisTemplate.opsForHash().get(RedisConst.SECKILL_GOODS, id.toString());
        return seckillGoods;
    }
}
