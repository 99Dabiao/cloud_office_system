package com.office.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.office.model.system.SysUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Dabiao
 * @since 2023-03-23
 */
public interface SysUserService extends IService<SysUser> {


    void updateStatus(Long id, Integer status);
}
