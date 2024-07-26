package com.lyw.springcloudstarter.config;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.FileRefreshableDataSource;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author: liuyaowen
 * @poject: spring-cloud-starter
 * @create: 2024-07-25 00:18
 * @Description:
 */
@Configuration
public class SentinelConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Converter<String, List<FlowRule>> flowRuleListParser = source -> {
        try {
            return objectMapper.readValue(source, new TypeReference<List<FlowRule>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };
    private final Converter<String, List<DegradeRule>> degradeRuleListParser = source -> {
        try {
            return objectMapper.readValue(source, new TypeReference<List<DegradeRule>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };
    private final Converter<String, List<SystemRule>> systemRuleListParser = source -> {
        try {
            return objectMapper.readValue(source, new TypeReference<List<SystemRule>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };

    //    TODO 开启file数据源 关闭注解
    @PostConstruct
    private void listenRules() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        String flowRulePath = URLDecoder.decode(classLoader.getResource("sentinel/FlowRule.json").getFile(), StandardCharsets.UTF_8);
        String degradeRulePath = URLDecoder.decode(classLoader.getResource("sentinel/DegradeRule.json").getFile(), StandardCharsets.UTF_8);
        String systemRulePath = URLDecoder.decode(classLoader.getResource("sentinel/SystemRule.json").getFile(), StandardCharsets.UTF_8);

        // Data source for FlowRule
        FileRefreshableDataSource<List<FlowRule>> flowRuleDataSource = new FileRefreshableDataSource<>(flowRulePath, flowRuleListParser);
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());

        // Data source for DegradeRule
        FileRefreshableDataSource<List<DegradeRule>> degradeRuleDataSource = new FileRefreshableDataSource<>(degradeRulePath, degradeRuleListParser);
        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());

        // Data source for SystemRule
        FileRefreshableDataSource<List<SystemRule>> systemRuleDataSource = new FileRefreshableDataSource<>(systemRulePath, systemRuleListParser);
        SystemRuleManager.register2Property(systemRuleDataSource.getProperty());
    }

    //    TODO 开启nacos数据源 关闭注解
//    @PostConstruct
    public void init() throws Exception {
        final String remoteAddress = "localhost";
        final String groupId = "Sentinel:Demo";
        final String dataId = "com.alibaba.csp.sentinel.demo.flow.rule";

        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, dataId, source -> {
            try {
                return objectMapper.readValue(source, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }
}
