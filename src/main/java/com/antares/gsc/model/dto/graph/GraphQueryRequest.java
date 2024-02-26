package com.antares.gsc.model.dto.graph;

import com.antares.gsc.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class GraphQueryRequest extends PageRequest implements Serializable {
    private Long did;
}
