package com.office.common.jwt;

import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @ClassName JwtHelper
 * @Description TODO jwt防伪
 * @Author Dabiao
 * @Date 2023/3/24 12:14
 * @Version 1.0
 */
public class JwtHelper {

    //数字签名
    private static final String tokenSignKey = "123456";

    public static String createToken(Long userId, String username) {
        //有效时间
        long tokenExpiration = 30L * 24 * 60 * 60 * 1000;
        String token = Jwts.builder()
                //分类
                .setSubject("AUTH-USER")
                //token有效时长
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                //设置主体部分
                .claim("userId", userId)
                .claim("username", username)
                //签名部分
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static Long getUserId(String token) {
        try {
            if (StringUtils.isEmpty(token)) return null;
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            Integer userId = (Integer) claims.get("userId");
            return userId.longValue();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getUsername(String token) {
        try {
            if (StringUtils.isEmpty(token)) return "";

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return (String) claims.get("username");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String token = JwtHelper.createToken(5L, "zzjl");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUsername(token));
    }

}
