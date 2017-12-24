---
title: MySQL技巧
date: 2017-10-08 18:51:00
tags: ["MySQL"]
categories: ["MySQL"]
---


1. 查询时间是今天的数据
    ```sql
    select * from table_name where to_days(create_time) = to_days(now());
    select * from table_name where date(create_time) = curdate();
    ```
    <!-- more -->
2. 查询一周之内的数据
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

3. 删除MySQL中的重复数据
    ```sql
    delete from table where id not in (select * from (select max(id) from table group by duplicate having count(duplicate) > 1) as b) and id in (select * from (select id from table group by duplicate having count(duplicate) > 1) as c);
    ```

4. MySQL语句的性能问题
    MySQL语句的性能尤为重要，尤其是对于千万级记录的表。碰到一个问题，有一个删除语句的使用 `TO_DAYS(NOW()) - TO_DAYS(create_time) > 14` 条件删除14天前的数据，因为数据有2000万条以上，所以执行起来特别慢，使得同一机器的其他数据表表的查询速度特别慢。将`create_time` 的类型从 `datetime` 修改为 `date`，并将语句修改为 `create_time > 'year-month-day'`