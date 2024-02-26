package com.antares.gsc.common.handler;

import com.antares.gsc.common.enums.HttpCodeEnum;
import com.antares.gsc.common.exception.BusinessException;
import com.antares.gsc.common.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public R systemExceptionHandler(BusinessException e) {
        //打印异常信息
        log.error("出现了异常！{}", e.toString());
        //从异常对象中获取信息，封装成ResponseResult后返回
        return R.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public R exceptionHandler(Exception e) {
        //打印异常信息
        log.error("出现了异常！{}", e.toString());
        //从异常对象中获取信息，封装成ResponseResult后返回
        return R.error(HttpCodeEnum.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
    }
}
