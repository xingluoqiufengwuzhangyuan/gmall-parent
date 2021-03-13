package com.atguigu.gmall.activity.service.impl;

import com.atguigu.gmall.activity.mapper.CouponInfoMapper;
import com.atguigu.gmall.activity.mapper.CouponRangeMapper;
import com.atguigu.gmall.activity.service.CouponInfoService;
import com.atguigu.gmall.model.activity.CouponInfo;
import com.atguigu.gmall.model.activity.CouponRange;
import com.atguigu.gmall.model.activity.CouponRuleVo;
import com.atguigu.gmall.model.enums.CouponRangeType;
import com.atguigu.gmall.model.enums.CouponType;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Created by IntelliJ IDEA.
 * @Author: XLQFWZY
 * @Date: 2021/3/12 18:33
 * @Version 1.8
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    @Resource
    private CouponInfoMapper couponInfoMapper;

    @Resource
    private CouponRangeMapper couponRangeMapper;

    @Resource
    private ProductFeignClient productFeignClient;

    @Override
    public IPage<CouponInfo> selectPage(Page<CouponInfo> pageParam) {
        QueryWrapper<CouponInfo> couponInfoQueryWrapper = new QueryWrapper<>();
        couponInfoQueryWrapper.orderByDesc("id");
        IPage<CouponInfo> couponInfoIPage = couponInfoMapper.selectPage(pageParam, couponInfoQueryWrapper);
        couponInfoIPage.getRecords().stream().forEach(couponInfo -> {
            couponInfo.setCouponTypeString(CouponType.getNameByType(couponInfo.getCouponType()));
            if (couponInfo.getRangeType() != null) {
                couponInfo.setRangeTypeString(CouponRangeType.getNameByType(couponInfo.getRangeType()));
            }
        });
        return couponInfoIPage;
    }

    @Override
    public Map<String,Object> findCouponRuleList(Long id) {
        HashMap<String, Object> map = new HashMap<>();
        CouponInfo couponInfo = getById(id);
        String rangeType = couponInfo.getRangeType();
        QueryWrapper<CouponRange> couponRangeQueryWrapper = new QueryWrapper<>();
        couponRangeQueryWrapper.eq("coupon_id",id);
        List<CouponRange> rangeInfoList = couponRangeMapper.selectList(couponRangeQueryWrapper);
        if (!CollectionUtils.isEmpty(rangeInfoList)) {
            List<Long> idList = rangeInfoList.stream().map(CouponRange::getRangeId).collect(Collectors.toList());
            if ("SPU".equals(rangeType)) {
                List<SpuInfo> couponRangeList = productFeignClient.findSpuInfoByIdList(idList);
                map.put("spuInfoList", couponRangeList);

            } else if ("TRADEMARK".equals(rangeType)) {
                List<BaseTrademark> couponRangeList = productFeignClient.findBaseTrademarkByIdList(idList);        map.put("couponRangeList", couponRangeList);
                map.put("category3List", couponRangeList);

            } else {
                List<BaseCategory3> couponRangeList = productFeignClient.findBaseCategory3ByIdList(idList);
                map.put("trademarkList", couponRangeList);
            }
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
        Long couponId = couponRuleVo.getCouponId();
        CouponInfo couponInfo = getById(couponId);
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setConditionNum(couponRuleVo.getConditionNum());
        couponInfo.setBenefitDiscount(couponRuleVo.getBenefitDiscount());
        couponInfo.setBenefitAmount(couponRuleVo.getBenefitAmount());
        couponInfo.setRangeType(couponRuleVo.getRangeType().name());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());

        couponInfoMapper.updateById(couponInfo);

        couponRangeMapper.delete(new QueryWrapper<CouponRange>().eq("coupon_id", couponId));

        CouponRangeType rangeType = couponRuleVo.getRangeType();
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        couponRangeList.stream().forEach(couponRange -> {
            couponRange.setCouponId(couponId);
            couponRangeMapper.insert(couponRange);
        });

    }

    @Override
    public List<CouponInfo> findCouponByKeyword(String keyword) {

        return couponInfoMapper.selectList(new QueryWrapper<CouponInfo>().like("coupon_name", keyword));
    }
}
