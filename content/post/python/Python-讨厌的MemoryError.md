---
title: Python 讨厌的MemoryError
date: 2015-04-01 15:41:43
tags: ["python"]
categories: ["python"]
---

在用Python处理大数据时，本来16G的内存，内存还没使用四分之一就开始报MemoryError的错误，后来才知道32bit的Python使用内存超过2G之后，就报这个错误，还没有其他的提示消息。果断换64bit的Python。
<!-- more -->
一开始安装32bit的Python，是因为numpy和scipy官方版本只支持32bit的，后来又找到了非官方的版本http://www.lfd.uci.edu/~gohlke/pythonlibs/#numpy<br>
wheel文件安装时出现：Fatal error in launcher: Unable to create process using '“”C:\Program Files (x86)\Python33\python.exe“” “C:\Program Files (x86)\Python33\pip.exe”<br>
在<a href="http://stackoverflow.com/questions/24627525/fatal-error-in-launcher-unable-to-create-process-using-c-program-files-x86" target="_blank">stackoverflow</a>上找到解决方法：
```bash
$python -m pip install XXX
```