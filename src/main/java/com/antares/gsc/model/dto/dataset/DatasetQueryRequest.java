package com.antares.gsc.model.dto.dataset;

import com.antares.gsc.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DatasetQueryRequest extends PageRequest implements Serializable {
    /**
     * 标签列表
     */
    private List<String> tags;
    private String keyword;

    private static final long serialVersionUID = 1L;
}