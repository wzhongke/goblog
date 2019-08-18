---
title: 深入浅出 nodejs
date: 2018-10-25 12:00:00
---

# 模块编译
Node 对获取的 JavaScript 文件内容进行了头尾包装：
```js
(function (exports, require, module, __filename, __dirname)) {
    // 文件内容
    var math = require('math');
    exports.area = function(radius) {
        return Math.PI * radius * radius;
    }
    // 文件内容结束
})
```

有上述代码可以看出，使用 `exports=function(){}` 导出内容并不能生效，这是因为 `exports` 是形参。

**JavaScript 层面上做位运算的效率不高，因为它现将 double 型数据转换成 int 型，再计算**

