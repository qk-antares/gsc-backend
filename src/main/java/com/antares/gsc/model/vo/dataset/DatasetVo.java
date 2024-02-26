package com.antares.gsc.model.vo.dataset;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.antares.gsc.mapper.GraphMapper;
import com.antares.gsc.mapper.NodeLabelMapper;
import com.antares.gsc.mapper.RetrievalRecordMapper;
import com.antares.gsc.model.entity.Dataset;
import com.antares.gsc.model.entity.Graph;
import com.antares.gsc.model.entity.RetrievalRecord;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.Data;

@Data
public class DatasetVo {
    private Long did;
    private String name;
    private List<String> tags;

    private Integer count;
    private Integer maxNodeNum;
    private Integer maxEdgeNum;

    private List<String> labels;

    private Date createTime;
    private Date updateTime;

    /**
     * 对象转包装类
     * @param dataset
     * @return
     */
    public static DatasetVo objToVo(Dataset dataset, NodeLabelMapper nodeLabelMapper, GraphMapper graphMapper, ThreadPoolExecutor threadPoolExecutor) {
        if (dataset == null) {
            return null;
        }
        DatasetVo datasetVo = new DatasetVo();
        BeanUtils.copyProperties(dataset, datasetVo);
        datasetVo.setTags(JSON.parseObject(dataset.getTags(), new TypeReference<List<String>>(){}));

        Long did = dataset.getDid();

        CompletableFuture<Void> countFuture = CompletableFuture.runAsync(() -> {
            Long count = graphMapper.selectCount(new LambdaQueryWrapper<Graph>().eq(Graph::getDid, did));
            datasetVo.setCount(count.intValue());
        }, threadPoolExecutor);

        CompletableFuture<Void> labelsFuture = CompletableFuture.runAsync(() -> {
            List<String> labels = nodeLabelMapper.selectDistinctLabelsByDid(datasetVo.getDid());
            datasetVo.setLabels(labels);
        }, threadPoolExecutor);

        CompletableFuture<Void> edgeNumFuture = CompletableFuture.runAsync(
                () -> datasetVo.setMaxEdgeNum(graphMapper.selectMaxEdgeNumByDid(dataset.getDid())),
                threadPoolExecutor);

        CompletableFuture<Void> nodeNumFuture = CompletableFuture.runAsync(
                () -> datasetVo.setMaxNodeNum(graphMapper.selectMaxNodeNumByDid(dataset.getDid())),
                threadPoolExecutor);

        CompletableFuture.allOf(countFuture,labelsFuture,edgeNumFuture,nodeNumFuture).join();

        return datasetVo;
    }

    public static DatasetVo objToVo(Dataset dataset) {
        if (dataset == null) {
            return null;
        }
        DatasetVo datasetVo = new DatasetVo();
        BeanUtils.copyProperties(dataset, datasetVo);
        datasetVo.setTags(JSON.parseObject(dataset.getTags(), new TypeReference<List<String>>(){}));
        return datasetVo;
    }

    public static DatasetVo objToVo(Dataset dataset, RetrievalRecordMapper retrievalRecordMapper, Long rid) {
        if (dataset == null) {
            return null;
        }
        DatasetVo datasetVo = new DatasetVo();
        BeanUtils.copyProperties(dataset, datasetVo);
        datasetVo.setTags(JSON.parseObject(dataset.getTags(), new TypeReference<List<String>>(){}));

        Long count = retrievalRecordMapper.selectCount(new LambdaQueryWrapper<RetrievalRecord>().eq(RetrievalRecord::getRid, rid));
        datasetVo.setCount(count.intValue());

        return datasetVo;
    }
}
