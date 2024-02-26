package com.antares.gsc.service;

import com.antares.gsc.model.dto.editPath.EditPathQueryRequest;
import com.antares.gsc.model.vo.retrieval.PathsVo;

public interface EditPathService {
    PathsVo getPaths(EditPathQueryRequest request);
}
