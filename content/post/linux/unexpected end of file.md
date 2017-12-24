---
title: "linux下执行脚本出现报：syntax error: unexpected end of file"
date: 2015-07-23 17:16:32
tags: ["linux"]
categories: ["linux"]
---

这是因为从Windows上拷贝过去的文件，会由于Windows与linux的回车和换行表示方法不一致导致的，可以通过执行`dos2unix shellname.sh`来解决该问题

perl脚本在shell中调用时，也可能会出现这样的问题。通过`dos2unix perl.pl`命令可以修正