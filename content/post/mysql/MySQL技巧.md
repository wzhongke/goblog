---
title: MySQL技巧
date: 2017-10-08 18:51:00
tags: ["MySQL"]
categories: ["MySQL"]
---

# `EXPLAIN`
`explain select * from user_info where id = 2` 上述 SQL 返回结果如下
```
  id: 1
  select_type: SIMPLE
        table: user_info
   partitions: NULL
         type: const
possible_keys: PRIMARY
          key: PRIMARY
      key_len: 8
          ref: const
         rows: 1
     filtered: 100.00
        Extra: NULL
1 row in set, 1 warning (0.00 sec)
```

各个列含义如下：
- id: SELECT 查询的标识符. 每个 SELECT 都会自动分配一个唯一的标识符.
- select_type: SELECT 查询的类型.
- table: 查询的是哪个表
- partitions: 匹配的分区
- type: join 类型
- possible_keys: 此次查询中可能选用的索引
- key: 此次查询中确切使用到的索引.
- ref: 哪个字段或常数与 key 一起被使用
- rows: 显示此查询一共扫描了多少行. 这个是一个估计值.
- filtered: 表示此查询条件所过滤的数据的百分比
- extra: 额外的信息

## `SELECT` 查询类型
select_type 表示了查询的类型, 它的常用取值有:
SIMPLE, 表示此查询不包含 UNION 查询或子查询
PRIMARY, 表示此查询是最外层的查询
UNION, 表示此查询是 UNION 的第二或随后的查询
DEPENDENT UNION, UNION 中的第二个或后面的查询语句, 取决于外面的查询
UNION RESULT, UNION 的结果
SUBQUERY, 子查询中的第一个 SELECT
DEPENDENT SUBQUERY: 子查询中的第一个 SELECT, 取决于外面的查询. 即子查询依赖于外层查询的结果.

## `type` 类型性能比较
**ALL < index < range ~ index_merge < ref < eq_ref < const < system**


# 剖析单条查询
1. `SHOW PROFILES;` 可以显示很高精度的查询时间，可以从结果中获取到 `QUERY_ID`
2. 使用 `SHOW PROFILES FOR QUERY ${QUERY_ID}` 来显示查询每个步骤执行的时间。但是这个不能排序，可以用下面的 SQL 排序：
   ```sql
    SELECT STATE, SUM(DURATION) AS Total_R,
        ROUND(
            100 * SUM(DURATION) /
                (SELECT SUM(DURATION) FROM INFORMATION_SCHEMA. PROFILING 
                WHERE QUERY_ID=@query_id
                ), 2) AS Pct_R,
        COUNT(*) AS Calls,
        SUM(DURATION) / COUNT(*) AS "R/Call"
    FROM INFORMATION_SCHEMA.PROFILING
    WHERE QUERY_ID = @query_id
    GROUP BY STATE
    ORDER BY Total_R DESC;
   ```



3. 查询时间是今天的数据
    ```sql
    select * from table_name where to_days(create_time) = to_days(now());
    select * from table_name where date(create_time) = curdate();
    ```
    <!-- more -->
4. 查询一周之内的数据
    ```sql
    select * from table_name where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(create_time);
    ```
    其中`DATE_SUB(date,INTERVAL expr unit)`是从日期中减去指定的时间间隔，`date`是合法的日期表达式，`expr`是时间间隔，`unit`可以是如下值

    Type值| 说明
    :-----|:--------
    MICROSECOND   | 毫秒
    SECOND        | 秒
    MINUTE        | 分 
    HOUR          | 时
    DAY           | 天
    WEEK          | 周
    MONTH         | 月
    QUARTER       | 刻
    YEAR          | 年

5. 删除MySQL中的重复数据
    ```sql
    delete from table where id not in (select * from (select max(id) from table group by duplicate having count(duplicate) > 1) as b) and id in (select * from (select id from table group by duplicate having count(duplicate) > 1) as c);
    ```

6. MySQL语句的性能问题
    MySQL语句的性能尤为重要，尤其是对于千万级记录的表。碰到一个问题，有一个删除语句的使用 `TO_DAYS(NOW()) - TO_DAYS(create_time) > 14` 条件删除14天前的数据，因为数据有2000万条以上，所以执行起来特别慢，使得同一机器的其他数据表表的查询速度特别慢。将`create_time` 的类型从 `datetime` 修改为 `date`，并将语句修改为 `create_time > 'year-month-day'`

7. MySQL 安装后，可能出现 `ERROR 1045 (28000): Access denied for user 'root'@'localhost' (using password: YES)` 问题。因为没有设置密码，登录不上去。
   解决方案：
   1. `sudo grep 'temporary password' /var/log/mysqld.log` 会生成一个临时密码，使用临时密码登陆后，`ALTER USER 'root'@'localhost' IDENTIFIED BY 'MyNewPass5!';` 修改密码

8. 从日志中查看 MySql 执行语句：`SET GLOBAL general_log = 'ON';`；使用 `SHOW VARIABLES LIKE "general_log%";` 查看日志目录