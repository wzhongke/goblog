---
title: nginx 配置
date: 2017-12-21 11:08:00
tags: ["linux"]
categories: ["linux"]
---

## nginx配置之 `location`

nginx 的 `location` 配置可以有如下多种方式：
```
# 精确匹配
location = / {
    # 精确匹配 / ，主机名后不能有任何字符串
    operateA;
}

# 通用匹配，未匹配到其他location的请求都会匹配到该条
location / {
    # 因为所有的地址都是以 / 开头，所以这条规则将匹配到所有请求
    # 但是正则和最长字符串会优先匹配
    operateB;
}

# 前缀匹配，优先级比正则低
location /documents/ {
    # 匹配任何以 /documents/ 开头的地址
    # 匹配符合后需要继续匹配其他规则，如果没有优先级比其高的，才会使用
    operateC;
}

# 区分大小写的正则匹配
location ~ /document/abc {
    # 匹配任何以 /documents/abc 开头的地址
    # 匹配符合后需要继续匹配其他规则，如果没有优先级比其高的，才会使用
    operateD;
}

# 前缀匹配，优先级比正则高
location ^~ /images/ {
    # 匹配任何以 /images/ 开头的地址，匹配成功后，停止匹配其他规则，采用该规则
    operateE;
}
# 正则匹配，不区分大小写
location ~* \.(gif|png)$ {
    # 匹配所有以 gif, png 结尾的请求
    # 但是 /images/ 下的gif和png会被 operateE 处理
    operateF;
}
```
`location` 的优先级如下：
(location =) > (location 完整路径) > (location ^~ 路径) > (location ~,~* 正则顺序) > (location 部分起始路径) > (location /)

## nginx配置之 `rewrite`