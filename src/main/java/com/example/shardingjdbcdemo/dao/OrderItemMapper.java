package com.example.shardingjdbcdemo.dao;

import com.example.shardingjdbcdemo.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface OrderItemMapper extends Mapper<OrderItem> {

    List<OrderItem> selectTest(@Param(value = "orderId") Long orderId, @Param("userId") Long userId);
}