package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/1/30 21:03
 * @Version 1.8
 */
@Service
public class ManageServiceImpl implements ManageService {
    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;

    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;

    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;

    @Resource
    private SpuInfoMapper spuInfoMapper;

    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;

    @Resource
    private SpuImageMapper spuImageMapper;

    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Resource
    private SkuInfoMapper skuInfoMapper;

    @Resource
    private SkuImageMapper skuImageMapper;

    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;

    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Resource
    private RabbitService rabbitService;

    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        return baseCategory2Mapper.selectList(new QueryWrapper<BaseCategory2>().eq("category1_id", category1Id));
    }

    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        return baseCategory3Mapper.selectList(new QueryWrapper<BaseCategory3>().eq("category2_id", category2Id));

    }

    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(Long category1Id, Long category2Id, Long category3Id) {
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.selectBaseAttrInfoList(category1Id, category2Id, category3Id);

        return baseAttrInfoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        Long bid = baseAttrInfo.getId();

        if (bid == null) {
            baseAttrInfoMapper.insert(baseAttrInfo);
        } else {
            baseAttrInfoMapper.updateById(baseAttrInfo);
        }
        baseAttrValueMapper.delete(new QueryWrapper<BaseAttrValue>().eq("attr_id", bid));
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if (!CollectionUtils.isEmpty(attrValueList)) {
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BaseAttrValue> getAttrValue(Long attrId) {
        List<BaseAttrValue> attrValueList = baseAttrValueMapper.selectList(new QueryWrapper<BaseAttrValue>().eq("attr_id", attrId));
        return attrValueList;
    }

    @Override
    public IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> pageParam, SpuInfo spuInfo) {

        QueryWrapper<SpuInfo> spuInfoQueryWrapper = new QueryWrapper<SpuInfo>().eq("category3_id", spuInfo.getCategory3Id()).orderByDesc("id");
        IPage<SpuInfo> spuInfoIPage = spuInfoMapper.selectPage(pageParam, spuInfoQueryWrapper);
        return spuInfoIPage;
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrList = baseSaleAttrMapper.selectList(null);
        return baseSaleAttrList;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfo(SpuInfo spuInfo) {
        spuInfoMapper.insert(spuInfo);
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (!CollectionUtils.isEmpty(spuImageList)) {
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            }
        }

        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (!CollectionUtils.isEmpty(spuSaleAttrList)) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);

                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (!CollectionUtils.isEmpty(spuSaleAttrValueList)) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }
    }

    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        List<SpuImage> spuImageList = spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id", spuId));
        return spuImageList;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(long spuId) {
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
        return spuSaleAttrList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuInfo(SkuInfo skuInfo) {
        /*
            skuInfo,skuAtrrValue,skuSaleAttrValue,skuImage
         */

        //  skuInfo
        skuInfoMapper.insert(skuInfo);
        //  skuImage
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (!CollectionUtils.isEmpty(skuImageList)) {
            // 循环遍历
            for (SkuImage skuImage : skuImageList) {
                //  数据补全
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            }
        }
        //  skuAtrrValue
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            //  循环遍历
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                //  数据补全
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
        //  skuSaleAttrValue
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (!CollectionUtils.isEmpty(skuSaleAttrValueList)) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {

                //  数据补全
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
        //  保存完成之后，进行商品上架
    }

    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);

        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS, MqConst.ROUTING_GOODS_UPPER, skuId);
    }

    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);

        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS, MqConst.ROUTING_GOODS_LOWER, skuId);
    }

    @GmallCache(prefix = "sku:")
    @Override
    public SkuInfo getSkuInfo(long skuId) {

        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo != null) {
            return skuInfo;
        }
        List<SkuImage> skuImageList = skuImageMapper.selectList(new QueryWrapper<SkuImage>().eq("sku_id", skuId));
        skuInfo.setSkuImageList(skuImageList);
        return skuInfo;
    }

    @GmallCache(prefix = "categoryView:")
    @Override
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {
        BaseCategoryView categoryView = baseCategoryViewMapper.selectById(category3Id);

        return categoryView;
    }

    @GmallCache(prefix = "skuPrice:")
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo != null) {
            return skuInfo.getPrice();
        }
        return new BigDecimal(0);
    }

    @GmallCache(prefix = "spuSaleAttrListCheckBySku:")
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    @GmallCache(prefix = "skuValueIdsMap:")
    @Override
    public Map getSkuValueIdsMap(Long spuId) {
        Map<Object, Object> map = new HashMap<>();
        // key = 125|123 ,value = 37
        List<Map> mapList = skuSaleAttrValueMapper.selectSaleAttrValuesBySpu(spuId);
        if (mapList != null && mapList.size() > 0) {
            // 循环遍历
            for (Map skuMap : mapList) {
                // key = 125|123 ,value = 37
                map.put(skuMap.get("values_id"), skuMap.get("sku_id"));
            }
        }
        return map;
    }
@GmallCache(prefix = "index:")
    @Override
    public List<JSONObject> getBaseCategoryList() {
        List<JSONObject> list = new ArrayList<>();

        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);

        Map<Long, List<BaseCategoryView>> cateGory1Map = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        int index=1;

        //        获取一级分类数据
        for (Map.Entry<Long, List<BaseCategoryView>> entry : cateGory1Map.entrySet()) {
            Long category1Id = entry.getKey();
            List<BaseCategoryView> category2List = entry.getValue();
            JSONObject category1 = new JSONObject();
            category1.put("index", index);
            category1.put("categoryId", category1Id);
            category1.put("categoryName", category2List.get(0).getCategory1Name());
            index++;
            Map<Long, List<BaseCategoryView>> cateGory2Map = category2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            ArrayList<JSONObject> cateGory2Child = new ArrayList<>();
            category1.put("categoryChild", cateGory2Child);

            for (Map.Entry<Long, List<BaseCategoryView>> category2Entry : cateGory2Map.entrySet()) {
                Long category2Id = category2Entry.getKey();
                List<BaseCategoryView> category3List = category2Entry.getValue();
                JSONObject category2 = new JSONObject();
                category2.put("categoryId", category2Id);
                category2.put("categoryName", category3List.get(0).getCategory2Name());
                cateGory2Child.add(category2);
                ArrayList<JSONObject> cateGory3Child = new ArrayList<>();
                category2.put("categoryChild", cateGory3Child);
                category3List.forEach(baseCategoryView->{
                    JSONObject category3 = new JSONObject();
                    category3.put("categoryId", baseCategoryView.getCategory3Id());
                    category3.put("categoryName", baseCategoryView.getCategory3Name());
                    cateGory3Child.add(category3);
                });
            }
            list.add(category1);
        }

        return list;
    }

    @Override
    public BaseTrademark getTrademarkByTmId(Long tmId) {
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(tmId);
        return baseTrademark;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(Long skuId) {
        return baseAttrInfoMapper.selectBaseAttrInfoListBySkuId(skuId);
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.like("sku_name", keyword);
        List<SkuInfo> skuInfoList = skuInfoMapper.selectList(skuInfoQueryWrapper);
        return skuInfoList;
    }

    @Override
    public List<SkuInfo> findSkuInfoBySkuIdList(List<Long> skuIdList) {
        List<SkuInfo> skuInfoList = skuInfoMapper.selectBatchIds(skuIdList);
        return skuInfoList;
    }

    @Override
    public List<SpuInfo> findSpuInfoByIdList(List<Long> idList) {
        List<SpuInfo> spuInfoList = spuInfoMapper.selectBatchIds(idList);
        return spuInfoList;
    }

    @Override
    public List<BaseCategory3> findBaseCategory3ByIdList(List<Long> idList) {
        List<BaseCategory3> baseCategory3List = baseCategory3Mapper.selectBatchIds(idList);
        return baseCategory3List;
    }

    @Override
    public List<SpuInfo> findSpuInfoByKeyword(String keyword) {
        List<SpuInfo> spuInfoList = spuInfoMapper.selectList(new QueryWrapper<SpuInfo>().like("spu_name", keyword));

        return spuInfoList;
    }


}
