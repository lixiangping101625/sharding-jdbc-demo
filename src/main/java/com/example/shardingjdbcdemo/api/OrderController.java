package com.example.shardingjdbcdemo.api;

import com.example.shardingjdbcdemo.dao.OrderMapper;
import com.example.shardingjdbcdemo.pojo.Order;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
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
        orderMapper.insert(order);
        return "成功";
    }

    @GetMapping("/order/{orderId}/{userId}")
    public Order add(@PathVariable("orderId") Long orderId,
                     @PathVariable("userId") Long userId){
        Example example = Example.builder(Order.class).build();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", orderId);
        criteria.andEqualTo("userId", userId);
        List<Order> list = orderMapper.selectByExample(example);
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

}
