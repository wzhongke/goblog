---
title: less 语法
date: 2017-08-11
draft: true
---
参考网站： http://www.bootcss.com/p/lesscss/
# 变量
可以用如下方式定义变量，Less中的变量只能定义一次：
```css
@nice-blue: #5B83AD;

#header { color: @nice-blue;}
```
输出如下：
```css
#header { color: #5B83AD; }
```
# 混合
在Less中我们可以定义通用的属性集为一个class，之后可以在其他class中调用这些属性：
```css
.bordered {
    border-top: dotted 1px black;
    border-bottom: solid 2px black;
}
// 通过如下方式调用
#menu a {
    color: #111;
    .boardered;
}
```
输出如下：
```css
#menu a {
  color: #111;
  border-top: dotted 1px black;
  border-bottom: solid 2px black;
}
```
## 带参数的混合
可以定义一个带参数的混合：
```css
.border-radius (@radius) {
    border-radius: @radius;
    -moz-border-radius: @radius;
     -webkit-border-radius: @radius;
}
// 通过如下方式调用
#header {
  .border-radius(4px);
}
```
还可以定义默认参数的混合
```css
.border-radius (@radius: 5px) {
  border-radius: @radius;
  -moz-border-radius: @radius;
  -webkit-border-radius: @radius;
}
```

# &
串联选择器和伪类选择器需要使用`&`
```css
.bordered {
  &.float {
    float: left; 
  }
  .top {
    margin: 5px; 
    &:hover    { text-decoration: none }
  }
}
```