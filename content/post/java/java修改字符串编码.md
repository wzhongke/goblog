---
title: java修改字符串编码
date: 2017-06-18 19:01:11
tags: ["java"]
categories: ["java"]
---

使用java处理接口返回数据时，经常会有编码转换的问题。
一开始以为如果将gbk编码的数据转换为utf8的数据，那么应该使用gbk编码获取数据，再将数据进行utf8编码。
```java
new String("中国".getBytes("gbk"), "utf8")
```
这样得出来的数据总是乱码。
查看`String的getBytes(String charsetName)`源码
<!-- more -->
```java
 public byte[] getBytes(String charsetName)
            throws UnsupportedEncodingException {
        if (charsetName == null) throw new NullPointerException();
        return StringCoding.encode(charsetName, value, 0, value.length);
    }
```
发现该方法是获得使用所传参数编码的byte数组。
因此正确的方式应该是：
```java
new String("中国".getBytes("utf8"), "utf8")
```
