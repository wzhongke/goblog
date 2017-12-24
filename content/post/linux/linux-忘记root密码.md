---
title: linux 忘记root密码
date: 2015-08-03 09:19:14
tags: ["linux"]
categories: ["linux"]
---

如果忘记了linux的root密码, 有两种比较通用的方式来修改

## 使用grub
1. 重启系统
2. 进入grub菜单： 在启动时，点击e进入详细设置；将光标移动到kernel上点击e进入编辑页面，输入如下指令，回车之后，按b就可以进入单用户维护模式
```bash
grub edit> kernel /vmlinuz-2.6.18-92.el5 ro root=LABEL=/ rhgb quiet <strong>single</strong>
```
3. 进入单用户维护模式后，系统会以root的权限直接给你一个shell，此时执行 passwd 就可以修改root密码了。
<!-- more -->
## 使用具有sudo权限的用户修改root密码
执行如下命令，输入用户密码后就可以修改密码了
```bash
$sudo passwd root
```
