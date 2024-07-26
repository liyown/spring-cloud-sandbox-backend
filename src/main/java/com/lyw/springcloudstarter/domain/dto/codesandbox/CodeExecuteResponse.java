package com.lyw.springcloudstarter.domain.dto.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: liuyaowen
 * @poject: springboot-starter
 * @create: 2024-07-24 19:31
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeExecuteResponse {

    public static String ERROR = "ERROR: ";

    private List<String> output;
    /**
     * 表示代码沙箱执行过程中是否出现错误，如果出现错误，error 字段会返回错误信息
     */
    private String error;

    /**
     * 代码执行的详细信息
     */
    private CodeRunResult<List<String>> codeRunResult;
}
