package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Created by IntelliJ IDEA.
 * @Author: Zzx
 * @Date: 2021/2/23 23:55
 * @Version 1.8
 */
@Controller
public class ListController {
    @Resource
    private ListFeignClient listFeignClient;

    /**
     * 列表搜索
     * @param searchParam
     * @return
     */
    @RequestMapping("list.html")
    public String search(SearchParam searchParam, Model model) {
        //  返回数据
        Result<Map> result = listFeignClient.list(searchParam);

        //  获取排序： 获取用户输入的排序规则
        Map<String,Object> orderMap = this.dealOrder(searchParam.getOrder());

        //  制作urlParam 记录用户点击哪些数据进行检索的！
        String urlParam = makeUrlParam(searchParam);
        //  后台需要存储一个trademarkParam
        String trademarkParam = makeTrademark(searchParam);

        //  获取传递过来的销售属性值
        List<Map<String,String>> propsParamList = this.makeProps(searchParam.getProps());

        //  存储数据
        model.addAttribute("orderMap",orderMap);
        //  存储平台属性
        model.addAttribute("propsParamList",propsParamList);
        //  存储的品牌
        model.addAttribute("trademarkParam",trademarkParam);
        //  result 返回的封装结果集：页面需要获取的数据 正好是返回结果的属性
        model.addAllAttributes(result.getData());
        //  需要存储数据
        model.addAttribute("searchParam",searchParam);
        model.addAttribute("urlParam",urlParam);
        return "list/index";
    }
    //  获取排序规则：order=2:asc order=2:desc
    private Map<String, Object> dealOrder(String order) {

        Map<String, Object> map = new HashMap<>();
        //  先判断不为空
        if (!StringUtils.isEmpty(order)){
            //  进行分割
            String[] split = order.split(":");
            if (split!=null && split.length==2){
                map.put("type",split[0]);
                map.put("sort",split[1]);
            }else {
                map.put("type","1");
                map.put("sort","desc");
            }
        }else {
            map.put("type","1");
            map.put("sort","desc");
        }

        return map;
    }

    //  获取销售属性面包屑
    //  &props=24:128G:机身内存&props=23:4G:运行内存
    //  机身内存 : 128G
    private List<Map<String, String>> makeProps(String[] props) {
        List<Map<String, String>>  list = new ArrayList<>();
        if (props!=null && props.length>0){
            //  循环遍历
            for (String prop : props) {
                //  prop = 24:128G:机身内存
                String[] split = prop.split(":");
                if (split!=null && split.length==3){
                    Map<String, String> map = new HashMap<>();
                    //  存储数据 key = 平台属性名称， value: 平台属性值名称
                    // map.put(split[2],split[1]);
                    map.put("attrId",split[0]);
                    map.put("attrValue",split[1]);
                    map.put("attrName",split[2]);
                    //  将每个平台属性面包屑放入集合
                    list.add(map);
                }
            }
        }
        return list;
    }

    //  制作品牌 trademark=4:小米
    private String makeTrademark(SearchParam searchParam) {
        //  trademark=4:小米  返回 品牌：小米
        String trademark = searchParam.getTrademark();
        if (!StringUtils.isEmpty(trademark)){
            String[] split = trademark.split(":");
            if (split!=null && split.length==2){
                return "品牌:" + split[1];
            }
        }
        return null;
    }

    //  制作查询参数的拼接
    private String makeUrlParam(SearchParam searchParam) {
        StringBuilder sb = new StringBuilder();
        //  用户检索的时候；是否根据三级分类Id 检索
        //  http://list.gmall.com/list.html?category3Id=61
        if (!StringUtils.isEmpty(searchParam.getCategory3Id())){
            sb.append("category3Id=").append(searchParam.getCategory3Id());
        }

        if (!StringUtils.isEmpty(searchParam.getCategory2Id())){
            sb.append("category2Id=").append(searchParam.getCategory2Id());
        }

        if (!StringUtils.isEmpty(searchParam.getCategory1Id())){
            sb.append("category1Id=").append(searchParam.getCategory1Id());
        }

        //  还有可能根据关键字
        //  http://list.gmall.com/list.html?keyword=手机
        if (!StringUtils.isEmpty(searchParam.getKeyword())){
            sb.append("keyword=").append(searchParam.getKeyword());
        }

        //  http://list.gmall.com/list.html?category3Id=61&trademark=4:小米
        if (!StringUtils.isEmpty(searchParam.getTrademark())){
            sb.append("&trademark=").append(searchParam.getTrademark());
        }

        //   http://list.gmall.com/list.html?category3Id=61&trademark=4:小米&props=24:128G:机身内存&props=23:4G:运行内存
        String[] props = searchParam.getProps();
        if (props!=null && props.length>0){
            //  循环
            for (String prop : props) {
                sb.append("&props=").append(prop);
            }
        }
       
        //  list.html?category3Id=61&trademark=2:苹果&order=
        return "list.html?"+sb.toString();
    }
}
