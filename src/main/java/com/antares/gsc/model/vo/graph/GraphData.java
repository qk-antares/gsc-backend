package com.antares.gsc.model.vo.graph;

import lombok.Data;

import java.util.List;

@Data
public class GraphData {
    private List<Edge> edges;
    private List<Node> nodes;
}
