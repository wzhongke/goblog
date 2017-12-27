---
title: java技巧
date: 2017-12-25 14:50:00
tags: ["java"]
categories: ["java"]
draft: true
---

## `replaceAll(String regex, String replacement)`
`replaceAll`的第一个参数是正则字符串，第二个参数中`\`有特殊含义，必须使用`\\`转义。在SQL查询中，可能需要对SQL的关键字进行转义，即在'%'等前面加上两个反斜杠：
```java
"1fd%kkd_".replaceAll("([%_])", "\\\\\\\\$1");
```
