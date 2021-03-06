---
title: linux 常用命令
date: 2017-06-17 19:42:25
tags: ["linux"]
categories: ["linux"]
---

使用ubuntu的时候经常会把常用的一些命令忘掉或不知道有些参数的意思，又懒得看那枯燥的文档。因此记录下来备忘。<br>
<!-- more -->

# 目录类
1. ls: 查看文件与目录
    ```bash
    ls [-aAdfFhilnrRSt] 目录或文件
    ls [--color={never,auto,always}] 目录或文件
    ls [--full-time] 目录或文件
    -a: 列出全部文件，包括隐藏文件
    -A: 列出全部文件，包括隐藏文件，但不包括 . 与 .. 两个目录
    -d: 仅列出目录，不列出目录内的文件
    -f: 直接列出结果，不进行排序 (ls默认会以文件名排序)
    -F: 根据文件、目录等信息给予附件数据结构 ( *:代表可执行文件，/: 代表目录，=: 代表socket文件，|: 代表FIFO文件)
    -h: 将文件容量以易读的方式列出
    -i: 列出inode号码
    -l: 列出文件属性权限等
    -n: 列出UID与GID，而非用户与用户组名
    -r: 将排序结果反向输出
    -R: 连同子目录内容一起列出来
    -S: 以文件容量大小排序
    -t: 以时间排序
    -color: never(不要依据文件特性给予颜色显示)，always(显示颜色)，auto(系统判定是否显示颜色)
    --full-time: 以完整的时间模式输出 年、月、日、时、分
    --time={atime, ctime}: 输出访问时间或改变权限属性时间(ctime)，而非内容更改时间
    ```

2. cd: 切换目录
    ``` bash
    cd [相对路径或者绝对路径]
    #回到自己的主文件夹
    cd [or cd ~]
    #回到上层目录
    cd ..
    #回到刚才的目录
    cd -
    ```

3. pwd : 显示当前目录
    ```bash
    pwd [-P]
    -P:显示当前的路径，而非使用连接路径
    ```

4. mkdir : 新建目录
    ```bash
    mkdir [-mp] 目录名称
    -m:配置文件夹的权限，忽略默认权限（umask)
    -p:递归地创建目录
    #新建权限为rwx--x--x的目录
    mkdir -m 711 dir_name
    ```

# 复制删除移动
1. 复制 cp
    ```bash
    #只复制一个文件或文件夹
    cp [-adfilprsu] 源文件 目标文件
    -a: 相当于 -pdr，看后文
    -d：若文件为连接文件，则只复制连接文件的属性
    -f: 若目标文件已经存在且无法开启，则删除后再试一次
    -i: 若目标文件已存在，覆盖时会先询问是否覆盖
    -l: 创建文件的硬链接
    -p: 连同文件的属性一起复制，备份常用
    **-r: 递归复制，用于目录的复制**
    -s: 创建文件的软连接
    -u: 若目标文件比源文件旧才更新目标文件
    #复制多个文件到某一文件夹下
    $ cp [options] 源文件1 源文件2 ... 目标文件
    ```

2. 移除文件或目录
    ```bash
    rm [-fir] 文件或目录
    -f: 忽略不存在的文件
    -i: 删除前再次确认
    -r: 递归删除，主要用来删除目录
    ```

3. 移动文件目录或者更名
    ```bash
    mv [-fiu] 文件或目录 目标文件或目录
    -f: 目标文件存在时，不询问直接覆盖
    -i: 目标文件存在，询问是否覆盖
    -u: 目标文件存在，且较新时，才会更新
    ```

4. 非纯文本 `od`
    ```bash
    od [-t TYPE] 文件
    -t: 后面可以接各种类型输出：
    a : 利用默认字符输出
    c : 使用ASCII字符输出
    d[size]: 用十进制输出数据，每个整数占用 size bytes
    f[siez]: 用浮点数输出数据，每个整数占用 size bytes
    o[size]: 用八进制来输出数据，每个整数占用 size bytes
    x[size]: 用十六进制来输出数据，每个整数占用 size bytes
    ```
    
5. 连接文件 `ln`
硬连接是将文件对应到同一个inode号码上的连接。硬连接不能跨文件系统，不能连接到目录。符号连接就是windows下的快捷方式。
```bash
ln [-sf] 源文件 目标文件(符合连接文件)
    -s: 创建符号连接，而不是硬连接
    -f: 如果目标文件存在，就将目标文件删除后再创建
```

6. 清空文件
清空文件有三种方式：
```bash
cat /dev/null > clear.txt
echo "" > clear.txt
> clear.txt
```

# 命令与文件查询
1. which：可以查询脚本文件的位置，比如 `ifconfig` 命令的位置。但是不能够查询bash内置的命令，比如`cd`
    ```bash
    which [-a] command
    -a: 列出所有 PATH 目录中包含的命令，没有该参数，只会列出第一个
    ```

2. whereis： 定位命令的二进制，源文件和帮助文件
    ```bash
    whereis [-bmsu] 文件或目录名
    -b : 只找二进制文件
    -m : 在menu下查找
    -s : 只找源文件
    -u : 其他文件
    ```

3. locate： 根据文件名搜索文件，输出所有的文件。因为是从存储文件记录的数据库文件`/var/lib/mlocate`中读取的，所以速度快。但是数据库文件是定时更新的，所以新增的文件查询不到。可以通过`updatedb`来更新文件，因为该命令是查找硬盘的，所以执行比较慢。
    ```bash
    locate [-ir] 文件名
    -i: 忽略大小写差异
    -r: 可以接正则表达式
    ```

4. find: 在目录下搜索文件，与xargs一起使用，功能强大
    ```bash
    find [PATH] [option] [action]
    # 与时间有关的参数有 -atime, -ctime, -mtime, 这三个参数使用方法类似。
    -mtime n: 在n天之前的 一天内 被更改过的文件
    -mtime +n: 在n天之前（不含n天）被更改过的文件
    -mtime -n: 在n天之内（含n天） 被更改过的文件
    -newer file: file问一个文件的路径，列出比file新的文件
    -newermt time: 比time更新的文件

    -type TYPE: 查找的文件类型，主要有： 一般文件(f), 设备文件(b,c), 目录(d), 连接文件(l), socket(s)等
    -perm [+/-]mode: 查找文件权限，刚好等于mode， "-" 表示文件权限必须包含 mode， "+" 表示文件权限包含任一 mode

    -name filename: 查找文件名为filename的文件， 使用通配符表示文件名时，需要加上 ''
    -size [+-]SIZE: 查找比size还要大(+)或小(-)的文件 ,可以是用K\M\G

    -exec command: command为命令，该命令可以处理查找结果，不支持别名。
    find / -exec ls -l {}\;
    find命令会将所有匹配到的文件一起传递给exec执行，但有些系统对能够传递给exec的命令有长度限制，会出现溢出错误。这时候可以使用xargs。
    find . | xargs grep xxx: 查找当前目录下含有x的文件
    ```

5. xargs: 给其他命令传递参数的一个过滤器，可以将标准输出数据转换成命令行参数
```bash
> cat test.txt
a b c d e f g
h i j k l m n
o p q
> cat test.txt | xargs
a b c d e f g h i j k l m n o p q
> cat test.txt | xargs -n3
a b c
d e f
g h i
j k l
m n o
p q 

> cat arg.txt | xargs -I {} ./sk.sh -p {} -l
-p aaa -l
-p bbb -l
-p ccc -l
```

# 更改权限
权限分数为： r(read)=4, w(write)=2, x(execute)=1
    ```bash
    chgrp [-R（递归更改)] groupname dirname/filename: 改变文件所属用户组
    chown [-R（递归更改)] username[:groupname] dirname/filename：改变文件所有者
    chmod [-R（递归更改)] [options] dirname/filename：改变文件所有者
    chmod 761 file: 将文件权限更改为 =rwxrw---x
    chmod u=rwx,g=rw,o=x file: u(user) g(group) o(others) =(设置) +(增加) -(取消)
    ```

# 数据流重定向
- `1>` : 以覆盖的方式将正确的数据输出到指定的文件或设备上
- `1>>` : 以累加的方式将正确的数据输出到指定的文件或设备上
- `2>` : 以覆盖的方式将错误的数据输出到指定的文件或设备上
- `2>>` : 以累加的方式将错误的数据输出到指定的文件或设备上
- `command > list 2>&1` ： 将正确信息和错误信息都输入到list文件中
- `command 2> /dev/null` : 不保存错误信息

# 命令执行判断依据： `;` `&&` `||`
- `com1;com2`  不考虑命令的相关性，连续执行命令 
- `com1&&com2` 前一个命令执行正确($?=0)，才执行第二个命令
- `com1||com2` 前一个命令执行不正确($?!=0)，才执行第二个命令


# 查看文件内容
1. cat
    ```bash
    cat [-AbEnTv] 文件
    -A: 相当于-vET 的整合，可列出一些特殊字符而不是空白
    -b: 列出行号，仅针对非空白行做行号显示
    -E: 将结尾的断行字符以 $ 显示出来
    -n: 打印行号，连同空白行也会有行号
    -T: 将[Tab]以 ^I显示出来
    -v: 列出一些看不出来的特殊字符
    ```

2. 翻页查看more
    ```bash
    more 文件路径
    #空格键    向下翻一页
    #Enter    向下滚动一行
    #/字符串   在内容中向下查找字符串
    #:f       显示文件名以及目前显示的行数
    #q        离开more，不再显示内容
    #b        向回翻页，只对文件有效
    ```

3. 翻页查看 less
    ```bash
    less 文件路径
    #空格键         向下翻一页
    #[PageDown]    向下翻一页
    #[PageUp]      向上翻一页
    #/字符串   在内容中向下查找字符串
    #?字符串   在内容中向上查找字符串
    #n        重复前一个查询
    #N        向上重复前一个查询
    #q        离开
    ```

4. 数据选取 head
    ```bash
    head [-n number] 文件
    -n: 接数字，表示显示头几行，默认显示前10行
    ```

5. 数据选取tail
    ```bash
    tail [-n number] 文件
    -n: 表示显示几行
    ```

另外可以修改 /etc/issue文件来改变终端的提示信息

# 文件分割
`split` 命令可以用来分割文件：
```bash
split -l 50000  4-2019-09-02-15-54-17.log -d -a 2  sp/4-2019-09-02-15-54-17_
```

参数说明：
- `l`: 每个文件拆分的行数
- `d`: 拆分之后的文件用数字计数，没有该参数的话，用字母
- `a`： 拆分后生成顺序文件名的长度

# 压缩
1. zip
    ```bash
    zip [-AcdDfFghjJKlLmoqrSTuvVwXyz$] [-b <工作目录>] [-ll] [-n <字尾字符串>] [-t <日期时间>] [-<压缩效率>] [压缩文件名] [待压缩文件...] [-i <范本样式>] [-x <范本样式>]
    -A: 调整可执行的自动解压缩文件。
    -b: <工作目录> 指定暂时存放文件的目录。
    -c: 替每个被压缩的文件加上注释。
    **-d: 从压缩文件内删除指定的文件**
    -D: 压缩文件内不建立目录名称。
    -F: 尝试修复已损坏的压缩文件。
    -g: 将文件压缩后附加在既有的压缩文件之后，而非另行建立新的压缩文件.
    **-i <范本样式>: 只压缩符合条件的文件。**
    **-x <范本样式>: 压缩时排除符合条件的文件。**
    -X: 不保存额外的文件属性。
    -j: 只保存文件名称及其内容，而不存放任何目录名称。
    -J: 删除压缩文件前面不必要的数据。
    -l: 压缩文件时，把LF字符置换成LF+CR字符。
    -ll: 压缩文件时，把LF+CR字符置换成LF字符。
    **-m: 将文件压缩并加入压缩文件后，删除原始文件，即把文件移到压缩文件中**
    **-n<字尾字符串>: 不压缩具有特定字尾字符串的文件。**
    -q: 不显示指令执行过程
    **-r: 递归处理，将指定目录下的所有文件和子目录一并处理。**
    -t<日期时间>: 把压缩文件的日期设成指定的日期
    -y: 直接保存符号连接，而非该连接所指向的文件，本参数仅在UNIX之类的系统下有效。
    # 示例
    zip -r search.zip search/  # 将search目录打包的zip文件中
    zip -r -x *.css search.zip search/ # 打包search目录，单不包含css文件
    ```

2. 使用zipsplit分割压缩的zip文件
    ```bash
    zipsplit (选项) (参数)
    -n: 指定分割后每个zip文件的大小，是字节大小；
    -t: 报告将要产生的较小的zip文件的大小；
    -b: 指定分割后的zip文件的存放位置。
    ```
    
3. tar压缩
tar参数中 -x,-c,-t不能同时出现。
    ```bash
    # 打包
    tar [-j|-z] [cv] [-f 新建的文件名] filename/dirname
    # 查看文件名
    tar [-j|-z] [tv] [-f 新建的文件名]
    # 解压
    tar [-j|-z] [xv] [-f 新建的文件名] [-C 目录]
    -c: 新建打包文件
    -t: 查看打包文件的内容有哪些文件名
    -x: 解压功能，可以配合 C 解压到特定目录
    -j: 通过bzip2来压缩/解压文件，此时文件名最好是 *.tar.bz2
    -z: 通过gzip来压缩/解压文件，此时文件名最好是 *.tar.gz
    -v: 在压缩/解压的过程中，将处理的文件名显示出来
    -f filename: -f后接要处理的文件名
    -C: 解压缩时，将文件解压到特定的目录

    -p: 保留备份数据的原本权限与属性，常用于备份重要的配置文件
    -P: 保留绝对路径
    --exclude=FILE: 在压缩过程中，不打包FILE

    # 示例
    tar -zcv -f filename.tar.gz 要压缩的文件或目录名 #压缩
    tar  -zxv -f filename.tar.gz -C 欲解压的目录  # 解压
    tar -jcv -f system.tar.bz2 --exclude=/etc* --exclude=gz* /etc/root
    ```

# 访问网络内容 `wget` `curl`
`wget` 用于从网络上下载资源，若没有指定目录，默认为当前目录
```bash
wget [参数] [url地址]
    -o, –output-file=FILE: 把**记录**写到FILE文件中
    -a, –append-output=FILE: 把**记录**追加到FILE文件中
    -d, –debug: 打印调试输出
    -q, –quiet: 安静模式(没有输出)
    -v, –verbose: 冗长模式(这是缺省设置)
    -nv, –non-verbose: 关掉冗长模式，但不是安静模式
    -i, –input-file=FILE: 下载在FILE文件中出现的URLs
    -F, –force-html: 把输入文件当作HTML格式文件对待
    -B, –base=URL: 将URL作为在-F -i参数指定的文件中出现的相对链接的前缀
    –sslcertfile=FILE: 可选客户端证书
    –sslcertkey=KEYFILE: 可选客户端证书的KEYFILE
    –egd-file=FILE: 指定EGD socket的文件名
    -t, –tries=NUMBER 设定最大尝试链接次数(0 表示无限制).

    -O –output-document=FILE 把文档写到FILE文件中
    -nc, –no-clobber 不要覆盖存在的文件或使用.#前缀
    -c, –continue 接着下载没下载完的文件
    –progress=TYPE 设定进程条标记
    -N, –timestamping 不要重新下载文件除非比本地文件新
    -S, –server-response 打印服务器的回应
    –spider 不下载任何东西
    -T, –timeout=SECONDS 设定响应超时的秒数
    -w, –wait=SECONDS 两次尝试之间间隔SECONDS秒
    –waitretry=SECONDS 在重新链接之间等待1…SECONDS秒
    –random-wait 在下载之间等待0…2*WAIT秒
    -Y, –proxy=on/off 打开或关闭代理
    -Q, –quota=NUMBER 设置下载的容量限制
    –limit-rate=RATE 限定下载输率

    --post-data="" : 通过post方式提交数据
```

# 管道命令 `|` - `cut` `grep` `sort` `uniq`
每个`|`后面接的第一个数据必须是能够接受standard input数据的命令，而且管道命令只能处理standard output，对于error output会忽略
1. `cut` 命令
    ```bash
    cut –d '分隔符' –f fields
        -d : 后边接分隔符， 与-f一起使用
        -f : 依据-d的分隔符将一段信息切割成为数段，有-f取出第几段的意思
    last | cut –d ' ' –f 1,2
    ```

2. `grep` 可用正则
    ```bash
    grep [-cinvP] [--color=auto] '查找字符串(正则)' filename
        -c : 计算找到字符串的次数
        -i : 忽略大小写
        -n : 给出行号
        -v : 反向选择
        -P : 用Perl正则表达式来匹配
    grep -–color=auto 'manpath' /etc/man.config
    ```

3. `sort` 排序命令
    ```bash
    sort [-fbMnrtuk] [file or stdin]
        -f: 忽略大小写
        -b: 忽略最前面的空格
        -M: 以月份的名字排序
        -n: 使用“纯数字”进行排序（默认是文字类型）
        -r: 反向排序
        -u: 同umiq
        -t: 分隔符，默认是[tab]分割
        -k: 以哪个区间来进行排序
    ```

4. `uniq` 计数
    ```bash
    uniq [-ic]
        -i: 忽略大小写字符的不同 
        -c: 进行计数
    ```

# 时间
`date`命令能够通过`date +Format`设置输出格式
```bash
date +Format
    - %Y : 年份
    - %y : 年份的最后两位
    - %d : 按月计的日期(例如：01)
    - %D : 按月计的日期；等于%m/%d/%y
    - %H : 小时(00-23)
    - %I : 小时(00-12)
    - %m : 月份
    - %M : 分钟
    - %S : 秒(00-60)
date +%Y%m%d%H%M%S   => 20170617194225
# 输出时间 是一小时前的时间
date -d'-1 hour' +%F' ' %T  
```
通过 `date -s` 设置时间
```bash
date -s 06/17/2017
date -s 19:42:25
```

# 磁盘与目录容量
1. `df` : 列出文件系统的整体磁盘使用量
    ```shell
    df [-ahikHTm] [目录或文件名]
        -a : 列出所有文件系统，包括系统特有的/proc等文件系统
        -h : 以人们较易阅读的GB、MB、KB等格式显示
        -i : 不用硬盘容量，而以inode的数量来显示
        -k/m : 以MB/KB的容量显示文件系统
    ```

2. `du` : 评估文件系统的磁盘使用量
    ```shell
    du [-ahskm] 文件或目录名称
        -a : 列出所有的文件与目录的容量，因为默认仅统计目录下面的文件量，不能同 s 一起使用
        -h : 以较易阅读的格式显示
        -s : 列出总量，不列出每个个别的目录占用量
        -S : 尚不理解
        -k/m : 以MB/KB的容量显示文件系统

    du -sh ./*  # 可以查看当前目录下文件大小，对于磁盘满了，寻找大文件很有用
    如果找不到，那么可以使用 `find . -type f -size +500M` 命令全局查找大文件
    ```


# `lsof` (list open files)
在linux下，任何事物都以文件的形式存在，通过文件不仅可以访问常规数据，还可以访问网络连接和硬件。如TCP和UDP套接字等。系统在后台都为该应用程序分配了一个文件描述符，该文件描述符提供了大量关于这个应用程序本身的信息。
```bash
lsof
    -a : 列出打开文件存在的进程
    -c<进程名>: 列出指定进程所打开的文件
    -g : 列出GID号进程详情
    -d<文件号> : 列出占用该文件号的进程
    +d<目录> : 列出目录下被打开的文件
    +D<目录> : 递归列出目录下被打开的文件
    -n<目录> : 列出使用NFS的文件
    -i<条件> : 列出符合条件的进程。（4、6、协议、:端口、 @ip ）
    -p<进程号> : 列出指定进程号所打开的文件
    -u : 列出UID号进程详情
    -h : 显示帮助信息
    -v : 显示版本信息
```

# 远程同步命令
## `rsync`命令
`rsync`命令是用来远程同步数据的，可以通过LAN/WAN快速同步多台机器间的文件。`rsync`通过自己的算法来比较本地和远程文件的不同部分，而不是每次都整份传送，所以速度比`scp`快。
`:`表明是通过远程shell连接，而`::` 和 `rsync://` 用于连接到rsync守护进程，它需要 src 或 dest 以模块名称开头
```bash
# 拷贝本地文件
rsync [options] src dest
# 使用一个远程shell程序将本地机器的内容拷贝到远程机器
rsync [options] src [user@]host:dest
# 使用一个远程shell将远程机器拷贝到本地机器
rsync [options] [user@]host:src dest
# 从远程rsync服务器中拷贝文件到本地机器
rsync [options] [user@]host::src dest
# 从本地机器拷贝文件到远程rsync服务器中
rsync [options] src [user@]host::dest
# 列远程机器的文件列表，类似rsync传输，不过需要在命令中省略本地机器
rsync [options] rsync://[user@]host[:port]/src [dest]

# 参数
-v, --verbose: 详细模式输出
-q, --quiet: 精简模式输出
-c, --checksum: 打开校验开关
-a, --archive: 归档模式，表示以递归方式传输文件，并保持所有文件属性，相当于 `-rlptgoD`
-r, --recursive: 对子目录以递归模式处理
-R, --relative: 使用相对路径信息
-b, --backup: 创建备份
    --backup-dir=DIR 将备份文件放到 DIR 中
    --suffix=SUFFIX: 定义备份文件前缀
-u, --update: 跳过接收机器上较新的文件
-d, --dirs: 不会递归地传输目录
-l, --link: 保留软连接
-k, --copy-dirlinks: 将符号链接转换为指定目录
-K, --keep-dirlinks: 将符号链接作为一个递归的目录
-H, --hard-links: 保留硬链接
-p, --perms: 保持文件权限
-o, --owner: 保持文件属主信息
-g, --group: 保持文件属组信息。 
-D, --devices: 保持设备文件信息
** -t, --times: 保持文件时间信息  **
-S, --sparse: 对稀疏文件进行特殊处理以节省DST的空间
-w, --whole-file: 拷贝文件，不进行增量检测 
-x, --one-file-system: 不要跨越文件系统边界
-B, --block-size=SIZE: 检验算法使用的块尺寸，默认是700字节
-e, --rsh=command: 指定使用rsh、ssh方式进行数据同步
    --rsync-path=PATH: 指定远程服务器上的rsync命令所在路径信息
    --existing: 仅仅更新那些已经存在于DST的文件，而不备份那些新创建的文件
    --delete: 删除那些DST中SRC没有的文件
    --delete-excluded: 同样删除接收端那些被该选项指定排除的文件
    --delete-after: 传输结束以后再删除
    --ignore-errors: 即使出现IO错误也进行删除
    --max-delete=NUM: 最多删除NUM个文件
-C, --cvs-exclude: 使用和CVS一样的方法自动忽略文件，用来排除那些不希望传输的文件
-P, --partial: 保留那些因故没有完全传输的文件，以是加快随后的再次传输
--force: 强制删除目录，即使不为空
--numeric-ids: 不将数字的用户和组id匹配为用户名和组名
--timeout=time: ip超时时间，单位为秒
-I, --ignore-times: 不跳过那些有同样的时间和长度的文件
--size-only: 当决定是否要备份文件时，仅仅察看文件大小而不考虑文件时间
--modify-window=NUM: 决定文件是否时间相同时使用的时间戳窗口，默认为0
-T --temp-dir=DIR: 在DIR中创建临时文件
--compare-dest=DIR: 同样比较DIR中的文件来决定是否需要备份。
--progress: 显示备份过程
-z, --compress: 对备份的文件在传输时进行压缩处理
    --exclude=PATTERN: 指定排除不需要传输的文件模式
    --include=PATTERN: 指定不排除而需要传输的文件模式
    --exclude-from=FILE: 排除FILE中指定模式的文件
    --include-from=FILE: 不排除FILE指定模式匹配的文件
    --version: 打印版本信息
    --address: 绑定到特定的地址
    --config=FILE: 指定其他的配置文件，不使用默认的rsyncd.conf文件
    --port=PORT: 指定其他的rsync服务端口
    --blocking-io: 对远程shell使用阻塞IO
    -stats: 给出某些文件的传输状态
    --progress: 在传输时现实传输过程
    --password-file=FILE: 从FILE中得到密码
-i, --itemize-changes: 输出所有更新的更改摘要
     --out-format=FORMAT     使用指定的FORMAT输出
     --log-file=FILE         将rsync做的操作记录到FILE中
     --list-only             只列出文件，而不拷贝
     --bwlimit=KBPS          限制I/O带宽，KBytes per second
-h, --help: 显示帮助信息。
```

示例：
```bash
rsync -azi machine::user/path/dir/ /search/odin/ --exclude '*_log*'
```

# 定时任务
在linux上，使用 `crontab` 来创建循环性工作调度。当然为了安全，可以通过`/etc/cron.allow`和`/etc/cron.deny`来限制用户使用 `crontab`。
当用户是用`crontab`来新建工作调度时，该项工作就会被记录到 `/var/spool/cron` 中，而且是以用户的账户来作为判别的。一般来说，不建议直接编辑`/var/spool/cron`中的文件，因为可能会由于输入语法错误，而导致 cron 不能正确执行。
```bash
crontab [-u username] [-l|-e|-r]
-u: 只有root才能使用这个参数，可以用其他用户的名义新建/删除 crontab 任务 
-l: 查看 crontab 的内容
-e: 编辑 crontab 的内容
-r: 删除所有的 crontab 的工作内容

# 示例
# 使用 crontab -e 进入到任务编辑页面
crontab -e  
 0  1  *  *  * shell exec.sh
#分 时 日 月 周   执行内容
```

只要通过 `:wq` 或者 `:x`，保存退出后，任务就自动定时执行。
配置时间方式如下表所示

分 | 时 | 日期 | 月 | 周 | 命令
:--|:--|:----|:---|:---|:---
0-59| 0-23| 1-31|1-12|0-7| command

“周”的0和7都是代表星期日的意思。还有一些辅助字符，如下：

特殊字符 | 代表意义
:-------| :--------
\* (星号) | 代表任何时刻都接受，上例中，表示无论日月周是什么，知道是1点钟，就执行命令
, (逗号)  | 代表分隔时段的意思。如要执行任务的时间是 3:00 和 6:00, 那么配置是： `0 3,6 * * * command`
\- (减号)  | 代表一段时间范围内。8点到12点的每小时的20分钟执行任务，那么配置是： `20 8-12 * * * command`
/n        | n是数字，即每隔 n 单位的时间间隔执行命令。例如每五分钟执行一次： `*/5 * * * * command` 

`contab -e` 是针对用户的 cron 来设计的，如果是系统例行性任务，可以直接编辑 `/etc/crontab` 这个文件。 `/etc/crontab` 是一个纯文本文件，可以用root账号编辑。
cron 服务最低的检测限制是“分钟”，所以cron 会每分钟去读取一次 `/etc/crontab` 与 `/var/spool/cron` 中的数据，所以，只要编辑并保存这些文件，就会生效。如果不生效，可以使用 `/etc/init.d/crondrestart` 来重启 cron 服务。

# bash环境中的特殊符号
bash环境中，有些符号是有特殊意义的：

符号   | 意义
:-----|:----
 \#    | 注释符号，最常用于script中
 \\    | 转义符号，之后跟的特殊符号作为一般符号处理
 \|   | 管道，分割两个管道命令
 ;    | 连续命令执行分隔符
 ~    | 用户的主文件夹
 $    | 使用变量前导符号
 &    | 将命令在后台运行
 /    | 目录符号
\ >, >>| 数据流重定向，输出导向
<, << | 数据流重定向， 输入导向
''    | 单引号，不具有变量置换的功能
""    | 双引号，具有变量置换的功能，其中的变量会用其值替换
\`\`    | 两个 \` 中间为可以先执行的命令，也可以使用 $()
()     | 中间为子shell的起始与结束
{ }    | 中间为命令块的组合

# 进程查看
我们使用 `ps` `top` 和 `pstree` 来查看进程的运行情况。

## `ps` : 将某个时间点的进程运行情况选取下来
一般 `ps` 有两种用法： `ps -l` 仅查看自己的bash相关的进程； `ps aux` 查看所有系统运行的程序。语法如下：
```bash
ps [options]
 -A: 所有的进程均显示出来
 -a: 与 terminal 无关的所有进程
 -u: 有效用户相关的进程
 x: 通常与 a 一起使用，可得到较为完整的信息

 l: 较详细地将PID的信息列出
 j: 工作的格式
 -f: 做一个更完整的输出

# 示例
ps aux
 USER       PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND
root         1  0.0  0.0  10352   680 ?        Ss    2015  18:00 init [3]                             
root         2  0.0  0.0      0     0 ?        S<    2015   1:40 [migration/0]
root         3  0.0  0.0      0     0 ?        SN    2015   0:50 [ksoftirqd/0]
root         4  0.0  0.0      0     0 ?        S<    2015   0:00 [watchdog/0]
```

`ps aux` 结果中，各字段的意义为：

字段名        | 字段含义
:------------| :----------------
USER         | 该进程属于哪个用户账号
PID          | 该进程的标志符
%CPU         | 该进程使用的CPU资源的百分比
%MEM         | 该进程所占用的物理内存百分比
VSZ          | 该进程使用的虚拟内存量
RSS          | 该进程占用的固定内容量
TTY          | 该进程是在哪个终端机上运行，若与终端无关则显示 ?；若为 pts/0 表示由网络连接的主机进程
STAT         | 该进程目前的状态。R(正在运行), S(可唤醒的睡眠状态), D(不可唤醒的睡眠状态，通常为等待I/O), T(停止状态), Z(僵尸状态，进程已经终止但无法删除)
START        | 该进程启动的时间
TIME         | 该进程使用CPU运行时间
COMMAND      | 该进程是哪个命令产生的

## top 动态查看进程的变化
`top` 命令可以持续检测程序的运行状态，使用方式如下：
```bash
top [-d 数字] | top [-bnp]
参数如下：
-d: 后面可以接秒数，就是整个进程界面更新的秒数，默认是 5s
-b: 以批次的方式执行top，通常会结合数据流重定向来批处理结果
-n: 与 -b 搭配，表示需要进行几次 top 结果的数据
-p: 指定某些PID来进行查看
-H: 显示线程
在top命令中，可以使用如下按键命令：
   ?: 显示在top当中可以输入的按键指令
   P: 以 CPU 的使用资源排序显示
   M: 以内存的使用资源排序显示
   N: 以PID来排序
   T: 该进程使用的CPU时间累积排序
   k: 给予某个PID一个信号
   r: 给某个PID重新定制一个nice属性
   q: 离开top
```

## pstree 查看进程树
```sh
pstree [-A|U] [-up]
参数：
-A: 各进程树之间的连接
-U: 各进程树之间的连接以utf8码的字符来连接
-p: 列出每个进程的PID
-u: 列出每个进程的账号
示例：

init(1)-+-agetty(2112)
        |-crond(2071)---crond(13076)---sh(13084)---sleep(13112)
        |-events/0(8)
        |-events/1(9)
        |-glusterfs(19972)-+-{glusterfs}(19973)
        |                  \`-{glusterfs}(19982)
        |-httpd(1988)-+-httpd(884,apache)
```

## kill 进程管理
我们可以使用 kill 或者 killall 来向进程发送信号。使用如下：
```bash
kill -signal PID
killall [-iIe] -signal 命令名称
参数: 
-i: interactive ，交互式的，若需要删除，会出现提示
-e: exact，表示后边接的 命令名称 要一致，但完整的命令不能超过15个字符
-I: 命令名称（可能含有参数），忽略大小写
```

其中常用的 signal 有：

代号  | 名称  | 含义
:----|:------|:-----
1    | SIGHUP| 启动被终止的进程，可以让该PID重新读取自己的配置，类似重启
2    | SIGINT| 相当于使用键盘的 ctrl+c 来中断一个进程
9    | SIGKILL| 代表强行中断一个进程的运行，如果进程执行到一半，尚未完成的部分不会被处理
15   | SIGTERM| 正常结束进程的执行。后续操作完成后，才终止
17   | SIGSTOP| 相当于使用键盘的 ctrl+z 来暂停一个进程的执行

```bash
# 关闭特定的进程
kill -SIGHUP $(ps aux | grep 'syslog' | grep -v 'grep' | awk '{print $2}')

# 关闭一个命令开启的所有进程
killall -i -9 bash
```

# 文件格式化处理 -- `grep`, `sed`, `awk`

## `sed`
`sed` 是一个管道命令，有将数据进行替换、删除、新增、选取特定行等功能。
```sh
sed [-nerf] [动作]
-n: 安静模式，只有经过处理的那行才会被列出来
-e: 直接在命令行模式上进行 sed 的动作编辑
-f: 直接将sed的动作写在一个文件中
-r: sed支持扩展型正则表达式
-i: 直接修改读取文件的内容

动作说明
    n1, n2: 可选内容，代表选择进行动作的行数；若动作需要在 10到20行之间进行，则 10,20 function
    function 有如下参数：
        a: 新增
        c: 替换
        d: 删除
        i: 插入
        p: 打印
        s: 替换，通常s搭配正则表达式
```

假设 `/etc/passwd` 中有如下内容：
```
root:x:0:0:root:/root:/bin/bash
bin:x:1:1:bin:/bin:/sbin/nologin
daemon:x:2:2:daemon:/sbin:/sbin/nologin
adm:x:3:4:adm:/var/adm:/sbin/nologin
lp:x:4:7:lp:/var/spool/lpd:/sbin/nologin
sync:x:5:0:sync:/sbin:/bin/sync
```

删除用法如下：
```sh
# 删除2~3行
nl /etc/passwd | sed '2,3d'
# 控制台输出
     1	root:x:0:0:root:/root:/bin/bash
     4	adm:x:3:4:adm:/var/adm:/sbin/nologin
     5	lp:x:4:7:lp:/var/spool/lpd:/sbin/nologin
     6	sync:x:5:0:sync:/sbin:/bin/sync
```

添加用法如下：
```sh
# 在第二行后加上 drink tea ...... 与 drink beer?
nl /etc/passwd | sed '2a drink tea ...... \
> drink beer?'
## 控制台输出
     1	root:x:0:0:root:/root:/bin/bash
     2	bin:x:1:1:bin:/bin:/sbin/nologin
drink tea ...... 
drink beer?
     3	daemon:x:2:2:daemon:/sbin:/sbin/nologin
     4	adm:x:3:4:adm:/var/adm:/sbin/nologin
     5	lp:x:4:7:lp:/var/spool/lpd:/sbin/nologin
     6	sync:x:5:0:sync:/sbin:/bin/sync
```

输出指定行（使用 `-n` 安静模式）
```sh
# 输出第2到4行
nl /etc/passwd | sed -n '2,4p'
# 控制台输出如下
     2	bin:x:1:1:bin:/bin:/sbin/nologin
     3	daemon:x:2:2:daemon:/sbin:/sbin/nologin
     4	adm:x:3:4:adm:/var/adm:/sbin/nologin
```

整行替换：
```sh
# 替换第 2到3行
nl /etc/passwd | sed '2,3c No 2-3number'
# 控制台输出如下
     1	root:x:0:0:root:/root:/bin/bash
No 2-3number
     4	adm:x:3:4:adm:/var/adm:/sbin/nologin
     5	lp:x:4:7:lp:/var/spool/lpd:/sbin/nologin
     6	sync:x:5:0:sync:/sbin:/bin/sync
```

部分数据查找替换：
```sh
sed 's/要被替换的字符串/新字符串/g'
/sbin/ifconfig eth0 | grep "inet addr" |sed 's/^.*addr://g' | sed 's/Bcast.*$//g'
# 控制台输出
192.168.0.1

# 还可以使用正则匹配捕获内容，替换中 \0 代表整行，\1 代表捕获的第一个内容
sed -rn 's/.*filter_([0-9]+).*$/\0@@@\1/p'
```



```bash
ob.resin.201803281530
cat adcost | awk '{sum+=$1} END {print "Avg= ", sum/NR}'

awk 'BEGIN {max = 0} {if ($1+0 > max+0) max=$1} END {print "Max=", max}' adcost
sed -rn 's/.*NewBlacklistFilter_([0-9]+).*$/\0@@@\1/p'  | awk 'BEGIN {FS="@@@"}  {if $2 >0 sum+=$2} END {print "AVG= ", sum/NR}' 
| awk '{sum+=$1} END {print "Avg= ", sum/NR}'

grep -P 'cost:[0-9]+' --color
```

## `grep` 命令


## `awk` 命令
`awk` 内建了如下变量：

变量符号     | 变量说明
:-----------|:-------------
`$0`        | 当前行的内容
`$1~$n`     | 当前行的第n个字段，字段间由 `FS` 分隔
`FS`        | 字段分隔符，默认是空格或者Tab
`NF`        | 当前记录中字段个数，即列数
`NR`        | 行号，从1开始，有多个文件，该值也会累加
`FNR`       | 当前记录数，文件自己的行号
`RS`        | 行的分隔符，默认是换行
`OFS`       | 输出字段分隔符，默认是空格
`ORS`       | 输出行的分隔符，默认是换行符
`FILENAME`  | 当前文件名

### `awk` 条件判断
`awk` 做条件判断，可以用如下方式：
```sh
cat /etc/passwd | awk '{FS="":} $3 < 10 {print $1 "\t" $3}'
# 使用 if
cat /etc/passwd | awk '{FS="":} {if($3<10) printf "%10s\t%10s", $1, $3}' 
```

### `awk` 正则匹配
```sh
awk '/nologin/' /etc/passwd
## 控制台输出
bin:x:1:1:bin:/bin:/sbin/nologin
daemon:x:2:2:daemon:/sbin:/sbin/nologin
adm:x:3:4:adm:/var/adm:/sbin/nologin
```

匹配某个字段中出现的字符：
```sh
awk -F ':' '$1 ~ /oo/' /etc/passwd
# 控制台输出
root:x:0:0:root:/root:/bin/bash
```

正则匹配某个字段中出现的字符
```sh
awk -F ':' '$1 ~ /o+/' /etc/passwd
# 控制台输出
root:x:0:0:root:/root:/bin/bash
daemon:x:2:2:daemon:/sbin:/sbin/nologin
shutdown:x:6:0:shutdown:/sbin:/sbin/shutdown
```

### `awk` 内建函数
函数          | 说明
:------------|:------------------
`atan2(y,x)` | y/x 的反正切值
`cos(x)`     | x 的余弦值
`exp(x)`     | x 的指数函数
`int(x)`     | x 的整数部分
`log(x)`     | x 的自然对数
`rand()`     | 返回一个随机数
`sin(x)`     | x 的正弦值
`sqrt(x)`    | x 的平方根
`srand(x)`   | x 是 rand() 的随机数种子


字符串函数：

函数          | 说明
:------------|:------------------
`gsub(r,s)`  | 将字符串中所有出现的r替换为s,返回替换发生的次数
`gsub(r,s,t)`| 将字符串t中所有出现的r替换为s,返回替换发生的次数
`index(s,t)` | 返回字符串t在s中第一次出现的位置，如果t没有出现的话，返回0
`length(s)`  | 返回s包含的字符个数
`match(s,r)` | 测试s是否包含能被r匹配的子串，返回子串的起始位置或0；设置RSTART和RLENGTH
`split(s,a)` | 用 FS 将 s 分隔到数组 a 中，返回字段个数
`split(s,a,fs)`|	用fs分割s到数组a中， 返回字段的个数
`sprintf(fmt,expr-list)`|	根据格式字符串fmt返回格式化后的expr-list
`sub(r,s)`|	将$0的最左最长的，能被r匹配的子字符串替换为s，返回替换发生的次数
`sub(r,s,t)`|	把t的最左最长的，能被r匹配的子字符串替换为s，返回替换发生的次数
`substr(s,p)`|	返回s中从位置p开始的后缀
`substr(s,p,n)`|	返回s中从位置p开始的，长度为n的子字符串



# 其他快捷方式
1. 使用快捷键 `ctrl+r` 可以快速使用历史命令