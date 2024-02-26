package com.antares.gsc.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 检索
 * @TableName retrieval
 */
@TableName(value ="retrieval")
@Data
@Builder
public class Retrieval implements Serializable {
    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long rid;

    /**
     * 使用的算法
     */
    private String algorithm;

    /**
     * 被检索的图的数据
     */
    private String graphData;

    /**
     * 被检索的数据集
     */
    private Long did;

    /**
     * 检索的状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 计算完成时间
     */
    private Date finishTime;

    /**
     * 删除标志
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}