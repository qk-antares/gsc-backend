package com.antares.gsc.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.antares.gsc.common.constant.CommonConstant;
import com.antares.gsc.common.enums.AlgorithmEnum;
import com.antares.gsc.common.enums.HttpCodeEnum;
import com.antares.gsc.common.enums.RetrievalStatusEnum;
import com.antares.gsc.common.exception.BusinessException;
import com.antares.gsc.mapper.RetrievalMapper;
import com.antares.gsc.model.dto.retrieval.GraphPairQueryRequest;
import com.antares.gsc.model.dto.retrieval.PreGedBatchRequest;
import com.antares.gsc.model.dto.retrieval.RetrievalAddRequest;
import com.antares.gsc.model.dto.retrieval.RetrievalDetailQueryRequest;
import com.antares.gsc.model.dto.retrieval.RetrievalQueryRequest;
import com.antares.gsc.model.entity.Graph;
import com.antares.gsc.model.entity.Retrieval;
import com.antares.gsc.model.entity.RetrievalRecord;
import com.antares.gsc.model.vo.graph.GraphData;
import com.antares.gsc.model.vo.graph.GraphPair;
import com.antares.gsc.model.vo.retrieval.RetrievalRecordVo;
import com.antares.gsc.service.GraphService;
import com.antares.gsc.service.RetrievalRecordService;
import com.antares.gsc.service.RetrievalService;
import com.antares.gsc.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

/**
* @author Antares
* @description 针对表【retrieval(相似图检索历史)】的数据库操作Service实现
* @createDate 2023-11-03 13:09:59
*/
@Service
public class RetrievalServiceImpl extends ServiceImpl<RetrievalMapper, Retrieval>
    implements RetrievalService{
    @Value("${gsc.api-address.gedgnn.value-batch}")
    private String gedgnnValueBatchApiAddr;
    @Value("${gsc.api-address.simgnn.value-batch}")
    private String simgnnValueBatchApiAddr;
    @Value("${gsc.api-address.tagsim.value-batch}")
    private String tagsimValueBatchApiAddr;

    @Resource
    private GraphService graphService;
    @Resource
    private RetrievalRecordService retrievalRecordService;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void retrievalGraphInDataset(RetrievalAddRequest request) {
        Retrieval retrieval = Retrieval.builder()
                .did(request.getDid())
                .createTime(new Date())
                .algorithm(request.getAlgorithm())
                .graphData(JSONUtil.toJsonStr(request.getGraphData()))
                .status(RetrievalStatusEnum.WAITING.getCode()).build();
        save(retrieval);

        CompletableFuture.runAsync(() -> retrieval(retrieval), threadPoolExecutor);
    }

    @Override
    public void retrieval(Retrieval retrieval) {
        retrieval.setStatus(RetrievalStatusEnum.COMPUTING.getCode());
        updateById(retrieval);

        //查询该数据集所有的图
        List<Graph> graphs = graphService.lambdaQuery()
                .select(Graph::getGid, Graph::getGraphData)
                .eq(Graph::getDid, retrieval.getDid())
                .list();
        List<GraphData> graphDataList = graphs.stream().map(graph -> JSONUtil.toBean(graph.getGraphData(), GraphData.class))
                .collect(Collectors.toList());
        List<Long> gids = graphs.stream().map(Graph::getGid)
                .collect(Collectors.toList());

        GraphData graph1 = JSONUtil.toBean(retrieval.getGraphData(), GraphData.class);
        Long rid = retrieval.getRid();
        String algorithm = retrieval.getAlgorithm();
        int batchSize = 128;
        int numBatches = gids.size() / batchSize;
        int numLeft = gids.size() % batchSize;

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 循环处理每个批次
        for (int i = 0; i < numBatches; i++) {
            int start = i * batchSize;
            int end = (i + 1) * batchSize;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<GraphData> graph2s = graphDataList.subList(start, end);
                processBatch(gids, graph1, graph2s, algorithm, start, rid);
            });
            futures.add(future);
        }

        // 处理剩余的元素
        if (numLeft != 0) {
            int start = numBatches * batchSize;
            int end = gids.size();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<GraphData> graph2s = graphDataList.subList(start, end);
                processBatch(gids, graph1, graph2s, algorithm, start, rid);
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        retrieval.setStatus(RetrievalStatusEnum.FINISH.getCode());
        retrieval.setFinishTime(new Date());
        updateById(retrieval);
    }

    private void processBatch(List<Long> gids, GraphData graph1, List<GraphData> graph2s, String algorithm, int start, Long rid) {
        String requestBody = JSONUtil.toJsonStr(PreGedBatchRequest.builder().graph1(graph1).graph2s(graph2s).build());
        int batchSize = graph2s.size();

        try (HttpResponse response = HttpUtil.createPost(getApiAddrByAlgorithm(algorithm))
                .body(requestBody, "application/json")
                .execute()) {
            List<BigDecimal> preGeds = (List<BigDecimal>) JSON.parseObject(response.body()).get("pre_geds");
            List<RetrievalRecord> batch = new ArrayList<>();
            for (int j = 0; j < batchSize; j++) {
                RetrievalRecord record = RetrievalRecord.builder()
                        .rid(rid)
                        .gid(gids.get(start+j))
                        .preGed(preGeds.get(j).doubleValue()).build();
                batch.add(record);
            }
            retrievalRecordService.saveBatch(batch);
        }
    }

    private String getApiAddrByAlgorithm(String algorithm){
        AlgorithmEnum algorithmEnum = AlgorithmEnum.getEnumByName(algorithm);
        switch (algorithmEnum){
            case GEDGNN: return gedgnnValueBatchApiAddr;
            case SIMGNN: return simgnnValueBatchApiAddr;
            case TAGSIM: return tagsimValueBatchApiAddr;
            default:
                break;
        }
        throw new BusinessException(HttpCodeEnum.BAD_REQUEST, "不支持的算法");
    }

    @Override
    public Wrapper<Retrieval> getQueryWrapper(RetrievalQueryRequest request) {
        QueryWrapper<Retrieval> queryWrapper = new QueryWrapper<>();

        String sortField = request.getSortField();
        String sortOrder = request.getSortOrder();

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public Page<RetrievalRecordVo> getRetrievalDetail(RetrievalDetailQueryRequest request) {
        int current = request.getCurrent();
        int pageSize = request.getPageSize();

        Page<RetrievalRecord> retrievalRecordPage = retrievalRecordService.page(new Page<>(current, pageSize),
                new QueryWrapper<RetrievalRecord>()
                        .eq("rid", request.getId())
                        .orderBy(true, true, "pre_ged"));

        List<RetrievalRecord> rawRecord = retrievalRecordPage.getRecords();
        List<Long> gids = rawRecord.stream().map(RetrievalRecord::getGid).collect(Collectors.toList());
        Map<Long, Graph> graphMap = graphService.listByIds(gids).stream()
                .collect(Collectors.toMap(Graph::getGid, graph -> graph));

        List<RetrievalRecordVo> records = rawRecord.stream()
                .map(record -> RetrievalRecordVo.objToVo(record, graphMap.get(record.getGid())))
                .collect(Collectors.toList());

        Page<RetrievalRecordVo> page = new Page<>(current, pageSize, retrievalRecordPage.getTotal());
        page.setRecords(records);
        return page;
    }

    @Override
    public GraphPair getRetrievalGraphPair(GraphPairQueryRequest request) {
        Long rid = request.getRid();
        Long gid = request.getGid();

        //分别获取两个图的数据
        CompletableFuture<GraphData> graph1Future = CompletableFuture.supplyAsync(() ->
                JSONUtil.toBean(getById(rid).getGraphData(), GraphData.class));

        CompletableFuture<GraphData> graph2Future = CompletableFuture.supplyAsync(() ->
                JSONUtil.toBean(graphService.getById(gid).getGraphData(), GraphData.class));

        CompletableFuture.allOf(graph1Future, graph2Future).join();

        try {
            return GraphPair.builder().graph1(graph1Future.get()).graph2(graph2Future.get()).build();
        } catch (Exception e) {
            throw new BusinessException(HttpCodeEnum.INTERNAL_SERVER_ERROR, "查询数据库异常");
        }
    }
}




