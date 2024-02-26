package com.antares.gsc.model.vo.graph;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GraphPair {
    private GraphData graph1;
    private GraphData graph2;
}
