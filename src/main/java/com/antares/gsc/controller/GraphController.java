package com.antares.gsc.controller;

import com.antares.gsc.common.response.R;
import com.antares.gsc.model.dto.graph.*;
import com.antares.gsc.model.entity.Graph;
import com.antares.gsc.model.vo.graph.GraphVo;
import com.antares.gsc.service.DatasetService;
import com.antares.gsc.service.GraphService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/graph")
public class GraphController {
    @Resource
    private GraphService graphService;
    @Resource
    private DatasetService datasetService;

    /**
     * 上传图文件，实际是创建了一个graph对象，将文件内容设置为data属性，之后保存到数据库
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<Long> uploadGraph(@RequestParam("file") MultipartFile file) {
        Long gid = graphService.uploadGraph(file);
        return R.ok(gid);
    }

    /**
     * 向某个数据集中添加图
     * @param request
     * @return
     */
    @PostMapping
    public R addGraphsToDataset(@RequestBody GraphAddRequest request){
        graphService.addGraphsToDataset(request);
        return R.ok();
    }

    @PostMapping("/editor_add")
    public R addEditorGraphToDataset(@RequestBody EditorGraphAddRequest request){
        graphService.addEditorGraphToDataset(request);
        return R.ok();
    }

    @DeleteMapping
    public R deleteGraphFromDataset(@RequestBody GraphsDeleteRequest request){
        graphService.deleteGraphFromDataset(request);
        return R.ok();
    }

    @PutMapping
    public R updateGraphName(@RequestBody GraphUpdateRequest request){
        graphService.updateById(Graph.builder().gid(request.getGid()).name(request.getName()).build());
        return R.ok();
    }

    @PostMapping("/page/vo")
    public R<Page<GraphVo>> listGraphVoByPage(@RequestBody GraphQueryRequest request){
        long current = request.getCurrent();
        long pageSize = request.getPageSize();
        Page<Graph> graphPage = graphService.page(new Page<>(current, pageSize),
                graphService.getQueryWrapper(request));
        List<GraphVo> records = graphPage.getRecords().stream().map(GraphVo::objToVo).collect(Collectors.toList());
        Page<GraphVo> page = new Page<>(current, pageSize, graphPage.getTotal());
        page.setRecords(records);
        return R.ok(page);
    }
}
