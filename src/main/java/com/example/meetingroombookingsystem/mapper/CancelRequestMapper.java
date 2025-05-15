package com.example.meetingroombookingsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.meetingroombookingsystem.entity.dto.order.CancelRequests;
import com.example.meetingroombookingsystem.entity.dto.order.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CancelRequestMapper extends BaseMapper<CancelRequests> {
}
