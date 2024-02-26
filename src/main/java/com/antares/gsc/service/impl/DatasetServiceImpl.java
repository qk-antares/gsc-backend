package com.antares.gsc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.antares.gsc.common.constant.CommonConstant;
import com.antares.gsc.common.enums.HttpCodeEnum;
import com.antares.gsc.common.exception.BusinessException;
import com.antares.gsc.mapper.DatasetMapper;
import com.antares.gsc.mapper.GraphMapper;
import com.antares.gsc.mapper.NodeLabelMapper;
import com.antares.gsc.model.dto.dataset.DatasetAddRequest;
import com.antares.gsc.model.dto.dataset.DatasetDeleteRequest;
import com.antares.gsc.model.dto.dataset.DatasetQueryRequest;
import com.antares.gsc.model.dto.graph.GraphAddRequest;
import com.antares.gsc.model.entity.Dataset;
import com.antares.gsc.model.entity.Graph;
import com.antares.gsc.model.vo.dataset.DatasetVo;
import com.antares.gsc.service.DatasetService;
import com.antares.gsc.service.GraphService;
import com.antares.gsc.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
* @author Antares
* @description 针对表【dataset】的数据库操作Service实现
* @createDate 2023-10-30 12:40:04
*/
@Service
public class DatasetServiceImpl extends ServiceImpl<DatasetMapper, Dataset>
    implements DatasetService{

    @Resource
    private NodeLabelMapper nodeLabelMapper;
    @Resource
    private GraphMapper graphMapper;
    @Resource
    private GraphService graphService;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void createDataset(DatasetAddRequest request) {
        Dataset dataset = Dataset.builder()
                .name(request.getName())
                .tags(JSON.toJSONString(request.getTags()))
                .build();
        save(dataset);

        GraphAddRequest addRequest = new GraphAddRequest();
        addRequest.setDid(dataset.getDid());
        addRequest.setGids(request.getGids());

        if(!request.getGids().isEmpty()){
            graphService.addGraphsToDataset(addRequest);
        }
    }

    public Wrapper<Dataset> getQueryWrapper(DatasetQueryRequest request) {
        QueryWrapper<Dataset> queryWrapper = new QueryWrapper<>();

        String sortField = request.getSortField();
        String sortOrder = request.getSortOrder();
        String keyword = request.getKeyword();
        List<String> tags = request.getTags();

        // 关键词查询
        boolean likeQuery = StrUtil.isNotBlank(keyword);
        queryWrapper.like(likeQuery, "name", keyword);

        //标签查询
        if (CollectionUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public List<String> getDatasetTags() {
        return lambdaQuery().select(Dataset::getTags).list().stream()
                .flatMap(dataset -> JSONUtil.toList(dataset.getTags(), String.class).stream())
                .distinct().collect(Collectors.toList());
    }

    @Override
    public Page<DatasetVo> listDatasetVoByPage(DatasetQueryRequest request) {
        long current = request.getCurrent();
        long pageSize = request.getPageSize();
        Page<Dataset> datasetPage = page(new Page<>(current, pageSize),
                getQueryWrapper(request));

        List<DatasetVo> records = datasetPage.getRecords().stream()
                .map(dataset -> DatasetVo.objToVo(dataset, nodeLabelMapper, graphMapper, threadPoolExecutor))
                .collect(Collectors.toList());
        Page<DatasetVo> page = new Page<>(current, pageSize, datasetPage.getTotal());
        page.setRecords(records);

        return page;
    }

    @Override
    public DatasetVo getDatasetById(Long did) {
        Dataset dataset = getById(did);
        if(dataset == null){
            throw new BusinessException(HttpCodeEnum.NOT_FOUND, "数据集不存在");
        }
        return DatasetVo.objToVo(dataset, nodeLabelMapper, graphMapper, threadPoolExecutor);
    }

    @Override
    public void deleteDataset(DatasetDeleteRequest request) {
        CompletableFuture<Void> removeDatasetFuture = CompletableFuture.runAsync(
                () -> removeById(request.getDid()), threadPoolExecutor);

        CompletableFuture<Void> removeGraphFuture = CompletableFuture.runAsync(
                () -> graphMapper.delete(new LambdaQueryWrapper<Graph>()
                        .eq(Graph::getDid, request.getDid())), threadPoolExecutor);

        CompletableFuture.allOf(removeDatasetFuture, removeGraphFuture).join();
    }
}




