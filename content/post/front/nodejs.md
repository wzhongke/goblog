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