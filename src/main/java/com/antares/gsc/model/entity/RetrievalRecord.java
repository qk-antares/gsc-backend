package com.antares.gsc.model.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Builder;
import lombok.Data;

/**
 * 检索的单条记录
 * @TableName retrieval_record
 */
@TableName(value ="retrieval_record")
@Data
@Builder
public class RetrievalRecord implements Serializable {
    /**
     * 检索id
     */
    private Long rid;

    /**
     * 图的id
     */
    private Long gid;

    /**
     * 预测GED
     */
    private Double preGed;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}