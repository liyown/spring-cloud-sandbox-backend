package com.lyw.springcloudstarter.judge.codesandbox;


import com.lyw.springcloudstarter.domain.dto.codesandbox.CodeRunResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author: liuyaowen
 * @poject: springboot-starter
 * @create: 2024-07-24 19:36
 * @Description:
 */
@Slf4j
public class DelegateCodeExecutorSandbox implements ICodeExecuteSandBox{

    @Value("${code-sandbox.type}")
    private String codeSandboxType;

    private final Map<String, ICodeExecuteSandBox> codeSanBoxMap = new HashMap<String, ICodeExecuteSandBox>();

    public DelegateCodeExecutorSandbox(List<ICodeExecuteSandBox> codeExecuteSandBoxes) {
        for (ICodeExecuteSandBox codeExecuteSandBox : codeExecuteSandBoxes) {
            codeSanBoxMap.put(codeExecuteSandBox.getType().name(), codeExecuteSandBox);
        }
    }

    public void registerCodeExecuteSandBox(ICodeExecuteSandBox codeExecuteSandBox) {
        codeSanBoxMap.put(codeExecuteSandBox.getType().name(), codeExecuteSandBox);
    }

    public DelegateCodeExecutorSandbox() {

    }

    @Override
    public CodeRunResult<List<String>> execute(String code, List<String> input) {
        ICodeExecuteSandBox codeExecuteSandBox = codeSanBoxMap.get(codeSandboxType);
        CodeRunResult<List<String>> result = codeExecuteSandBox.execute(code, input);
        log.info("CodeExecutorSandboxType: {}, Code: {}, Input: {}, Result: {}", codeSandboxType, code, input, result);
        return result;
    }

    @Override
    public TYPE getType() {
        return TYPE.DELEGATE;
    }
}
