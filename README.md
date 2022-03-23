##数据表
##### 新建两个库，在库下分别创建如下真实表:
CREATE TABLE `t_order_0` (
  `id` bigint(11) NOT NULL,
  `order_amount` decimal(10,2) NOT NULL,
  `order_status` int(11) NOT NULL,
  `user_id` bigint(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_order_1` (
  `id` bigint(11) NOT NULL,
  `order_amount` decimal(10,2) NOT NULL,
  `order_status` int(11) NOT NULL,
  `user_id` bigint(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;