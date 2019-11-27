---
title: CSS学习笔记
date: 2018-02-03 09:00:00
draft: true
categories: ["css"]
---

# 媒体查询
媒体查询包含一个可选的媒体类型和媒体特性表达式(0或多个)最终会被解析为true或false。如果媒体查询中指定的媒体类型匹配展示文档所使用的设备类型，并且所有的表达式的值都是true，那么该媒体查询的结果为true。
```html
<!-- link元素中的CSS媒体查询 -->
<link rel="stylesheet" media="(max-width: 800px)" href="example.css" />

<!-- 样式表中的CSS媒体查询 -->
<style>
@media (max-width: 600px) {
  .facet_sidebar {
    display: none;
  }
}
</style>
```

## 媒体查询中的逻辑操作符
媒体查询可以使用 `not`, `and`, `only` 等逻辑操作符

# 设置滚动条
```css
/* 设置滚动条的样式 */
::-webkit-scrollbar {
    width:12px;
}
/* 滚动槽 */
::-webkit-scrollbar-track {
    -webkit-box-shadow:inset006pxrgba(0,0,0,0.3);
    border-radius:10px;
}
/* 滚动条滑块 */
::-webkit-scrollbar-thumb {
    border-radius:10px;
    background:rgba(0,0,0,0.1);
    -webkit-box-shadow:inset006pxrgba(0,0,0,0.5);
}
::-webkit-scrollbar-thumb:window-inactive {
    background:rgba(255,0,0,0.4);
}
```