---
title: vue 脚手架
---

# 安装
可以使用下面任一个命令安装：
```sh
npm install -g @vue/cli
yarn global add @vue/cli
```

可以使用如下命令查看版本：
```sh
node --version
```

# 创建一个项目
运行命令 `vue create project` 来创建一个新的项目，可以根据需要来选择特性。

## 视图化管理
```sh
vue ui
```

上述命令会打开一个浏览器窗口，可以使用图形化界面创建一个项目

## CLI
在 Vue CLI 项目中， `@vue/cli-service` 安装了一个名为 `vue-cli-service` 的命令。可以在 npm scripts 中已 `vue-cli-service` 访问该命令。

```json
{
  "scripts": {
    "serve": "vue-cli-service serve",
    "build": "vue-cli-service build"
  }
}
```