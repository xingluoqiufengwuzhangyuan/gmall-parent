package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.service.ActivityInfoService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.ActivityInfo;
import com.atguigu.gmall.model.activity.ActivityRuleVo;
import com.atguigu.gmall.model.enums.ActivityType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/10 22:32
 * @Version 1.8
 */
@RestController
@RequestMapping("/admin/activity/activityInfo")
public class ActivityInfoController {

    @Resource
    private ActivityInfoService activityInfoService;

    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @PathVariable Long page, @PathVariable Long limit) {
        Page<ActivityInfo> pageParam = new Page<>(page, limit);
        IPage<ActivityInfo> pageModel = activityInfoService.selectPage(pageParam);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "获取活动")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ActivityInfo activityInfo = activityInfoService.getById(id);
        activityInfo.setActivityTypeString(ActivityType.getNameByType(activityInfo.getActivityType()));
        return Result.ok(activityInfo);
    }

    @ApiOperation(value = "新增活动")
    @PostMapping("save")
    public Result save(@RequestBody ActivityInfo activityInfo) {
        activityInfo.setCreateTime(new Date());
        activityInfoService.save(activityInfo);
        return Result.ok();
    }

    @ApiOperation(value = "修改活动")
    @PutMapping("update")
    public Result updateById(@RequestBody ActivityInfo activityInfo) {
        activityInfoService.updateById(activityInfo);
        return Result.ok();
    }

    @ApiOperation(value = "删除活动")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        activityInfoService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "根据id列表删除活动")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        activityInfoService.removeByIds(idList);
        return Result.ok();
    }

    @ApiOperation(value = "保存活动规则")
    @PostMapping("saveActivityRule")
    public Result saveActivityRule(@RequestBody ActivityRuleVo activityRuleVo) {
        activityInfoService.saveActivityRule(activityRuleVo);
        return Result.ok();
    }

    @GetMapping("findSkuInfoByKeyword/{keyword}")
    public Result findSkuInfoByKeyword(@PathVariable("keyword") String keyword) {
        return Result.ok(activityInfoService.findSkuInfoByKeyword(keyword));
    }

    @GetMapping("findActivityRuleList/{activityId}")
    public Result findActivityRuleList(@PathVariable("activityId")Long activityId) {
        Map<String, Object> activityRuleMap = activityInfoService.findActivityRuleList(activityId);
        return Result.ok(activityRuleMap);
    }


}
