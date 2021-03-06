---
title: jvm调优
date: 2017-09-21 12:00:00
tags: ["java"]
categories: ["java"]
---

# 垃圾回收

CMS 收集器无法处理浮动垃圾（Floating Garbage），可能会出现 `Concurrent Mode Failure` 失败，而导致另一次 STW 的 Full GC 的产生。
由于 CMS 并发清理阶段用户线程还在运行，伴随着程序运行自然就还会有新的垃圾不断产生，这部分垃圾出现在标记过程之后，CMS 无法在当次收集中处理掉它们，只好留到下次

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
jstat [options] pid [interval[s|ms] [count]]
- options: 选项，我们一般使用 -gcutil 查看gc情况
- interval: 间隔时间，单位为秒或者毫秒
- count: 打印次数，如果缺省则打印无数次
```

option   | 用途  | 例子 
:------  |:-----------------|:---------
class    | 用于查看类加载情况的统计 | `jstat -class <pid>` : 显示加载class的数量，所占空间等信息
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
jstat -gc <pid>
```
结果
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT   
43520.0 43520.0  0.0    0.0   611840.0 371440.0 1398272.0  1077246.3  34816.0 33485.4 3584.0 3324.3      7    1.202  32     34.763   35.966

```bash
jstat -gcutil <pid> 5000
```
 S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT
  0.00   1.30  19.87  15.23  95.56  92.59    269   20.071     2    0.136   20.207
  1.29   0.00  13.94  15.23  95.56  92.59    270   20.138     2    0.136   20.274
  0.00   1.35   9.45  15.23  95.56  92.59    271   20.204     2    0.136   20.341

参数说明：
- S0C: 年轻代中第一个survivor（幸存区）的容量 (字节) 
- S1C: 年轻代中第二个survivor（幸存区）的容量 (字节) 
- S0U: 年轻代中第一个survivor（幸存区）目前已使用空间 (字节) 
- S1U: 年轻代中第二个survivor（幸存区）目前已使用空间 (字节) 
- EC: 年轻代中Eden（伊甸园）的容量 (字节) 
- EU: 年轻代中Eden（伊甸园）目前已使用空间 (字节) 
- OC: 老年代大小 (字节) 
- OU: 老年代使用大小 (字节) 
- MC: 方法区大小 (字节) 
- MU: 方法区使用大小 (字节) 
- CCSC: 压缩类空间大小 (字节) 
- CCSU: 压缩类空间使用大小 (字节) 
- YGC: 年轻代垃圾回收次数
- YGCT: 年轻代垃圾回收消耗时间
- FGC: 老年代垃圾回收次数
- FGCT: 老年代垃圾回收消耗时间
- GCT: 垃圾回收消耗总时间
- NGCMN: 年轻代(young)中初始化(最小)的大小 (字节) 
- NGCMX: 年轻代(young)的最大容量 (字节) 
- NGC: 年轻代(young)中当前的容量 (字节) 
- OGCMN: old代中初始化(最小)的大小 (字节) 
- OGCMX: old代的最大容量 (字节) 
- OGC: old代当前新生成的容量 (字节) 
- PGCMN: perm代中初始化(最小)的大小 (字节) 
- PGCMX: perm代的最大容量 (字节)   
- PGC: perm代当前新生成的容量 (字节) 
- S0: 年轻代中第一个survivor（幸存区）已使用的占当前容量百分比 
- S1: 年轻代中第二个survivor（幸存区）已使用的占当前容量百分比 
- E: 年轻代中Eden（伊甸园）已使用的占当前容量百分比 
- O: old代已使用的占当前容量百分比 
- P: perm代已使用的占当前容量百分比 
- S0CMX: 年轻代中第一个survivor（幸存区）的最大容量 (字节) 
- S1CMX : 年轻代中第二个survivor（幸存区）的最大容量 (字节) 
- ECMX: 年轻代中Eden（伊甸园）的最大容量 (字节) 
- DSS: 当前需要survivor（幸存区）的容量 (字节)（Eden区已满） 
- TT:  持有次数限制 
- MTT:  最大持有次数限制 

## `jinfo`: Java 配置信息
`jinfo` 可以获取当前线程的jvm运行和启动信息，使用如下：
```bash
jinfo [option] pid
```

## `jmap`: Java 内存映射工具
`jmap` 命令用于将堆转存成快照，

查看内存中类的情况：
```
jmap -histo:live <pid>
```


