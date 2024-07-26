package com.lyw.springcloudstarter.judge.codesandbox;

import com.lyw.springcloudstarter.domain.dto.codesandbox.CodeExecuteRequest;
import com.lyw.springcloudstarter.domain.dto.codesandbox.CodeExecuteResponse;
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
}
