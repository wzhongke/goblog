-----
title: vue.js
draft: true
-----

# 环境搭建

```
## 安装cnpm
npm install -g cnpm --registry=https://registry.npm.taobao.org
## 安装vue
cnpm install -g vue-cli
## 初始化一个项目
vue init webpack my-project
## 下载依赖
cnpm install 
```


Vue中重要的组件部分
```
new Vue({
	data: {
		a: 1,
		b: []
	}
	methods: {
		doSomething: function() {
			this.a++;
		}
	},
	watch: {
		'a': function(val, oldVal) {
			console.log(val, oldVal);
		}
	}
})
```

```
对html内容进行转义
<p v-text="a"></p>
# 保留html格式
<p v-html="a"></p>
```