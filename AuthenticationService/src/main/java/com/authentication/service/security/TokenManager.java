package com.authentication.service.security;

import com.common.exception.entity.authentication.JWTParsingException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@SuppressWarnings("all")
@RequiredArgsConstructor
public class TokenManager {
    private final static Logger logger = LoggerFactory.getLogger(TokenManager.class);
    private final static String secretKey = "zxr-student-course-election-system-secret";
    private final static String redisLoginKey = "student:token:";

    @Autowired
    private final StringRedisTemplate stringRedisTemplate;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(String id, String username, Integer roleId) {
        long expirationTime = 1000 * 60 * 60 * 24;
        logger.info("Token created for user: {} (ID: {}), Role: {}", username, id, roleId);
        return Jwts.builder()
                .subject("health")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .claim("id", id)
                .claim("username", username)
                .claim("roleId", roleId)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            logger.debug("Token格式验证通过");
            Map<String, String> stringStringMap = extractUserDetails(token);
            if (!stringRedisTemplate.hasKey(redisLoginKey + stringStringMap.get("id"))) {
                throw new RuntimeException("token已过期");
            }
            return true;
        } catch (ExpiredJwtException expiredJwtException) {
            logger.warn("Token已过期: {}", expiredJwtException.getMessage());
            throw new JWTParsingException("Token已过期: " + expiredJwtException.getMessage());
        } catch (UnsupportedJwtException unsupportedJwtException) {
            logger.error("不支持的Token格式: {}", unsupportedJwtException.getMessage());
            throw new JWTParsingException("不支持的Token格式: " + unsupportedJwtException.getMessage());
        } catch (MalformedJwtException malformedJwtException) {
            logger.error("Token结构错误: {}", malformedJwtException.getMessage());
            throw new JWTParsingException("Token结构错误: " + malformedJwtException.getMessage());
        } catch (SignatureException signatureException) {
            logger.error("签名验证失败: {}", signatureException.getMessage());
            throw new JWTParsingException("签名验证失败: " + signatureException.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            logger.error("Token为空: {}", illegalArgumentException.getMessage());
            throw new JWTParsingException("Token为空: " + illegalArgumentException.getMessage());
        } catch (Exception exception) {
            logger.error("Token验证失败: {}", exception.getMessage());
            throw new JWTParsingException("Token验证失败 " + exception.getMessage());
        }
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