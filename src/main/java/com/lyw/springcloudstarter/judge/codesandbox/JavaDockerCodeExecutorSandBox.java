package com.lyw.springcloudstarter.judge.codesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import com.lyw.springcloudstarter.common.ErrorCode;
import com.lyw.springcloudstarter.constant.QuestionSubmitConstant;
import com.lyw.springcloudstarter.domain.dto.codesandbox.CodeRunResult;
import com.lyw.springcloudstarter.exception.BusinessException;
import com.lyw.springcloudstarter.utils.JavaCompilerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: liuyaowen
 * @poject: spring-cloud-sandbox
 * @create: 2024-07-25 21:53
 * @Description:
 */
@Slf4j
public class JavaDockerCodeExecutorSandBox implements ICodeExecuteSandBox {

    private final Long MEMORY_LIMIT = 100 * 1024 * 1024L;
    private final Long TIME_LIMIT = 5000L;



    private static final String JAVA_SDK_IMAGE = "openjdk:17-alpine";

    private static final String JAVA_SDK_CONTAINER = "java_sdk_container";
    private final DockerClient dockerClient;
    private final List<String> blackJavaClass = List.of("File", "ProcessBuilder", "Runtime", "Thread", "ThreadGroup", "ClassLoader", "SecurityManager", "SecurityManager");
    private Boolean initComplete = false;

    {
        this.dockerClient = DockerClientBuilder.getInstance("unix:///var/run/docker.sock").withDockerCmdExecFactory(new NettyDockerCmdExecFactory()).build();
        init();
    }

    @Override
    @SuppressWarnings("deprecation")
    public CodeRunResult<List<String>> execute(String code, List<String> input) {
        // 检查条件
        CodeRunResult<List<String>> runResult = new CodeRunResult<>();
        runResult.setTimeCost(0L);
        runResult.setMemoryCost(0L);
        if (input == null || input.isEmpty()) {
            runResult.setStatus(CodeRunResult.Status.RUNTIME_ERROR.ordinal());
            runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_SYSTEM_ERROR + " " + "input is empty");
            return runResult;
        }
        // 编译代码
        JavaCompilerUtils.CompilerResult<String> compilerResult = JavaCompilerUtils.compiler(code);
        log.info("compiler result: {}", compilerResult.getData());

        if (compilerResult.isSuccess()) {
            // 编译成功
            StopWatch stopWatch = new StopWatch();
            // 创建容器
            String container = createContainer(compilerResult.getCompilerPath());
            List<String> output = new ArrayList<>();

            // 监控内存
            dockerClient.statsCmd(container).exec(new ResultCallbackTemplate<>() {
                @Override
                public void onNext(Statistics object) {
                    Long usage = object.getMemoryStats().getUsage();
                    runResult.setMemoryCost(Math.max(usage, runResult.getMemoryCost()));
                }
            });
            final Boolean[] errorFlag = {false};
            Boolean isNotTimeOut = true;

            for (String inputArgs : input) {


                String[] inputArgsArray = inputArgs.split(" ");
                String[] cmd = new String[]{"java", "-cp", "/usr/src/myapp", "Main"};
                String[] append = ArrayUtil.append(cmd, inputArgsArray);
                ExecCreateCmdResponse execCreateCmdResponse = dockerClient
                        .execCreateCmd(container)
                        .withAttachStdout(true)
                        .withAttachStderr(true)
                        .withAttachStdin(true)
                        .withCmd(append)
                        .exec();

                StringBuilder stringBuilder = new StringBuilder();

                // 执行命令
                try {

                    ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                        @Override
                        public void onNext(Frame item) {

                            if (StreamType.STDOUT.equals(item.getStreamType())) {
                                log.debug("stdout: {}", new String(item.getPayload()));
                                // 收集输出
                                output.add(new String(item.getPayload()).substring(0, new String(item.getPayload()).length() - 1));
                            } else {
                                log.debug("stderr: {}", new String(item.getPayload()));
                                // 运行时错误
                                runResult.setStatus(CodeRunResult.Status.RUNTIME_ERROR.ordinal());
                                stringBuilder.append(new String(item.getPayload()));
                                errorFlag[0] = true;
                            }
                        }
                    };
                    stopWatch.start();
                    isNotTimeOut = dockerClient
                            .execStartCmd(execCreateCmdResponse.getId())
                            .exec(execStartResultCallback)
                            .awaitCompletion(TIME_LIMIT, TimeUnit.MILLISECONDS);
                    stopWatch.stop();
                } catch (Exception e) {
                    log.error("exec command error", e);
                    FileUtil.del(compilerResult.getCompilerPath());
                    dockerClient.removeContainerCmd(container).withForce(true).exec();
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "exec command error");
                }
                if (!isNotTimeOut) {
                    break;
                }
                // 如果某个用例运行时错误直接返回
                if (errorFlag[0]) {
                    String s = stringBuilder.toString();
                    if (s.contains("OutOfMemoryError")) {
                        // 内存限制
                        runResult.setStatus(CodeRunResult.Status.MEMORY_LIMIT_ERROR.ordinal());
                        runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_MEMORY_LIMIT_EXCEEDED);
                    } else if (s.contains("StackOverflowError")) {
                        // 栈限制
                        runResult.setStatus(CodeRunResult.Status.STACK_LIMIT_ERROR.ordinal());
                        runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_STACK_LIMIT_EXCEEDED);
                    } else if (s.contains("SecurityException")) {
                        // 安全限制
                        runResult.setStatus(CodeRunResult.Status.FORBIDDEN.ordinal());
                        runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_FORBIDDEN_OPERATION + " " + s);
                    } else {
                        // 运行错误
                        runResult.setStatus(CodeRunResult.Status.RUNTIME_ERROR.ordinal());
                        runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_RUNTIME_ERROR + " " + s);
                    }
                    break;
                }
            }
            if (!isNotTimeOut) {
                runResult.setStatus(CodeRunResult.Status.TIME_LIMIT_ERROR.ordinal());
                runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_TIME_LIMIT_EXCEEDED);
                runResult.setMemoryCost(0L);
            } else if (errorFlag[0]) {
                runResult.setMemoryCost(0L);
            } else {
                runResult.setData(output);
                runResult.setTimeCost(stopWatch.getTotalTimeMillis());
                runResult.setStatus(CodeRunResult.Status.SUCCESS.ordinal());
                runResult.setMessage(QuestionSubmitConstant.SUBMIT_STATUS_SUCCESS);
                runResult.setMemoryCost(runResult.getMemoryCost() / 1024);
            }
            dockerClient.removeContainerCmd(container).withForce(true).exec();
        } else {
            // 编译错误
            runResult.setStatus(CodeRunResult.Status.COMPILE_ERROR.ordinal());
            runResult.setMessage(QuestionSubmitConstant.JUDGE_RESULT_COMPILE_ERROR + " " + compilerResult.getCompileMessage());
        }
        // 删除文件和容器
        FileUtil.del(compilerResult.getCompilerPath());
        return runResult;
    }

    @Override
    public TYPE getType() {

        return TYPE.JAVA_DOCKER;
    }


    private void checkJavaImage() {
        // 检查是否存在java sdk image
        // 不存在则拉取
        List<Image> exec = dockerClient.listImagesCmd().exec();
        log.info("----------------images: {}----------------", exec.size());
        for (Image image : exec) {
            log.info("image: {} tags: {}", image.getId(), image.getRepoTags());
        }
        boolean isExist = exec.stream().anyMatch(image -> image.getRepoTags() != null && image.getRepoTags().length !=0 && image.getRepoTags()[0].equals(JAVA_SDK_IMAGE));
        if (!isExist) {
            log.info("pull image: {}", JAVA_SDK_IMAGE);
            try {
                dockerClient.pullImageCmd(JAVA_SDK_IMAGE).exec(new PullImageResultCallback() {
                    @Override
                    public void onNext(PullResponseItem item) {
                        log.info("pull image: {} status: {}", JAVA_SDK_IMAGE, item.getStatus());
                        super.onNext(item);
                    }
                }).awaitCompletion();
                log.info("pull image: {} success", JAVA_SDK_IMAGE);
            } catch (InterruptedException e) {
                log.error("pull image: {} failed", JAVA_SDK_IMAGE);
                throw new RuntimeException("pull image " + JAVA_SDK_IMAGE + " failed");
            }
        }
        log.info("check image: {} success", JAVA_SDK_IMAGE);
    }

    private String createContainer(String javaClassPath) {
        // 创建java sdk 容器
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(MEMORY_LIMIT);
        hostConfig.withCpuCount(1L);
        hostConfig.setBinds(new Bind(javaClassPath, new Volume("/usr/src/myapp")));
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(JAVA_SDK_IMAGE);
        CreateContainerResponse createContainerResponse = containerCmd.withHostConfig(hostConfig).withAttachStderr(true).withAttachStdin(true).withAttachStdout(true).withTty(true).withCmd("/bin/sh").exec();
        // 启动tty
        dockerClient.startContainerCmd(createContainerResponse.getId()).exec();
        log.info("create container: {} with image: {} success, id: {}", JAVA_SDK_CONTAINER, JAVA_SDK_IMAGE, createContainerResponse.getId());
        return createContainerResponse.getId();
    }


    public void init() {
        if (initComplete) {
            return;
        }
        synchronized (this) {
            if (!initComplete) {
                checkJavaImage();
                initComplete = true;
            }
        }
    }
}
