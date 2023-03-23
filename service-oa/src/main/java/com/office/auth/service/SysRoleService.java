package com.office.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.office.model.system.SysRole;
import com.office.vo.system.AssignRoleVo;

import java.util.Map;

/**
 * @ClassName SysRoleServiceImpl
 * @Description TODO 接口
 * @Author Dabiao
 * @Date 2023/3/20 17:44
 * @Version 1.0
 */
public interface SysRoleService extends IService<SysRole> {
    /**
     * 根据用户获取角色数据
     * @param userId
     * @return
     */
    Map<String, Object> findRoleByAdminId(Long userId);

    /**
     * 分配角色
     * @param assignRoleVo
     */
    void doAssign(AssignRoleVo assignRoleVo);

}
