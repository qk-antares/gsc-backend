package com.antares.gsc.model.dto.dataset;

import lombok.Data;

import java.util.List;

@Data
public class DatasetUpdateRequest {
    private Long did;
    private String name;
    private List<String> tags;
}
