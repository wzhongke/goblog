---
title: webpack
date: 2018-04-13 12:00:00
tags: ["javascript"]
categories: ["javascript"]
---

# 安装
使用如下命令安装webpack到当前目录下
```
npm install webpack --save
```

如果需要使用 `webpack` 命令行，需要全局安装
```
npm install webpack-cli -g
```

如果因为权限问题，安装失败，则需要使用如下命令：
```
sudo npm install webpack-cli -g
```

# 打包文件
使用npm打包文件时，可以在 `package.json` 中如下配置：
```json
"scripts": {
    "start": "webpack --config webpack.config.js", 
    "test": "echo \"Error: no test specified\" && exit 1",
    "dev": "webpack --mode development",
    "build": "webpack --mode production"
}
```

这样我们可以使用 `npm run dev` 命令来运行 webpack，而不用再输入各种参数。
其中 `webpack.config.js` 的配置如下（具体配置在配置中说明）：
```js
const path = require('path')

module.exports = {
    entry: './src/index.js',
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'bundle.js'
    },
    module: {
        rules: []
    }
}
```

# 管理资源

## 安装css
为了从 JavaScript 模块中 `import` 一个 CSS 文件，你需要在 `module` 配置中 安装并添加 `style-loader` 和 `css-loader`
```
npm install --save-dev style-loader css-loader
```

webpack 根据正则表达式来确定应该查找哪些文件，并将其提供给指定的 loader。配置如下：
```js
 module: {
    rules: [
         test: /\.css$/,
         use: [
           'style-loader',
           'css-loader'
         ]
       }
     ]
   }
```

上述配置中，以 `.css` 结尾的全部文件，都将被提供给 `style-loader` 和 `css-loader`。
这使你可以在依赖于此样式的文件中 `import './style.css'`。现在，当该模块运行时，含有 CSS 字符串的 `<style> `标签，将被插入到 html 文件的 `<head>` 中。

## 加载图片
使用 `file-loader`，我们可以轻松地将图片或者图标等内容混合到 CSS 中：
```
npm install --save-dev file-loader
```

使用js写成如下方式，`webpack` 会将图片的md5作为其新的名称，打包到指定的目录下：
```js
import _ from 'lodash'; // Lodash, now imported by this script
import './style.css';
import MyImage from './page.png'

function component() {
    var element = document.createElement('div');

    // Lodash（目前通过一个 script 脚本引入）对于执行这一行是必需的
    element.innerHTML = _.join(['Hello', 'webpack'], ' ');
    element.classList.add('hello')

    // 将图像添加到我们现有的 div。
    var myIcon = new Image();
    myIcon.src = MyImage;

    element.appendChild(myIcon);
    return element;
}

document.body.appendChild(component());
```

## 加载字体
`file-loader` 和 `url-loader` 可以接收并加载任何文件，然后将其输出到构建目录。这就是说，我们可以将它们用于任何类型的文件，包括字体。
```js
{
    test: /\.(woff|woff2|eot|ttf|otf)$/,
    use: [
        'file-loader'
    ]
}
```

## 加载数据资源
因为 JSON 是内置的，可以直接用 `import Data from './data.json'` 来引入 JSON 数据。如果是 `xml` 或者 `csv` 格式的数据文件，则需要  `csv-loader` 和 `xml-loader` :
```
npm install --save csv-loader xml-loader
```

在 `webpack.config.js` 的 `rules` 加入如下规则：
```js
{
  test: /\.(csv|tsv)$/,
  use: [
    'csv-loader'
  ]
},
{
  test: /\.xml$/,
  use: [
    'xml-loader'
  ]
}
```

# 管理输出
到目前为止，我们在 `index.html` 文件中手动引入所有资源，然而随着应用程序增长，并且一旦开始对文件名使用哈希(hash)]并输出多个 `bundle`，手动地对 `index.html` 文件进行管理，一切就会变得困难起来。然而，可以通过一些插件，会使这个过程更容易操控。
新建文件 `src/print.js` ：
```js
export default function printMe() {
  console.log('I get called from print.js!');
}
```

并在 `index.js` 中引用该文件：
```js
import _ from 'lodash';
import printMe from './print.js';

function component() {
    var element = document.createElement('div');
    var btn = document.createElement('button');

    element.innerHTML = _.join(['Hello', 'webpack'], ' ');

    btn.innerHTML = 'Click me and check the console!';
    btn.onclick = printMe;

    element.appendChild(btn);

    return element;
}

document.body.appendChild(component());
```

更新 `dist/index.html` 引入 `print.js` 文件：
```html
<!doctype html>
  <html>
    <head>
        <title>Output Management</title>
        <script src="./print.bundle.js"></script>
    </head>
    <body>
        <script src="./app.bundle.js"></script>
    </body>
  </html>
```

然后调整 `webpack.config.js` 配置来加入新的入口文件和更改输出文件名字：
```js
entry: {
    app: './src/index.js',
    print: './src/print.js'
},
output: {
    filename: '[name].bundle.js',
    path: path.resolve(__dirname, 'dist')
}
```

运行 `npm run dev` 可以看到在 `dist` 目录下生成了生成 `print.bundle.js` 和 `app.bundle.js` 文件。

## `HtmlWebpackPlugin` 插件
`[HtmlWebpackPlugin](https://github.com/jantimon/html-webpack-plugin)` 对于每次编译都要改变hash值的文件特别有用，该插件会创建一个全新的文件，并将所有的bundle都自动添加到文件中。
使用 `npm install html-webpack-plugin --save` 安装插件
```js
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    entry: {
        app: './src/index.js',
        print: './src/print.js'
    },
    plugins: [
        new HtmlWebpackPlugin({
            title: 'Output Management'
        })
    ],
    output: {
        filename: '[name].bundle.js',
        path: path.resolve(__dirname, 'dist')
    }
};
```

## 插件
由于每次新生成的文件都放到 `dist` 目录下，会导致 `dist` 目录非常乱。推荐的做法是每次生成新文件之前，清理 `dist` 目录。
使用 `npm install clean-webpack-plugin --save` 来安装插件。
配置如下：
```js
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');

module.exports = {
    entry: {
        app: './src/index.js',
        print: './src/print.js'
    },
    plugins: [
        new CleanWebpackPlugin(['dist']),
        new HtmlWebpackPlugin({
            title: 'Output Management'
        })
    ],
    output: {
        filename: '[name].bundle.js',
        path: path.resolve(__dirname, 'dist')
    }
};
```

# 开发
## source map
使用 webpack 打包时，可能很难追踪到警告或者错误在源代码中的位置。JavaScript 提供了 source map 的功能，将编译后的代码映射到原始代码，这样可以更容易地定位问题。
我们可以通过在 `webpack.config.js` 的 `exports` 中加入如下代码来启用 source map 功能：
```js
module.exports = {
    devtool: 'inline-source-map',
    ...
}
```

## 开发工具
每次修改都运行 `npm run dev` 会非常麻烦，webpack 有几个不同的选项可以帮助我们在代码发生变化后，自动编译：
1. webpack's Watch Mode
2. webpack-dev-server
3. webpack-dev-middleware

## 观察者模式
你可以指示 webpack "watch" 依赖图中的所有文件以进行更改。如果其中一个文件被更新，代码将被重新编译，所以你不必手动运行整个构建。

我们添加一个用于启动 webpack 的观察模式的 npm script 脚本：
```json
"scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
    "watch": "webpack --mode development --watch",
    "build": "webpack --mode production "
},
```

在命令行中运行 `npm run watch`，就会看到 webpack 编译代码，然而却不会退出命令行。这是因为 script 脚本还在观察文件。如果修改了文件，那么可以看到 webpack 自动重新编译后的文件。

唯一的缺点是，为了看到修改后的实际效果，你需要刷新浏览器。

## 使用 `webpack-dev-server`
`webpack-dev-server` 提供了一个简单的 web 服务，并能够实时重新加载。使用如下命令安装：
```
npm install webpack-dev-server --save-dev
```

修改 `webpack.config.js` 文件，增加如下内容：
```js
module.exports = {
    devtool: 'inline-source-map',
    devServer: {
        contentBase: './dist'
    }
    ...
}
```

以上配置告知 `webpack-dev-server`，在 localhost:8080 下建立服务，并将 `dist` 目录下的文件，作为可访问文件。

在 `package.json` 文件中，加入如下脚本：
```json
"scripts": {
    "start": "webpack-dev-server --open --config webpack.config.js"
}
```

就可以通过命令 `npm start` 来运行开发服务器。

## `webpack-dev-middleware`
`webpack-dev-middleware` 是一个容器，它可以把 webpack 处理后的文件传递给一个服务器(server)。 `webpack-dev-server` 在内部使用了它，同时，它也可以作为一个单独的包来使用，以便进行更多自定义设置来实现更多的需求。接下来是一个 `webpack-dev-middleware` 配合 express server 的示例。

```js
const express = require('express');
const webpack = require('webpack');
const webpackDevMiddleware = require('webpack-dev-middleware');

const app = express();
const config = require('./webpack.conf.js');
const compiler = webpack(config);

// Tell express to use the webpack-dev-middleware and use the webpack.config.js
// configuration file as a base.
app.use(webpackDevMiddleware(compiler, {
    publicPath: config.output.publicPath
}));

// Serve the files on port 3000.
app.listen(3000, function () {
    console.log('Example app listening on port 3000!\n');
});
```

通过运行 `node server.js` 就可以完成自动检测文件变化并且编译文件。

# 生产环境构建
而在生产环境中，我们的目标则转向于关注更小的 bundle，更轻量的 source map，以及更优化的资源，以改善加载时间。由于要遵循逻辑分离，我们通常建议为每个环境编写彼此独立的 webpack 配置。
为了遵循不重复原则，我们将保留一个通用配置。为了将通用配置同其他配置合并在一起，使用了 `webpack-merge` 工具：
```
npm install webpack-merge --save
```

在根目录下新建三个文件：`webpack.common.js`, `webpack.prod.js`, `webpack.dev.js`
其中 `webpack.common.js` 内容如下：
```js
const path = require('path');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    entry: {
        app: './src/index.js'
    },
    plugins: [
        new CleanWebpackPlugin(['dist']),
        new HtmlWebpackPlugin({
            title: 'Production'
        })
    ],
    output: {
        filename: '[name].bundle.js',
        path: path.resolve(__dirname, 'dist')
    }
};
```

`webpack.dev.js` 文件如下：
```js
const merge = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common, {
    devtool: 'inline-source-map',
    devServer: {
        contentBase: './dist'
    }
});
```

`webpack.prod.js` 文件如下：
```js
const merge = require('webpack-merge');
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const common = require('./webpack.common.js');

module.exports = merge(common, {
    devtool: 'source-map',
    plugins: [
        new UglifyJSPlugin({
            sourceMap: true,
        })
    ]
});
```

**避免在生产中使用 inline-\*\*\* 和 eval-\*\*\*，因为它们可以增加 bundle 大小，并降低整体性能**

修改 `package.json` 文件，将脚本定义为新的配置：
```json
"scripts": {
    "start": "webpack-dev-server --open --config webpack.dev.js",
    "build": "webpack --mode production  --config webpack.prod.js"
},
```

## 指定环境
许多 library 将通过与 `process.env.NODE_ENV` 环境变量关联，以决定 library 中应该引用哪些内容。例如，当不处于生产环境中时，某些 library 为了使调试变得容易，可能会添加额外的日志记录(log)和测试(test)。其实，当使用 `process.env.NODE_ENV === 'production'` 时，一些 library 可能针对具体用户的环境进行代码优化，从而删除或添加一些重要代码。我们可以使用 webpack 内置的 DefinePlugin 为所有的依赖定义这个变量：

```js
/* webpack.prod.js */
const webpack = require('webpack');
const merge = require('webpack-merge');
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const common = require('./webpack.common.js');

module.exports = merge(common, {
    devtool: 'source-map',
    plugins: [
        new UglifyJSPlugin({
            sourceMap: true
        }),
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify('production')
        })
    ]
})
```

# 代码分离
有三种常用的代码分离方法：
- 入口起点：使用 entry 配置手动地分离代码。
- 防止重复：使用 CommonsChunkPlugin 去重和分离 chunk。
- 动态导入：通过模块的内联函数调用来分离代码。

入口起点分离最简单直观：
```js
/* webpack.config.js */
const path = require('path');
const HTMLWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    entry: {
        index: './src/index.js',
        another: './src/another-module.js'
    },
    plugins: [
        new HTMLWebpackPlugin({
            title: 'Code Splitting'
        })
    ],
    output: {
        filename: '[name].bundle.js',
        path: path.resolve(__dirname, 'dist')
    }
};
```

上述配置会生成两个文件：`index.bundle.js` 和 `another.bundle.js`。但是这种方式存在两个问题：
- 如果入口 chunks 之间包含重复的模块，那些重复模块都会被引入到各个 bundle 中
- 不能将核心应用程序逻辑进行动态拆分代码。

## 防止重复
`CommonsChunkPlugin` 插件可以将公共的依赖模块提取到已有的入口 `chunk` 中，或者提取到一个新生成的 `chunk`。
修改 `webpack.config.js` 文件：
```js
plugins: [
    new HTMLWebpackPlugin({
        title: 'Code Splitting'
    }),
    new webpack.optimize.CommonsChunkPlugin({
        name: 'common' // 指定公共 bundle 的名称。
    })
],
```

上述配置在执行后，会将 两个入口文件的公共模块提取出来并生成一个新的 `common.bundle.js` 作为公共模块。

## 懒加载
懒加载或者按需加载，是一种很好的优化网页或应用的方式。这种方式实际上是先把你的代码在一些逻辑断点处分离开，然后在一些代码块中完成某些操作后，立即引用或即将引用另外一些新的代码块。这样加快了应用的初始加载速度，减轻了它的总体体积，因为某些代码块可能永远不会被加载。

对 `print.js` 来说，只有当用户点击按钮时，才会用到该文件，我们可以将其抽离出来，作为懒加载的模块。
```js
/*index.js*/
btn.onclick = e => import(/* webpackChunkName: "print" */ './print').then(module => {
    var print = module.default;
    print();
});
```

# 缓存
浏览器使用缓存技术以降低网络流量，使网站加载速度更快。如果我们在部署新版本时不更改资源的文件名，浏览器可能会认为它没有被更新，就会使用它的缓存版本。由于缓存的存在，当你需要获取新的代码时，就会显得很棘手。

## 输出文件替换
通过使用 `output.filename` 进行文件名替换，可以确保浏览器获取到修改后的文件。`[hash]` 替换可以用于在文件名中包含一个构建相关(build-specific)的 hash，但是更好的方式是使用 `[chunkhash]` 替换，在文件名中包含一个 chunk 相关(chunk-specific)的哈希。
```js
/* webpack.config.js */
output: {
    /* filename: 'bundle.js', */
    filename: '[name].[chunkhash].js',
    path: path.resolve(__dirname, 'dist')
}
```

修改 `webpack.config.js` 后，可以看到生成的文件名中含有文件的 hash 值。