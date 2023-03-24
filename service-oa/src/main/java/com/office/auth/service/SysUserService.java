package com.office.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.office.model.system.SysUser;

import java.util.Map;

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

    SysUser getByUserName(String username);

    void saveUser(SysUser user);


    Map<String, Object> getUserInfo(Long userid);

}

