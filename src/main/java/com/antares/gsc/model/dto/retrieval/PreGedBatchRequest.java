package com.antares.gsc.model.dto.retrieval;

import com.antares.gsc.model.vo.graph.GraphData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PreGedBatchRequest {
    private GraphData graph1;
    private List<GraphData> graph2s;
}
