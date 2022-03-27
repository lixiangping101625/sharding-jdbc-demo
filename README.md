# 分库分表说明
#####  当公司业务量急剧上升后，数据库会达到性能瓶颈。对数据库一味纵向升级很难满足业务场景！与对单节点数据库升级相比更建议使用分库分表的方案。
    目前常用的分科分表中间件有MyCat和Sharding-JDBC。
    本项目使用Sharding-JDBC，究其原因是Sharding-JDBC更加轻量，通俗的讲对非DBA人员更加友好：你只需要根据项目
    的选型，选择Sharding-JDBC对应的配置方式（ShardingSphere-JDBC 可以通过 Java，YAML，Spring 命名空间和
    Spring Boot Starter 这 4 种方式进行配置，开发者可根据场景选择适合的配置方式。 详情请参见用户手册：
    https://shardingsphere.apache.org/document/current/cn/quick-start/shardingsphere-jdbc-quick-start/）。
    本项目采用的是Spring boot，所以自然选择的是Spring boot starter的方式进行分库分表的规则配置。
    另外，Sharding-JDBC支持同库分表，但MyCat不支持！Sharding-JDBC使用Java开发的，所以如果项目非java开发，请慎用~~

# 场景说明（需求）
#####  对订单表进行分库分表：
    订单id为奇数时数据落到ds0库，订单id为偶数时数据落到ds1库.
    user_id为奇数时数据落到t_order_0表，user_id为偶数时数据落到t_order_1表.


###数据库&表 准备工作：
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

###pom添加依赖：
    参见官方文档：https://shardingsphere.apache.org/document/current/cn/user-manual/shardingsphere-jdbc/spring-boot-starter/
    <!--sharding-jdbc-->
    <dependency>
        <groupId>org.apache.shardingsphere</groupId>
        <artifactId>shardingsphere-jdbc-core-spring-boot-starter</artifactId>
        <version>5.1.0</version>
    </dependency>
    
### ——————————————————————————    
### Spring boot starter方式配置Sharding-jdbc说明    
    #### 生命ShardingSphere的数据源
    spring.shardingsphere.datasource.names=ds0,ds1
    ### 配置第 1 个数据源
    spring.shardingsphere.datasource.ds0.type=com.zaxxer.hikari.HikariDataSource
    spring.shardingsphere.datasource.ds0.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.shardingsphere.datasource.ds0.jdbc-url=jdbc:mysql://localhost:3307/shard_order
    spring.shardingsphere.datasource.ds0.username=root
    spring.shardingsphere.datasource.ds0.password=123456
    ### 配置第 2 个数据源
    spring.shardingsphere.datasource.ds1.type=com.zaxxer.hikari.HikariDataSource
    spring.shardingsphere.datasource.ds1.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.shardingsphere.datasource.ds1.jdbc-url=jdbc:mysql://120.27.203.113:3306/sharding_order
    spring.shardingsphere.datasource.ds1.username=root
    spring.shardingsphere.datasource.ds1.password=yuanban_mysql
    
    
    #####################################################
    ######t_order表分片规则（其实就是实际节点）
    #####################################################
    spring.shardingsphere.rules.sharding.tables.t_order.actual-data-nodes=ds$->{0..1}.t_order_$->{0..1}
    ##### 分库策略，缺省表示使用默认分库策略，以下的分片策略只能选其一
    # 指定分库策略（单分片键）
    spring.shardingsphere.rules.sharding.tables.t_order.database-strategy.standard.sharding-column=id
    ##### #数据库分片算法名称（自定义）
    spring.shardingsphere.rules.sharding.tables.t_order.database-strategy.standard.sharding-algorithm-name=database-inline
    #####指定分表策略
    spring.shardingsphere.rules.sharding.tables.t_order.table-strategy.standard.sharding-column=user_id
    ##### #数据表分片算法名称（自定义）
    spring.shardingsphere.rules.sharding.tables.t_order.table-strategy.standard.sharding-algorithm-name=table-inline
    ##### 分片（分库分表）算法配置
    spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline.type=INLINE
    ##### #分库规则：根据`id`对`2`取模：值为0到`ds0`库，值为1到`ds1`库
    spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline.props.algorithm-expression=ds$->{id % 2}
    ##### #分表页选择`行表达式分片算法`
    spring.shardingsphere.rules.sharding.sharding-algorithms.table-inline.type=INLINE
    ##### #分表规则：根据`user_id`对`2`取模：值为0到`t_order_0`表，值为1到`t_order_1`表
    spring.shardingsphere.rules.sharding.sharding-algorithms.table-inline.props.algorithm-expression=t_order_$->{user_id % 2}
    
    
    #################################################################
    ############ 绑定表（t_order和t_order_item）说明：###########
    # 指分片规则一致的主表和子表。：
    # 1、这里t_order_item表是t_order表的绑定表（MyCat称之为子表）
    # 2、根据对t_order表配置的分片（分库分表）规则——id分库、user_id分表。所以t_order_item表分片（分库分表）规则必须完全一致
    #           所以t_order_item表应该有和t_order表相对应的字段：order_id关联t_order的id、 user_id。在所有分库中创建对应的t_order_item_0、t_order_item_0
    #                    CREATE TABLE `t_order_item_0` (
    #                      `id` bigint(11) NOT NULL,
    #                      `order_id` bigint(11) NOT NULL,
    #                      `item_count` int(11) DEFAULT NULL,
    #                      `user_id` bigint(20) DEFAULT NULL,
    #                      PRIMARY KEY (`id`)
    #                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
    # 3、配置绑定：spring.shardingsphere.rules.sharding.binding-tables[0]=t_order,t_order_item
    # 4、绑定表关联查询：使用绑定表进行多表关联查询时，必须使用分片键进行关联，否则会出现笛卡尔积关联或跨库关联，从而影响查询效率。
    #################################################################
    
    #####################################################
    ######t_order_item表分片规则（其实就是实际节点）配置
    #####################################################
    spring.shardingsphere.rules.sharding.tables.t_order_item.actual-data-nodes=ds$->{0..1}.t_order_item_$->{0..1}
    ##### 分库策略，缺省表示使用默认分库策略，以下的分片策略只能选其一
    # 指定分库策略（单分片键）
    spring.shardingsphere.rules.sharding.tables.t_order_item.database-strategy.standard.sharding-column=order_id
    spring.shardingsphere.rules.sharding.tables.t_order_item.database-strategy.standard.sharding-algorithm-name=database-inline-item
    ##### 指定分表策略
    spring.shardingsphere.rules.sharding.tables.t_order_item.table-strategy.standard.sharding-column=user_id
    spring.shardingsphere.rules.sharding.tables.t_order_item.table-strategy.standard.sharding-algorithm-name=table-inline-item
    ##### 分片（分库分表）算法配置
    spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline-item.type=INLINE
    spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline-item.props.algorithm-expression=ds$->{order_id % 2}
    spring.shardingsphere.rules.sharding.sharding-algorithms.table-inline-item.type=INLINE
    spring.shardingsphere.rules.sharding.sharding-algorithms.table-inline-item.props.algorithm-expression=t_order_item_$->{user_id % 2}
    
    #  绑定t_order和t_order_item
    spring.shardingsphere.rules.sharding.binding-tables[0]=t_order,t_order_item
    
    
    ## ————————————————————————————————————————————————————————————————
    ##### 广播表（MyCat中称之为全局表）：https://shardingsphere.apache.org/document/current/cn/features/sharding/concept/table/#广播表
    ##### 广播表（MyCat中称之为全局表）：指所有的分片数据源中都存在的表，表结构及其数据在每个数据库中均完全一致。
    #####            适用于数据量不大且需要与海量数据的表进行关联查询的场景，例如：字典表。
    spring.shardingsphere.rules.sharding.broadcast-tables=gender_info

    广播表测试：新增时所有分库的广播表都会新增记录
    2022-03-23 20:45:48.749  INFO 41936 --- [nio-8080-exec-4] ShardingSphere-SQL                       : Logic SQL: INSERT INTO gender_info  ( id,code,description ) VALUES( ?,?,? )
    2022-03-23 20:45:48.749  INFO 41936 --- [nio-8080-exec-4] ShardingSphere-SQL                       : SQLStatement: MySQLInsertStatement(setAssignment=Optional.empty, onDuplicateKeyColumns=Optional.empty)
    2022-03-23 20:45:48.749  INFO 41936 --- [nio-8080-exec-4] ShardingSphere-SQL                       : Actual SQL: ds0 ::: INSERT INTO gender_info  ( id,code,description ) VALUES(?, ?, ?) ::: [22, MAN, 男]
    2022-03-23 20:45:48.749  INFO 41936 --- [nio-8080-exec-4] ShardingSphere-SQL                       : Actual SQL: ds1 ::: INSERT INTO gender_info  ( id,code,description ) VALUES(?, ?, ?) ::: [22, MAN, 男]
    广播表测试：查询时可以看到只会查询一次，而不是从查询出多个
    2022-03-23 20:48:59.444  INFO 36604 --- [nio-8080-exec-1] ShardingSphere-SQL                       : Logic SQL: SELECT id,code,description  FROM gender_info  WHERE  id = ?
    2022-03-23 20:48:59.445  INFO 36604 --- [nio-8080-exec-1] ShardingSphere-SQL                       : SQLStatement: MySQLSelectStatement(table=Optional.empty, limit=Optional.empty, lock=Optional.empty, window=Optional.empty)
    2022-03-23 20:48:59.445  INFO 36604 --- [nio-8080-exec-1] ShardingSphere-SQL                       : Actual SQL: ds0 ::: SELECT id,code,description  FROM gender_info  WHERE  id = ? ::: [22]
    查询到记录数：1
    