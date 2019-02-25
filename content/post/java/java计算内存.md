---
title: java 使用代码计算内存
date: 2019-02-22 11:02:00
tags: ["java"]
categories: ["java"]
---

# 原始类型
在java中，原始类型占用的内存大小是固定的：
类型     |  大小 (byte)
:-------|:---------
boolean | 1
byte    | 1
short   | 2
char    | 2
int     | 4
float   | 4
long    | 8
double  | 8


# 对象大小计算
对象大小可以使用 `java.lang.instrument.Instrumentation` 类来计算。改类提供了一个 `getObjectSize` 的方法来计算对象的大小，但是这个方法返回的对象大小中不包括其成员变量所引用的对象。
但是这个方法不能直接调用，必须实现一个 `instrumentation` 代理类并且打包。

定义代理类：
```java
package com;

import java.lang.instrument.Instrumentation;  
import java.lang.reflect.Array;  
import java.lang.reflect.Field;  
import java.lang.reflect.Modifier;  
import java.util.IdentityHashMap;  
import java.util.Map;  
import java.util.Stack;
/** 
 * 借助 Instrumentation 接口的 getObjectSize 方法计算对象占用空间 
 * 原来的 sizeOf 只能计算本对象占用空间， 无法计算继承下来的占用空间， 
 * 不过可以用反射的方法把全部占用空间计算出来 
 * 
 * Created by zhuyb on 16/3/20. 
 */  
public class SizeOfAgent {  
    static Instrumentation instrumentation;  
  
    // 第一个参数由 –javaagent， 第二个参数由 JVM 传入  
    public static void premain(String agentArgs, Instrumentation instP) {  
        instrumentation = instP;  
    }  
  
    // 返回没有子类对象大小的大小  
    public static long sizeOf(Object o) {  
        if (instrumentation == null) {  
            throw new IllegalStateException("Can not access instrumentation environment.\n" +  
                    "Please check if jar file containing SizeOfAgent class is \n" +  
                    "specified in the java's \"-javaagent\" command line argument.");  
        }  
        return instrumentation.getObjectSize(o);  
    }  
  
    /** 
     * 
     * 计算复合对象 
     * @param obj object to calculate size of 
     * @return object size 
     */  
    public static long fullSizeOf(Object obj) {  
        Map<Object, Object> visited = new IdentityHashMap<Object, Object>();  
        Stack<Object> stack = new Stack<Object>();  
  
        long result = internalSizeOf(obj, stack, visited);  
        while (!stack.isEmpty()) {  
            result += internalSizeOf(stack.pop(), stack, visited);  
        }  
        visited.clear();  
        return result;  
    }  
  
    // 这个算法使每个对象仅被计算一次， 避免循环引用，即死循环计算  
    private static boolean skipObject(Object obj, Map<Object, Object> visited) {  
        if (obj instanceof String) {  
            // String 池里已有的不再计算  
            if (obj == ((String) obj).intern()) {  
                return true;  
            }  
        }  
        return (obj == null) // 已有对象不再计算  
                || visited.containsKey(obj);  
    }  
  
    private static long internalSizeOf(Object obj, Stack<Object> stack,  Map<Object, Object> visited) {  
  
        if (skipObject(obj, visited)){  
            return 0;  
        }  
        visited.put(obj, null);  
  
        long result = 0;  
        // get size of object + primitive variables + member pointers  
        result += SizeOfAgent.sizeOf(obj);  
  
        // 处理所有数组内容  
        Class clazz = obj.getClass();  
        if (clazz.isArray()) {  
            // [I , [F 基本类型名字长度是2  
            if(clazz.getName().length() != 2) {// skip primitive type array  
                int length =  Array.getLength(obj);  
                for (int i = 0; i < length; i++) {  
                    stack.add(Array.get(obj, i));  
                }  
            }  
            return result;  
        }  
  
        // 处理对象的所有字段  
        while (clazz != null) {  
            Field[] fields = clazz.getDeclaredFields();  
            for (int i = 0; i < fields.length; i++) {  
                // 不重复计算静态类型字段  
                if (!Modifier.isStatic(fields[i].getModifiers())) {  
                    // 不重复计算原始类型字段  
                    if (fields[i].getType().isPrimitive()) {  
                        continue;  
                    } else {  
                        // 使 private 属性可访问  
                        fields[i].setAccessible(true);  
                        try {  
                            // objects to be estimated are put to stack  
                            Object objectToAdd = fields[i].get(obj);  
                            if (objectToAdd != null) {  
                                stack.add(objectToAdd);  
                            }  
                        } catch (IllegalAccessException ex) {  
                            assert false;  
                        }  
                    }  
                }  
            }  
            clazz = clazz.getSuperclass();  
        }  
        return result;  
    }  
}
```

## 将代理类打包
为了让JVM知道代理类，必须将其打进jar包并且设定 `manifest.mf` 文件中的书记邢：
```
Premain-Class: com.SizeOfAgent
Can-Redefine-Classes: true
```

可以通过 maven 进行打包，打包的时候执行 `manifest.mf` 属性，在 `pom.xml` 文件的 `<bulid>` 元素中添加如下的 `<plugins>`:
```xml
<build>
    <plugins>
        <!-- maven 打包插件, 可指定生成的 META-INF/manifest.mf 文件中的属性 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <version>2.1</version>
            <configuration>
                <archive>
                    <!-- 生成 manifest.mf 时添加的属性 -->
                    <manifestEntries>
                        <Premain-Class>com.SizeOfAgent</Premain-Class>
                        <Can-Redefine-Classes>true</Can-Redefine-Classes>
                    </manifestEntries>
                </archive>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 使用jar包
使用时，必须配置启动参数来指定jar文件：
```
-javaagent: pathToJar
```