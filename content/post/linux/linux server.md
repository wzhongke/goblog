---
title: linux 网络常用命令
date: 2017-09-21 12:00:00
tags: ["other"]
categories: ["other"]
---
# linux 常用网络命令
## `ifconfig`, `ifup`, `ifdown`
这三个指令都是用来启动网络接口的。
1. `ifconfig`
`ifconfig`主要是可以手动启动、观察与修改网络接口的相关参数。语法如下: 
```
ifconfig [interface] {up/down}         ## 观察与启动接口
ifconfig interface {options}           ## 设定与修改接口
# 参数说明
- interface:  网卡接口代号，如: eth0, th1, ppp0等
- options:  可以使用的参数: 
    - up/down:  启动/关闭该网络接口
    - mtu :  设定不同的MTU数值（不建议修改）
    - netmask:  设置自屏蔽网络
    - broadcast:  广播地址
```
在linux机器上执行`ifconfig`，会返回如下结果
```
eth0      Link encap: Ethernet  HWaddr AA: AA: AA: 31: 79: 95  
          inet addr: 10.143.59.167  Bcast: 10.143.63.255  Mask: 255.255.248.0
          UP BROADCAST RUNNING MULTICAST  MTU: 1500  Metric: 1
          RX packets: 69269420 errors: 0 dropped: 0 overruns: 0 frame: 0
          TX packets: 43411105 errors: 0 dropped: 0 overruns: 0 carrier: 0
          collisions: 0 txqueuelen: 1000 
          RX bytes: 159293308457 (148.3 GiB)  TX bytes: 106356430482 (99.0 GiB)
```
- eth0:  网卡代号
- HWaddr:  网卡硬件地址
- RX:  网络由启动到目前为止的封包接收情况，packets代表封包数、errors代表封包发生错误的数量、dropped代表封包由于有问题而遭到丢弃的数量
- TX:  与RX相反，为网络由启动到目前为止的传送情况
- collisions:  代表封包碰撞情况
- RX bytes, TX bytes:  总接收、发送字节总量
修改网络接口
```shell
ifconfig eth0 10.134.96.237    # 系统会依照IP所在的class范围，自动计算netmask、broadcast等IP参数
ifconfig eth0 10.134.96.237 netmask 255.255.255.128 mtu 8000  # 修改其他数值
```
如果手动设置错误或者有问题，我们可以通过`/etc/init.d/network restart` 来重启整个网络接口
2. `ifup`, `ifdown`
还可以用配置文件来修改网络参数，执行这两个命令会执行`/etc/sysconfig/network-scripts`中相应的脚本

## `route`
route可以查看机器使用的路由信息
```bash
route [-nee]
route add [-net/host] [网域或主机] netmask [mask] [gw/dev]
route del  [-net/host] [网域或主机] netmask [mask] [gw/dev]
## 参数说明
-n:  直接展示通讯协议或主机名，直接用IP或port number
-ee:  使用更详细的信息显示
## 增加与删除路由
-nest:  后边接的是路由为一个域名
-host:  后面接的为连接到单部主机的路由
netmask:  与域名有关，可以设定netmask决定域名的大小
gw:  gateway的简写，后接的是IP
dev:  如果指定哪一块网卡联机出去，设定该值
```
使用`route`指令字段的含义如下: 
- Destination, Genmask:  分别是network和netmask
- Gateway:  表明该机器是通过哪个gateway连接出去的，如果是 0.0.0.0 表示该路由直接是本机传送
- Iface:  表明是从哪个网卡出去的
```bash
# 路由的增加与删除
# 删除192.168.0.0/16网段，需要将路由表上的信息都写入才能删除
route del -net 192.168.0.0 netmask 255.255.0.0 dev eth0
# 增加预设路由，只有一个就够了
route add default gw 192.168.1.250
```
当出现`Network is unreachable`时，可能是gw后边的IP直接连接。

## `ping`
ping通过ICMP封包来进行网络状态报告。其使用方式如下: 
```bash
ping [选项与参数] IP
    -c 数值:  后接ping的次数
    -n:  输出数据时不进行IP与主机的反查，直接用IP输出，速度较快
    -s 数值:  发送出去的ICMP封包大小，预设为56bytes，可以调大
    -t 数值:  TTL的数值，预设是255，每经过一个节点就会减1
    -W 数值:  等待响应时间
# 例子
ping -c 3 192.168.22.10
PING 192.168.22.10 (192.168.22.10) 56(84) bytes of data.
64 bytes from 192.168.22.10: icmp_seq=1 ttl=59 time=4.40 ms
64 bytes from 192.168.22.10: icmp_seq=2 ttl=59 time=1.40 ms
64 bytes from 192.168.22.10: icmp_seq=3 ttl=59 time=1.12 ms

--- 192.168.22.10 ping statistics ---
3 packets transmitted, 3 received, 0% packet loss, time 2004ms
rtt min/avg/max/mdev = 1.121/2.309/4.405/1.486 ms
```
上例中响应消息中，几个重要的项目是：
- 64bytes: 表示传送的ICMP封包大小为64bytes，在某些特殊场合中，可以用-s 2000来取代
- imcp_seq=1: ICMP所侦测进行的次数，第一次编号为1
- ttl=59

## `traceroute` 两主机间各节点分析
`traceroute`用来追踪两部主机之间通过的各个节点通讯状况的好坏。其使用方式如下: 
```bash
traceroute [选项与参数] IP
    -n:  必进行主机的名称解析，单纯用IP，速度较快
    -U:  使用UDP的port 33433 来进行侦测
    -I:  会用 ICMP 的方式 进行侦测
    -w:  若对方主机在几秒钟没有回声，就放弃
    -T:  使用TCP来侦测，因为UDP/ICMP的攻击层出不穷，因此很多路由器可能取消两个封包的响应功能。

## 例子
traceroute -n -w1 10.152.105.195
traceroute to 10.152.105.195 (10.152.105.195), 30 hops max, 60 byte packets
 1  10.144.103.252  0.662 ms  0.646 ms  0.741 ms
 2  * * *
 3  * * *
 4  * * *
 5  * * *
 6  10.152.105.195  1.344 ms  1.172 ms  1.042 ms
```
其中 `* * *` 是因为该节点可能有某些防护措施，让我们发送的封包信息被丢弃

## `netstat` 查看本机的网络联机与后门
`netstat`可以查看网络接口监听情况

```bash
# 与路由有关的参数
netstat -[rn]
# 与网络接口有关的参数
netstat -[antulpc]
与路由有关的参数：
-r : 列出路由表(route table)，功能如同 route 这个指令；
-n : 不使用主机名与服务名称，使用 IP 与 port number ，如同 route -n
与网络接口有关的参数: 
-a : 列出所有的联机状态，包括 tcp/udp/unix socket 等；
-t : 仅列出 TCP 封包的联机；
-u : 仅列出 UDP 封包的联机；
-l : 仅列出有在 Listen (监听) 的服务之网络状态；
-p : 列出 PID 与 Program
-c : 可以设定几秒钟后自动更新一次，例如 -c 5 每五秒更新一次网络状态的显示
# 例子
netstat -an
Active Internet connections (servers and established)
Proto Recv-Q Send-Q Local Address               Foreign Address             State      
tcp        0      0 0.0.0.0:9090                0.0.0.0:*                   LISTEN      
tcp        0      0 127.0.0.1:6600              0.0.0.0:*                   LISTEN      
tcp        0      0 0.0.0.0:80                  0.0.0.0:*                   LISTEN      
# 显示目前已经启动的网络服务
netstat -tulnp
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address               Foreign Address             State       PID/Program name   
tcp        0      0 0.0.0.0:9090                0.0.0.0:*                   LISTEN      18251/java          
tcp        0      0 127.0.0.1:6600              0.0.0.0:*                   LISTEN      18201/java          
tcp        0      0 0.0.0.0:80                  0.0.0.0:*                   LISTEN      26249/nginx         
tcp        0      0 0.0.0.0:8080                0.0.0.0:*                   LISTEN      18251/java 
# 观察本机上头所有的网络联机状态
netstat -atunp
```

netstat的输出主要分为两大部分：TPC/IP的网络接口；传统的Unix socket。输出参数的含义如下：
- Proto: 联机封包协议，TCP/UDP等
- Recv-Q: 非由用户程序连接所复制而来的总bytes数，
- Send-Q: 由远程主机所传送而来，但不具有ACK标志的总bytes数，意指主动联机SYNdrome或其他标志的封包所占的bytes数
- Local Address: 本地端的地址和端口号
- Foreign Address: 远程主机IP与端口
- stat: 连接状态

## `host` 侦测主机名

```bash
host [-a] hostname [server]
-a: 列出该主机详细的各项主机名设定数据
[server]: 可以使用非为 /etc/resolv.con 的DNS服务IP来查询
# 例子
host www.baidu.com
```
