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

### Promise/Deferred 模式
异步的广泛使用使得回调、嵌套出现，但是一旦出现深度的嵌套，就会让编程和体验变得不愉快，而Promise/Deferred模式在一定程度上缓解了这个问题。CommonJS 草案目前抽象出了 Promise/A、Promises/B、Promises/C典型的 Promise/Deferred 模型。
Promise/Deferred 模式其实包含两部分: Promise 和 Deferred。
Promise/A 对单个异步操作的抽象定义如下：
- Promise 操作只会处在3种状态中一种：未完成态、完成态和失败态。
- Promise 的状态只会从未完成态向完成态或失败态转化，不能逆反。完成态和失败态不能相互转化。
- Promise 的状态一旦转化，将不能被更改。

对于 API 的定义，Promises/A 只要求 Promise 对象具备 `then()` 方法：
- 接受完成态、错误态的回调方法。在操作完成或者出现错误时，调用对应的方法
- 可选地支持 progress 事件回调作为第三个方法
- `then()` 方法只接受 function 对象
- `then()` 方法继续返回 Promise 对象，以实现链式调用

以下代码是对 Promises/A 模式的一个简单模拟：
```js
let Promise = function () {
    EventEmitter.call(this);
};
util.inherits(Promise, EventEmitter);

Promise.prototype.then = function (fulfilledHandler, errorHandler, progressHandler) {
    if (typeof fulfilledHandler === 'function') {
        // 利用 once() 方法保证成功回调只执行一次
        this.once('success', fulfilledHandler);
    }
    if (typeof errorHandler === 'function') {
        this.once('error', errorHandler);
    }
    if (typeof progressHandler === 'function') {
        this.on('progress', progressHandler);
    }
    return this;
}
```

`then()` 方法将回调函数存放起来。Deferred，也被称为延迟对象，是触发执行这些回调函数的地方：
```js
let Deferred = function () {
    this.state = 'unfulfilled';
    this.promise = new Promise();
}
Deferred.prototype.resolve = function (obj) {
    this.state = 'fulfilled';
    this.promise.emit('success', obj);
}
Deferred.prototype.reject = function (err) {
    this.state = 'failed';
    this.promise.emit('error', err);
}
Deferred.prototype.progress = function (data) {
    this.promise.emit('progress', data);
}
```

利用 Promise/A 模式，可以对一个典型的响应对象进行封装，相关代码如下：
```js
res.on('data', function (chunk) {
    console.log(`BODY: ${chunk}`);
});
res.on('end', function () {
    // DONE
});
res.on('error', function (err) {
    // ERR
});
```

以上代码可以转换成如下形式：
```js
res.then(function () {
    // DONE
}, function (err) {
    // ERR
}, function (chunk) {
    console.log(`BODY: ${chunk}`);
});
```

可以通过如下方式将代码改造成上面的简略形式：
```js
let promisify = function (res) {
    let deferred = new Deferred();
    let result = '';
    res.on('data', function (chunk) {
        result += chunk;
        deferred.progress(chunk);
    });
    res.on('end', function () {
        deferred.resolve(result);
    });
    res.on('error', function (err) {
        deferred.reject(err);
    });
    return deferred.promise;
}
```

### Promise 处理多异步协作
Promise 主要是处理单个异步操作中存在的问题，当需要处理多个异步调用时，下面是一个简单的模型：
```js
Deferred.prototype.all = function (promises) {
    let count = promises.length;
    let results = [];
    promises.forEach(function (promise, i) {
        promise.then(function (data) {
            count --;
            results[i] = data;
            if (count === 0) {
                that.resolve(results);
            }
        }, function (err) {
            that.reject(err);
        })        
    })
}
```

### 支持序列化执行的 Promise
理想的编程应该是链式调用：
```js
promise()
    .then(fun1)
    .then(fun2)
    .then (function (data){

    }, function (error) {

    });
```

将代码改造实现链式调用：
```js
var Deferred = function () {
    this.promise = new Promise();
}
// 完成态
Deferred.prototype.resolve = function (obj) {
    let promise = this.promise;
    let handler;
    while ((handler = promise.queue.shift())) {
        if (handler && handler.fulfilled) {
            let ret = handler.fulfilled(obj);
            if (ret && ret.isPromise) {
                ret.queue = promise.queue;
                this.promise = ret;
                return ;
            }
        }
    }
};

// reject 
Deferred.prototype.reject = function (err) {
    let promise = this.promise;
    let handler;
    while ((handler = promise.queue.shift())) {
        let ret = handler.error(err);
        if (ret && ret.isPromise) {
            ret.queue = promise.queue;
            this.promise = ret;
            return ;
        }
    }
};

// 回调函数
Deferred.prototype.callback = function () {
    let taht = this;
    return function (err, file) {
        return that.reject(file);
    }
    that.resolve(file);
};

var Promise = function () {
    // 队列用于存储待执行的回调函数
    this.queue = [];
    this.isPromise = true;
};

Promise.prototype.then = function (fulfilledHandler, errorHandler, progressHandler) {
    let handler = {};
    if (typeof fulfilledHandler === 'function') {
        handler.fulfilled = fulfilledHandler;
    }
    if (typeof errorHandler === 'function') {
        handler.error = errorHandler;
    } 
    this.queue.push(handler);
    return this;
}
```

以读取第二个文件依赖于第一个文件为列：
```js
let readFile = function (file, encoding) {
    let deferred = new Deferred();
    fs.readFile(file, encoding, deferred.callback());
    return deferred.promise;
}
readFile('file1.txt', 'utf8').then(function (file1) {
    return readFile(file1.trim(), 'utf8');
}).then (function (file2) {
    console.log(file2);
})
```

让 Promise 支持链式调用，主要通过以下两个步骤：
- 将所有的回调都存到队列中
- Promise 完成时，逐个执行回调，一旦检测到返回了新的 Promise 对象，停止执行，然后将当前 Deferred 对象的 `promise` 引用改变为新的 Promise 对象，并将队列中余下的回调转交给它。