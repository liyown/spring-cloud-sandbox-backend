package com.lyw.springcloudstarter.judge.codesandbox;

import cn.hutool.core.io.FileUtil;
import com.lyw.springcloudstarter.constant.QuestionSubmitConstant;
import com.lyw.springcloudstarter.domain.dto.codesandbox.CodeRunResult;
import com.lyw.springcloudstarter.utils.JavaCompilerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: liuyaowen
 * @poject: spring-cloud-sandbox
 * @create: 2024-07-26 10:36
 * @Description:
 */
@Slf4j
public class ProcessWrapper {

    private final StopWatch stopWatch = new StopWatch();



    // JVM限制 最大内存和栈大小
    String jvmLimitCommand = "-Xmx24m -Xss256k";

    public CodeRunResult<List<String>> runCode(String code, List<String> input) {

        CodeRunResult<List<String>> runResult = new CodeRunResult<>();
        if (input == null || input.isEmpty()) {
            runResult.setStatus(CodeRunResult.Status.RUNTIME_ERROR.ordinal());
            runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_SYSTEM_ERROR + " " + "input is empty");
            return runResult;
        }

        JavaCompilerUtils.CompilerResult<String> compilerResult = JavaCompilerUtils.compiler(code);
        log.info("compiler result: {}", compilerResult.getData());

        if (compilerResult.isSuccess()) {
            // 编译成功
            List<String> output = new ArrayList<>();
            CodeRunResult<String> runInteractResult;
            for (String s : input) {
                runInteractResult = InteractRunCode(compilerResult.getData(), s);
                if (runInteractResult.getStatus() != CodeRunResult.Status.SUCCESS.ordinal()) {
                    runResult.setStatus(runInteractResult.getStatus());
                    runResult.setMessage(runInteractResult.getMessage());
                    return runResult;
                } else {
                    output.add(runInteractResult.getData());
                }
            }
            runResult.setData(output);
            runResult.setTimeCost(stopWatch.getTotalTimeMillis());
            // todo 统计内存
            runResult.setMemoryCost(0L);
            runResult.setStatus(CodeRunResult.Status.SUCCESS.ordinal());
            runResult.setMessage(QuestionSubmitConstant.SUBMIT_STATUS_SUCCESS);
        } else {
            // 编译错误
            runResult.setStatus(CodeRunResult.Status.COMPILE_ERROR.ordinal());
            runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_COMPILE_ERROR + " " + compilerResult.getCompileMessage());
        }
        // 删除文件
        FileUtil.del(compilerResult.getCompilerPath());
        return runResult;
    }

    String readStream(InputStream inputStream) {
        byte[] bytes = new byte[1024];
        int len;
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream is = inputStream) {
            while ((len = is.read(bytes)) != -1) {
                stringBuilder.append(new String(bytes, 0, len));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 清除最后一个换行符和空格
        if (!stringBuilder.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();


    }

    private CodeRunResult<String> InteractRunCode(String command, String input) {
        CodeRunResult<String> runResult = new CodeRunResult<>();
        try {
            String runCommand = command + " " + input + " " + jvmLimitCommand;
            log.info("run command: {}", runCommand);
            stopWatch.start();

            Process process = Runtime.getRuntime().exec(runCommand);
            InputStream errorStream = process.getErrorStream();
            InputStream inputStream = process.getInputStream();
            new Thread(() -> {
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (process.isAlive()) {
                    // !!!超时

                    process.destroy();
                    runResult.setStatus(CodeRunResult.Status.TIME_LIMIT_ERROR.ordinal());
                    log.info("time limit and destroy process");
                }

            }).start();
            int i = process.waitFor();
            stopWatch.stop();
            if (runResult.getStatus() == CodeRunResult.Status.TIME_LIMIT_ERROR.ordinal()) {
                // 超时
                runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_TIME_LIMIT_EXCEEDED);
                return runResult;
            }
            if (i != 0) {
                String s = readStream(errorStream);
                if (s.contains("java.lang.OutOfMemoryError")) {
                    // 内存限制
                    runResult.setStatus(CodeRunResult.Status.MEMORY_LIMIT_ERROR.ordinal());
                    runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_MEMORY_LIMIT_EXCEEDED);
                } else if (s.contains("java.lang.StackOverflowError")) {
                    // 栈限制
                    runResult.setStatus(CodeRunResult.Status.STACK_LIMIT_ERROR.ordinal());
                    runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_MEMORY_LIMIT_EXCEEDED);
                } else if (s.contains("java.lang.SecurityException")) {
                    // 安全限制
                    runResult.setStatus(CodeRunResult.Status.FORBIDDEN.ordinal());
                    runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_FORBIDDEN_OPERATION + " " + s);
                } else {
                    // 运行错误
                    runResult.setStatus(CodeRunResult.Status.RUNTIME_ERROR.ordinal());
                    runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_RUNTIME_ERROR + " " + s);
                }
            } else {
                // 运行成功
                runResult.setStatus(CodeRunResult.Status.SUCCESS.ordinal());
                runResult.setData(readStream(inputStream));
            }

        } catch (Exception e) {
            // 系统错误
            runResult.setStatus(CodeRunResult.Status.RUNTIME_ERROR.ordinal());
            runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_SYSTEM_ERROR + " " + e.getMessage());
        }
        return runResult;
    }


}
