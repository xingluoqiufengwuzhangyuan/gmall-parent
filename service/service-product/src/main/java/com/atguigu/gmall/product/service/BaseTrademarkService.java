package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/3 8:55
 * @Version 1.8
 */
public interface BaseTrademarkService extends IService<BaseTrademark> {
    IPage<BaseTrademark> getPage(Page<BaseTrademark> baseTrademarkPage);

    List<BaseTrademark> findBaseTrademarkByIdList(List<Long> idList);

    List<BaseTrademark> findBaseTrademarkByKeyword(String keyword);
}
