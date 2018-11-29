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


事件驱动是通过主循环加事件触发的方式来运行程序

# 异步编程解决方案
目前异步编程主要方案有三种：
- 事件发布/订阅模式
- promise/defeered 模式
- 流程控制库

## 事件发布/订阅模式
事件监听器模式是一种广泛用于异步编程的模式，将回调函数作为事件，也叫发布/订阅模式。
示例代码如下：
```js
// 订阅
emitter.on('event1', function (message) {
    console.log('event 1');
});
// 发布
emitter.emit('event1', 'this is the message');
```

可以通过事件发布/订阅模式进行组件封装，将不变的部分封装到组件内部，将容易变化、自定义的部分通过事件暴露给外部处理，这是一种典型的逻辑分离方式。

### 利用事件队列解决雪崩问题
事件发布/订阅模式中，通常有一个 `once()·方法，通过它添加的侦听器只能执行一次，执行之后就会解除关联。这个方法可以帮助我们过滤一些重复性的事件响应。
缓存由于存放在内存中，访问速度快，常常用于加速数据访问，让大多数请求不必做一些低效的数据读取。
**雪崩问题** 就是在高访问量、大并发的情况下缓存失效的情形，此时大量的请求同时涌入数据库，数据库无法同时承受如此大的查询请求，进而影响到网站整体的响应速度。

```js
let select = function (callback) {
    db.select("SQL", function (results) {
        callback(results);
    })
}
```

如果访问量巨大，同一个SQL语句会被发送到数据库中反复查询，进而影响到服务的整体性能。可以通过增加状态锁的方式改进该方法：
```js
let status = 'ready';
let select = function (callback) {
    if (status == 'ready') {
        status = 'pending';
         db.select("SQL", function (results) {
            status = 'ready';
            callback(results);
         });
    }
}
```

但是，连续多次调用 `select()`，只有第一次调用是有效的，后续 `select()` 没有数据服务。可以通过引入事件队列来解决：
```js
let proxy = new events.EventEmitter();
let status = 'ready';
let select = function (callback) {
    proxy.once('selected', callback);
    if (status == 'ready') {
        status = 'pending';
         db.select("SQL", function (results) {
            proxy.emit('selected', results);
            status = 'ready';
         });
    }
}
```

对于相同的SQL，保证同一个查询开始到结束的过程只有一次。SQL在进行查询时，新到来的相同调用只需在队列中等待数据就绪即可。一旦查询结束，得到的结果可以被这些调用同时使用。  