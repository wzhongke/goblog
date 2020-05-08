---
title: Redis
date: 2017-06-18 19:42:25
tags: ["sql"]
categories: ["sql"]
---

# 为什么使用 Redis

# Redis 单线程
Redis 单线程模型能有如此的处理能力，可以归结为三点：
1. 纯内存访问，是如此高的处理能力的重要基础
2. 非阻塞I/O，使用 epoll 来作为 I/O 多路复用技术的实现，再加上将其转换成事件，不在网络 I/O 上浪费时间
3. 避免线程切换和竞争产生的消耗

问题： 如果某个命令执行过长，会造成其他命令阻塞，对 Redis 这种高性能服务来说很致命，所以 Redis 是面向快速执行场景的数据库

# 1. Redis 安装
在 Linux 下使用如下命令下载安装 redis：
```shell
# 下载 Redis 到当前目录
wget http://download.redis.io/releases/redis-5.0.8.tar.gz
# 解压 Redis 到当前目录
tar zxf redis-5.0.8.tar.gz 
cd redis-5.0.8
make
make test
make install
```

中间可能出现错误：`You need tcl 8.5 or newer in order to run the Redis test`
安装 tcl 8.5 以上版本：
```shell
wget http://downloads.sourceforge.net/tcl/tcl8.6.1-src.tar.gz
sudo tar xzvf tcl8.6.1-src.tar.gz 
cd  tcl8.6.1/unix/  
sudo ./configure  
sudo make  
sudo make install 
```

# Redis 命令
Redis 安装完成之后，在 `/usr/local/bin` 中多了几个 Redis 命令：
1. `redis-server`: 启动 Redis
2. `redis-cli`   : Redis 命令行客户端
3. `redis-benhmark`: Redis 基准测试工具
4. `redis-check-aof`: Redis AOF 持久化工具
5. `redis-check-dump`: Redis RDB 持久化文件检查和修复工具
6. `redis-sentinel`: 启动 Redis 哨兵

## 启动 Redis
启动 Redis 时指定配置文件：
```shell
redis-server redis.conf
```

配置文件一般会指定 端口号、日志文件目录、Redis工作目录、守护方式启动 Redis等。

在 Redis 安装目录中有 Redis 提供的 `redis.conf` 文件

使用客户端命令 `CONFIG GET *` 可以获取到所有 Redis 配置


## Redis 客户端命令
通过命令 `redis-cli -h ${host} -p ${port}` 的方式连接到 Redis 服务。


## 停止 Redis
使用命令 `redis-cli shutdown save|nowave` 来停止 Redis
`save|nosave` 代表在关闭 Redis 前，是否生成持久化文件

# 2. API
Redis 有五种数据结构：string、hash、list、set、zset(有序集合)

## Redis 全局命令
1. 查看所有的键： `keys *`；该命令会遍历所有的键，线上环境禁止使用
2. 查看键的总数： `dbsize`；该命令获取 Redis 内置键总数变量，时间复杂度是 O(1)
3. 检查键是否存在: `exists key`
4. 删除键: `del key [key...]`
5. 设置键过期时间: `expire key seconds`
6. 查看键的数据结构类型: `type key`
7. 查看键值的编码: `object encoding key`

## 数据结构和内部编码
数据结构   | 内部编码 
:---------|:----------
string    | raw/int/embstr
hash      | hashtable/ziplist
list      | linkedlist/ziplist
set       | hashtable/intset
zset      | skiplist/ziplist

## 字符串命令

### 设置值命令
`set key value [ex seconds] [px milliseconds] [nx|xx]`
- `ex seconds`: 为键设置秒级过期时间
- `px milliseconds`: 为键设置毫秒级过期时间
- `nx`: 键必须不存在，才可以设置成功，用于添加，等同于 `setnx`
- `xx`: 键必须存在，才可以设置成功，用于更新，等同于 `setxx`

**`setnx` 可作为分布式锁的一种实现方案** [http://redis.io/topics/distlock]

### 获取值命令
`get key` 获取值，如果不存在返回空

### 批量操作命令
1. 批量设置值： `mset key value [key value ...]`
2. 批量获取值：`mget key [key...]`

### 计数
`incr key` 用于对值做自增操作，返回结果分为三种情况：
- 值不是整数，返回错误
- 值是整数，返回自增后的结果
- 键不存在，按照值为0自增，返回结果1

### 字符串内部编码
字符串内部编码有三种类型：
- `int`: 8个字节的长整型
- `embstr`: 小于等于39个字节的字符串
- `raw`: 大于39个字节的字符串

### 典型使用场景
1. 缓存功能：Redis 作为缓存，MySQL 作为存储层
2. 计数功能
3. 共享 Session，分布是 Web 将 Session 保存在各自的服务器，可以保存到 Redis 进行集中管理
4. 限速，限制某段时间内访问次数

## 哈希
1. 设置值: `hset key field value`
2. 获取值: `hget key field`
3. 删除: `hdel key field [field...]`
4. 计算 field 个数: `hlen key`
5. 批量获取field-value: `hmget key field [field...]` 
6. 批量设置field-value: `hmset key field value [field value...]` 
7. 判断 field 是否存在: `hexists key field`
8. 获取所有 value: `hvals key`
9. 获取所有 field-value: `hgetall key` (若hash元素个数较多，会阻塞 Redis，建议使用 `hmget` 或 `hscan`)
10. 自增: `hincrby key field`, `hincrbyfloat key field`
11. 计算 value 长度: `hstrlen key field`

### 内部编码
hash类型有两种内部编码：
- ziplist(压缩列表): 当hash类型元素个数小于 `hash-max-ziplist-entries` 配置时、同时所有值小于 `hash-max-ziplist-value` 配置时，使用 ziplist 编码。
- hashtable(哈希表): 哈希类型不能满足 ziplist 条件时， Redis 会使用 hashtable

hash 类型是稀疏的，会消耗更多内存

ziplist 使用更加紧凑的结构实现多个元素的连续存储，比 hashtable 节省内存

## list 列表
列表类型用来存储多个有序的字符串，支持对列表两端 push 和 pop，还可以获取指定范围元素。可以充当栈和队列的角色。

列表支持如下命令：
1. 从右边插入元素: `rpush key value [value...]`
2. 从左边插入元素: `lpush key value [value...]`
3. 从左到右获取所有元素: `lrange key 0 -1`
4. 向某个元素前或后插入元素: `linsert key before|after item value`。 `item` 是列表中的某个元素，如果元素不存在，返回 -1
5. 获取指定范围内的元素列表: `lrange key strart end`
6. 获取指定索引下标元素: `lindex key index`
7. 获取列表长度: `llen key`
8. 从列表左侧弹出元素: `lpop key`
9. 从列表右侧弹出元素: `rpop key`
10. 从列表删除指定元素: `lrem key count value`
    1.  `count > 0`: 从左到右，删除最多 count 个元素
    2.  `count < 0`: 从右到左，删除最多 count 个元素
    3.  `count = 0`: 删除所有
11. 按照索引范围修剪列表: `ltrim key start end`
12. 修改指定索引下标元素: `lset key index newValue`
13. 阻塞式弹出: `blpop|brpop key [key...] timeout`。列表为空，客户端阻塞 timeout 时间后返回，若此期间添加了数据，客户端立即返回；列表不为空，立即返回

### 内部编码
列表类型有两种内部编码：
- `ziplist`(压缩列表): 当列表的元素个数小于`list-max-ziplist-entries` 配置 (默认512个)，同时列表中每个元素的值都小于 `list-max-ziplist-value` 配置时 (默认64字节)，Redis会选用ziplist来作为列表的内部实现来减少内存的使用。
- `linkedlist`(链表): 当列表类型无法满足ziplist的条件时，Redis会使用 linkedlist 作为列表的内部实现。

### 使用场景
**消息队列**: 使用 `lpush + brpop` 命令可以实现阻塞队列，生产者使用 `lpush` 从列表左侧插入元素，多个消费者使用 `brpop` 从阻塞式抢队尾元素。

作为数据结构使用：
- `lpush + lpop`: 栈
- `lpush + rpop`: 队列
- `lpush + ltrim`: 有限集合
- `lpush + brpop`: 消息队列

## 集合
集合中不允许有重复元素，且其中元素是无序的。

集合支持如下命令：
1. 添加元素: `sadd key element [element...]`
2. 删除元素: `srem key element [element...]`
3. 计算元素个数: `scard key`
4. 判断元素是否在集合中: `sismember key element`
5. 随机返回指定个数元素: `srandmember key [count]`
6. 从集合随机弹出元素: `spop key`
7. 获取所有元素: `smembers key`

`smembers、lrange、hgetall` 都是比较重的命令，元素过多存在阻塞 Redis 的可能性

集合间操作命令：
1. `sinter key [key...]`: 求多个集合的交集
2. `sunion key [key...]`: 求多个集合的并集
3. `sdiff key [key...]`: 求多个集合的差集
4. `sinterstore destination key [key ...]` `suionstore destination key [key ...]` `sdiffstore destination key [key ...]`: 将交集、并集、差集的结果保存

### 内部编码
集合类型也有两种编码方式：
- intset(整数集合): 当集合中的元素都是整数且元素个数小于set-max- intset-entries配置(默认512个)时
- hashtable(哈希表): 当集合类型无法满足 intset 的条件时

### 常见场景
Redis 集合类型常见的场景有：
- `sadd`: 标签
- `spop/srandmember`: 生成随机数，比如抽奖
- `sadd + sinter`: 社交等


## 有序集合
有序集合依据每个元素设置的分数作为排序的依据。

有序集合命令如下：
- `zadd key score member [score member ...]`: 添加成员
- `zcard key`: 计算成员个数
- `zscore key member`: 计算成员分数
- `zrank key member`: 计算成员排名，从低到高返回排名
- `zrevrank key member`: 按排名从高到低返回成员
- `zrem key member [member ...]`: 删除成员
- `zincrby key increment member`: 为某个成员增加分数
- `zrange key start end [withscores]`: 按照分数从低到高返回指定范围的成员
- `zrangebyscore key min max [withscores] [limit offset count]`: 按照分数从低到高返回指定分数范围的成员
- `zremrangebyrank key start end`: 删除指定排名内的升序元素
- `zremreangebyscore key min max`: 删除指定分数范围的成员

### 内部编码
有序集合有两种编码方式：
- ziplist(压缩列表):当有序集合的元素个数小于 `zset-max-ziplist- entries` 配置(默认128个)，同时每个元素的值都小于`zset-max-ziplist-value` 配置(默认64字节)时
- skiplist(跳跃表): 当ziplist条件不满足时，有序集合会使用 `skiplist` 作为内部实现，因为此时 `ziplist` 的读写效率会下降。

### 使用场景
排行榜系统：用户点赞、取消等

## Bitmaps
Bitmaps 本身不是数据结构，是字符串，只是它可以对字符串的进行操作。

其命令如下：
1. `setbit key offset value`: 设置键的第 offset 个位的值
2. `getbit key offset`: 获取键的第offset位的值
3. `bitcount [start][end]`: 获取Bitmaps指定范围值为1的个数
4. `bitop operaction destkey key[key....]`: 可以做多个Bitmaps的and(交集)、or(并 集)、not(非)、xor(异或)操作并将结果保存在destkey中

## 键管理命令
重命名命令：
1. `rename key newkey`: 键重命名，如果 `newkey` 存在就会被覆盖
2. `renamenx key newkey`: 只有 `newkey` 不存在时，才会重命名成功

键过期命令
1. `expire key seconds`: 键在 seconds 后过期
2. `expireat key timestamp`: 键在秒级时间戳 `timestamp` 后过期
3. `pexpire key milliseconds`: 键在 milliseconds 毫秒后过期
4. `pexpireat key milliseconds-timestamp`: 键在毫秒级时间戳`timestamp` 后过期。
5. `persist key`: 清除过期时间

迁移键命令：
- `move key db`: 在 Redis 内部进行数据迁移，将指定键从源数据库迁移到目标数据库
- `dump key` + `restore key ttl value`: 在不同 Redis 实例间进行数据迁移功能。
  - 在源 Redis 上使用 `dump` 将键值序列化，格式使用 RDB
  - 在目标 Redis 上使用 `restore` 将上面序列化值进行复原
- `migrete host port key|'' destination-db timeout [copy] [replace] [keys key [key...]]`: 直接将键迁移到目标 Redis，是 `dump + restore`  的内部实现

遍历键命令：
- `keys pattern`: 遍历指定的键值。pattern 是使用的 glob 风格的通配符。如果 key 很多，会阻塞 Redis
- `scan cursor [match pattern] [count number]`: 渐进式遍历
  - `cursor`: 必须参数，遍历开始的游标，由上一次命令返回
  - `match pattern`: 可选参数，模式匹配
  - `count number`: 可选参数，表明每次遍历键的个数，默认为 10

scan 采用渐进式遍历方式来解决 keys 命令可能带来的阻塞问题，每次scan命令的时间复杂度是 O(1)，但是要真正实现keys的功能，需要执行多次scan。

### 数据库管理
1. `select dbIndex`: 切换数据库，数据库用数字区分，Redis 默认有16个数据库
2. `flushdb/flushall`: 清除数据库，`flushdb` 只清除当前数据库，`flushall` 会清除所有数据库。

# 3.小功能

## 慢查询分析
慢查询日志即系统在命令执行前后计算每条命令的执行时间，超过阈值时，将该命令记录下来。

Redis 使用一个列表来存储慢查询日志

慢查询有两个配置项：
- `slowlog-log-slower-than`: 预设阈值，单位为毫秒，默认值为 10000；值为 0，记录所有查询日志；小于0，所有日志都不记录
- `slowlog-max-len`: 列表的最大长度。建议配置在 1000 以上

获取慢查询日志：
- `slowlog get [n]`，获取 n 条慢查询日志
- `slowlog len`: 获取慢查询日志当前长度
- `slowlog reset`: 清空列表

## Redis Shell

### redis-cli 参数
- `-c`: 连接 Redis cluster 结点时使用，防止 moved 和ask 异常
- `-a`: Redis 密码
- `--slave`: 将当前客户端模拟成 Redis 的从节点，可以用来获取当前 Redis 节点的更新操作
- `--pipe`: 将命令封装 Redis 通信协议定义的数据格式，批量发送给 Redis 执行

### redis-benchmark 参数
`redis-benchmark` 可以为 Redis 做基准性能测试：
- `-c`：客户端的并发数量，默认是 50
- `-n`: 客户端请求总量，默认是十万
- `-r`: 向 Redis 插入更多的随机键

## pipline
pipline (流水线)将一组 Redis 命令进行组装，通过 RTT 传输给 Redis，再将这组 Redis 命令的执行结果按顺序返回给客户端。

Pipline 同 原生批量命令区别：
- 原生批量命令是原子的，Pipline 是非原子的
- 原生批量命令是一个命令对应多个 key，Pipline 支持多个命令
- 原生批量命令是 Redis 服务端支持实现，而 Pipline 需要服务端和客户端共同实现

## 发布订阅
Redis 发布订阅命令如下：
1. `publish channel message`: 向 channel 频道发布一条消息，返回结果为订阅者个数
2. `subscribe channel [channel ...]`: 订阅者可以订阅一个或多个频道
3. `unsubscribe [channel [channel ...]]`: 取消订阅
4. `psubscribe pattern [pattern...]`: 按照模式订阅
5. `punsubscribe [pattern [pattern ...]]`: 按照模式取消订阅
6. `pubsub channels [pattern]`: 查看活跃的频道
7. `pubsub numsub [channel ...]`: 查看频道订阅数

客户端在执行订阅命令后进入订阅状态，只能接收 subscribe、psubscribe、unsubscribe、punsubscribe 命令

### 使用场景
聊天室、公告牌、服务之间利用消息解耦都可以使用发布订阅模式

## 地理信息定位(GEO)
其指令如下：
1. `geoadd key longitude latitude member [longitude latitude member ...]`: 增加地理位置信息
2. `geopos key member [member ...]`: 获取地址位置信息
3. `geodist key member1 member2 [unit]`: 获取两个地理位置的距离
4. `zrem key member`: 删除地址位置信息

# 客户端

## 连接注意事项
Redis 为每个客户端分配了输入缓冲区，将客户端发送的命令临时保存，同时 Redis 会从输入缓冲区拉取命令并执行。输入缓冲区使用不当，会产生两个问题：
1. 一旦某个客户端的输入缓冲区超过1G，客户端将会被关闭
2. 输入缓冲区不受maxmemory控制，假设一个Redis实例设置了 maxmemory为4G，已经存储了2G数据，但是如果此时输入缓冲区使用了 3G，已经超过maxmemory限制，可能会产生数据丢失、键值淘汰、OOM等 情况

造成输入 缓冲区过大的可能原因有：
1. 输入缓冲区过大主要是因为Redis的处理速度跟不上输入缓冲区的输入速度，并且每次进入输入缓冲区的命令包含了大量 bigkey
2. Redis发生了阻塞，短期内不能处理命令，造成客户端输入的命令积压在了输入缓冲区

发现和监控：
1. 通过定期执行 `client list` 命令，收集 `qbuf` 和`qbuf-free` 找到异常的连接记录并分析，最终找到可能出问题的客户端
2. 通过 `info clients` 模块，找到最大的输入缓冲区

## 客户端命令

`client list` 能够列出与 Redis 服务端相连的所有客户端连接信息：
```
id=3607 addr=10.129.231.142:55044 fd=7 name= age=461 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=26 qbuf-free=32742 obl=0 oll=0 omem=0 events=r cmd=client
```

返回内容含义：
- `id`: 客户端连接唯一标识，id是随着Redis的连接自增的，重启 Redis后会重置为0。
- `fd`: socket 文件描述符。fd=-1 代表当前客户端不是外部客户端，而是Redis内部的伪装客户端
- `name`: 客户端名字
- `qubf`: 缓冲区的总容量
- `qbuf-free`: 缓冲区的剩余容量
- `age`: 客户端已经连接的时间
- `idle`: 最近一次的空闲时间
- `flag`: 标识当前客户端类型，S 标识 slave 客户端，N 标识普通客户端，O 代码客户端执行 monitor 命令

`client setName xx`: 设置客户端名字
`client getName`: 获取客户端名字
`client kill ip:port`: 杀掉指定 IP 地址和端口的客户端
`client pasue timeout`: 阻塞客户端 timeout 毫秒数；该命令只对普通和发布订阅客户端有效，对主从复制无效。
`monitor`: 能够监听客户端所有的命令，一旦 Redis 并发量过大，monitor 客户端的输出缓存会暴涨，可能瞬间占用大量内存

# 5. 持久化
Redis 支持 RDB 和 AOF 两种持久化机制，持久化功能有效地避免因进程退出造成的数据丢失问题，当下次重启时利用之前持久化的文件即可实现数据恢复

## RDB
RDB持久化是把当前进程数据生成快照保存到硬盘的过程，触发RDB持
久化过程分为手动触发和自动触发：
- `save`: 阻塞当前 Redis 服务器，直到 RDB 过程完成，对内存比较大的实例会造成长时间阻塞
- `bgsave`: 通过 fork 操作创建子进程，子进程执行 RDB 持久化操作。

bgsave 命令不会阻塞主进程，目前是通用的方案。

### RDB 文件处理
RDB文件保存在dir配置指定的目录下，文件名通过dbfilename配 置指定。

Redis默认采用LZF算法对生成的RDB文件做压缩处理，压缩后的 文件远远小于内存大小，默认开启

### RDB 优缺点
优点：
1. RDB 是紧凑压缩的二进制文件，代表Redis在某个时间点上的数据快照。非常适用于备份，全量复制等场景。
2. Redis加载RDB恢复数据远远快于AOF的方式

缺点：
1. RDB方式数据没办法做到实时持久化/秒级持久化
2. 老版本Redis服务无法兼容新版RDB格式的问题。

## AOF 持久化
以独立日志的方式记录每次写命令，重启时再重新执行AOF文件中的命令达到恢复数据的目的。AOF的主要作用是解决了数据持久化的实时性

开启AOF功能需要设置配置: `appendonly yes`，默认不开启。AOF文件名 通过 `appendfilename` 配置设置，默认文件名是`appendonly.aof`。保存路径同 RDB持久化方式一致，通过dir配置指定

**AOF文件重写**是把Redis进程内的数据转化为写命令同步到新AOF文件的过程。重写支持两种触发方式：
- 手动触发：直接调用 `bgwriteaof` 命令
- 自动触发：设置 `auto-aof-rewrite-min-size` 和 `auto-aof-rewrite-percentage` 参数来确定自动触发时机

AOF和RDB文件都可以用于服务器重启时的数据恢复，启动时加载说明：
1. AOF 持久化开启且存在AOF文件时，优先加载AOF文件
2. AOF关闭或者AOF文件不存在时，加载RDB文件
3. 加载AOF/RDB文件成功后，Redis启动成功
4. AOF/RDB文件存在错误时，Redis启动失败并打印错误信息

# 6.复制
在分布式系统中为了解决单点问题，通常会把数据复制多个副本部署到其他机器，满足故障恢复和负载均衡等需求。

## 复制配置
默认情况下，Redis都是主节点。每个从节点只能有一个主节点，而主节点可以同时具有多个从节点。复制的数据流是单向的，只能由主节点复制到从节点。配置复制的方式有以下三种:
- 配置文件中加入 `slaveof {masterHost} {masterPort}`，随 Redis 启动生效
- `redis-server --slaveof {masterHost} {masterPort}` 启动
- 直接使用 redis 命令： `slaveof {masterHost} {masterPort}`

可以使用 `info replication` 来查看复制相关状态

使用 `slaveof no one` 来断开与主节点的复制关系。断开连接后，从节点并不会抛弃原有数据，只是无法再获取主节点上的数据变化。

## 传输延迟
主从结点一般部署在不同机器，复制时网络会有延时。
`repl-disable-tcp-nodelay` 用于控制是否关闭 TCP_NODELAY，默认关闭。
- 关闭时，主节点产生数据无论大小都会及时地发送给从节点，主从结点延时变小，但是增加了网络带宽消耗
- 开启时，主节点会合并较小的TCP数据包从而节省带宽，默认发送间隔取决于 Linux 内核。适用于网络环境不好的情况

## 拓扑
Redis的复制拓扑结构可以支持单层或多层复制关系，根据拓扑复杂性
可以分为以下三种: 
- 一主一从，用来备份数据，提供故障转移
- 一主多从，在读占比大时，将读命令发送到从节点来分担主节点压力
- 树状主从结构，引入复制中间层，有效降低主节点负载和需要传送给从节点的数据量

## 数据同步
通过过程分为：
1. 全量同步：初次复制时，主节点将数据全量发送给从节点
2. 部分复制：处理主从复制中网络闪断等造成的数据丢失

## 可能的问题
通过复制机制，数据集可以存在多个副本，这些副本可以应用于读写分离、故障转移、实时备份等场景。但也存在一些需要避免的问题

### 读写分离
对于读占比较高的场景，可以把读流量分摊到从节点来减轻主节点的压力。同时需要注意对主节点只进行写操作

业务端可能遇到问题：
1. 数据延迟：取决于网络带宽和命令阻塞情况。无法容忍大量延迟场景，可以编写监控复制偏移量的监听程序
2. 读到过期数据，Redis 3.2 版本解决该问题
3. 从节点故障，客户端维护可用从节点列表

### 主从配置不一致
主从配置不一致是一个容易忽视的问题。对于有些配置主从之间是可以不一致，比如:主节点关闭AOF在从节点开启。但对于内存相关的配置必须要一致，比如 `maxmemory`，`hash-max-ziplist-entries` 等参数。当配置的 `maxmemory` 从节点小于主节点，如果复制的数据量超过从节点`maxmemory` 时，它会根据 `maxmemory-policy` 策略进行内存溢出控制，此时从节点数据已经丢失，但主从复制流程依然正常进行，复制偏移量也正常

### 规避全量复制
全量复制是一个非常消耗资源的操作，如何避免全量复制:
- 第一次建立复制，全量复制无法避免，建议低峰避免，或者尽量规避使用大数据量 Redis 节点
- 节点运行 ID 不匹配：主节点故障重启，运行ID还会改变，从节点会认为自己复制的是一个新的主节点从而进行全量复制。建议手动提升从节点为主节点或采用支持自动故障转移的哨兵或集群方案
- 复制积压缓冲区不足：如果从节点请求的偏移量不在主节点的积压缓冲区内，则无法提供给从节点数据，因此部分复制会退化为全量复制。这时需要增大积压缓冲区，保证`repl_backlog_size>net_break_time*write_size_per_minute`，从而避免因复制积 压缓冲区不足造成的全量复制

### 规避复制风暴
复制风暴是指大量从节点对同一主节点或者对同一台机器的多个主节点 短时间内发起全量复制的过程

1. 单节点复制风暴：主节点挂载多个从节点，主节点重启时，向多个从节点发送 RDB 快照，可能使主节点网络带宽消耗严重，造成主节点延迟变大。可以通过减少主节点挂在从节点数量或者加入中间层从节点。
2. 单机器复制风暴：单台机器上部署多个 master 主节点；将主节点尽量分散在多台机器，提供故障转移机制避免密集全量复制

# 7.阻塞
Redis 是单线程架构，在高并发场景时，如果出现阻塞，都会导致恶果。导致阻塞问题的场景大致分为
- 内在原因：不合理使用 API 或数据结构(大对象)、CPU饱和、持久化阻塞
- 外在原因：CPU竞争、内存交换、网络问题

# 8.内存
限制内存的目的主要有：
1. 当超出内存上限 `maxmemory` 时使用LRU等删除策略释放空间
2. 防止所用内存超过服务器物理内存

Redis默认无限使用服务器内存，为防止极端情况下导致系统内存耗尽，建议所有的Redis进程都要配置maxmemory

## 内存回收策略
Redis的内存回收机制主要体现在以下两个方面: 
- 删除到达过期时间的键对象（惰性删除和定时删除两种策略）
- 内存使用达到maxmemory上限时触发内存溢出控制策略

# 9. 哨兵 (Sentinel)
Redis Sentinel 是Redis的高可用实现方案。
Redis的主从复制模式，可以起到两个作用：
1. 作为主节点的灾备，保证数据尽量不丢失
2. 从节点可以扩展主节点的读能力

主从复制也有如下问题：
1. 一旦主节点出现故障，需要手动将一个从节点晋升为主节点，同时需 要修改应用方的主节点地址，还需要命令其他从节点去复制新的主节点，整个过程都需要人工干预
2. 主节点的写能力受到单机的限制
3. 主节点的存储能力受到单机的限制

主节点故障，转移过程如下：
1. 如果主节点无法正常启动，需要选出一个从节点 (slave-1)，对其执行 `slaveof no one` 命令使其成为新的主节点
2. slave-1成为新的主节点后，更新应用方的主节点信息，重新启动应用方。
3. 客户端命令另一个从节点(slave-2)去复制新的主节点(new-master)
4. 待原来的主节点恢复后，让它去复制新的主节点

转移过程中需要注意如下问题：
1. 判断节点不可达机制是否健全
2. 存在多个从节点时，怎么保证只有一个晋升为主节点
3. 通知客户端新的主节点机制是否健全

Redis Sentinel 就是为了解决如上问题。

Redis Sentinel是一个分布式架构，其中包含若干个Sentinel节点和Redis 数据节点，每个Sentinel节点会对数据节点和其余Sentinel节点进行监控，当它发现节点不可达时，会对节点做下线标识。如果被标识的是主节点，它还会和其他Sentinel节点进行“协商”，当大多数Sentinel节点都认为主节点不可达时，它们会选举出一个Sentinel节点来完成自动故障转移的工作，同时会将这个变化实时通知给Redis应用方。

Sentinel节点本身就是独立的Redis节点，只不过它们有一些特殊，它们不存储数据，只支持部分命令

Redis Sentinel 故障转移处理逻辑:
1. 每个Sentinel节点通过定期监控发现主节点出现了故障
2. 多个Sentinel节点对主节点的故障达成一致，选举出一个哨兵节点作为领导者负责故障转移。
3. Sentinel领导者节点执行了故障转移，过程跟人工干预一致

## 哨兵配置说明
`sentinel monitor <master-name> <ip> <port> <quorum>`:
- `master-name`: 主节点名
- `ip` `port`: 主节点 ip 和 端口
- `quorum`: 主节点最终不可达所需要的票数
  
`sentinel down-after-milliseconds <master-name> <times>`:
- times: 如果超过了 times 节点没有有效回复，则判定节点不可达；times 单位为毫秒

`sentinel parallel-syncs <master-name> <nums>`:
- `num`: parallel-syncs就是用来限制在一次故障转移之后，每次向新的主节点发起复制操作的从节点个数。

哨兵也可以监控多个主节点，只需要指定多个 master-name 来区分不同的主节点。

## 部署技巧
1. sentinel 不应该部署在一台物理机器上
2. 至少部署三个且奇数个 sentinel 节点，便于判断是否故障，以及选出领导者

## API
sentinel 是一个特殊的 Redis 节点，有专属的 API：
1. `sentinel masters`: 展示所有被监控的主节点状态以及相关的统计信息
2. `sentinel master <master-name>`: 展示指定主节点状态以及相关统计信息
3. `sentinel slaves <master-name>`: 展示指定主节点的从节点状态以及相关的统计信息
4. `sentinel sentinels <master-name>`: 展示指定主节点的Sentinel节点集合(不包含当前Sentinel节点)
5. `sentinel reset <pattern>`: 对符合 pattern 的主节点的配置进行重置，包含清除主节点相关状态，重新发现从节点和 Sentinel节点
6. `sentinel failover <master-name>`: 对指定主节点进行强制故障转移
7. `sentinel ckquorum <master-name>`: 检测当前可达的Sentinel节点总数是否达到 <quorum> 的个数
8. `sentinel flushconfig`: 将 Sentinel 节点的配置强制刷到磁盘上
9. `sentinel remove<master name>`: 取消当前 Sentinel节点对于指定主节点的监控

## 原理
一套合理的监控机制是 Sentinel 节点判定节点不可达的重要保证，Redis Sentinel通过三个定时监控任务完成对各个节点发现和监控:
1. 每隔 10 秒，每个Sentinel节点会向主节点和从节点发送info命令获取最新的拓扑结构
   1. 通过向主节点执行info命令，获取从节点的信息，这也是为什么 Sentinel节点不需要显式配置监控从节点。
   2. 当有新的从节点加入时都可以立刻感知出来。 
   3. 节点不可达或者故障转移后，可以通过info命令实时更新节点拓扑信息。
2. 每隔 2 秒，每个Sentinel节点会向Redis数据节点的__sentinel__:hello 频道上发送该Sentinel节点对于主节点的判断以及当前Sentinel节点的信息，同时每个Sentinel节点也会订阅该频道，来了解其他 Sentinel节点以及它们对主节点的判断，所以这个定时任务可以完成以下两个工作：
   1. 发现新的Sentinel节点:通过订阅主节点的__sentinel__:hello了解其他 的Sentinel节点信息，如果是新加入的Sentinel节点，将该Sentinel节点信息保 存起来，并与该Sentinel节点创建连接
   2. Sentinel节点之间交换主节点的状态，作为后面客观下线以及领导者选举的依据
3. 每隔1秒，每个Sentinel节点会向主节点、从节点、其余Sentinel节点 发送一条ping命令做一次心跳检测，来确认这些节点当前是否可达

**主观下线**： 每个Sentinel节点会每隔1秒对主节点、从节点、其他Sentinel节点发送ping命令做心跳检测，当这些节点超过 down-after-milliseconds没有进行有效回复，Sentinel节点就会对该节点做失败判定

**客观下线**：当Sentinel主观下线的节点是主节点时，该Sentinel节点会通过sentinel is-master-down-by-addr命令向其他Sentinel节点询问对主节点的判断，当超过 `<quorum>`个数，Sentinel节点认为主节点确实有问题

领导者选举出的Sentinel节点负责故障转移，在从节点列表中选出一个节点作为新的主节点：
1. 过滤:“不健康”(主观下线、断线)、5秒内没有回复过Sentinel节点ping响应、与主节点失联超过down-after-milliseconds*10秒
2. 选择slave-priority(从节点优先级)最高的从节点列表，如果存在则返回，不存在则继续。
3. 选择复制偏移量最大的从节点(复制的最完整)，如果存在则返回，不存在则继续
4. 选择runid最小的从节点

# 集群

## 集群功能限制
Redis 集群相对单机在功能上存在一些限制：
1. key批量操作支持有限。如 `mset`、`mget`，目前只支持具有相同slot值的key执行批量操作
2. key事务操作支持有限。同理只支持多key在同一节点上的事务操作
3. key作为数据分区的最小粒度，因此不能将一个大的键值对象如 hash、list等映射到不同的节点
4. 不支持多数据库空间。单机下的Redis可以支持16个数据库，集群模式下只能使用一个数据库空间，即db0。
5. 复制结构只支持一层，从节点只能复制主节点，不支持嵌套树状复制结构。

# 缓存设计
缓存能够有效地加速应用的读写速度，同时也可以降低后端负载，对日 常应用的开发至关重要。但是将缓存加入应用架构后也会带来一些问题

## 缓存的收益和成本
收益：
- 加速读写
- 降低后端负载

成本：
- 数据不一致性
- 代码维护成本
- 运维成本

缓存主要使用场景如下：
- 开销大的复杂计算
- 加速请求响应

## 缓存更新策略
缓存中的数据会和数据 源中的真实数据有一段时间窗口的不一致，需要利用某些策略进行更新。

1. LRU/LFU/FIFO 算法更新: 该算法通常用于缓存使用量超过了预设的最大值时候。要清理哪些数据是由具体算法决定，所以**数据的一致性是最差的，开发维护成本低**。
2. 超时剔除: 设置过期时间，过期后自动删除。**一段时间窗口内(取决于过期时间长短)存在一致性问题，开发维护成本较低，只需要设置超时时间**
3. 主动更新: 对数据一致性要求高，需要真实数据更新后，立即更新缓存数据。**一致性高，维护成本高**

## 缓存粒度控制
缓存到什么维度:
- 缓存全部列，更加通用，但实际看，应用只需要几个重要属性；而且浪费内存
- 缓存部分重要列，数据增加新字段，需要修改业务代码刷新缓存

## 穿透优化
缓存穿透是指查询一个根本不存在的数据，缓存层和存储层都不会命中，通常出于容错的考虑，如果从存储层查不到数据则不写入缓存层。
**缓存穿透将导致不存在的数据每次请求都要到存储层去查询，失去了缓存保护后端存储的意义**

造成缓存穿透的基本原因有两个：
1. 自身业务代码或者数据出现问题
2. 一些恶意攻击、爬虫等造成大量空命中

解决缓存穿透问题：
1. 缓存空对象，存储层不命中，仍然将空对象保留到缓存层；这样会造成需要缓存更多的键，需要更多内存空间，可以设置较短的过期时间
2. 布隆过滤器拦截，这种方法适用于数据命中不高、数据相对固定、实时性低

## 无底洞优化
无底洞问题分析：
- 客户端一次批量操作涉及多次网络操作，**批量操作会随着节点增多，耗时会不断增大**
- 网络连接数变多，对节点性能有一定影响

更多的节点不代表更高的性能，所谓“无底洞”就是说投入越多不一定产出越多。但是分布式又是不可以避免的，因为访问量和数据量越来越大，一个节点根本抗不住，所以如何高效地在分布式缓存中批量操作是一个难点。

常见的IO优化思路:
- 减少网络通信次数
- 降低接人成本，如采用长连接、连接池、NIO 等
  
 批量操作解决方案

 方案 | 优点        | 缺点        | 网络 IO
 :----|:---------------|:-----------|:--------
 串行命令 | 编程简单；少量keys，性能满足需求 | 大量 keys 请求延迟严重 | O(keys)
 串行IO | 编程简单；少量节点，性能满足需求 | 大量节点延迟严重 | O(nodes)
 并行 IO | 利用并行特性，延迟取决于最慢节点 | 编程复杂；问题定位较难 | O(max_slow(nodes))
 hash_tag| 性能高  | 容易出现数据倾斜，维护成本高 | O(1)

 ## 雪崩优化
 **缓存雪崩**: 由于缓存层承载着大量请求，有效地保护了存储层，但是如果缓存层由于某些原因不能提供服务，于是所有的请求都会达到存储层，存储层的调用量会暴增，造成存储层也会级联宕机的情况

 常见解决方案：
 1. 保证缓存服务高可用性
 2. 依赖组件隔离为后端限流降级 (Hystrix)
 3. 提前演练，模拟情况，提供预设方案

