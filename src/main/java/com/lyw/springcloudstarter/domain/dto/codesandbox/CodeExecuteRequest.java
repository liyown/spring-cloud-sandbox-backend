package com.lyw.springcloudstarter.domain.dto.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: liuyaowen
 * @poject: springboot-starter
 * @create: 2024-07-24 19:30
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeExecuteRequest {

    private List<String> input;

    private String code;
}