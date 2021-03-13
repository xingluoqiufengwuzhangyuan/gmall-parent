package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/3 8:56
 * @Version 1.8
 */
@Service
public class BaseTrademarkServiceImpl extends ServiceImpl<BaseTrademarkMapper, BaseTrademark> implements BaseTrademarkService {

    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;
    @Override
    public IPage<BaseTrademark> getPage(Page<BaseTrademark> baseTrademarkPage) {
        IPage<BaseTrademark> page = baseTrademarkMapper.selectPage(baseTrademarkPage, new QueryWrapper<BaseTrademark>().orderByDesc("id"));
        return page;
    }

    @Override
    public List<BaseTrademark> findBaseTrademarkByIdList(List<Long> idList) {
        List<BaseTrademark> baseTrademarkList = baseTrademarkMapper.selectBatchIds(idList);
        return baseTrademarkList;
    }

    @Override
    public List<BaseTrademark> findBaseTrademarkByKeyword(String keyword) {
        return baseTrademarkMapper.selectList(new QueryWrapper<BaseTrademark>().like("tm_name", keyword));
    }
}
