---
title: docker
---

# 安装
windows 直接下载(安装包)[https://docs.docker.com/docker-for-windows/install/]安装

Linux 下安装 docker：
```bash
> yum -y install docker-io
# 安装完成后，启动 docker 后台服务
> service docker start
```

# Docker 容器
镜像（`image`）和容器（`container`）的关系就像是面向对象程序设计中的类和实例一样。镜像是静态和定义，容器是镜像运行时的实体。容器可以被创建、启动、停止、删除、暂停等。

容器的实质是进程，运行于属于自己的独立的命名空间。容器内的进程是运行在人个隔离的环境中，就好像是在一个独立于宿主的系统下操作一样。

# 镜像加速器
国内从 Docker Hub 拉取镜像时可能会遇到困难，此时可以配置镜像加速器，如：
- [Docker 官方提供的中国 registry mirror](https://docs.docker.com/registry/recipes/mirror/#use-case-the-china-registry-mirror)
- [阿里去加速器](https://cr.console.aliyun.com/#/accelerator)
- [DaoCloud 加速器](https://www.daocloud.io/mirror#accelerator-doc)


# 镜像
## 获取镜像
从 Docker 镜像仓库中获取镜像的命令是 `docker pull`，其使用格式为：
```
docker pull [options] [镜像地址[:端口]/]仓库名[:标签]
```

- 镜像地址一般是 `<域名/IP>[:端口号]`，默认地址是 Docker Hub
- 仓库名：形式为 `<用户名>/<软件名>`，对于Docker Hub，如果不给出用户名，则默认是 `library`（官方镜像）

```
# docker pull ubuntu:16.04
Trying to pull repository docker.io/library/ubuntu ... 
16.04: Pulling from docker.io/library/ubuntu
297061f60c36: Pull complete 
e9ccef17b516: Pull complete 
dbc33716854d: Pull complete 
8fe36b178d25: Pull complete 
686596545a94: Pull complete 
Digest: sha256:1dfb94f13f5c181756b2ed7f174825029aca902c78d0490590b1aaa203abc052
Status: Downloaded newer image for docker.io/ubuntu:16.04
```

## 运行
下载完镜像之后，就能够以该镜像为基础启动并运行一个容器。如果我们打算启动镜像中的 `bash` 进行交互操作的话，可以执行：
```
# docker run -it --rm ubuntu:16.04 bash
root@618c76554bbb:/# cat /etc/os-release 
NAME="Ubuntu"
VERSION="16.04.4 LTS (Xenial Xerus)"
ID=ubuntu
ID_LIKE=debian
PRETTY_NAME="Ubuntu 16.04.4 LTS"
VERSION_ID="16.04"
HOME_URL="http://www.ubuntu.com/"
SUPPORT_URL="http://help.ubuntu.com/"
BUG_REPORT_URL="http://bugs.launchpad.net/ubuntu/"
VERSION_CODENAME=xenial
UBUNTU_CODENAME=xenial
root@618c76554bbb:/# exit
exit
```

`docker run` 是运行容器的命令，其参数有：
- `-it`: 这是两个参数，`-i` 表示交互式操作；`-t` 表示一个终端。也就是打开一个交互式终端
- `--rm`: 这个参数表示容器退出后将其删除。
- `bash`: **放在镜像名之后**，提供一个交互式 Shell

最后使用 `exit` 退出容器。

## 列出镜像
可以通过 `docker image ls` 命令查看下载下来的镜像。
```
# docker image ls
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
docker.io/redis     latest              bfcb1f6df2db        3 weeks ago         107 MB
docker.io/nginx     latest              ae513a47849c        3 weeks ago         109 MB
docker.io/ubuntu    16.04               0b1edfbffd27        4 weeks ago         113 MB
```

列表中包含了 仓库名、标签、镜像id、创建时间及所占空间。

可以使用 `docker system df` 来查看镜像、容器、数据卷所占的空间。
```
# docker system df
TYPE                TOTAL               ACTIVE              SIZE                RECLAIMABLE
Images              6                   3                   1.039 GB            628.7 MB (60%)
Containers          4                   1                   60 B                60 B (100%)
Local Volumes       4                   1                   276 B               184 B (66%)
```

### 虚悬镜像
官方镜像发布了新版本后，重新执行 `docker pull image:image-version` 后，`image:image-version` 这个镜像名被转移到新下载的镜像上，从而导致旧镜像变为 `<none>`。
`docker build` 也可以导致虚悬镜像的产生。可以用一下命令删除这类镜像
```
# docker image prune
```

## 删除本地镜像
使用 `docker image rm` 命令删除本地镜像，格式为：
```
docker image rm [options] <image1> [<image2> ...]
```

## 使用 `commit` 制作镜像
`docker commit` 用来理解镜像构成，但是不要使用它定制镜像。定制镜像应该使用 Dockerfile 来完成。
以定制一个 web 服务器为例：
```
# docker run --name webserver -d -p 80:80 nginx
```

这条命令会用 `nginx` 镜像启动一个容器，命名为 `webserver`，并映射了 80 端口。这样我们就可以通过 http://ip 来访问。

如果要修改成欢迎 Docker 的文字，可以使用 `docker exec` 命令进入容器：
```
# docker exec -it webserver bash
root@9063e9ccd146:/# echo '<h1>hello,docker!</h1>' > /usr/share/nginx/html/index.html 
root@9063e9ccd146:/# exit
```

`docker exec -it webserver bash` 使我们以交互式终端进入 `webserver` 容器，从而可以执行 `bash` 命令。
然后修改了 `index.html` 中的内容，刷新浏览器，会发现内容已经被修改了。

修改容器文件，也就修改了容器的存储层。可以通过 `docker diff` 命令查看具体改动：
```
# docker diff webserver
C /root
A /root/.bash_history
C /run
A /run/nginx.pid
A /run/secrets
C /usr/share/nginx/html/index.html
C /var/cache/nginx
A /var/cache/nginx/client_temp
A /var/cache/nginx/fastcgi_temp
A /var/cache/nginx/proxy_temp
A /var/cache/nginx/scgi_temp
A /var/cache/nginx/uwsgi_temp
```

修改之后，可以将其保存下来形成镜像。
`docker commit` 命令，可以将容器的存储层保存成镜像，即在原来的基础上，再叠加上容器的存储层，形成新的镜像。该命令的语法如下：
```
docker commit [options] <容器ID或容器名> [<仓库名>[:<标签>]]
```

可以使用下面的命令将容器保存为镜像：
```
# docker commit \
      --author "Author Name <email@email.com>" \
      --message "修改了默认网页" \
      webserver \
      nginx:v2
```

`--author` 是指定修改的作者，`--message` 是记录本次修改的内容。当然这些信息可以为空。

可以使用 `docker history` 查看镜像内的历史记录。
```
# docker history nginx:v2
IMAGE               CREATED             CREATED BY                                      SIZE                COMMENT
805b11733cbc        5 hours ago         nginx -g daemon off;                            101 B               修改了默认网页
ae513a47849c        4 weeks ago         /bin/sh -c #(nop)  CMD ["nginx" "-g" "daem...   0 B                 
<missing>           4 weeks ago         /bin/sh -c #(nop)  STOPSIGNAL [SIGTERM]         0 B                 
<missing>           4 weeks ago         /bin/sh -c #(nop)  EXPOSE 80/tcp                0 B                 
<missing>           4 weeks ago         /bin/sh -c ln -sf /dev/stdout /var/log/ngi...   22 B                
<missing>           4 weeks ago         /bin/sh -c set -x  && apt-get update  && a...   53.7 MB             
<missing>           4 weeks ago         /bin/sh -c #(nop)  ENV NJS_VERSION=1.13.12...   0 B                 
<missing>           4 weeks ago         /bin/sh -c #(nop)  ENV NGINX_VERSION=1.13....   0 B                 
<missing>           4 weeks ago         /bin/sh -c #(nop)  LABEL maintainer=NGINX ...   0 B                 
<missing>           4 weeks ago         /bin/sh -c #(nop)  CMD ["bash"]                 0 B                 
<missing>           4 weeks ago         /bin/sh -c #(nop) ADD file:ec5be7eec56a749...   55.3 MB  
```

**慎用`docker commit`**
`docker commit` 命令可以比较直观地理解镜像分层存储的概念，但是在实际环境中并不会这样使用。

如果仔细观察之前的 `docker diff webserver` 的结果，你会发现除了真正想要修改的 `/usr/share/nginx/html/index.html` 文件外，由于命令的执行，还有很多文件被改动或添加了。这还仅仅是最简单的操作，如果是安装软件包、编译构建，那会有大量的无关内容被添加进来，如果不小心清理，将会导致镜像极为臃肿。

另外使用 `docker commit` 意味着所有对镜像的操作都是黑箱操作。生成的镜像也被称为黑箱镜像，换句话说，就是除了制作镜像的人知道执行过什么命令、怎么生成的镜像，别人根本无从得知。

# 使用 Dockerfile 定制镜像
Dockerfile 是一个文本文件，其内包含了一条条指令，每一条指令构建一层。每一条指令的内容就是描述该层应当如何构建。
以 `nginx` 镜像为例，新建一个 `Dockerfile`:
```
# makdir nginx
# cd mynginx
# touch Dockerfile
```

其中 `Dockerfile` 文件的内容是：
```dockerfile
FROM nginx
RUN echo '<h1>Hello, Dokcer!</h1>' > /usr/share/nginx/html/index.html
```

## `FROM` 指定基础镜像
定制镜像必须以一个镜像为基础，在其上进行定制。`FROM` 就是指定这个基础镜像的，因此 `Dockerfile` 中 `FROM` 是必备的指令，而且必须是第一条指令。
在 [Docker Store](https://store.docker.com/) 上有非常多的高质量的官方镜像，如 `nginx`, `redis`, `mongo`, `tomcat` 等。可以在其中寻找一个最符合最终目标的镜像为基础镜像进行定制。
如果没有找到对应服务的镜像，官方镜像中还提供了一些更为基础的操作系统，如 `ubuntu`, `debian`, `centos` 等。

## `RUN` 执行命令
`RUN` 指令用来执行命令行命令，其格式在两种：
- shell 格式： `RUN <命令>`，就像直接在命令行中输入的命令一样。
- exec 格式：`RUN ["可执行文件", "参数1", "参数2"]`

因为 `RUN` 可以像 shell 脚本一样执行命令，因此可以把每个 shell 命令对应成一个 `RUN`：
```dockerfile
FROM debian:jessie

RUN apt-get update
RUN apt-get install -y gcc libc6-dev make
RUN wget -O redis.tar.gz "http://download.redis.io/relaese/redis-3.2.5.tar.gz"
RUN mkdir -p /usr/src/redis
RUN tar -xzf redis.tar.gz -C /usr/src/redis --strip-components=1
RUN make -C /usr/src/redis
RUN make -C /usr/src/redis install
```

Dockerfile 中每一个指令都会建立一层，每个 `RUN` 都会新建立一层。以上的写法创建了7层镜像，而且很多运行时不需要的东西，都被装进了镜像中，比如编译环境、更新软件包等。结果产生了非常臃肿、非常多层的镜像。

上面的 `Dockerfile` 应该写成：
```dockerfile
FROM debian:jessie
RUN buildDeps='gcc libc6-dev make' \
    && apt-get update \
    && apt-get install -y $buildDeps \
    && wget -O redis.tar.gz "http://download.redis.io/releases/redis-3.2.5.tar.gz" \
    && mkdir -p /usr/src/redis \
    && tar -xzf redis.tar.gz -C /usr/src/redis --strip-components=1 \
    && make -C /usr/src/redis \
    && make -C /usr/src/redis install \
    && rm -rf /var/lib/apt/lists/* \
    && rm redis.tar.gz \
    && rm -r /usr/src/redis \
    && apt-get purge -y --auto-remove $buildDeps
```

这里使用了一个 `RUN` 命令，并且用 `&&` 将各个所需的命令串联起来，这样就只构建了一层。
Dockerfile 支持 shell 行尾添加 `\` 来换行的方式，以及行首使用 `#` 注释的格式。
**这组命令的最后添加了清理工作，删除了为了编译构建所需要的软件，清理了所有下载、展开的文件，`apt` 缓存文件。**
很多人初学 Docker 制作出了很臃肿的镜像的原因之一，就是忘记了每一层构建的最后一定要清理掉无关文件。

## 构建镜像
在 `Dockerfile` 文件所在的目录执行：
```
# docker build [options] <上下文路径/URL/->
# docker build -t nginx:v3 .
Sending build context to Docker daemon 2.048 kB
Step 1/2 : FROM nginx
 ---> ae513a47849c
Step 2/2 : RUN echo '<h1>Hello, Dokcer!</h1>' > /usr/share/nginx/html/index.html
 ---> Running in 5d2652f8db8c

 ---> 2be761668cf1
Removing intermediate container 5d2652f8db8c
Successfully built 2be761668cf1
```

## 镜像上下文
`docker build` 命令最后有一个 `.`，表示当前目录，制定了上下文路径。
当构建的时候，用户会指定构建镜像上下文的路径，`docker build` 命令得知这个路径后，会将路径下的所有内容打包，然后上传给 Docker 引擎。这样 Docker 引擎收到这个上下文包后，展开就会获得构建镜像所需的一切文件。

如果在 Dockerfile 中有如下命令：
```
COPY ./package.json /app/
```

该命令是复制上下文目录下的 `package.json` 文件。
`COPY` 这类指令中的源文件的路径都是相对路径，因此 `COPY ../package.json /app` 或者 `COPY /opt/xxx /app` 无法工作，因为这些路径已经超出了上下文的范围。

一般来说，应该将 Dockerfile 放在空目录下，或者项目根目录下。如果目录下有不希望构建时传给 Docker 引擎的文件，那么可以用 `.gitignore` 一样的语法写一个 `.dockerignore`，该问价能用于剔除不需要作为上下文传递给 Docker 引擎的。

# Dockerfile 命令
## COPY - 复制文件
`COPY` 指令的格式为：
```
COPY <源路径>... <目标路径>
COPY ["<源路径1>", ... "<目标路径>"]
```

`COPY` 指令将从构建上下文目录中 `<源路径>` 的文件/目录复制到新的一层镜像内的 `<目标路径>` 位置：
```dockerfile
COPY package.json /usr/src/app/
COPY hom* /mydir/
COPY hom?.txt /mydir/
```

`COPY` 指令执行时，源文件的各种元数据都会保留，比如读，写，执行权限等。

## `ADD` - 更高级的复制文件
如果 `<源路径>` 为一个 `tar` 压缩文件的话，并且压缩格式为 `gzip`, `bzip2` 以及 `xz` 的情况下，`ADD` 指令将会自动将该文件解压到 `<目标路径>` 中。
```dockerfile
FROM scratch
ADD ubuntu-xenial-core-clouding-amd64.tar.gz /
```

**因为 `ADD` 指令会令镜像构建缓存失效，从而使镜像构建变得比较缓慢。所有文件复制用 `COPY` 指令，仅在需要自动解压的场合使用 `ADD`**

## `CMD` - 容器启动命令
`CMD` 指令的格式和 `RUN` 相似，也有两种格式：
- `shell` 格式： `CMD <命令>`
- `exec` 格式：`CMD ["可执行文件", "参数1", "参数2"]`
- 参数列表格式：`CMD ["参数1", "参数2"...]`。在指定了 `ENTRYPOINT` 指令后，用 `CMD` 指定具体的参数。

Docker 不是虚拟机，容器就是进程。`CMD` 指令就是用于指定默认的容器主进程的启动命令。

在指令格式上，推荐使用 `exec` 格式，这类格式会被解析成JSON数组，因此一定要使用双引号。
`shell` 格式会被包装成 `sh -c` 的参数形式进行执行：
```dockerfile
CMD echo $HOME
```

在实际执行中，会变更为：
```
CMD ["sh", "-c", "echo $HOME"]
```

Docker 不是虚拟机，容器中的应用都应该以前台形式执行，而不应该像虚拟机、物理机那样，用 `upstart/systemd` 去启动后台服务，容器中没有后台服务的概念。
因此使用 `CMD service nginx start` 并不能启动后台服务。该命令会被理解为 `CMD [ "sh", "-c", "service nginx start"]`，因此主进程是 `sh`，那么 `service nignx start` 命令结束后，`sh` 也就结束了，`sh` 作为主进程退出了，自然就会令容器退出。
正确的方式是直接执行 `nginx` 可执行文件，并且以前台形式运行：
```dockerfile
CMD ["nginx", "-g", "daemon off;"]
```

## `ENV` 设置环境变量
`ENV` 有两种格式：
- `ENV <key> <value>`
- `ENV <key1>=<value1> <key2>=<value2>`

环境变量的设置同 shell 脚本设置变量一样，使用方式也相似
```dockerfile
ENV NODE_VERSION 7.2.0

RUN curl -SLO "https://nodejs.org/dist/v$NODE_VERSION/node-v$NODE_VERSION-linux-x64.tar.xz" 
```

## `EXPOSE` 声明端口
该指令的格式为 `EXPOSE <端口1> [<端口2> ...]`

`EXPOSE` 指令是声明运行时容器提供的服务端口，只是一个声明。在运行时并不会因为这个声明，应用就会开启这个端口。Dockerfile 中声明有两个好处：一个是帮助竞相使用者理解这个镜像服务的守护端口，以便配置映射；另一个是在运行时使用随机端口映射。

## `WORKDIR` - 指定工作目录
该指令的格式为 `WORKDIR <工作目录路径>`
使用 `WORKDIR` 指令来指定工作目录，以后各层的当前目录就是指定的目录。如果该目录不存在，会被自动创建。
使用一下命令会找不到 `/app/world.txt` :
```dockerfile
RUN cd /app
RUN echo "hello" > world.txt
```

这是因为，这两行的 `RUN` 命令的执行环境不一样，是两个完全不同的容器。
之前说过每一个 `RUN` 都是启动一个容器、执行命令、然后提交存储层文件变更。第一层 `RUN cd /app` 的执行仅仅是当前进程的工作目录变更，一个内存上的变化而已，其结果不会造成任何文件变更。而到第二层的时候，启动的是一个全新的容器，跟第一层的容器更完全没关系，自然不可能继承前一层构建过程中的内存变化。

# 容器 
容器是独立运行的一个或一组应用，以及它们的运行环境。

## 启动容器
启动容器有两种方式：一种是基于镜像新建一个容器并启动；将在终止状态的容器重新启动

### 新建并启动
下面的命令启动一个容器，并输出 "Hello World"，之后容器终止
```
# docker run ubuntu /bin/echo 'Hello World'
Hello World
```

下面的命令则启动一个 bash 终端，并允许用户进行交互。
```
# docker run -it ubuntu /bin/bash
```

当利用 `docker run` 来创建容器时，Docker 在后台运行的标准操作包括：

- 检查本地是否存在指定的镜像，不存在就从公有仓库下载
- 利用镜像创建并启动一个容器
- 分配一个文件系统，并在只读的镜像层外面挂载一层可读写层
- 从宿主主机配置的网桥接口中桥接一个虚拟接口到容器中去
- 从地址池配置一个 ip 地址给容器
- 执行用户指定的应用程序
- 执行完毕后容器被终止

### 启动已经终止的容器
使用 `docker container start` 命令启动一个已经终止的容器。

## 后台运行容器
大部分时间，都需要让 Docker 在后台运行而不是直接把执行命令的结果输出在当前宿主机下。可以通过添加 `-d` 参数来实现。
如果不是用 `-d` 参数运行容器：
```
# docker run ubuntu /bin/sh -c "while true; do echo hello world; sleep 1; done"
hello world
hello world
hello world
hello world
hello world
```

如果使用 `-d` 参数运行容器：
```
# docker run -d ubuntu /bin/sh -c "while true; do echo hello world; sleep 1; done"
16938a3656257c548549e5b580fdad48b26dce153c782f5a3047ef165321dc36
```

此时容器会在后台运行并不会把输出的结果 (STDOUT) 打印到宿主机上面(输出结果可以用 `docker logs` 查看)。

通过 `docker container logs [container ID or NAMES]` 查看文件信息：
```
# docker container logs 16938a3656257c
hello world
hello world
hello world
hello world
hello world
```

## 终止容器
使用 `docker container stop` 来终止一个运行中的容器。另外当 Docker 容器中指定的应用终结时，容器也会自动终止。
对于一个启动终端的容器，可以通过 `exit` 命令 或者 `ctrl+d` 来退出终端。
终止状态的容器，可以用 `docker container ls -a` 命令来查看

处于终止状态的容器，可以通过 `docker container start` 命令来重新启动。
`docker container restart` 命令会将一个运行中的容器终止，然后再重启。

## 进入容器
有时候需要进入运行中的容器中进行操作，可以使用 `docker attach` 命令或者 `docker exec` 命令。

`docker attach` 是 Docker 自带的命令：
```
# docker container ls
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                NAMES
16938a365625        ubuntu              "/bin/sh -c 'while..."   34 minutes ago      Up 34 minutes                            jolly_volhard

# docker attach 16938a365625 
hello world
hello world
hello world
hello world
hello world
```

`docker exec` 之后可以跟多个参数。`-it` 是最常用的参数，执行后可以看到非常熟悉的 Linux 命令提示符

## 导入和导出容器
可以使用命令 `docker export [container id] > name.tar` 来将容器快照导出到本地。

使用 `docker import` 从容器快找文件中导入镜像。
```
# cat name.tar | docker import - test/ubuntu:v1
```

## 删除容器
使用 `docker container rm ` 来删除一个处于终止状态的容器。
使用 `docker container prune` 来清理掉所有处于终止状态的容器。

# Docker 数据管理
在容器中管理数据主要有两种方式：
- 数据卷
- 挂载主机目录

## 数据卷
数据卷是一个可供一个或多个容器使用的特殊目录，它绕过UFS，可以提供很多有用的特性：
- 数据卷可以在容器之间共享和重用
- 对数据卷的修改会立马生效
- 对数据卷的更新不会影响镜像
- 数据卷默认会一直存在，即使容器被删除

> 注意：数据卷 的使用，类似于 Linux 下对目录或文件进行 mount，镜像中的被指定为挂载点的目录中的文件会隐藏掉，能显示看的是挂载的 数据卷。

使用 `docker volume create my-vol` 来创建一个数据卷。
查看指定数据卷的信息：
```
# docker volume ls
DRIVER              VOLUME NAME
local               my-vol

# docker volume inspect my-vol
[
    {
        "Driver": "local",
        "Labels": {},
        "Mountpoint": "/var/lib/docker/volumes/my-vol/_data",
        "Name": "my-vol",
        "Options": {},
        "Scope": "local"
    }
]
```

可以通过 `docker volume rm my-vol` 来删除数据卷。
数据卷是被设计用来持久化数据的，它的生命周期独立于容器，Docker 不会再容器被删除后自动删除数据卷，而且也不存在垃圾回收这样的机制来处理没有任何容器引用的数据卷。如果需要在删除容器的同时移除数据卷，可以用 `docker rm -v` 这个命令

也可以使用 `docker volume prune` 来清理无主的数据卷。

## 监听主机目录
使用 `--mount` 标记可以指定挂载一个主机本地的目录到容器中去。
```
# docker run -d -P \
     -name web \
     -v /src/webapp:/optwebapp\
     --mount type=bind,source=/src/webapp,target=/opt/webapp \
     traing/webapp \
     python app.py
```

上面的命令加载主机的 `/src/webapp` 目录到容器的 `/opt/webapp` 目录。本地目录的路径必须是绝对路径，如果本地目录不存在，Docker 会报错。

Docker 挂载主机目录的默认权限是读写，可以增加 `readonly` 来指定为只读：
```
# docker run -d -P \
    --name web \
    # -v /src/webapp:/opt/webapp:ro \
    --mount type=bind,source=/src/webapp,target=/opt/webapp,readonly \
    training/webapp \
    python app.py
```

可以通过 `docker inspect web` 来查看容器信息：
```
# docker inspect web
"Mounts": [
    {
        "Type": "bind",
        "Source": "/src/webapp",
        "Destination": "/opt/webapp",
        "Mode": "",
        "RW": true,
        "Propagation": "rprivate"
    }
],
```

还可以从主机挂载单个文件到容器：
```
$ docker run --rm -it \
   # -v $HOME/.bash_history:/root/.bash_history \
   --mount type=bind,source=$HOME/.bash_history,target=/root/.bash_history \
   ubuntu:17.10 \
   bash

root@2affd44b4667:/# history
1  ls
2  diskutil list
```

# docker 命令
依据 docker 命令的用途，可以将其分为以下几类。

子命令分类    | 子命令
:----------------|:-----------------
docker环境信息    | info, version
容器生命周期管理   | create, exec, kill, pause, restart, rm, run, start, stop, unpause
镜像仓库命令       | login, logout, pull, push, search
镜像管理          | build, images, import, load, rmi, save, tag, commit
容器运维操作       | attach, export, inspect, port, ps, rename, stats, top, wait, cp, diff, update
容器资源管理       | volume, network
系统日志信息       | events, history, logs 

可以用如下命令测试是否安装成功：
```
> docker --version
```

从 Docker Hub 中拉取 hello-word 镜像：
```
> docker run hello-world
```

列出镜像：
```
> docker image ls
```

列出所有容器：
```
> docker container ls --all
```

列出运行中的容器
```
> docker container ls
```

使用容器名称停止运行中的容器：
```
> docker container stop webserver
```

使用容器名称移除容器
```
docker container rm webserver
```

docker 帮助
```
> docker --help
> docker container --help
> docker container ls --help
> docker run --help
```