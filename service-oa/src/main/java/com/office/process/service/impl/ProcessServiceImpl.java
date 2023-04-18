package com.office.process.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.office.auth.mapper.SysDeptMapper;
import com.office.auth.mapper.SysPostMapper;
import com.office.auth.mapper.SysUserMapper;
import com.office.auth.service.SysUserService;
import com.office.model.process.Process;
import com.office.model.process.ProcessRecord;
import com.office.model.process.ProcessTemplate;
import com.office.model.system.SysDept;
import com.office.model.system.SysPost;
import com.office.model.system.SysUser;
import com.office.process.mapper.ProcessMapper;
import com.office.process.service.ProcessRecordService;
import com.office.process.service.ProcessService;
import com.office.process.service.ProcessTemplateService;
import com.office.process.service.WeChatMessageService;
import com.office.security.custom.LoginUserInfoHelper;
import com.office.vo.process.ApprovalVo;
import com.office.vo.process.ProcessFormVo;
import com.office.vo.process.ProcessQueryVo;
import com.office.vo.process.ProcessVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author Dabiao
 * @since 2023-04-03
 */
@Slf4j
@Service
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, Process> implements ProcessService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysPostMapper sysPostMapper;
    @Autowired
    private ProcessTemplateService processTemplateService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;
    @Autowired
    private ProcessMapper processMapper;
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessRecordService processRecordService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private WeChatMessageService weChatMessageService;

    @Override
    public Map<String, Object> getCurrentUser() {
        SysUser sysUser = sysUserMapper.selectById(LoginUserInfoHelper.getUserId());
        SysDept sysDept = sysDeptMapper.selectById(sysUser.getDeptId());
        SysPost sysPost = sysPostMapper.selectById(sysUser.getPostId());
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("name", sysUser.getName());
            map.put("phone", sysUser.getPhone());
            map.put("deptName", sysDept.getName());
            map.put("postName", sysPost.getName());
        }catch (NullPointerException e){
            map.put("deptName", "无");
            map.put("postName", "无");
        }

        return map;
    }

    //审批管理列表
    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> page = processMapper.selectPage(pageParam,processQueryVo);
        return page;
    }

    @Override
    public void deployByZip(String deployPath) {
        // 定义zip输入流
        InputStream inputStream = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream(deployPath);
        assert inputStream != null;
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 流程部署
        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("流程id:"+deploy.getId());
    }


    /**
     * @author Dabiao
     * @date 2023/4/13 17:4
     * @description 启动流程实例
     **/
    public void startUp(ProcessFormVo processFormVo) {
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());

        ProcessTemplate processTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());
        Process process = new Process();
        BeanUtils.copyProperties(processFormVo, process);
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
        process.setStatus(1);
        processMapper.insert(process);
        //绑定业务id
        String businessKey = String.valueOf(process.getId());
        //将表单数据放入流程实例中
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formData = jsonObject.getJSONObject("formData");
        //封装formData
        Map<String, Object> map = new HashMap<>();
        //循环转换
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        //流程参数
        Map<String, Object> variables = new HashMap<>();
        variables.put("data", map);
        //启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                processTemplate.getProcessDefinitionKey(),
                        businessKey, variables);
        //业务表关联当前流程实例id
        String processInstanceId = processInstance.getId();
        //计算下一个审批人，可能有多个（并行审批）
        List<Task> taskList = this.getCurrentTaskList(processInstanceId);
        if (!CollectionUtils.isEmpty(taskList)) {
            List<String> assigneeList = new ArrayList<>();
            for(Task task : taskList) {
                SysUser user = sysUserService.getByUserName(task.getAssignee());
                assigneeList.add(user.getName());
                //推送消息给下一个审批人，后续完善
                 weChatMessageService.pushPendingMessage(process.getId(),user.getId(), task.getId());
                 weChatMessageService.pushProcessedMessage(process.getId(),user.getId(),process.getStatus());
            }
            process.setProcessInstanceId(processInstanceId);
            process.setDescription("等待" + StringUtils.join(assigneeList.toArray(), ",") + "审批");
        }
        //更新oa_process
        processMapper.updateById(process);
        //记录操作行为
        processRecordService.record(process.getId(), 1, "发起申请");
    }

    /**
     * 获取当前任务列表
     * @param processInstanceId
     * @return
     */
    private List<Task> getCurrentTaskList(String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        return tasks;
    }


    /**
     * @author Dabiao
     * @date 2023/4/13 17:4
     * @description 待处理
     **/
    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
        // 根据当前人的ID查询
        TaskQuery query = taskService.createTaskQuery().taskAssignee(LoginUserInfoHelper.getUsername()).orderByTaskCreateTime().desc();
        List<Task> list = query.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());
        long totalCount = query.count();

        List<ProcessVo> processList = new ArrayList<>();
        // 根据流程的业务ID查询实体并关联
        for (Task item : list) {
            String processInstanceId = item.getProcessInstanceId();
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (processInstance == null) {
                continue;
            }
            // 业务key
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null) {
                continue;
            }
            Process process = this.getById(Long.parseLong(businessKey));
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId(item.getId());
            processList.add(processVo);
        }
        IPage<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        page.setRecords(processList);
        return page;
    }

    /**
     * @author Dabiao
     * @date 2023/4/13 17:4
     * @description 已处理
     **/



    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        // 根据当前人的ID查询
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished().orderByTaskCreateTime().desc();
        List<HistoricTaskInstance> list = query.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());
        long totalCount = query.count();

        List<ProcessVo> processList = new ArrayList<>();
        for (HistoricTaskInstance item : list) {
            String processInstanceId = item.getProcessInstanceId();
            Process process = this.getOne(new LambdaQueryWrapper<Process>().eq(Process::getProcessInstanceId, processInstanceId));
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId("0");
            processList.add(processVo);
        }
        IPage<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        page.setRecords(processList);
        return page;
    }

    /**
     * @author Dabiao
     * @date 2023/4/13 17:4
     * @description 已发起
     **/
    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> pageModel = baseMapper.selectPage(pageParam, processQueryVo);
        for (ProcessVo item : pageModel.getRecords()) {
            item.setTaskId("0");
        }
        return pageModel;
    }

    /**
     * @author Dabiao
     * @date 2023/4/13 17:4
     * @description 展示审批流程
     **/
    @Override
    public Map<String, Object> show(Long id) {
        // 1 根据流程id获取流程信息
        Process process = baseMapper.selectById(id);

        // 2 根据流程id获取流程记录信息
        List<ProcessRecord> processRecordList = processRecordService.list(
                new LambdaQueryWrapper<ProcessRecord>().eq(ProcessRecord::getProcessId,id));

        // 3 根据模板id查询模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());

        // 4 判断当前用户是否可以审批
        // 可以看到信息不一定能审批，不能重复审批
        boolean isApprove = this.getCurrentTaskList(process.getProcessInstanceId())
                .stream()
                .anyMatch(task -> task.getAssignee().equals(LoginUserInfoHelper.getUsername()));

        // 5 查询数据封装到map集合，返回
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        map.put("isApprove", isApprove);

        return map;
    }


    /**
     * @author Dabiao
     * @date 2023/4/13 17:4
     * @description 审批
     **/
    @Override
    public void approve(ApprovalVo approvalVo) {
        //1 从approvalVo获取任务id，根据任务id获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for(Map.Entry<String,Object> entry:variables.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

        //2 判断审批状态值
        if(approvalVo.getStatus() == 1) {
            //2.1 状态值 =1  审批通过
            Map<String, Object> variable = new HashMap<>();
            taskService.complete(taskId,variable);
        } else {
            //2.2 状态值 = -1 驳回，流程直接结束
            this.endTask(taskId);
        }

        //3 记录审批相关过程信息 oa_process_record
        String description = approvalVo.getStatus().intValue() ==1 ? "已通过" : "驳回";
        processRecordService.record(approvalVo.getProcessId(),
                approvalVo.getStatus(),description);

        //4 查询下一个审批人，更新流程表记录 process表记录
        Process process = baseMapper.selectById(approvalVo.getProcessId());
        //查询任务
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if(!CollectionUtils.isEmpty(taskList)) {
            List<String> assignList = new ArrayList<>();
            for(Task task : taskList) {
                String assignee = task.getAssignee();
                SysUser sysUser = sysUserService.getByUserName(assignee);
                assignList.add(sysUser.getName());
                //TODO 公众号消息推送
                weChatMessageService.pushPendingMessage(process.getId(),sysUser.getId(), task.getId());
                weChatMessageService.pushProcessedMessage(process.getId(),sysUser.getId(),process.getStatus());
            }
            //更新process流程信息
            process.setDescription("等待" + StringUtils.join(assignList.toArray(), ",") + "审批");
            process.setStatus(1);
        } else {
            if(approvalVo.getStatus().intValue() == 1) {
                process.setDescription("审批完成（通过）");
                process.setStatus(2);
            } else {
                process.setDescription("审批完成（驳回）");
                process.setStatus(-1);
            }
        }

        weChatMessageService.pushProcessedMessage(process.getId(),process.getUserId(),approvalVo.getStatus());
        baseMapper.updateById(process);

    }

    /**
     * @author Dabiao
     * @date 2023/4/13 17:4
     * @description 结束流程
     **/
    private void endTask(String taskId) {
        //1 根据任务id获取任务对象 Task
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        //2 获取流程定义模型 BpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());

        //3 获取结束流向节点
        List<EndEvent> endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        if(CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode)endEventList.get(0);

        //4 当前流向节点
        FlowNode currentFlowNode = (FlowNode)bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //5 清理当前流动方向
        currentFlowNode.getOutgoingFlows().clear();

        //6 创建新流向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlow");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);

        //7 当前节点指向新方向
        List newSequenceFlowList = new ArrayList();
        newSequenceFlowList.add(newSequenceFlow);
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //8 完成当前任务
        taskService.complete(task.getId());
    }


}
