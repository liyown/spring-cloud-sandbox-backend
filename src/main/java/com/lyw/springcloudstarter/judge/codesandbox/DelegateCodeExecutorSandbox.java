package com.lyw.springcloudstarter.judge.codesandbox;


import com.lyw.springcloudstarter.domain.dto.codesandbox.CodeRunResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * @author: liuyaowen
 * @poject: springboot-starter
 * @create: 2024-07-24 19:36
 * @Description:
 */
@Slf4j
public class DelegateCodeExecutorSandbox implements ICodeExecuteSandBox{

    private final ICodeExecuteSandBox codeExecuteSandBox;

    public DelegateCodeExecutorSandbox(ICodeExecuteSandBox codeExecuteSandBox) {
        this.codeExecuteSandBox = codeExecuteSandBox;
    }

    @Override
    public CodeRunResult<List<String>> execute(String code, List<String> input) {
        log.info("CodeExecuteInfo: code: {}, input: {}", code, input);
        CodeRunResult<List<String>> result = codeExecuteSandBox.execute(code, input);
        log.info("CodeExecuteResult: {}", result);
        return result;
    }
}
