package com.office.auth.controller;


import com.office.auth.service.SysMenuService;
import com.office.auth.service.SysUserService;
import com.office.common.config.exception.CloudOfficeException;
import com.office.common.jwt.JwtHelper;
import com.office.common.result.Result;
import com.office.common.utils.MD5;
import com.office.model.system.SysUser;
import com.office.vo.system.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName IndexController
 * @Description TODO 后台登陆、登出
 * @Author Dabiao
 * @Date 2023/3/22 13:11
 * @Version 1.0
 */
@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    @Autowired
    SysUserService sysUserService;
    @Autowired
    SysMenuService sysMenuService;

    /**
     * 登录
     *
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
        //获取输入用户名和密码，根据用户名查询数据库
        SysUser sysUser = sysUserService.getByUserName(loginVo.getUsername());
        //判断用户信息是否存在
        if (null == sysUser) {
            throw new CloudOfficeException(201, "用户不存在");
        }
        //输入的密码加密处理后比较数据库中已加密的密码
        if (!MD5.encrypt(loginVo.getPassword()).equals(sysUser.getPassword())) {
            throw new CloudOfficeException(201, "密码错误");
        }
        //判断用户状态
        if (sysUser.getStatus() == 0) {
            throw new CloudOfficeException(201, "用户被禁用");
        }
        //使用jwt根据用户id和用户名生成token字符串，返回结果
        Map<String, Object> map = new HashMap<>();
        map.put("token", JwtHelper.createToken(sysUser.getId(), sysUser.getUsername()));
        return Result.ok(map);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @ApiOperation("获取用户信息")
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        Long userId = JwtHelper.getUserId(request.getHeader("token"));
        Map<String, Object> map = sysUserService.getUserInfo(userId);
        return Result.ok(map);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("logout")
    public Result logout() {
        return Result.ok();
    }

}
