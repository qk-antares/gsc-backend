package com.antares.gsc.mapper;

import com.antares.gsc.model.entity.NodeLabel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Antares
* @description 针对表【node_label(节点类型)】的数据库操作Mapper
* @createDate 2023-12-08 10:26:44
* @Entity com.antares.gsc.model.entity.NodeLabel
*/
public interface NodeLabelMapper extends BaseMapper<NodeLabel> {
    List<String> selectDistinctLabelsByDid(@Param("did") Long did);
}




