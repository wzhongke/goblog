---
title: mysql
draft: true
---

# 配置MySQL
1. 运行MySQL配置向导文件： $mysql-path/bin/MySQLInstanceConfig.exe
2. 修改MySQL的配置文件： $mysql-path/my.ini
    修改编码方式：
     ```
     [mysql] 客户端配置
    default-character-set=utf8
    [mysqld]  服务器端存储配置
    character-set-server=utf8
     ```

# 启动/关闭MySQL
1. linux 启动 `service mysql start`，关闭服务 `service mysql stop`
2. Windows 启动`net start mysql`， 关闭服务 `net stop mysql`

# 登录MySQL
MySQL登录参数

参数                | 描述
:------------------| :----------------
-D --database=name | 打开指定的数据库
--delimiter=name   | 指定分隔符
-h, --host=name    | 服务器地址或域名
-p, --password[=name] | 密码
-P， --port=3306    | 端口号
--prompt=name       | 设置提示符
-u, --user=name    | 用户名
-V, --version      | 输出版本信息

```bash
# 使用 地址+端口号 登录
mysql -p -P3306 -hlocalhost
```

退出MySQL ： `exit;`  或 `quit;` 或  `\q;`

修改MySQL的提示符：

参数         | 说明
:-----------| :----------
\h | 服务器的名称
\D | 完整的日期
\u | 当前用户
\d | 当前的数据库

```bash
mysql -uroot -p --prompt 提示符
// 连接上客户端之后
mysql> prompt 提示符
```



## 创建数据库
```sql
CREATE {DATABASE | SCHEMA} [IF NOT EXISTS] db_name [DEFAULT] CHARACTER SET [=] charset_name
```
实例：
```sql
CREATE DATABASE test;
CREATE DATABASE IF NOT EXISTS test1 CHARACTER SET gbk;
```

## 修改数据库
命令格式：
```sql
ALTER {DATABASE | SCHEMA} [db_name] [DEFAULT] CHARACTER SET [=] charset_name;
```
示例：
```sql
ALTER DATABASE test2 CHARACTER SET = utf8
```

## 删除数据库
```sql
DROP {DATABASE | SCHEMA} [IF EXISTS] db_name;
```


## 创建表
```sql
CREATE TABLE [IF NOT EXISTS] table_name (
    column_name data_type,
    ...
)
```

## 插入记录 `INSERT`
`AUTO_INCREMENT` 是自动编号，且必须与主键组合使用，默认情况下，起始值为1，每次的增量为1


```sql
INSERT [INTO] ta_name [(col_name, ...)] VALUES (val, ...) [, (s_val, ...)]
-- 如果主键相同，则替换之
REPLACE [INTO] ta_name [(col_name, ...)] VALUES (val, ...) [(s_val, ...)]
```

## 查找 `SELECT`
### 简单的查找
```sql
SELECT expr, ...    FROM tb_name;
```


## 约束
1. 约束是为了保证数据的完整性和一致性
2. 约束分为表级约束和列级约束
3. 约束有五种类型： 非空约束(NOT NULL)，主键约束(PRIMARY KEY)，唯一约束(UNIQUE KEY)，默认约束(DEFAULT), 外键约束(FOREIGN KEY)

### MySQL主键约束 (PRIMARY KEY)
主键（PRIMARY KEY）约束：
- 每张数据表只能存在一个主键
- 主键保证记录的唯一性
- 主键自动为NOT NULL

### 唯一约束 (UNIQUE KEY)
- 唯一约束可以保证记录的唯一性
- 唯一约束的字段可以为空值 (NULL)
- 每个数据表可以存在多个唯一约束

### 默认约束 (DEFAULT)
当插入记录时，如果没有为字段赋值，则自动赋予默认的值

```sql
CREATE TABLE tb_name (
    id smallint unsigned auto_increment primary key,
    username varchar(20) unique key,
    sex enum('1','2','3') default '3'
);
```

### 外键约束
外键列有如下要求：
1. 父表与子表必须使用相同的存储引擎，而且禁止使用临时表
2. 数据表的存储引擎只能用 InnoDB
3. 外键列和参考列必须具有相似的数据类型。其中数字的长度或是否有符号位必须相同；字符串的长度可以不同
4. 外键列和参考列必须创建索引。如果外键列不存在索引，MySQL将会自动创建索引。 

外键语法： `FOREIGN KEY (column_child) REPERENCES tb (column_parent)`， 其中 `tb` 是父表

外键约束的参照操作：
1. CASCADE: 从父表删除或更新且自动删除或更新子表中匹配的行
2. SET NULL: 从父表删除或更新行，并设置子表中的外键列为NULL。使用该选项，必须保证子表列没有指定NOT NULL
3. RESTRICT: 拒绝对父表的删除或更新操作
4. NO ACTION: 在MySQL中同RESTRICT相同

```sql
CREATE TABLE tb_name (
    id smallint unsigned auto_increment primary key,
    username varchar(20) unique key,
    sex enum('1','2','3') default '3',
    pid varchar(20) not null,
    foreign key (pid) references tb (id) on delete cascade
);
```

### 列级约束和列级约束
对一个数据列建立的约束，称为列级约束
对多个数据列建立的约束，称为表级约束
列级约束既可以在列定义时声明，也可以在列定义后声明；表级约束只能在列定义后声明