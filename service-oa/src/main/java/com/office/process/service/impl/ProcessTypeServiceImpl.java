package com.office.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.office.model.process.ProcessTemplate;
import com.office.model.process.ProcessType;
import com.office.process.mapper.ProcessTypeMapper;
import com.office.process.service.ProcessTemplateService;
import com.office.process.service.ProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author Dabiao
 * @since 2023-04-02
 */
@Service
public class ProcessTypeServiceImpl extends ServiceImpl<ProcessTypeMapper, ProcessType> implements ProcessTypeService {

    @Autowired
    private ProcessTemplateService processTemplateService;

    @Override
    public List<ProcessType> findProcessType() {
        //1 查询所有审批分类，返回list集合
        List<ProcessType> processTypeList = baseMapper.selectList(null);
        //2 遍历返回所有审批分类list集合,根据审批分类id查询对应审批模板数据（List）封装到每个审批分类对象里面
        processTypeList.forEach(processType -> processType.setProcessTemplateList(
                processTemplateService.list(
                        new LambdaQueryWrapper<ProcessTemplate>()
                                .eq(ProcessTemplate::getProcessTypeId, processType.getId())
                )
        ));
        return processTypeList;
    }
}
