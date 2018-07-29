---
title: 包管理工具

---


# npm

使用 npm 安装一个本地的包：
```
npm install <package-name>
```

该命令会在当前的目录下创建一个 `node_modules` 的目录（如果不存在的话），然后将下载的包保存到这个目录下。

## package.json
使用 `package.json` 是管理本地npm安装包最好的方式。
该文件必须有如下内容：
- `name` : 全部小写，没有空格的名字，可以用 `-` 或者 `_` 。
- `version` : 格式为 `x.x.x`

```
# 在当前目录下，初始化一个 package.json 文件
npm init 

# 在当前目录下，生成一个默认的 package.json 文件
npm init --yes
npm init -y
```

执行 `npm init -y` 会生成一个默认的 package.json 文件
```json
{
    "name": "async",
    "version": "1.0.0",
    "description": "",
    "main": "index.js",
    "scripts": {
        test": "echo \"Error: no test specified\" && exit 1"
    },
    "keywords": [],
    "author": "",
    "license": "ISC",
    "dependencies": {
        "express": "^4.16.3"
    }
}
```

- name: the current directory name
- version: always 1.0.0
- description: info from the readme, or an empty string ""
- main: always index.js
- scripts: by default creates an empty test script
- keywords: empty
- author: empty
- license: ISC
- bugs: info from the current directory, if present
- homepage: info from the current directory, if present

我们可以在 `package.json` 中指定依赖的包：
- "dependencies": These packages are required by your application in production.
- "devDependencies": These packages are only needed for development and testing.

除了手动编辑 `package.json` 外，我们还可以通过 `npm` 命令行来添加依赖
```
# 加入到 dependencies 中
npm install <package-name> --save 

# 加入到 devDependencies 中
npm install <package-name> --save-dev
```

# webpack
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

### `exec()` 方法
`child_process.exec()` 使用子进程执行命令，缓存子进程的输出，并将子进程的输出以回调函数参数的形式返回：
```js
child_process.exec(command, [options,] callback)
```

参数如下：
- `command`: 字符串，将要运行的命令
- `options`: 对象，可以是：
    - `cwd`: 字符串，子进程当前的工作目录
    - `env`: 对象环境变量键值对
    - `encoding`: 字符编码 (默认 utf8)
    - `shell`: 字符串，将要执行命令的 shell（默认 linux下的 /bin/sh，window下的 cmd.exe）
    - `timeout`: 超时时间
    - `maxBuffer`: 在 `stdout` 或 `stderr` 中允许存在的最大缓冲