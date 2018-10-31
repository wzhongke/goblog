---
title: node
date: 2018-10-25 12:00:00
draft: true
---

node 运行是基于事件驱动的。

Node 保持了JavaScript的单线程的特点。Node中，JavaScript与其余线程是无法共享任何状态的。单线程最大的好处是不用在意同步问题，没有死锁，也没有线程上下文切换所带来的新能开销。
但是，单线程也有如下弱点：
- 无法利用多核 CPU
- 错误会引起整个应用退出
- 大量计算占用 CPU 会导致无法继续调用异步 I/O

Node 采用了同 Web Workers 相同的思路来解决单线程中大量计算的问题：子线程 child_process.

# 流程控制