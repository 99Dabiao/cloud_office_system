package com.office.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.office.auth.service.SysUserService;
import com.office.model.process.ProcessRecord;
import com.office.model.system.SysUser;
import com.office.process.mapper.ProcessRecordMapper;
import com.office.process.service.ProcessRecordService;
import com.office.security.custom.LoginUserInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author Dabiao
 * @since 2023-04-05
 */
@Service
public class ProcessRecordServiceImpl extends ServiceImpl<ProcessRecordMapper, ProcessRecord> implements ProcessRecordService {
    @Autowired
    private ProcessRecordMapper processRecordMapper;
    @Autowired
    private SysUserService sysUserService;
    @Override
    public void record(Long processId, Integer status, String description) {
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUserId(sysUser.getId());
        processRecord.setOperateUser(sysUser.getName());
        baseMapper.insert(processRecord);
    }
}
