package com.antares.gsc.common.exception;


import com.antares.gsc.common.enums.HttpCodeEnum;
import lombok.Getter;

/**
 * @author Antares
 */
@Getter
public class BusinessException extends RuntimeException{
    private final int code;

    private final String msg;

    public BusinessException(HttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }

    public BusinessException(HttpCodeEnum httpCodeEnum, String msg) {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg() + "：" + msg;
    }
}