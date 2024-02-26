package com.antares.gsc.common.enums;

import lombok.Getter;

@Getter
public enum HttpCodeEnum {
    // 成功
    SUCCESS(200,"操作成功"),
    //服务器内部异常
    INTERNAL_SERVER_ERROR(505, "未知的服务器内部异常"),
    //请求参数不合法
    BAD_REQUEST(400, "请求参数不合法"),
    //无权限
    NO_AUTH(403, "没有执行操作的权限"),
    //请求的资源不存在
    NOT_FOUND(404, "请求的资源不存在");

    public final int code;
    public final String msg;

    HttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

    public static HttpCodeEnum getEnumByCode(int code) {
        for (HttpCodeEnum httpCodeEnum : HttpCodeEnum.values()) {
            if (httpCodeEnum.code == code) {
                return httpCodeEnum;
            }
        }
        return HttpCodeEnum.INTERNAL_SERVER_ERROR;
    }
}
