package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author mqx
 * @date 2020-11-4 10:22:05
 */
@Configuration // CorsConfig.xml
public class CorsConfig {

    //  CorsWebFilter
    @Bean
    public CorsWebFilter getCorsWebFilter(){
        //  配置跨域
        //  声明一个对象CorsConfiguration
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);

        //  CorsConfigurationSource 这个是接口，接口不能new !
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);

        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }
}
