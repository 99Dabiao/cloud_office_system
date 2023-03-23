package com.office.auth.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.office.auth.mapper.SysMenuMapper;
import com.office.auth.service.SysMenuService;
import com.office.auth.service.SysRoleMenuService;
import com.office.auth.utils.MenuHelp;
import com.office.common.config.exception.CloudOfficeException;
import com.office.model.system.SysMenu;
import com.office.model.system.SysRoleMenu;
import com.office.vo.system.AssignMenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author Dabiao
 * @since 2023-03-23
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;



    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        //查询所有可用菜单（status=1）
        List<SysMenu> allSysMenuList = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));
        //根据roleId查询角色菜单关系表里面对应的所有菜单id
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        //根据菜单id获取对应菜单对象
        List<Long> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        //根据菜单id与菜单集合进行比较，相同则封装
        allSysMenuList.stream().forEach(item -> {
            if (menuIdList.contains(item.getId())){
                item.setSelect(true);
            }else {
                item.setSelect(false);
            }
        });
        //返回规定菜单格式
        return MenuHelp.buildTree(allSysMenuList);
    }

    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
        //先删除原有角色菜单对应关系，然后重新分配
        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId,assignMenuVo.getRoleId()));
        for (Long menuId : assignMenuVo.getMenuIdList()) {
            if (StringUtils.isEmpty(menuId)) continue;
            SysRoleMenu rolePermission = new SysRoleMenu();
            rolePermission.setRoleId(assignMenuVo.getRoleId());
            rolePermission.setMenuId(menuId);
            sysRoleMenuService.save(rolePermission);
        }

    }
    @Override
    public List<SysMenu> findNodes() {
        //全部权限列表
        if (CollectionUtils.isEmpty(this.list())) return null;
        //构建树形数据
        return MenuHelp.buildTree(this.list());
    }


    @Override
    public boolean removeById(Serializable id) {
        int count = this.count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
        if (count > 0) {
            throw new CloudOfficeException(201,"菜单不能删除");
        }
        sysMenuMapper.deleteById(id);
        return false;
    }
}
