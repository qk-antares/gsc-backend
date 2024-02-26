package com.antares.gsc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.antares.gsc.model.entity.NodeLabel;
import com.antares.gsc.service.NodeLabelService;
import com.antares.gsc.mapper.NodeLabelMapper;
import org.springframework.stereotype.Service;

/**
* @author Antares
* @description 针对表【node_label(节点类型)】的数据库操作Service实现
* @createDate 2023-12-08 10:26:44
*/
@Service
public class NodeLabelServiceImpl extends ServiceImpl<NodeLabelMapper, NodeLabel>
    implements NodeLabelService{

}




