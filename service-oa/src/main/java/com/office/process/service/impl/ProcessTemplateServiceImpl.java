package com.office.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.office.model.process.ProcessTemplate;
import com.office.model.process.ProcessType;
import com.office.process.mapper.ProcessTemplateMapper;
import com.office.process.service.ProcessService;
import com.office.process.service.ProcessTemplateService;
import com.office.process.service.ProcessTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author Dabiao
 * @since 2023-04-02
 */
@Service
public class ProcessTemplateServiceImpl extends ServiceImpl<ProcessTemplateMapper, ProcessTemplate> implements ProcessTemplateService {
    @Resource
    private ProcessTemplateService processTemplateService;
    @Resource
    private ProcessTypeService processTypeService;
    @Resource
    private ProcessService processService;

    @Override
    public IPage<ProcessTemplate> selectTemplatePage(Page<ProcessTemplate> pageParam) {
        //1 调用mapper的方法实现分页查询
        IPage<ProcessTemplate> processTemplatePage = baseMapper.selectPage(pageParam, null);
        //2 第一步分页查询返回分页数据，从分页数据获取列表List集台
        List<ProcessTemplate> processTemplatePageRecords = processTemplatePage.getRecords();
        //3 遍历List集合，得到每个对象的审批类型id
        for (ProcessTemplate processTemplate : processTemplatePageRecords) {
            //4 根据审批类型d，查询获取对应名称
            ProcessType processType = processTypeService.getOne(new LambdaQueryWrapper<ProcessType>()
                    .eq(ProcessType::getId,processTemplate.getProcessTypeId()));
            if (processType == null){
                continue;
            }
            processTemplate.setProcessTypeName(processType.getName());
        }
        //5 完成最终封装processTypeName
        return processTemplatePage;
    }

    @Transactional
    @Override
    public void publish(Long id) {
        ProcessTemplate processTemplate = this.getById(id);
        processTemplate.setStatus(1);
        processTemplateService.updateById(processTemplate);

        //TODO 部署流程定义，后续完善
        //优先发布在线流程设计
        if(!StringUtils.isEmpty(processTemplate.getProcessDefinitionPath())) {
            processService.deployByZip(processTemplate.getProcessDefinitionPath());
        }

    }
}
