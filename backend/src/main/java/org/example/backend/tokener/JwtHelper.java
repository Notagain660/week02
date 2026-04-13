package org.example.backend.tokener;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSignerUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtHelper {

    @Value("${jwt.secretkey}")
    private String secretkey;

    public boolean validateTokenWithTime(String token) {
        try {
            JWTUtil.verify(token, secretkey.getBytes(StandardCharsets.UTF_8));//验证签名，hutool的方法

            JWTValidator.of(token)
                    .validateDate(new Date())  // 验证签发时间、生效时间、过期时间是否有效
                    .validateAlgorithm(JWTSignerUtil.hs256(secretkey.getBytes(StandardCharsets.UTF_8)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ThreadContext.UserInfo getUserInfoFromToken(String token) {
        if (!validateTokenWithTime(token)) {
            return null;
        }
        JWT jwt = JWTUtil.parseToken(token);//解析token

        String userId = (String) jwt.getPayload("userId");
        String role = (String) jwt.getPayload("role");
        String phone = (String) jwt.getPayload("userphone");
        String email = (String) jwt.getPayload("useremail");

        ThreadContext.UserInfo userInfo = new ThreadContext.UserInfo(userId, role, phone, email);
        userInfo.setUserId(userId);
        userInfo.setRole(role);//存进线程
        userInfo.setUserPhone(phone);
        userInfo.setUserEmail(email);
        return userInfo;
    }

}