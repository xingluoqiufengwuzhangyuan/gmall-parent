package com.atguigu.gmall.activity.service.impl;

import com.atguigu.gmall.activity.mapper.ActivityInfoMapper;
import com.atguigu.gmall.activity.mapper.ActivityRuleMapper;
import com.atguigu.gmall.activity.mapper.ActivitySkuMapper;
import com.atguigu.gmall.activity.mapper.CouponInfoMapper;
import com.atguigu.gmall.activity.service.ActivityInfoService;
import com.atguigu.gmall.model.activity.*;
import com.atguigu.gmall.model.enums.ActivityType;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/10 22:27
 * @Version 1.8
 */
@Slf4j
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {

    @Resource
    private ActivityInfoMapper activityInfoMapper;

    @Resource
    private ActivityRuleMapper activityRuleMapper;

    @Resource
    private ActivitySkuMapper activitySkuMapper;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private CouponInfoMapper couponInfoMapper;

    @Override
    public IPage<ActivityInfo> selectPage(Page<ActivityInfo> pageParam) {
        QueryWrapper<ActivityInfo> activityInfoQueryWrapper = new QueryWrapper<>();
        activityInfoQueryWrapper.orderByDesc("id");
        IPage<ActivityInfo> page = activityInfoMapper.selectPage(pageParam, activityInfoQueryWrapper);
        page.getRecords().stream().forEach(item -> {
            item.setActivityTypeString(ActivityType.getNameByType(item.getActivityType()));
        });

        return page;
    }

    @Override
    public void saveActivityRule(ActivityRuleVo activityRuleVo) {
        activityRuleMapper.delete(new QueryWrapper<ActivityRule>().eq("activity_id", activityRuleVo.getActivityId()));
        activitySkuMapper.delete(new QueryWrapper<ActivitySku>().eq("activity_id", activityRuleVo.getActivityId()));
        CouponInfo couponInfo = new CouponInfo();
        couponInfo.setActivityId(0L);
        couponInfoMapper.update(couponInfo, new QueryWrapper<CouponInfo>().eq("activity_id", activityRuleVo.getActivityId()));
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        activityRuleList.stream().forEach((activityRule -> {
            activityRule.setActivityId(activityRuleVo.getActivityId());
            activityRuleMapper.insert(activityRule);
        }));

        List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList();
        activitySkuList.stream().forEach(activitySku -> {
            activitySku.setActivityId(activityRuleVo.getActivityId());
            activitySkuMapper.insert(activitySku);
        });

        List<Long> couponIdList = activityRuleVo.getCouponIdList();
        couponIdList.stream().forEach(couponId -> {
            CouponInfo couponInfo1 = new CouponInfo();
            couponInfo1.setId(couponId);
            couponInfo1.setActivityId(activityRuleVo.getActivityId());
            couponInfoMapper.updateById(couponInfo1);
        });
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoByKeyword(keyword);
        List<Long> skuIdList = skuInfoList.stream().map(SkuInfo::getId).collect(Collectors.toList());
        List<Long> existSkuIdList = activityInfoMapper.selectExistSkuIdList(skuIdList);
        skuIdList.removeAll(existSkuIdList);
        ArrayList<SkuInfo> skuInfoListFinal = new ArrayList<>();
        skuInfoList.stream().forEach(skuInfo -> {
            for (Long aLong : skuIdList) {
                if (skuInfo.getId().equals(aLong)) {
                    skuInfoListFinal.add(skuInfo);
                }
            }
        });
        return skuInfoListFinal;
    }

    @Override
    public Map<String, Object> findActivityRuleList(Long activityId) {
        HashMap<String, Object> map = new HashMap<>();

        QueryWrapper<ActivityRule> activityRuleQueryWrapper = new QueryWrapper<>();
        activityRuleQueryWrapper.eq("activity_id", activityId);
        List<ActivityRule> activityRuleList = activityRuleMapper.selectList(activityRuleQueryWrapper);
        map.put("activityRuleList", activityRuleList);

        QueryWrapper<ActivitySku> activitySkuQueryWrapper = new QueryWrapper<>();
        activitySkuQueryWrapper.eq("activity_id", activityId);
        List<ActivitySku> activitySkuList = activitySkuMapper.selectList(activitySkuQueryWrapper);
        List<Long> skuIdList = activitySkuList.stream().map(ActivitySku::getSkuId).collect(Collectors.toList());
        List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoBySkuIdList(skuIdList);
        map.put("skuInfoList", skuInfoList);

        QueryWrapper<CouponInfo> couponInfoQueryWrapper = new QueryWrapper<>();
        couponInfoQueryWrapper.eq("activity_id", activityId);
        List<CouponInfo> couponInfoList = couponInfoMapper.selectList(couponInfoQueryWrapper);
        map.put("couponInfoList", couponInfoList);

        return map;
    }
}
