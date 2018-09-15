---
title: javascript 基础
date: 2018-08-17 11:00:00
---

# Boolean 类型
下表给出了各种数据类型及其对应的转换成 Boolean 类型的规则

数据类型  | 转换为 true 的值 | 转换为 false 的值
:--------|:---------------|:---------------------------
Boolean  | `true`         | `fasle`
String   | 任何非空字符串   | "" (空字符串)
Number   | 任何非零数值(包括无穷大) | `0` 和 `NaN`
Obeject  | 任何对象         | `null`
Undefined | 不适用          | `undefined`

# Array 类型
创建数组有两种方式：一种是使用 Array 构造函数：
```js
var colors = new Array();
// 创建指定长度的数组
var colors = new Array(10);
// 创建含有指定项的数组
var colors = new Array('red', 'black');
// 也可以省略 new 
var colors = Array(10);
```

第二种方式是使用数组字面量表示：
```js
var colors = ['red', 'blue', 'green'];
// 创建一个空数组
var names = [];
```

数组的 `length` 属性是可设置的：
```js
var colors = ['red', 'blue', 'green'];
// 会删除第三项
colors.length = 2;
// 数组长度为 5，新增的项都是 undefined
colors.length = 5;
```

## 检测数组
可以使用 `instanceof` 来检测数组。但是这种方式在包含多个框架的网页中可能会有问题：
```js
if (value instanceof Array) {}
```

可以使用 ES5 新增的检测方式：
```js
if (Array.isArray(value)) {}
```

## 栈方法
数组有一组 `pop()` 和 `push()` 的方法，用来实现栈结构：
```js
colors.push('red', 'green');
// 将最后一项取出
var item = colors.pop();
```

## 队列方法
使用 `push()` 和 `shift()` 方法就能像使用队列一样使用数组：
```js
colors.push('black');
// 将第一项取出
var firstItem = colors.shift();
```

数组还有一个 `unshift()` 方法，它在数组前端添加任意个项并返回新的数组长度：
```js
var length = colors.unshift('red', 'green');
```

## 重排序方法
`reverse()` 方法可以反转数组的顺序：
```js
var values = [1, 2, 3, 4];
values.reverse();
console.log(values); // [4, 3, 2, 1]
```

`sort()` 在默认情况下，会按升序排列数组。`sort()` 方法会调用每个数组项的 `toString()` 方法，然后比较得到的字符串。即使数组中的每一项都是数值，`sort()` 也是比较字符串。
```js
var values = [0, 1, 5, 10, 15];
values.sort();
console.log(values); // [0, 1, 10, 15, 5]
```

可见，`sort()` 方法会比较字符串来确定先后顺序。所以 `10` 会位于 `5` 的前面。
因此 `sort()` 方法还可以接收一个比较函数作为参数，以便我们确定两个值的顺序。
比较函数接收两个参数，如果第一个参数应该位于第二个之前则返回一个负数；如果两个参数相等则返回0；如果第一个参数应该位于第二个之后则返回一个正数：
```js
function compare (value1, value2) {
    if (value1 < value2) {
        return -1;
    } else if (value1 > value2) {
        return 1;
    } else {
        return 0;
    }
}

values.sort(compare);
```

## 操作方法
`concat()` 方法可以基于当前数组中的所有项创建一个新数组。该方法会先创建当前数组的一个副本，然后将接收到的参数添加到这个副本的末尾，最后返回一个新数组。如果参数是数组类型，那么该方法会将这些数组中的每一项都添加到结果数组中：
```js
var colors = ['red', 'green'];
var colors2 = colors.concat('yellow', ['black', 'brown']);
console.log(colors);  // ["red", "green"]
console.log(colors2);  // ["red", "green", "yellow", "black", "brown"]
```

`slice()` 方法基于当前数组是的一个或多个项创建一个新数组。`slice()` 方法可以接受一个或两个参数，即要返回项的起始和结束位置。
如果只有一个参数，`slice()` 方法返回从该参数指定位置开始到当前数组末尾的所有项。如果有两个参数，该方法返回起始和结束位置之间的项，但不包括结束位置；
如果参数中有负数，则用数组长度加上该数来确定相应的位置。`slice()` 不影响原始数组：
```js
var colors = ["red", "green", "yellow", "black", "brown"];
var colors2 = colors.slice(1);
var colors3 = colors.slice(1,4);
console.log(colors2); // ["green", "yellow", "black", "brown"]
console.log(colors3); // ["green", "yellow", "black"]
console.log(colors.slice(1,-1);) // ["green", "yellow", "black"]
```

`splice()` 方法和主要用途是向数组的中部插入项，主要用法有如下三种：

- 删除：可以删除任意数量的项，只需指定2个参数：要删除的第一项的位置和要删除的项数，如 `splice(0,2)` 会删除数组中的前两项。
- 插入：可以向指定位置插入任意数量的项，需指定3个参数：起始位置、0（要删除的项数）、要插入的项。如 `splice(2, 0, 'red', 'green')`
- 替换：可以向指定位置插入任意数量的项，且同时删除任意数量的项。如`splice(2, 1, 'red', 'green')`。

```js
var colors = ["red", "green", "yellow"];
var removed = colors.splice(0 ,1)

console.log(colors); // ["green", "yellow"]
console.log(removed); // ["red"]

removed = colors.splice(1, 0, 'yellow', 'orange');
console.log(colors);  // ["green", "yellow", "orange", "yellow"]
console.log(removed); // []

removed = colors.splice(1, 1, 'red', 'purple');
console.log(colors); // ["green", "red", "purple", "orange", "yellow"]
console.log(removed); //  ["yellow"]
```

## 位置方法
数组实例有两个位置方法：`indexOf()` 和 `lastIndexOf()`。
这两个方法都接收两个参数：要查找的项和查找起点位置的索引（可选）。`indexOf` 从开头查找，`lastIndexOf`从数组的末尾开始向前查找。
这两个方法都返回要查找的项在数组中的位置，或在没找到的情况下返回 -1。**在比较时，会使用全等(===)来比较**

## 迭代方法
数组定义了5个迭代方法，每个方法都接收两个参数：要在每一项上运行的函数和运行该函数的作用域对象本身（可选）。

- `every()`: 对数组中的每一项运行给定函数，如果该函数对每一项都返回 `true`，则返回 `true`
- `filter()`: 对数组中的每一项都运行给定函数，返回 `true` 的项目组成的数组
- `forEach()`: 对数组中的每一项运行给定函数，无返回值
- `map()`: 对数组中的每一项运行给定函数，返回每次函数调用结果组成的数组
- `some()`: 对数组中的每一项运行给定函数，如果该函数任一项返回 `true`，则返回 `true`.

**以上方法不会修改原数组中的值**

```js
var numbers = [1, 2, 3, 4, 5, 6];
var mapResult = numbers.map(function (item, index, array) {
    return item * 2;
});
console.log(mapResult); // [2, 4, 6, 8, 10, 12]
```

## 数组归并
ES5 提供了两个归并数组的方法：`reduce()` 和 `reduceRight()`。这两个方法都会迭代数组的所有项，然后构建一个最终返回值。`reduce` 方法从数组第一项开始，逐个遍历到最后。`reduceRight` 方法则从数组的最后一项开始，向前遍历到第一项。
两个函数都可以接收两个参数：第一个是在每一项上都调用的函数，第二个是可选值，是作为归并基础的初始值。
传入 `reduce` 和 `reduceRight` 的函数接收 4 个参数：前一个值，当前值，项的索引和数组对象。
```js
var values = [1, 2, 3, 4, 5];
var sum = values.reduce(function (prev, cur, index, array) {
    return prev + cur;
})
console.log(sum); // 15
```

# RegExp 正则表达式
JavaScript 通过 `RegExp` 类型来支持正则表达式，创建方式如下：
```js
let expression = /pattern/gim;
```

## RegExp 实例方法
RegExp 对象的主要方法是 `exec()`，该方法是专门为捕获组设置的。`exec()` 接受一个参数，即要应用模式的字符串，然后返回一个包含第一个匹配项信息的数组；或者在没有匹配项时返回 null。
```js
let text = 'mom and dad and baby';
let pattern = /mom( and dad( and baby)?)?/gi;
let matches = pattern.exec(text);
console.log(matched.index); // 0 匹配开始的下标
console.log(matches.input); // mom and dad and baby  应用匹配的字符串
console.log(matches[0]); // mom and dad and baby
console.log(matches[1]); // and dad and baby
console.log(matches[2]); // and baby
```

# call() 和 apply()
每个函数都包含两个非继承而来的方法：`apply()` 和 `call()`，这两个方法的用途都是在特定的作用域中调用函数，即设置函数体内的 `this` 对象的值。
`apply()` 接收两个参数：一个是在其中运行的函数作用域，另一个是参数数组。参数数组可以是 Array 实例，也可以是 `arguments` 对象：

```js
function sum (num1, num2) {
    return num1 + num2;
}

function callSum1 (num1, num2) {
    return sum.apply(this, arguments);
}

function callSum2 (num1, num2) {
    return sum.apply(this, [num1, num2]);
}
```

`call()` 接收参数的方式不同，传递给函数的参数必须列举出来：
```js
function sum (num1, num2) {
    return num1 + num2;
}

function callSum1 (num1, num2) {
    return sum.call(this, [num1, num2]);
}
```

# string 字符串类型
JavaScript 有三个基于子字符串创建新字符串的方法： `slice()`, `substr()` 和 `substring()`
这三个方法都接受一个或者两个参数。第一个参数指定子字符串的开始位置，第二个参数表示子字符串到哪里结束。`slice()` 和 `substring()` 的第二个参数指定的是子字符串最后一个字符后面的位置。而 `substr()` 的第二个参数指定的则是返回的字符的个数。如果没有给定第二个参数，则将字符串的长度作为结束位置。
```js
let str = 'hello world';
str.slice(3); // lo world
str.substring(3); // lo world
str.substr(3); // lo world
str.slice(3, 7); // lo w
str.substring(3, 7); // lo w
str.substr(3, 7); // lo worl
```
 
当传递的参数是负值的情况下，`slice()` 方法会将传入的负值与字符串的长度相加，`substr()` 方法将负的第一个参数加上字符串的长度，将负的第二个参数转换成0，`substring()` 方法会把所有负值参数转换成0.
```js
str.slice(-3); // rld
str.substring(-3); // hello world
str.substr(-3); // rld
str.slice(3, -4); // lo w
str.substr(3, -4); // ''
```