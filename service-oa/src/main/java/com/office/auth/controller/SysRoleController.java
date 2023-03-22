package com.office.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.office.auth.service.SysRoleService;
import com.office.common.config.exception.CloudOfficeException;
import com.office.common.result.Result;
import com.office.model.system.SysRole;
import com.office.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SysRoleController
 * @Description TODO ds
 * @Author Dabiao
 * @Date 2023/3/20 20:16
 * @Version 1.0
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation( value = "查询所有角色")
    @GetMapping("findAll")
    public Result<List<SysRole>> findAll(){
        List<SysRole> sysRoleList = sysRoleService.list();
        return Result.ok(sysRoleList);
    }

    @ApiOperation( value = "自定义异常调试")
    @GetMapping("exception")
    public Result<List> exception(){
        try {
            int a = 10/0;
        }catch(Exception e) {
            throw new CloudOfficeException(20001,"出现自定义异常");
        }
        return Result.ok();
    }

    @ApiOperation( value = "条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result<IPage<SysRole>> pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo){
        //1 创建Page对象，传递分页相关参数
        //page 当前页  limit 每页显示记录数
        Page<SysRole> pageParam = new Page<>(page,limit);
        //2 封装条件，判断条件是否为空，不为空进行封装
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if(!StringUtils.isEmpty(roleName)) {
            //封装 like模糊查询
            wrapper.like(SysRole::getRoleName,roleName);
        }
        //3 调用方法实现
        IPage<SysRole> pageModel = sysRoleService.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }
    @ApiOperation( value = "添加角色")
    @PostMapping("save")
    public Result<SysRole> save(@RequestBody(required = false) @Validated SysRole sysRole){
        boolean is_save = sysRoleService.save(sysRole);
        if (is_save){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @ApiOperation( value = "根据id查询")
    @GetMapping("get/{id}")
    public Result<SysRole> get(@PathVariable Long id){
        SysRole byId = sysRoleService.getById(id);
        return Result.ok(byId);
    }
    @ApiOperation( value = "修改角色")
    @PutMapping("update")
    public Result<SysRole> update(@RequestBody(required = false) SysRole sysRole){
        boolean is_update = sysRoleService.updateById(sysRole);
        if (is_update){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @ApiOperation( value = "根据id删除")
    @DeleteMapping("remove/{id}")
    public Result<SysRole> remove(@PathVariable Long id){
        boolean is_removeById = sysRoleService.removeById(id);
        if (is_removeById){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @ApiOperation( value = "批量删除")
    @DeleteMapping("batchRemove")
    public Result<SysRole> batchRemove(@RequestBody List<Long> idList){
        boolean is_removeByIds = sysRoleService.removeByIds(idList);
        if (is_removeByIds){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }


}
