---
title : vue 学习笔记
date: 2017-01-21 12:00:00
tags: ["javascript"]
categories: ["javascript"]
---

## 属性和方法
每个vue实例都会代理其data对象的所有属性
```vue
var data = { a: 1 }
var vm = new Vue({
  data: data
})
vm.a === data.a // -> true
// setting the property also affects original data
vm.a = 2
data.a // -> 2
// ... and vice-versa
data.a = 3
vm.a // -> 3
```
在vue中只有被代理的属性才会被监控，如果在创建Vue后，再data中新添加属性，是不会被监控的，也就是更新值没有任何响应。可以通过$watch方法加入响应 。
Vue中自带一些以`$`开头的属性和方法：
```vue
var data = { a: 1 }
var vm = new Vue({
  el: '#example',
  data: data
})
vm.$data === data // -> true
vm.$el === document.getElementById('example') // -> true
// $watch is an instance method
vm.$watch('a', function (newVal, oldVal) {
  // this callback will be called when `vm.a` changes
})
```

## 实例的生命周期
下面的图是vue实例的整个生命周期。
![](http://vuejs.org/images/lifecycle.png)
在vue实例创建的过程中，有一些hook会被调用。我们可以通过修改这些hook来执行自定义的操作。 hook有`created`, `mounted`, `updated`, `destroyed`.
```vue
var vm = new Vue({
  data: {
    a: 1
  },
  created: function () {
    // `this` points to the vm instance
    console.log('a is: ' + this.a)
  }
})
// -> "a is: 1"
```
## 模板语法
所有的vue模板都是有效的HTML代码。Vue通过模板引擎将Vue的模板编译成虚拟DOM渲染函数。结合响应系统，Vue能够智能地找出重新渲染的最小代价，并将其重新渲染到DOM上。
如果喜欢使用原生的JavaScript，可以使用JSX语法直接写渲染函数。

## 插值
### 文本
数据绑定最常见的形式就是使用两个大括号（Mustache）括起来的文本形式
```
<span>Message: {{msg}} </span>
```
括号中的msg将会被数据对象的`msg`属性替换掉。绑定的数据对象上`msg`属性发生了改变，插值处的内容都会更新。
使用`v-once`指令可以只执行一次插值，后续数据对象的`msg`属性发生变化，插值处的内容也不会更改
```vue
<span v-once>This will never change: {{msg}}</span>
```
### 纯HTML
使用Mustache语法的文本数据将会被解释为纯文本，如果想要输出HTML，需要使用`v-html`指令：
```
<div v-html="rawHtml"></div>
```
使用该方式插入的HTML数据绑定会被忽略。注意，你不能使用 v-html 来复合局部模板，因为 Vue 不是基于字符串的模板引擎。组件更适合担任 UI 重用与复合的基本单元。
>动态渲染的任意 HTML 会非常危险，因为它很容易导致 XSS 攻击。请只对可信内容使用 HTML 插值，绝不要对用户提供的内容插值。
### 属性
在HTML的属性上使用`v-bind`指令，而不是Mustache
```
<div v-bind:id="dynamicId"></div>
```
如果绑定的数据类型是boolean类型，如果值为false，该属性将会被移除
### 使用JavaScript表达式
可以在Mustache中使用**JavaScript表达式**
```
{{number+1}}
{{ok ? 'yes':'no}}
{{msg.split('').reverse().join('')}}
<div v-bind:id="'list-'+id"></div>
```
>模板表达式都被放在沙盒中，只能访问全局变量的一个白名单，如`Math`和`Date`。自定义的全局变量不能够被访问到。
### 指令
指令是以`vr-`开头的属性，指令的属性值应该会是一个单独的JavaScript表达式。指令的功能是在其表达式的值改变时将其对应的DOM进行重新渲染
```
<p v-if="seen">Now you see me</p>
```
### 参数 arguments
有些指令能接收一个“参数”，在指令后以冒号指明。例如，`v-bind`指令被用来响应地更新 HTML 属性：
```
<a v-bind:href="url"></a>  缩写 <a :href="url"></a>
<a v-on:click="doSomething"></a> 缩写 <a @click="doSomething"></a>
```
在这里href是参数，将该元素的href属性与url的值进行绑定
### 修饰符
修饰符（Modifiers）是以半角句号 . 指明的特殊后缀，用于指出一个指令应该以特殊方式绑定。例如，.prevent 修饰符告诉 v-on 指令对于触发的事件调用 event.preventDefault()
```
<form v-on:submit.prevent="onSubmit"></form>
```
### 过滤器
过滤器可以用在Mustache插值和`v-bind`表达式中，由管道符号 `|`指定。过滤器的设计目的就是为了用于文本转换，如果需要使用复杂的数据变换，应该是使用计算属性。
因为过滤器是JavaScript函数，因此可以向其传递参数。
```
{{msg | capitalize | filterA | filterB('arg1', 'arg2' }}
<div v-bind:id="rawId | formatId"></div>

new Vue({
  // ...
  filters: {
    capitalize: function (value) {
      if (!value) return ''
      value = value.toString()
      return value.charAt(0).toUpperCase() + value.slice(1)
    }
  }
})
```

## 计算属性和监控
### 计算属性

在模板内使用表达式是非常方斌啊的，但是他们只能用来做简单的计算。在模板中放入太多的逻辑会让模板过重且难以维护。例如：
```javascript
<div id="example">
    {{message.split('').reverse().join('')}}
</div>
```
这种情况下模板不再是直观简洁的。当你想要在模板中多次反向显示`message`的时候，问题会变得更糟糕。
因此，对于复杂逻辑，建议使用计算属性。
### 示例
```html
<div id="example">
    <p>Original message: "{{ message }}"</p>
    <p>Computed reversed message: "{{ reversedMessage }}"</p>
</div>
```
```javascript
var vm = new Vue({
   el: "#example",
    data: {
        message: "hello"
    },
    computed: {
        reversedMessage: function () {
            // this 指向vm实例
            return this.message.split('').reverse().join('');
        }
    }
});
```
在上面的例子中，我们声明了一个计算属性`reversedMessage`，函数`vm.reversedMessage`作为属性`reversedMessage`的getter方法。
`vm.reversedMessage`的值依赖于`message`的值，当`message`发生变化时，`vm.reversedMessage`的绑定也会更新。
### 计算属性 vs 方法
可以通过表达式调用方法来实现同样的效果。而**计算属性是基于他们的依赖进行缓存的**，计算属性只有在他依赖的发生改变时，才会重新求值。多次访问`reversedMessage`计算属性会立即返回之前计算的结果，而不会再重新计算。
缓存是为了避免重复计算，尤其是性能开销比较大的计算。
## 计算属性 vs Watched属性
Vue 确实提供了一种更通用的方式来观察和响应 Vue 实例上的数据变动：watch 属性。当有些数据需要基于其他数据改变而改变时，可能会滥用`watch`。然而，使用计算属性比使用`watch`更好。如下例：
```
<div id="demo">{{ fullName }}</div>
```
```javascript
var vm1 = new Vue({
   el: '#demo',
    data: {
        firstName: 'Foo',
        lastName: 'Bar',
        fullName: 'Foo Bar'
    },
    watch: {
        firstName: function (val) {
            this.fullName = val + ' ' + this.lastName
        },
        lastName: function (val) {
            this.fullName = this.firstName + ' ' + val
        }
    }
});
```
上面的代码是命令式，重复的，与计算属性相比较：
```javascript
var vm = new Vue({
    el: '#demo',
    data: {
        firstName: 'Foo',
        lastName: 'Bar'
    },
    computed: {
        fullName: function () {
            return this.firstName + ' ' + this.lastName
        }
    }
})
```
### 计算属性的Setter
计算属性默认只有getter方法，但是也可以根据需要定义setter
```JavaScript
computed: {
    fullName: {
        get: function () {
            return this.firstName + ' ' + this.lastName
        },
        set: function (newValue) {
            var names = newValue.split(' ');
            this.firstName = names[0];
            this.lastName = names[names.length - 1]
        }
    }
}
```
当运行`vm.fullName = 'John Doe'`时，set方法会被调用，vm.firstName和vm.lastName的值也会更新。
## Watchers
在大多数情况下，计算属性都比watch更合适。但是在执行异步操作或者开销较大时，watch更合适。
```html
<div id="watch-example">
  <p>
    Ask a yes/no question:
    <input v-model="question">
  </p>
  <p>{{ answer }}</p>
</div>
```
```
<!-- Since there is already a rich ecosystem of ajax libraries    -->
<!-- and collections of general-purpose utility methods, Vue core -->
<!-- is able to remain small by not reinventing them. This also   -->
<!-- gives you the freedom to just use what you're familiar with. -->
<script src="https://unpkg.com/axios@0.12.0/dist/axios.min.js"></script>
<script src="https://unpkg.com/lodash@4.13.1/lodash.min.js"></script>
<script>
var watchExampleVM = new Vue({
  el: '#watch-example',
  data: {
    question: '',
    answer: 'I cannot give you an answer until you ask a question!'
  },
  watch: {
    // 如果 question 发生改变，这个函数就会运行
    question: function (newQuestion) {
      this.answer = 'Waiting for you to stop typing...'
      this.getAnswer()
    }
  },
  methods: {
    // _.debounce 是一个通过 lodash 限制操作频率的函数。
    // 在这个例子中，我们希望限制访问yesno.wtf/api的频率
    // ajax请求直到用户输入完毕才会发出
    // 学习更多关于 _.debounce function (and its cousin
    // _.throttle), 参考: https://lodash.com/docs#debounce
    getAnswer: _.debounce(
      function () {
        var vm = this
        if (this.question.indexOf('?') === -1) {
          vm.answer = 'Questions usually contain a question mark. ;-)'
          return
        }
        vm.answer = 'Thinking...'
        axios.get('https://yesno.wtf/api')
          .then(function (response) {
            vm.answer = _.capitalize(response.data.answer)
          })
          .catch(function (error) {
            vm.answer = 'Error! Could not reach the API. ' + error
          })
      },
      // 这是我们为用户停止输入等待的毫秒数
      500
    )
  }
})
</script>
```

# Class与Style绑定
因为元素的class和style都是属性，所以可以使用`v-bind`处理他们。但是由于字符串拼接容易出错，因此vue对此专门进行了加强。表达式的结果类型除了字符串外，还可以是对象或数组。
## 绑定class
### 对象语法
可以通过传给`v-bind:class`一个对象，动态地切换class。也可以在对象中传入更多属性用来动态切换多个class，`v-bind:class`指令可以与普通的class属性共存。。
```
<div class="static" v-bind:class="{ active:isActive, 'text-danger': hasError}></div>
```
上面的语法表示 class `active` 的更新将取决于数据属性 `isActive` 是否为真值 。
也可以绑定一个对象，也可以绑定返回对象的计算属性：
```
<div v-bind:class="classObject"></div>
<script>
data: {
    isActive: true,
    error: null
},
computed: {
    classObject: function () {
        return {
            active: this.isActive && !this.error,
            'text-danger': this.error && this.error.type === 'fatal',
        }
    }
}
</script>
```

### 数组语法
将一个数据传递给`v-bind:class`，以应用一个class列表
```
<div v-bind:class="[activeClass, errorClass]">
data: {
    activeClass: 'active',
    errorClass: 'text-danger'
}
```
渲染为：
```
<div class="active text-danger"></div>
```
如果要根据条件切换列表中的class，可以用三元表达式：
```
<div v-bind:class="[isActive?activeClass:'', errorClass]">
```
`isActive`为true时，才添加`activeClass`
当有多个条件时，这样写比较繁琐。可以在数组语法中使用对象语法。

### 用在组件上
在一个定制的组件上用到`class`属性的时候，这些类将被添加到根元素上，这个元素上已经存在的类不会被覆盖。
```
Vue.component('myComponent', {
    template: '<p class="foo bar">Hi</p>'
})
```
然后使用该组件渲染时，添加一下class：
```
<myComponent class="baz boo"></myComponent>
```
HTML最终将会被渲染为
```
<p class="foo bar baz boo">Hi</p>
```
自定义组件，同样可以绑定HTML class：
```
<myComponent class="baz boo" v-bind:class="{active:isActive}"></myComponent>
```
## 绑定内联样式
### 对象语法
`v-bind:style`的对象语法非常像CSS，CSS属性名可以用驼峰式（camelCase）或者短横分隔符命名(kabab-case):
```
<div v-bind:style="{color:activeColor, fontSize:fontSize+'px'}"></div>
```
也可以直接绑定一个对象
```
<div v-bind:style="styleObject"></div>
<script>
data:{
    styleObject:{
       color: 'red',
       fontSize: '13px'
    }
}
```
### 自动添加前缀
当`v-bind:style`使用的属性需要添加特定前缀时，如`transform`，Vue会自动添加

# 条件渲染
## v-if
可以使用`v-if`来判断是否展示某个元素，当然也可以组合`v-else`，`v-else`元素必须紧跟在`v-if`或者`v-else-if`后边
```
div v-if="type === 'A'">A</div>
<div v-else-if="type === 'B'">B</div>
<div v-else>Not A/B</div>
```
## 在`<template>`中使用`v-if`条件组
如果想要切换多个元素，可以使用`<v-template>`元素来包装元素，并在上边使用`v-if`，最终渲染的HTML并不会包含`<template>`
```
<template v-if="ok">
   <h1>Title</h1>
   <p>Paragraph 1</p>
</tempalte>
```
## 用`key`管理可复用的元素
Vue会尽可能高效地渲染元素，通常会复用已有元素而不是从头开始渲染。例如，允许用户在不同的登录方式之间切换
```
<template v-if="loginType === 'username'">
  <label>Username</label>
  <input placeholder="Enter your username">
</template>
<template v-else>
  <label>Email</label>
  <input placeholder="Enter your email address">
</template>
```
在上面两个切换时，不会清除用户已经输入的内容，两个模板使用了相同的元素，`<input>`不会被替换，只是替换了它的`placehoder`.
如果这样不符合实际需求，可以使用`key`属性来表明元素不要复用
```
<template v-if="loginType === 'username'">
  <label>Username</label>
  <input placeholder="Enter your username" key="username-input">
</template>
<template v-else>
  <label>Email</label>
  <input placeholder="Enter your email address" key="email-input">
</template>
```
## `v-show`
`v-show`也会根据条件展示元素：
```
<h1 v-show="ok">Hello!</h1>
```
不同的是，`v-show`的元素会始终保留在渲染的DOM中。该指令只是简单地切换元素的CSS属性`display`，它不支持`<template>`语法
## `v-if` vs `v-show`
`v-if` 是“真正的”条件渲染，因为它会确保在切换过程中条件块内的事件监听器和子组件适当地被销毁和重建。
`v-if`也是惰性的：如果在初始渲染时条件为假，则什么也不做——直到条件第一次变为真时，才会开始渲染条件块。
相比之下，`v-show`就简单得多——不管初始条件是什么，元素总是会被渲染，并且只是简单地基于 CSS 进行切换。
一般来说，`v-if`有更高的切换开销，而`v-show`有更高的初始渲染开销。因此，如果需要非常频繁地切换，则使用`v-show`较好；如果在运行时条件不太可能改变，则使用`v-if`较好。
# 列表渲染
## `v-for`
用`v-for`指令根据一组数组的选项列表进行渲染，`v-for`指令需要以`item in items`形式的特殊语法，`items`是源数据数组并且`item`是数组元素迭代的别名。
## 基本用法
在`v-for`块中，对副作用域属性具有完全访问权限，`v-for`还支持一个可选的第二个参数作为索引。
可以用`of` 代替`in`.
```
<ul id="list_1">
    <li v-for="(item,index) in/of items">
       {{ index }}. {{ item.message }}
    </li>
</ul>
```
其中数据为：
```
var list_1 = new Vue({
    el: '#list_1',
    data: {
        items: [
            {message: 'Foo' },
            {message: 'Bar' }
        ]
    }
});
```
## Template v-for
可以在<template>标签中使用`v-for`来渲染多个模块。
```
<ul>
    <template v-for="item in items">
        <li>{{item.message}}</li>
        <li>other info</li>
    </template>
</ul>
```
## 对象迭代`v-for`
可以用`v-for`来迭代一个对象的属性。
```
<ul id="repeat-object" class="demo">
<div v-for="(value, key, index) in object">
  {{ index }}. {{ key }} : {{ value }}
</div>
</ul>
```
```
var object = new Vue({
  el: '#repeat-object',
  data: {
    object: {
      FirstName: 'John',
      LastName: 'Doe',
      Age: 30
    }
  }
})
```
>注意：使用`v-for`迭代数据或者对象属性时，修改属性时，可以这样： `list_1.items.push({message:'data'});`类型要与items中的类型一致。
## 整数迭代`v-for`
```
<div>
  <span v-for="n in 10">{{ n }}</span>
</div>
```
上面的例子是从1开始的。
## 组件和`v-for`
在自定义组件里，可以像使用任何普通元素一样使用`v-for`。组件中使用`v-for`不能自动将数据传递到数组里，因为组件有自己的作用域。为了传递数据到组件，需要用到`props`:
```
<myComponent v-for="item in items" :key="item.id"></myComponent>
<myComponent
  v-for="(item, index) in items"
  v-bind:item="item"
  v-bind:index="index"
  v-bind:key="item.id">
</myComponent>
```
下边是一个完整的例子：
```
<div id="todo-list-example">
  <input
    v-model="newTodoText"
    v-on:keyup.enter="addNewTodo"
    placeholder="Add a todo"
  >
  <ul>
    <li
      is="todo-item"
      v-for="(todo, index) in todos"
      v-bind:title="todo"
      v-on:remove="todos.splice(index, 1)"
    ></li>
  </ul>
</div>
<script>
Vue.component('todo-item', {
  template: `
    <li>
      {{ title }}
      <button v-on:click="$emit('remove')">X</button>
    </li>
  `,
  props: ['title']
});
new Vue({
  el: '#todo-list-example',
  data: {
    newTodoText: '',
    todos: [
      'Do the dishes',
      'Take out the trash',
      'Mow the lawn'
    ]
  },
  methods: {
    addNewTodo: function () {
      this.todos.push(this.newTodoText)
      this.newTodoText = ''
    }
  }
});
</script>
```
## `v-for`与`v-if`一同使用
当这两个指令在同一个节点时，`v-for`的优先级比`v-if`高。在每个`v-for`循环中，`v-if`都会运行。
```
<li v-for="todo in todos" v-if="!todo.isComplete">{{todo}}</li>
```
上边的代码只展示了isComplete为false的值。
如果想根据条件判断是否执行循环，可以用`<template>`包裹元素`v-if`:
```
<ul v-if="shouldRenderTodos">
  <li v-for="todo in todos">
    {{ todo }}
  </li>
</ul>
```
## key
`v-for`在更新已渲染过的元素列表时，它默认用“就地复用”的策略。如果数据项的顺序被改变，Vue不移动DOM元素来匹配数据顺序，而是简单复用每个元素，并确保它在特定索引下显示已经被渲染过的每个元素。
这个默认的模式是有效的，但是只适用于不依赖子组件状态或临时 DOM 状态（例如：表单输入值）的列表渲染输出。
为了能够跟踪每个节点，从而能重用和重排序现有元素，需要为每项提供一个唯一`key`属性。每个`key`值都有唯一id
```
<div v-for="item in items" :key="item.id">
  <!-- 内容 -->
</div>
```
## 数组更新检测
### 变异方法
Vue包含一组观察数组的变异方法，他们也会触发视图更新：
- `push()`
- `pop()`
- `shift()`
- `unshift()`
- `splice()`
- `sort()`
- `reverse()`
### 重塑数组
如果不是变异方法，那么不会触发视图更新，可以用新数组替换旧数组。Vue采用了一些智能启发式方式来最大化DOM元素重用，所以替换是一个相对高效的方式：
```
example1.items = example1.items.filter(function (item) {
  return item.message.match(/Foo/)
})
```
### 注意事项
由于 JavaScript 的限制， Vue 不能检测以下变动的数组：
1. 利用索引直接设置一个项时，例如：`vm.items[indexOfItem] = newValue`
2. 修改数组的长度时，例如：`vm.items.length = newLength`
对于第一类问题，可以用一下方法触发更新：
```
// Vue.set
Vue.set(example1.items, indexOfItem, newValue)
// Array.prototype.splice 变异方法
example1.items.splice(indexOfItem, 1, newValue)
```
对于第二类问题，可以使用`splice`
```
example1.items.splice(newLength)
```

## 显示过滤/排序结果
若果要显示一个数组的过滤或排序副本，而不实际改变或重置原始数据。在这种情况下，可以创建返回过滤或排序数组的计算属性。
```
<li v-for="n in evenNumbers">{{ n }}</li>
<script>
data: {
  numbers: [ 1, 2, 3, 4, 5 ]
},
computed: {
  evenNumbers: function () {
    return this.numbers.filter(function (number) {
      return number % 2 === 0
    })
  }
}
</script>
```
若计算属性不使用，例如： 在嵌套`v-for`循环中，可以使用method方法
```
<li v-for="n in even(numbers)">{{ n }}</li>
<script>
data: {
  numbers: [ 1, 2, 3, 4, 5 ]
},
methods: {
  even: function (numbers) {
    return numbers.filter(function (number) {
      return number % 2 === 0
    })
  }
}
</script>
```

# 事件处理器
## 监听事件
可以用`v-on`指令监听DOM事件来触发事件：
```
<div id="example-1">
  <button v-on:click="counter += 1">增加 1</button>
  <p>这个按钮被点击了 {{ counter }} 次。</p>
</div>
<script>
var example1 = new Vue({
  el: '#example-1',
  data: {
    counter: 0
  }
})
</script>
```
## 方法事件处理器
对于复杂的事件处理逻辑，可以使用`v-on`来接收一个方法调用。
```
<div id="example-2">
  <!-- `greet` 是在下面定义的方法名 -->
  <button v-on:click="greet">Greet</button>
</div>
<script>
var example2 = new Vue({
  el: '#example-2',
  data: {
    name: 'Vue.js'
  },
  // 在 `methods` 对象中定义方法
  methods: {
    greet: function (event) {
      // `this` 在方法里指当前 Vue 实例
      alert('Hello ' + this.name + '!')
      // `event` 是原生 DOM 事件
      if (event) {
        alert(event.target.tagName)
      }
    }
  }
})
</script>
```
## 内联处理器方法
除了直接绑定到一个方法，也可以用内联的JavaScript语句，如果庶在内联语句中范文原生DOM事件event，可以将特殊变量`$event`传入方法中：
```
<div id="example-3">
  <button v-on:click="say('hi')">Say hi</button>
  <button v-on:click="say('what', $event)">Say what</button>
</div>
<script>
new Vue({
  el: '#example-3',
  methods: {
    say: function (message, event) {
      if (event) event.preventDefault();
      alert(message);
    }
  }
})
</script>
```
## 事件修饰符
可以通过`v-on`的事件修饰符来调用`event.preventDefault()` 或 `event.stopPropagation()`:
- `.stop`
- `.prevent`
- `.capture`
- `.self`
- `.once`
```
<!-- 阻止单击事件冒泡 -->
<a v-on:click.stop="doThis"></a>
<!-- 提交事件不再重载页面 -->
<form v-on:submit.prevent="onSubmit"></form>
<!-- 修饰符可以串联  -->
<a v-on:click.stop.prevent="doThat"></a>
<!-- 只有修饰符 -->
<form v-on:submit.prevent></form>
<!-- 添加事件侦听器时使用事件捕获模式 -->
<div v-on:click.capture="doThis">...</div>
<!-- 只当事件在该元素本身（而不是子元素）触发时触发回调 -->
<div v-on:click.self="doThat">...</div>
<!-- 点击事件将只会触发一次（2.1.4新增） -->
<a v-on:click.once="doThis"></a>
```
**使用修饰符时，顺序很重要；相应的代码会以同样的顺序产生。用 @click.prevent.self 会阻止所有的点击，而 @click.self.prevent 只会阻止元素上的点击。**
## 键值修饰符
在监听键盘事件时，经常需要监测常见的键值。Vue中可以用`v-on`来监听键盘事件.
```
<!-- 只有在 keyCode 是 13 时调用 vm.submit() -->
<input v-on:keyup.13="submit">
<!-- 同上 -->
<input v-on:keyup.enter="submit">
<!-- 缩写语法 -->
<input @keyup.enter="submit">
```
全部的按键别名：
- `.enter`
- `.tab`
- `.delete`
- `.esc`
- `.space`
- `.up`
- `.down`
- `.left`
- `.right`
除此之外，还可以用`config.keyCodes	`对象[自定义键值修饰符别名]("https://cn.vuejs.org/v2/api/#keyCodes")
```
// 可以使用 v-on:keyup.f1
Vue.config.keyCodes.f1 = 112
```
## 修饰健
在2.1.0中，新增了鼠标和辅助健的监听事件
- `.ctrl`
- `.alt`
- `.shift`
- `.meta`
```
<!-- Alt + C -->
<input @keyup.alt.67="clear">
<!-- Ctrl + Click -->
<div @click.ctrl="doSomething">Do something</div>
```
修饰键比正常的按键不同；修饰键和 keyup 事件一起用时，事件引发时必须按下正常的按键。换一种说法：如果要引发 keyup.ctrl，必须按下 ctrl 时释放其他的按键；单单释放 ctrl 不会引发事件。
## 鼠标监听
在2.1.0中，可以监听鼠标的按键
- `.left`
- `.right`
- `.middle`
## 在HTML中监听事件的好处
你可能注意到这种事件监听的方式违背了关注点分离（separation of concern）传统理念。不必担心，因为所有的 Vue.js 事件处理方法和表达式都严格绑定在当前视图的 ViewModel 上，它不会导致任何维护上的困难。实际上，使用 v-on 有几个好处：
1. 扫一眼 HTML 模板便能轻松定位在 JavaScript 代码里对应的方法。
2. 因为你无须在 JavaScript 里手动绑定事件，你的 ViewModel 代码可以是非常纯粹的逻辑，和 DOM 完全解耦，更易于测试。
3. 当一个 ViewModel 被销毁时，所有的事件处理器都会自动被删除。你无须担心如何自己清理它们。
# 表单控件绑定
## 基础用法
`v-model`指令可以在表单控件元素上创建双向数据绑定。它会根据控件类型自动选取正确的方法来更新元素。它是负责监听用户的输入事件以更新数据，并特别处理一些极端的例子的语法糖。
**对于要求 IME （如中文、 日语、 韩语等） 的语言，你会发现那v-model不会在 ime 构成中得到更新。如果你也想实现更新，请使用 input事件。**
### 文本
```
<input v-model="message" placeholder="edit me">
<p>Message is: {{ message }}</p>
```
### 多行文本
```
<span>Multiline message is:</span>
<p style="white-space: pre">{{ message }}</p>
<br>
<textarea v-model="message" placeholder="add multiple lines"></textarea>
```
**在文本区域插值( <textarea></textarea> ) 并不会生效，应用 v-model 来代替**
### 复选框
单个勾选框，逻辑值
```
<input type="checkbox" id="checkbox" v-model="checked">
<label for="checkbox">{{ checked }}</label>
```
多个勾选框，绑定到同一个数组
```
<input type="checkbox" id="jack" value="Jack" v-model="checkedNames">
<label for="jack">Jack</label>
<input type="checkbox" id="john" value="John" v-model="checkedNames">
<label for="john">John</label>
<input type="checkbox" id="mike" value="Mike" v-model="checkedNames">
<label for="mike">Mike</label>
<br>
<span>Checked names: {{ checkedNames }}</span>
<script>
new Vue({
  el: '...',
  data: {
    checkedNames: []
  }
})
</script>
```
**[其他表单情况]("https://cn.vuejs.org/v2/guide/forms.html")**
## 组件
组件（Component）是Vue.js最强大的功能之一。组件可以扩展HTML元素，封装可重用的代码。组件是自定义的元素，Vue.js编译器为它添加特殊功能。在有些情况下，组件也可以是原生HTML元素的形式，以is特性扩展。
## 使用组件
### 全局注册
要注册一个全局组件，可以使用`Vue.component(tagName, options)`。
```
Vue.component('my-component', {
  // 选项
})
```
组件在注册后，便可以在父实例的模块中自定义元素。**要保证在初始化根实例之前注册了组件**
```
<div id="example">
  <my-component></my-component>
</div>
<script>
// 注册
Vue.component('my-component', {
  template: '<div>A custom component!</div>'
})
// 创建根实例
new Vue({
  el: '#example'
})
</script>
```
### 局部注册
没有必要所有的组件都注册在全局中，可以通过使用组件实例选项注册，可以使组件仅在另一个实例/组件的作用域中可用。这种封装也适用于其它可注册的 Vue 功能，如指令。
```
var Child = {
  template: '<div>A custom component!</div>'
}
new Vue({
  // ...
  components: {
    // <my-component> 将只在父模板可用
    'my-component': Child
  }
})
```
### DOM 模板解析说明
使用DOM作为模板时，会受到HTML的一些限制。因为Vue只要在浏览器解析和标准化HTML之后才能获取模板内容。尤其是`<ul>, <ol>, <table>, <select>`限制了能被它包裹的元素。
在自定义组件中使用这些首限制的元素时会导致一些问题：
```
<table>
    <my-row>...</my-row>
</table>
```
自定义组件`<my-row>`被认为是无效的内容，在渲染的时候会出现问题。可以用下面的方法来绕过HTML的限制
```
<table>
  <tr is="my-row"></tr>
</table>
```
### `data`必须是函数
通过Vue构造器传入的各种选项大多数可以在组件里用。`data`是一个例外，他必须是函数。
```
Vue.component('my-component', {
  template: '<span>{{ message }}</span>',
  data: {
    message: 'hello'
  }
})
```
在上述代码中，Vue会停止运行，并在控制台报警。可以通过以下方式绕开Vue的警告
```
<div id="example-2">
  <simple-counter></simple-counter>
  <simple-counter></simple-counter>
  <simple-counter></simple-counter>
</div>
<script>
var data = { counter: 0 }
Vue.component('simple-counter', {
  template: '<button v-on:click="counter += 1">{{ counter }}</button>',
  // 技术上 data 的确是一个函数了，因此 Vue 不会警告，
  // 但是我们返回给每个组件的实例的却引用了同一个data对象
  data: function () {
    return data
  }
})
new Vue({
  el: '#example-2'
})
</script>
```
由于这三个组件共享了同一个`data`，因此增加一个counter会影响所有的组件。
### 构成组件
组件意味着协同工作，组件之间需要通讯。在Vue中，父子组件的关系可以总结为props down，eventsup。父组件通过props向下传递数据给子组件，子组件通过events给父组件发送消息。
![Alt text](https://cn.vuejs.org/images/props-events.png)
## prop
### 使用prop传递数据
组件实例的作用域是孤立的，这意味着不能(也不应该)在子组件的模板内直接引用父组件的数据。要让子组件使用父组件的数据，我们需要通过子组件的props选项。
子组件要显示地用`props`选项声明它期待获取的数据。
```
<child message="hello!"></child>
<script>
Vue.component('child', {
  // 声明 props
  props: ['message'],
  // 就像 data 一样，prop 可以用在模板内
  // 同样也可以在 vm 实例中像 “this.message” 这样使用
  template: '<span>{{ message }}</span>'
})
</script>
```
### 驼峰式 vs 短划线式
HTML特性是不区分大小写。所以，当不使用字符串模板，驼峰式命名的prop需要转换为相应的短划线式命名：
```
<!-- kebab-case in HTML -->
<child my-message="hello!"></child>
<script>
Vue.component('child', {
  // camelCase in JavaScript
  props: ['myMessage'],
  template: '<span>{{ myMessage }}</span>'
})
</script>
```
### 动态prop
在模板中，要动态绑定父组件的数据到子模板的props，与绑定到任何普通的HTML特性相似，用`v-bind`。当父组件的数据变化时，该变化也会传导给子组件：
```
<div>
  <input v-model="parentMsg">
  <br>
  <child v-bind:my-message="parentMsg"></child>
</div>
```
### 单向数据流
prop是单向绑定的：当父组件的属性变化时，将传导给子组件，但是反过来不会。这是为了防止子组件修改父组件的状态。
另外，**每次父组件更新时，子组件的所有`prop`都会更新为最新值。这意味着你不应该在子组件内部改变`prop`**。
1. 定义一个局部变量，并用 prop 的值初始化它：
```
props: ['initialCounter'],
data: function () {
  return { counter: this.initialCounter }
}
```
2. 定义一个计算属性，处理 prop 的值并返回。
```
props: ['size'],
computed: {
  normalizedSize: function () {
    return this.size.trim().toLowerCase()
  }
}
```
### prop 验证
可以为组件的props指定验证规格，如果传入的数据不符合规格，Vue会发出警告。
要指定验证规格，需要用对象的形式，而不是字符串数组。
```
Vue.component('example', {
  props: {
    // 基础类型检测 （`null` 意思是任何类型都可以）
    propA: Number,
    // 多种类型
    propB: [String, Number],
    // 必传且是字符串
    propC: {
      type: String,
      required: true
    },
    // 数字，有默认值
    propD: {
      type: Number,
      default: 100
    },
    // 数组／对象的默认值应当由一个工厂函数返回
    propE: {
      type: Object,
      default: function () {
        return { message: 'hello' }
      }
    },
    // 自定义验证函数
    propF: {
      validator: function (value) {
        return value > 10
      }
    }
  }
})
```
`type`可用的原生构造器有： String, Number, Boolean, Fucntion, Object, Array， `type`也可以是一个自定义构造函数，使用`instanceof`检测
## 自定义事件
使用自定义事件将数据从子组件传递到父组件。绑定的事件是父组件的methods方法
### 使用`v-on`绑定自定义事件
- 使用`$on(eventName)`监听事件
- 使用`$emit(eventName)` 触发事件
```
<div id="counter-event-example">
  <p>{{ total }}</p>
  <button-counter v-on:increment="incrementTotal"></button-counter>
  <button-counter v-on:increment="incrementTotal"></button-counter>
</div>
<script>
Vue.component('button-counter', {
  template: '<button v-on:click="increment">{{ counter }}</button>',
  data: function () {
    return {
      counter: 0
    }
  },
  methods: {
    increment: function () {
      this.counter += 1
      this.$emit('increment')
    }
  },
})
new Vue({   // 父组件
  el: '#counter-event-example',
  data: {
    total: 0
  },
  methods: {
    incrementTotal: function () {
      this.total += 1
    }
  }
})
</script>
```
可以使用`.native`修饰`v-on`来在某个组件的根元素上监听一个原生事件。
```
<my-component v-on:click.native="doTheThing"></my-component>
```
### 使用自定义事件的表单输入组件
自定义事件可以用来创建自定义的表单输入组件，使用`v-model`来进行数据双向绑定.
```
<input v-model="something">
```
可以看做是以下实例的语法糖：
```
<input v-bind:value="something" v-on:input="something = $event.target.value">
```
所以要让组件的`v-model`生效，它必须：
- 接收一个`value`属性
- 在有新的value时触发`input`事件
```
<div id="app">
  <currency-input 
    label="Price" 
    v-model="price"
  ></currency-input>
  <currency-input 
    label="Shipping" 
    v-model="shipping"
  ></currency-input>
  <currency-input 
    label="Handling" 
    v-model="handling"
  ></currency-input>
  <currency-input 
    label="Discount" 
    v-model="discount"
  ></currency-input>
  
  <p>Total: ${{ total }}</p>
</div>
```
```
Vue.component('currency-input', {
  template: '\
    <div>\
      <label v-if="label">{{ label }}</label>\
      $\
      <input\
        ref="input"\
        v-bind:value="value"\
        v-on:input="updateValue($event.target.value)"\
        v-on:focus="selectAll"\
        v-on:blur="formatValue"\
      >\
    </div>\
  ',
  props: {
    value: {
      type: Number,
      default: 0
    },
    label: {
      type: String,
      default: ''
    }
  },
  mounted: function () {
    this.formatValue()
  },
  methods: {
    updateValue: function (value) {
      var result = currencyValidator.parse(value, this.value)
      if (result.warning) {
        this.$refs.input.value = result.value
      }
      this.$emit('input', result.value)
    },
    formatValue: function () {
      this.$refs.input.value = currencyValidator.format(this.value)
    },
    selectAll: function (event) {
      // Workaround for Safari bug
      // http://stackoverflow.com/questions/1269722/selecting-text-on-focus-using-jquery-not-working-in-safari-and-chrome
      setTimeout(function () {
      	event.target.select()
      }, 0)
    }
  }
})

new Vue({
  el: '#app',
  data: {
    price: 0,
    shipping: 0,
    handling: 0,
    discount: 0
  },
  computed: {
    total: function () {
      return ((
        this.price * 100 + 
        this.shipping * 100 + 
        this.handling * 100 - 
        this.discount * 100
      ) / 100).toFixed(2)
    }
  }
})
```
### 非父子组件通信
如果两个组件不是父子关系，可以使用一个空的Vue实例作为中间通信。在复杂的情况下，我们应该考虑使用专门的 状态管理模式.
```
var bus = new Vue();
/ 触发组件 A 中的事件
bus.$emit('id-selected', 1)
// 在组件 B 创建的钩子中监听事件
bus.$on('id-selected', function (id) {
  // ...
})
```
### 使用slot分发内容