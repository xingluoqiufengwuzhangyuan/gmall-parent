package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
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
 * @Date: 2021/2/2 20:26
 * @Version 1.8
 */
@RestController
@Api("spu管理控制器")
@RequestMapping("admin/product")
public class SpuManageController {

    @Resource
    private ManageService manageService;

    @GetMapping("baseSaleAttrList")
    public Result getBaseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrList = manageService.getBaseSaleAttrList();

        return Result.ok(baseSaleAttrList);
    }

    @PostMapping("saveSpuInfo")
    @ApiOperation(value = "添加spu接口")
    public Result saveSpuInfo(
            @ApiParam(value = "新增的spu信息",required = true)
            @RequestBody SpuInfo spuInfo
            ) {
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    @GetMapping("spuSaleAttrList/{spuId}")
    @ApiOperation(value = "根据spuId获取销售属性列表与销售属性值")
    public Result getSpuSaleAttrList(
            @ApiParam(value = "spuId",required = true)
            @PathVariable("spuId")long spuId
    ) {
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }

    //    http://localhost/admin/product/findSpuInfoByKeyword/%E7%9A%84
    @GetMapping("/findSpuInfoByKeyword/{keyword}")
    public Result findSpuInfoByKeyword(@PathVariable String keyword) {
        return Result.ok(manageService.findSpuInfoByKeyword(keyword));

    }
}
