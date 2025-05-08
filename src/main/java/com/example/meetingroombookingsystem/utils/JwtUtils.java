package com.example.meetingroombookingsystem.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    @Value("${spring.security.jwt.key}")
    private String key;

    @Value("${spring.security.jwt.expire}")
    private int expire;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public boolean invalidateToken(String headerToken) {
        String token = this.convertToToken(headerToken);
        if (token == null)
            return false;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            String id = decodedJWT.getId();
            return deleteToken(id, decodedJWT.getExpiresAt());
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private boolean deleteToken(String uuid, Date time) {
        if (this.isInvalidToken(uuid))
            return false;
        Date date = new Date();
        long expire = Math.max(time.getTime() - date.getTime(), 0);
        stringRedisTemplate.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean isInvalidToken(String uuid) {
        return stringRedisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid);
    }

    public DecodedJWT resolveToken(String headerToken) {
        String token = this.convertToToken(headerToken);
        if (token == null)
            return null;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            if (this.isInvalidToken(decodedJWT.getId()))
                return null;
            Date expiresAt = decodedJWT.getExpiresAt();
            return new Date().after(expiresAt) ? null : decodedJWT;
        } catch (JWTVerificationException e) {
            return null;
        }

    }

    private String convertToToken(String headToken) {
        if (headToken == null || !headToken.startsWith("Bearer "))
            return null;
        return headToken.substring(7);
    }

    public Date expireTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire * 24);
        return calendar.getTime();
    }

    public String createJWT(UserDetails userDetails, int id, String username) {
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expire = this.expireTime();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id", id)
                .withClaim("username", username)
                .withClaim("authorities",
                        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expire)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    public UserDetails toUser(DecodedJWT decodedJWT) {
        Map<String, Claim> claims = decodedJWT.getClaims();
        return User
                .withUsername(claims.get("username").asString())
                .password("")
                .authorities(claims.get("authorities").asList(String.class).stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList())
                .build();
    }

    public Integer toId(DecodedJWT decodedJWT) {
        Map<String, Claim> claims = decodedJWT.getClaims();
        return claims.get("id").asInt();
    }
}
