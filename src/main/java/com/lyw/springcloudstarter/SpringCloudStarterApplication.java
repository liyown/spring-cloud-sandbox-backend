package com.lyw.springcloudstarter;

import com.lyw.springcloudstarter.openfeign.config.FeignLogConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.lyw.springcloudstarter.mapper")
@EnableFeignClients(basePackages = "com.lyw.springcloudstarter.openfeign.client",defaultConfiguration = FeignLogConfig.class)
public class SpringCloudStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudStarterApplication.class, args);
    }

}
