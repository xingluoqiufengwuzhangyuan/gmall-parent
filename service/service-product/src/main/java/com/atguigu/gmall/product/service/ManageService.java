package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/1/30 20:54
 * @Version 1.8
 */
public interface ManageService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(Long category1Id);

    List<BaseCategory3> getCategory3(Long category2Id);

    List<BaseAttrInfo> getBaseAttrInfo(Long category1Id, Long category2Id, Long category3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValue(Long attrId);

    IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> spuInfoPage, SpuInfo spuInfo);

    List<BaseSaleAttr> getBaseSaleAttrList();

    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuImage> getSpuImageList(Long spuId);

    List<SpuSaleAttr> getSpuSaleAttrList(long spuId);

    void saveSkuInfo(SkuInfo skuInfo);

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    SkuInfo getSkuInfo(long skuId);

    BaseCategoryView getCategoryViewByCategory3Id(Long category3Id);

    BigDecimal getSkuPrice(Long skuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    Map getSkuValueIdsMap(Long spuId);

    List<JSONObject> getBaseCategoryList();

    BaseTrademark getTrademarkByTmId(Long tmId);

    List<BaseAttrInfo> getAttrList(Long skuId);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    List<SkuInfo> findSkuInfoBySkuIdList(List<Long> skuIdList);

    List<SpuInfo> findSpuInfoByIdList(List<Long> idList);

    List<BaseCategory3> findBaseCategory3ByIdList(List<Long> idList);

    List<SpuInfo> findSpuInfoByKeyword(String keyword);
}
