---
title: apply和call
date: 2017-06-17 19:48:32
tags: ["javascript"]
categories: ["javascript"]
---

JavaScript中每个函数都包含两个非继承而来的方法：apply()和call().
apply()方法接收两个参数：运行调用函数的作用域；参数数组或者arguments对象。
call()方法同apply()不同之处在于除了第一个参数是函数运行的作用域，其余参数都是直接传递给函数的，因此使用call()方法时传递的参数必须全部列举出来。
<!-- more -->

```js
function sum(num1, num2) {
	console.log(num1 + num2);
}

function timeoutCall(context, f) {
	var len = arguments.length;
	var args = [];
	for (var i = 2; i < len; i++) {
		args.push(arguments[i]);
	}
	return function() {
		f.apply(context, args);
	}
}

timeoutCall(this, sum, 1,2);
```
上述方法可以用在setTimeout中，用来维持函数原有的作用域。