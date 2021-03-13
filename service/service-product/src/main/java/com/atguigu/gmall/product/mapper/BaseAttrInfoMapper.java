package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/1/30 20:45
 * @Version 1.8
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    List<BaseAttrInfo> selectBaseAttrInfoList(@Param("category1Id")Long category1Id, @Param("category2Id")Long category2Id,@Param("category3Id") Long category3Id);

    List<BaseAttrInfo> selectBaseAttrInfoListBySkuId(Long skuId);
}
