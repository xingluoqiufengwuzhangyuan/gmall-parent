package com.atguigu.gmall.product.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/4 20:28
 * @Version 1.8
 */
@RestController
@Api("内部调用接口")
@RequestMapping("api/product")
public class ProductApiController {

    @Resource
    private ManageService manageService;

    @Resource
    private BaseTrademarkService baseTrademarkService;


    @ApiOperation(value = "通过skuId获取skuInfo")
    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getAttrValueList(
            @ApiParam("skuId")
            @PathVariable("skuId") long skuId
    ) {
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        return skuInfo;
    }

    @ApiOperation(value = "通过category3Id获取分类信息")
    @GetMapping("inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(
            @ApiParam("category3Id")
            @PathVariable("category3Id") Long category3Id) {
        return manageService.getCategoryViewByCategory3Id(category3Id);
    }

    @ApiOperation(value = "通过skuId获取实时价格")
    @GetMapping("inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(
            @ApiParam("skuId")
            @PathVariable("skuId") Long skuId) {
        return manageService.getSkuPrice(skuId);
    }

    @ApiOperation(value = "通过skuId与spuId获取销售属性有销售属性值")
    @GetMapping("inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(
            @ApiParam("skuId")
            @PathVariable("skuId") Long skuId,
            @ApiParam("spuId")
            @PathVariable("spuId") Long spuId) {

        return manageService.getSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    // 获取销售属性值与skuId 组成的map！
    @GetMapping("inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable Long spuId) {
        return manageService.getSkuValueIdsMap(spuId);
    }

    @GetMapping("getBaseCategoryList")
    public Result getBaseCategoryList() {
        List<JSONObject> baseCategoryList = manageService.getBaseCategoryList();

        //  返回数据
        return Result.ok(baseCategoryList);
    }

    @GetMapping("inner/getTrademark/{tmId}")
    public BaseTrademark getTrademark(@PathVariable("tmId") Long tmId) {
        return manageService.getTrademarkByTmId(tmId);
    }

    @GetMapping("inner/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable("skuId") Long skuId) {
        return manageService.getAttrList(skuId);
    }

    @GetMapping("inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable("keyword") String keyword) {
        return manageService.findSkuInfoByKeyword(keyword);
    }

    @PostMapping("inner/findSkuInfoBySkuIdList")
    public List<SkuInfo> findSkuInfoBySkuIdList(@RequestBody List<Long> skuIdList) {
        List<SkuInfo> skuInfoList = manageService.findSkuInfoBySkuIdList(skuIdList);
        return skuInfoList;
    }

    @PostMapping("inner/findSpuInfoByIdList")
    public List<SpuInfo> findSpuInfoByIdList(@RequestBody List<Long> idList) {
        List<SpuInfo> spuInfoList = manageService.findSpuInfoByIdList(idList);
        return spuInfoList;
    }

    @PostMapping("inner/findBaseCategory3ByIdList")
    public List<BaseCategory3> findBaseCategory3ByIdList(@RequestBody List<Long> idList) {
        List<BaseCategory3> baseCategory3List = manageService.findBaseCategory3ByIdList(idList);
        return baseCategory3List;
    }

    @PostMapping("inner/findBaseTrademarkByIdList")
    public List<BaseTrademark> findBaseTrademarkByIdList(@RequestBody List<Long> idList) {
        List<BaseTrademark> baseTrademarkList = baseTrademarkService.findBaseTrademarkByIdList(idList);
        return baseTrademarkList;
    }

}
