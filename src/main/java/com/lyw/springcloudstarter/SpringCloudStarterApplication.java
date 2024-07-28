package com.lyw.springcloudstarter;

import com.alibaba.csp.sentinel.log.LogBase;
import com.lyw.springcloudstarter.openfeign.config.FeignLogConfig;
import jakarta.annotation.PostConstruct;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.io.FileInputStream;
import java.io.IOException;


@SpringBootApplication
@MapperScan("com.lyw.springcloudstarter.mapper")
@EnableFeignClients(basePackages = "com.lyw.springcloudstarter.openfeign.client",defaultConfiguration = FeignLogConfig.class)
public class SpringCloudStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudStarterApplication.class, args);
    }

    @PostConstruct
    private void init() {
        System.out.println(System.getProperty(LogBase.LOG_DIR));
    }

}
