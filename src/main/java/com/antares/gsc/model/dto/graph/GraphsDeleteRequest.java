package com.antares.gsc.model.dto.graph;

import lombok.Data;

import java.util.List;

@Data
public class GraphsDeleteRequest {
    private Long did;
    private List<Long> gids;
}
