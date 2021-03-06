---
title: sed和awk
date: 2017-09-01 14:20:00
tag: ["linux"]
categories: ['linux']
---

`awk`和`sed`有很多共同点：
- 使用相似的语法
- 面向流的，从文本文件中一次读取一行并将处理结果输出到标准输出
- 使用正则表达式来进行模式匹配
- 允许在脚本文件中指定命令
这是因为`awk`和`sed`都可以追溯到原始的UNIX行编辑器--`ed`。vim中使用的命令与`ed`非常相似

# `ed`
使用行编辑器，需要知道每次处理都是以行为单位的。重要的是知道当前处理的是文件的哪行。但是用`ed`打开文件时，控制台展示了文件的字符数，并且指向最后一行。
```bash 
$ ed test
23
```
`ed`命令下没有提示符。如果输入错误的命令，`ed`会返回一个问号。可以使用`p`来显示当前行。
默认情况下，一个命令只对当前行生效。可以通过输入行号来跳转到指定的行。可以通过`1d`命令来删除第一个行(同vim一样)
还可以使用正则表达式来指定地址。删除所有包含有"regular"的行，可以通过执行命令`g/regular/d`
替换命令在`ed`中如下：
```
[address]s/pattern/replacement/flag
```
其中`[address]`地址限制命令可以用数字，正则表达式来表示
可以像下面使用替换命令
```bash
# 用 complex 替换当前行的 <b>第一个</b> regular 
s/regular/complex
# 用complex 替换当前行所有的regular
s/regular/complex/g
# 用 complex 替换 含有regular的所有行的regular
# 第一个 /regular 是行地址选择
/regular/s/regular/complex/g
# 替换所有的regular为complex
g/regular/s/regular/complex/g
# 当 正则表达式和要被替换的内容相同时，可以简写为
g/regular/s//complex/g
```
UNIX下的`grep`命令可以用`ed`的 `g/regular/p` 来实现。
`ed`可以执行操作集合的文件，将要执行的命令放到一个文件中，通过指定它们为行编辑器的输入，就可以执行相应的命令
```bash
ed test < edscript
## edscript 中的内容可以是
/wz/
p
```

# `awk`和`sed`中的正则表达式
## 元字符汇总
元字符   | 含义
:--------|:-----
.        | 匹配换行符以外的任意单个字符，awk中能匹配换行符
*        | 匹配任意一个在它前面的字符
[...]    | 匹配方括号字符中的任意一个，如果第一个字符是^，则表示不匹配其中字符的任意一个
^        | 如果作为正则表达式的第一个字符，则表示匹配行的开始
&        | 如果作为正则表达式中的最后一个字符，表示匹配行的结束
\\{n, m\\}| 匹配前面某个范围内单个字符出现的词数
\\      | 转义字符

## 扩展的元字符(egrep 和 awk)
扩展字符 | 含义
:--------| :-----------
+        | 匹配前面的正则表达式一次或多次
?        | 匹配前面的正则表达式零次或一次
\|       | 指定可以匹配前面或者后面的表达式
()       | 分组
{n,m}    | 匹配前面某个范围内单个字符出现的次数


# `sed`
使用`sed`可以将类似vim编辑器手动的操作过程提取出来，编写成一个可以执行的脚本来实现

## `sed`的基本用法

```bash
sed [-nefr] [动作]
    -n: 使用安静模式，一般情况下，所有来自STDIN的数据都会输出到屏幕上，加上-n，则只有经过sed特殊处理的那行才会被列出来
    -e: 直接在命令行模式上进行sed的动作编辑
    -f: 直接将sed的动作写在一个文件内，-f filename 可以执行filename中的sed动作
    -r: sed的动作执行的是扩展型的正则表达式语法
    -i: 直接修改读取的文件内容，而不是输出到屏幕上
## 动作说明
[n1 [,n2]] function
    n1, n2: 可选参数，表示进行动作的行数。如 10,20动作， 会在10到20行执行动作
## function可以是下面的参数
    a: 新增，a后接字符串，字符串会添加到当前行的下一行
    c: 替换，c后接字符串，这些字符串替换n1, n2之间的内容
    d: 删除
    i: 插入，i后接字符串，字符串会插入到当前行的前一行
    p: 打印
    s: 替换，后接正则表达式，执行行内的替换工作
```
示例如下：
```bash
# 删除2到8行
cat filename | sed '2,8d'
# 在第2行后新增内容
cat filename | sed '2a append string'
# 在第2行前新增内容
cat filename | sed '2i append string'
# 增加多行内容，使用 \ 加 回车换行
cat filename | sed '2a append string  \
second line'
# 输出文件的第2到8行，需要加上 -n，否则2到8行会重复输出
cat filename | sed -n '2,8p'
# 替换字符串
sed 's/replaced string/new string/g'
# 将文件filename中的每行结尾的 . 替换成 !
sed -i 's/\.$/\.!/g' filename
```

`sed`可以用来做：
1. 对一个或者多个文件自动执行编辑操作
2. 简化对多个文件执行相同的编辑操作
3. 编写转换程序

`sed`工作的三个基本原理：
1. 脚本中的所有编辑命令都将依次应用于每个输入行
2. 命令应用于所有行，除非寻址限制
3. 原始的输入文件未改变

## `sed`脚本
`sed` 将文件中每一行都顺序执行脚本内的命令。`sed`总是处理行的最新内容，因此生成的任何编辑工作都会改变后续命令应用的行。
```
s/pig/cow/g
s/cow/horse/g
```
上面的命令会将 pig 替换成 cow，再将cow替换成horse，也就是将pig和cow都替换成horse。所以这么写是有问题的。

`sed`可以用地址限制命令来告诉它哪一行需要相关的操作 `/pig/s/cow/horse/g`: 行中有pig，才将cow替换为horse

`sed`命令最多可以指定两个地址，地址可以是描述模式、行号或者行寻址符号的正则表达式
- 如果没有指定地址，那么命令将用于每行
- 如果只有一个地址，命令应用于于地址匹配的任意行
- 如果指定了由逗号分隔的两个地址，那么命令会应用于第一个地址和第二个地址之间的行
- 如果地址后边有感叹号(!)，那么命令就应用于不匹配改地址的所有行

```bash
# 删除第一行
1d
# 删除最后一行
$d
# 删除空行
/^$/d
# 删除第一行到第一个空行中的所有行
1,/^$/d
```
可以通过如下方式执行脚本，并将结果保存到临时文件中
```bash
sed -f sedscr $x > tmp.$x
done
```

### 分组命令
`sed`使用大括号见给一个地址嵌套在另一个地址中，或在相同的地址上应用多个命令。
```bash
# <b>左大括号可以放在行末，但右大括号必须单独占一行。大括号之后没有空格</b>
## .TS 和 .TE 标记了tb1输入
## 删除tb1输入中的空行，并执行替换命令
/^\.TS/,/^\.TE/ {
    /^$/d
    s/^\.ps 10/.ps 8/
    s/^\.vs 12/.vs 10/
}
```


`sed`只能用来执行脚本，没有像`ed`的交互功能。`sed`不会更改输入文件，如果想要改变输入文件，可以用流的重定向功能。


# `awk` - 可编程编辑器

## `awk`的基本使用

`awk`命令格式如下：
```bash
# 所有的动作都用 单引号 包裹
awk '条件类型1 {动作1} 条件类型2 {动作2} ...' filename
```
`awk` 后可以接文件，也可以作为管道命令使用。其处理流程大概为：
1. 读入第一行，并将第一行的数据填入到$0, $1, $2等变量中
2. 依据条件类型限制，判断后续动作是否要执行
3. 做完所有的动作与条件类型
4. 重复1-3，直到所有的数据处理完
`awk`中有如下内置变量
变量名     | 含义
:----------|:---------
NF         | 每行拥有的字段总数
NR         | 目前awk处理的是行号
FS         | 目前的分隔符，默认是空格

例子：
```bash
cat filename | awk 'BEGIN {FS=":"} $3 < 10 {print $1 "\t" $3}'

cat filename | awk '{if (NR==1) printf "%10s %10s %10s\n", $1,$2,$3, "Total"}
                    NR>=2 {total = $2+$3 printf "%10s %10s %10s\n", $1,$2,$3, total}'
```



`awk` 将文本编辑的功能扩展到计算，可以进行大多数的数据处理，像分析，提取和数据报告。
`awk` 最独特的特性是它将输入的每行解析成可用于脚本处理的单个字。
使用`awk` 可以做的事有：
- 将文本文件看做是有record和field的数据库
- 可以使用变量操作数据库
- 可以使用数学和字符串运算符
- 可以使用条件和循环等编程结构
- 生成格式化的报表
- 定义函数
- 执行脚本
- 处理UNIX命令的结果
- 更优雅地处理命令行参数
- 轻松地处理多输入流


命令行语法是 `commend [options] script filename`