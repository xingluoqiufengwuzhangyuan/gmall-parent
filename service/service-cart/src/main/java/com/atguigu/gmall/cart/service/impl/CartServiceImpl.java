package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartAsyncService;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/27 20:45
 * @Version 1.8
 */
@Service
public class CartServiceImpl implements CartService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private CartInfoMapper cartInfoMapper;

    @Resource
    private CartAsyncService cartAsyncService;

    @Override
    public void addToCart(Long skuId, String userId, Integer skuNum) {
//        添加到缓存
//        判断缓存中是否有购物车
        String cartKey = getCartKey(userId);
        Boolean hasKey = redisTemplate.hasKey(cartKey);
//        1.不存在,查询数据库加入缓存
        if (!hasKey) {
            loadCartCache(userId);
        }
//        2.存在,直接查询缓存
        CartInfo cartInfoExist = (CartInfo) redisTemplate.boundHashOps(cartKey).get(skuId.toString());
//判断购物车中是否存在该商品
//1.不存在,添加到缓存,//
        if (cartInfoExist == null) {
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            CartInfo cartInfo = new CartInfo();
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuPrice(productFeignClient.getSkuPrice(skuId));
            cartInfo.setUserId(userId);
            cartInfo.setSkuId(skuId);
            cartInfo.setSkuNum(skuNum);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setCreateTime(new Timestamp(new Date().getTime()));
            cartInfo.setUpdateTime(new Timestamp(new Date().getTime()));
//            异步同步到mysql
            cartInfoExist = cartInfo;
            cartAsyncService.saveCartInfo(cartInfoExist);
        } else {
//        2.存在,修改skuNum,//
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum() + skuNum);
            cartInfoExist.setUpdateTime(new Timestamp(new Date().getTime()));
            cartInfoExist.setSkuPrice(productFeignClient.getSkuPrice(skuId));
//            异步同步到mysql
            cartAsyncService.updateCartInfo(cartInfoExist);
        }
        redisTemplate.boundHashOps(cartKey).put(skuId.toString(), cartInfoExist);

        setCartExpire(cartKey);

    }

    @Override
    public List<CartInfo> getCartList(String userId, String userTempId) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        if (StringUtils.isEmpty(userId)) {
            cartInfoList = getCartList(userTempId);
        }
        if (!StringUtils.isEmpty(userId)) {
            if (StringUtils.isEmpty(userTempId)) {
                cartInfoList = getCartList(userId);
            }
            List<CartInfo> cartTempList = getCartList(userTempId);
            if (!CollectionUtils.isEmpty(cartTempList)) {
//                合并购物车并返回(查询数据库logincartlist,遍历判断carttemplist是否包含
//                ,分别修改数据库或加入数据库,查询加入缓存)
                mergetToCartList(cartTempList, userId);
//                然后删除临时缓存与数据库
                deleteCartList(userTempId);
            }
            if (CollectionUtils.isEmpty(cartTempList)) {
                cartInfoList = getCartList(userId);

            }

        }

        return cartInfoList;
    }

    private void deleteCartList(String userTempId) {
        String cartKey = getCartKey(userTempId);
//        删除缓存
        if (redisTemplate.hasKey(cartKey)) {
            redisTemplate.delete(cartKey);
        }
//        异步删除数据库
        cartAsyncService.deleteCartInfo(userTempId);
    }

    private List<CartInfo> mergetToCartList(List<CartInfo> cartTempList, String userId) {
        //                查询数据库logincartlist,
        List<CartInfo> cartLoginList = getCartList(userId);
        //                遍历判断carttemplist是否包含
        Map<Long, CartInfo> cartLoginMap = cartLoginList.stream().collect(Collectors.toMap(CartInfo::getSkuId, cartInfo -> cartInfo));
        for (CartInfo cartTemp : cartTempList) {
            Long skuId = cartTemp.getSkuId();
            if (cartLoginMap.containsKey(skuId)) {
//                修改数据
                CartInfo cartInfo = cartLoginMap.get(skuId);
                cartInfo.setSkuNum(cartInfo.getSkuNum() + cartTemp.getSkuNum());
                cartInfo.setUpdateTime(new Timestamp(new Date().getTime()));
                if (cartTemp.getIsChecked().intValue() == 1) {
                    if (cartInfo.getIsChecked().intValue() == 0) {
                        cartInfo.setIsChecked(1);
                    }
                }
                cartInfoMapper.update(cartInfo, new QueryWrapper<CartInfo>().eq("user_id", userId).eq("sku_id", skuId));
            } else {
//                添加数据
                cartTemp.setUserId(userId);
                cartTemp.setCreateTime(new Timestamp(new Date().getTime()));
                cartTemp.setUpdateTime(new Timestamp(new Date().getTime()));
                cartInfoMapper.insert(cartTemp);
            }

        }
//                ,分别修改数据库或加入数据库,查询加入缓存)
        List<CartInfo> cartInfoList = loadCartCache(userId);

        return cartInfoList;
    }

    public List<CartInfo> getCartList(String userIdOrtemp) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        if (StringUtils.isEmpty(userIdOrtemp)) {
            return cartInfoList;
        }
        String cartKey = getCartKey(userIdOrtemp);
        //        如果缓存中有,则取缓存
        cartInfoList = redisTemplate.boundHashOps(cartKey).values();
        //  cartInfoList = redisTemplate.opsForHash().values(cartKey);
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            //  有排序规则？
            cartInfoList.sort(new Comparator<CartInfo>() {
                //  自定义比较器
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    //  按照更新时间进行比较！
                    return DateUtil.truncatedCompareTo(o2.getUpdateTime(), o1.getUpdateTime(), Calendar.SECOND);
                }
            });
            return cartInfoList;
        } else {
            //  从数据库获取数据并放入缓存！
            cartInfoList = this.loadCartCache(userIdOrtemp);
            return cartInfoList;
        }
    }

    @Override
    public void checkCart(String userId, Long skuId, Integer isChecked) {
        cartAsyncService.checkCart(userId, skuId, isChecked);
        String cartKey = getCartKey(userId);
        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(cartKey);
        Boolean hasKey = boundHashOperations.hasKey(skuId.toString());
        if (hasKey) {
            CartInfo cartInfoUpd = (CartInfo) boundHashOperations.get(skuId.toString());
            cartInfoUpd.setIsChecked(isChecked);
            redisTemplate.opsForHash().put(cartKey, skuId.toString(), cartInfoUpd);
            setCartExpire(cartKey);
        }
    }

    @Override
    public void deleteCart(String userId, Long skuId) {
        cartAsyncService.deleteCart(userId, skuId);
        String cartKey = getCartKey(userId);
        redisTemplate.opsForHash().delete(cartKey, skuId.toString());
    }

    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();

        // 定义key user:userId:cart
        String cartKey = getCartKey(userId);
        List<CartInfo> cartCachInfoList = redisTemplate.opsForHash().values(cartKey);
        if (null != cartCachInfoList && cartCachInfoList.size() > 0) {
            for (CartInfo cartInfo : cartCachInfoList) {
                // 获取选中的商品！
                if (cartInfo.getIsChecked().intValue() == 1) {
                    cartInfoList.add(cartInfo);
                }
            }
        }
        return cartInfoList;

    }

    @Override
    public List<CartInfo> loadCartCache(String userId) {
        QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
        cartInfoQueryWrapper.eq("user_id", userId);
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(cartInfoQueryWrapper);

        if (CollectionUtils.isEmpty(cartInfoList)) {
            return cartInfoList;
        }
        String cartKey = getCartKey(userId);
        HashMap<String, Object> hashMap = new HashMap<>();
        //  循环遍历集合
        for (CartInfo cartInfo : cartInfoList) {
            //  细节问题：   给实时价格赋值
            cartInfo.setSkuPrice(productFeignClient.getSkuPrice(cartInfo.getSkuId()));
            //  修改添加时价格
            //  cartInfo.setCartPrice(productFeignClient.getSkuPrice(cartInfo.getSkuId()));

            hashMap.put(cartInfo.getSkuId().toString(), cartInfo);
            //  redisTemplate.opsForHash().put(cartKey,cartInfo.getSkuId().toString(),cartInfo);
        }

        //  将数据存储到缓存!
        redisTemplate.opsForHash().putAll(cartKey, hashMap);
        //  设置一个过期时间
        this.setCartExpire(cartKey);
        //  排序。。。。。

        //  返回数据
        return cartInfoList;
    }

    private void setCartExpire(String cartKey) {
        redisTemplate.expire(cartKey, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }

    private String getCartKey(String userId) {
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }
}
