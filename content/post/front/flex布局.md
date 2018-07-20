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

# `align-content`
`align-content` 属性只在 flex 元素有多行子元素时有效。
`align-content` 主要用来调整伸缩行在伸缩容器中的对齐方式，同 `justify-content` 属性类似。
```css
.container {
    align-content: flex-start | flex-end | center | space-between | space-around | stretch;
}
```

- `flex-start`: 各行向伸缩容器的起点位置堆叠
- `flex-end`: 各行向伸缩容器结束位置堆叠
- `center`: 各行向伸缩容器的中间位置堆叠
- `space-between`: 各行在伸缩容器中平均分配剩余空间
- `space-around`: 各行在伸缩容器中平均分配剩余空间，两边各有一半空间
- `stetch`: 默认值，各行将会伸展以占用额外空间。

# 内部元素属性

## `order`
默认情况下，伸缩元素根据文档流的结构顺序排列。`order` 属性可以控制内部元素在容器中的先后位置。
```css
.item {
    order: <integer>; /* 默认是0 */
}
```

## `flex-grow`
`flex-grow` 定义了 flex 元素在必要时增长的规则。它接受一个无单位的值作为一个比例。它决定了容器内元素占用剩余空间的比例。
如果所有元素的 `flex-grow` 都是 1，那么容器内剩余的空间将会被其中的元素平均分配。如果其中一个元素的值是 2，那么该元素分配的空间将会是其他元素的 2 倍。
```css
.item {
    flex-grow: <number>; /* 默认值是 0 */
}
```

## `flex-shrink`
`flex-shrink` 定义了元素在必要时收缩的规则。
```css
.item {
    flex-shrink: <number>; /* 默认值是1 */
}
```

## `flex-basis`
`flex-basis` 定义了分配剩余空间之前，元素的默认大小。它的值可以是一个长度单位（20%，5rem等）或者是一个关键字。
```css
.item {
    flex-basis: <length> | auto; /* 默认是auto */
}
```

## `flex`
`flex` 是 `flex-grow`, `flex-shrink` 和 `flex-basis` 的缩写。`flex-shrink` 和 `flex-basis` 是可选值。
```css
.item {
    flex: none | [<'flex-grow'> <'flex-shrink'>?|| <'flex-basis'> ]]
}
```

## `align-self`
`align-self` 允许覆盖默认的对齐规则。
```css
.item {
    align-self: auto| flex-start| flex-end| center| baseline| stretch;
}
```


# 示例
元素左右垂直居中：
```css
.parent {
    display: flex;
    height: 300px; /* whatever */
}
.child {
    width: 100px; /* whatever */
    height: 100px; /* whatever */
    margin: auto;
}
```

{{<html>}}
<div id="flex-item">
<ul class="flex-container">  
<li class="flex-item">1</li>  
<li class="flex-item">2</li>  
<li class="flex-item">3</li>  
<li class="flex-item">4</li>  
<li class="flex-item">5</li>  
<li class="flex-item">6</li>
</ul>
</div>
<style>
#flex-item .flex-container {
  padding: 0;
  margin: 0;
  list-style: none;
  display: -webkit-box;
  display: -moz-box;
  display: -ms-flexbox;
  display: -webkit-flex;
  display: flex;
  -webkit-flex-flow: row wrap;
  justify-content: space-around;
}

#flex-item .flex-item {
  background: tomato;
  padding: 5px;
  width: 200px;
  height: 150px;
  margin-top: 10px;
  line-height: 150px;
  color: white;
  font-weight: bold;
  font-size: 3em;
  text-align: center;
}
</style>
{{</html>}}

导航：
{{<html>}}
<div id="navigation">
<ul class="navigation">
  <li><a href="#">Home</a></li>
  <li><a href="#">About</a></li>
  <li><a href="#">Products</a></li>
  <li><a href="#">Contact</a></li>
</ul>
</div>
<style>
#navigation .navigation {
  list-style: none;
  margin: 0;
  background: deepskyblue;
  display: -webkit-box;
  display: -moz-box;
  display: -ms-flexbox;
  display: -webkit-flex;
  display: flex;
  -webkit-flex-flow: row wrap;
  justify-content: flex-end;
}

#navigation .navigation a {
  text-decoration: none;
  display: block;
  padding: 1em;
  color: white;
}

#navigation .navigation a:hover {
  background: #00b7f5;
}

@media all and (max-width: 800px) {
  #navigation .navigation {
    justify-content: space-around;
  }
}
@media all and (max-width: 600px) {
  #navigation .navigation {
    -webkit-flex-flow: column wrap;
    flex-flow: column wrap;
    padding: 0;
  }

  #navigation .navigation a {
    text-align: center;
    padding: 10px;
    border-top: 1px solid rgba(255, 255, 255, 0.3);
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  }

  #navigation .navigation li:last-of-type a {
    border-bottom: none;
  }
}
</style>
{{</html>}}