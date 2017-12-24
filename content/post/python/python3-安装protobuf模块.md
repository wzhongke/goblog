---
title: python3 安装protobuf模块
date: 2017-06-17 19:02:10
categories: ["python"]
tags: ["python"]
---

python3 网上没有现成的protobuf模块可以直接使用，我在安装时费了一番周折，故此记录下来。

记录下自己安装python3-protobuf的过程

1. 从 [protobuf官网](https://github.com/openx/python3-protobuf) 下载源码
2. 解压后打开 vsprojects/protobuf.sln (一般用visual studio打开）
3. 右键生成，在Debug目录下找到protoc.exe （该文件是用来生成proto buffer的代码模块）
4. 使用命令行定位到Python目录下，然后输入python setup.py install  protobuf 的python模块，至此python3下的protobuf就可以使用了