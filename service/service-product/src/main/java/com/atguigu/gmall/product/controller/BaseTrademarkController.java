package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/2 20:40
 * @Version 1.8
 */
@RestController
@Api("商品品牌管理控制器")
@RequestMapping("admin/product/baseTrademark")
public class BaseTrademarkController {

    @Resource
    private BaseTrademarkService baseTrademarkService;


    @GetMapping("getTrademarkList")
    @ApiOperation(value = "获取品牌属性")
    public Result getTrademarkList() {
        List<BaseTrademark> trademarkList = baseTrademarkService.list(null);
        return Result.ok(trademarkList);
    }

    @PostMapping("save")
    @ApiOperation(value = "新增品牌属性")
    public Result saveTrademark(
            @ApiParam(value = "新增品牌", required = true)
            @RequestBody BaseTrademark baseTrademark
    ) {
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    @PutMapping("update")
    public Result updateTrademark(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    @DeleteMapping("remove/{id}")
    public Result deleteBaseTrademark(@PathVariable Long id) {
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    @GetMapping("get/{id}")
    public Result getBaseTrademark(@PathVariable Long id) {
        return Result.ok(baseTrademarkService.getById(id));
    }

    @GetMapping("{page}/{limit}")
    public Result getbaseTrademarkList(@PathVariable Long page,
                                       @PathVariable Long limit) {
        // new Page
        Page<BaseTrademark> baseTrademarkPage = new Page<>(page, limit);
        IPage<BaseTrademark> pageList = baseTrademarkService.getPage(baseTrademarkPage);
        //  返回数据！
        return Result.ok(pageList);

    }

    @GetMapping("findBaseTrademarkByKeyword/{keyword}")
    public Result findBaseTrademarkByKeyword(@PathVariable String keyword) {
        return Result.ok(baseTrademarkService.findBaseTrademarkByKeyword(keyword));
    }
}
