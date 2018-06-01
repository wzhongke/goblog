---
title: zookeeper 集群搭建
---

## 安装 zookeeper
下载zookeeper，建议选择稳定版本：
```sh
wget 
```

将下载文件解压到 `/usr/local/zookeeper`

## 配置 zookeeper 集群

将 `conf/zoo_sample.cfg` 复制到 `conf/zoo.cfg`，并修改该文件：

```cfg
tickTime=2000
dataDir=/search/odin/zookeeper
clientPort=2181
initLimit=5
syncLimit=2
server.1=10.134.96.237:2888:3888
server.2=10.152.105.195:2888:3888
server.3=10.134.85.164:2888:3888
```

上述参数说明如下：
参数名  | 说明
:-----------|:----------------
clientPort  | 监听客户端连接的端口，通常为 2181
dataDir     | zookeeper 存储快照文件的目录。默认情况下，事务日志也会存储在这里。建议同时配置参数dataLogDir, 事务日志的写性能直接影响 zookeeper 性能。
tickTime    | zookeeper 的基本时间单元，单位是毫秒。
dataLogDir  | 将事务日志写到 dataLogDir 下，这允许定义专用的日志设备，避免同快照竞争资源
globalOutstandingLimit | 最大请求堆积数，客户端提交请求的速度可以比Zookeeper处理请求的速度快。为了防止因排队请求过多而导致内存不足，Zookeeper将会限制等待请求的数量，默认是1000
preAllocSize | 预先开辟磁盘空间，用于后续写入事务日志。默认是64M，每个事务日志大小就是64M。如果ZK的快照频率较大的话，建议适当减小这个参数。
snapCount   | ZooKeeper 使用快照和事务日志记录事务。snapCount 决定了一个事务日志可以记录的事务数目。
maxClientCnxns | 单个客户端与服务器之间的连接限制数目，是根据ip来的，默认是60，如果设置为0，则表明不作限制。主要是为了防止 DoS 攻击。
minSessionTimeout | 最小超时时间限制，默认是 2*tickTime
maxSessionTimeout | 最大超时时间限制，默认是 20*tickTime
initLimit   | 
leaderServes | 默认情况下， leader 接受客户端连接，并提供读写服务。但是如果想让 leader 专注于集群协调，可以将该参数设置为 no
server.x=[ip/host]:nnnn[:nnnn]   | x 是一个数字，同 dataDir/myid 文件中的id是一致的。可以配置两个端口，第一个端口用于配置 fellow 和 leader 之间的数据同步和其他通信，第二个端口用于 leader 选举


## 启动
```sh
cd $zookeeper/bin
sh zkServer.sh start
```

启动之后，可以使用如下命令查看 zookeeper 状态：
```sh
sh zkServer.sh status
```

如果启动失败，可以使用如下命令查看 zookeeper 启动过程和启动失败原因：
```sh
sh zkServer.sh start-foreground
```