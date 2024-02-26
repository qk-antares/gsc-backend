package com.antares.gsc.common.enums;

import com.antares.gsc.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlgorithmEnum {
    ACCURATE("Accurate"),
    GEDGNN("GedGNN"),
    SIMGNN("SimGNN"),
    TAGSIM("TaGSim");

    public final String name;

    public static AlgorithmEnum getEnumByName(String name) {
        for (AlgorithmEnum algorithmEnum : AlgorithmEnum.values()) {
            if (algorithmEnum.name.equals(name)) {
                return algorithmEnum;
            }
        }
        throw new BusinessException(HttpCodeEnum.INTERNAL_SERVER_ERROR, "不存在的枚举");
    }
}
