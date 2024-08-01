package com.lyw.springcloudstarter.config;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.lyw.springcloudstarter.judge.codesandbox.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * @author: liuyaowen
 * @poject: spring-cloud-sandbox
 * @create: 2024-07-26 19:03
 * @Description:
 */
@Configuration
public class CodeSandBoxConfig {

    {
        FlowRule flowRule = new FlowRule();
        flowRule.setResource("execute");
        flowRule.setCount(10);
        flowRule.setGrade(1);
        FlowRuleManager.loadRules(List.of(flowRule));
    }



    @Bean
    @Primary
    public ICodeExecuteSandBox delegateCodeExecutorSandbox() {
        DelegateCodeExecutorSandbox delegateCodeExecutorSandbox = new DelegateCodeExecutorSandbox();
        delegateCodeExecutorSandbox.registerCodeExecuteSandBox(new JavaDockerCodeExecutorSandBox());
        delegateCodeExecutorSandbox.registerCodeExecuteSandBox(new JavaNativeCodeExecutorSandBox());
        return delegateCodeExecutorSandbox;
    }



}
