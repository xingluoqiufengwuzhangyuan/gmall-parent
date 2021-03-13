package com.atguigu.gmall.activity.receiver;

import com.atguigu.gmall.activity.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/11 15:58
 * @Version 1.8
 */
@Component
public class SeckillReceiver {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_TASK_1),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_TASK),
            key = {MqConst.ROUTING_TASK_1}
    ))
    public void importItemToRedis(Message message, Channel channel) {
        QueryWrapper<SeckillGoods> seckillGoodsQueryWrapper = new QueryWrapper<>();
        seckillGoodsQueryWrapper.ge("stock_count",0).eq("status",1).eq("DATE_FORMAT(start_time,'%Y-%m-%d')", DateUtil.formatDate(new Date()));

        List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectList(seckillGoodsQueryWrapper);

        if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
            seckillGoodsList.stream().forEach(seckillGoods -> {
                Boolean hasKey = redisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).hasKey(seckillGoods.getSkuId().toString());
                if (!hasKey) {
                    redisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).put(seckillGoods.getSkuId().toString(), seckillGoods);
                }
                for (Integer i = 0; i < seckillGoods.getStockCount(); i++) {
                    redisTemplate.boundListOps(RedisConst.SECKILL_STOCK_PREFIX + seckillGoods.getSkuId()).leftPush(seckillGoods.getSkuId().toString());
                }
            });
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

}
