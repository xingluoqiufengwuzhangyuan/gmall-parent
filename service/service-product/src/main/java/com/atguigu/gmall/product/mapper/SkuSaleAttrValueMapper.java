package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/1/30 20:45
 * @Version 1.8
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    List<Map> selectSaleAttrValuesBySpu(Long spuId);
}
