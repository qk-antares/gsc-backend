package com.antares.gsc.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 节点类型
 * @TableName node_label
 */
@TableName(value ="node_label")
@Data
@Builder
public class NodeLabel implements Serializable {
    /**
     * 数据集id
     */
    private Long did;

    /**
     * 图id
     */
    private Long gid;

    /**
     * 节点标签
     */
    private String nodeLabel;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}