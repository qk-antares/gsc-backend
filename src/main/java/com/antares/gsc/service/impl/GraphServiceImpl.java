package com.antares.gsc.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.antares.gsc.common.constant.CommonConstant;
import com.antares.gsc.common.enums.HttpCodeEnum;
import com.antares.gsc.common.exception.BusinessException;
import com.antares.gsc.mapper.GraphMapper;
import com.antares.gsc.mapper.RetrievalRecordMapper;
import com.antares.gsc.model.dto.graph.EditorGraphAddRequest;
import com.antares.gsc.model.dto.graph.GraphAddRequest;
import com.antares.gsc.model.dto.graph.GraphQueryRequest;
import com.antares.gsc.model.dto.graph.GraphsDeleteRequest;
import com.antares.gsc.model.entity.Graph;
import com.antares.gsc.model.entity.NodeLabel;
import com.antares.gsc.model.entity.RetrievalRecord;
import com.antares.gsc.model.vo.graph.GraphData;
import com.antares.gsc.model.vo.graph.GraphVo;
import com.antares.gsc.service.GraphService;
import com.antares.gsc.service.NodeLabelService;
import com.antares.gsc.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
* @author Antares
* @description 针对表【graph(图数据)】的数据库操作Service实现
* @createDate 2023-10-30 10:55:47
*/
@Service
public class GraphServiceImpl extends ServiceImpl<GraphMapper, Graph>
    implements GraphService {
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private NodeLabelService nodeLabelService;
    @Resource
    private RetrievalRecordMapper retrievalRecordMapper;

    @Override
    public Long uploadGraph(MultipartFile file) {
        String data = null;
        try {
            data = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BusinessException(HttpCodeEnum.INTERNAL_SERVER_ERROR, "文件读取失败！");
        }

        GraphData graphData = JSONUtil.toBean(data, GraphData.class);

        // 将图文件的相关数据保存到数据库
        Graph graph = Graph.builder().name(FileUtil.mainName(file.getOriginalFilename()))
                .graphData(JSONUtil.toJsonStr(graphData))
                .nodeNum(graphData.getNodes().size())
                .edgeNum(graphData.getEdges().size())
                .build();

        save(graph);
        return graph.getGid();
    }

    @Override
    public Wrapper<Graph> getQueryWrapper(GraphQueryRequest request) {
        QueryWrapper<Graph> queryWrapper = new QueryWrapper<>();
        String sortField = request.getSortField();
        String sortOrder = request.getSortOrder();

        queryWrapper.eq("did", request.getDid())
                .orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);

        return queryWrapper;
    }

    @Override
    @Transactional
    public void addGraphsToDataset(GraphAddRequest request) {
        Long did = request.getDid();
        List<Long> gids = request.getGids();

        CompletableFuture<Void> graphUpdateFuture = CompletableFuture.runAsync(() -> {
            //将graph表中的did修改
            update(new LambdaUpdateWrapper<Graph>()
                    .set(Graph::getDid, did)
                    .in(Graph::getGid, gids));
        }, threadPoolExecutor);

        CompletableFuture<Void> nodeLabelsSaveFuture = CompletableFuture.runAsync(() -> {
            //插入nodeLabels
            List<NodeLabel> nodeLabels = listByIds(gids).stream()
                    .map(graph -> {
                        GraphVo graphVo = GraphVo.objToVo(graph);
                        return graphVo.getGraphData().getNodes().stream()
                                .map(node -> NodeLabel.builder().did(did).gid(graph.getGid()).nodeLabel(node.getLabel()).build())
                                .collect(Collectors.toList());
                    }).flatMap(List::stream).collect(Collectors.toList());
            nodeLabelService.saveBatch(nodeLabels);
        }, threadPoolExecutor);

        CompletableFuture.allOf(graphUpdateFuture, nodeLabelsSaveFuture).join();
    }

    @Override
    @Transactional
    public void deleteGraphFromDataset(GraphsDeleteRequest request) {
        Long did = request.getDid();
        List<Long> gids = request.getGids();

        CompletableFuture<Void> graphRemoveFuture = CompletableFuture.runAsync(() -> {
            //删除对应的graphs
            removeByIds(gids);
        }, threadPoolExecutor);

        CompletableFuture<Void> nodeLabelRemoveFuture = CompletableFuture.runAsync(() -> {
            //删除对应的nodeLabel
            nodeLabelService.remove(new LambdaQueryWrapper<NodeLabel>()
                    .eq(NodeLabel::getDid, did).in(NodeLabel::getGid, gids));
        }, threadPoolExecutor);

        //把该图相关的检索记录也全部删除
        CompletableFuture<Void> retrievalRecordRemoveFuture = CompletableFuture.runAsync(() -> {
            retrievalRecordMapper.delete(new LambdaQueryWrapper<RetrievalRecord>()
                    .in(RetrievalRecord::getGid, gids));
        }, threadPoolExecutor);

        CompletableFuture.allOf(graphRemoveFuture, nodeLabelRemoveFuture, retrievalRecordRemoveFuture).join();
    }

    @Override
    public void addEditorGraphToDataset(EditorGraphAddRequest request) {
        Long did = request.getDid();

        Graph graph = Graph.builder()
                .graphData(JSONUtil.toJsonStr(request.getGraphData()))
                .did(request.getDid())
                .name(request.getName())
                .build();

        CompletableFuture<Void> graphSaveFuture = CompletableFuture.runAsync(() -> {
            save(graph);
        }, threadPoolExecutor);

        CompletableFuture<Void> nodeLabelsSaveFuture = CompletableFuture.runAsync(() -> {
            //插入nodeLabels
            GraphVo graphVo = GraphVo.objToVo(graph);
            List<NodeLabel> nodeLabels = graphVo.getGraphData().getNodes().stream()
                    .map(node -> NodeLabel.builder().did(did).gid(graph.getGid()).nodeLabel(node.getLabel()).build())
                    .collect(Collectors.toList());
            nodeLabelService.saveBatch(nodeLabels);
        }, threadPoolExecutor);

        CompletableFuture.allOf(graphSaveFuture, nodeLabelsSaveFuture).join();
    }
}
