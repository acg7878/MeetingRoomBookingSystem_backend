package com.example.meetingroombookingsystem.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.meetingroombookingsystem.entity.dto.auth.Roles;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RolesMapper extends BaseMapper<Roles> {
}
