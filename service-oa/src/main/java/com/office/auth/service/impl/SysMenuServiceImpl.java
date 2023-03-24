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
import com.office.vo.system.MetaVo;
import com.office.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.LinkedList;
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
            if (menuIdList.contains(item.getId())) {
                item.setSelect(true);
            } else {
                item.setSelect(false);
            }
        });
        //返回规定菜单格式
        return MenuHelp.buildTree(allSysMenuList);
    }

    //根据用户id获取用户可操作菜单
    @Override
    public List<RouterVo> findUserMenuList(Long userId) {
        List<SysMenu> sysMenuList = null;
        if (userId.longValue() == 1) {
            //管理员可以查询所有菜单列表
            sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1).
                    orderByAsc(SysMenu::getSortValue));
        } else {
            //不是管理员则根据userId查询可以操作菜单列表(多表查询)
            sysMenuList = sysMenuMapper.findListByUserId(userId);
        }
        //构造树形的查询结果
        List<SysMenu> sysMenuTreeList = MenuHelp.buildTree(sysMenuList);
        //构建框架需要的路由结构
        List<RouterVo> routerVoList = this.buildRouter(sysMenuTreeList);
        return routerVoList;
    }



    //构建框架需要的路由结构
    private List<RouterVo> buildRouter(List<SysMenu> shouldBuildTreeList) {
        List<RouterVo> routerVoList = new LinkedList<RouterVo>();
        //menus遍历
        for (SysMenu menu : shouldBuildTreeList) {
            RouterVo routerVo = new RouterVo();
            routerVo.setHidden(false);
            routerVo.setAlwaysShow(false);
            routerVo.setPath(getRouterPath(menu));
            routerVo.setComponent(menu.getComponent());
            routerVo.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            List<SysMenu> children = menu.getChildren();
            //如果当前是菜单，需将按钮对应的路由加载出来，如：“角色授权”按钮对应的路由在“系统管理”下面
            if (menu.getType().intValue() == 1) {
                List<SysMenu> hiddenMenuList = children.stream()
                        .filter(item -> !StringUtils.isEmpty(item.getComponent()))
                        .collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouterVo = new RouterVo();
                    hiddenRouterVo.setHidden(true);
                    hiddenRouterVo.setAlwaysShow(false);
                    hiddenRouterVo.setPath(getRouterPath(hiddenMenu));
                    hiddenRouterVo.setComponent(hiddenMenu.getComponent());
                    hiddenRouterVo.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routerVoList.add(hiddenRouterVo);
                }
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    if (children.size() > 0) {
                        routerVo.setAlwaysShow(true);
                    }
                    //递归
                    routerVo.setChildren(buildRouter(children));
                }
            }
            routerVoList.add(routerVo);
        }
        return routerVoList;
    }

    @Override
    public List<String> findUserPermsList(Long userId) {
        List<SysMenu> sysMenuList = null;
        if (userId.longValue() == 1) {
            //管理员可以查询所有菜单列表
            sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));
        } else {
            //不是管理员则根据userId查询可以操作菜单列表(多表查询)
            sysMenuList = baseMapper.findListByUserId(userId);
        }
        //获取查询数据中可以操作按钮值的list集合
        List<String> collect = sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());
        return collect;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if (menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
        //先删除原有角色菜单对应关系，然后重新分配
        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, assignMenuVo.getRoleId()));
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
            throw new CloudOfficeException(201, "菜单不能删除");
        }
        sysMenuMapper.deleteById(id);
        return false;
    }
}
