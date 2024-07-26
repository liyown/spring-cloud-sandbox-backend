package com.lyw.springcloudstarter.domain.dto.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeRunResult<T> {

    private String message;
    /**
     * 0: success return data which is the result of the code
     * 1: compile error return message which is the error message
     * 2: runtime error return message which is the error message
     * 3: memory limit error return message which is the error message
     * 4: stack limit error return message which is the error message
     * 5: time limit error return message which is the error message
     */
    private int status;

    private Long timeCost;
    private Long memoryCost;

    /**
     * only return data when status is 0 else return null
     */
    private T data;

    public enum Status {

        SUCCESS,
        FORBIDDEN,
        COMPILE_ERROR,
        RUNTIME_ERROR,
        MEMORY_LIMIT_ERROR,
        STACK_LIMIT_ERROR,
        TIME_LIMIT_ERROR,
    }
}