---
title: shell 编程
date: 2017-08-19 09:52:00
tags: ["linux"]
categories: ["linux", "编程"]
---

shell script虽然是程序，但是它处理数据的速度是不够的。因为shell用的是外部的命令bash shell的一些默认工具，所以它经常会调用外部的函数库。
shell 经常被用来管理系统，而不是处理数据。
<!-- more -->
## shell程序执行
有两种方式可以执行shell脚本：
1. 直接命令执行：这种方式要求文件有rx权限
   - 绝对路径 `/home/user/shell.sh`
   - 相对路径 `./shell.sh`
   - 放到`PATH`路径下，直接执行 `shell.sh`
2. 通过`bash shell.sh`或者 `sh shell.sh`来执行
## shell基本说明
```shell
#!/bin/bash
# Program: shell脚本用途说明
# 修改时间，作者相关信息
read -p "name: " name
echo -e "\nname is $name"
```
shell 脚本通常以`#!/bin/bash` 开头，来声明这个文件使用bash语法。在文件执行时，能够加载bash的相关环境配置文件
## test测试命令
test命令经常用来检测文件或者是其相关属性
### 检测文件类型  `test -e filename`
测试标志      | 代表含义
:-------------|:------------------
-e            | 该文件名是否存在
-f            | 该文件名是否存在且为文件
-d            | 该文件名是否存在且为目录
-L            | 该文件名是否存在且为一个连接文件

### 检测文件权限 `test -r filename`

测试标志      | 代表含义
:-------------|:------------------
-r            | 检测文件名是否存在且具有可读权限
-w            | 检测文件名是否存在且具有可写权限
-x            | 检测文件名是否存在且具有可执行权限
-u            | 检测文件名是否存在且具有SUID属性
-s            | 检测文件名是否存在且为 非空白文件

### 两个文件间比较 `test file1 -nt file2`
测试标志      | 代表含义
:-------------|:------------------
-nt           | (newer than) 判断file1是否比file2新
-ot           | (older than) 判断file1是否比file2旧
-ef           | 判断file1与file2是否为同一个文件，可用在hard link的判断上

### 整数之间的判断 `test n1 -eq n2`
测试标志      | 代表含义
:-------------|:------------------
-eq           | 两数值相等
-ne           | 两数值不相等
-gt           | n1 大于 n2
-lt           | n1 小于 n2
-ge           | n1 大于等于 n2
-le           | n1 小于等于 n2

### 判定字符串的数据
测试标志      | 代表含义
:-------------|:------------------
test -z string| 判定字符串是否为0，若string为字符串，则为true
test -n string| 判定字符串是否为0，若string为字符串，则为false, -n可以省略
test st1 = str2 | 判断str1是否等于str2，若相等，则为true
test st1 != str2 | 判断str1是否等于str2，若相等，则为false

### 多条件判定 `test -r filename -a -x filename`
测试标志      | 代表含义
:-------------|:------------------
-a            | 两个条件同时成立
-o            | 任何一个条件成立
!             | 反向状态，not

### 使用判断符号 `[]` 
可以使用`[]`来代替`test`进行判断
```shell
[ -z "$HOME" ]
```
使用`[]`必须要特别注意，在bash语法中使用时，必须要注意中括号内的每个组件都需要有空格键来分隔，中括号中的变量最好都以双引号括起来
## shell脚本的默认变量
向shell脚本中传递参数和获取参数的方法如下：
```
scriptname opt1 opt2 opt3 ...
$0         $1   $2   $3
```
其他的一些参数：
- `$#` : 后接参数个数
- `$@` : 所有的参数
### `shift`
`shift`会移动变量，后面可以跟数字，表示一次移除多少变量

## 条件判断
### `if ... then...`
`if ... then...` 是最常见的条件判断式。
当只有一个判断要进行时，可以使用如下方式：
```shell
if [ 条件测试语句 ]; then
    条件成立
fi
```
可以通过`&&`或者`||`来分隔两个条件判断式，如：
```shell
if [ "$yn" == "Y" || "$yn" == "y"]; then
    "do something"
fi
```
对于多重、复杂条件判断，可以用如下方式：
```shell
if [ 条件判断式一 ]; then
    "do something"
elif [ 条件判断式二 ]; then
    "do something"
else 
    "do something"
fi
```
### 使用 `case...esac`判断
这种判断经常用来做固定字符串来执行某些操作的需求
`case` 语法如下：
```shell
case $变量 in
    "第一个变量内容" )
        "do something"
        ;;   #需要用两个分号结尾
    * ) 
        其他条件执行内容
        ;;
esac
```
## function
shell中函数的语法如下：
```shell
function fname () {
    'do some thing'
}
```
因为shell脚本的执行方式是由下而上的，由左而右的，因此，function的设置一定要在程序的最前面，这样才能在执行时被找到可用的程序段。
function中也有内置变量，函数名为$0, 而后续连接的变量也是以$1, $2... 来代替的。
```shell
#!/bin/bash
function print () {
    echo "Choice is $1" 
}

echo "This program will print your choice: "
case $1 in 
    "one" )
        print 1
        ;;
    "two" )
        print 2
        ;;
    * )
       echo "Usage $0 (one|two)"
       ;;
esac
```
## 循环
### 不定循环：`while do done`, `util do done`
两种方式的循环如下：
```shell
# 当满足条件condition时，执行循环体
while [ condition ]
do
    'do something'
done

# 当不满足条件时执行循环体
until [condition]
do
    'do something'
done
```
自加运算如下：
```shell
s=0
i=0
while [ "$i" != "100" ]
do
    i=$(($i+1))
    s=$(($s+$i))
done
```
### `for...do...done`
语法如下：
```shell
for var in con1 con2 con3 ...
do
    'do something'
done
# 第二种方式
for ( ( 初始值; 限制数; 执行步长 ))
do
    'do something'
done   
```
可以对其他输出内容进行遍历
```shell
users=$( cut -d ':' -f1 /etc/passwd )
for username in $users
do
    id $username
    finger $username
done

# 扫描网络
network="192.168.1"
for sitenu in $(seq 1 100)
do 
    ping -c 1 -w 1 ${network}.${sitenu} &> /dev/null && result=0 || result=1
    if [ "$result" == 0 ]; then
        echo "Server ${network}.${sitenu} is up"
    fi
done

# 数值处理
s=0
nu=100
for ( ( i=1; i<=$nu; i=i+1))
do
    s=$( ($s+$i))
done
```
## shell脚本调试
可以利用shell脚本参数来检查shell的正确性
```shell
sh [-nvx] script.sh
-n: 不执行script，只检查语法
-v: 执行script前，将script内容输出到屏幕上
-x: 将使用到的script内容显示到屏幕上
```

## 环境变量
可以用 `export` 来将自定义变量转换成环境变量