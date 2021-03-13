package com.atguigu.gmall.product.client.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/4 22:52
 * @Version 1.8
 */
@Component
public class ProductDegradeFeignClient implements ProductFeignClient {

    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return null;
    }

    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        return null;
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        return null;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return null;
    }


    @Override
    public Map getSkuValueIdsMap(Long spuId) {
        return null;
    }

    @Override
    public Result getBaseCategoryList() {
        return null;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(Long skuId) {
        return null;
    }

    @Override
    public BaseTrademark getTrademark(Long tmId) {
        return null;
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        return null;
    }

    @Override
    public List<SkuInfo> findSkuInfoBySkuIdList(List<Long> skuIdList) {
        return null;
    }

    @Override
    public List<SpuInfo> findSpuInfoByIdList(List<Long> idList) {
        return null;
    }

    @Override
    public List<BaseCategory3> findBaseCategory3ByIdList(List<Long> idList) {
        return null;
    }

    @Override
    public List<BaseTrademark> findBaseTrademarkByIdList(List<Long> idList) {
        return null;
    }
}

