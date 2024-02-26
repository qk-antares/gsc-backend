package com.antares.gsc.model.vo.graph;

import cn.hutool.json.JSONUtil;
import com.antares.gsc.model.entity.Graph;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class GraphVo {
    private Long gid;
    private Long did;
    private String name;
    private GraphData graphData;
    private Date createTime;
    private Date updateTime;

    public static GraphVo objToVo(Graph graph) {
        if (graph == null) {
            return null;
        }
        GraphVo graphVo = new GraphVo();
        BeanUtils.copyProperties(graph, graphVo);
        graphVo.setGraphData(JSONUtil.toBean(graph.getGraphData(), GraphData.class));
        return graphVo;
    }
}
