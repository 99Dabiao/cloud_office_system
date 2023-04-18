package com.office.security.filter;

import com.alibaba.fastjson.JSON;
import com.office.common.jwt.JwtHelper;
import com.office.common.result.ResponseUtil;
import com.office.common.result.Result;
import com.office.common.result.ResultCodeEnum;
import com.office.security.custom.LoginUserInfoHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TokenAuthenticationFilter
 * @Description TODO 认证解析token过滤器
 * @Author Dabiao
 * @Date 2023/3/25 20:16
 * @Version 1.0
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private RedisTemplate redisTemplate;
    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        logger.info("uri:"+request.getRequestURI());
        //如果是登录接口，直接放行
        if("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if(null != authentication) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_AUTH));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // token置于header里
        String token = request.getHeader("token");
        logger.info("token:"+token);
        if (!StringUtils.isEmpty(token)) {
            String username = JwtHelper.getUsername(token);
            logger.info("username:"+username);
            if (!StringUtils.isEmpty(username)) {
                //放置用户属性到ThreadLocal
                LoginUserInfoHelper.setUserId(JwtHelper.getUserId(token));
                LoginUserInfoHelper.setUsername(username);
                //通过username从redis中获取数据
                String authString = (String) redisTemplate.opsForValue().get(username);
                //把redis获取的权限数据转换要求集合类型List<SimpleGrantedAuthority>
                if (!StringUtils.isEmpty(authString)){
                    List<Map> mapList = JSON.parseArray(authString, Map.class);
                    System.out.println(mapList);
                    List<SimpleGrantedAuthority> authList = new ArrayList<>();
                    for (Map map : mapList) {
                        authList.add(new SimpleGrantedAuthority((String)map.get("authority")));
                    }
                    return new UsernamePasswordAuthenticationToken(username, null, authList);
                } else {
                    return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                }
            }
        }
        return null;
    }
}
