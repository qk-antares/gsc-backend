package com.antares.gsc.model.vo.retrieval;

import com.antares.gsc.model.entity.Graph;
import com.antares.gsc.model.entity.RetrievalRecord;
import com.antares.gsc.model.vo.graph.GraphVo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RetrievalRecordVo {
    private GraphVo graph;
    private Double preGed;

    public static RetrievalRecordVo objToVo(RetrievalRecord record, Graph graph) {
        return RetrievalRecordVo.builder()
                .graph(GraphVo.objToVo(graph))
                .preGed(record.getPreGed())
                .build();
    }
}
