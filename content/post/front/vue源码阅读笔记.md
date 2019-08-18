
# 目录结构
```
├── dist
├── examples
├── flow
├── packages
├── scripts
├── src
```

# flow
flow 是Facebook的JavaScript静态类型检查工具。vue用flow作为静态代码检查。

# src
vue 的源码都在 src 目录下：
```
src
├── compiler        # 编译相关 
├── core            # 核心代码 
├── platforms       # 不同平台的支持
├── server          # 服务端渲染
├── sfc             # .vue 文件解析
├── shared          # 共享代码
```

```js
Object.keys(obj) // 返回一个由一个给定对象的自身可枚举属性组成的数组
for (let key in obj) // 同 Object.keys(obj) 返回的数组中属性的顺序相同
```

## rollup
rollup 是 JavaScript 模块打包器，可以将小块代码编译成大块复杂的代码，例如 library 或应用程序。Rollup 对代码模块使用新的标准化格式，这些标准都包含在 JavaScript 的 ES6 版本中，而不是以前的特殊解决方案，如 CommonJS 和 AMD。ES6 模块可以使你自由、无缝地使用你最喜爱的 library 中那些最有用独立函数，而你的项目不必携带其他未使用的代码。

## `window.performance`
Web Performance API允许网页访问某些函数来测量网页和Web应用程序的性能，包括 Navigation Timing API和高分辨率时间数据。
通过设置 `Vue.config.performance = true;` 可以打开 Vue 的性能日志。在浏览器中通过 `window.performance.getEntries()` 查看

## 数组扁平化

```js
let children = ['a', ['b', 1]];
Array.prototype.concat.apply([], children)
// ["a", "b", 1]
```