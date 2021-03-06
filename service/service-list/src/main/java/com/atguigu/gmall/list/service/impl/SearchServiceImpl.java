package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.repository.GoodsRepository;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/22 19:42
 * @Version 1.8
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private GoodsRepository goodsRepository;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void upperGoods(Long skuId) {
        Goods good = new Goods();
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if (skuInfo != null) {
                good.setId(skuId);
                good.setDefaultImg(skuInfo.getSkuDefaultImg());
                good.setTitle(skuInfo.getSkuName());
                good.setPrice(skuInfo.getPrice().doubleValue());
                good.setCreateTime(new Date());
            }
            return skuInfo;
        });
        CompletableFuture<Void> categoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo -> {
            if (skuInfo != null) {
                BaseCategoryView categoryView = productFeignClient.getCategoryView(skuId);
                if (categoryView != null) {
                    good.setCategory1Id(categoryView.getCategory1Id());
                    good.setCategory1Name(categoryView.getCategory1Name());
                    good.setCategory2Id(categoryView.getCategory2Id());
                    good.setCategory2Name(categoryView.getCategory2Name());
                    good.setCategory3Id(categoryView.getCategory3Id());
                    good.setCategory3Name(categoryView.getCategory3Name());
                }
            }
        }));
        CompletableFuture<Void> trademarkCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null) {
                BaseTrademark trademark = productFeignClient.getTrademark(skuId);
                if (trademark != null) {
                    good.setTmId(trademark.getId());
                    good.setTmName(trademark.getTmName());
                    good.setTmLogoUrl(trademark.getLogoUrl());
                }
            }
        });
        CompletableFuture<Void> collectCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null) {
                List<BaseAttrInfo> attrList = productFeignClient.getAttrList(skuId);
                if (attrList != null) {
                    List<SearchAttr> collect = attrList.stream().map((baseAttrInfo) -> {
                        SearchAttr searchAttr = new SearchAttr();
                        searchAttr.setAttrId(baseAttrInfo.getId());
                        searchAttr.setAttrName(baseAttrInfo.getAttrName());
                        searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
                        return searchAttr;
                    }).collect(Collectors.toList());
                    good.setAttrs(collect);
                }
            }
        });
        CompletableFuture.allOf(skuInfoCompletableFuture,
                categoryViewCompletableFuture,
                trademarkCompletableFuture,
                collectCompletableFuture).join();
        goodsRepository.save(good);
    }


    @Override
    public void lowerGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    @Override
    public Result incrHotScore(Long skuId) {
        System.err.println("????????????----------------");
        String hotkey = "hotScore";
        Double hotScore = redisTemplate.opsForZSet().incrementScore(hotkey, "skuId:" + skuId, 1);
        if (hotScore % 10 == 0) {
            Optional<Goods> optional = goodsRepository.findById(skuId);
            Goods goods = optional.get();
            goods.setHotScore(Math.round(hotScore));
            goodsRepository.save(goods);
        }
        return Result.ok();
    }

    @Override
    public SearchResponseVo search(SearchParam searchParam) throws IOException {
//        ??????dsl??????
        SearchRequest searchRequest = buildQueryDsl(searchParam);
//??????????????????
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//       ????????????????????????searchResponseVo,trademarkList,attrsList,goodsList,total
       SearchResponseVo searchResponseVo= parseSearchResult(searchResponse);
//private Integer pageSize;//?????????????????????
//    private Integer pageNo;//????????????
//    private Long totalPages;
        searchResponseVo.setPageSize(searchParam.getPageSize());
        searchResponseVo.setPageNo(searchParam.getPageNo());
        long totalPages = (searchResponseVo.getTotal() + searchParam.getPageSize() - 1) / searchParam.getPageSize();
        searchResponseVo.setTotalPages(totalPages);
        return searchResponseVo;
    }

    private SearchResponseVo parseSearchResult(SearchResponse searchResponse) {
//  ????????????
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        /*
         private List<SearchResponseTmVo> trademarkList;
         private List<SearchResponseAttrVo> attrsList = new ArrayList<>();
         private List<Goods> goodsList = new ArrayList<>();
         private Long total;//????????????
         */
        //  ?????????????????????????????????
        Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().asMap();
        //  ??????key ?????? tmIdAgg
        //  ??????buckets ?????????????????????????????? Aggregation --> ParsedLongTerms
        ParsedLongTerms tmIdAgg = (ParsedLongTerms) aggregationMap.get("tmIdAgg");
        //  Function T R
        List<SearchResponseTmVo> trademarkList = tmIdAgg.getBuckets().stream().map((bucket) -> {
            //  ??????????????????
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            String tmId = ((Terms.Bucket) bucket).getKeyAsString();
            //  ????????????Id
            searchResponseTmVo.setTmId(Long.parseLong(tmId));
            //  ????????????????????? Aggregation ---> ParsedStringTerms
            ParsedStringTerms tmNameAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().asMap().get("tmNameAgg");
            //  ??????????????????
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseTmVo.setTmName(tmName);

            //  ???????????????LogoUrl
            ParsedStringTerms tmLogoUrlAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().asMap().get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            searchResponseTmVo.setTmLogoUrl(tmLogoUrl);

            return searchResponseTmVo;
        }).collect(Collectors.toList());

        //  ??????
        searchResponseVo.setTrademarkList(trademarkList);

        //  ??????????????????????????????????????? ???????????? nested Aggregation -->
        ParsedNested attrAgg = (ParsedNested) aggregationMap.get("attrAgg");
        //  ????????????Id ??????
        ParsedLongTerms attrIdAgg = (ParsedLongTerms) attrAgg.getAggregations().asMap().get("attrIdAgg");
        //  ????????????????????????
        List<SearchResponseAttrVo> attrsList = attrIdAgg.getBuckets().stream().map((bucket) -> {
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //  ????????????Id
            Number keyAsNumber = ((Terms.Bucket) bucket).getKeyAsNumber();
            searchResponseAttrVo.setAttrId(keyAsNumber.longValue());
            //  ??????????????????
            ParsedStringTerms attrNameAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().asMap().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseAttrVo.setAttrName(attrName);

            //  ?????????????????????
            ParsedStringTerms attrValueAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().asMap().get("attrValueAgg");
            //  ???????????????????????????

            List<? extends Terms.Bucket> buckets = attrValueAgg.getBuckets();

            //   Terms.Bucket::getKeyAsString ?????????  bucket1.getKeyAsString()
            List<String> strList = buckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());
            //  ??????????????????
            //            List<String> strList = new ArrayList<>();
            //            //  ??????????????????
            //            for (Terms.Bucket bucket1 : buckets) {
            //                //  ????????????????????????
            //                String attrValueName = bucket1.getKeyAsString();
            //                strList.add(attrValueName);
            //            }
            //  ??????????????????????????????
            searchResponseAttrVo.setAttrValueList(strList);
            return searchResponseAttrVo;
        }).collect(Collectors.toList());

        //  ??????
        searchResponseVo.setAttrsList(attrsList);

        SearchHits hits = searchResponse.getHits();
        //  ????????????
        SearchHit[] subHits = hits.getHits();
        List<Goods> goodsList = new ArrayList<>();
        //  ?????????????????????
        if (subHits!=null && subHits.length>0){
            for (SearchHit subHit : subHits) {
                //  ??????source
                String sourceJson = subHit.getSourceAsString();
                //  ?????????Goods
                Goods goods = JSON.parseObject(sourceJson, Goods.class);
                //  ?????????????????? ??????????????????Id ???????????????????????????????????????
                if (subHit.getHighlightFields().get("title")!=null){
                    Text title = subHit.getHighlightFields().get("title").getFragments()[0];
                    //  ???????????????????????????
                    goods.setTitle(title.toString());
                }
                //  ???????????????
                goodsList.add(goods);
            }
        }
        //  ????????????sku??????
        searchResponseVo.setGoodsList(goodsList);

        // ???????????????
        searchResponseVo.setTotal(hits.totalHits);
        return searchResponseVo;
    }

    //????????????dsl??????
    private SearchRequest buildQueryDsl(SearchParam searchParam) {
//        ???????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//       query- bool-filter
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //  ???????????????????????????????????????
        if (!StringUtils.isEmpty(searchParam.getKeyword())){
            //  Operator.AND ?????????????????????????????????????????????????????????
            // {must ---match ---title }
            boolQueryBuilder.must(QueryBuilders.matchQuery("title",searchParam.getKeyword()).operator(Operator.AND));
        }

        //  ????????????Id
        if (!StringUtils.isEmpty(searchParam.getCategory3Id())){
            //  {bool - filter - category3Id}
            boolQueryBuilder.filter(QueryBuilders.termQuery("category3Id",searchParam.getCategory3Id()));
        }

        if (!StringUtils.isEmpty(searchParam.getCategory2Id())){
            //  {bool - filter - category3Id}
            boolQueryBuilder.filter(QueryBuilders.termQuery("category2Id",searchParam.getCategory2Id()));
        }

        if (!StringUtils.isEmpty(searchParam.getCategory1Id())){
            //  {bool - filter - category3Id}
            boolQueryBuilder.filter(QueryBuilders.termQuery("category1Id",searchParam.getCategory1Id()));
        }

        //  ??????????????? ????????????????????? trademark=2:??????
        String trademark = searchParam.getTrademark();
        if(!StringUtils.isEmpty(trademark)){
            // trademark=2:??????
            // ??????????????????Id ??????????????????
            String[] split = trademark.split(":");
            if (split!=null && split.length==2){
                //  ?????????Id split[0]
                boolQueryBuilder.filter(QueryBuilders.termQuery("tmId",split[0]));
            }
        }
//  ????????????????????? props=23:8G:????????????&props=24:256G:????????????
        //  ????????????Id????????????????????????????????????
        String[] props = searchParam.getProps();
        if (props!=null && props.length>0){
            // ????????????
            for (String prop : props) {
                //  ??????prop ?????????????????????  23:8G:???????????? ?????????Id 0?????????????????? 1?????????????????? 2
                //  org.apache.commons.lang3.StringUtils.split(":")
                String[] split = prop.split(":");
                if (split!=null && split.length==3){
                    // ???????????????????????????nested??? ??????
                    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                    // ????????????????????? ??????
                    BoolQueryBuilder subBoolQuery = QueryBuilders.boolQuery();

                    //  ??????????????????
                    subBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",split[0]));
                    subBoolQuery.must(QueryBuilders.termQuery("attrs.attrValue",split[1]));

                    //  ????????????bool ???????????? must -- nested ---
                    boolQuery.must(QueryBuilders.nestedQuery("attrs",subBoolQuery, ScoreMode.None));

                    //  ????????????bool ??????filter
                    boolQueryBuilder.filter(boolQuery);
                }
            }
        }
        //  ???????????????
        //  1:hotScore 2:price
        String order = searchParam.getOrder();
        if (!StringUtils.isEmpty(order)){
            //  ???????????? ??????????????????????????? ???  1:asc 1:desc  2:asc  2:desc
            String[] split = order.split(":");
            if (split!=null && split.length==2){
                String field = null;
                // ??????
                switch (split[0]){
                    case "1":
                        // ?????? hotScore
                        field = "hotScore";
                        break;
                    case "2":
                        field = "price";
                        break;
                }
                // ???????????? ??????????????????????????????asc ,????????????????????????
                searchSourceBuilder.sort(field,"asc".equals(split[1])? SortOrder.ASC:SortOrder.DESC);
            }else {
                //  ?????????????????????
                searchSourceBuilder.sort("hotScore",SortOrder.DESC);
            }
        }

        //  ??????
        //  (????????? + pageSize - 1)/ pageSize;
        //   (pageNo - 1)*pageSize;
        //   4  2 |  pageNo = 1  0,2 pageNo = 2  2,2
        int from = (searchParam.getPageNo()-1)*searchParam.getPageSize();
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(searchParam.getPageSize());
        //  ??????
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style=color:red>");
        highlightBuilder.postTags("</span>");
        //  ?????????????????????????????????????????????
        searchSourceBuilder.highlighter(highlightBuilder);
        //  ????????? ?????? + ????????????
        //   tmIdAgg --  terms  -- field
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        //  ????????????
        searchSourceBuilder.aggregation(termsAggregationBuilder);

        // ?????????????????? ??????nested ??????
        searchSourceBuilder.aggregation(AggregationBuilders.nested("attrAgg","attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))));

        // {query -- bool}
        searchSourceBuilder.query(boolQueryBuilder);

        //  ????????????????????? ??????????????????????????????????????????????????????
        searchSourceBuilder.fetchSource(new String[]{"id","defaultImg","title","price"},null);
        //  GET /goods/info/_search
        SearchRequest searchRequest = new SearchRequest("goods");
        searchRequest.types("info");

        String dsl = searchSourceBuilder.toString();
        System.out.println("DSL:\t"+dsl);
        //  ????????????dsl ????????????searchRequest;
        searchRequest.source(searchSourceBuilder);
        //  ??????
        return searchRequest;
    }
}
