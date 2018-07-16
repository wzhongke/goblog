---
title: 你不知道的JavaScript读书笔记
date: 2018-04-21 09:49:00
tag: ["javascript"]
categories: ["javascript"]
---

# 类型
JavaScript 有7中内置类型：
- 空值 `null`
- 未定义 `undefined`
- 布尔值 `boolean`
- 数字 `number`
- 字符串 `string`
- 对象 `object`
- 符号 `symbol` （ES6新增）

我们可以用 `typeof` 运算符来查看值的类型，它返回的类型的字符串值。但是它对 `null` 的处理有问题：
```js
typeof null === "object"; //true
```

对于未声明的变量和声明后未赋值的变量，`typeof` 都会返回 `undefined`
```js
var a;
typeof a; // undefined
typeof b; // undefined
```

判断一个数值是否为 `NaN` 的方法，因为 `NaN` 是 JavaScript 中唯一一个不等于自身的值：
```js
if (!Number.isNaN) {
    Number.isNaN = function (n) {
        return n !== n;
    }
}
```

# 原生函数
所有 `typeof` 返回值为 `object` 的对象，都包含一个内部属性 `[[Class]]`，这个属性无法直接访问，一般通过如下方式来查看：
```js
Object.prototype.toString.call([1,2,3]); // "[object Array]"
Object.prototype.toString.call(/regex/i); // "[object RegExp]
```

多数情况下，对象内部的 `[[Class]]` 属性和创建该对象的内建原生构造函数相对应。

# 强制类型转换
对于布尔类型来说，下列值在强制转换时会是 `false`:
- `undefined`
- `null`
- `false`
- `+0, 0, -0` 和 `NaN`
- ""

上列值用在条件判断语句时，都会转换成 `false`，这些值以外的值都会是 `true`

# 异步程序
当运行时间较长的数据处理函数执行时，页面上的其他代码都不能执行，包括UI刷新，甚至滚动、输入、点击这样的用户事件。
所以要创建一个协作性更强更友好的，不会霸占事件循环队列的并发系统，来异步第处理这些数据。一个非常简单有效的方法：
```js
let res = [];
function dataProcess (data) {
     // 一次处理 1000 条数据
    let chunk = data.splice(0, 1000);

    // 添加到已有的 res 组
    res = res.concat(
        chunk.map(val => val * 2);
    );

    if (data.length > 0) {
        // 异步调度下一次批处理
        setTimeout(()=>response(data));
    }
}
```

在ES6中，有一个新的建立在事件循环队列上的概念，叫做**任务队列**。它是挂在事件循环队列的每个tick之后的一个队列。在事件循环的每个tick中，可能出现的异步动作不会导致一个完整的新事件添加到事件循环队列的末尾，而会在当前tick的任务队列末尾天剑一个任务。

