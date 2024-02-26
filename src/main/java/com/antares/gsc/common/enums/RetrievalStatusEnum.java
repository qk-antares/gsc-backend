package com.antares.gsc.common.enums;

import com.antares.gsc.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RetrievalStatusEnum {
    WAITING(0, "排队中"),
    COMPUTING(1, "计算中"),
    FINISH(2, "完成");

    public final int code;
    public final String msg;

    public static RetrievalStatusEnum getEnumByCode(int code) {
        for (RetrievalStatusEnum retrievalStatusEnum : RetrievalStatusEnum.values()) {
            if (retrievalStatusEnum.code == code) {
                return retrievalStatusEnum;
            }
        }
        throw new BusinessException(HttpCodeEnum.INTERNAL_SERVER_ERROR, "不存在的枚举");
    }
}
