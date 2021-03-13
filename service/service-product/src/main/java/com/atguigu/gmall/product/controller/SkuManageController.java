package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/2 21:49
 * @Version 1.8
 */
@RestController
@Api("sku管理接口")
@RequestMapping("admin/product")
public class SkuManageController {

    @Resource
    private ManageService manageService;

    @GetMapping("spuImageList/{spuId}")
    @ApiOperation(value = "根据spuid获取图片列表")
    public Result getSpuImageList(
            @ApiParam(value = "spuId", required = true)
            @PathVariable("spuId") Long spuId
    ) {
        List<SpuImage> spuImageList = manageService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }

    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){

        manageService.saveSkuInfo(skuInfo);

        return Result.ok();
    }

    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        //  调用商品上架
        manageService.onSale(skuId);

        return Result.ok();
    }


    //  http://api.gmall.com/admin/product/cancelSale/{skuId}
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){
        //  调用商品上架
        manageService.cancelSale(skuId);

        return Result.ok();
    }


}
