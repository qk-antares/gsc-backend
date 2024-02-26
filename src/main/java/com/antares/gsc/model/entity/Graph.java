package com.antares.gsc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图数据
 * @TableName graph
 */
@TableName(value ="graph")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Graph implements Serializable {
    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long gid;

    /**
     * 属于的数据集id
     */
    private Long did;

    /**
     * 图的名称
     */
    private String name;

    /**
     * 图的数据
     */
    private String graphData;

    /**
     * 边数
     */
    private Integer edgeNum;

    /**
     * 节点数
     */
    private Integer nodeNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标志
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}