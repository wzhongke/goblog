---
title: XPath 笔记
date: 2017-11-16
tags: ["other"]
categories: ["other"]
---

XPath 是一门在 XML 文档中查找信息的语言。XPath 用于在 XML 文档中通过元素和属性进行导航。

# XPath 节点
**在XPath中有七种类型的节点：元素、属性、文本、命名空间、处理指令、注释、以及文档节点**

对于下面的XML文档：
```xml
<bookstore>
    <book>
      <title lang="eng">Harry Potter</title>
      <price>29.99</price>
    </book>
    <book>
      <title lang="eng">Learning XML</title>
      <price>39.95</price>
    </book>
</bookstore>
```
节点有： 文档节点`<bookstore>`、元素节点`<price>29.99</price>`、元素节点`lang="eng` 

# XPath 语法
XPath 使用路径表达式在 XML 文档中选取节点。节点是通过沿着路径或者 step 来选取的。
下面列出了最有用的路径表达式:

表达式    | 描述
:--------|:---------------
nodename | 选取此节点的所有子节点
/        | 从根节点选取
//       | 从匹配选择当前节点选择文档中的节点，不考虑其位置
.        | 选取当前节点
..       | 选取当前节点的父节点
@        | 选取属性

实例如下：
路径表达式    | 结果
:-----------|:-------------
bookstore   |选取bookstore元素所有的子节点
bookstore/book | 选取bookstore子元素的所有book元素
//book      | 选取所有book元素，而不考虑其位置
bookstore//book |  选择bookstore元素后代中所有book元素，而不考虑其位置 
//@lang     | 选取名为lang的所有属性  

## 谓语
谓语用来查找某个特定的节点或者包含某个指定值的节点，即指定条件。谓语需要用 中括号 嵌套：

路径表达式             | 结果
:--------------------|:--------------
/bookstore/book[1]   | 选取属于 bookstore 子元素的第一个 book 元素
/bookstore/book[last()] | 选取属于 bookstore 子元素的最后一个 book 元素
/bookstore/book[last()-1] | 选取属于 bookstore 子元素的倒数第二个 book 元素
/bookstore/book[position()<3] | 选取最前面两个属于 bookstore元素的book子元素
//title[@lang]  | 选取所有拥有名为 lang 的属性的 title 元素
//title[@lang='eng'] |    选取所有 title 元素，且这些元素拥有值为 eng 的 lang 属性
/bookstore/book[price>35.00] | 选取 price元素大于35.00的 bookstore 下的 book 元素
/bookstore/book[price>35.00]/title  | 选取 price 元素大于 35.00 的 bookstore 下的 book 元素的所有 title元素

## 选取未知节点
XPath 可以用通配符来选取未知的XML元素

通配符  | 描述
:------|:-------
\*     | 匹配任何元素节点
@\*    | 匹配任何属性节点
node() | 匹配任何任性的节点

示例如下：

路径表达式  | 结果
:----------|:-----------
/bookstore/\* | 选取 bookstore 元素的所有子元素
//\*       | 选取文档中所有元素
//title[@\*]| 选取所有带属性的title 元素

## 使用'|' 来选取多个路径
通过在路径表达式中使用 '|' 运算符，可以选取多个路径：
//book/title | //price 选取book元素的所有title子元素和所有 price 元素

# XPath Axes
轴可以定义相对于当前节点的节点集:

轴名称       | 结果
:-----------|:--------
ancestor    | 选取当前节点的所有先辈节点
ancestor-or-self | 选取当前节点的所有先辈（父、祖父等）以及当前节点本身
attribute   | 选取当前节点的所有属性
child       | 选取当前节点的所有子元素
descendant  | 选取当前节点的所有后代元素
descendant-or-self | 选取当前节点的所有后代元素（子、孙等）以及当前节点本身。
following   | 选取文档中当前节点的结束标签之后的所有节点
parent      | 选取当前节点的父节点
preceding   | 选取文档中当前节点开始标签之前的所有节点
self        | 选取当前节点

## 位置路径表达式
我们通过XPath轴可以很方便的获取子或父元素。XPath 轴可以的使用方式如下：

路径表达式     | 结果
:------------|:--------------
**child::book**  | 选取所有属于当前节点的子元素的book节点
**attribute:lang** | 选取当前节点的 lang 属性
**child::\***    | 选取当前节点的所有子元素
**attribute::\***| 选取当前节点的所有属性
**child::text()**| 选取当前节点的所有文本子节点
**child::node()** | 选取当前节点的所有子节点
**descendant::book** | 选取当前节点的所有book后代
**ancestor::book** | 选取当前节点的所有book先辈
**ancestor-or-self::book**| 选取当前节点的所有book先辈及当前节点
**child::\*/child::price** | 选取当前节点的所有 price 孙节点

# XPath 运算符

运算符   | 描述 | 实例 | 返回值
:------|:-------|:-------|:-------
\|     | 计算两个节点集 | //book \| //cd |  返回所有拥有 book 和 cd 元素的节点集
+      |  加法 | 6 + 4 |  10
-      | 减法 | 6 - 4 |  2
*   |乘法 | 6 * 4  | 24
div| 除法 | 8 div 4 | 2
=  | 等于 | price=9.80 | 如果 price 是 9.80，则返回 true。
!= | 不等于 | price!=9.80 | 如果 price 是 9.90，则返回 true。
<  | 小于 | price<9.80 |如果 price 是 9.00，则返回 true。
<= | 小于或等于  | price<=9.80 | 如果 price 是 9.00，则返回 true。
\> |  大于 | price>9.80  |如果 price 是 9.90，则返回 true。
\>= |  大于或等于  | price>=9.80 | 如果 price 是 9.90，则返回 true
or | 或 |  price=9.80 or price=9.70 | 如果 price 是 9.80，则返回 true; 如果 price 是 9.50，则返回 false。
and | 与 |  price>9.00 and price<9.90  | 如果 price 是 9.80，则返回 true; 如果 price 是 8.50，则返回 false。
mod |计算除法的余数| 5 mod 2| 1