---
title: hexo Hello World
date: 2017-06-17 19:48:32
tags: ["nodejs"]
categories: ["nodejs"]
---
Welcome to [Hexo](https://hexo.io/)! This is your very first post. Check [documentation](https://hexo.io/docs/) for more info. If you get any problems when using Hexo, you can find the answer in [troubleshooting](https://hexo.io/docs/troubleshooting.html) or you can ask me on [GitHub](https://github.com/hexojs/hexo/issues).

## Quick Start

### Create a new post

``` bash
$ hexo new "My New Post"
```

More info: [Writing](https://hexo.io/docs/writing.html)

### Run server

``` bash
$ hexo server
```

More info: [Server](https://hexo.io/docs/server.html)

### Generate static files

``` bash
$ hexo generate
```

More info: [Generating](https://hexo.io/docs/generating.html)

### Deploy to remote sites

``` bash
$ hexo deploy
```

More info: [Deployment](https://hexo.io/docs/deployment.html)

出现过的问题：
1. 执行`hexo d`时，出现 `ERROR Deployer not found: git`，解决方法：
    - 是否执行过`npm install hexo-deployer-git --save`
    - 执行`hexo init`之后，是否切换过根目录。如果切换过，需要新建一个目录重新`hexo init`