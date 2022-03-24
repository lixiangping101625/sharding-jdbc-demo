package com.example.shardingjdbcdemo.api;

import com.example.shardingjdbcdemo.dao.OrderItemMapper;
import com.example.shardingjdbcdemo.dao.OrderMapper;
import com.example.shardingjdbcdemo.pojo.Order;
import com.example.shardingjdbcdemo.pojo.OrderItem;
import com.example.shardingjdbcdemo.snowflake.SnowflakeUtils;
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

    /**
     * 新增订单测试分库分表：
     *  订单id为奇数时数据落到ds0库，订单id为偶数时数据落到ds1库.
     *  user_id为奇数时数据落到t_order_0表，user_id为偶数时数据落到t_order_1表.
     * @param order
     * @return
     */
    @PostMapping("/order/add")
    public String add(@RequestBody Order order){
        int i = orderMapper.insert(order);
        if (i > 0) {
            return "新增成功~";
        }
        return "新增失败~";
    }


    /**
     * 查询订单详情（注意：参与分片（分库分表）的字段必须都参与查询，才能唯一确定一条数据）
     * @param orderId 订单id
     * @param userId 用户id
     * @return
     */
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


    /**
     * 以下是对绑定表的新增和查询测试
     */

    @Resource
    private OrderItemMapper itemMapper;
    /**
     * 测试绑定表添加：
     * @param order
     * @return
     */
    @PostMapping("/order/add2")
    public String add2(@RequestBody Order order){
        //1、添加order
        int i = orderMapper.insert(order);
        //2、创建orderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setId(SnowflakeUtils.nextId());
        orderItem.setOrderId(order.getId());//分片（分库）键
        orderItem.setItemCount(10);
        orderItem.setUserId(order.getUserId());//分片（分表）键
        itemMapper.insert(orderItem);
        if (i > 0) {
            return "新增成功~";
        }
        return "新增失败~";
    }

    /**
     * 查询详情
     * @param orderId 订单id 分片（分库）键
     * @param userId userId 分片（分表）键
     * @return
     */
    @GetMapping("/item/{orderId}/{userId}")
    public List<OrderItem> test(@PathVariable("orderId") Long orderId,
                                @PathVariable("userId") Long userId){
        List<OrderItem> orderItems = itemMapper.selectTest(orderId, userId);
        return orderItems;
    }

}
