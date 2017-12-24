---
title: css 动画
date: 2017-12-01 12:00:00
tags: ["javascript"]
---

<link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

# transitions
可以通过设置 transitions 来实现过渡效果。其用法如下：
```CSS
transition:  [ <transition-property> |
               <transition-duration> |
               <transition-timing-function> |
               <transition-delay> ]
```
<!-- more -->
{{< html >}}
<style>.wrapper{position:relative;border:1px #aaa solid;width:500px;height:500px;margin:0 auto 10px;padding:10px}.shadow{-webkit-box-shadow:5px 5px 5px #aaa;-moz-box-shadow:5px 5px 5px #aaa;box-shadow:5px 5px 5px #aaa;margin-bottom:10px}.normal,.example2,.example3{width:100px;height:100px;position:absolute;top:210px;left:210px;border-radius:50px;background-color:red;text-align:center;transition:all 1s ease-in-out}.example2{background-color:blue;transition-property:top,left;transition-duration:1s,1s;transition-delay:0s,1s}.example3{background-color:purple;transition-property:top,left,border-radius,background-color;transition-duration:2s,1s,0.5s,0.5s;transition-delay:0s,0.5s,1s,1.5s}.wrapper:hover .normal{top:0;left:0}.wrapper:hover .example2{top:398px;left:398px}.wrapper:hover .example3{top:0;left:398px;border-radius:0}.wrapper p{line-height:70px;color:white;font-weight:bold;margin-left:0 0 10px}</style><div class="wrapper shadow"><div class="normal shadow"><p>Normal</p></div><div class="example2"><p>Example2</p></div><div class="example3"><p>Example3</p></div></div>
{{< /html >}}


# transforms
可以通过 transforms 来实现变形效果。目前有2D变形和3D变形，不过3D变形只有新的浏览器中支持。其语法如下:
```CSS
transform: none|transform-functions
```
它的属性值如下表

值    | 含义
:-----|:-----
none  |  定义不进行转换。
matrix(n,n,n,n,n,n) | 定义 2D 转换，使用六个值的矩阵。
matrix3d(n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n) |  定义 3D 转换，使用 16 个值的 4x4 矩阵。
translate(x,y) | 定义 2D 转换。
translate3d(x,y,z) | 定义 3D 转换。
translateX(x)  | 定义转换，只是用 X 轴的值。
translateY(y)  | 定义转换，只是用 Y 轴的值。
translateZ(z)  | 定义 3D 转换，只是用 Z 轴的值。
scale(x,y) | 定义 2D 缩放转换。
scale3d(x,y,z) |  定义 3D 缩放转换。
scaleX(x) |  通过设置 X 轴的值来定义缩放转换。
scaleY(y)  | 通过设置 Y 轴的值来定义缩放转换。
scaleZ(z)  | 通过设置 Z 轴的值来定义 3D 缩放转换。
rotate(angle) |  定义 2D 旋转，在参数中规定角度。
rotate3d(x,y,z,angle) |  定义 3D 旋转。
rotateX(angle) | 定义沿着 X 轴的 3D 旋转。
rotateY(angle) | 定义沿着 Y 轴的 3D 旋转。
rotateZ(angle) | 定义沿着 Z 轴的 3D 旋转。
skew(x-angle,y-angle) |  定义沿着 X 和 Y 轴的 2D 倾斜转换。
skewX(angle)   | 定义沿着 X 轴的 2D 倾斜转换。
skewY(angle)   | 定义沿着 Y 轴的 2D 倾斜转换。
perspective(n) | 为 3D 转换元素定义透视视图

可以将 transform 同 transition 结合使用，如下所示：
{{< html >}}
<style>
#transDemo div {
    height: 120px;width: 120px; border: 1px blue solid; margin: 10px auto; padding:10px; text-align: center; transition: all 2s ease-in-out;
}
#transDemo .hover {cursor: pointer;}
#transDemo div:hover {transform: rotate(720deg) scale(2,2);}
</style>
<div id="transDemo">
<div class="hover" > Hover and see what will happen</div>
</div>
{{< /html >}}

## 3D transform
3D tranform 同 2D transform 相似，其基本属性为 `translate3d`, `scale3d`, `rotateX`, `rotateY`, `rotateZ`。如下列：
{{< html >}}
<style>
#transform3D div {
  transition:all 2s ease-in-out;
  perspective: 800px;
  perspective-origin: 50% 100px;
}
#transform3D:hover #rotateX {
  transform:rotateX(180deg);
}
#transform3D:hover #rotateY {
  transform:rotateY(180deg);
}
#transform3D:hover #rotateZ {
  transform:rotateZ(180deg);
}
</style>

<div id="transform3D" class="shadow hover">
    <div id="rotateX">rotate x</div>
    <div id="rotateY">rotate y</div>
    <div id="rotateZ">rotate z</div>
</div>
{{< /html >}}


还可以实现一个 3d 的图片旋转
{{< html >}}
<style>
#cubeCarousel {
    -webkit-perspective: 800;
    perspective: 800;
    perspective-origin: 50% 100px;
    margin: 100px auto 20px auto;
    width: 450px;
    height: 400px;
}
#cubeCarousel #cubeSpinner {
    position: relative;
    margin: 0 auto;
    height: 281px;
    width: 450px;
    transform-style: preserve-3d;
    transform-origin: 50% 100px 0;
    transition: all 1.0s ease-in-out;
}
#cubeCarousel .face {
    position: absolute;
    height: 220px;
    width: 450px;
    padding: 0px;
    overflow: hidden;
}
#cubeSpinner .one {
    transform: translateZ(225px);
}
#cubeSpinner .two {
    transform: rotateY(90deg) translateZ(225px);
}

#cubeSpinner .three {
    transform: rotateY(180deg) translateZ(225px);
}
#cubeSpinner .four {
    transform: rotateY(-90deg) translateZ(225px);
}
# span {
    cursor: pointer;
}
#controls .selected{
    font-weight: bold;
}
.trans {
    opacity: 0.5;
}

</style>
<div id="cubeCarousel">
    <div id="cubeSpinner" style="">
      <div class="face one">
        <img src="http://img02.sogoucdn.com/app/a/100520024/71a9c49ed8a2693c43a50288b719ea67">
      </div>
      <div class="face two">
        <img src="http://img04.sogoucdn.com/app/a/100520024/a89685511fef9f796e70e63cb02d0c0f">
      </div>
      <div class="face three">
        <img src="http://img04.sogoucdn.com/app/a/100520024/83af06dcb64a1031737577a57dba95a9">
      </div>
      <div class="face four">
        <img src="http://img03.sogoucdn.com/app/a/100520024/02091b74ed63fe2784e98292861a6a7b">
      </div>
    </div>
    <div id="controls">
        <span class="selected" id="controls_1">Image 1</span>
        <span class="" id="controls_2">Image 2</span>
        <span class="" id="controls_3">Image 3</span>
        <span class="" id="controls_4">Image 4</span>
        <span id="toggleOpacity">显示图片的旋转</span>
    </p>
  </div>
</div>
<script type="text/javascript">
    var controls = [];
    controls.push(document.getElementById("controls_1"));
    controls.push(document.getElementById("controls_2"));
    controls.push(document.getElementById("controls_3"));
    controls.push(document.getElementById("controls_4"));

    controls.forEach(function(item, index) {
        item.addEventListener("click", function() {
            controls.forEach(function(it){
               it.className = "";
            });
            this.className = "selected";
            document.getElementById("cubeSpinner").style.transform = "rotateY(" + (-1 * (index * 90)) + "deg)";
        });
    });
    document.getElementById("toggleOpacity").addEventListener("click", function() {
        if (/selected/.test(this.className)) {
            this.className = '';
        } else {
            this.className = "selected";
        }
        var images = document.getElementById("cubeSpinner").getElementsByTagName("img");
        for (var position=0; position<images.length; position++) {
            if (/trans/.test(images[position].className)) {
                images[position].className = '';
            } else {
                images[position].className = "trans";
            }
        }
    });
</script>
{{< /html >}}

## animation
在CSS中使用动画，首先要为动画指定关键帧（
元素在某些时候会有什么样式）。浏览器会为你添加相应的渐变效果。语法如下：
```CSS
animation: name duration timing-function delay iteration-count direction;
```

其中 animation 的值有如下含义

值         | 描述
:----------|:---------
animation-name | 规定需要绑定到选择器的 keyframe名称
animation-duration| 规定完成动画所花费的时间，以秒或毫秒计数
animation-timing-function | 规定动画的速度曲线
animation-delay | 规定在动画开始之前的延迟
animation-iteration-count | 规定动画应该播放的次数
animation-direction | 规定是否应该轮流反向播放动画 (normal:默认值。动画应该正常播放; alternate    动画应该轮流反向播放)

### @keyframes 规则
举例来说，将一个元素逐渐下移 200px :
```CSS
@keyframes move {
    from {top: 0px;}
    to {top: 200px;}
}
```

从例子中可以看出，`@keyframes` 指定了动画的代码。动画的效果是从一个CSS样式渐变到另外一个CSS样式。在动画执行期间，我们可以多次更改CSS样式。
样式可以设定百分比改变或者使用 "from" 和 "to"，"from" 相当于 0%，"to" 相当于 100%。
为了获得最佳的浏览器支持，应该始终定义 0% 和 100% 选择器。
@keyframes 的语法如下：
```CSS
@keyframes animation-name {keyfromes-selector {css-styles;}}
```

例如：
```CSS
@keyframes mymove {
    0%   {top: 0px;}
    25%  {top: 200px;}
    50%  {top: 100px;}
    75%  {top: 200px;}
    100% {top: 0px;}
}
```

### Demo1
{{< html >}}
<style>
@keyframes resize {
    0% {padding: 0;}
    50% {padding: 0 40px; background-color: rgba(255,0,0,0.2);}
    100% {padding: 0 150px; background-color: rgba(255,0,0, 0.9);}
}
#animationBox {
    height: 50px;
    width: 50px;
    margin: 0 auto;
    border: 1px red solid;
    background-color: rgba(255,0,0,0.7);
}
#animationBox:hover {
     animation: resize 1s infinite alternate
}
</style>
<div id="animationBox" class="shadow">
</div>
{{< /html >}}

### Demo2
animation 动画适用于微妙、精美的动画，而不是那些特别复杂的动画。 WCAG规定，使用animation不应该包含每秒闪烁超过3次的内容。

{{< html >}}
<style>
#animationDemo2 {width: 255px; margin: 10px auto;}
#animationDemo2 button {
    width: 255px;
    height: 35px;
    background: #cde;
    border: 2px solid #ccc;
    border-color: rgba(0,0,255,0.5);
    font-size: 18px;
    color: #000;
    text-shadow: rgba(20, 20, 20, 0.5) 1px 1px 5px;
    text-align: center;
    border-radius: 16px;
    box-shadow: 0 0 16px rgba(66, 140, 240, 0.5);
}
#animationDemo2 button:hover {
    background-color: #cce;
    animation: glow 1s ease-in-out infinite alternate;
}
@keyframes glow {
  0% {
    box-shadow: 0 0 16px rgba(66, 140, 240, 0.5);
    border-color: rgba(0,0,255,0.5);
  }
  100% {
    box-shadow: 0 0 16px rgba(66, 140, 240, 1.0), 0 0 36px rgba(0, 140, 255, 1.0);
    border-color: rgba(0,0,255,1.0);
  }
}
</style>
<div id="animationDemo2">
  <button class="transition hover">Hover to Pulsate</button>
</div>
{{< /html >}}

## filters
CSS中的 filter 并不是官网标准，但是有很多浏览器支持filter样式。目前，filter 应用于整个元素和所有它的后代，不能只针对背景或边框。
虽然CSS滤镜涵盖了许多可能性，但是最常用的还是调整色彩，像灰度，棕褐色，饱和度，色调旋转，反转，不透明度，亮度，对比度，模糊和阴影，如下：
```CSS
.thing_you_want_to_filter {
  /*
    these are all default values, note that hue-rotate and blur have units.
    You'll also need to include the vendor prefixes.
  */
  /* 灰度 */
  filter: grayscale(0);
   /* 棕褐色 */
  filter: sepia(0);
  /* 饱和度 */
  filter: saturate(1);
  /* 色调旋转 */
  filter: hue-rotate(0deg);
  /* 反转 */
  filter: invert(0);
  /* 不透明度 */
  filter: opacity(1);
  /* 亮度 */
  filter: brightness(1);
  /* 对比度 */
  filter: contrast(1);
  /* 模糊 */
  filter: blur(0px);

  /* Drop shadow has the same syntax as box-shadow – see below for why it's amazing! */
  filter: drop-shadow(5px 5px 10px #ccc);
}
```

{{< html >}}
<style>
    .filtered {
        position:relative;
        height:281px;
        width:450px;
        margin:0 auto 10px;
    }
    #controls {
        width: 50%;
        margin: 10px auto;
    }
 </style>
<div class="filtered shadow">
    <img src="http://css3.bradshawenterprises.com/images/Turtle.jpg">
</div>
<div id="controls">
    <h4>Play with the sliders…</h4>
    <form class="form-horizontal">
        <div class="control-group">
            <label class="control-label" for="grayscale">Grayscale</label>
            <div class="controls">
                <input type="range" data-default="0" value="0" min="0" max="1" step="0.1" id="grayscale">
                <p id="grayscale_output" class="pull-right">grayscale(0.5)</p>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="sepia">Sepia</label>
            <div class="controls">
                <input type="range" data-default="0" value="0" min="0" max="1" step="0.1" id="sepia">
                <p id="sepia_output" class="pull-right">sepia(0)</p>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="saturate">Saturate</label>
            <div class="controls">
                <input type="range" data-default="1" value="1" min="0" max="1" step="0.1" id="saturate">
                <p id="saturate_output" class="pull-right">saturate(1)</p>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="hue-rotate">Hue-rotate</label>
            <div class="controls">
                <input type="range" data-default="0" value="0" min="0" max="360" step="1" id="hue-rotate">
                <p id="hue-rotate_output" class="pull-right">hue-rotate(0deg)</p>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="invert">Invert</label>
            <div class="controls">
                <input type="range" data-default="0" value="0" min="0" max="1" step="0.1" id="invert">
                <p id="invert_output" class="pull-right">invert(0)</p>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="opacity">Opacity</label>
            <div class="controls">
                <input type="range" data-default="1" value="1" min="0" max="1" step="0.1" id="opacity">
                <p id="opacity_output" class="pull-right">opacity(1)</p>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="brightness">Brightness</label>
            <div class="controls">
                <input type="range" data-default="1" value="1" min="0" max="1" step="0.1" id="brightness">
                <p id="brightness_output" class="pull-right">brightness(1)</p>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="contrast">Contrast</label>
            <div class="controls">
                <input type="range" data-default="1" value="1" min="0" max="2" step="0.1" id="contrast">
                <p id="contrast_output" class="pull-right">contrast(1)</p>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="blur">Blur</label>
            <div class="controls">
                <input type="range" data-default="0" value="0" min="0" max="10" step="1" id="blur">
                <p id="blur_output" class="pull-right">blur(0px)</p>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="reset">&nbsp;</label>
            <div class="controls">
                <a href="#" id="reset" class="btn btn-info">Reset to defaults</a>
            </div>
        </div>
    </form>
</div>
 <script>
        $(document).ready(function() {
            var controls_input = $("#controls input"),
            image = $(".filtered img");

            function updateDisplay() {
                var newCSS = '';
                controls_input.each(function(){
                    var units = '';

                    if ($(this).attr('id') == 'blur') {
                        units = 'px';
                    } else if ($(this).attr('id') == 'hue-rotate') {
                        units = 'deg';
                    }
                    newCSS += $(this).attr('id')+"("+$(this).val()+units+") ";
                    $("#"+$(this).attr('id')+"_output").text($(this).attr('id')+"("+$(this).val()+units+")");
                })
                image.css("-webkit-filter", newCSS);
                image.css("-moz-filter", newCSS);
                image.css("-ms-filter", newCSS);
                image.css("-o-filter", newCSS);
                image.css("filter", newCSS);
            }
            updateDisplay();

            controls_input.change(updateDisplay);

            $("#reset").click(function(){
                controls_input.each(function(){
                    $(this).val($(this).attr('data-default'));
                });
                updateDisplay();
                return false;
            });
        });
        </script>
{{< /html >}}


## 示例
{{< html >}}
<style>
.shadow {
    -webkit-box-shadow: 5px 5px 5px #aaa;
    -moz-box-shadow: 5px 5px 5px #aaa;
    box-shadow: 5px 5px 5px #aaa;
    margin-bottom: 10px;
}
#cf4 {
    position: relative;
    height: 281px;
    width: 450px;
    margin: 0 auto;
}
#cf4 img {
    position: absolute;
    left: 0;
    transition: all 1s ease-in-out;
}
#cf4 img.top {
    transform: scale(0, 0);
    opacity: 0;
}

#cf4:hover img.top {
    opacity: 1;
    transform: scale(1, 1);
    transform-origin: top right;
}

#cf4:hover img.bottom {
    transform: scale(0,0);
    transform-origin: bottom left;
}
</style>
<div id="cf4" class="hover">
    <img class="top shadow" src="http://css3.bradshawenterprises.com/images/Birdman.jpg">
    <img class="bottom shadow" src="http://css3.bradshawenterprises.com/images/Rainbow%20Worm.jpg">
</div>
<style type="text/css">
    #cf5 {
        position: relative;
        height: 281px;
        width: 450px;
        margin: 10px auto;
    }
    #cf5 img{
        position: absolute;
        left: 0;
        transition: all 1s ease-in-out;
    }

    #cf5 img.top {
        transform: scale(1,0);
        filter: alpha(opacity=0);
        opacity: 0;
    }

    #cf5:hover img.top {
        transform: scale(1,1);
        opacity: 1;
    }

    #cf5:hover img.bottom {
        filter: alpha(opacity=0);
        transform: rotate(360deg) scale(0,0);
    }


</style>
<div id="cf5" class="hover">
    <img class="bottom shadow" src="http://css3.bradshawenterprises.com/images/Turtle.jpg">
    <img class="top shadow" src="http://css3.bradshawenterprises.com/images/Rainbow%20Worm.jpg">
 </div>
 {{< /html >}}

## 参考网站
[CSS3 Transitions, Transforms, Animation, Filters and more!](http://css3.bradshawenterprises.com/)
