---
title: java 内存以及GC
date: 2019-02-23 12:39:00
tags: ["java"]
categories: ["java"]
---

# java 堆内存
根据 Generation 算法，Java 的堆内存被划分为新生代、老年代和持久代。新生代又进一步划分为 Eden 和 Survivor 区，最后 Survivor 由 FromSpace(Survivor0) 和 ToSpace(Survivo1) 组成。