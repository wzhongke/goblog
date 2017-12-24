---
title: jvm调优
date: 2017-09-21 12:00:00
tags: ["java"]
categories: ["java"]
---

在常见的线上问题中，如下问题比较常见：
- 内存泄露
- 某个进程CPU突然飙升
- 线程死锁
- 响应变慢

如果遇到上述问题，我们可以基于监控工具来定位问题。java中常用的分析监控工具有： `jps`、`jstat`、`jinfo`、`jmap`、`jhat`、`jstack`

## `jps`: JVM进程状况
`jps`用来查看进程的状况，语法如下：

```bash
jps [options] [hostid]
参数：
-q: 不输出类名、jar名和传入main方法的参数
-l: 输出main类或jar的全限名
-m: 输出传入main方法的参数
-v: 输出传入JVM的参数

示例
jps -l
    7605 sun.tools.jps.Jps
    13598 com.caucho.server.resin.Resin
```

## `jstat`: JVM 统计信息监控工具
`jstat` 是用于查看虚拟机各种运行状态的命令行工具，它可以显示本地或远程虚拟机中的类装载、内存、垃圾收集、jit编译等运行数据。其具体用法如下：

```
jstat [generalOption|outputOptions vmid [interval[s|ms] [count]]]
generalOption: 单个常用的命令行选项，如 -help, -options
outputOptions: 一个或多个输出选项，由单个的statOption选项组成
```

option   | 用途  | 例子 
:------  |:-----------------|:---------
class    | 用于查看类加载情况的统计 | jstat -class pid : 显示加载class的数量，所占空间等信息
complier | 查看HotSpot中即时编译情况统计 | `jstat -compiler pid`: 显示VM实时编译数量等信息
gc       | 查看JVM中堆的垃圾收集情况的统计| `jstat -gc pid`
gccapacity| 查看新生代、老生代及持久代的存储容量情况 | `jstat -gccapacity`: 可以显示VMware内存中三代对象的使用和占用大小
gccause  | 查看垃圾收集的统计情况，如果有发生垃圾收集，它还会显示最后一次及当前正在发生的垃圾收集原因 | `jstat -gccause`: 显示gc原因
gcnew    | 查看新生代垃圾内含物的情况   | `jstat -gcnew pid`: new 对象的信息
gcnewcapacity | 用于查看新生代的存储容量情况 | `jstat -gcnewcapacity pid`: new 对象的信息及其占用量
gcold    | 查看老生代及持久代GC情况   | `jstat -gcold pid`: old 对象的信息
gcoldcapacity | 用于查看老生代的容量  | `jstat -gcoldcapacity`: old对象的信息及其占用量
gcpermacpacity | 用于查看持久代的容量 | `jstat -gcpermcapacity pid`: 持久代对象的信息
gcutil   | 查看新生代、老年代及持久代垃圾收集情况 | `jstat -util pid`: 统计gc信息

示例
```bash
jstat -gc 17835
```
结果
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT   
43520.0 43520.0  0.0    0.0   611840.0 371440.0 1398272.0  1077246.3  34816.0 33485.4 3584.0 3324.3      7    1.202  32     34.763   35.966

参数说明：


## `jinfo`: Java 配置信息
`jinfo` 可以获取当前线程的jvm运行和启动信息，使用如下：
```bash
jinfo [option] pid
```

## `jmap`: Java 内存映射工具
`jmap` 命令用于将堆转存成快照，
