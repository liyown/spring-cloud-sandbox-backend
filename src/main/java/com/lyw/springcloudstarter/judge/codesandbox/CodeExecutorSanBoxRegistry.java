package com.lyw.springcloudstarter.judge.codesandbox;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: liuyaowen
 * @poject: springboot-starter
 * @create: 2024-07-24 19:43
 * @Description:
 */
public class CodeExecutorSanBoxRegistry {

    private final Map<String, ICodeExecuteSandBox> codeSanBoxMap = new HashMap<String, ICodeExecuteSandBox>();


    public void register(String type, ICodeExecuteSandBox codeExecuteSandBox) {
        codeSanBoxMap.put(type, codeExecuteSandBox);
    }

    public ICodeExecuteSandBox get(String type) {
        return codeSanBoxMap.get(type);
    }
}
