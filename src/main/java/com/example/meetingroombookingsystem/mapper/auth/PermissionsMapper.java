package com.example.meetingroombookingsystem.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.meetingroombookingsystem.entity.dto.auth.Permissions;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PermissionsMapper extends BaseMapper<Permissions> {
}
