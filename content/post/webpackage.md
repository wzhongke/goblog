---
title: webpackage 
---

文件中的缩进是2，修改idea的配置之后不生效，需要修改文件 .editorconfig

创建webpack
```
vue init webpack  
# 或者
vue init gurghet/webpack

npm install # 安装依赖
```

如果有 less 文件，需要安装
```
npm install --save-dev less-loader less
```
指定webpack入口文件：
```
module.exports = {
	entry: {
		main: './src/main.js',
		a: './src/a.js'
	}
}

```

指定输出路径
```
module.exports = {
	output: {
		path: './dist',
		filename: 'js/[name]-[chunkhash].js'
	}
}
```

安装`htmlWebpackPlugin`插件：
```javascript
module.exports = {
    // 环境的上下文
	context: ./ 
	plugins: {
		new htmlWebpackPlugin({
		    // 指定html模板
			template: index.html,
			// 指定生成文件名
			filename: 'index-[hash].html',
			// 指定script标签位置
			inject: 'head',
			// 设定参数
			title: 'webpack is awesome!'
		})
	}
}
```

在html中使用htmlWebpackPlugin的参数
```html
<title><%= htmlWebpackPlugin.options.title %></title>
```