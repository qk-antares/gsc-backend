package com.antares.gsc.service;

import java.util.List;

import com.antares.gsc.model.dto.dataset.DatasetAddRequest;
import com.antares.gsc.model.dto.dataset.DatasetDeleteRequest;
import com.antares.gsc.model.dto.dataset.DatasetQueryRequest;
import com.antares.gsc.model.entity.Dataset;
import com.antares.gsc.model.vo.dataset.DatasetVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Antares
* @description 针对表【dataset】的数据库操作Service
* @createDate 2023-10-30 12:40:04
*/
public interface DatasetService extends IService<Dataset> {

    void createDataset(DatasetAddRequest request);

    List<String> getDatasetTags();

    Page<DatasetVo> listDatasetVoByPage(DatasetQueryRequest request);

    DatasetVo getDatasetById(Long did);

    void deleteDataset(DatasetDeleteRequest request);
}
