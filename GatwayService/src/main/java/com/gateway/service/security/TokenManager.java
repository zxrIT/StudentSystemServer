package com.gateway.service.security;

import com.common.exception.entity.authentication.JWTParsingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

@Component
@SuppressWarnings("all")
public class TokenManager {
    private final static Logger logger = LoggerFactory.getLogger(TokenManager.class);
    private final static String secretKey = "zxr-student-course-election-system-secret";
    private final static String redisLoginKey = "student:token:";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Map<String, String> extractUserDetails(String token) throws JWTParsingException {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Map<String, String> userDetails = new HashMap<>();
            userDetails.put("id", claims.get("id", String.class));
            userDetails.put("username", claims.get("username", String.class));
            userDetails.put("roleId", claims.get("roleId").toString());

            logger.info("从Token中提取用户信息 - 用户ID: {}, 用户名: {}, 角色ID: {}",
                    userDetails.get("id"),
                    userDetails.get("username"),
                    userDetails.get("roleId"));
            return userDetails;
        } catch (Exception exception) {
            logger.error("提取用户信息失败: {}", exception.getMessage());
            throw new JWTParsingException("Token解析失败" + exception.getMessage());
        }
    }
}
