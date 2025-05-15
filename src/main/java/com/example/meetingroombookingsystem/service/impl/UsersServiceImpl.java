package com.example.meetingroombookingsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.meetingroombookingsystem.entity.dto.auth.*;
import com.example.meetingroombookingsystem.entity.vo.request.auth.ConfirmResetVO;
import com.example.meetingroombookingsystem.entity.vo.request.auth.EmailRegisterVO;
import com.example.meetingroombookingsystem.entity.vo.request.auth.EmailResetVO;
import com.example.meetingroombookingsystem.entity.vo.response.user.UsersResponseVo;
import com.example.meetingroombookingsystem.mapper.auth.*;
import com.example.meetingroombookingsystem.service.UsersService;

import com.example.meetingroombookingsystem.utils.Const;
import com.example.meetingroombookingsystem.utils.FlowUtils;

import com.example.meetingroombookingsystem.utils.TimeUtils;
import jakarta.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Resource
    private UserRolesMapper userRolesMapper;

    @Resource
    private RolesMapper rolesMapper;

    @Resource
    private PermissionsMapper permissionsMapper;

    @Resource
    private RolePermissionsMapper rolePermissionsMapper;

    @Resource
    AmqpTemplate rabbitTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    FlowUtils flow;

    @Value("${spring.web.verify.mail-limit}")
    int verifyLimit;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // users 和 user
        Users users = this.findUsersByNameOrEmail(username); // username可能是用户名也可能是邮箱
        if (users == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        String roleName = findRoleByUserId(users.getUserId());
        List<String> permissions = findPermissionsByRoleName(roleName);
        return User
                .withUsername(username)
                .password(users.getPassword())
                .authorities(permissions.toArray(new String[0]))
                .build();
    }

    private List<String> findPermissionsByRoleName(String roleName) {
        // 通过角色名称查找角色ID
        LambdaQueryWrapper<Roles> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(Roles::getRoleName, roleName);
        Roles role = rolesMapper.selectOne(roleWrapper);
        if (role == null) {
            return Collections.emptyList();
        }
        Integer roleId = role.getRoleId();
        // 通过角色ID查找权限ID列表
        LambdaQueryWrapper<RolePermissions> rolePermissionsWrapper = new LambdaQueryWrapper<>();
        rolePermissionsWrapper.eq(RolePermissions::getRoleId, roleId);
        List<RolePermissions> rolePermissions = rolePermissionsMapper.selectList(rolePermissionsWrapper);
        List<Integer> permissionIds = rolePermissions.stream()
                .map(RolePermissions::getPermissionId)
                .collect(Collectors.toList());
        if (permissionIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 通过权限ID列表查找具体的权限名
        LambdaQueryWrapper<Permissions> permissionsWrapper = new LambdaQueryWrapper<>();
        permissionsWrapper.in(Permissions::getPermissionId, permissionIds);
        List<Permissions> permissions = permissionsMapper.selectList(permissionsWrapper);
        List<String> permissionNames = permissions.stream()
                .map(Permissions::getPermissionName)
                .collect(Collectors.toList());
        // System.out.println("角色 " + roleName + " 的权限列表: " + permissionNames);
        return permissionNames;
    }

    public Users findUsersByNameOrEmail(String text) {
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    public String findRoleByUserId(Integer userId) {
        // 1. 通过 userId 查找 user_role 中对应的 roleId
        LambdaQueryWrapper<UserRoles> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoles::getUserId, userId);
        UserRoles userRole = userRolesMapper.selectOne(wrapper);
        if (userRole == null) {
            return null;
        }
        // 2. 通过 roleId 查找 Roles 表
        Roles role = rolesMapper.selectById(userRole.getRoleId());
        return role != null ? role.getRoleName() : null;
    }

    /**
     * 生成注册验证码存入Redis中，并将邮件发送请求提交到消息队列等待发送
     *
     * @param type    类型
     * @param email   邮件地址
     * @param address 请求IP地址
     * @return 操作结果，null表示正常，否则为错误原因
     */
    public String registerEmailVerifyCode(String type, String email, String address) {
        synchronized (address.intern()) { // 加锁，防止过多线程同时请求
            if (!this.verifyLimit(address))
                return "请求频繁，请稍后再试";
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            rabbitTemplate.convertAndSend(Const.MQ_MAIL, data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 邮件验证码注册账号操作，需要检查验证码是否正确以及邮箱、用户名是否存在重名
     *
     * @param info 注册基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    public String registerEmailAccount(EmailRegisterVO info) {
        String email = info.getEmail();
        String code = this.getEmailVerifyCode(email);
        String role = info.getRole();
        if (code == null)
            return "请先获取验证码";
        if (!code.equals(info.getCode()))
            return "验证码错误，请重新输入";
        if (this.existsAccountByEmail(email))
            return "该邮件地址已被注册";
        String username = info.getUsername();
        if (this.existsAccountByUsername(username))
            return "该用户名已被他人使用，请重新更换";
        String password = passwordEncoder.encode(info.getPassword());
        Users account = new Users(null, info.getUsername(),
                password, email, TimeUtils.timestampToLong(new Timestamp(System.currentTimeMillis())), TimeUtils.timestampToLong(new Timestamp(System.currentTimeMillis())), "pending");
        if (!this.save(account)) {
            return "内部错误，注册失败";
        } else {
            Integer userId = account.getUserId();
            Roles roleEntity = rolesMapper.selectOne(Wrappers.<Roles>query().eq("role_name", role));
            if (roleEntity == null) {
                return "角色不存在，请联系管理员";
            }
            Integer roleId = roleEntity.getRoleId();
            UserRoles userRole = new UserRoles(userId, roleId);
            if (userRolesMapper.insert(userRole) <= 0) {
                return "内部错误，角色分配失败";
            }
            this.deleteEmailVerifyCode(email);
            return "注册成功，请等待管理员审核";
        }
    }

    /**
     * 邮件验证码重置密码操作，需要检查验证码是否正确
     *
     * @param info 重置基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String resetEmailAccountPassword(EmailResetVO info) {
        String verify = resetConfirm(new ConfirmResetVO(info.getEmail(), info.getCode()));
        if (verify != null)
            return verify;
        String email = info.getEmail();
        String password = passwordEncoder.encode(info.getPassword());
        boolean update = this.update().eq("email", email).set("password", password).update();
        if (update) {
            this.deleteEmailVerifyCode(email);
        }
        return update ? null : "更新失败，请联系管理员";
    }

    /**
     * 重置密码确认操作，验证验证码是否正确
     *
     * @param info 验证基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String resetConfirm(ConfirmResetVO info) {
        String email = info.getEmail();
        String code = this.getEmailVerifyCode(email);
        if (code == null)
            return "请先获取验证码";
        if (!code.equals(info.getCode()))
            return "验证码错误，请重新输入";
        return null;
    }

    @Override
    public List<UsersResponseVo> listAllUsers() {
        return this.list().stream().map(user -> {
            UsersResponseVo responseVo = new UsersResponseVo();
            responseVo.setUsername(user.getUsername());
            responseVo.setEmail(user.getEmail());
            responseVo.setCreatedAt(user.getCreatedAt());
            responseVo.setUpdatedAt(user.getUpdatedAt());
            responseVo.setStatus(user.getStatus());
            return responseVo;
        }).collect(Collectors.toList());
    }

    @Override
    public String updateUserStatus(String userName, String status) {
        // 校验 status 是否合法
        if (status == null || !List.of("frozen", "pending", "active").contains(status)) {
            return "状态值非法，请传入正确的状态值（frozen, pending, active）";
        }
        // 查询用户是否存在
        Users user = this.findUsersByNameOrEmail(userName);
        if (user == null) {
            return "用户不存在";
        }
        // 更新用户状态
        boolean update = this.update()
                .eq("username", userName)
                .set("status", status)
                .update();
        return update ? null : "更新失败，请联系管理员";
    }

    @Override
    public String deleteUser(String userName) {
        // 检查用户是否存在
        Users user = this.findUsersByNameOrEmail(userName);
        if (user == null) {
            return "用户不存在";
        }
        // 删除用户
        boolean removed = this.removeById(user.getUserId());
        return removed ? null : "删除失败！！";
    }

    /**
     * 移除Redis中存储的邮件验证码
     *
     * @param email 电邮
     */
    private void deleteEmailVerifyCode(String email) {
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取Redis中存储的邮件验证码
     *
     * @param email 电邮
     * @return 验证码
     */
    private String getEmailVerifyCode(String email) {
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 针对IP地址进行邮件验证码获取限流
     *
     * @param address 地址
     * @return 是否通过验证
     */
    private boolean verifyLimit(String address) {
        String key = Const.VERIFY_EMAIL_LIMIT + address;
        return flow.limitOnceCheck(key, verifyLimit);
    }

    /**
     * 通过用户名或邮件地址查找用户
     *
     * @param text 用户名或邮件
     * @return 账户实体
     */
    public Users findAccountByNameOrEmail(String text) {
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    /**
     * 查询指定邮箱的用户是否已经存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    private boolean existsAccountByEmail(String email) {
        return this.baseMapper.exists(Wrappers.<Users>query().eq("email", email));
    }

    /**
     * 查询指定用户名的用户是否已经存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    private boolean existsAccountByUsername(String username) {
        return this.baseMapper.exists(Wrappers.<Users>query().eq("username", username));
    }

}