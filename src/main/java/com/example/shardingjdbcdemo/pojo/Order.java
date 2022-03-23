package com.example.shardingjdbcdemo.pojo;

import java.math.BigDecimal;
import javax.persistence.*;

@Table(name = "t_order_1")
public class Order {
    private Integer id;

    @Column(name = "order_amount")
    private BigDecimal orderAmount;

    @Column(name = "order_status")
    private Integer orderStatus;

    @Column(name = "user_id")
    private Integer userId;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return order_amount
     */
    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    /**
     * @param orderAmount
     */
    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    /**
     * @return order_status
     */
    public Integer getOrderStatus() {
        return orderStatus;
    }

    /**
     * @param orderStatus
     */
    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * @return user_id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}