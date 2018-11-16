---
title: node
---

# npm 常用功能

## 查看帮助
`npm -v` 可以查看当前 npm 的版本。
`npm` 可以查看 npm 帮助说明。

## 安装依赖包
`npm install <package>` 安装依赖包。执行后，会在当前目录的 node_modules 目录下创建以包名为名的目录，并将包安装到该目录下。

`npm install <package> -g` 全局安装依赖包。全局模式并不是将一个模块包安装为一个全局包的意思，它并不意味着可以从任 何地方通过require()来引用到它。
`-g` 是将一个包安装为全局可用的执行命令。

`npm install underscore --registry=http://registry.url` 通过镜像安装依赖，即非官方的依赖。如果使用过程中几乎都采用镜像源安装，可以执行以下命令指定默认源: `npm config set registry http://registry.url`


由于事件循环模型需要应对海量请求，海量请求同时作用在单线程上，就需要防止任何一个 计算耗费过多的CPU时间片。至于是计算密集型，还是I/O密集型，只要计算不影响异步I/O的调 度，那就不构成问题。建议对CPU的耗用不要超过10 ms，或者将大量的计算分解为诸多的小量 计算，通过setImmediate()进行调度。只要合理利用Node的异步模型与V8的高性能，就可以充分 发挥CPU和I/O资源的优势。