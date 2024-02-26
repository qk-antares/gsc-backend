package com.antares.gsc.model.dto.editPath;

import com.antares.gsc.model.vo.graph.GraphPair;
import lombok.Data;

/**
 * 计算图编辑路径请求
 */
@Data
public class EditPathQueryRequest {
    private GraphPair graphPair;
    private String algorithm;
    private Integer k=100;
}
