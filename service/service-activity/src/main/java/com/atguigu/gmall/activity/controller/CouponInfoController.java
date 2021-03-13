package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.service.CouponInfoService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.CouponInfo;
import com.atguigu.gmall.model.activity.CouponRuleVo;
import com.atguigu.gmall.model.enums.CouponType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: XLQFWZY
 * @Date: 2021/3/12 18:35
 * @Version 1.8
 */
@RestController
@RequestMapping("/admin/activity/couponInfo")
public class CouponInfoController {

    @Resource
    private CouponInfoService couponInfoService;

    //    http://localhost/admin/activity/couponInfo/1/10
    @GetMapping("{page}/{limit}")
    public Result selectPage(@PathVariable Long page,
                             @PathVariable Long limit) {
        Page<CouponInfo> pageParam = new Page<>(page, limit);
        IPage<CouponInfo> couponInfoIPage = couponInfoService.selectPage(pageParam);

        return Result.ok(couponInfoIPage);
    }

    //    http://localhost/admin/activity/couponInfo/save
    @PostMapping("save")
    public Result save(@RequestBody CouponInfo couponInfo) {
        couponInfoService.save(couponInfo);
        return Result.ok();
    }

    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        couponInfoService.removeById(id);
        return Result.ok();
    }

    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        couponInfoService.removeByIds(idList);
        return Result.ok();
    }

    @PutMapping("update")
    public Result update(@RequestBody CouponInfo couponInfo) {
        couponInfoService.updateById(couponInfo);
        return Result.ok();
    }

    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        CouponInfo couponInfo = couponInfoService.getById(id);
        couponInfo.setCouponTypeString(CouponType.getNameByType(couponInfo.getCouponType()));
        return Result.ok(couponInfo);
    }

    //    http://localhost/admin/activity/couponInfo/findCouponRuleList/3
    @GetMapping("findCouponRuleList/{id}")
    public Result findCouponRuleList(@PathVariable Long id) {

        return Result.ok(couponInfoService.findCouponRuleList(id));

    }

    //    http://localhost/admin/activity/couponInfo/saveCouponRule
    @PostMapping("saveCouponRule")
    public Result saveCouponRule(@RequestBody CouponRuleVo couponRuleVo) {

        couponInfoService.saveCouponRule(couponRuleVo);
        return Result.ok();

    }

    @GetMapping("findCouponByKeyword/{keyword}")
    public Result findCouponByKeyword(@PathVariable String keyword) {
        return Result.ok(couponInfoService.findCouponByKeyword(keyword));
    }
}
