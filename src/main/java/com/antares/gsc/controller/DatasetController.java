package com.antares.gsc.controller;


import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.antares.gsc.common.response.R;
import com.antares.gsc.model.dto.dataset.DatasetAddRequest;
import com.antares.gsc.model.dto.dataset.DatasetDeleteRequest;
import com.antares.gsc.model.dto.dataset.DatasetQueryRequest;
import com.antares.gsc.model.dto.dataset.DatasetUpdateRequest;
import com.antares.gsc.model.entity.Dataset;
import com.antares.gsc.model.vo.dataset.DatasetVo;
import com.antares.gsc.service.DatasetService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@RequestMapping("/dataset")
public class DatasetController {
    @Resource
    private DatasetService datasetService;

    /**
     * 获取数据集的全部标签
     * @return
     */
    @GetMapping("/tags")
    public R<List<String>> getDatasetTags(){
        List<String> tags = datasetService.getDatasetTags();
        return R.ok(tags);
    }

    /**
     * 分页获取数据集信息（表格）
     * @param request
     * @return
     */
    @PostMapping("/page/vo")
    public R<Page<DatasetVo>> listDatasetVoByPage(@RequestBody DatasetQueryRequest request){
        Page<DatasetVo> page = datasetService.listDatasetVoByPage(request);
        return R.ok(page);
    }

    /**
     * 获取某个数据集的详细信息
     * @param did
     * @return
     */
    @GetMapping("/{did}")
    public R<DatasetVo> getDatasetById(@PathVariable("did") Long did){
        DatasetVo vo = datasetService.getDatasetById(did);
        return R.ok(vo);
    }

    /**
     * 创建一个数据集
     * @param request
     * @return
     */
    @PostMapping
    public R createDataset(@RequestBody DatasetAddRequest request){
        datasetService.createDataset(request);
        return R.ok();
    }

    /**
     * 更新某个数据集的基本信息
     * @param request
     * @return
     */
    @PutMapping
    public R updateDataset(@RequestBody DatasetUpdateRequest request){
        datasetService.updateById(Dataset.builder()
                .did(request.getDid())
                .name(request.getName())
                .tags(JSON.toJSONString(request.getTags()))
                .build());
        return R.ok();
    }

    /**
     * 删除某个数据集
     * @param request
     * @return
     */
    @DeleteMapping
    public R deleteDataset(@RequestBody DatasetDeleteRequest request){
        datasetService.deleteDataset(request);
        return R.ok();
    }

    @GetMapping("/list")
    public R<List<DatasetVo>> listAllDataset(){
        return R.ok(datasetService.list().stream()
                .map(DatasetVo::objToVo)
                .collect(Collectors.toList()));
    }

}
