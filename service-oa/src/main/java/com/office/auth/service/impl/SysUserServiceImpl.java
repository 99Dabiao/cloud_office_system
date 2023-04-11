package com.office.auth.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.office.auth.mapper.SysUserMapper;
import com.office.auth.service.SysMenuService;
import com.office.auth.service.SysUserService;
import com.office.common.utils.MD5;
import com.office.model.system.SysUser;
import com.office.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Dabiao
 * @since 2023-03-23
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    SysMenuService sysMenuService;
    @Autowired
    SysUserService sysUserService;
//    @Autowired
//    private SysDeptService sysDeptService;
    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        SysUser sysUser = this.getById(id);
        if(status.intValue() == 1) {
            sysUser.setStatus(status);
        } else {
            sysUser.setStatus(0);
        }
        this.updateById(sysUser);
    }

    @Override
    public SysUser getByUserName(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,username));
    }

    @Override
    public void saveUser(SysUser user) {

        user.setPassword(MD5.encrypt(user.getPassword()));
        sysUserService.save(user);
    }

    @Override
    public Map<String, Object> getUserInfo(Long userid) {
        Map<String, Object> result = new HashMap<>();
        SysUser sysUser = sysUserService.getById(userid);

        //根据用户id获取菜单权限值
        List<RouterVo> routerVoList = sysMenuService.findUserMenuList(sysUser.getId());
        //根据用户id获取用户按钮权限
        List<String> permsList = sysMenuService.findUserPermsList(sysUser.getId());

        result.put("name", sysUser.getName());
        result.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        //当前权限控制使用不到，我们暂时忽略
        result.put("roles",  new HashSet<>());
        result.put("buttons", permsList);
        result.put("routers", routerVoList);
        return result;
    }


}
