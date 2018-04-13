---
title: nodejs 学习笔记
date: 2018-03-08 12:01:00
tags: ["javascript"]
categories: ["javascript"]
---

# 创建一个应用
使用Node.js时，不仅仅在实现一个应用，同时还实现了整个HTTP服务。

Node.js有如下三部分组成：
1. 引入required模块
2. 创建服务器
3. 接收请求和响应请求

```js
// 引入required模块
var http = require("http");
// 创建服务器
http.createServer(function (request, response) {
    // 发送HTTP头部
    response.writeHead(200, {'Content-Type': 'text/plain'});

    // 发送响应体
    response.end('Hello World!\n');
}).listen(8080);

// 终端打印如下信息
console.log('Server running at http://localhost:8080/');
```

# NPM
NPM 是NodeJs的包管理工具，可以下载第三方包，或者命令程序，也可以将自己编写的包或命令程序上传到NPM服务器。

使用 NPM 安装模块的命令如下：
```
# 本地安装
npm install <Module Name>
# 全局安装
npm install <Module Name> -g
# 查看全局模块安装信息
npm list -g
```

本地安装会将安装包放到运行安装命令目录的 `node_modules` 下。

`package.json` 位于模块目录下，用于定义包的属性，语法为json，其属性说明如下：
- name : 包名
- version : 包的版本号
- description : 包的描述
- homepage : 包的官网url
- author : 包的作者名
- contributors : 包的其他贡献者
- **dependencies** : 包的依赖列表，npm会自动将依赖包安装到node_module目录下
- repository : 包代码存放的位置
- **main** : 指定了程序的主入口文件， `require('moduleName)` 会加载这个文件，默认值是模块根目录下面的index.js
- keywords : 关键字

## 回调函数
NodeJs是异步编程，其直接体现就是回调函数。所谓回调函数是在任务执行完成后就会被调用的函数，NodeJs所有的API都支持回调函数。

```js
let fs = require("fs");

fs.readFile("file.txt", function(err, data) {
    if (err) return console.error(err);
    console.log(data.toString());
});

console.log("程序执行结束");
```

## 事件驱动
NodeJs 使用事件驱动模型，当web server 接到请求，就将其关闭然后处理，随后去服务下一个 web 请求。
当这个请求处理完成后，它会被放回处理队列，当达到队列开头，结果会被返回给用户。

![Alt text](http://www.runoob.com/wp-content/uploads/2015/09/event_loop.jpg)

NodeJs 中有许多内置的事件，可以通过引入 `events` 模块来实例化 `EventEmitter` 来绑定和监听事件：
```js
let events = require('events');
// 初始化 EventEmitter 对象
let eventEmitter = new events.EventEmitter();
// 绑定事件处理程序
eventEmitter.on('eventName', eventHandler);
// 触发事件
eventEmitter.emit('eventName');
```

`events` 模块只提供了一个对象 `EventEmitter`，该对象有如下方法：

1. `addListener(eventName, listener)` : 为指定事件添加一个监听器到监听数组的尾部
2. `on(eventName, listener)` : 为指定事件注册一个监听器
3. `once(eventName, listener)` : 为指定事件注册一个单次监听器，该监听器只会触发一次
4. `removeListener(eventName, listener)` : 移除指定事件的某个监听器
5. `removeAllListener([eventName])` : 移除所有的监听器
6. `listeners(event)` : 返回指定事件的监听器数组

## Buffer 缓冲区
JavaScript只有字符串类型数据，不支持二进制数据，`Buffer` 是NodeJs中处理二进制数据的类。使用方式如下：
```js
// 创建一个长度为10，且初始值为0的Buffer
const buf1 = Buffer.alloc(10);

// 创建一个长度为10，用0x1填充的 Buffer
const buf2 = Buffer.alloc(10, 1);

// 创建一个长度为10，未初始化的 Buffer（Buffer可能会有旧数据）
const buf3 = Buffer.allocUnsafe(10);

// 创建一个包含 [0x1, 0x2, 0x3] 的 Buffer
const buf4 = Buffer.from([1,2,3]);

// 创建一个utf8字节的Buffer
const buf5 = Buffer.from('test');

// 写入缓冲区, writeSize 是实际写入的大小
let writeSize = buf.write(string [, offset [, length]][, encoding]);

// 从缓冲区读取数据
buf.toString([encoding[, start[, end]]]);
console.log(buf5.toString('hex'));

// 将Buffer转换成JSON对象
let json = buf.toJSON();

// 缓存区合并
Buffer.concat(list [, totalLength]);

let buf6 = Buffer.concat([buf1, buf2]);

// 拷贝缓冲区，将buf中的内容拷贝到targetBuffer中
buf.copy(targetBuffer [, targetStart [, sourceStart [, sourceEnd]]]);
buf1.copy(buf2, 2);

// 剪切缓冲区，返回一个新的缓冲区，**它和旧缓冲区指向同一内存**
buf.slice([start [,end]]);
buf1.slice(1,2);

// 返回缓冲区的长度
console.log(buf1.length);
```

## stream
所有的stream都是 EventEmitter 的实例，其常见的事件有：
- `data` : 当流中有数据可读时触发
- `end` : 没有更多数据可读时触发
- `error` : 在接受和写入的过程中发生错误时触发
- `finish` : 所有数据已被写入到底层系统时触发

链式流是连接输出流到另外一个流并创建多个流操作链的机制，一般用于管道操作。
```js
let fs = require('fs');
let data = '';

let readerStream = fs.createReadStream('input.txt');
readerStream.setEncoding('UTF8');

readerStream.on('data', function (chunk) {
    data += chunk;
});

let writerStream = fs.createWriteStream('output.txt', {'flags': 'a'});

readerStream.on('end', function(){
    console.log(data);
    writerStream.write(data, 'UTF8');
    writerStream.end();

    writerStream.on('finish', function() {
        console.log('write done.');
    });

    writerStream.on('error', function(err) {
        console.log(err.stack);
    });

    console.log('write end.');
});

readerStream.on('error', function(err){
    console.log(err.stack);
});

console.log('read end');

// 流 会自动关闭么
// 使用管道拷贝文件
readerStream.pipe(writerStream);

// 链式流
let zlib = require('zlib');
// 文件压缩
fs.createReadStream('input.txt')
    .pipe(zlib.createGzip())
    .pipe(fs.createWriteStream('input.txt.gz'));

console.log('file compress done.')

// 解压缩
fs.createReadStream('input.txt.gz')
  .pipe(zlib.createGunzip())
  .pipe(fs.createWriteStream('input2.txt'));

console.log("文件解压完成。");
```

## 模块系统
模块是NodeJs的基本组成部分，和文件是一一对应的，也就是说一个Node.js文件就是一个模块。这个文件可以是JavaScript、JSON或者编译过的C/C++扩展。
可以通过如下方式创建一个简单的hello.js模块：
```js
function Hello () {
    var name;
    this.setName = function(thyName) {
        name = thyName;
    }
    this.sayHello = function () {
        console.log('Hello ' + name);
    }
}
```

然后我们就可以直接获取这个对象了
```js
let Hello = require('./hello');
hello = new Hello();
hello.setName('your name');
hello.sayHello();
```

`require` 方法接受以下参数：
- `http`、`fs`、`path` 等原生模块
- `./mod` 或 `../mod` 等相对路径的文件模块
- `/path/to/mod` 绝对路径
- `mod` 非原生模块的文件模块。

## NodeJs 路由
可以通过 `url.parse(request.url).pathname` 获取请求的路径，通过 `url.parse(request.url).query` 获取请求参数。

可以通过 `querystring.parse(query)['param']` 来获取请求参数的值。

## Express框架简单应用
Express是一个简单灵活的nodejs WEB应用框架，提供了一些创建各种Web应用，和丰富HTTP的强大特性。
Express框架：
- 可以设置中间件来响应HTTP请求
- 定义了路由表用于执行不同的HTTP请求动作
- 通过向模块传递参数来动态渲染HTML页面

1. 安装： `npm install express --save`
2. 简单应用：
```js
let express = require('express');
let app = express();

app.get('/', function(req, res) {
    res.send('Hello World');
});
let server = app.listen(8081, function() {
    let host = server.address().address;
    let port = server.address().port;

    console.log("应用实例，访问地址为 http://%s:%s", host, port);
});
```

3. 路由
```js
app.get('/', function (req, res) {
    console.log("主页 GET 请求");
    res.send('Hello GET');
});
//  POST 请求
app.post('/', function (req, res) {
    console.log("主页 POST 请求");
    res.send('Hello POST');
});

//  /del_user 页面响应
app.get('/del_user', function (req, res) {
    console.log("/del_user 响应 DELETE 请求");
    res.send('删除页面');
});

// 对页面 abcd, abxcd, ab123cd, 等响应 GET 请求
app.get('/ab*cd', function(req, res) {   
   console.log("/ab*cd GET 请求");
   res.send('正则匹配');
});
```

4. 静态文件
```js
 // 静态文件，路径为去除static的路径
 app.use(express.static('static'));
```

5. 文件上传
```js
// 文件上传
let multer = require('multer');
let fs = require('fs');
app.use(bodyParser.urlencoded({entended: false}));
app.use(multer({dest: '/tem/'}).array('image'));

app.post('/file_upload', function(req, res) {
    console.log(req.files[0]);

    let des_file = __dirname + "/" + req.files[0].originalname;
    fs.readFile(req.files[0].path, function(err, data) {
        fs.writeFile(des_file, data, function(err) {
            if (err) {
                console.log(err);
            } else {
                response = {
                    message: 'File Upload successfully!',
                    filename: req.files[0].originalname
                };
            }
            console.log(response);
            res.type('application/json;charset=utf8');
            res.end(JSON.stringify(response));
        })
    })
})
```

## nodejs 多进程
NodeJs 是以单线程的模式运行的，但它使用的是事件驱动来处理并发，我们可以在多核CPU系统上创建多个子进程，提高性能。
Node 提供了 child_process 模块来创建子进程，方法如下：
- `exec` : 使用子进程执行命令，缓存子进程的输出，并将子进程的输出以回调函数参数的形式返回
- `spawn` : 使用指定的命令行参数创建新进程
- `fork` : `spawn` 的特殊形式，用于在子进程中运行的模块

