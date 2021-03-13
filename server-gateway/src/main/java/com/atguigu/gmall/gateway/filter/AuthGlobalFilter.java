package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.IpUtil;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author mqx
 * @date 2020-11-16 10:41:04
 */
@Component
public class AuthGlobalFilter implements GlobalFilter {

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${authUrls.url}")
    private String authUrlsUrl; // authUrlsUrl = trade.html,myOrder.html,list.html

    //  获取对象
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 过滤作用
     * @param exchange 获取请求，响应
     * @param chain 过滤器链
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //  限制用户直接通过浏览器访问 /**/inner/** 这样的数据接口
        //  获取用户访问的url http://list.gmall.com/list.html?category3Id=61
        ServerHttpRequest request = exchange.getRequest();
        //  request.getURI() = http://item.gmall.com/32.html
        //  path = 32.html
        String path = request.getURI().getPath();
        System.out.println("path:\t"+path);
        //  验证url 中是否包含 /**/inner/** 这样格式的请求！
        if(antPathMatcher.match("/**/inner/**",path)){
            //  限制访问
            ServerHttpResponse response = exchange.getResponse();
            //  信息提示：  209 没有权限访问
            return out(response, ResultCodeEnum.PERMISSION);
        }

        //  获取用户Id
        String userId = getUserId(request);
        //  获取临时用户Id
        String userTempId = getUserTempId(request);

        //  判断用户Id , 获取用户Id 失败！ 验证当前登录的Ip 与 缓存中的Ip 不一致！
        if("-1".equals(userId)){
            //  限制访问
            ServerHttpResponse response = exchange.getResponse();
            //  信息提示：  209 没有权限访问
            return out(response, ResultCodeEnum.PERMISSION);
        }

        //  用户是否访问了 带有 /api/**/auth/** 数据接口，如果访问了，则要限制
        if (antPathMatcher.match("/api/**/auth/**",path)){
            //  判断用户是否登录 ，如果没有登录，则提示信息，没有权限的信息！
            if (StringUtils.isEmpty(userId)){
                //  限制访问
                ServerHttpResponse response = exchange.getResponse();
                //  信息提示：  208 未登陆
                return out(response, ResultCodeEnum.LOGIN_AUTH);
            }
        }

        //  限制用户是否访问了 trade.html,myOrder.html,list.html
        //  获取到黑名单，白名单的数据  authUrlsUrl = trade.html,myOrder.html,list.html
        //  对黑/白名单进行分割
        String[] split = authUrlsUrl.split(",");
        //  判断 split
        if (split!=null && split.length>0){
            //  循环遍历
            for (String authUrl : split) {
                //  authUrl = trade.html |   authUrl = myOrder.html  |  authUrl = list.html
                //  用户访问的路径中如果包含上述的xxx.html,并且 用户为空 ！必须登录，
                //  String str = "abcdef"  判断 str 中是否包含  e ,怎么写？
                if (path.indexOf(authUrl)!=-1 && StringUtils.isEmpty(userId)){
                    //  必须登录
                    ServerHttpResponse response = exchange.getResponse();
                    //  设置状态
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    //  设置一个地址
                    //  第一个参数：设置本地， 第二个参数数值跳转的数据： "http://www.gmall.com/login.html?originUrl="+request.getURI()
                    //  http://passport.gmall.com/login.html?originUrl=http://list.gmall.com/
                    response.getHeaders().set(HttpHeaders.LOCATION,"http://www.gmall.com/login.html?originUrl="+request.getURI());
                    //  做重定向去 login 页面！
                    return response.setComplete();
                }
            }
        }

        //  用户Id 不为空的时候，将用户信息传递到后台! 比如说  order ,payment 等。
        if (!StringUtils.isEmpty(userId) || !StringUtils.isEmpty(userTempId)){

            if(!StringUtils.isEmpty(userId)){
                //  将用户信息userId 传递到后台，放入header,后续在 AuthContextHolder 中获取，
                //  还有一个web-util 中也获取了！ 后续讲解！
                request.mutate().header("userId",userId).build();
            }

            if (!StringUtils.isEmpty(userTempId)){
                request.mutate().header("userTempId",userTempId).build();
            }

            //  设置返回值 设置好的request ---> exchange对象
            return chain.filter(exchange.mutate().request(request).build());
        }
        //  完成返回
        return chain.filter(exchange);
    }

    private String getUserTempId(ServerHttpRequest request) {
        String userTempId = "";
        //  获取header 中。
        List<String> list = request.getHeaders().get("userTempId");
        if (!StringUtils.isEmpty(list)){
            //  获取数据X
            userTempId = list.get(0);
        }
        //  获取cookie 中。
        HttpCookie httpCookie = request.getCookies().getFirst("userTempId");
        if(httpCookie!=null){
            //  value 是啥？   值
            userTempId = httpCookie.getValue();
        }
        return userTempId;
    }

    //  获取用户Id
    private String getUserId(ServerHttpRequest request) {
        //  用户Id 在缓存，在缓存 缓存的key user:login:token
        //  key 的关键在于获取token ，token 存储在header ，cookie 中。
        String token = "";
        //  获取header 中。
        List<String> list = request.getHeaders().get("token");
        if (!StringUtils.isEmpty(list)){
            //  获取数据
            token = list.get(0);
        }
        //  Cookie cookie = new Cookie("token","haha");
        //  获取cookie 中。
        HttpCookie httpCookie = request.getCookies().getFirst("token");
        if(httpCookie!=null){
            //  value 是啥？
            token = httpCookie.getValue();
        }
        //  token 有了，整缓存的key
        if (!StringUtils.isEmpty(token)){
            String userKey = "user:login:" + token;
            //  获取到缓存的数据：
            String objectStr = (String) redisTemplate.opsForValue().get(userKey);
            //  将字符串转换为JsonObject
            JSONObject jsonObject = JSON.parseObject(objectStr, JSONObject.class);
            //  校验Ip 地址
            String ip = (String) jsonObject.get("ip");
            if (ip.equals(IpUtil.getGatwayIpAddress(request))){
                String userId = (String) jsonObject.get("userId");
                return userId;
            }else {
                return "-1";
            }
        }
        return "";
    }

    //  用户消息提示
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        //  将用户的提示消息直接输入到页面！ 用户提示消息 resultCodeEnum
        Result<Object> result = Result.build(null, resultCodeEnum);
        //  将这个返回结果变成字符串
        //  如果不设置字符，防止出现字符集乱码可以使用
        //  String str = JSON.toJSONString(result).getBytes("utf-8");
        String str = JSON.toJSONString(result);
        DataBuffer wrap = response.bufferFactory().wrap(str.getBytes());
        //  向页面输入内容的时候，需要设置排头 "Content-Type", "application/json;charset=UTF-8"
        response.getHeaders().add("Content-Type","application/json;charset=UTF-8");
        //  直接将数据输入到页面！
        return response.writeWith(Mono.just(wrap));
    }
}
