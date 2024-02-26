package com.antares.gsc.model.vo.retrieval;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PathsVo {
    private Integer preGed;
    private Object editPath;
    private Double timeUse;
}
