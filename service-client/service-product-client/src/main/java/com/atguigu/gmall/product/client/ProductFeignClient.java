package com.atguigu.gmall.product.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.impl.ProductDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/4 22:51
 * @Version 1.8
 */
@FeignClient(value ="service-product", fallback = ProductDegradeFeignClient.class)
public interface ProductFeignClient {

    /**
     * 根据skuId获取sku信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);


    /**
     * 通过三级分类id查询分类信息
     *
     * @param category3Id
     * @return
     */
    @GetMapping("/api/product/inner/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id);

    /**
     * 获取sku最新价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable(value = "skuId") Long skuId);

    /**
     * 根据spuId，skuId 查询销售属性集合
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("skuId") Long skuId, @PathVariable("spuId") Long spuId);

    /**
     * 根据spuId 查询map 集合属性
     *
     * @param spuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId);

    @GetMapping("/api/product/getBaseCategoryList")
    public Result getBaseCategoryList();

    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/inner/getTrademark/{tmId}")
    BaseTrademark getTrademark(@PathVariable("tmId")Long tmId);

    @GetMapping("/api/product/inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable("keyword") String keyword);

    @PostMapping("/api/product/inner/findSkuInfoBySkuIdList")
    public List<SkuInfo> findSkuInfoBySkuIdList(@RequestBody List<Long> skuIdList);

    @PostMapping("/api/product/inner/findSpuInfoByIdList")
    public List<SpuInfo> findSpuInfoByIdList(@RequestBody List<Long> idList);

    @PostMapping("/api/product/inner/findBaseCategory3ByIdList")
    public List<BaseCategory3> findBaseCategory3ByIdList(@RequestBody List<Long> idList);

    @PostMapping("/admin/product/baseTrademark/inner/findBaseTrademarkByIdList")
    public List<BaseTrademark> findBaseTrademarkByIdList(@RequestBody List<Long> idList);

}


