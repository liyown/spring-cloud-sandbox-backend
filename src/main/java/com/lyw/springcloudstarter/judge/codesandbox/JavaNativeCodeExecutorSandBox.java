package com.lyw.springcloudstarter.judge.codesandbox;

import cn.hutool.dfa.WordTree;
import com.lyw.springcloudstarter.domain.dto.codesandbox.CodeRunResult;
import com.lyw.springcloudstarter.judge.ProcessWrapper;

import java.util.List;

/**
 * @author: liuyaowen
 * @poject: spring-cloud-sandbox
 * @create: 2024-07-25 21:53
 * @Description:
 */
public class JavaNativeCodeExecutorSandBox implements ICodeExecuteSandBox {

    private final List<String> blackJavaClass = List.of("File", "ProcessBuilder", "Runtime", "Thread", "ThreadGroup", "ClassLoader", "SecurityManager", "SecurityManager");

    public static void main(String[] args) {
        JavaNativeCodeExecutorSandBox javaNativeCodeExecutorSandBox = new JavaNativeCodeExecutorSandBox();
        CodeRunResult<List<String>> result = javaNativeCodeExecutorSandBox.execute("""
                                                                                       public class Main {
                                                                                       public static void main(String[] args) {
                                                                                           System.out.println("Hello World!");
                                                                                       }
                                                                                   }
                                                                                   """, List.of());
                                                                                   System.out.println(result);
    }

    @Override
    public CodeRunResult<List<String>> execute(String code, List<String> input) {
        // 1. 检查代码是否包含黑名单类
        WordTree tree = new WordTree();
        tree.addWords(blackJavaClass);
        if (tree.isMatch(code)) {
            return CodeRunResult.<List<String>>builder().status(CodeRunResult.Status.FORBIDDEN.ordinal()).build();
        }
        // 2. 执行代码
        ProcessWrapper processWrapper = new ProcessWrapper();
        return processWrapper.runCode(code, input);
    }
}
