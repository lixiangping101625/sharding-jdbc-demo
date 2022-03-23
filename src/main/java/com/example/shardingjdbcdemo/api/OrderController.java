package com.example.shardingjdbcdemo.api;

import com.example.shardingjdbcdemo.dao.OrderMapper;
import com.example.shardingjdbcdemo.pojo.Order;
import com.example.shardingjdbcdemo.snowflake.SnowflakeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lixiangping
 * @createTime 2022年03月23日 16:14
 * @decription: 分库分表从测试
 */
@RestController
public class OrderController {

    @Resource
    private OrderMapper orderMapper;

    @PostMapping("/order/add")
    public String add(@RequestBody Order order){
//        for (int i = 0; i < 10000; i++) {

//            order.setId(SnowflakeUtils.nextId());
//            order.setUserId(SnowflakeUtils.nextId());

            orderMapper.insert(order);
//        }
        return "成功";
    }

}
