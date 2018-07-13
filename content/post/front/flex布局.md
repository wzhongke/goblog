---
title: flex 布局
date: 2018-06-08 18:51:00
tags: ["css"]
categories: ["css"]
---

[参考地址](https://css-tricks.com/snippets/css/a-guide-to-flexbox/)

Flexbox 布局模型旨在提供一种更有效的方式来布置，对齐和分配容器中的物品之间的空间，即使其尺寸未知或动态的。

Flexbox 布局最主要的思想是容器可以为了填满可用空间而修改其内部元素的宽高。一个扩展容器可以扩展它内部元素来填满剩余的空间，也可以防止溢出而缩小它内部的元素。

最重要的是，与常规布局不同，Flexbox 布局与方向无关。

> Flexbox 布局最适合应用程序的组件和小规模布局，而Grid布局则适用于大规模布局。

# 父元素属性
通过设置元素的 CSS 属性 `display: flex;` 来使用 Flexbox 布局。

## `flex-direction`
`flex-direction` 定义了容器的主轴，也就是容器内部元素的排列方向。有如下属性：
```css
.container {
    flex-direction: row | row-reverse | column | column-reverse;
}
```

- `row`（默认值）: `ltr` 是从左到右，`rtl` 是从右到左
- `row-reverse`: `ltr` 是从右到左，`rtl` 是从左到右
- `column`: 同 `row`，但是元素是从上到下
- `column-reverse` : 同 `row-reverse`，但是元素是从下到上

## `flex-wrap`
默认情况下，flex 元素会试着在一行内排列。可以用 `flex-wrap` 属性来调整：
```css
.container {
    flex-wrap: nowrap | wrap | wrap-reverse;
}
```

- `nowrap` (默认): 所有 flex 元素都会在一行
- `wrap` : flex 元素会从上到下包装成多行
- `wrap-reverse`: flex 元素会从下到上包装成多行

## `flex-flow`
`flex-flow` 是 `flex-direction` 和 `flex-wrap` 的缩写，默认是 `row nowrap`:
```css
.container {
    flex-flow: <flex-direction> || flex-wrap
}
```

## `justify-content`
`justify-content` 定义了容器内元素在主轴方向的对齐方式。如果一行的元素不是浮动的，或者是浮动但达到最大尺寸时，它会分配额外的空间。当元素溢出时，它也对元素的对齐进行控制。
```css
.container {
    justify-content: flex-start | flex-end | center | space-between | space-around | space-evenly;
}
```

- `flex-start`(默认): 元素从起始处开始
- `flex-end`: 元素从结束处开始
- `center`: 元素居中
- `space-between`: 元素平均分布，第一个元素在起始处，最后一个元素在结尾
- `space-around`: 元素平均分布剩余空间，每个元素两侧的空间相同。
- `space-evenly`: 两个元素之间的空间相等

{{<html>}}
<style>
#justify-content .container {
    background-color: rgb(87, 39, 75);
    height: 40px;
    display: flex;
}

#justify-content .item {
    background-color: peru;
    height: 20px;
    /* 控制父容器剩余空间分配比例 */
    /* flex-grow: 1; */
    align-self: center;
}

#justify-content span {
    display: block;
    margin-top: 10px;
    text-align: left;
}

#justify-content .item2 {
    margin: 0 10px;
}

#justify-content .container1 {
    justify-content: flex-start;
}

#justify-content .container2 {
    justify-content: flex-end;
}

#justify-content .container3 {
    justify-content: center;
}

#justify-content .container4 {
    justify-content: space-around;
}

#justify-content .container5 {
    justify-content: space-between;
}

#justify-content .container6 {
    justify-content: space-evenly;
}
</style>
<div id="justify-content">
    <span>flex-start</span>
    <div class="container container1">
        <div class="item item1" style="width: 100px"></div>
        <div class="item item2" style="width: 50px"></div>
        <div class="item item3" style="width: 150px"></div>
    </div>
    <span>flex-end</span>
    <div class="container container2">
        <div class="item item1" style="width: 100px"></div>
        <div class="item item2" style="width: 50px"></div>
        <div class="item item3" style="width: 150px"></div>
    </div>
    <span>center</span>
    <div class="container container3">
        <div class="item item1" style="width: 100px"></div>
        <div class="item item2" style="width: 50px"></div>
        <div class="item item3" style="width: 150px"></div>
    </div>
    <span>space-around</span>
    <div class="container container4">
        <div class="item item1" style="width: 100px"></div>
        <div class="item item2" style="width: 50px"></div>
        <div class="item item3" style="width: 150px"></div>
    </div>
    <span>space-between</span>
    <div class="container container5">
        <div class="item item1" style="width: 100px"></div>
        <div class="item item2" style="width: 50px"></div>
        <div class="item item3" style="width: 150px"></div>
    </div>
    <span>space-evenly</span>
    <div class="container container6">
        <div class="item item1" style="width: 100px"></div>
        <div class="item item2" style="width: 50px"></div>
        <div class="item item3" style="width: 150px"></div>
    </div>
</div>
{{</html>}}

## `align-items`
`align-items` 定义了主轴垂直方向，也就是辅轴方向元素对齐的方式。
```css
.container {
    align-items: flex-start | flex-end | center | baseline | stretch;
}
```

- `flex-start`: 辅轴开始边缘
- `flex-end`: 辅轴结束边缘
- `center`: 辅轴方向居中
- `baseline`: 根据基线对齐
- `stretch`(默认): 填满容器，还遵循 `min-width`/`max-width`

{{<html>}}
<style>
#align-items .container {
  background-color: rgb(87, 39, 75);
  height: 80px;
  width: 80%;
  display: flex;
}

#align-items .item {
  background-color: peru;
  height: 40px;
  width: 50px;
}

#align-items .item2 {
    margin: 0 10px;
    height: 30px;
}

#align-items .container1 {
  align-items: flex-start;
}

#align-items .container2 {
  align-items: flex-end;
}

#align-items .container3 {
  align-items: center;
}

#align-items .container4 {
  align-items: baseline;
}

#align-items .container5 {
  align-items: stretch;
}
</style>
<div id="align-items">
    <div style="display:flex;">
        <div>
        <span>flex-start</span>
        <div class="container container1">
            <div class="item item1"></div>
            <div class="item item2"></div>
            <div class="item item3"></div>
        </div>
        </div>
        <div>
        <span>flex-end</span>
        <div class="container container2">
        <div class="item item1"></div>
            <div class="item item2"></div>
            <div class="item item3"></div>
        </div>
        </div>
    </div>
    <span>center</span>
    <div class="container container3">
        <div class="item item1"></div>
        <div class="item item2"></div>
        <div class="item item3"></div>
    </div>
    <span>space-around</span>
    <div class="container container4">
        <div class="item item1"></div>
        <div class="item item2"></div>
        <div class="item item3"></div>
    </div>
    <span>space-between</span>
    <div class="container container5">
        <div class="item item1"></div>
        <div class="item item2"></div>
        <div class="item item3"></div>
    </div>
</div>
{{</html>}}