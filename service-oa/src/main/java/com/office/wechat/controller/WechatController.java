package com.office.wechat.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.office.auth.service.SysUserService;
import com.office.common.jwt.JwtHelper;
import com.office.common.result.Result;
import com.office.model.system.SysUser;
import com.office.vo.wechat.BindPhoneVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @ClassName WechatController
 * @Description TODO
 * @Author Dabiao
 * @Date 2023/4/17 12:43
 * @Version 1.0
 */
@Controller
@RequestMapping("/admin/wechat")
@Slf4j
@CrossOrigin
public class WechatController {
    @Resource
    private SysUserService sysUserService;

    @Autowired
    private WxMpService wxMpService;

    @Value("${wechat.userInfoUrl}")
    private String userInfoUrl;

    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl,
                            HttpServletRequest request) {
        //buildAuthorizationUrl,授权路径,固定值,授权后跳转路径
        String redirectURL = null;
        try {
            redirectURL = wxMpService.getOAuth2Service().buildAuthorizationUrl(userInfoUrl,
                    WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                    URLEncoder.encode(returnUrl.replace("officeoa", "#"),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("【微信网页授权】获取code,redirectURL={}", redirectURL);
        return "redirect:" + redirectURL;

    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws Exception {
        log.info("【微信网页授权】code={}", code);
        log.info("【微信网页授权】state={}", returnUrl);
        //获取accessToken
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        //获取openId
        String openId = accessToken.getOpenId();
        log.info("【微信网页授权】openId={}", openId);
        //获取微信用户信息
        WxOAuth2UserInfo wxMpUser = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        log.info("【微信网页授权】wxMpUser={}", JSON.toJSONString(wxMpUser));
        //根据openId查询用户表
        SysUser sysUser = sysUserService.getOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getOpenId, openId));
        String token = "";
        //null != sysUser 说明已经绑定，反之为建立账号绑定，去页面建立账号绑定
        if(null != sysUser) {
            token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        }
        if(!returnUrl.contains("?")) {
            return "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
        } else {
            return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
        }
    }

    @ApiOperation(value = "微信账号绑定手机")
    @PostMapping("bindPhone")
    @ResponseBody
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
        SysUser sysUser = sysUserService.getOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, bindPhoneVo.getPhone()));
        if(null != sysUser) {
            sysUser.setOpenId(bindPhoneVo.getOpenId());
            sysUserService.updateById(sysUser);
            String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
            return Result.ok(token);
        } else {
            return Result.fail("手机号码不存在，绑定失败");
        }
    }



}
