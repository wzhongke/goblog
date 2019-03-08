---
title: java 内存以及GC
date: 2019-02-23 12:39:00
tags: ["java"]
categories: ["java"]
---

# java 堆内存
根据 Generation 算法，Java 的堆内存被划分为新生代、老年代和持久代。新生代又进一步划分为 Eden 和 Survivor 区，最后 Survivor 由 FromSpace(Survivor0) 和 ToSpace(Survivo1) 组成。

![Alt text](/img/memory.png)

所有通过 `new` 创建的对象都会被分配到堆内存中，堆内存的大小可以通过 `-Xmx` 和 `-Xms` 来控制。

分代收集是基于 不同的对象的生命周期是不一样的。因此，可以将不同生命周期的对象分代，并采用不同的回收算法进行回收。

Java 的堆内存以外的部分：
- 栈，每个线程执行方法时都会在栈中申请一个栈帧，每个栈帧包括局部变量区和操作数栈，用于存放此次方法调用过程中的临时变量、参数和中间结果。
- 本地方法栈，用于支持 native 方法的执行
- 方法区，存放了要加载的类的信息、静态变量、final类型的常亮、属性和方法信息。JVM 用持久代存放方法区，通过 `-XX:PermSize` 和 `-XX:MaxPermSize` 来指定最小和最大值

## 堆内存分配
### 新生代
新生代的内存是按照 8:1:1 的比例分为一个 Eden 区和两个 Survivor 区。

大部分对象在 Eden 区生成，当新对象生成时，会在 Eden 区申请内存，如果申请失败，则会发起一次新生代的 GC。

回收时现将 Eden 区存活对象复制到 Survivor0 区，然后清空 Eden 区。当 Survivor0 区也存放满了，则将 Eden 区和 Survivor0 区存活的对象复制到 Survivor1 区，然后清空 Eden 和 Survivor0 区，然后将 Survivor0 和 Survivor1 区交换，保持 Survivor1 区为空。

如果 Survivor1 区不足以存放 Eden 和 Survivor0 的存活对象时，则将存活对象直接放到老年代。

当对象在 Survivor0 区没有被 GC 的话，则其年龄加1。默认情况下，当对象年龄达到15时，就会用到过到老年代中。

### 老年代
在新生代经历 N 次回收仍然存活的对象，就会被放到老年代中。

老年代一般都是生命周期较长的对象，内存的比例也比新生代大，大概是2:1。

当老年代内存满时触发 Full GC，Full GC 的频率比较低，如果频繁 Full GC 说明内存不够或者有内存溢出。

老年代的对象存活时间比较长。一般来说，大对象会直接被分配到老年代。大对象指的是需要大量连续存储空间的对象。

### 持久代
用于存放静态文件（class类、方法等）和常量。持久代对垃圾回收没有显著影响，但是有些应用（例如Hibernate）可能动态生成或者调用一些class，此时就需要设置一个比较大的持久代空间来存放这些运行过程中新增的类。

对永久代回收主要是两部分内容：废弃常量和无用的类。

内存不够时，java8以前会抛出：`java.lang.OutOfMemoryError: PermGen error` 错误。

java8 将永久代移除，用元空间（MetaSpace）代替。

## 堆内存分配策略
- 对象优先在 Eden 分配
- 大对象直接进入老年代
- 长期存活的对象将进入老年代

# 垃圾回收算法
垃圾回收一般做两件事
1. 找到所有存活对象
2. 回收无用对象所占用的内存

## 引用计数法
堆中的每个对象都有一个引用计数器。每当有地方引用时，计数器的值就加 1. 当引用失效时，计数器的值就减 1. 任何引用计数为 0 的对象可以被回收，当一个对象被回收时，它引用的任何对象计数减 1.

**优点**：引用计数收集器执行简单，判定效率高，对程序不被长时间打断的实时环境有利
**缺点**：难以检测对象之间的循环引用，同时增加了程序执行开销

## 可达性分析算法
该算法的基本思路：
1. 通过一系列名为 GC Roots 的对象作为起点，寻找对应的引用节点
2. 找到节点后，从这些节点开始向下搜寻它们的引用节点
3. 重复 2
4. 搜索所走过的路径成为引用链，当一个对象到 GC Roots 没有任何引用链相连时，那么该对象是不可用的

一般 GC Roots 包含如下对象：
- 虚拟机栈中引用的对象
- 方法区中的常量引用对象
- 方法区中的类静态属性引用的对象
- 本地方法栈中 JNI （Native 方法） 引用的对象
- 活跃线程

![Alt text](/img/mark.png)

存活对象在上图中被标记为蓝色。标记阶段完成后，所有存活对象都已被标记，剩余的灰色是不可达对象，可以回收掉。

# ParNew 回收器
ParNew 回收器是年轻代常用的垃圾回收器，采用的是复制算法，YongGC 的典型日志信息如下：
```log

```

日志信息如下：
- 2018-04-12T13:48:26.134+0800：Mirror GC 发生的时间；
- 15578.050：GC 开始时，相对 JVM 启动的相对时间，单位时秒，这里是4h+；
- ParNew：收集器名称，这里是 ParNew 收集器，它使用的是并行的 mark-copy 算法，GC 过程也会 Stop the World；
- 3412467K->59681K：收集前后年轻代的使用情况，这里是 3.25G->58.28M；
- 3774912K：整个年轻代的容量，这里是 3.6G；
- 0.0971990 secs：Duration for the collection w/o final cleanup.
- 9702786K->6354533K：收集前后整个堆的使用情况，这里是 9.25G->6.06G;
- 24746432K：整个堆的容量，这里是 23.6G；
- 0.0974940 secs：ParNew 收集器标记和复制年轻代活着的对象所花费的时间（包括和老年代通信的开销、对象晋升到老年代开销、垃圾收集周期结束一些最后的清理对象等的花销）；

对于 `[Times: user=0.95 sys=0.00, real=0.09 secs]`，涉及三种时间类型：
- user: GC 线程在垃圾回收期间所使用的 CPU 总时间
- sys: 系统调用或者等待系统事件所花费的时间
- real: 应用程序被暂停的时间，由于 GC 是多线程的，导致 real 小于 (user + sys)

# CMS 回收算法
CMS 全称 Concurrent Mark Sweep，是一款并发的、使用标记-清除算法的垃圾回收器。在老年代使用 CMS 垃圾回收器时，需要添加 `-XX:+UseConcMarkSweepGC` 的虚拟机参数。

**使用场景**：GC过程短暂停，适合对延迟要求较高的服务，用户线程不允许长时间的停顿。

CMS 涉及的阶段比较多，以下是其主要的阶段：

## Yong GC
将存活的对象从 Eden 和 Survivor 区拷贝到另外一个 Survivor区。所有达到年龄阈值的对象被放到老年代。

![](/image/yonggc1.png)
![](/image/yonggc2.png)

Yong GC 后，Eden 和其中一个 Survivor 区被清空
![](/image/yonggc3.png)

## Initial Mark
CMS 垃圾回收有两次 SWT (stop the world)，其中一次是 Initial Mark。这个阶段的目标是：标记那些**直接**被 GC Roots 引用或者被年轻代存活的对象所引用的对象。

在日志中对应的信息是：
```log
2019-03-05T09:13:03.124+0800: 59223.296: [GC (CMS Initial Mark) [1 CMS-initial-mark: 6051135K(8437760K)] 6279638K(10258240K), 0.0252641 secs] [Times: user=0.16 sys=0.00, real=0.02 secs]
```

- 2019-03-05T09:13:03.124+0800: 59223.296：GC 开始的时间，以及相对于 JVM 启动的相对时间（单位是秒），与前面 ParNew 类似；
- CMS-initial-mark：初始标记阶段，它会收集所有 GC Roots 以及其直接引用的对象；
- 6051135K：当前老年代使用的容量，这里是 5.77G；
- (8437760K)：老年代可用的最大容量，这里是 8G；
- 6279638K：整个堆目前使用的容量，这里是 5.99G；
- (10258240K)：堆可用的容量，这里是 9.78G；
- 0.0252641 secs：这个阶段的持续时间；
- [Times: user=0.16 sys=0.00, real=0.02 secs]：与前面的类似，这里是相应 user、system and real 的时间统计。

## Concurrent Mark
在这个阶段会根据上一个阶段的 GC Roots 遍历老年代，并标记所有存活的对象。

并发标记阶段与用户的应用程序并发运行，但并不是所有的老年代存活对象都会被标记，因为在标记期间用户的应用程序可能会改变一些引用。
```log
2019-03-05T09:13:03.149+0800: 59223.321: [CMS-concurrent-mark-start]
2019-03-05T09:13:04.627+0800: 59224.799: [CMS-concurrent-mark: 1.403/1.478 secs] [Times: user=4.45 sys=0.10, real=1.47 secs]
```

这里详细对上面的日志解释，如下所示：
- CMS-concurrent-mark：并发收集阶段，这个阶段会遍历老年代，并标记所有存活的对象；
- 1.403/1.478 secs：这个阶段的持续时间与时钟时间；
- [Times: user=4.45 sys=0.10, real=1.47 secs]：如前面所示，但是这部的时间，其实意义不大，因为它是从并发标记的开始时间开始计算，这期间因为是并发进行，不仅仅包含 GC 线程的工作。


## Concurrent Preclean
该阶段也是一个并发阶段，不会 STW。在并发运行的过程中，一些对象的引用就会发生变化。这种情况发生时，JVM 会将包含这个对象的区域标记为 Dirty，也就是 Card Marking。

在 PreClean 阶段，那些能够从 Dirty 对象到达的对象也会被标记。标记做完后，Dirty card 标记就会被清除。

日志信息如下：
```log
2019-03-05T09:13:04.627+0800: 59224.799: [CMS-concurrent-preclean-start]
2019-03-05T09:13:04.653+0800: 59224.825: [CMS-concurrent-preclean: 0.025/0.026 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]
```

含义为：
- CMS-concurrent-preclean：Concurrent Preclean 阶段，对在前面并发标记阶段中引用发生变化的对象进行标记；
- 0.025/0.026 secs：这个阶段的持续时间与时钟时间；
- [Times: user=0.03 sys=0.00, real=0.03 secs]：同并发标记阶段中的含义。

## Concurrent Abortable Preclean
这个阶段是为了尽量承担 STW 中最终标记阶段的工作。这个阶段是在重复做很多相同的工作，直至满足一些条件，如：重复迭代的次数、完成的工作量或者时钟时间等。

日志信息如下：
```log
2019-03-05T09:13:04.653+0800: 59224.825: [CMS-concurrent-abortable-preclean-start]
2019-03-05T09:13:08.918+0800: 59229.089: [CMS-concurrent-abortable-preclean: 3.843/4.264 secs] [Times: user=9.57 sys=0.19, real=4.26 secs]
```

含义为：
- CMS-concurrent-abortable-preclean：Concurrent Abortable Preclean 阶段；
- 3.843/4.264 secs：这个阶段的持续时间与时钟时间，本质上，这里的 gc 线程会在 STW 之前做更多的工作，通常会持续 5s 左右；
- [Times: user=9.57 sys=0.19, real=4.26 secs]：同前面。

## Final Remark
这是第二个 STW 的阶段，这个阶段是标记所有老年代存活的对象。因为之前的阶段是并发执行的，gc 线程可能跟不上应用程序的变化，为了完成标记，STW 就非常必要了。

通常 CMS 的 Final Remark 阶段会在年轻代尽可能干净的时候运行，目的是为了减少连续 STW 发生的可能性（年轻代存活对象过多的话，也会导致老年代涉及的存活对象会很多）。这个阶段会比前面的几个阶段更复杂一些，相关日志如下：

```log
2019-03-05T09:13:08.923+0800: 59229.094: [GC (CMS Final Remark) [YG occupancy: 1319324 K (1820480 K)]2019-03-05T09:13:08.923+0800: 59229.095: [Rescan (parallel) , 0.0853762 secs]2019-03-05T09:13:09.008+0800: 59229.180: [weak refs processing, 0.0046217 secs]2019-03-05T09:13:09.013+0800: 59229.185: [class unloading, 0.0673909 secs]2019-03-05T09:13:09.080+0800: 59229.252: [scrub symbol table, 0.0129999 secs]2019-03-05T09:13:09.093+0800: 59229.265: [scrub string table, 0.0029732 secs][1 CMS-remark: 6161078K(8437760K)] 7480403K(10258240K), 0.1744124 secs] [Times: user=0.69 sys=0.00, real=0.17 secs]
```

- YG occupancy: 1319324 K (1820480 K)：年轻代当前占用量及容量，这里分别是 1.26G 和 1.74G；
- ParNew:...：触发了一次 young GC，这里触发的原因是为了减少年轻代的存活对象，尽量使年轻代更干净一些；
- [Rescan (parallel) , 0.0853762 secs]：这个 Rescan 是当应用暂停的情况下完成对所有存活对象的标记，这个阶段是并行处理的，这里花费了 0.0853762s；
- [weak refs processing, 0.0046217 secs]：第一个子阶段，它的工作是处理弱引用；
- [class unloading, 0.0673909 secs]：第二个子阶段，它的工作是：unloading the unused classes；
- [scrub symbol table, 0.0129999 secs] ... [scrub string table, 0.0029732 secs]：最后一个子阶段，它的目的是：cleaning up symbol and string tables which hold class-level metadata and internalized string respectively，时钟的暂停也包含在这里；
- 6161078K(8437760K)：这个阶段之后，老年代的使用量与总量，这里分别是 5.88G 和 8.05G；
- 7480403K(10258240K)：这个阶段之后，堆的使用量与总量（包括年轻代，年轻代在前面发生过 GC），这里分别是 7.13G 和 9.78G；
- 0.1744124 secs：这个阶段的持续时间；
- [Times: user=1.24 sys=0.00, real=0.14 secs]：对应的时间信息。

## Concurrent Sweep
在经过以上五个阶段后，老年代所有存活的对象都被标记了，现在可以通过清除算法清理老年代。

这个阶段不用 STW。对应的日志如下：
```log
2019-03-05T09:13:09.097+0800: 59229.269: [CMS-concurrent-sweep-start]
2019-03-04T16:46:44.118+0800: 44.290: [CMS-concurrent-sweep: 0.561/0.590 secs] [Times: user=1.75 sys=0.05, real=0.59 secs]
```

- CMS-concurrent-sweep：这个阶段主要是清除那些没有被标记的对象，回收它们的占用空间；
- 0.561/0.590 secs：这个阶段的持续时间与时钟时间；
- [Times: user=30.34 sys=16.44, real=8.28 secs]：同前面；

## Concurrent Reset
这个阶段也是并发执行的，它会重设 CMS 内部的数据结构，为下次 GC 做准备，日志信息如下：
```
2019-03-04T16:46:44.118+0800: 44.290: [CMS-concurrent-reset-start]
2019-03-04T16:46:44.137+0800: 44.309: [CMS-concurrent-reset: 0.018/0.018 secs] [Times: user=0.08 sys=0.00, real=0.02 secs]
```

- CMS-concurrent-reset：这个阶段的开始，目的如前面所述；
- 0.018/0.018 secs：这个阶段的持续时间与时钟时间；
- [Times: user=0.15 sys=0.10, real=0.04 secs]：同前面。

## CMS 的一些问题
CMS 将大量工作分散到并发处理阶段来减少 STW 的时间，但是 CMS 也有如下问题：
- CMS 收集器无法处理浮动垃圾(Floating Garbage)，可能会出现 `Concurrent Mode Failure` 失败，从而导致另一次 Full GC，也可能引起串行 Full GC。
- 空间碎片，导致无法分配大对象。CMS 收集器提供了 `-XX:+UseCMSCompactAtFullCollection` 开关参数（默认就是开启的），用于在 CMS 收集器顶不住要进行 Full GC 时开启内存碎片的合并整理过程，内存整理的过程是无法并发的，空间碎片问题没有了，但停顿时间不得不变长；
- 对于堆比较大的应用，GC 的时间很难预估

# G1 收集器
G1 GC（垃圾优先型回收器）是适用于 Java HotSpot VM 的低暂停、服务器风格的分代式垃圾回收器。
G1 使用并发和并行阶段实现其目标暂停时间，并保持良好的吞吐量。当有必要进行垃圾回收时，会优先收集存活数据最少的区域。

G1 是区域化、分代式垃圾回收器，它将 Java 堆划分成大小相同的若干区域。启动时，JVM 会根据堆的大小，划分出不超过2048个区域，每个区域的大小从 1M 到 32M 不等。这样 Eden、Survivo 和老年代是一系列不连续的逻辑区域。

新生代的垃圾收集依然采用 STW 方式，将存活的对象拷贝到老年代或者 Survivor 空间。因为老年代也分很多区域，所以回收时将老年代对象从一个区域复制到另一个区域，完成清理工作，这样也就不会有 CMS 的内存碎片问题。

![](/image/g1.png)

上图中的 H 代表 Humongous，表示这些区域存储的是巨型对象。一般巨型对象的大小大于等于区域大小的一半。

巨型对象有如下特征：
- 直接被分配到 H 区，防止反复拷贝
- 在 Concurrent Marking 阶段的 cleanup 和 Full GC 阶段回收

## RSet
RSet 全称 Remembered Set，是一种空间换时间的工具。RSet 是其他区域中的对象引用该区域中的对象的集合，属于 points-into 结构，记录谁引用了我的对象。G1 使用 RSet 跟踪区域的引用，独立的 RSet 可以并行、独立地回收区域，只需要对区域的 RSet 进行区域引用进行扫描。

G1 将一组或多组区域（回收集 CSet）中的存活对象以增量、并行的方式复制到不同的新区域来实现压缩。
一般来说，每个区域都有对一个的 RSet，

在 GC 的时候，对于 old->yong 和 old->old 的跨代对象引用，只要扫描对应的 CSet 中的 RSet。

## 年轻代回收
G1 可以满足添加到 eden 区域集的大多数分配请求。在年轻代垃圾回收期间，G1 会同时回收 Eden 区域和上次垃圾回收的存活区域。Eden 和存活区的对象将被复制或疏散到新的区域集。足够老的对象疏散到老年代，否则疏散到存活区，并将包含在下一次年轻代或混合垃圾回收的 CSet 中
![](/image/g1yong.png)
![](/image/g1yong1.png)

## 混合垃圾回收
G1 在完成并发标记周期后，从执行年轻代垃圾回收切换为执行混合垃圾回收。
在混合垃圾回收期间，G1 将一些旧的区域添加到 Eden 和存活区来供将来回收。在回收足够的旧区域后，G1 将恢复执行年轻代的垃圾回收。

## 参数设定

参数内容       |   取值| 含义
:------------|:------|:----------
`-XX:G1HeapRegionSize`| 1M - 32M （2的指数）| 设定区域的大小


## 对比 CMS
相比 CMS，G1 有以下特色：
- G1有一个整理内存的过程，不会产生很多内存碎片
- G1的 STW 更可控，它在停顿时间上添加了预测机制，用户可以指定期望停顿时间



# 参考
https://www.oracle.com/technetwork/tutorials/tutorials-1876574.html