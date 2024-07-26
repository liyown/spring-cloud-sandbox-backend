package com.lyw.springcloudstarter.exception;



import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.lyw.springcloudstarter.common.BaseResponse;
import com.lyw.springcloudstarter.common.ErrorCode;
import com.lyw.springcloudstarter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.Flow;

/**
 * 全局异常处理器
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(FlowException.class)
    public BaseResponse<?> flowExceptionHandler(FlowException e) {
        log.error("FlowException", e);
        return ResultUtils.error(ErrorCode.FLOW_ERROR, "限流");
    }


    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
