---
title: linux编译安装httpd
date: 2017-12-28 19:21:00
tags: ["linux"]
categories: ["linux"]
---

有时候需要在内网的机器上安装httpd，因为机器不能上网，yum源也不可用，只能手动编译。

## 准备工作——下载httpd的安装包
1. [Apach Http server](http://httpd.apache.org/download.cgi#apache24)
2. [apr以及apr-util](http://apr.apache.org/download.cgi)
3. [pcre](https://sourceforge.net/projects/pcre/files/pcre/)

## 编译安装
虚机上要先安装gcc/gcc-c++。
将这些都安装到 `/opt` 目录下：
1. 新建安装目录
    ```shell
    mkdir -p /opt/apr
    mkdir -p /opt/apr-util
    mkdir -p /opt/pcre
    mkdir -p /opt/httpd
    ```

2. 解压
    ```shell
    tar -zxf httpd-*.tar.gz -C httpd
    tar -zxf apr-*.tar.gz -C apr
    tar -zxf apr-util-*.tar.gz -C apr-util
    tar -zxf pcre-*.tar.gz -C pcre
    ```


3. 安装
    ```shell
    cd apr
    ./configure --prefix=/opt/apr
    make
    make install

    cd ../apr-util
    ./configure --prefix=/opt/apr/apr-util --with-apr=/opt/apr/apr/bin/apr-1-config
    make 
    make install

    cd ../pcre
    ./configure --prefix=/opt/pcre --with-apr=/opt/apr/bin/apr-1-config
    make 
    make install

    cd ../httpd
    ./configure --prefix=/opt/httpd --with-pcre=/opt/pcre --with-apr=/opt/apr --with-apr-util=/opt/apr-util
    make
    make install
    ```


## 问题
安装`apr-util`过程中碰到了`xml/apr_xml.c:35:19: error: expat.h: No such file or directory`这样的问题，这是因为缺少了库文件。
安装 `expat` 库解决