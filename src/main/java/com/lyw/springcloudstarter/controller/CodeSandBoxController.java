package com.lyw.springcloudstarter.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.lyw.springcloudstarter.domain.dto.codesandbox.CodeExecuteRequest;
import com.lyw.springcloudstarter.domain.dto.codesandbox.CodeRunResult;
import com.lyw.springcloudstarter.judge.codesandbox.ICodeExecuteSandBox;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: liuyaowen
 * @poject: spring-cloud-sandbox
 * @create: 2024-07-26 18:55
 * @Description:
 */
@RestController
@RequestMapping("/codesandbox")
@Slf4j
public class CodeSandBoxController {

    @Resource
    private ICodeExecuteSandBox codeExecuteSandBox;

    @PostMapping("/execute")
    @SentinelResource(value = "execute", fallback = "fallback")
    public CodeRunResult<List<String>> execute(@RequestBody CodeExecuteRequest request) throws BlockException {
        return codeExecuteSandBox.execute(request.getCode(), request.getInput());
    }

}
