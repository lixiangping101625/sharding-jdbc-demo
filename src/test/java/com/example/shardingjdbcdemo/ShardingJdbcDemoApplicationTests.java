package com.example.shardingjdbcdemo;

import com.example.shardingjdbcdemo.dao.OrderMapper;
import com.example.shardingjdbcdemo.pojo.Order;
import com.example.shardingjdbcdemo.snowflake.SnowflakeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;

@SpringBootTest
class ShardingJdbcDemoApplicationTests {

    @Resource
    private OrderMapper orderMapper;
    @Test
    void contextLoads() {

        Order order = new Order();
        order.setId(SnowflakeUtils.nextId());
        order.setOrderAmount(BigDecimal.ZERO);
        order.setOrderStatus(0);
        order.setUserId(SnowflakeUtils.nextId());

        int i = orderMapper.insert(order);
    }

}
