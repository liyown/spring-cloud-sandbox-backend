package com.lyw.springcloudstarter.openfeign.config;


import com.lyw.springcloudstarter.openfeign.client.fallback.ItermsServiceFallbackFactory;
import com.lyw.springcloudstarter.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import static com.lyw.springcloudstarter.utils.UserContext.USER_INFO;


/**
 * @author: liuyaowen
 * @poject: hmall
 * @create: 2024-07-01 20:41
 * @Description:
 */
@Slf4j
public class FeignLogConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }


    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // 1.获取用户信息
            Long user = UserContext.getUser();
            if (user == null) {
                log.warn("Feign调用未获取到用户信息");
                // 获取当前完整调用路径
                log.warn("Feign调用路径:{} {}", requestTemplate.request().httpMethod(), requestTemplate.request().url());
                return;
            }
            // 2.将用户信息放入请求头
            requestTemplate.header(USER_INFO, user.toString());
            log.info("Feign调用 userId:{}", user);
        };
    }

    @Bean
    public ItermsServiceFallbackFactory itermServiceFallbackFactory() {
        return new ItermsServiceFallbackFactory();
    }

}
