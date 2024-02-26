package com.antares.gsc.common.response;

import com.antares.gsc.common.enums.HttpCodeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class R<T> implements Serializable {
	private static final long serialVersionUID = 6682215287252208284L;

	private Integer code;
	private String msg;
	private T data;

	public R(int code, String msg){
		this.code = code;
		this.msg = msg;
	}

	public static R ok() {
		return new R(HttpCodeEnum.SUCCESS.getCode(), HttpCodeEnum.SUCCESS.getMsg());
	}

	public static <T> R<T> ok(T data){
		R<T> r = new R<>(HttpCodeEnum.SUCCESS.getCode(), HttpCodeEnum.SUCCESS.getMsg());
		r.setData(data);
		return r;
	}

	public static R error() {
		return new R(HttpCodeEnum.INTERNAL_SERVER_ERROR.getCode(), HttpCodeEnum.INTERNAL_SERVER_ERROR.getMsg());
	}

	public static R error(HttpCodeEnum httpCodeEnum){
		return new R(httpCodeEnum.code, httpCodeEnum.msg);
	}

	public static R error(int code, String msg) {
		return new R(code, msg);
	}
}