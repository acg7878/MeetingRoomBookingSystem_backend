package com.example.meetingroombookingsystem.config;

import org.springframework.security.access.AccessDeniedException;
import com.example.meetingroombookingsystem.entity.dto.auth.Users;
import com.example.meetingroombookingsystem.filter.JwtAuthorizeFilter;
import com.example.meetingroombookingsystem.service.UsersService;
import com.example.meetingroombookingsystem.entity.vo.RestBean;
import com.example.meetingroombookingsystem.utils.JwtUtils;
import com.example.meetingroombookingsystem.entity.vo.response.AuthorizeVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;

    @Resource
    UsersService usersService;

    /**
     * 配置 Spring Security 的过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        .anyRequest().authenticated())
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .failureHandler(this::handleProcess)
                        .successHandler(this::handleProcess)
                        .permitAll())
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess))
                .exceptionHandling(conf -> conf
                        .accessDeniedHandler(this::handleProcess)
                        .authenticationEntryPoint(this::handleProcess))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 统一处理登录成功、登录失败、未登录拦截等逻辑
     */
    private void handleProcess(HttpServletRequest request,
            HttpServletResponse response,
            Object exceptionOrAuthentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();

        if (exceptionOrAuthentication instanceof AccessDeniedException exception) {
            // 处理权限不足的情况
            writer.write(RestBean.forbidden(exception.getMessage()).asJsonString());
        } else if (exceptionOrAuthentication instanceof Exception exception) {
            // 处理登录失败或未登录拦截的情况
            writer.write(RestBean.unauthorized(exception.getMessage()).asJsonString());
        } else if (exceptionOrAuthentication instanceof Authentication authentication) {
            // 登录成功的处理逻辑
            User user = (User) authentication.getPrincipal();
            // 动态获取用户信息
            Users account = usersService.findUsersByNameOrEmail(user.getUsername());
            if (account == null) {
                writer.write(RestBean.forbidden("用户信息不存在").asJsonString());
                return;
            }
            String roleName = usersService.findRoleByUserId(account.getUserId());
            String status = account.getStatus();
            // 生成 JWT
            String jwt = jwtUtils.createJWT(user, account.getUserId(), account.getUsername());
            if (jwt == null) {
                writer.write(RestBean.forbidden("登录验证频繁，请稍后再试").asJsonString());
            } else {
                // 构造返回的 VO 对象
                AuthorizeVO vo = new AuthorizeVO();
                vo.setToken(jwt);
                vo.setExpireTime(jwtUtils.expireTime());
                vo.setUsername(account.getUsername());
                vo.setRole(roleName);
                vo.setStatus(status);
                writer.write(RestBean.success(vo).asJsonString());
            }
        }
    }

    private void onLogoutSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if (jwtUtils.invalidateToken(authorization)) {
            writer.write(RestBean.success("退出登录成功").asJsonString());
        } else {
            writer.write(RestBean.failure(400, "退出登录失败").asJsonString());
        }
    }
}
