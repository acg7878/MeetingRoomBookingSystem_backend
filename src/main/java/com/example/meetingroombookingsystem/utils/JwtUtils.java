package com.example.meetingroombookingsystem.utils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {
    @Value("${spring.security.jwt.key}")
    private String key;

    @Value("${spring.security.jwt.expire}")
    private int expire;

    public DecodedJWT resolveToken(String headToken) {
        String token = this.convertToToken(headToken);
        if(token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            Date expiresAt = decodedJWT.getExpiresAt();
            return new Date().after(expiresAt) ? null : decodedJWT;
        } catch (JWTVerificationException e) {return null;}

    }

    private String convertToToken(String headToken) {
        if(headToken == null || !headToken.startsWith("Bearer ")) return null;
        return headToken.substring(7);
    }

    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire*24);
        return calendar.getTime();
    }

    public String createJWT(UserDetails userDetails,int id, String username) {
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expire = this.expireTime();
        return JWT.create()
                .withClaim("id",id)
                .withClaim("username", username)
                .withClaim("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expire)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    public UserDetails toUser(DecodedJWT decodedJWT) {
        Map<String, Claim> claims = decodedJWT.getClaims();
        return User
                .withUsername(claims.get("username").asString())
                .password("没设密码")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    public Integer toId(DecodedJWT decodedJWT) {
        Map<String, Claim> claims = decodedJWT.getClaims();
        return claims.get("id").asInt();
    }
}
