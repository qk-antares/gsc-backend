package com.antares.gsc.model.dto.retrieval;

import com.antares.gsc.model.vo.graph.GraphData;
import lombok.Data;

@Data
public class RetrievalAddRequest {
    private GraphData graphData;
    private Long did;
    private String algorithm;
}
