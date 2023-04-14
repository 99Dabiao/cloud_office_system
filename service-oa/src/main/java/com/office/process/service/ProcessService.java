package com.office.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.office.model.process.Process;
import com.office.vo.process.ApprovalVo;
import com.office.vo.process.ProcessFormVo;
import com.office.vo.process.ProcessQueryVo;
import com.office.vo.process.ProcessVo;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author Dabiao
 * @since 2023-04-03
 */
public interface ProcessService extends IService<Process> {
    //审批管理列表
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);
    //部署流程定义
    void deployByZip(String deployPath);
    //启动流程实例
    void startUp(ProcessFormVo processFormVo);

    //查询处理任务
    IPage<ProcessVo> findPending(Page<Process> pageParam);

    IPage<ProcessVo> findProcessed(Page<Process> pageParam);

    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
    //查看审批详情
    Map<String, Object> show(Long id);

    void approve(ApprovalVo approvalVo);

    //我的
    Map<String, Object> getCurrentUser();
}


