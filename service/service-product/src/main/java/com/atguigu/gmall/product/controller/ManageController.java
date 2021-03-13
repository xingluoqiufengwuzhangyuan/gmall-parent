package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/1/31 0:18
 * @Version 1.8
 */
@Api("商品基础属性接口")
@RestController
@RequestMapping("admin/product")
public class ManageController {
    @Resource
    private ManageService manageService;

    @GetMapping("getCategory1")
    @ApiOperation("获取一级分类属性")
    public Result<List<BaseCategory1>> getCategory1() {
        List<BaseCategory1> baseCategory1List = manageService.getCategory1();
        return Result.ok(baseCategory1List);
    }

    @GetMapping("getCategory2/{category1Id}")
    @ApiOperation("获取二级分类属性")
    public Result<List<BaseCategory2>> getCategory2(
            @ApiParam(value = "所属一级分类id", required = true)
            @PathVariable("category1Id") Long category1Id) {
        List<BaseCategory2> baseCategory2List = manageService.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }

    @GetMapping("getCategory3/{category2Id}")
    @ApiOperation("获取三级分类属性")
    public Result<List<BaseCategory3>> getCategory3(
            @ApiParam(value = "所属二级分类id", required = true)
            @PathVariable("category2Id") Long category2Id) {
        List<BaseCategory3> baseCategory3List = manageService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }

    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    @ApiOperation("通过分类id获取平台属性")
    public Result<List<BaseAttrInfo>> attrInfoList(
            @ApiParam(value = "一级分类id", required = true)
            @PathVariable("category1Id") Long category1Id,
            @ApiParam(value = "二级分类id", required = true)
            @PathVariable("category2Id") Long category2Id,
            @ApiParam(value = "三级分类id", required = true)
            @PathVariable("category3Id") Long category3Id) {
        List<BaseAttrInfo> attrInfoList = manageService.getBaseAttrInfo(category1Id, category2Id, category3Id);
        return Result.ok(attrInfoList);

    }

    @PostMapping("saveAttrInfo")
    @ApiOperation(value = "添加平台属性")
    public Result saveAttrInfo(
            @ApiParam(value = "添加的属性相关信息", required = true)
            @RequestBody BaseAttrInfo baseAttrInfo
    ) {
        manageService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    @GetMapping("getAttrValueList/{attrId}")
    @ApiOperation(value = "根据平台属性id获取平台属性值")
    public Result<List<BaseAttrValue>> getAttrValueList(
            @ApiParam(value = "平台属性id", required = true)
            @PathVariable(value = "attrId") Long attrId) {
        List<BaseAttrValue> attrValueList = manageService.getAttrValue(attrId);
        return Result.ok(attrValueList);
    }

    @GetMapping("{page}/{limit}")
    @ApiOperation(value = "获取spu列表")
    public Result getSpuList(
            @ApiParam(value = "第几页")
            @PathVariable(value = "page") long page,
            @ApiParam(value = "每页数量")
            @PathVariable(value = "limit") long limit,
            SpuInfo spuInfo
    ) {
        System.err.println("=======================" + spuInfo.getCategory3Id());
        Page<SpuInfo> spuInfoPage = new Page<>(page, limit);
        IPage<SpuInfo> spuInfoIPage = manageService.getSpuInfoPage(spuInfoPage, spuInfo);
        return Result.ok(spuInfoIPage);
    }


}
