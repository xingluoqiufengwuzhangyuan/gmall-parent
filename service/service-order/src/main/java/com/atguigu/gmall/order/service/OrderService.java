package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/1 22:25
 * @Version 1.8
 */
public interface OrderService extends IService<OrderInfo> {

    Long saveOrderInfo(OrderInfo orderInfo);

    /**
     * 生产流水号
     * @param userId
     * @return
     */
    String getTradeNo(String userId);

    /**
     * 比较流水号
     * @param userId 获取缓存中的流水号
     * @param tradeCodeNo   页面传递过来的流水号
     * @return
     */
    boolean checkTradeCode(String userId, String tradeCodeNo);


    /**
     * 删除流水号
     * @param userId
     */
    void deleteTradeNo(String userId);

    boolean checkStock(Long skuId, Integer skuNum);

    void execExpiredOrder(Long orderId,String flag);

    OrderInfo getOrderInfo(Long orderId);

    public void updateOrderStatus(Long orderId, ProcessStatus processStatus);

    void sendOrderStatus(Long orderId);

    Map initWareOrder(OrderInfo orderInfo);

    List<OrderInfo> orderSplit(Long orderId, String wareSkuMap);

}
