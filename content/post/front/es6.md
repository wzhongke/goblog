---
title: es6
date: 2018-04-21 11:28:11
tags: ["javascript"]
categories: ["javascript"]
---

# babel: ES6 转换成 ES5
babel 是一个将ES6转换成ES5的转码器，babel的配置文件是 `.babelrc`，存放在项目的根目录。
全局安装babel：
```
$ npm install --global babel-cli
```

配置文件如下：
```
{
    "presets": [
      "latest",
      "react",
      "stage-2"
    ],
    "plugins": []
  }
```

安装转码规则方式如下：
```shell
# 最新转码规则
$ npm install --save-dev babel-preset-latest

# react 转码规则
$ npm install --save-dev babel-preset-react

# 不同阶段语法提案的转码规则（共有4个阶段），选装一个
$ npm install --save-dev babel-preset-stage-0
$ npm install --save-dev babel-preset-stage-1
$ npm install --save-dev babel-preset-stage-2
$ npm install --save-dev babel-preset-stage-3
```

## 使用
babel的基本用法如下：
```shell
# 转码结果输出到标准输出
$ babel example.js

# 转码结果写入一个文件
# --out-file 或 -o 参数指定输出文件
$ babel example.js --out-file compiled.js
# 或者
$ babel example.js -o compiled.js

# 整个目录转码
# --out-dir 或 -d 参数指定输出目录
$ babel src --out-dir lib
# 或者
$ babel src -d lib

# -s 参数生成source map文件
$ babel src -d lib -s
```

# let 关键字
`let` 同 `var` 类似，但是声明的变量，只在 `let` 命令所在的代码块内有效。
```js
var a = [];
for (var i = 0; i < 10; i++) {
    a[i] = function () {
        console.log(i);
    };
}
a[6](); // 10

var a = [];

for (let i = 0; i < 10; i++) {
    a[i] = function () {
        console.log(i);
    };
}
a[6](); // 6

// for 循环中，设置循环变量 let i=0 中的 i 是一个父作用域
// 循环体内是一个单独的子作用域
for (let i = 0; i < 3; i++) {
  let i = '123';
  console.log(i); // 123
}
```

**`let` 声明的变量必须在声明之后使用，否则会报错，而且在变量作用域内，变量声明之前不可以使用该变量**。
```js
typeof x; // 会抛出错误： ReferenceError
let x;
```

## const
`const` 声明一个只读的常量，一旦声明，常量的值就不能改变。这意味着 `const` 声明的变量必须立即初始化，而且其声明的变量同 `let` 一样就有块级作用域。
```js
if (true) {
    const PI = 3.14;
}
PI // PI is not defined.
```

同java中的常量一样，不能够修改其指向的内存地址，却可以修改对象的属性，比如：
```js
const CONST = {};
CONST.p = 123;
```

如果想要冻结一个对象，可以使用如下方式：
```js
var constantize = (obj) => {
    Object.freeze(obj);
    Object.keys(obj).forEach( (key, i) => {
        if ( typeof obj[key] === 'object' ) {
            constantize( obj[key] );
        }
    });
};
```

# 数组
对于数组解构赋值可以用如下方式：
```js
let [a, b, c] = [1, 2, 3];
a // 1
b // 2
c //3

let [ , , third] = ["foo", "bar", "baz"];
third // "baz"

let [head, ...tail] = [1, 2, 3, 4];
head // 1
tail // [2, 3, 4]

// 如果解构不成功，那么变量的值就等于 undefined
let [foo] = [];
let [bar, foo] = [1];
foo // undefined

// 指定默认值，当 y===undefined 时，默认值才生效
let [x, y='b'] = ['a', undefined] // y='b'

// 对对象进行解构
let obj = {
  p: [
    'Hello',
    { y: 'World' }
  ]
};

let { p, p: [x, { y }] } = obj;
x // "Hello"
y // "World"
p // ["Hello", {y: "World"}]

// 字符串解构
const [a, b] = 'he';
a // "h"
b // "e"

// 第一个是对象的属性值
let {length : len} = 'he';
len // 2

// 对函数的解构，也可以使用默认值
function move({x = 0, y = 0} = {}) {
  return [x, y];
}

move({x: 3, y: 8}); // [3, 8]
move({x: 3}); // [3, 0]
move({}); // [0, 0]
move(); // [0, 0]
```

对于解构，需要等号右边是的数据结构具有 Iterator 接口才能够解构成功，否则会报错。
```js
function * fibs () {
    let a = 0;
    let b = 1;
    while (true) {
        yield a;
        [a, b] = [b, a+b];
    }
}

let [first, second, third, fourth] = fibs();
fourth // 2
```

## 解构的应用
1. 变量交换
```js
let x=1, y=2;
[x,y] = [y,x];
```

2. 函数返回多个值
```js
function example() {
    return [1, 2, 3];
}
let [a, b, c] = example();
```

3. 函数参数的定义
```js
function f([x, y, z]) { ... }
f([1, 2, 3]);
f({z: 3, y:2, x:1});
```

4. 提取JSON数据
```js
let jsonData = {
    id: 42,
    status: "OK",
    data: [867, 5309]
};

let { id, status, data: number } = jsonData;
```

5. 遍历map解构
```js
const map = new Map();
map.set('first', 'hello');
map.set('second', 'world');

for (let [key, value] of map) {
    console.log(key + " is " + value);
}

// 获取键名
for (let [key] of map) {
  // ...
}

// 获取键值
for (let [,value] of map) {
  // ...
}
```

# 字符串
ES6对字符串做了些扩展

## 字符串遍历
ES6为字符串提供了遍历器接口：
```js
for (let c of 'test') {
    console.log(c);
}

// 可以识别大于 0xFFFF 的码
for (let c of String.fromCodePoint(0x20BB7)) {
    console.log(c);
}
```

## normalize()
`normalize()` 方法把字符的不同表示方法统一为同样的形式，目前不能识别三个或三个以上字符的合成。这种情况下，还是只能使用正则表达式，通过 Unicode 编号区间判断。
```js
'\u01D1'.normalize() === '\u004F\u030C'.normalize() //true
```

## 包含
- includes(s, startPos)：返回布尔值，表示是否找到了参数字符串。
- startsWith(s, startPos)：返回布尔值，表示参数字符串是否在原字符串的头部。
- endsWith(s, startPos)：返回布尔值，表示参数字符串是否在原字符串的尾部。

## repeat()
返回重复次数的字符串:
```js
'hello'.repate(2); // hellohello
```

## 字符串补全
字符串长度补全功能如下：
```js
'x'.padStart(5, 'ab') // 'ababx'
'x'.padStart(4, 'ab') // 'abax'

'x'.padEnd(5, 'ab') // 'xabab'
'x'.padEnd(4, 'ab') // 'xaba'
```

## 模板字符串
模板字符串（template string）是增强版的字符串，用反引号（`）标识。
它可以当作普通字符串使用，也可以用来定义多行字符串，或者在字符串中嵌入变量。可以在 `${ ... }` 中放入任何合法的JavaScript表达式
```js
// 将变量使用 ${} 嵌入到模板中
$('#result').append(`
    There are <b>${basket.count}</b> items
    in your basket, <em>${basket.onSale}</em>
    are on sale!
`);
```

## 标签模板
模板可以紧跟在一个函数名后面，该函数将被调用来处理这个模板字符串。
```js
alert`123`; // 等同于 alert(123)

// 模板中有变量的情况
let a=5, b=10;
tag`Hello ${a+b} word ${a*b}`;
tag(['Hello ', ' word ',''], 15, 50);
// tag 是一种这样的函数
function tag (stringArr, ...values) {}

// 过滤HTML字符串
let message = SafeHTML`<p>${sender} has sent you a message.</p>`;
function SafeHTML (templateData) {
    let s = templateData[0];
    for (let i=1; i<arguments.length; i++) {
        let arg = String(arguments[i]);

        s + arg.replace(/&/g, "&amp;")
              .replace(/</g, "&lt;")
              .replace(/>/g, "&gt;");

        s += templateData[i];
    }
    return s;
}
```

# 函数扩展
ES6 允许为函数的参数指定默认值，如下：
```js
function log(x, y=0) {}
// 参数默认值不是传值的，每次都会重新计算
let x = 1;
function f (p = x+1) {
    return p;
}
f(); // 2
x = 2;
f(); //3
```

ES6 还引入了 rest 参数，即 `... value`，用于获取函数多余的参数，这样就无需使用 `arguments` 对象。
```js
function add(...values) {
  let sum = 0;

  for (var val of values) {
    sum += val;
  }

  return sum;
}

add(2, 5, 3) // 10
```

## 箭头函数
ES6 中允许使用 `=>` 定义函数：
```js
let f = v => v;
// 等价于
lef f = fucntion(v) {
    return v;
};
```

如果函数需要多个参数或者不需要参数，就要使用圆括号代表参数部分；如果函数体中有大于一条语句，就要用大括号括起来：
```js
let f = () => 5;
let sum = (a, b) => {return a + b;};
```

**箭头函数体内的`this`对象，就是指向定义时所在的对象，而不是使用时所在的对象。** 即箭头函数中的`this`指向固化了，这是因为箭头函数中没有自己的`this`，而是引用外层的`this`.
```js
function foo() {
  setTimeout(() => {
    console.log('id:', this.id);
  }, 100);
}

var id = 21;
foo.call({ id: 42 }); // id: 42
```

## 函数绑定运算符
`::` 是函数绑定运算符，它将左边的对象做为上下文环境（`this`）绑定到右边的函数上。
```js
foo::bar;
// 等同于
bar.bind(foo);

//如果双冒号左边为空，右边是一个对象的方法，则等于将该方法绑定在该对象上面。
var method = obj::obj.foo;
// 等同于
var method = ::obj.foo;
```

# 数组

## 数组扩展运算符
扩展运算符`...`，将一个数组转换成逗号分隔的参数序列。
```js
console.log(...[1,2,3]) // 1,2,3

function add(x, y) {
    return x + y;
}

const numbers = [4, 38];
add(...numbers) // 42

// ES6 的写法，可以不将数组转换成序列
Math.max(...[14, 3, 77])

// 等同于
Math.max(14, 3, 77);

// 字符串扩展
[...'hello'] //  [ "h", "e", "l", "l", "o" ]
```

## `Array.from()`
`Array.from()` 可以将类数组对象或者具有Iterator接口的对象转换成数组。
```js
Array.from('hello')  // ['h', 'e', 'l', 'l', 'o']

// 第二个参数用来处理数组中每个元素
Array.from(arrayLike, x => x * x);
// 等同于
Array.from(arrayLike).map(x => x * x);
```

## `Array.of()`
`Array.of()` 用来将一组值转换成数组。
```js
Array.of(3, 11, 8) // [3,11,8]
```

## 数组的`copyWithin`方法
`copyWithin(target, start=0, end=this.length)` 方法会在当前数组内部，将指定位置的成员复制到其他位置（会覆盖原有成员），然后返回当前数组，会修改数组。
```js
[1, 2, 3, 4, 5].copyWithin(0, 3) // [4, 5, 3, 4, 5]
```

该方法接收3个参数：
- target ： 开始替换数据的位置
- start ：读取数据开始的位置
- end ： 在该位置之前， 停止读取数据

## `find` 和 `findIndex`
`find` 方法用于找出第一个符合条件的数组成员。它的参数是一个回调函数，所有数组成员依次执行该回调函数，直到找出第一个返回值为true的成员。如果没有返回值为true的成员，则返回undefined。
```js
// find 的回调函数的参数依次是 值，下标，数组
[1, 5, 10, 15].find(function(value, index, arr) {
  return value > 9;
}) 
```

`findIndex` 同 `find` 类似，只是返回的是第一个符合条件的数组的下标，若没有符合条件的成员，返回-1.

## 数组遍历方式
可以使用数组的 `keys()`、`values()` 和 `entries()`来遍历一个数组，分别返回的是键、值和键值对。
```js
// 键，也就是下标
for (let index of ['a', 'b'].keys()) {
    console.log(index);
}
// 0, 1
for (let value of ['a', 'b'].values()) {
    console.log(value);
}
// "a", "b"
for (let [index, elem] of ['a', 'b'].entries()) {
    console.log()
}

```

## 数组包含
`Array.prototype.includes(value, start=0)` 方法返回一个布尔值，表示某个数组是否包含给定的值。
`value` 是要查找的值，`start` 是开始查找的位置，如果是负数，则从倒数位置开始
```js
[1, 2, 3].includes(2)     // true
[1, 2, 3].includes(3, 3);  // false
```

# 对象扩展

## 属性简介表示
ES6可以在对象中直接写变量，这时属性名为变量名，属性值为变量值。
```js
const foo = 'bar';
const baz = {foo};
baz // {foo: "bar"}

// 等同于
const baz = {foo: foo};

function f(x, y) {
    return {x, y};
}

f(1, 2) // {x:1, y:2}

// 方法简写
const o = {
    method() {
        return "Hello!";
    }
};
// 等同于
const o = {
    method: function () {
        return "Hello!";
    }
}
```

CommonJS 模块输出一组变量，就可以用如下方式：
```js
let ms = {};

function getItem (key) {
    return key in ms ? ms[key] : null;
}
module.exports = {getItem};
// 等同于
module.exports = {
    getItem: getItem
}
```

方法的get和set简写
```js
const cart = {
  _wheels: 4,

  get wheels () {
    return this._wheels;
  },

  set wheels (value) {
    if (value < this._wheels) {
      throw new Error('数值太小了！');
    }
    this._wheels = value;
  }
}
cart.wheels // 4
cart.wheels = 1; // Uncaught Error: 数值太小了！

```

## 属性名
ES6 中可以用表达式定义变量名或方法名：
```js
let propKey = 'foo';

let obj = {
    [propKey]: true,
    ['a' + 'bc']: 123
};

let obj = {
    ['h' + 'ello']() {
        return 'hi';
    }
};

obj.hello() // hi
```

## Object.assign()
`Object.assign(target, source1, source2, ...);` 用于将源对象所有可枚举的属性复制到目标对象中。
如果目标对象与源对象有同名属性，或多个源对象有同名属性，则后面的属性会覆盖前面的属性。
**Object.assign() 是浅拷贝**
```js
const target = { a: 1, b: 1 };

const source1 = { b: 2, c: 2 };
const source2 = { c: 3 };

Object.assign(target, source1, source2);
target // {a:1, b:2, c:3}
```

比较常用的用途如下：
```js
// 为对象添加属性
class Point {
    constructor(x, y) {
        Object.assign(this, {x, y});
    }
}

// 为对象添加方法
Object.assign(SomeClass.prototype, {
    someMethod(arg1, arg2) { },
    anotherMethod() { }
});

// 合并对象
const merge = (target, ...sources) => Object.assign(target, ...sources);

// 为对象指定默认值
const DEFAULTS = {
  logLevel: 0,
  outputFormat: 'html'
};
options = Object.assign({}, DEFAULTS, options);
```

## 属性的枚举和遍历
对象的每个属性都有一个描述对象（Descriptor），用来控制该属性的行为。`Object.getOwnPropertyDescriptor`方法可以获取该属性的描述对象。
描述对象的属性`enumerable`为`false`时，表明该属性是不可枚举的。下面四个操作会忽略不可枚举的属性。
- `for...in` ： 只遍历对象自身的和继承的可枚举的属性。
- `Object.keys()` ：返回对象自身的所有可枚举的属性的键名。
- `JSON.stringify()` ：只串行化对象自身的可枚举的属性。
- `Object.assign()` ： 忽略enumerable为false的属性，只拷贝对象自身的可枚举的属性。

# `Set` 数据结构
ES6 提供了新的数据结构`Set`，它成员的值是唯一的，不存在重复的值。`Set` 可以用数组作为参数来初始化。
向`Set` 加入值时，不会进行类型转换，它是用 `===` 来判断两个值是否相等的。但是两个对象是不相等的。
```js
let set = new Set([1,2,3,4,3,2,1]); // [1,2,3,4]
```

对 `Set` 集合的操作主要有如下四种方法：
- `add(value)` : 添加值，返回Set本身，可以链式调用
- `has(value)` : 判断值是否在Set中，返回布尔值
- `delete(value)` : 删除值，返回是否删除
- `clear()` : 清空所有成员

`Set` 有四种遍历方法，因为它只有值没有键，所以key和value都是它的值。
- `keys()` : 返回键名的遍历器
- `values()` : 返回键值的遍历器
- `entries()` : 返回键值对的遍历器
- `forEach()` : 使用回调函数遍历每个成员

```js
let set = new Set([1, 2, 3]);
set.add('color');

for (let item of set.keys()) {
    console.log(item);
}

for (let item of set.values()) {
    console.log(item);
}

for (let [key, value] of set.entries()) {
    console.log(key + " : " + value);
}

set.forEach((value, key, setSelf) => console.log(key + ' : ' + value))

// 将set集合转换成数组，这样就可以使用数组的 map、filter等方法
let arr = [...set];
// 直接改变数组中的值
set = new Set([...set].map(val => val * 2));
```

# `Map` 数据结构
任何具有 `Iterator` 接口、且每个成员都是一个双元素的数组的数据结构都可以当作Map构造函数的参数。
`Map` 的键是跟内存地址绑定的，只要内存地址不一样，就视为两个键。如果 `Map` 的键是一个简单类型的值（数字、字符串、布尔值），则只要两个值严格相等，`Map` 将其视为一个键。

`Map` 有以下的属性和方法：
- `size` : `Map` 中成员的数目
- `get(key)` : 读取 `key` 对应的键值，如果找不到 `key` ，返回 `undefined`。
- `set(key, value)` : 设置键名 `key` 对应的键值为 `value`，然后返回该 `Map` 以便于链式调用
- `has(key)` : 判断 `Map` 中是否有 `key`
- `delete(key)` : 删除键为 `key` 的键值对
- `clear()` : 清楚 `Map` 中所有成员

`Map` 的遍历方法同 `Set` 类似。

```js
const map = new Map([
    ['F', 'no'],
    ['T',  'yes'],
]);

for (let key of map.keys()) {
  console.log(key);
}

for (let value of map.values()) {
  console.log(value);
}

for (let item of map.entries()) {
  console.log(item[0], item[1]);
}

for (let [key, value] of map.entries()) {
  console.log(key, value);
}
// 等同于使用map.entries()
for (let [key, value] of map) {
  console.log(key, value);
}
```

## `Map` 同其他数据结构互转
```js
// map 转数组
const map = new Map()
  .set(true, 7)
  .set({foo: 3}, ['abc']);
let arr = [...map];

// map 转对象
function strMapToObj(strMap) {
    let obj = Object.create(null);
    for (let [k,v] of strMap) {
        obj[k] = v;
    }
    return obj;
}

// 对象转 map
function objToStrMap(obj) {
    let strMap = new Map();
    for (let k of Object.keys(obj)) {
        strMap.set(k, obj[k]);
    }
    return strMap;
}

// Map 转为 JSON
function strMapToJson(strMap) {
    return JSON.stringify(strMapToObj(strMap));
}

// JSON 转为 Map
function jsonToStrMap(jsonStr) {
    return objToStrMap(JSON.parse(jsonStr));
}
```

# 代理 -- `Proxy(target, handler)`
Proxy 用于修改某些操作的默认行为，等同于在语言层面进行修改。

`Proxy(target, handler)` 中的 `target` 是要代理的对象，而 `handler` 用来定制拦截行为。
```js
// obj 代理了属性的读写操作
var obj = new Proxy({}, {
    get: function (target, key, receiver) {
        console.log(`getting ${key}!`);
        return Reflect.get(target, key, receiver);
    },
    set: function (target, key, value, receiver) {
        console.log(`setting ${key}!`);
        return Reflect.set(target, key, value, receiver);
    }
});

obj.count = 1
//  setting count!
++obj.count
//  getting count!
//  setting count!
//  2
```

代理所支持的拦截对象如下，其中 `receiver` 参数是代理对象本身：

操作          |  说明
:-------------|:-----------------
`get(target, propKey, receiver)` | 拦截对象属性的读取
`set(target, propKey, value, receiver)` | 拦截对象属性的赋值
`has(target, propKey)` | 拦截 `propKey in proxy` 的操作，返回一个布尔值。
`deleteProperty(target, propKey)`  | 拦截`delete proxy[propKey]`的操作，返回一个布尔值。
`ownKeys(target)`    | 拦截 `Object.getOwnPropertyNames(proxy)`、`Object.getOwnPropertySymbols(proxy)`、`Object.keys(proxy)`，返回一个数组。该方法返回目标对象所有自身的属性的属性名，而`Object.keys()`的返回结果仅包括目标对象自身的可遍历属性。
`getOwnPropertyDescriptor(target, propKey)` | 返回属性的描述对象。
`defineProperty(target, propKey, propDesc)` | 拦截`Object.defineProperty(proxy, propKey, propDesc)`、`Object.defineProperties(proxy, propDescs)`，返回一个布尔值。
`preventExtensions(target)` | 拦截`Object.preventExtensions(proxy)`，返回一个布尔值。
`getPrototypeOf(target)` | 拦截`Object.getPrototypeOf(proxy)`，返回一个对象。
`isExtensible(target)` | 拦截 `Object.isExtensible(proxy)`，返回一个布尔值。
`setPrototypeOf(target, proto)` | 拦截`Object.setPrototypeOf(proxy, proto)`，返回一个布尔值。如果目标对象是函数，那么还有两种额外操作可以拦截。
`apply(target, object, args)` | 拦截 Proxy 实例作为函数调用的操作，比如 `proxy(...args)`、`proxy.call(object, ...args)`、`proxy.apply(...)`。
`construct(target, args)` | 拦截 Proxy 实例作为构造函数调用的操作，比如`new proxy(...args)`

## 代理对象的this指向问题
使用Proxy代理对象时，目标对象的 `this` 会指向Proxy对象。
```js
const target = {
    m: function () {
        console.log(this === proxy);
    }
};
const handler = {};

const proxy = new Proxy(target, handler);

target.m() // false
proxy.m()  // true
```

对于那些通过 `this` 才能访问的属性或方法，使用代理无法直接拿到，通过 `this` 绑定原生对象可以拿到。
```js
const target = new Date('2015-01-01');
const handler = {
    get(target, prop) {
        if (prop === 'getDate') {
        return target.getDate.bind(target);
        }
        return Reflect.get(target, prop);
    }
};
const proxy = new Proxy(target, handler);

proxy.getDate() // 1
```

## `Reflect` 类
`Reflect` 类的方法同 `Proxy` 可以拦截的方法一一对应，这就让 `Proxy` 对象可以很方便地调用 `Reflect` 方法。

# `Promise`
`Promise` 是一个容器，保存着异步执行事件的结果。它是JavaScript异步编程的一种解决方案，比回调函数和事件更合理、更强大。
`Promise` 有两个特点：
1. 对象的状态不受外界影响，只有异步操作的结果可以影响对象的状态：pending（进行中）、fulfilled（已成功）和rejected（已失败）
2. 一旦状态改变，就不会再变，任何时候都可以得到这个结果。

`Promise(function(resolve, reject) {})` 的构造函数接受一个函数作为参数，该函数的两个参数分别是resolve和reject。它们是两个函数，由 JavaScript 引擎提供。
`resolve` 函数的作用是，将 `Promise` 对象的状态从“未完成”变为“成功”，在异步操作成功时调用，并将异步操作的结果，作为参数传递出去；`reject` 函数的作用是，将 `Promise` 对象的状态从“未完成”变为“失败”，并将异步操作报出的错误，作为参数传递出去。
`Promise` 实例生成以后，可以用 `then` 方法分别指定 `resolved` 状态和 `rejected` 状态的回调函数。
```js
promise.then(function(value) {
    // success
}, function(error) {
    // failure
});
```

下面是一个使用 `Promise` 完成ajax的例子
```js
const getJSON = function(url) {
    const promise = new Promise(function(resolve, reject) {
        const handler = function() {
            if (this.readyState !== 4) {
                return ;
            }
            if (this.status === 200) {
                resolve(this.response);
            } else {
                reject(new Error(this.statusText));
            }
        };
        const client = new XMLHttpRequest();
        client.open("GET", url);
        client.onreadystatechange = handler;
        client.responseType = "json";
        client.setRequestHeader("Accept", "application/json");
        client.send();
    });

    return promise;
}

getJSON("/test.json").then(function(json) {
    console.log(json);
}, function(error) {
    console.log("Something happened: " + error);
});
```

## `Promise.prototype.then()`
`Promise` 的 `then` 方法返回的是一个新的 `Promise` 实例，因此可以链式调用，这样就可以指定一组按照次序调用的回调函数：
```js
getJSON("/post/1.json").then(
    post => getJSON(post.commentURL)
).then(
    comments => console.log("resolved: ", comments),
    err => console.log("rejected: ", err)
);
```

## `Promise.prototype.catch()`
`catch(rejection)` 方法等同于 `then(null, rejection)`，用于指定发生错误时的回调函数。
`Promise`对象的错误具有“冒泡”性质，会一直向后传递，直到被捕获为止。也就是说，错误总是会被下一个 `catch` 语句捕获。
```js
getJSON('/post/1.json').then(function(post) {
     return getJSON(post.commentURL);
}).then(function(comments) {
     // some code
}).catch(function(error) {
    // 处理前面三个Promise产生的错误
});
```

在 `resolve` 语句之后抛出的异常，不会被捕获。因为 `resolve` 之后，`Promise` 的状态已经发生改变，`Promise` 的状态一旦改变，就永久保持，不会再变了。
```js
const promise = new Promise(function(resolve, reject) {
    resolve('ok');
    throw new Error('error'); // 该异常不会被catch捕获
});

promise.then(function(value) {console.log(value);})
    .catch(function(err) {console.log(err);})

```

推荐下面的第二种写法，因为 `catch` 中可以捕获 `then` 中的异常，`catch` 方法返回的是一个新的 `Promise` 对象，因此还可以继续调用 `Promise` 的方法。
```js
// bad
promise.then(function(data) {
    // success
  }, function(err) {
    // error
  });

// good
promise.then(function(data) { //cb
    // success
  })
  .catch(function(err) {
    // error
  });
```

**如果没有`catch`方法处理回调函数中的异常，`Promise` 会打印出异常，但不会终止程序运行**。

## `Promise.prototype.finally()`
`finally()` 方式用于不管 Promise 对象最终状态如何，都会执行的操作。因为该方法不接受任何参数，因此其处理的事情应该同 Promise 无关。
```js
promise.then (result => {})
    .catch(err => {})
    .finally(() => {})
```

## `Promise.prototype.all([...promise])`
`all([...promise])` 接受具有 `Iterator` 接口的对象作为参数，每个元素都需要是 `Promise` 对象。如果不是，会先调用 `Promise.resolve()`来将其转换为 `Promise` 对象。
如果 `[...promise]` 中所有元素的状态都变为 `fulfilled`，那么 `all` 返回的 `Promise` 实例才会成为 `fulfilled`，只要有一个元素成为 `reject` 状态，那么该实例就会成为 `reject` 状态。

如果作为参数的 `Promise` 实例，自己定义了 `catch` 方法，那么它一旦被 `rejected`，并不会触发 `Promise.all()` 的 `catch` 方法。
```js
const p1 = new Promise((resolve, reject) => {
  resolve('hello');
}).then(result => result)
    .catch(e => e);

const p2 = new Promise((resolve, reject) => {
  throw new Error('报错了');
}).then(result => result)
    .catch(e => e);

Promise.all([p1, p2]).then(result => console.log(result))
    .catch(e => console.log(e));
// ["hello", Error: 报错了]
```

## `Promise.prototype.race()`
`race([...promise])` 的参数同 `all()` 方法的参数一样，返回的也是 `Promise` 实例。参数中首先发生状态改变的元素会传递给该实例的回调函数。
```js
const p = Promise.race([
    fetch('/resource-that-may-take-a-while'),
    new Promise(function (resolve, reject) {
        setTimeout(() => reject(new Error('request timeout')), 5000)
    })
]);

p.then(console.log)
  .catch(console.error);
```

## `Promise.prototype.resolve()`
`resolve(object)` 将对象转换为 `Promise` 对象。
```js
Promise.resolve('foo')
// 等价于
new Promise(resolve => resolve('foo'))
```

## `Promise.prototype.reject()`
`reject()` 返回一个 `reject` 状态的 `Promise` 对象。
```js
const thenable = {
    then(resolve, reject) {
        reject('出错了');
    }
};

Promise.reject(thenable)
    .catch(e => {
        console.log(e === thenable)
    })
```

# `Generator` 生成器函数
Generator 函数是ES6提供的一种异步编程解决方案。可将其理解为封装了多个内部状态的状态机。执行 Generator 函数会返回一个遍历器对象，Generator 还是一个遍历器生成函数。
其形式如下，`function` 关键字后紧跟一个星号，使用`yield`(`yield` 表达式只能用在 `Generator` 函数中) 返回状态：
```js
function *myGenerator() {
    yield 'hello';
    yield 'world';
    return 'ending';
}
let generator = new myGenerator();
```

必须调用遍历器对象的 `next` 方法，使得指针移向下一个状态。也就是说，每次调用 `next` 方法，内部指针就从函数头部或上一次停下来的地方开始执行，直到遇到下一个 `yield `表达式（或 `return` 语句）为止。

## `next()` 方法
`next()` 可以有一个参数，该参数会被当作上一个yield表达式的返回值。
```js
function *f() {
    for(var i = 0; true; i++) {
        var reset = yield i;
        if(reset) { i = -1; }
    }
}

// 构造一个迭代器 g 来控制这个生成器
var g = f();

// 这里启动 f()，表示开始执行 f 函数中的内容
g.next() // { value: 0, done: false }
g.next() // { value: 1, done: false }
g.next(true) // { value: 0, done: false }
```

## `for .. of ..`
生成器函数可以使用 `for ... of ...` 来遍历。下面是一个利用 `Generator` 函数和 `for...of` 循环，实现斐波那契数列的例子。
```js
function* fibonacci() {
    let [prev, curr] = [0, 1];
    for (;;) {
        [prev, curr] = [curr, prev + curr];
        yield curr;
    }
}

for (let n of fibonacci()) {
    if (n > 1000) break;
    console.log(n);
}
```

利用 `for ... of ...` 循环可以遍历任意对象的方法，即使原生JavaScript不支持，也可以通过Generator函数给其加上相应的接口。
```js
function* objectEntries(obj) {
    let propKeys = Reflect.ownKeys(obj);

    for (let propKey of propKeys) {
        yield [propKey, obj[propKey]];
    }
}

let jane = { first: 'Jane', last: 'Doe' };

for (let [key, value] of objectEntries(jane)) {
    console.log(`${key}: ${value}`);
}
// first: Jane
// last: Doe
```

## `Generator.prototype.throw()`
Generator 的 `throw` 方法可以在函数体外抛出错误，然后在 Generator 函数中捕获。
```js
var g = function* () {
    try {
        yield;
    } catch (e) {
        console.log('内部捕获', e);
    }
};

var i = g();
i.next();

// 第一个错误被 g 函数体内的 catch 捕获
// 因为catch已经执行，所以第二个错误被函数体外的 catch 捕获
try {
    i.throw('a');
    i.throw('b');
} catch (e) {
    console.log('外部捕获', e);
}
// 内部捕获 a
// 外部捕获 b
```

`throw` 被捕获之后，生成器会自动执行一次 `next`，返回下一次的 `yield`。
如果 `throw` 没有被捕获，就不会再执行下去了。如果此后还调用 `next`，将会返回一个 `{value: undefined, done: true}` 的值。


## `Generator.prototype.return()`
`return(value)` 方法可以返回给定的值，并且终结遍历器 `Generator` 函数。若不提供 `value` 参数，则返回 `undefined`；若函数内有 `try ... finally`，那么 `return` 方法会推迟到 `finally` 代码块执行完再执行。
```js
function* numbers () {
    yield 1;
    try {
        yield 2;
        yield 3;
    } finally {
        yield 4;
        yield 5;
    }
    yield 6;
}
var g = numbers();
g.next() // { value: 1, done: false }
g.next() // { value: 2, done: false }
g.return(7) // { value: 4, done: false }
g.next() // { value: 5, done: false }
g.next() // { value: 7, done: true }
```

如果生成器中有无限循环，也可以通过 `break` 来终止生成器，对于释放生成器中的资源很有用。+
```js
function *something() {
    try {
        let nextVal;
        while (true) {
            if (nextVal === undefined) {
                nextVal = 1;
            } else {
                nextVal = (3*nextVal) + 6;
            }
            yield next;
        }
    } finally {
        console.log("cleaning up!");
    }
}

for (let v of something()) {
    console.log(v);
    if (v > 5) {
        break;
    }
}
/**
1
9
33
105
321
969
cleaning up!
*/
```

## `yield*` 表达式
如果在 Generator 函数内部调用另外一个 Generator 函数，默认情况下是没有效果的，这就需要用到 `yield*` 表达式。
`yield*` 后接的是遍历器对象，返回的是遍历器内部的对象。
```js
function* inner() {
    yield 'hello'
    return 'world!'
}
function* outer() {
    yield 'open'
    let result = yield* inner()
    console.log('inner\'s result: ' + result)
    yield 'close'
    return 'return'
}

let gen = outer()
gen.next().value // "open"
gen.next().value // "hello"
gen.next().value // "close"

// 也可以用 for ... of 遍历，但是 for ... of 不会遍历 return 后的值
let gen2 = outer()
for (let g of gen2) {
    console.log(g);
}
// "open"
// "hello"
// "inner's result: world!"
// "close"
```

`yield*` 后跟数组的话，因为数组支持遍历器，所以会遍历数组成员。
```js
function* gen() {
    yield* ['a', 'b'];
}

gen.next().value // "a"
```

可以使用`yield*` 很方便地取出嵌套数组的所有成员：
```js
function* iterTree(tree) {
    if (Array.isArray(tree)) {
        for(let i=0; i < tree.length; i++) {
        yield* iterTree(tree[i]);
        }
    } else {
        yield tree;
    }
}

const tree = [ 'a', ['b', 'c'], ['d', 'e'] ];

for(let x of iterTree(tree)) {
    console.log(x);
}
```

## 对象中的 Generator 函数
在对象中使用如下方式编写 Generator 函数：
```js
// 简写方式如下
let obj = {
    * myGeneratorMethod() {
        // ···
    }
};
// 完整方式
let obj = {
    myGeneratorMethod: function* () {
        // ···
    }
};
```

## Generator 的应用
1. Generator 将异步操作转换成同步操作表达.
```js
// 将 Ajax 请求用同步的方式表达
function* main() {
    var result = yield request("http://some.url");
    var resp = JSON.parse(result);
    console.log(resp.value);
}

function request(url) {
    makeAjaxCall(url, function(response){
        it.next(response);
    });
}

var it = main();
it.next();
```

2. 控制流程管理：如果有一个耗时的同步操作，写成多个回调函数：
```js
step1(function (value1) {
    step2(value1, function(value2) {
        step3(value2, function(value3) {
        step4(value3, function(value4) {
            // Do something with value4
        });
        });
    });
});

// 将其改写成Promise对象
Promise.resolve(step1)
    .then(step2)
    .then(step3)
    .then(step4)
    .then(function (value4) {
        // Do something with value4
    }, function (error) {
        // Handle any error from step1 through step4
    })
    .done();

// 写成生成器的形式
let steps = [step1, step2, step3];

function* iterateSteps(steps){
    for (let i=0; i< steps.length; i++){
        let step = steps[i];
        yield step();
    }
}

for (let step of iterateJobs(jobs)){
    console.log(step.id);
}
```

3. 部署 Iterator 接口
利用 Generator 函数，可以在任意对象上部署 Iterator 接口。
```js
function* iterEntries(obj) {
    let keys = Object.keys(obj);
    for (let i=0; i < keys.length; i++) {
        let key = keys[i];
        yield [key, obj[key]];
  }
}

let myObj = { foo: 3, bar: 7 };

for (let [key, value] of iterEntries(myObj)) {
    console.log(key, value);
}
```

# 异步的 Generator
`yield` 命令会暂停当前函数的执行，让出控制权，等再次获取到控制权之后继续执行。因此可以将异步操作封装到一个Generator函数中，还可以通过  `next` 方法向异步操作中传递参数，通过 `throw` 方法还可以捕获程序外部抛出的异常信息。
我们可以用如下方式对异步任务进行封装：
```js
let fetch = require('node-fetch');

function* gen(){
    let url = 'https://api.github.com/users/github';
    let result = yield fetch(url);
    console.log(result.bio);
}

// 调用方式如下
let g = gen();
let result = g.next();

result.value.then(function(data){
    return data.json();
}).then(function(data){
    g.next(data);
});
```

从上面代码可以看到，用 Generator 函数封装异步操作很简洁明了，但是对流程管理却不很擅长。

```js
function foo(x, y) {
    ajax(`http://som.url/?x=${x}&y=${y}`, function (err, data) {
        if (err) {
            // 向 *main() 抛出一个错误
            it.throw(err);
        } else {
            // 用收到的 data 恢复 *main()
            it.next(data);
        }
    })
}

function *main () {
    try {
        var text = yield foo(11, 31);
        console.log(text);
    } catch (err) {
        console.log(err);
    }
}
```

## Trunk 函数
编译器的“传名调用”实现，往往是将参数放到一个临时函数之中，再将这个临时函数传入函数体。这个临时函数就叫做 Thunk 函数。它是“传名调用”的一种实现策略，用来替换某个表达式。
```js
function f(m) {
    return m * 2;
}

f(x + 5);

// 等同于
let thunk = function () {
    return x + 5;
};

function f(thunk) {
    return thunk() * 2;
}
```

但是 JavaScript 中的 Trunk 函数不是表达式，而是将多参数函数替换成一个只接受回调函数作为参数的单参数函数。
```js
// 正常版本的readFile（多参数版本）
fs.readFile(fileName, callback);

// Thunk版本的readFile（单参数版本）
var Thunk = function (fileName) {
    return function (callback) {
        return fs.readFile(fileName, callback);
    };
};

var readFileThunk = Thunk(fileName);
readFileThunk(callback);
```

只要有回调函数作为参数的函数，就能写成Thunk函数的形式，下面是一个简单的函数转换器：
```js
var Thunk = function (fn) {
    return function () {
        var args = Array.prototype.slice.call(arguments);
        return function (callback) {
            args.push(callback);
            return fn.apply(this, args);
        }
    }
}

// ES6
const Thunk = function(fn) {
    return function(...args) {
        return function(callback) {
            return fn.call(this, ...args, callback);
        }
    }
}
```

## 使用 `Thunkify` 模块
生产环境中，推荐使用 Thunkify 模块。安装 `npm install thunkify`，使用方式如下：
```js
let thunkify = require('thunkify');
let fs = require('fs');
let read = thunkify(fs.readFile);
read('file')(function (err, str){
    // do something
})
```

## Generator 流程管理
使用 Thunk 函数可以将 Generator 函数自执行。下面就是一个基于 Thunk 函数的 Generator 执行器。
```js
function run(fn) {
    var gen = fn();

    function next(err, data) {
        var result = gen.next(data);
        if (result.done) return;
        result.value(next);
    }

    next();
}

function* g() {
  // ...
}

run(g);
```

有了这个执行器，执行 Generator 函数方便多了。不管内部有多少个异步操作，直接把 Generator 函数传入 `run` 函数即可。当然，前提是每一个异步操作都是 Thunk 函数。

# `async` 函数
`async` 函数就是 Generator 生成器的语法糖。
对于读取文件的操作：
```js
// 使用 Generator
const fs = require('fs');

const readFile = function (fileName) {
    return new Promise(function (resolve, reject) {
        fs.readFile(fileName, function(error, data) {
            if (error) return reject(error);
            resolve(data);
        });
    });
};

const gen = function* () {
    const f1 = yield readFile('/etc/fstab');
    const f2 = yield readFile('/etc/shells');
    console.log(f1.toString());
    console.log(f2.toString());
};

// 使用 async 函数
const asyncReadFile = async function () {
    const f1 = await readFile('/etc/fstab');
    const f2 = await readFile('/etc/shells');
    console.log(f1.toString());
    console.log(f2.toString());
};
```

比较发现：`async` 函数就是将 Generator 函数的星号（`*`）替换成 `async`，将`yield`替换成`await`. `await` 相比于 Generator 有如下优势：
1. 内置执行器。Generator 函数的自执行必须靠执行器，而 `async` 函数不用
2. 更好的语义。`async` 和 `await` 比 `*` 和 `yield` 有更好的语义
3. 返回值是 Promise 对象，可以用 `then` 方法添加回调函数

**函数前面的 `async` 关键字，表明该函数内部有异步操作。调用该函数时，会立即返回一个 Promise 对象。**
**`await`会等待 `async` 函数中的 `Promise` 执行完毕.**
```js
function timeout(ms) {
    return new Promise((resolve) => {
        setTimeout(resolve, ms);
    });
}

async function asyncPrint(value, ms) {
    await timeout(ms);
    console.log(value);
}

asyncPrint('hello world', 50);
```

## 使用
`async` 函数返回的是 Promise 对象，函数 `return` 返回的值成为 `then` 方法的回调函数。
如果 `async` 函数中抛出了错误，那么返回的 Promise 处于 reject 状态，会被 Promise 的 `catch` 方法捕获。
```js
async function f() {
    throw new Error('出错了');
}

f().then(
    v => console.log(v),
    e => console.log(e)
)
// Error: 出错了
```

只有 `async` 函数内部的异步操作执行完，Promise 对象才会发出变化，执行then方法指定的回调函数。

## `async` 错误处理
在 `async` 函数中，如果有一个 `await` 后的 Promise 对象变为 reject 状态，那么后续的代码就不会再执行。如果不想因该语句影响后续的执行，那么就需要处理异常：
```js
async function f() {
  try {
    await Promise.reject('出错了');
  } catch(e) { }
  return await Promise.resolve('hello world');
}

f().then(v => console.log(v))

// 或者使用 Promise 对象的 catch
async function f1() {
    await Promise.reject('出错了')
            .catch (e => console.log(e));
    return await Promise.resolve('hello world');
}
```

还可以使用 `try ... catch` 实现多次尝试
```js
const superagent = require('superagent');
const NUM_RETRIES = 3;

async function test() {
    let i;
    for (i = 0; i < NUM_RETRIES; ++i) {
        try {
            await superagent.get('http://google.com/this-throws-an-error');
            break; // 没有异常，跳出循环
        } catch(err) {}
    }
    console.log(i); // 3
}

test();
```

如果多个 `await` 后的操作相互之间不存在依赖关系，那么可以使用如下方式让其同时触发
```js
async function dbFuc(db) {
    let docs = [{}, {}, {}];
    let promises = docs.map((doc) => db.post(doc));

    let results = await Promise.all(promises);
    console.log(results);
}
```

# Class 的基本语法
ES6 的 Class 类只是一个语法糖，它的绝大多数功能 ES5 都能实现，它只是让对象原型的写法更加清晰、更像面向对象编程的语法。
类和模块的内部，默认就是严格模式，所以不需要使用 `use strict` 指定运行模式。只要你的代码写在类或模块之中，就只有严格模式可用。

## 构造器方法
`constructor` 方法是类的默认方法，通过 `new` 命令生成对象实例时，自动调用该方法。一个类必须有`constructor`方法，如果没有显式定义，一个空的`constructor`方法会被默认添加。`constructor` 默认返回实例对象，而且类必须使用 `new` 调用，否则会报错。
```js
class Bar {
    constructor () {}
}

let bar = new Bar();

Bar(); // 报错
```

## 实例对象
实例的属性除非显示地定义在其自身（`this` 对象），否则都定义在原型（`prototype`）上。
```js
//定义类
class Point {
    constructor(x, y) {
        this.x = x;
        this.y = y;
    }

    toString() {
        return '(' + this.x + ', ' + this.y + ')';
    }
}

var point = new Point(2, 3);

point.toString() // (2, 3)

point.hasOwnProperty('x') // true
point.hasOwnProperty('y') // true
point.hasOwnProperty('toString') // false
point.__proto__.hasOwnProperty('toString') // true
```

## class 表达式
JavaScript 中的 class 也可以写成表达式的形式：
```js
// 类名是 MyClass，Me 只能在 class 内部可以使用，相当于 this
// 如果在 class 中不使用 Me，可以将其省略
let MyClass = class Me {
    getClassName() {
        return Me.name;
    }
}

let me = new Me(); // 会报错： ReferenceError: Me is not defined

// 立即执行类
let you = new class {
    constructor (name) {
        this.name = name;
    }
    getName() {
        return this.name;
    }
}('name');
you.getName();
```

**类不存在变量提升**
```js
new Foo(); // ReferenceError
class Foo {}
```

## getter 和 setter 方法
在类的内部可以使用 `get` 和 `set` 关键字，对某个属性设置存值函数和取值函数，拦截该属性的存取行为。
```js
class MyClass {
    constructor() {
        // ...
    }

    // this.value 中的 value 如果同 get value() 的名字一致，就会 RangeError: Maximum call stack size exceeded
    get prop() {
        return this.pro;
    }
    set prop(value) {
        this.pro = value;
    }
}

let inst = new MyClass();

inst.prop = 123;
inst.prop  // '123'
```

## 静态方法
如果在一个方法前，加上 `static` 关键字，就表示该方法不会被实例继承，而是直接通过类来调用，这就称为“静态方法”。
```js
class Foo {
    static bar () {
        this.baz();
    }
    static baz () {
        console.log('hello');
    }
    // 静态方法可以和非静态方法重名
    baz () {
        console.log('world');
    }
}

Foo.bar() // hello
```

静态方法中的 `this` 关键字，指向的是类，而不是实例。
在实例上调用静态方法，会抛出不存在该方法的错误。

父类的静态方法，也可以被子类继承。

## class 的静态属性和实例属性
静态属性是类的属性，而不是实例属性。对于静态属性只有如下方式可以实现，因为ES6明确规定，Class内部只有静态方法，没有静态属性：
```js
class Foo {}
// 静态属性 prop
Foo.prop = 1;
```

## `new.target` 属性
`new.target` 属性一般用在构造函数中，返回 `new` 命令作用的那个构造函数。如果构造函数不是通过 `new` 命令调用的，`new.target` 会返回 `undefined`.
```js
class Point {
    constructor (x, y) {
        if (new.target !== undefined) {
             this.x = x;
            this.y = y;
            console.log(new.target); // [Function: Point]
        } else {
            throw new Error('must use new to instance');
        }
    }
}
```

# 类继承
Class 可以通过 `extends` 关键字实现继承：
```js
class Rectangle extends Point {
    constructor(x, y, tangle) {
        super(x, y); // 调用父类的构造器
        this.tagnle = tangle;
    }

    toString () {
        return this.tangle + ' ' + super.toString();
    }
}
let r = new Rectangle(1,2,30);
```

**子类必须在`constructor`方法中调用`super`方法，这是因为子类没有自己的`this`对象，而是继承父类的`this`对象，而后对其加工。若不调用`super`方法，那么子类就没有`this`对象**

**子类的构造函数中，只有调用`super`之后，才可以使用`this`关键字，因为子类实例的构建是基于对父类实例加工，只有`super`方法才能返回父类实例。**

## 类的`prototype`属性和`__proto__`属性
ES5中，每一个对象都有 `__proto__`属性，指向对应的构造函数 `prototype` 属性。`class` 作为语法糖，同时有`prototype`属性和`__proto__`属性。
1. 子类的 `__proto__` 属性，表示构造函数的继承，总是指向父类
2. 子类的 `prototype` 属性的 `__proto__` 属性，表示方法的继承，总是指向父类的 `prototype` 属性。

```js
class A {}

class B extends A {}

B.__proto__ === A // true
B.prototype.__proto__ === A.prototype // true

// 继承实现模式
// B 的实例继承 A 的实例
Object.setPrototypeOf(B.prototype, A.prototype);
// B 继承 A 的静态属性
Object.setPrototypeOf(B, A);
```

## 继承目标
`extends` 后可以跟许多类型，只要该类型是有 `prototype` 属性的函数。
有三种特殊情况：
1. 子类继承 `Object` 类
    ```js
    class A extends Object {}
    A.__proto__ === Object // true
    A.prototype.__proto__ === Object.prototype // true
    ```

2. 不继承任何类
    ```js
    class A {}
    A.__proto__ === Function.prototype // true
    A.prototype.__proto__ === Object.prototype // true
    ```

3. 子类继承 `null`
    ```js
    class A extends null {}
    A.__proto__ === Function.prototype // true
    A.prototype.__proto__ === undefined // true
    ```

## Mixin 模式实现
Mixin 指多个对象合成一个新对象，新对象具有各个组成成员的接口。
```js
function mix(...mixins) {
    class Mix {}

    for (let mixin of mixins) {
        copyProperties(Mix, mixin);
        copyProperties(Mix.prototype, mixin.prototype)
    }
    return Mix;
}

function copyProperties (target, source) {
    for (let key of Reflect.ownKeys(source)) {
        if (key !== 'constructor'
            && key !== 'prototype'
            && key !== 'name'
        ) {
            let desc = Object.getOwnPropertyDescriptor(source, key);
            Obejct.defineProperty(target, key, desc);
        }
    }
}
```

# Module 语法
模块功能主要由两个命令构成：`export` 和 `import`。`export` 命令用于规定该模块的对外接口，`import` 命令用于引入其他模块的功能。

该功能nodejs可能不支持，需要使用 babel 才能正常运行：
```s
# 全局安装
npm install babel-cli -g
# 本地安装
npm install babel-cli --save

# 使用
babel-node main.js
```

## `export`
一个模块就是一个独立的文件，文件内部的所有变量，外部无法获取。使用 `export` 关键字可以将希望外部能够获取的变量输出：
```js
// example.js
export let firstName = 'Michael'
export let lastName = 'Jackson'

// 建议书写方式
let firstName = 'Michael'
let lastName = 'Jackson' 

export {firsName, lastName}
```

`export` 还可以输出函数或者类
```js
// example.js
function f1 () {}
function f2 () {}

export {
    f1 as fun1,
    f2 as fun2,
    f2 as fun2Alias
}
```

## `import`
使用 `export` 命令定义模块对外接口后，就可以用 `import` 来加载该模块。
```js
import {firstName, lastName} from './example';
console.log(firstName); // "Michael"
console.log(lastName);  // "Jackson"
```

`import` 命令接受一对大括号，其中指定从其他模块导入的变量名，**大括号中的变量名，必须与被导入的模块对外接口的名称相同**

如果要为导入的变量重定义名字，可以用如下方式
```js
import {firstName as name} from './example'
```

`import` 输入的变量都是只读的，不允许在加载模块的脚本里修改。如果输入的变量是对象，可以修改它的属性，但是不建议这么做。

因为 `import` 是静态执行的，所以不能使用表达式、变量或者 `if` 结构。

## 整体加载模块
可以使用如下方式将整个模块加载：
```js
import * as name from './example'
console.log('firstName: ' + name.firstName);
console.log('lastName: ' + name.lastName);
// 不建议修改变量
name.firstName = 'Jack';
```

## `export default` 命令
`export default` 命令可以为模块指定默认输出，一个模块只能有一个默认输出
```js
// default.js
export default function default () {
    console.log('default')
}
```

当其他模块加载 `deafult.js` 时，`import` 命令可以为该模块指定任意名称，且此时 `import` 命令后不使用大括号。
```js
import myDefault from './default'
// 同下面的引入方式等价
import {default as myDefault} from './default'
myDefault.default();
```

## `export` 和 `import` 同时使用
如果在一个模块中，先引入后输出同一个模块，可以采用如下方式：
```js
import {foo, bar} from 'module'
export {foo, bar};
// 简写如下
export {foo, bar} from 'module';
```

简写方式时，`foo` 和 `bar` 并没有被引入到当前模块，只是相当于对外转发了这两个接口，因此当前模块不能直接使用 `foo` 和 `bar`。

## 模块的继承
模块之间可以继承。假设有一个 `circleplus` 模块，继承了 `circle` 模块：
```js
export * from 'circle';
export var e = 2.71;
export default function (x) {
    return Math.exp(x);
}
```

`export *` 将 `circle` 模块中所有的方法和属性都输出。但是`export *` 会忽略 `circle` 模块的 `default` 方法。

加载上面的模块写法如下：
```js
// 导入 export default 之外的变量和方法
import * as math from 'circleplus';
// 导入 export default 的变量和方法
import exp fro 'circleplus';
console.log(exp(math.e));
```

## 跨模块常量
`const` 声明的常量只在当前代码块中有效，若要设置跨模块常量，可以使用如下方式：
```js
// constants.js
export const A = 1;
export const B = 3;
export const C = 4;

// test1.js
import * as constatns from './constants';

// test2.js 
import {A, B} from './contants'
```

在常量比较多的情况下，可以建立一各专门的常量目录，将各个常量写在不同的文件中，然后将这些常量合并在 `index.js` 文件中。
```js
// constants/db.js
export const db = {
    url: '',
    username: '',
    password: ''
}

// constants/user.js
export const users = ['root', 'admin']

// index.js
export {db} from './db'
export {users} from './user'
```