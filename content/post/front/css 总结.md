---
title: css使用方式总结
date: 2018-03-08 11:28:11
tags: ["css"]
categories: ["css"]
---

# 写在前面的话
在使用css的过程中，总会有一些需要查询或者迈过的坑，产生一些想法和处理问题的方式，亦或自己的经验和体会。将自己实践过程中碰到过的问题和解决方案记录下来，以备查阅。

# 元素的垂直居中
如果高度固定，简单的垂直居中的方式可以通过设置 `margin-top` 来解决。对于高度不能固定的元素，可以通过如下方式处理：

```CSS
.parent {
    position: relative;
}

.child {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
}
```

对于单行文字居中，可以将 `line-height` 设置为父元素的高度。

# `flex`
关于 flex 布局可以参考 https://css-tricks.com/snippets/css/a-guide-to-flexbox/

# margin
`margin` 属性使用百分比时，其相对元素是父元素的宽高。

# border
border使用的方式一般是 `border: width solid color`，其中color若采用 rgba 的方式设置透明度是不起作用的。
如果需要设置透明度，可以使用 `box-shadow: 0 0 0 0.35rem rgba(26,133,219,0.3);` 来对背景色加透明度处理。