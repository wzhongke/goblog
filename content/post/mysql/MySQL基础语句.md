---
title: MySQL基础语句
date: 2017-10-08 19:48:32
tags: ["MySQL"]
categories: ["MySQL"]
---

# MySQL数据库

MySQL 是典型的关系型数据库，因为其免费开源，所以被广泛的应用
数据库是按照数据结构来组织、存储和管理数据的仓库当然我们也可以将数据存储在文件中，但是文件中读写数据速度相对较慢所以，我们使用关系型数据库管理系统（RDBMS）来存储和管理数据关系型数据库，是建立在关系模型基础上的数据库，通过集合代数等数学概念和方法来处理数据库中的数据。
<!-- more -->
RDBMS (Relational Database Management System)的特点：
1. 数据以表格的形式出现
2. 每行为各种记录名称
3. 每列为记录名称所对应的数据域
4. 许多行和列构成一张表
5. 若干个表组成数据库

## 数据库术语
- 数据库： 数据库是关系型数据表的集合
- 数据表： 表示数据的矩阵
- 列： 一列包含了相同类型的数据
- 行： 一行是一组相关的数据
- 冗余： 存储两倍数据，冗余降低性能，但提高了数据的安全性
- 主键： 主键是唯一的，一个数据表中只能包含一个主键
- 外键： 外键用于将两个表关联起来
- 复合键： 复合键将多个列作为一个索引键，一般用于符合索引
- 索引： 使用索引可以快速访问数据表中的信息一般索引使用B+ Tree对数据表中的一列或多列进行排序

# MySQL特殊命令
1. 按ip查看连接: `select SUBSTRING_INDEX(host,':',1) as ip , count(*) from information_schema.processlist group by ip;`
2. 查看执行数据库执行列表: `show processlist;`
3. 查看索引使用情况: `explain SQL;`

# MySQL简单的命令
1. 查看当前服务器版本： `SELECT VERSION();`
2. 显示当前日期： `SELECT NOW();`
3. 显示当前用户： `SELECT USER();`
4. 查看所有的数据库： `SHOW DATABASES;`
5. 切换数据库： `USE db_name;`
6. 查看警告信息： `SHOW WARNINGS;`
7. 查看数据库表的创建信息： `SHOW CREATE db_name;`
8. 查看当前打开的数据库： `SHOW DATABASE();`
9. 查看数据库中的表: `SHOW TABLES [FROM db_name] [LIKE 'pattern' | WHERE expr];`
10. 查看数据库表的结构: `SHOW COLUMNS FROM tb_name`
11. 查看数据表的创建语句： `SHOW CREATE TABLE tb_name;`
12. 查看索引: `SHOW INDEXES FROM tb_name;`
13. 删除表： `DROP TABLE tb_name;`
14. 删除数据库： `DROP DATABASE db_name;`

# MySQL 数据类型
数据类型是指列、存储过程参数、表达式和局部变量的数据特征，它决定了数据的存储格式，代表了不同的信息类型
## 整型
可以根据下表选择需要的整型类型，既能保证能存储所需数值，又能节省空间 `UNSIGNED` 无符号值。

数据类型   | 存储范围              | 字节
:--------|:---------------      |:-------------
tinyint   | 有符号值： -128到127 (-2^7 到 2^7-1) <br> 无符号值： 0到255 (0到2^8-1)   | 1字节
samllint  | 有符号值： -32768到32767 (-2^15 到 2^15-1) <br> 无符号值： 0到65535 (0到2^16-1)   | 2字节
mediumint | 有符号值： -2^23 到 2^23-1 <br> 无符号值： 0到2^24-1   | 3 字节 (3*8)
int       | 有符号值： -2^31 到 2^31 -1 <br> 无符号值： 0 到 2^32-1 | 4 字节
bigint    | 有符号值： -2^63 到 2^63 -1 <br> 无符号值： 0 到 2^64-1 | 8 字节

## 浮点型
数据类型               |  存储范围
:----------------| :-----------
float[(M,D)]     |-3.402823466E+38 到 -1.175494351E-38、0 和 1.175494351E-38 到 3.402823466E+38 <br> M 是数字的总位数，D是小数点后边的位数如果没有M和D，那么根据硬件允许的限制来保存值单精度浮点数精确到大概7位小数
double[(M,D)]    | -1.7976931348623157E+308 到 -2.2250738585072014E-308、 0 和 2.2250738585072014E-308 到 1.7976931348623157E+308

## 日期类型
数据类型      | 存储需求
:------------| :-----
year        | 1
time        | 3
date        | 3
datetime    | 8
timestamp   | 4

## 字符型

数据类型                  | 存储需求
:----------------------- |:----------
char(M)                  | M 个字节，定长类型， 0 <= M <= 255
varchar(M)              | L+1个字节(变长类型)，L<=M && 0 <= M <= 65536
tinytext                | L+1个字节，L < 2^8
text                    | L+2 个字节，L < 2^16
mediumtext              | L+3 个字节，L < 2^24
longtext                | L+4 个字节，L < 2^32
enum('value1', 'value2', ...) | 1或2个字节，取决于枚举值的个数 (最大65535个值)
set('value1','value2', ...) | 1,2,3,4或8个字节，取决于set成员的数目(最多64个成员)

# 创建表
以下是MySQL创建数据表的通用语法：
```sql
CREATE TABLE [IF NOT EXISTS] table_name (
    column_name data_type,
    ...
);
```
下例子中，我们创建了一张 tb_name 的表
```sql
CREATE TABLE IF NOT EXISTS `tb_name` (
    `id` INT UNSIGNED AUTO_INCREMENT,
    `name` VARCHAR(20) NOT NULL,
    `birthday` DATE,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 结果：受影响的行: 0，时间: 0.042s
```

解释：
1. 如果不想将字段的值为NULL，可以设置字段的属性为 `NOT NULL`，这样在插入数据时，如果输入该字段为 NULL，那么数据库就会报错
2. `AUTO_INCREMENT` 定义列为自增属性，一般用于主键
3. `PRIMARY KEY` 用于定义列为主键，可以定义过个列为主键，用逗号分隔
4. `ENGINE` 用来设置存储引擎，`CHARSET` 设置编码

## 插入数据
MySQL 通过 `INSERT INTO` 或者 `REPLACE INTO` 来插入数据，语法如下：
```sql
INSERT [INTO] ta_name [(field1, field2,...fieldN)] VALUES (val1, val2, ..., valN) [, (val1, val2, ..., valN)];
-- 如果主键相同，则替换之
REPLACE [INTO] ta_name [(field1, field2,...fieldN)] VALUES (val1, val2, ..., valN) [, (val1, val2, ..., valN)];
```
如果数据是字符型，必须使用双引号或者单引号，如 "val"
下例中，我们向数据表中插入一条数据：
```sql
INSERT INTO tb_name (`name`, `birthday`) VALUES ("Jhon", "1991-11-23");
-- 结果: 受影响的行: 1，时间: 0.003s
```
如果 `field` 的名字同 MySQL 关键字的名字相同，需要使用 `\`` 来将 `field` 括起来

如果需要从另一张表中导入数据到一张表，可以用 `INSERT INTO ... SELECT`，其语法如下：
```sql
-- 将tb2中所有的列都拷贝到tb1中
INSERT INTO tb1 
SELECT * FROMT tb2
WHERE condition;
-- 将特定的列从tb1中拷贝到tb2中
INSERT INTO tb1 (column1, column2,..., columnn) 
SELECT column1, column2,..., columnn 
FROM tb2 
WHERE condition;
```
拷贝指定的列时，`INSERT` 后的列要一一对应。


## 简单查询数据
MySQL 通过 SELECT 语句来查询数据，语法如下：
```sql
SELECT col_name, col_name,... FROM tb_name [WHERE clause] [OFFSET m] [LIMIT n];
```
SELECT 说明：
- 查询语句中可以使用一个或者多个表，表之间用逗号分隔， `WHERE` 用来设定查询语句
- 使用 * 来代替其他字段，SELECT 语句会返回表中所有的字段数据
- 使用 `OFFSET` 来指定 SELECT 语句开始查询的数据偏移量
- 使用 `LIMIT` 来设定返回的记录数

```sql
SELECT * FROM tb_name;
-- 结果：
 +----+------+------------+
| id | name | birthday   |
+----+------+------------+
|  1 | Jhon | 1991-11-23 |
+----+------+------------+
1 row in set (0.00 sec)
```

### CASE WHEN

可以在 `select` 的列中使用 `case when condition then val1 else val2 end` 来进行查询操作

```sql
INSERT INTO `db1`.`table1` (`keyword`, `match_type`, `type`, `create_time`, `modify_time`, `start_time`, `end_time`, `user`) 
SELECT 
    sugg_word as keyword, 
    (CASE WHEN fuzz=0 THEN 0 ELSE 1 end) AS match_type,
    (CASE WHEN sugg_case='' THEN 0 ELSE 1 END) AS type,
    create_time, modify_time, start_time, end_time, user 
from `db2`.`table2` where sugg_case='';
```

## UPDATE 更新命令
如果我们要更改MySQL中的数据，那么我们需要使用 UPDATE 命令，其语法如下：
```sql
UPDATE tb_name SET field1=newValue1, field2=newValue2, ... [WHERE clause];
```

`UPDATE` 语句说明：
- 可以同时更新多个字段；
- 可以在 WHERE 子句中指定任何条件
- 可以在一个单独表中同时更新数据

示例：
```sql
UPDATE tb_name SET birthday='2000-10-01' WHERE name='Jhon';
-- 结果如下
Query OK, 1 row affected (0.00 sec)
Rows matched: 1  Changed: 1  Warnings: 0
```

## DELETE 语句
可以使用 DELETE 来删除 MySQL 表中的记录，语法如下：
```sql
DELETE FROM tb_name [WHERE clause];
```

DELETE 语句会删除所有命中 `WHERE` 条件的记录。
`DELETE` 说明：
- 如果没有指定 WHERE 子句， MySQL 表中的所有记录都会被删除
- 可以在 WHERE 子句中指定任何条件
- 可以在表单中一次性删除记录


## 查询

### `WHERE` 子句
`WHERE` 子句是有条件地从表中筛选数据，语法如下：
```sql
SELECT field1, field2,...fieldN FROM tb_name1, tb_name2...
[WHERE condition1 [AND [OR]] condition2.....
```
`WHERE` 子句说明：
- 查询语句中可以指定一个或多个表，表之间用逗号分隔
- 在 WHERE 子句中指定任何条件，使用 AND 或 OR 来分隔不同的条件
- WHERE 可以用在 SQL 的 DELETE 或者 UPDATE 命令中

`WHERE` 子句中可以使用如下操作符，其中 A=10， B=20, s='test'：

操作符 | 描述            | 实例
:-----|:----------------|:--------
=     | 等号，检测两个值是否相等，如果相等返回true     | (A = B) 返回false
<>, !=|  不等于，检测两个值是否相等，如果不相等返回true |  (A != B) 返回 true
\>    | 大于号，检测左边的值是否大于右边的值, 如果左边的值大于右边的值返回true | (A > B) 返回false
<     | 小于号，检测左边的值是否小于右边的值, 如果左边的值小于右边的值返回true | (A < B) 返回 true
\>=   |  大于等于号，检测左边的值是否大于或等于右边的值, 如果左边的值大于或等于右边的值返回true |  (A >= B) 返回false
<=    |小于等于号，检测左边的值是否小于于或等于右边的值, 如果左边的值小于或等于右边的值返回true  | (A <= B) 返回 true
IS [NOT] NULL | 判断值是否为空   | (`s IS NOT NULL`) 返回false
LIKE  | 字符串模糊匹配 | (`s LIKE '%es_'`) 返回 true
REGEXP| 正则匹配      | (`s REGEXP '^t[aec]{1,3}$'`) 返回true

### LIKE 子句
LIKE 子句如果没有 `%` 和 `_`， 其效果同 `=` 相同。
在 LIKE 中，`%` 代表任意个数的任意字符； `_` 代表任意一个字符。

示例：
```sql
SELECT * FROM tb_name WHERE NAME LIKE 'A%';
-- 结果：
+----+------+------------+
| id | name | birthday   |
+----+------+------------+
|  6 | Andy | 2009-09-01 |
+----+------+------------+
```

**`WHERE` 子句中的字符串不区分大小写，可以使用 `BINARY` 来设定字符串区分大小写**： `WHERE BINARY NAME LIKE 'A%';`

### UNION 操作符
UNION 操作符用于连接两个以上的 SELECT 语句，它将 SELECT 语句的结果组合到一个结果集中，多个 SELECT 语句会删除重复的数据。语法如下：
```sql
SELECT exp1, exp2, ..., expn FROM tb_name 
[WHERE condition] 
UNION [ALL | DISTINCT]
SELECT exp1, epx2, ..., expn FROM tb_name 
[WHERE condition];
```

语句说明：
- DISTINCT: 可选，删除结果集中重复的数据，默认为 DISTINCT
- ALL: 可选，返回所有的结果集，包括重复数据
- SELECT 所选的列的个数应该是相等的

```sql
SELECT `name`, `birthday` FROM tb_name WHERE name LIKE 'D%'
UNION ALL
SELECT `name`, `birthday` FROM person WHERE name like 'D%';
-- 结果
+-------+------------+
| name  | birthday   |
+-------+------------+
| Devon | 2009-09-01 |
| Dewey | 1990-12-01 |
| Devon | 1990-12-01 |
| Dewey | 1990-12-01 |
+-------+------------+

SELECT `name`, `birthday` FROM tb_name WHERE name LIKE 'D%'
UNION 
SELECT `name`, `birthday` FROM person WHERE name like 'D%';
-- 结果
+-------+------------+
| name  | birthday   |
+-------+------------+
| Devon | 2009-09-01 |
| Dewey | 1990-12-01 |
| Devon | 1990-12-01 |
+-------+------------+
```

### 排序
可以通过 SELECT 将数据从数据库中取出，还可以通过 ORDER BY 子句对查询结果进行排序。语法如下：
```sql
SELECT field1, field2,..., fieldn FROM tb_name [WHERE condition] ORDER BY field1, [field2...] [ASC [DESC]];
```

ORDER BY 使用说明：
- 可以使用任何字段作为排序的条件
- 可以设定多个字段来排序，会按字段的先后顺序来对结果进行排序
- ASC 是排列升序，DESC 是降序排列，默认情况下是升序

示例：
```sql
SELECT `name`, `birthday` FROM tb_name WHERE NAME LIKE 'D%' ORDER BY birthday;
-- 结果
+-------+------------+
| name  | birthday   |
+-------+------------+
| Dewey | 1990-12-01 |
| Devon | 2009-09-01 |
+-------+------------+

SELECT `name`, `birthday` FROM tb_name WHERE NAME LIKE 'D%' ORDER BY birthday DESC;
+-------+------------+
| name  | birthday   |
+-------+------------+
| Devon | 2009-09-01 |
| Dewey | 1990-12-01 |
+-------+------------+
```

### 分组查询
我们可以使用 GROUP BY 语句根据一个或多个列队结果集进行分组。在列上我们可以使用 COUNT、SUN、AVG等聚合函数。语法如下：
```sql
SELECT field1, function(field2), ... FROM tb_name 
[WHERE condition] 
GROUP BY field1 [WITH ROLLUP]
[ORDER BY field1];
```

说明：
- WITH ROLLUP : 可以实现在分组统计的基础上在进行相同的统计(SUM, AVG, COUNT)
- COALESCE(a,b,c): 如果a==null,则选择b；如果b==null,则选择c；如果a!=null,则选择a；如果a b c 都为null ，则返回为null（没意义）

示例：
```sql
SELECT COUNT(`name`), `birthday` FROM tb_name GROUP BY birthday  ORDER BY birthday DESC;
-- 结果
+---------------+------------+
| COUNT(`name`) | birthday   |
+---------------+------------+
|             4 | 2009-09-01 |
|             1 | 2004-06-16 |
|             1 | 2000-10-01 |
|             1 | 1990-12-01 |
+---------------+------------+

SELECT COALESCE(birthday, '总数'), SUM(name) AS name_count FROM tb_name GROUP BY birthday WITH ROLLUP;
-- 结果
+------------------------------+------------+
| COALESCE(birthday, '总数') | name_count |
+------------------------------+------------+
| 1990-12-01                   |          1 |
| 2000-10-01                   |          1 |
| 2004-06-16                   |          1 |
| 2009-09-01                   |          4 |
| 总数                       |          7 |
+------------------------------+------------+
```

### MySQL NULL 值处理
WHERE 子句在使用查询条件时，如果查询条件的字段为NULL，会有些特殊：
- `IS NULL`: 若当前列的值为 NULL，则返回true
- `IS NOT NULL`: 当前列的值不为NULL，返回true
- `<=>`: 比较操作符，当比较的两个值为 NULL 时返回true

NULL 条件比较运算比较特殊，不能使用 `=NULL` 或 `!=NULL` 在列中查找 NULL 值。 在 MySQL中， NULL值与任何其他值的比较永远返回 false，`NULL=NULL` 也会返回false。
** MySQL中处理 NULL 使用 IS NULL 和 IS NOT NULL**

### MySQL 正则表达式
MySQL 除了可以用 `LIKE` 来进行模糊匹配外，还可以用 `REGEXP` 来进行正则匹配。其正则匹配的模式同Perl正则匹配相似
```sql
-- 选出 以 D 或 J 开头的，并且以 n 结尾的姓名
SELECT * FROM tb_name WHERE name regexp '^[DJ].*n$';
-- 结果
+----+-------+------------+
| id | name  | birthday   |
+----+-------+------------+
|  1 | Jhon  | 2000-10-01 |
|  2 | Devon | 2009-09-01 |
+----+-------+------------+
```

## 修改表结构
当我们需要修改数据库的表明或者修改数据表字段时，可以通过MySQL的 ALTER 命令来修改。
1. 删除表中的字段： `ALTER TABLE tb_name DROP field;`
2. 使用 ADD 来向数据表中添加列： `ALTER TABLE tb_name ADD sex INT;`
3. 修改字段类型及名称： `ALTER TABLE tb_name MODIFY sex CHAR(2);`
4. 修改字段的默认值： `ALTER TABLE tb_name MODIFY sex TIYINT NOT NULL DEFAULT 1;`
5. 修改表名： `ALTER TABLE tb_name RENAME TO tb;`
6. 修改存储引擎： `ALTER TABLE tb_name ENGINE=MYISAM;`
7. 删除外键约束：`ALTER TABLE tb_name DROP FOREIGN KEY keyName;`

## 索引
表的索引就像书的目录，能够快速地找到找到数据，能够极大地提高以索引列为条件的查询的效率。
索引分为单列索引和组合索引。单列索引只包含单个列，一个表可以有多个单列索引。组合索引是一个索引包含多个列。
可以把索引看做一张有序表，保存了主键和索引字段，并指向实体表的记录。
虽然索引能够极大地提高查询速度，但是却会降低更新表的速度，如对表进行 INSERT、UPDATE和DELETE。因为更新表时，也需要更新索引。
索引分为 普通索引，唯一索引。 唯一索引列的值必须是唯一的，但允许有空置。如果是组合索引，则列的组合必须唯一。
1. 创建索引: `CREATE [UNIQUE] INDEX indexName NO tb_name(name(lenght))`。如果是 CHAR，VARCHAR类型，length可以小于字段实际长度；如果是BLOB或TEXT类型，必须制定length
2. 修改表结构(添加索引)： `ALERT table tb_name ADD INDEX indexName(columnName)`
3. 创建表时直接指定：
    ```sql
    CREATE TABLE tb_name (
        id INT NOT NULL,
        username VARCHAR(16) NOT NULL,
        ...
        INDEX [indexName] (username(length))
    );
    ```
4. 删除索引： `DROP INDEX [indexName] ON tb_name;`
5. 显示索引信息： `SHOW INDEX FROM tb_name;`

## 临时表
在我们需要保存一些临时数据时，可以使用临时表。临时表只在当前连接可见，关闭连接时，MySQL会自动删除表并释放空间。
临时表的创建同表的创建类似，只是多了一个关键字：
```sql
CREATE TEMPORARY TABLE temporary_table();
```
但是，使用 `SHOW TABLES` 命令时，无法看到临时表。

## 序列
1. 在MySQL的客户端中，使用 `LAST_INSERT_ID()` 函数来获取最后插入表中的自增列的值
2. 可以使用 `auto_increment=100` 来为序列指定一个开始值

## 重复数据的处理
MySQL中允许存在重复的记录，但有些时候我们需要删除一些重复的数据。
可以在MySQL表中，通过将相应的字段设置为 PRIMARY KEY 或者 UNIQUE 索引来保证数据的唯一性：
```sql
CREATE TABLE test1 (
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    PRIMARY KEY(last_name, first_name)
    -- 或者使用 UNIQUE
    UNIQUE (last_name, first_name)
);
```
如果我们设置了唯一索引，那么在插入重复数据时，SQL语句会执行报错。可以使用 `REPLACE INTO` 或者 `INSERT IGNORE INTO`，这两种方式会忽略重复的数据。如果数据库没有数据，就插入新的数据，如果有数据的话就跳过这条数据。这样就可以保留数据库中已经存在数据，达到在间隙中插入数据的目的。

我们还可以统计表中重复的记录：
```sql
SELECT COUNT(*) AS cnt, last_name, first_name FROM test1
GROUP BY last_name, first_name
HAVING cnt > 1;
```

## 导出数据
我们可以使用 `SELECT ... INTO OUTFILE` 来简单地导出数据到文本文件中：
```sql
SELECT * FROM test1 INTO OUTFILE '/tmp/test1.txt';
```
还可以像下例中，指定文件格式：
```sql
SELECT * FROM test1 INTO OUTFILE '/tmp/test1.txt' 
FIELDS TERMINATED BY ',' ENCLOSED BY '"' 
LINES TERMINATED BY '\r\n'
```
