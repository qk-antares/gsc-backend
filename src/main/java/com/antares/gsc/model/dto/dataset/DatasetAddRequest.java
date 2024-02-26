package com.antares.gsc.model.dto.dataset;

import lombok.Data;

import java.util.List;

@Data
public class DatasetAddRequest {
    private String name;
    private List<String> tags;
    private List<Long> gids;
}
