---
title: vuex
date: 2017-08-30 19:42:25
tag: ["javascript","vue"]
categories: ["javascript","vue"]
---

## Vuex 能做什么
Vuex将多个组件共享的状态从组件中抽离出来，，以一个全局单例的模式进行统一管理。
解决问题
- 多层嵌套组件间传参繁琐，且兄弟组件间的状态传递无能为力。
- 父子组件直接引用或者通过事件来变更和同步状态的多份拷贝，这种方式非常脆弱，代码不易维护
![](https://vuex.vuejs.org/images/vuex.png)
## 核心概念
## 单一状态树 -- state
单一状态树是全局唯一的变量，唯一的数据源，每个应用仅包含一个store实例
## 在 Vue 组件中获得 Vuex 状态
Vuex 通过调用 `Vue.use(Vuex)` 将store注入到子组件中，子组件可以通过`this.$store`访问到store中的内容：
```javascript
const Counter = {
    template: `<div> {{count}}</div>`,
    computed: {
        count() {
            return this.$store.state.count
        }
    }
}
const app = new Vue({
  el: '#app',
  // 把 store 对象提供给 “store” 选项，这可以把 store 的实例注入所有的子组件
  store,
  components: { Counter },
  template: `
    <div class="app">
      <counter></counter>
    </div>
  `
})
```

## getter
当我们需要从store中提取出一些状态，可以在computed中进行过滤计算。但是如果多个组件要使用该属性，那么就会导致代码冗余。可以在store中敌营getter来解决该问题。
而且getter会将计算的值缓存起来，当其依赖的值发生改变才会被重新计算。
```javascript
const store = new Vuex.Store({
  state: {
    todos: [
      { id: 1, text: '...', done: true },
      { id: 2, text: '...', done: false }
    ]
  },
  getters: {
      doneTodosCount: (state, getters) => {
          return getters.doneTodos.length
      }
  }
})
// 在组件中使用
computed: {
    doneTodoCount() {
        return this.$store.getters.doneTodosCount
    }
}
```
## 更改store变量的值 -- 同步方式mutation
更改 Vuex 的 store 中的状态的唯一方法是提交mutation：每个mutation都有一个字符串的**事件类型**和**回调函数**，该函数接受一个state作为第一个参数
```javascript
const store = new Vuex.Store({
    state: {
        count: 1
    },
    mutations: {
        increment (state) {
            state.count++  // 变更状态
        }
    }
})
// 在组件中需要用如下方式调用：
store.commit("increment")
```
如果提交的内容含有参数：
```javascript
mutations: {
  increment (state, n) {
    state.count += n
  }
}

this.$store.commit("increment", 1)
// 对象方式提交
mutations: {
  increment (state, payload) {
    state.count += payload.amount
  }
}
this.$store.commit("increment" {amount: 10})
this.$store.commit({
    type: 'increment',
    amount: 10
})
```
### mutations 遵循vue响应规则
1. 最好提前在你的 store 中初始化好所有所需属性。
2. 当需要在对象上添加新属性时，你应该
    - 使用 `Vue.set(obj, 'newProp', 123)`
    - 以新对象替换老对象 `state.obj = { ...state.obj, newProp: 123 }`
## 更改store变量的值 -- 异步方式actions
action可以包含任意异步操作。
定义action：
```javascript
const store = new Vuex.Store({
    state: {
        count: 0
    },
    mutations: {
        increment (state) {
            state.count++
        }
    },
    actions: {
        increment (context) {
            context.commit('increment')
        },
        incrementAsync ({ commit }) {
            setTimeout(() => {
                commit('increment')
            }, 1000)
        }
    }
})
```
调用actions
```javascript
store.dispatch('increment')
// 以载荷形式分发
store.dispatch('incrementAsync', {
  amount: 10
})

// 以对象形式分发
store.dispatch({
  type: 'incrementAsync',
  amount: 10
})
```
在组件中使用action，可以用`this.$store.dispatch('xxx')` 分发 action，或者使用 `mapActions` 辅助函数将组件的 `methods` 映射为 `store.dispatch` 调用:
```javascript
import { mapActions } from 'vuex'

export default {
    methods: {
        ...mapActions([
            'increment' // 映射 this.increment() 为 this.$store.dispatch('increment')
        ]),
        ...mapActions({
            add: 'increment' // 映射 this.add() 为 this.$store.dispatch('increment')
        })
    }
}
```
