package com.office.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.office.model.system.SysMenu;
import com.office.vo.system.AssignMenuVo;
import com.office.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author Dabiao
 * @since 2023-03-23
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();


    List<SysMenu> findSysMenuByRoleId(Long roleId);

    void doAssign(AssignMenuVo assignMenuVo);


    List<String> findUserPermsList(Long userId);

    List<RouterVo> findUserMenuList(Long userId);


}
