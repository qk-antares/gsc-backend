package com.antares.gsc.model.dto.graph;

import com.antares.gsc.model.vo.graph.GraphData;
import lombok.Data;

@Data
public class EditorGraphAddRequest {
    private String name;
    private Long did;
    private GraphData graphData;
}
