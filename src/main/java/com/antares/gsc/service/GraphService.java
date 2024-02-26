package com.antares.gsc.service;

import com.antares.gsc.model.dto.graph.EditorGraphAddRequest;
import com.antares.gsc.model.dto.graph.GraphAddRequest;
import com.antares.gsc.model.dto.graph.GraphsDeleteRequest;
import com.antares.gsc.model.dto.graph.GraphQueryRequest;
import com.antares.gsc.model.entity.Graph;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
* @author Antares
* @description 针对表【graph(图数据)】的数据库操作Service
* @createDate 2023-10-30 10:55:47
*/
public interface GraphService extends IService<Graph> {

    Long uploadGraph(MultipartFile file);

    Wrapper<Graph> getQueryWrapper(GraphQueryRequest request);

    void addGraphsToDataset(GraphAddRequest request);

    void deleteGraphFromDataset(GraphsDeleteRequest request);

    void addEditorGraphToDataset(EditorGraphAddRequest request);
}
