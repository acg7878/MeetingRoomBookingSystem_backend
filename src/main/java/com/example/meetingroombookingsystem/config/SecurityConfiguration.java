package com.example.meetingroombookingsystem.config;

import com.example.meetingroombookingsystem.controller.RestBean;
import com.example.meetingroombookingsystem.entity.auth.Users;
import com.example.meetingroombookingsystem.repository.auth.UserRepository;
import com.example.meetingroombookingsystem.utils.JwtUtils;
import com.example.meetingroombookingsystem.vo.response.AuthorizeVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
public class SecurityConfiguration {
    @Resource
    JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure))
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        String role = user.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .findFirst()
                .orElse("USER");
        int userId = userRepository.findByUsername(username)
                .map(Users::getUserId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        String token = jwtUtils.createJWT(user, userId, username);
        AuthorizeVO vo = new AuthorizeVO();
        vo.setExpireTime(jwtUtils.expireTime());
        vo.setRole(role);
        vo.setToken(token);
        vo.setUsername(user.getUsername());
        response.getWriter().write(RestBean.success(token).asJsonString());
    }

    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.failture(401, exception.getMessage()).asJsonString());
    }

    public void onLogoutSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {
        response.getWriter().write("退出成功");
    }
}
