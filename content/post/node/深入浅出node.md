---
title: 深入浅出 nodejs
date: 2018-10-25 12:00:00
---

# 模块编译
Node 对获取的 JavaScript 文件内容进行了头尾包装：
```js
(function (exports, require, module, __filename, __dirname)) {
    // 文件内容
    var math = require('math');
    exports.area = function(radius) {
        return Math.PI * radius * radius;
    }
    // 文件内容结束
})
```

有上述代码可以看出，使用 `exports=function(){}` 导出内容并不能生效，这是因为 `exports` 是形参。

**JavaScript 层面上做位运算的效率不高，因为它现将 double 型数据转换成 int 型，再计算**

# 异步 I/O 原理
让部分线程进行阻塞 I/O 或者非阻塞 I/O 加轮询技术来完成数据的获取，让一个线程进行计算处理，通过线程之间的通信将 I/O 得到的数据进行传递。

# Node 异步 I/O
事件循环、观察者、请求对象、I/O线程池共同构成了 Node 异步 I/O 模型的基本要素。

## 事件循环
进程启动时，Node 便会创建一个类似于 `while(true)` 的循环，每执行一次循环体的过程我们称之为 **Tick**.
每个 Tick 的过程就是查看是否有事件待处理，如果有，就取出事件机器相关的回调函数。如果存在关联的回调函数，就执行它们。
然后就进入下个循环，如果不再有事件处理，就退出进程。

# 异步编程
Node 带来的最大特性是 **基于事件驱动的非阻塞 I/O 模型**
非阻塞 I/O 可以使 CPU 与 I/O 并不相互依赖等待，让资源得到更好的利用。Node 为了解决编程模型中阻塞 I/O 的性能问题，采用了单线程模型，这也导致了 Node 更像一个处理 I/O 密集型问题的能手，而 CPU 密集型问题却可能成了性能瓶颈（可以通过调用 C/C++ 扩展模块）。

## 难点1：处理异常
如下代码是使用 Java 代码处理异常时经常用到的：
```js
try {
    JSON.parse(json);
} catch (e) {
    // handle exception
}
```

但是这对于异步编程来说并不适用。异步 I/O 主要包含两个阶段：提交请求和处理结果。这两个阶段中间有事件循环的调度，两者彼此并不关联。异步方法通常在第一个阶段提交请求后立即返回，但是异常并不一定在这个阶段发生。
```js
var async = function(callback) {
    process.nextTick(callback);
}
try {
    async(callback)
} catch (e) {
}
```

调用 `async()` 方法后， `callback` 被存放起来，直到下一个事件循环才会取出来执行。尝试对异步方法进行 `try/catch` 操作只能捕获当次事件循环内的异常，对 `callback` 执行时抛出的异常无能为力。

异步方法一般遵循一些原则：
- 必须执行调用者传入的回调函数
- 正确传递回异常供调用者判断

