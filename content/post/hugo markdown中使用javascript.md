---
title: hugo markdown 中使用javascript
date: 2017-12-24 15:13:00
tags: ["markdown"]
---

# 问题
在使用hugo制作个人博客时，不支持在markdown文件中直接引入`<script>/*javascript code*/</script>`。它会对javascript代码作markdown转换。

# 解决办法
使用hugo中的Shortcodes：
1. 在 `layouts` 文件夹中，新建一个 `shortcodes` 文件夹
2. 新建一个文件，暂时叫 `javascript.html`。名字可以随意，文件后缀名要用`html`。文件内容为`{{.Inner}}`
3. 在markdown中，通过如下方式使用：`&#123;&#123;< javascript >&#125;&#125;<script>/*javascript code*/</script>&#123;&#123;< /javascript >}}`。其中`&#123;&#123;< javascript >}}`的`javascript`同第二步中的文件名一致
