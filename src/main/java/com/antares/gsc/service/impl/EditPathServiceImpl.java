package com.antares.gsc.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.antares.gsc.common.enums.AlgorithmEnum;
import com.antares.gsc.common.enums.HttpCodeEnum;
import com.antares.gsc.common.exception.BusinessException;
import com.antares.gsc.model.dto.editPath.EditPathQueryRequest;
import com.antares.gsc.model.vo.graph.GraphPair;
import com.antares.gsc.model.vo.retrieval.PathsVo;
import com.antares.gsc.service.EditPathService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EditPathServiceImpl implements EditPathService {
    @Value("${gsc.api-address.gedgnn.paths}")
    private String gedgnnApiAddress;
    @Value("${gsc.api-address.accurate.paths}")
    private String accurateApiAddress;

    @Override
    public PathsVo getPaths(EditPathQueryRequest request) {
        GraphPair graphPair = request.getGraphPair();
        if(graphPair.getGraph1().getNodes().size() > graphPair.getGraph2().getNodes().size()){
            throw new BusinessException(HttpCodeEnum.BAD_REQUEST, "请确保节点数较少的图位于第一个参数");
        }

        AlgorithmEnum algorithmEnum = AlgorithmEnum.getEnumByName(request.getAlgorithm());
        String apiAddress;
        switch (algorithmEnum) {
            case GEDGNN:
                apiAddress = gedgnnApiAddress;break;
            case ACCURATE:
                apiAddress = accurateApiAddress;break;
            default:
                throw new BusinessException(HttpCodeEnum.BAD_REQUEST, "不支持的编辑路径算法");
        }

        String requestBody = JSONUtil.toJsonStr(request);
        try (HttpResponse response = HttpUtil.createPost(apiAddress)
                .body(requestBody, "application/json")
                .execute()) {
            Object editPath = JSON.parseObject(response.body()).get("paths");
            BigDecimal preGed = (BigDecimal) JSON.parseObject(response.body()).get("pre_ged");
            BigDecimal timeUse = (BigDecimal) JSON.parseObject(response.body()).get("time_use");
            return PathsVo.builder()
                    .preGed(preGed.intValue())
                    .editPath(editPath)
                    .timeUse(timeUse.doubleValue()).build();
        }
    }
}
