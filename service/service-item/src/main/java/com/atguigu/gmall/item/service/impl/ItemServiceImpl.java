package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/4 0:59
 * @Version 1.8
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private ListFeignClient listFeignClient;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
        Map<String, Object> result = new HashMap<>();

        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            result.put("skuInfo",skuInfo);
            return skuInfo;
        },threadPoolExecutor);

        CompletableFuture<Void> spuSaleAttrListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuInfo.getId(), skuInfo.getSpuId());
            result.put("spuSaleAttrList", spuSaleAttrList);
        },threadPoolExecutor);

        CompletableFuture<Void> valuesSkuJsonCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            String valuesSkuJson = JSON.toJSONString(skuValueIdsMap);
            result.put("valuesSkuJson", valuesSkuJson);
        },threadPoolExecutor);

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(()->{
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            result.put("price",skuPrice);
        },threadPoolExecutor);

        CompletableFuture<Void> categoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            result.put("categoryView", categoryView);
        },threadPoolExecutor);

        CompletableFuture<Void> incrHotScoreCompletableFuture = CompletableFuture.runAsync(() -> {
            Result result1 = listFeignClient.incrHotScore(skuId);
        }, threadPoolExecutor);


        CompletableFuture.allOf(
                skuInfoCompletableFuture,
                spuSaleAttrListCompletableFuture,
                valuesSkuJsonCompletableFuture,
                voidCompletableFuture,
                categoryViewCompletableFuture,
                incrHotScoreCompletableFuture
        ).join();
        return result;
    }



}
