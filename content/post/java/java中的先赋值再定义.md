---
title: 'java中的先赋值再定义'
date: 2015-04-02 11:32:36
tags: ["java", "error"]
categories: ["java", "error"]
---

如下代码
```java
public class MyTest{
    {
        value = 3;
        System.out.println("函数块");
    }
    MyTest(){
        System.out.println("构造函数");
        System.out.println(value);

    }
    public int getValue() {
        return value;
    }
    int value = 0;
}
```
如果value定义在下边，因为函数块是在构造函数之前运行的，也就是value还没有定义，就已经赋值了。
实际应用中应该尽量避免该情况出现，最好把属性定义放在函数开始的位置。


