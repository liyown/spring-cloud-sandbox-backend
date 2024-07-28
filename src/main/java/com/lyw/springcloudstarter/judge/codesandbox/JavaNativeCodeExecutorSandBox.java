package com.lyw.springcloudstarter.judge.codesandbox;

import cn.hutool.core.io.FileUtil;
import com.lyw.springcloudstarter.domain.dto.codesandbox.CodeRunResult;

import java.nio.charset.Charset;
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
        String s = FileUtil.readString("D:\\github\\java\\spring-cloud-sandbox\\src\\main\\java\\com\\lyw\\springcloudstarter\\judge\\test\\Main.java", Charset.defaultCharset());
        CodeRunResult<List<String>> result = javaNativeCodeExecutorSandBox.execute(s, List.of("1"));
        System.out.println(result);
    }

    @Override
    public CodeRunResult<List<String>> execute(String code, List<String> input) {
        // 1. 检查代码是否包含黑名单类
//        WordTree tree = new WordTree();
//        tree.addWords(blackJavaClass);
//        if (tree.isMatch(code)) {
//            return CodeRunResult.<List<String>>builder().status(CodeRunResult.Status.FORBIDDEN.ordinal()).message(JUDGE_RESULT_FORBIDDEN_OPERATION).build();
//        }
        // 2. 执行代码
        ProcessWrapper processWrapper = new ProcessWrapper();
        return processWrapper.runCode(code, input);
    }
}
