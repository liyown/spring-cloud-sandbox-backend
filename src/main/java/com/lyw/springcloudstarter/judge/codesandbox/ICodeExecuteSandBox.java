package com.lyw.springcloudstarter.judge.codesandbox;

import com.lyw.springcloudstarter.domain.dto.codesandbox.CodeRunResult;

import java.util.List;

/**
 * @author: liuyaowen
 * @poject: springboot-starter
 * @create: 2024-07-24 19:28
 * @Description:
 */
public interface ICodeExecuteSandBox {

    CodeRunResult<List<String>> execute(String code, List<String> input);

    TYPE getType();


    enum TYPE {
        JAVA_NATIVE("java_native"),
        JAVA_DOCKER("java_docker"),


        DELEGATE("delegate");
        private String name;

        TYPE(String typeName) {
            this.name = typeName;
        }
    }
}

