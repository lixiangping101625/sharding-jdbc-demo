package com.example.shardingjdbcdemo.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "t_order_item")
@Data
public class OrderItem implements Serializable {
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "item_count")
    private Integer itemCount;

    @Column(name = "user_id")
    private Long userId;

}