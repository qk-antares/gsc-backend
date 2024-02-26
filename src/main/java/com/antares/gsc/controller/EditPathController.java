package com.antares.gsc.controller;

import com.antares.gsc.common.response.R;
import com.antares.gsc.model.dto.editPath.EditPathQueryRequest;
import com.antares.gsc.model.vo.retrieval.PathsVo;
import com.antares.gsc.service.EditPathService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/paths")
public class EditPathController {
    @Resource
    private EditPathService editPathService;

    /**
     * 计算编辑路径
     * @param request
     * @return
     */
    @PostMapping
    public R<PathsVo> getPaths(@RequestBody EditPathQueryRequest request){
        PathsVo pathsVo = editPathService.getPaths(request);
        return R.ok(pathsVo);
    }
}
