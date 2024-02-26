package com.antares.gsc.service.impl;

import org.springframework.stereotype.Service;

import com.antares.gsc.mapper.RetrievalRecordMapper;
import com.antares.gsc.model.entity.RetrievalRecord;
import com.antares.gsc.service.RetrievalRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
* @author Antares
* @description 针对表【retrieval_record(检索的单条记录)】的数据库操作Service实现
* @createDate 2023-11-16 10:59:16
*/
@Service
public class RetrievalRecordServiceImpl extends ServiceImpl<RetrievalRecordMapper, RetrievalRecord>
    implements RetrievalRecordService{
}




