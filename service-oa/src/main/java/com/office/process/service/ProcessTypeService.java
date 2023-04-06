package com.office.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.office.model.process.ProcessType;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author Dabiao
 * @since 2023-04-02
 */
public interface ProcessTypeService extends IService<ProcessType> {
    List<ProcessType> findProcessType();
}
