package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.common.util.HttpClientUtil;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/1 22:26
 * @Version 1.8
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderService {
    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RabbitService rabbitService;

    @Resource
    private OrderService orderService;

    @Value("${ware.url}")
    private String WARE_URL;

    @Override
    @Transactional
    public Long saveOrderInfo(OrderInfo orderInfo) {
        orderInfo.sumTotalAmount();
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        String outTradeNo = "ATGUIGU" + System.currentTimeMillis() + "" + new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setCreateTime(new Date());
        // 定义为1天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        orderInfo.setExpireTime(calendar.getTime());

        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        // 获取订单明细
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        StringBuffer tradeBody = new StringBuffer();
        for (OrderDetail orderDetail : orderDetailList) {
            tradeBody.append(orderDetail.getSkuName()+" ");
        }
        if (tradeBody.toString().length()>100){
            orderInfo.setTradeBody(tradeBody.toString().substring(0,100));
        }else {
            orderInfo.setTradeBody(tradeBody.toString());
        }

        orderInfoMapper.insert(orderInfo);

        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insert(orderDetail);
        }

        rabbitService.sendDelayMessage(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL, MqConst.ROUTING_ORDER_CANCEL, orderInfo.getId(),MqConst.DELAY_TIME);
        return orderInfo.getId();
    }

    @Override
    public String getTradeNo(String userId) {
        String tradeNoKey = "user:" + userId + ":tradeCode";
        String tradeNo = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(tradeNoKey, tradeNo);
        return tradeNo;

    }

    @Override
    public boolean checkTradeCode(String userId, String tradeCodeNo) {
        String tradeNoKey = "user:" + userId + ":tradeCode";
        String redisTradeNo = (String) redisTemplate.opsForValue().get(tradeNoKey);
        return tradeCodeNo.equals(redisTradeNo);
    }

    @Override
    public void deleteTradeNo(String userId) {
// 定义key
        String tradeNoKey = "user:" + userId + ":tradeCode";
        // 删除数据
        redisTemplate.delete(tradeNoKey);

    }

    @Override
    public boolean checkStock(Long skuId, Integer skuNum) {
        String result = HttpClientUtil.doGet(WARE_URL + "/hasStock?skuId=" + skuId + "&num=" + skuNum);
        return "1".equals(result);

    }

    @Override
    public void execExpiredOrder(Long orderId,String flag) {
        // 调用方法 状态
        updateOrderStatus(orderId,ProcessStatus.CLOSED);
        if ("2".equals(flag)){
            // 发送消息队列，关闭支付宝的交易记录。
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_CLOSE,MqConst.ROUTING_PAYMENT_CLOSE,orderId);
        }
    }


    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        QueryWrapper<OrderDetail> orderDetailQueryWrapper = new QueryWrapper<>();
        orderDetailQueryWrapper.eq("order_id", orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(orderDetailQueryWrapper);
        orderInfo.setOrderDetailList(orderDetails);
        return orderInfo;
    }

    @Override
    public void updateOrderStatus(Long orderId, ProcessStatus processStatus) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setOrderStatus(processStatus.getOrderStatus().name());
        orderInfo.setProcessStatus(processStatus.name());
        orderInfoMapper.updateById(orderInfo);
    }

    @Override
    public void sendOrderStatus(Long orderId) {
        //  发送消息之前：更新订单状态
        updateOrderStatus(orderId,ProcessStatus.NOTIFIED_WARE);

        //  获取发送消息内容：
        String wareJson = initWareOrder(orderId);
        //  发送消息
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_WARE_STOCK,MqConst.ROUTING_WARE_STOCK,wareJson);

    }
    //  通过orderId 将发送的数据封装成功Json - String
    private String initWareOrder(Long orderId) {
        //  Json 字符串组成 是由orderInfo 中的部分组成！
        //  先获取到OrderInfo
        OrderInfo orderInfo = getOrderInfo(orderId);
        //  将这个 orderInfo 中组成Json 的字符串的字段，封装到一个Map 中。
        Map map = initWareOrder(orderInfo);
        //  将map 转换为Json 字符串
        return JSON.toJSONString(map);
    }

    //  将orderInfo 部分字段转换为Map
    @Override
    public Map initWareOrder(OrderInfo orderInfo) {
        //  先声明一个map 集合
        Map<String,Object> map = new HashMap();
        map.put("orderId",orderInfo.getId());
        map.put("consignee",orderInfo.getConsignee());
        map.put("consigneeTel", orderInfo.getConsigneeTel());
        map.put("orderComment", orderInfo.getOrderComment());
        map.put("orderBody", orderInfo.getTradeBody());
        map.put("deliveryAddress", orderInfo.getDeliveryAddress());
        map.put("paymentWay", "2");
        map.put("wareId", orderInfo.getWareId());// 仓库Id ，减库存拆单时需要使用！

        //  声明一个集合来存储orderDetailMap
        List<HashMap> orderDetailArrayList = new ArrayList<>();
        //  所有的数据都是来自于订单明细：OrderDetail;
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            HashMap<String, Object> orderDetailMap = new HashMap<>();
            orderDetailMap.put("skuId",orderDetail.getSkuId());
            orderDetailMap.put("skuNum",orderDetail.getSkuNum());
            orderDetailMap.put("skuName",orderDetail.getSkuName());
            //  将orderDetailMap 存储起来
            orderDetailArrayList.add(orderDetailMap);
        }

        //  [{skuId:101,skuNum:1,skuName:’小米手64G’},{skuId:201,skuNum:1,skuName:’索尼耳机’}]
        map.put("details",orderDetailArrayList);
        //  返回map集合
        return map;
    }

    @Override
    @Transactional
    public List<OrderInfo> orderSplit(Long orderId, String wareSkuMap) {
        ArrayList<OrderInfo> orderInfoArrayList = new ArrayList<>();
    /*
    1.  先获取到原始订单 107
    2.  将wareSkuMap 转换为我们能操作的对象 [{"wareId":"1","skuIds":["2","10"]},{"wareId":"2","skuIds":["3"]}]
        方案一：class Param{
                    private String wareId;
                    private List<String> skuIds;
                }
        方案二：看做一个Map mpa.put("wareId",value); map.put("skuIds",value)

    3.  创建一个新的子订单 108 109 。。。
    4.  给子订单赋值
    5.  保存子订单到数据库
    6.  修改原始订单的状态
    7.  测试
     */
        OrderInfo orderInfoOrigin = getOrderInfo(orderId);
        List<Map> maps = JSON.parseArray(wareSkuMap, Map.class);
        if (maps != null) {
            for (Map map : maps) {
                String wareId = (String) map.get("wareId");

                List<String> skuIds = (List<String>) map.get("skuIds");

                OrderInfo subOrderInfo = new OrderInfo();
                // 属性拷贝
                BeanUtils.copyProperties(orderInfoOrigin, subOrderInfo);
                // 防止主键冲突
                subOrderInfo.setId(null);
                subOrderInfo.setParentOrderId(orderId);
                // 赋值仓库Id
                subOrderInfo.setWareId(wareId);

                // 计算子订单的金额: 必须有订单明细
                // 获取到子订单明细
                // 声明一个集合来存储子订单明细
                ArrayList<OrderDetail> orderDetails = new ArrayList<>();

                List<OrderDetail> orderDetailList = orderInfoOrigin.getOrderDetailList();
                // 表示主主订单明细中获取到子订单的明细
                if (orderDetailList != null && orderDetailList.size() > 0) {
                    for (OrderDetail orderDetail : orderDetailList) {
                        // 获取子订单明细的商品Id
                        for (String skuId : skuIds) {
                            if (Long.parseLong(skuId) == orderDetail.getSkuId().longValue()) {
                                // 将订单明细添加到集合
                                orderDetails.add(orderDetail);
                            }
                        }
                    }
                }
                subOrderInfo.setOrderDetailList(orderDetails);
                // 计算总金额
                subOrderInfo.sumTotalAmount();
                // 保存子订单
                saveOrderInfo(subOrderInfo);
                // 将子订单添加到集合中！
                orderInfoArrayList.add(subOrderInfo);
            }
        }
        // 修改原始订单的状态
        updateOrderStatus(orderId, ProcessStatus.SPLIT);
        return orderInfoArrayList;
    }

}
