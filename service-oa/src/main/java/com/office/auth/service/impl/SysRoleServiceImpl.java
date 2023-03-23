package com.office.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.office.auth.mapper.SysRoleMapper;
import com.office.auth.mapper.SysUserRoleMapper;
import com.office.auth.service.SysRoleService;
import com.office.model.system.SysRole;
import com.office.model.system.SysUserRole;
import com.office.vo.system.AssginRoleVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName SysRoleServiceImpl
 * @Description TODO
 * @Author Dabiao
 * @Date 2023/3/20 17:45
 * @Version 1.0
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Override
    public Map<String, Object> findRoleByAdminId(Long userId) {
        //查询所有角色，返回list
        List<SysRole> allRoleList = this.list();
        //已存在的角色id
        List<SysUserRole> existSysUserRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId,userId).select(SysUserRole::getRoleId));
        List<Long> existRoleIdList = existSysUserRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        //对角色进行分类
        List<SysRole> assignRoleList = new ArrayList<>();
        for (SysRole role : allRoleList){
            if (existRoleIdList.contains(role.getId())){
                assignRoleList.add(role);
            }
        }
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assignRoleList", assignRoleList);
        roleMap.put("allRolesList", allRoleList);
        return roleMap;
    }
    @Transactional
    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        //根据userid删除用户原有角色
        sysUserRoleMapper.delete(new
                LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, assginRoleVo.getUserId()));
        //重新分配角色


        for(Long roleId : assginRoleVo.getRoleIdList()) {
            if(StringUtils.isEmpty(roleId)) continue;
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(assginRoleVo.getUserId());
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insert(userRole);
        }


    }
}
