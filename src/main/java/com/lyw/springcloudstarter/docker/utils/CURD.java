package com.lyw.springcloudstarter.docker.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author: liuyaowen
 * @poject: spring-cloud-sandbox
 * @create: 2024-07-30 14:47
 * @Description:
 */
@Component
public class CURD {

    @Resource
    private DockerClient dockerClient;


    public void testPullImage() {
        dockerClient.pingCmd().exec();
        String imageName = "nginx:latest";
        dockerClient.pullImageCmd(imageName).exec(new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                System.out.println("成功下载：" + item.getStatus());
            }
        });
    }

    public void testCreateContainer() {
        dockerClient.createContainerCmd("nginx:latest").exec();
    }

    public void testStartContainer() {
        dockerClient.startContainerCmd("nginx:latest").exec();
    }

    public void testStopContainer() {
        dockerClient.stopContainerCmd("nginx:latest").exec();
    }

    public void testRemoveContainer() {
        dockerClient.removeContainerCmd("nginx:latest").exec();
    }

    public void testListContainer() {
        dockerClient.listContainersCmd().withShowAll(true).exec();
    }

    public void testListImage() {
        dockerClient.listImagesCmd().exec();
    }

    public void testRemoveImage() {
        dockerClient.removeImageCmd("nginx:latest").exec();
    }

    public void testLogContainer() {
        LogContainerResultCallback logContainerResultCallback = new LogContainerResultCallback() {
            @Override
            public void onNext(Frame item) {
                System.out.println(item.toString());
            }
        };
        dockerClient.logContainerCmd("nginx:latest").exec(logContainerResultCallback);
    }

}
