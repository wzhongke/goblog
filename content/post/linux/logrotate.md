---
title: Logrotate
---


Logrotate 是一个日志管理工具，用于分割日志文件，删除旧的日志文件，并创建新的日志文件

## 配置文件

Linux 系统默认安装 logrotate 工具，默认的配置文件在：
```
/etc/logrotate.conf
/etc/logrotate.d/
```

Logrotate是基于CRON来运行的，其脚本是 `/etc/cron.daily/logrotate` ，日志轮转是系统自动完成的。

如果等不及cron自动执行日志轮转，想手动强制切割日志，需要加-f参数；不过正式执行前最好通过Debug选项来验证一下（-d参数），这对调试也很重要
```
/usr/sbin/logrotate -f /etc/logrotate.d/nginx
/usr/sbin/logrotate -d -f /etc/logrotate.d/nginx
```

logrotate命令格式：`logrotate [OPTION...] <configfile>`
- d: --debug ：debug模式，测试配置文件是否有错误。
- f: --force ：强制转储文件。
- m: --mail=command ：压缩日志后，发送日志到指定邮箱。
- s: --state=statefile ：使用指定的状态文件。
- v: --verbose ：显示转储过程。

## 切割介绍
比如以系统日志 `/var/log/message` 做切割来简单说明下：
第一次执行完rotate(轮转)之后，原本的 `messages` 会变成 `messages.1`，而且会制造一个空的 `messages` 给系统来储存日志；
第二次执行之后，`messages.1` 会变成 `messages.2`，而 `messages` 会变成 `messages.1`，又造成一个空的 `messages` 来储存日志！
如果仅设定保留三个日志（即轮转3次）的话，那么执行第三次时，则 messages.3这个档案就会被删除，并由后面的较新的保存日志所取代！也就是会保存最新的几个日志。
日志究竟轮换几次，这个是根据配置文件中的rotate参数来判定的。