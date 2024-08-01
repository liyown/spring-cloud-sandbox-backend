package com.lyw.springcloudstarter.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaseDockerClientProxy implements FactoryBean<DockerClient> {

    @Value("${docker.host}")
    private String serverUrl;

    @Value("${docker.registry.url}")
    private String registryUrl;

    @Value("${docker.registry.user}")
    private String registryUser;

    @Value("${docker.registry.pass}")
    private String registryPass;

    @Override
    @SuppressWarnings("deprecation")
    public DockerClient getObject() {
        return DockerClientBuilder.getInstance(serverUrl).withDockerCmdExecFactory(new NettyDockerCmdExecFactory()).build();

    }

    @Override
    public Class<?> getObjectType() {
        return DockerClient.class;
    }
}