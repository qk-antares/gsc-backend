package com.antares.gsc.mapper;

import com.antares.gsc.model.entity.Graph;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Antares
* @description 针对表【graph(图数据)】的数据库操作Mapper
* @createDate 2023-10-30 14:07:38
* @Entity com.antares.gsc.model.entity.Graph
*/
public interface GraphMapper extends BaseMapper<Graph> {
    Integer selectMaxEdgeNumByDid(@Param("did") Long did);
    Integer selectMaxNodeNumByDid(@Param("did") Long did);
}




