package com.office.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.office.model.process.ProcessRecord;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author Dabiao
 * @since 2023-04-05
 */
public interface ProcessRecordService extends IService<ProcessRecord> {
    void record(Long processId, Integer status, String description);

}
