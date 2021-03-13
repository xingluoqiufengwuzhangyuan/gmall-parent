package com.atguigu.gmall.activity.mapper;

import com.atguigu.gmall.model.activity.ActivityInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/3/10 22:13
 * @Version 1.8
 */
@Mapper
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {
    List<Long> selectExistSkuIdList(@Param("skuIdList") List<Long> skuIdList);
}
