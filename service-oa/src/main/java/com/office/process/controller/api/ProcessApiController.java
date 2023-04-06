package com.office.process.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.office.common.result.Result;
import com.office.process.service.ProcessService;
import com.office.process.service.ProcessTemplateService;
import com.office.process.service.ProcessTypeService;
import com.office.vo.process.ProcessFormVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ProcessApiController
 * @Description TODO 审批流管理
 * @Author Dabiao
 * @Date 2023/4/3 16:26
 * @Version 1.0
 */
@Api(tags = "审批流管理")
@RestController
@RequestMapping(value="/admin/process")
@CrossOrigin  //跨域
public class ProcessApiController {
    @Autowired
    private ProcessService processService;

    @Autowired
    private ProcessTypeService processTypeService;
    @Autowired
    private ProcessTemplateService processTemplateService;

    @ApiOperation(value = "待处理")
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        return Result.ok(processService.findPending(pageParam));
    }


    @ApiOperation(value = "启动流程")
    @PostMapping("/startUp")
    public Result start(@RequestBody ProcessFormVo processFormVo) {
        processService.startUp(processFormVo);
        return Result.ok();
    }

    @ApiOperation( value = "获取审批模板数据")
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result getProcessTemplate(@PathVariable Long processTemplateId){
        return Result.ok(processTemplateService.getById(processTemplateId));

    }

    @ApiOperation(value = "获取全部审批分类及模板")
    @GetMapping("findProcessType")
    public Result findProcessType() {
        return Result.ok(processTypeService.findProcessType());
    }
}
