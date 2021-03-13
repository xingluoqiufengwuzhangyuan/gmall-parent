package com.atguigu.gmall.list.repository;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/22 19:41
 * @Version 1.8
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {

}
