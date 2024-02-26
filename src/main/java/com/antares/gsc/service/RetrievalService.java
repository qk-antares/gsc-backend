package com.antares.gsc.service;

import com.antares.gsc.model.dto.retrieval.GraphPairQueryRequest;
import com.antares.gsc.model.dto.retrieval.RetrievalAddRequest;
import com.antares.gsc.model.dto.retrieval.RetrievalDetailQueryRequest;
import com.antares.gsc.model.dto.retrieval.RetrievalQueryRequest;
import com.antares.gsc.model.entity.Retrieval;
import com.antares.gsc.model.vo.graph.GraphPair;
import com.antares.gsc.model.vo.retrieval.RetrievalRecordVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Antares
* @description 针对表【retrieval(相似图检索历史)】的数据库操作Service
* @createDate 2023-11-03 13:09:59
*/
public interface RetrievalService extends IService<Retrieval> {

    void retrievalGraphInDataset(RetrievalAddRequest request);

    void retrieval(Retrieval retrieval);

    Wrapper<Retrieval> getQueryWrapper(RetrievalQueryRequest request);

    Page<RetrievalRecordVo> getRetrievalDetail(RetrievalDetailQueryRequest request);

    GraphPair getRetrievalGraphPair(GraphPairQueryRequest request);
}
