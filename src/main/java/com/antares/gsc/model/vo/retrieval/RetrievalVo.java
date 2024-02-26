package com.antares.gsc.model.vo.retrieval;

import cn.hutool.json.JSONUtil;
import com.antares.gsc.common.enums.RetrievalStatusEnum;
import com.antares.gsc.mapper.DatasetMapper;
import com.antares.gsc.mapper.GraphMapper;
import com.antares.gsc.mapper.NodeLabelMapper;
import com.antares.gsc.mapper.RetrievalRecordMapper;
import com.antares.gsc.model.entity.Retrieval;
import com.antares.gsc.model.vo.dataset.DatasetVo;
import com.antares.gsc.model.vo.graph.GraphData;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

@Data
public class RetrievalVo {
    private Long rid;
    private String algorithm;
    private GraphData graphData;
    private DatasetVo dataset;
    private String status;
    private Date createTime;
    private Date finishTime;
    private Long timeCost;

    /**
     * 对象转包装类
     * @param retrieval
     * @return
     */
    public static RetrievalVo objToVo(Retrieval retrieval, DatasetMapper datasetMapper, RetrievalRecordMapper retrievalRecordMapper) {
        if (retrieval == null) {
            return null;
        }
        RetrievalVo retrievalVo = new RetrievalVo();
        BeanUtils.copyProperties(retrieval, retrievalVo);

        retrievalVo.setGraphData(JSONUtil.toBean(retrieval.getGraphData(), GraphData.class));
        retrievalVo.setDataset(DatasetVo.objToVo(datasetMapper.selectById(retrieval.getDid()), retrievalRecordMapper, retrieval.getRid()));
        retrievalVo.setStatus(RetrievalStatusEnum.getEnumByCode(retrieval.getStatus()).getMsg());
        if(retrieval.getFinishTime() != null){
            retrievalVo.setTimeCost(retrieval.getFinishTime().getTime() - retrieval.getCreateTime().getTime());
        }

        return retrievalVo;
    }

    public static RetrievalVo objToVo(Retrieval retrieval,
                                      DatasetMapper datasetMapper,
                                      NodeLabelMapper nodeLabelMapper,
                                      GraphMapper graphMapper,
                                      ThreadPoolExecutor threadPoolExecutor) {
        if (retrieval == null) {
            return null;
        }
        RetrievalVo retrievalVo = new RetrievalVo();
        BeanUtils.copyProperties(retrieval, retrievalVo);

        retrievalVo.setGraphData(JSONUtil.toBean(retrieval.getGraphData(), GraphData.class));
        retrievalVo.setDataset(DatasetVo.objToVo(
                datasetMapper.selectById(retrieval.getDid()),
                nodeLabelMapper, graphMapper, threadPoolExecutor));
        retrievalVo.setStatus(RetrievalStatusEnum.getEnumByCode(retrieval.getStatus()).getMsg());
        if(retrieval.getFinishTime() != null){
            retrievalVo.setTimeCost(retrieval.getFinishTime().getTime() - retrieval.getCreateTime().getTime());
        }

        return retrievalVo;
    }
}
