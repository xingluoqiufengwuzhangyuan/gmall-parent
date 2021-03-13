package com.atguigu.gmall.activity.service;

import com.atguigu.gmall.model.activity.CouponInfo;
import com.atguigu.gmall.model.activity.CouponRuleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: XLQFWZY
 * @Date: 2021/3/12 18:32
 * @Version 1.8
 */
public interface CouponInfoService extends IService<CouponInfo> {
    IPage<CouponInfo> selectPage(Page<CouponInfo> pageParam);

    Map<String,Object> findCouponRuleList(Long id);

    void saveCouponRule(CouponRuleVo couponRuleVo);

    List<CouponInfo> findCouponByKeyword(String keyword);
}
