---
title: "java中没有见过的用法"
date: 2017-07-20 17:16:32
tags: ["java"]
categories: ["java"]
---

# interface & interface
在阅读`Comparator`源码时，无意间发现了如下这种用法：
```java
public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
            Function<? super T, ? extends U> keyExtractor) {
    Objects.requireNonNull(keyExtractor);
    return (Comparator<T> & Serializable)
        (c1, c2) -> keyExtractor.apply(c1).compareTo(keyExtractor.apply(c2));
}
```
<!-- more -->
在这个方法中有 `(Comparator<T> & Serializable)` 这样的用法。经过调研，发现是类型转换的意思，返回的结果被转换成实现`Comparator`和`Serializable`两个接口的实例。
java中做强制转换时，对于类，只能指定一个；对于接口，能够指定无数个。