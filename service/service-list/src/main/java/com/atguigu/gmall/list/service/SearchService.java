package com.atguigu.gmall.list.service;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;

import java.io.IOException;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/22 19:40
 * @Version 1.8
 */
public interface SearchService {
    /**
     * 上架商品列表
     * @param skuId
     */
    void upperGoods(Long skuId);

    /**
     * 下架商品列表
     * @param skuId
     */
    void lowerGoods(Long skuId);

    Result incrHotScore(Long skuId);

    SearchResponseVo search(SearchParam searchParam) throws IOException;
}
