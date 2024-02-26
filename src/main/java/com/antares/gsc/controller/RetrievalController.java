package com.antares.gsc.controller;

import com.antares.gsc.common.response.R;
import com.antares.gsc.mapper.DatasetMapper;
import com.antares.gsc.mapper.GraphMapper;
import com.antares.gsc.mapper.NodeLabelMapper;
import com.antares.gsc.mapper.RetrievalRecordMapper;
import com.antares.gsc.model.dto.retrieval.GraphPairQueryRequest;
import com.antares.gsc.model.dto.retrieval.RetrievalAddRequest;
import com.antares.gsc.model.dto.retrieval.RetrievalDetailQueryRequest;
import com.antares.gsc.model.dto.retrieval.RetrievalQueryRequest;
import com.antares.gsc.model.entity.Retrieval;
import com.antares.gsc.model.vo.graph.GraphPair;
import com.antares.gsc.model.vo.retrieval.RetrievalRecordVo;
import com.antares.gsc.model.vo.retrieval.RetrievalVo;
import com.antares.gsc.service.RetrievalService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/retrieval")
public class RetrievalController {
    @Resource
    private RetrievalService retrievalService;
    @Resource
    private DatasetMapper datasetMapper;
    @Resource
    private GraphMapper graphMapper;
    @Resource
    private NodeLabelMapper nodeLabelMapper;
    @Resource
    private RetrievalRecordMapper retrievalRecordMapper;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 从某个数据集中检索某个图
     * @param request
     * @return
     */
    @PostMapping
    public R retrievalGraphInDataset(@RequestBody RetrievalAddRequest request){
        retrievalService.retrievalGraphInDataset(request);
        return R.ok();
    }

    /**
     * 获取某次检索的信息
     * @param rid
     * @return
     */
    @GetMapping("/{rid}")
    public R<RetrievalVo> getRetrievalById(@PathVariable("rid") Long rid){
        Retrieval retrieval = retrievalService.getById(rid);
        return R.ok(RetrievalVo.objToVo(retrieval, datasetMapper, nodeLabelMapper, graphMapper, threadPoolExecutor));
    }

    /**
     * 分页获取检索信息（表格）
     * @param request
     * @return
     */
    @PostMapping("/page/vo")
    public R<Page<RetrievalVo>> listRetrievalVoByPage(@RequestBody RetrievalQueryRequest request){
        long current = request.getCurrent();
        long pageSize = request.getPageSize();
        Page<Retrieval> retrievalPage = retrievalService.page(new Page<>(current, pageSize),
                retrievalService.getQueryWrapper(request));

        List<RetrievalVo> records = retrievalPage.getRecords().stream()
                .map(retrieval -> RetrievalVo.objToVo(retrieval, datasetMapper, retrievalRecordMapper)).collect(Collectors.toList());
        Page<RetrievalVo> page = new Page<>(current, pageSize, retrievalPage.getTotal());
        page.setRecords(records);
        return R.ok(page);
    }

    /**
     * 分页获取检索结果详细信息
     * @param request
     * @return
     */
    @PostMapping("/page/vo/detail")
    public R<Page<RetrievalRecordVo>> getRetrievalDetail(@RequestBody RetrievalDetailQueryRequest request){
        Page<RetrievalRecordVo> pageVo = retrievalService.getRetrievalDetail(request);
        return R.ok(pageVo);
    }

    @PostMapping("/pair")
    public R<GraphPair> getRetrievalGraphPair(@RequestBody GraphPairQueryRequest request){
        GraphPair graphPair = retrievalService.getRetrievalGraphPair(request);
        return R.ok(graphPair);
    }

}
