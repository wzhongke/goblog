---
title: javascript 常用技巧
date: 2017-06-17 19:42:25
tags: ["javascript"]
categories: ["javascript"]
---
javascript在使用中有各种各样的技巧：提高执行效率，降低执行频率等。

# Web Storage 的使用
Web Storage的目的是克服由cookie带来的一些限制，当数据需要被严格控制在客户端，无须将数据返回到服务器时。
<!-- more -->
- 提供一种在cookie之外的存储会话数据的途径
- 提供一种存储大量可以跨会话存在的数据机制
有两种storage存储对象：
- sessionStorage: 存储特定于某个会话的数据，数据只保持到浏览器关闭
- localStorage: 页面必须来自同一个域名（子域名无效），使用同一种协议，在同一个端口上
storage提供的方法有如下几种：
- `clear()`: 清除所有值
- `getItem(name)`: 根据名字获取对应的值
- `key(index)`: 获取`index`位置处的值
- `removeItem(name)`: 删除由`name` 指定的键值对
- `setItem(name, value)`: 为指定的`name`设置一个对应的值
获取`localStorage`的方法：
```javascript
var WebStorage = {
    getLocalStorage: function() {
        if (typeof localStorage === 'object') {
            return localStorage;
        } else if (typeof globalStorage === 'object') {
            return globalStorage;
        } else {
            throw new Error("Local Storage not available");
        }
    }
}
```

# 使用外部变量时，超时调用
在使用`setTimeout`进行超时调用时，其作用域是`window`，因此要注意其`this`的使用。
```javascript
for (var i=0; i<10; i++) {
   setTimeout( function () {
        console.log(i);
    }, 200);
}
```
上例中，会取`i`的最终值10，正确的方法如下：
```javascript
for (var i=0; i<10; i++) {
    setTimeout(function(a) {
        return function() {
            console.log(a);
        }    
    }(i), 200);
}
```

# 判断手机联网状态
如果手机不支持如下属性，可以参考 [github](https://github.com/daniellmb/downlinkMax)
```javascript
// 是否在线
navigator.onLine
// 连接类型
navigator.connection.type
// type值可能是: unknown, ethernet, wifi, 2g, 3g, 4g, none.
// 下行最大比特率 downlinkMax
navigator.connection.downlinkMax

```

# 节流函数
```js
var throttle = function (time, func){
    var timeoutId= null;
    return function() {
        clearTimeout(timeoutId);

        timeoutId = setTimeout(function(){
            func();
        }, time);
    };
};

// 使用
throttle(100, function () {
    if (!elementInViewport2(video[0])) {
        video[0].pause();
    }
})();
```

# 元素是否在可视区域
```js
function elementInViewport2(el) {
    var top = el.offsetTop;
    var left = el.offsetLeft;
    var width = el.offsetWidth;
    var height = el.offsetHeight;

    while(el.offsetParent) {
        el = el.offsetParent;
        top += el.offsetTop;
        left += el.offsetLeft;
    }

    return (
        top < (window.pageYOffset + window.innerHeight) &&
        left < (window.pageXOffset + window.innerWidth) &&
        (top + height) > window.pageYOffset &&
        (left + width) > window.pageXOffset
    );
}
```

# 图片懒加载

```js
var imgLazyLoad = $(".js-pic-lazy-load").toArray();
function processDownFresh () {
    var scrollTop = $(window).scrollTop(),
        winHeight = $(window).height();
    while (imgLazyLoad.length > 0 && scrollTop + winHeight >= $(imgLazyLoad[0]).offset().top - winHeight) {
        var load = $(imgLazyLoad.shift());
        load.removeClass('js-pic-lazy-load');
        load.find('img').forEach(function (item) {
            var img = $(item);
            img.attr("src", img.attr('data-src'));
        });
    }
}
var scrollProgress = throttle(500, processDownFresh);
$(window).on("scroll", scrollProgress);
```

# 问题
1. 对于含有小数的值，不用等于判断
2. 使用 Zepto 监控图片 load 时间时，可能会有不触发的问题，可以使用如下方式处理：
    ```javascript
    // 使用 one 保证该事件只被触发一次
    $("img").one("load", function() {
        // do something
    }).each (function(){
        this.complete && $(this).trigger("load");
    }) 
    ```