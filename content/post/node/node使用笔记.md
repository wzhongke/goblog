---
title: node使用笔记
date: 2019-10-25 12:00:00
---

# `Date` 类型
`Date` 时间类型在 `JSON.stringify()` 时调用了 `Date.prototype.toJSON()` 方法。但是该方法返回的是国际时间，可以通过如下方式返回需要的内容：
```js
const moment = require('moment');
Date.prototype.toJSON = function() {
    return moment(this).format('YYYY-MM-DD HH:mm:ss');
}
```