package com.office.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.office.model.process.ProcessRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 审批记录 Mapper 接口
 * </p>
 *
 * @author Dabiao
 * @since 2023-04-05
 */
@Mapper
public interface ProcessRecordMapper extends BaseMapper<ProcessRecord> {

}
