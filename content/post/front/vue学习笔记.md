---
title : vue 学习笔记
date: 2017-01-21 12:00:00
tags: ["javascript"]
categories: ["javascript"]
---


# 属性和方法
每个vue实例都会代理其data对象的所有属性
```js
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

在vue中只有被代理的属性才会被监控，如果在创建Vue后，data 新添加属性，是不会被监控的，也就是更新值没有任何响应。可以通过 `$watch` 方法加入响应 。
Vue中自带一些以`$`开头的属性和方法：
```js
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

# 实例的生命周期
下面的图是vue实例的整个生命周期。
![](http://vuejs.org/images/lifecycle.png)
在vue实例创建的过程中，有一些hook会被调用。我们可以通过修改这些hook来执行自定义的操作。 hook有`created`, `mounted`, `updated`, `destroyed`.
```js
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

# 模板语法
Vue 使用了基于 HTML 的模板语法，允许开发者声明式地将 DOM 绑定到底层 Vue 实例的上述。
在底层实现上，Vue 将模板编译成虚拟 DOM 渲染函数，结合响应系统，Vue 能够智能地计算出最少需要重新渲染多少组件，并把 DOM 操作次数降到最少。

## 插值
### 文本
数据绑定最常见的形式就是使用“Mustache”语法(双大括号)的文本插值：
```html
<span>Message: {{msg}}</span>
<!-- 使用 v-once 指令，只能执行一次性插值，当数据改变时，插值内容不会更改 -->
<span v-once>这个将不会改变: {{ msg }}</span>
```

### 原始HTML
文本插值只能将数据转换成普通文本。需要使用 `v-html` 来输出真正的 HTML
```html
<p> using v-html: <span v-html="rawHtml"></span></p?
```

上例中 `span` 的内容将会被替换成 `rawHtml`，但是不会解析 `rawHtml` 中的数据绑定。

### 特性
Mustache 语法不能在HTML标签的属性上使用，需要使用 `v-bind` 指令：
```html
<div v-bind:id="dynamicId"></div>
```

在属性取值是布尔值的情况下，它们存在即为 `true`:
```html
<button v-bind:disabled="isButtonDisabled">Button</button>
```

### 在插值中使用JavaScript表达式
对于所有的数据绑定，Vue都提供了完全的JavaScript表达式支持：
```html
{{number + 1}}
{{ ok? 'YES':'NO'}}
{{ message.split('').reverse().join() }}
<div v-bind:id="'list' + id"></div>
```

## 指令
指令是带有 `v-` 前缀的特殊属性，比如：
```html
<p v-if="seen">now you see me</p>
```

`v-if` 会根据表达式 `seen` 的值的真假来插入或者移除 `<p>` 元素

### 参数
一些指令能够接收一个参数，在指令名称后用冒号表示：
```html
<a v-bind:href="url" v-on:click="doSomething"></a>
```

这里的 `href` 是参数，其作用是告知 `v-bind` 指令将该元素的 `href` 属性与表达式 `url` 的值绑定。

`v-on` 也是指令，它用于监听 DOM 事件。

### 修饰符
修饰符是以半角句号 `.` 指明的特殊后缀，用于指出一个指令应该以特殊的方式绑定。
```html
<form v-on:submit.prevent="onSubmit">
```

上例中的 `.prevent` 修饰符告诉 `v-on` 指令对于触发的事件，调用 `event.preventDefault()`

## 缩写
`v-` 前缀作为视觉提示，用来识别模板中 vue 特定的特性。但是对于频繁用到的指令来说，`v-` 前缀就显得比较累赘了。因此可以将如下指令进行简写：
- `v-bind:href="url"` 可以缩写为 `:href="url"`
- `v-on:click="fun"` 可以缩写为 `@click="fun"`

# 计算属性
模板内使用表达式可以非常方便地进行简单计算，但是如果进行复制计算，就会让模板过重而且难以维护。因此，对于任何复杂逻辑，都应该使用计算属性。
```html
<div id="example">
    <p>Original message: "{{ message }}"</p>
    <p>Computed reversed message: "{{ reversedMessage }}"</p>
</div>
```

```js
var vm = new Vue({
   el: "#example",
    data: {
        message: "hello"
    },
    computed: {
        // 声明计算属性
        reversedMessage: function () {
            // this 指向vm实例
            return this.message.split('').reverse().join('');
        }
    }
});
```

上例中，我们声明了一个计算属性 `reversedMessage`，我们提供的函数将作为 `reversedMessage` 的 getter 函数：
```js
console.log(vm.reversedMessage) // 'olleh'
vm.message = 'Goodbye'
console.log(vm.reversedMessage) // 'eybdooG'
```

我们可以将 `reversedMessage` 作为 `vm` 的属性使用，而且它的值取决于 `vm.message` 的值。
计算属性相比方法的优点是**计算属性是基于它们的依赖进行缓存的**。计算属性只有在它的相关依赖发生变化时才会重新求值，而方法会在每次触发重新渲染时都会执行函数。
假设我们有一个性能开销比较大的的计算属性 A，它需要遍历一个巨大的数组并做大量的计算。然后我们可能有其他的计算属性依赖于 A 。如果没有缓存，我们将不可避免的多次执行 A 的 getter！
如果你不希望有缓存，请用方法来替代。

计算属性默认情况下，只有 getter。当然，在需要时可以提供一个 setter：
```js
//...
computed: {
    fullName: {
        get: function () {
            return this.firstName + ' ' + this.lastName
        },
        set: function () {
            var names = newValue.split(' ');
            this.firstName = names[0];
            this.lastName = names[names.length - 1];
        }
    }
}
// ...
// 现在可以用如下方式更新 fullName
vm.fullName = 'John Doe';
```

# 侦听器
虽然计算属性在大多数情况下更合适，但是有时也需要自定义的侦听器。**侦听器更适合在数据变化时执行异步或者开销较大的操作时使用**

```html
<div id="watch-example">
    <p>
        Ask a yes/no question:
        <input v-model="question">
    </p>
    <p>{{ answer }}</p>
</div>

<script src="https://cdn.jsdelivr.net/npm/axios@0.12.0/dist/axios.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/lodash@4.13.1/lodash.min.js"></script>
<script>
var watchExampleVM = new Vue({
    el: '#watch-example',
    data: {
        question: '',
        answer: 'I cannot give you an answer until you ask a question!'
    },
    watch: {
        // 如果 `question` 发生改变，这个函数就会运行
        question: function (newQuestion, oldQuestion) {
        this.answer = 'Waiting for you to stop typing...'
        this.getAnswer()
        }
    },
    methods: {
        // `_.debounce` 是一个通过 Lodash 限制操作频率的函数。
        // 在这个例子中，我们希望限制访问 yesno.wtf/api 的频率
        // AJAX 请求直到用户输入完毕才会发出。想要了解更多关于
        // `_.debounce` 函数 (及其近亲 `_.throttle`) 的知识，
        // 请参考：https://lodash.com/docs#debounce
        getAnswer: _.debounce(
        function () {
            if (this.question.indexOf('?') === -1) {
            this.answer = 'Questions usually contain a question mark. ;-)'
            return
            }
            this.answer = 'Thinking...'
            var vm = this
            axios.get('https://yesno.wtf/api')
            .then(function (response) {
                vm.answer = _.capitalize(response.data.answer)
            })
            .catch(function (error) {
                vm.answer = 'Error! Could not reach the API. ' + error
            })
        },
        // 这是我们为判定用户停止输入等待的毫秒数
        500
        )
    }
})
</script>
```

# 条件渲染
可以使用 `v-if` 和 `v-else` 指令来实现条件渲染：
```html
<h1 v-if="ok">Yes<h1>
<h1 v-else>No</h1>
```

还可以使用 `v-else-if` :
```html
<div v-if="type === 'A'">
  A
</div>
<div v-else-if="type === 'B'">
  B
</div>
<div v-else-if="type === 'C'">
  C
</div>
<div v-else>
  Not A/B/C
</div>
```

## 用 `key` 管理可复用元素
Vue 会尽可能高效地渲染元素，通常会复用已有元素而不是从头开始渲染。这么做除了使 Vue 变得非常快之外，还有其它一些好处。例如，如果你允许用户在不同的登录方式之间切换：
```html
<template v-if="loginType === 'username'">
    <label>Username</label>
    <input placeholder="Enter your username">
</template>
<template v-else>
    <label>Email</label>
    <input placeholder="Enter your email address">
</template>
```

那么在上面的代码中切换 `loginType` 将不会清除用户已经输入的内容。因为两个模板使用了相同的元素，`<input>` 不会被替换掉——仅仅是替换了它的 `placeholder`。

这样也不一定能符合实际需求，所以可以通过 `key` 属性来表达两个元素完全是独立的，不复用他们：
```html
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
另一个根据条件展示元素的指令是 `v-show`:
```js
<h1 v-show="ok">Hello</h1>
```

`v-if` 是“真正”的条件渲染，因为它会确保在切换过程中条件块内的事件监听器和子组件适当地被销毁和重建。

`v-if` 也是惰性的：如果在初始渲染时条件为假，则什么也不做——直到条件第一次变为真时，才会开始渲染条件块。

`v-show` 就简单得多——不管初始条件是什么，元素总是会被渲染，并且只是简单地基于 CSS 进行切换。

一般来说，`v-if` 有更高的切换开销，而 `v-show` 有更高的初始渲染开销。因此，**如果需要非常频繁地切换，则使用 `v-show` 较好；如果在运行时条件很少改变，则使用 `v-if` 较好。**

# 列表渲染
`v-for` 指令可以将一个数组渲染成列表。格式为 `v-for="item in itmes"`，其中 `items` 是原始数组数据，`item` 是数组中每个迭代元素的别名。
```html
<ul id="example">
    <li v-for="item in items">
        {{item.message}}
    </li>
</ul>
<script>
    var example = new Vue({
        el: '#example',
        data: {
            items: [
                { message: 'Foo' },
                { message: 'Bar' }
            ]
        }
    });
</script>
```

`v-for` 还支持可选的第二个参数，作为当前项的索引：
```html
<ul id="example">
    <li v-for="(item,index) in items">
        {{index}} - {{item.message}}
    </li>
</ul>
```

## 遍历对象
`v-for` 也支持遍历对象属性，它是按照 `Object.keys()` 得出 `key` 的枚举顺序来遍历的：
```html
<div v-for="value in object">
    {{ value }}
</div>
<div v-for="(value, key) in object">
    {{ key }}: {{ value }}
</div>
<div v-for="(value, key, index) in object">
    {{ index }}. {{ key }}: {{ value }}
</div>
```

## 数组变化检测
Vue 对下列数组方法包装，以便在调用时触发视图更新：
- `push()`
- `pop()`
- `shift()`
- `unshift()`
- `splice()`
- `sort()`
- `reverse()`

## 数组替换
有一些方式不是对数据进行替换，而是返回一个新的数组，例如 `filter()`, `concat()` 和 `slice()`。当使用非变化数组方法时，可以将就数组替换为新数组：
```js
example.items = example.items.filter(function (item) {
    return item.message.match(/Foo/)
})
```

因为 JavaScript 语法限制，**Vue 无法检测到以下数组变动**：
1. 使用索引直接设置一项： `example.items[index] = newValue`，可以用 Vue 提供的方法设置： `vm.$set(vm.items, index, newValue)`
2. 修改数组长度：`example.items.length = newLength`，可以使用 `vm.items.splice(newLength)` 方法代替

## 对象变化检测
Vue 无法检测对象属性的添加或者删除，但是可以通过 `Vue.set(object, key, value)` 来将响应式属性添加到嵌套对象上。
```js
var vm = new Vue({
  data: {
    a: 1
  }
})
// `vm.a` 是响应的

vm.b = 2
// `vm.b` 不是响应的

/** 可以使用如下方式*/
var vm = new Vue({
    data: {
        userProfile: {
        name: 'Anika'
        }
    }
})
Vue.set(vm.userProfile, 'age', 27)
// 或者
vm.$set(vm.userProfile, 'age', 27)
```

## 使用计算属性或者方法
如果要显示一个数组过滤或者排序后的副本，可以使用计算属性，当计算属性不适用的情况下，可以使用 method 方法：
```html
<li v-for="n in evenNumbers"> {{n}} </li>
<script>
    data: {
        numbers: [1, 2, 3, 4, 5]
    },
    computed: {
        evenNumbers: function () {
            return this.numbers.filter(function(number) {
                return number % 2 === 0;
            })
        }
    }
</script>

<!-- 使用method -->
<li v-for="n in even(numbers)"> {{n}} </li>
<script>
    data: {
        numbers: [1, 2, 3, 4, 5]
    },
    methods: {
        even: function () {
            return this.numbers.filter(function(number) {
                return number % 2 === 0;
            })
        }
    }
</script>
```

## 使用 `v-for` 在整数值范围内迭代
```html
<div>
    <span v-for="n in 10">{{n}} </span>
</div>
```

## 使用 `<template>` 来渲染多个模块
```html
<ul>
    <template v-for="item in items">
        <li> {{item.msg}} </li>
        <li class="divider"></li>
    </template>
</ul>
```

## `v-for` 中的 `v-if`
当他们处于一个节点时，`v-for` 的优先级比 `v-if` 高，这意味着 `v-if` 将分别在循环中的每次迭代上运行：
```html
<li v-for="todo in todos" v-if="!todo.isComplete">
    {{todo}}
</li>
```

# 事件处理
`v-on` 指令用于监听 DOM 事件，并在事件被触发时，执行一些 JavaScript 代码。
```html
<div id="example">
    <button v-on:click="counter += 1">增加1</button>
    <p> 上面的按钮被点击了 {{counter}} 次 </p>
</div>
<script>
    var example = new Vue ({
        el: '#example',
        data: {
            counter: 0
        }
    })
</script>
```

## 在 `methods` 中定义事件处理函数
对于复杂的事件处理逻辑，可以调用定义在 `methods` 中的方法：
```html
<div id="example2">
    <!-- 绑定到方法名 -->
    <button @click="greet">Greet</button>
    <!-- 直接运行 JavaScript 方法，可以通过 $event 将原始 DOM 事件对象传递给方法 -->
    <button @click="say('hi', $event)">Say hi</button>
</div>
<script>
    var example2 = new Vue({
        el: '#example2',
        methods: {
            greet: function(event) {
                alert('Hello');
                // event 是原始 DOM 事件对象
                if (event) {
                    alert(event.target.tagName);
                }
            },
            say: function(msg, event) {
                alert(msg);
                if (event) event.preventDefault()
            }
        }
    })
</script>
```

## 事件修饰符
在事件处理程序中调用 `event.preventDefault()` 或者 `event.stopPropagation()` 是非常常见的需求。`v-on` 指令提供了修饰符，可以隐藏 DOM 事件细节：
- `.stop`
- `.prevent`
- `.capture`
- `.self`
- `.once`

```html
<!-- 停止点击事件冒泡 -->
<a v-on:click.stop="doThis"></a>

<!-- 提交事件不再重新载入页面 -->
<form v-on:submit.prevent="onSubmit"></form>

<!-- 修饰符可以链式调用 -->
<a v-on:click.stop.prevent="doThat"></a>

<!-- 只有修饰符 -->
<form v-on:submit.prevent></form>

<!-- 添加事件监听器时，使用事件捕获模式 -->
<!-- 也就是说，内部元素触发的事件先在此处处理，然后才交给内部元素进行处理 -->
<div v-on:click.capture="doThis">...</div>

<!-- 只有在 event.target 是元素自身时，才触发处理函数。 -->
<!-- 也就是说，event.target 是子元素时，不触发处理函数 -->
<div v-on:click.self="doThat">...</div>
```

> 使用修饰符时的顺序会产生一些影响，因为相关的代码会以相同的顺序生成。所以，使用`v-on:click.prevent.self` 会阻止所有点击，而 `v-on:click.self.prevent` 只阻止元素自身的点击。

## 按键修饰符
在监听键盘事件时，我们经常需要查找常用按键对应的 code 值。Vue可以在 `v-on` 上添加按键修饰符，用于监听按键事件：
```html
<!-- 只在 `keyCode` 是 13 时，调用 `vm.submit()` -->
<input v-on:keyup.13="submit">
```

Vue 提供了常用按键的别名：
- `.enter`
- `.tab`
- `.delete`
- `.esc`
- `.space`
- `.up`
- `.down`
- `.left`
- `.right`

## 系统辅助按键
修饰符对应的按键被按下时，才会触发鼠标或键盘事件监听器：
- `.ctrl`
- `.alt`
- `.shift`
- `.meta`

```html
<!-- Alt + C -->
<input @keyup.alt.67="clear">

<!-- Ctrl + Click -->
<div @click.ctrl="doSomething">做一些操作</div>
```

# 表单绑定
`v-model` 指令可以将表单中的数据绑定起来。

## `input`
使用 `v-model` 指令将表单中的 `input` 和 `textarea` 元素上创建双向数据绑定。
```html
<input v-model="message" placeholder="编辑">
<p>message 是：{{ message }}</p>

<span>多行 message 是：</span>
<p style="white-space: pre-line;">{{ message }}</p>
<br>
<textarea v-model="message" placeholder="添加多行"></textarea>
```

## `checkbox`
单选 checkbox:
```html
<input type="checkbox" id="checkbox" v-model="checked">
<label for="checkbox">{{ checked }}</label>
```

多选 checkbox，绑定到同一个数组：
```html
<div id='example2'>
    <input type="checkbox" id="jack" value="Jack" v-model="checkedNames">
    <label for="jack">Jack</label>
    <input type="checkbox" id="john" value="John" v-model="checkedNames">
    <label for="john">John</label>
    <input type="checkbox" id="mike" value="Mike" v-model="checkedNames">
    <label for="mike">Mike</label>
    <br>
    <span>勾选的名字是：{{ checkedNames }}</span>
</div>
<script>
new Vue({
    el: '#example2',
    data: {
        checkedNames: []
    }
})
</script>
```

## `radio`
```html
<div id="example3">
    <input type="radio" id="one" value="one" v-model="picked">
    <label for="one">One</label>
    <input type="radio" id="two" value="Two" v-model="picked">
    <label for="two">Two</label>
    <span>选中的是：{{ picked }}</span>
</div>
<script>
    new Vue({
        el: '#example3',
        data: {
            picked: ''
        }
    })
</script>
```

## `select`
单选：
```html
<div id="example4">
    <select v-model="selected">
        <option disabled value="">请选择其中一项</option>
        <option>A</option>
        <option>B</option>
        <option>C</option>
    </select>
    <span>选中的是：{{ selected }}</span>
</div>
<script>
    new Vue({
        el: '#example4',
        data: {
            selected: ''
        }
    })
</script>
```

多选：
```html
<div id="example5" >
    多选
    <select v-model="selected" multiple>
        <option v-for="option in options" v-bind:value="option.value">
            {{ option.text }}
        </option>
    </select>
    <span>选中的是：{{ selected }}</span>
</div>
<script>
    new Vue({
        el: '#example5',
        data: {
            selected: [],
            options: [
                { text: 'One', value: 'A' },
                { text: 'Two', value: 'B' },
                { text: 'Three', value: 'C' }
            ]
        }
    })
</script>
```

## 同 `value` 属性绑定
`v-model` 将 radio, checkbox, select的option选项与静态字符串 `value` 属性关联在一起。有时也可能需要把 value 与 Vue 实例上的动态属性绑定到一起，可以通过 `v-bind` 来实现。

```html
<!-- checkbox -->
<input
  type="checkbox"
  v-model="toggle"
  true-value="yes"
  false-value="no"
>

<!-- radio -->
<input type="radio" v-model="pick" v-bind:value="a">

<!-- select -->
<select v-model="selected">
    <!-- 内联对象字面量 -->
    <option v-bind:value="{ number: 123 }">123</option>
</select>
```

## 修饰符
默认情况下， `v-model` 会在每次 `input` 事件触发后，将数据同步到 `input` 元素中。可以添加 `lazy` 修饰符，转为触发 `change` 事件后同步：
```html
<!-- 在触发 "change" 事件后同步，而不是在触发 "input" 事件后更新 -->
<input v-model.lazy="msg" >
```

将用户的输入，自动转换为 Number 类型:
```html
<input v-model.number="age" type="number">
```

将用户的输入，自动过滤掉首尾空格:
```html
<input v-model.trim="msg">
```

# 组件
以下是一个简单的 Vue 组件示例：
```js
// 定义一个新的组件，名称是 button-counter
Vue.component('button-counter', {
    data: function () {
        return {
            count: 0
        }
    },
    template: '<button v-on:click="count++">你点击了 {{ count }} 次。</button>'
})
```

**组件中的 `data` 必须是一个函数**，这是便于每个实例都可以维护彼此独立的数据副本。

上面代码中定义了一个组件，组件时具有 `name` 名称的可复用的 Vue 实例。
使用 `new Vue` 创建一个 Vue 根实例，然后可以将该组件作为其中的一个元素：
```html
<div id="components">
    <button-counter></button-counter>
    <button-counter></button-counter>
    <button-counter></button-counter>
</div>
<script>
    new Vue({el: '#components'})
</script>
```

> 当点击按钮时，每个按钮都维护彼此独立的 `count`，因为每次使用组件，都会创建出一个新的组件实例。

## 注册组件
注册组件有两种方式：全局注册和局部注册：
```js
// 全局注册方式
Vue.component('my-component-name', {
    // ... options ...
})

// 局部注册方式
var componentA = {};
var componentB = { /* ... */ }
// 在 `components` 选项中，定义所用到的组件
new Vue ({
    el: '#app'
    components: {
        'component-a': componentA,
        'component-b': componentB
    }
})
```

`components` 对象的每个属性，key是自定义元素的名称，value 是组件对象。

**局部注册的组件在子组件中无法访问**，若想在 `componentB` 中访问 `componentA`，就必须在 `componentB` 中引入 `componentA`：
```js
var componentB = {
    components: {
        'component-a': componentA
    }
}
```

## 模块系统
模块系统中，推荐将组件文件放到 `components` 目录下。然后在局部注册组件前，需要预先导入每个需要用到的组件：
```js
import componentA from './componentA'

// 可以在这个模块使用 componentA 组件

export default {
    components: {
        componentA
    },
}
```

对于许多相对通用的组件，可以注册成全局组件，避免每个模块都引入一些列的基本组件：
```js
import Vue from 'vue'
import upperFirst from 'lodash/upperFirst'
import camelCase from 'lodash/camelCase'

const requireComponent = require.context(
    // components 文件夹的相对路径
    './components',
    // 是否查找子文件夹
    false,
    // 用于匹配组件文件名的正则表达式
    /Base[A-Z]\w+\.(vue|js)$/
)

requireComponent.keys().forEach(fileName => {
    // 获取组件配置
    const componentConfig = requireComponent(fileName)

    // 取得组件的 Pascal 式命名
    const componentName = upperFirst(
        camelCase(
        // 将文件名前面的 `'./` 和扩展名剥离
        fileName.replace(/^\.\/(.*)\.\w+$/, '$1')
        )
    )

    // 以全局方式注册组件
    Vue.component(
        componentName,
        // 如果组件是通过 `export default` 导出，
        // 则在 `.default` 中，查找组件选项，
        // 否则回退至模块根对象中，查找组件选项
        componentConfig.default || componentConfig
    )
})
```

## 使用 `props` 向子组件中传递数据
`props` 是注册在组件选项上的自定义属性。当一个值被放置在 `props` 中，作为其中一个 `prop`，这个值就会成为组件实例上的一个可访问的属性：
```js
Vue.component('blog-post', {
    props: ['title'],
    template: '<h3>{{title}}</h3>'
})
```

在预先注册好一个 `prop` 属性之后，就可以将数据作为自定义属性传递给这个 `prop` 属性：
```html
<!-- 静态 props -->
<blog-post title="我的 Vue 旅程"></blog-post>
<blog-post title="用 Vue 写博客"></blog-post>
```

也可以将 `data` 中的 `posts` 设为一个数组：
```js
new Vue({
    el: '#blog-post-demo',
    data: {
        posts: [
            { id: 1, title: '我的 Vue 旅程' },
            { id: 2, title: '用 Vue 写博客' },
            { id: 3, title: 'Vue 如此有趣' },
        ]
    }
})
```

然后将每条数据渲染为一个组件，动态的 props，使用 `v-bind` 来绑定：
```html
<!-- 动态 props -->
<blog-post
    v-for="post in posts"
    v-bind:key="post.id"
    v-bind:title="post.title">
</blog-post>
```

HTML 属性名称对大小写不敏感，浏览器会将所有大写字符解释为小写字符。因此，**在 DOM 模板中写 prop 时，应当将驼峰式转写为等价的串联式**：
```html
<div id="example">
    <blog-post post-title="hello!"></blog-post>
</div>
 <script>
    Vue.component('blog-post', {
        props: ['postTitle'],
        template: `<h3>{{postTitle}}</h3>`
    })

    new Vue({
        el: '#example'
    })
</script>
```

### 单向数据流
所有 `props` 都在父组件和子组件之间形成一个单向往下流动的数据绑定：当父组件的属性更新时，数据就会向下流动到子组件；但是，子组件的属性更新时，父组件并不会感知。这种机制可以防止子组件意外地修改了父组件的状态，造成应用程序的数据流变得难以理解。
一般，我们会在两种情况下修改子组的 prop：
1. prop 用于传递初始值，之后子组件将 prop 转换为一个局部数据属性。这种情况下，最好定义一个局部的 data 属性，然后将 prop 的值，作为局部属性的初始值：
    ```js
    props: ['initialCounter'],
    data: function () {
        return {
            counter: this.initialCounter
        }
    }
    ```
2. prop 用于传递一个需要转换的未加工值。这种情况下，最好预先定义一个 computed 属性，然后在函数内部引用 prop 的值：
    ```js
    props: ['size'],
    computed: {
        normalizedSize: function () {
            return this.size.trim().toLowerCase()
        }
    }
    ```

> JavaScript 中的对象和数组都是通过引用传递的，如果 prop 是一个数组或对象，则在子组件内部改变对象或数组本身，仍然会影响到父组件的状态。

### prop 验证
可以为 props 指定一些接收条件，如果某个接收条件未满足验证，Vue 就会在浏览器的 JavaScript 控制台中发出警告。这时 `props` 的值就是一个带有验证条件的对象：
```js
Vue.component('my-component', {
    props: {
        // 基本类型(base type)的检查（`null` 表示接受所有类型）
        propA: Number,
        // 多种可能的类型
        propB: [String, Number],
        // 必须传递，且 String 类型
        propC: {
            type: String,
            required: true
        },
        // Number 类型，有一个默认值
        propD: {
            type: Number,
            default: 100
        },
        // Object 类型，有一个默认值
        propE: {
            type: Object,
            // Object/Array 类型，
            // 默认必须返回一个工厂函数
            default: function () {
                return { message: 'hello' }
            }
        },
        // 自定义验证函数
        propF: {
            validator: function (value) {
                // 值必须是这些字符串中的一个
                return ['success', 'warning', 'danger'].indexOf(value) !== -1
            }
        }
    }
})
```

props 会在组件实例创建之前进行验证，因此在 `default` 或 `validator` 这些验证函数中，无法访问到实例上的属性，例如`data`, `computed`.

其中类型 `type` 可以是以下原生构造函数：
- String
- Number
- Boolean
- Function
- Object
- Array
- Symbol

还可以自定义函数，Vue 会通过 `instanceof` 对 props 值进行类型推断：
```js
function Person(firstName, lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
}

Vue.component('blog-post', {
    props: {
        author: Person
    }
})
```

## 使用 `events` 向父组件发送消息
当子组件要向父组件通信时，可以调用实例中内置的 `$emit` 方法来向父组件中发送事件：
```js
Vue.component('blog-post', {
    props: ['post'],
    template: `
        <div class="blog-post">
            <h3>{{ post.title }}</h3>
            <button @click="$emit('enlarge-text')"> 放大文本 </button>
            <div v-html="post.content"></div>
        </div>
    `
})
```

在父组件中也需要 `v-on` 来监听这个事件：
```html
<div id="components3">
    <div :style="{ fontSize: postFontSize + 'em' }">
        <blog-post
            v-for="post in posts"
            v-bind:key="post.id"
            v-bind:post="post"
            v-on:enlarge-text="postFontSize += 0.1"
        ></blog-post>
    </div>
</div>
```

当然，也可以在 `event` 事件中发送一个特定的值，可以使用 `$emit` 的第二个参数：
```html
<button @click="$emit('enlarge-text', 0.1)">放大文本</button>
```

然后，在父实例中监听这个事件，并通过 `$event` 来访问这次事件的值：
```html
<blog-post v-on:enlarge-text="postFontSize += $event"></blog-post>
```

如果事件处理函数是一个方法：
```html
<blog-post v-on:enlarge-text="onEnlargeText"></blog-post>
<script>
    methods: {
        onEnlargeText: function (enlargeAmount) {
            this.postFontSize += enlargeAmount
        }
    }
</script>
```

还可以使用 `v-model` 来创建自定义事件：
```html
<input v-model="searchText">
<!-- input 等同于 -->
<input v-bind:value="searchText" v-on:input="$event.target.value">
```

对于组件，`v-model` 可以替换如下：
```html
<custom-input v-bind:value="searchText" v-on:input="searchText = $event"></custom-input>
```

为了组件内部能够有效运行，组件内的 `<input>` 必须：
- 将 `value` 属性绑定到 `value` 的 prop
- 在 `input` 输入框中，在自定义的 `input` 事件中，发送一个新值

```js
Vue.component('custom-input', {
    props: ['value'],
    template: `
        <input 
            v-bind:value="value"
            v-on:input="$emit('input', $event.target.value)">
    `
})
```

 现在 `custom-input` 组件，应该可以实现 `v-model` 的完美运行：
 ```html
<custom-input v-model="searchText"></custom-input>
 ```

 

# 自定义事件
与 components 和 props 不同，事件名称并不提供命名自动转换。触发的事件名称必须与监听时的名称完全匹配，否则无法接收到事件。
```js
this.$emit("myEvent");
```

以下方式无法接收到事件：
```html
<my-component v-on:my-event="doSomething"></my-component>
```

**总是使用串联式命名(kebab-cased)来命名事件名称**

## 定制组件 `v-model`
自定义事件还可以用来创建出 “实现 `v-model` 机制的自定义输入框”：
```html
<input v-model="searchText">
<!-- 等同于 -->
<input v-bind:value="searchText"
       v-on:input="searchText = $event.target.value">
<!-- 用组件替换 -->
<custom-input
    v-bind:value="searchText"
    v-on:input="searchText = $event"
></custom-input>
```

为了组件内部能够有效运行，组件内的 `<input>` 必须：
- 将 `value` 属性绑定到 `value` prop
- 在 `input` 输入框中，在自定义的 `input` 事件中，发送一个新的值：
```js
Vue.component('custom-input', {
    props: ['value'],
    template: `
        <input 
            v-bind:value="value"
            v-on:input="$emit('input', $event.target.value)
        >
    `
})
```

现在我们的组件可以用 `v-model` 完美运行：
```html
<custom-input v-model="searchText"></custom-input>
```

在一个组件中，`v-model` 默认使用 `value` 作为 prop，已经默认使用 `input` 作为监听事件。然而对于CheckBox和radio等input元素，由于他们的input元素本身具有不同的语法，可能会占用 `value` 的特性。使用组件的 `model` 选项可以避免冲突：
```js
Vue.component('base-checkbox', {
    model: {
        prop: 'checked',
        event: 'change'
    },
    props: {
        checked: Boolean
    },
    template: `
        <input
        type="checkbox"
        v-bind:checked="checked"
        v-on:change="$emit('change', $event.target.checked)"
        >
    `
})
```

现在可以在这个组件中使用 `v-model`:
```html
<base-checkbox v-model="lovingVue"></base-checkbox>
```



# 使用 slots 进行内容分发
内容分发机制，可以构建如下组件：
```html
<navigation-link url="#">
    Your Profile
</navigation-link>
```

在 `<navigation-link>` 模板中，可以是：
```html
<a v-bind:href="url" class="nav-link">
    <slot></slot>
</a>
```

在组件渲染时，`<slot>` 元素就会被替换成 Your Profile。在 slot 位置，可以包含任何模板代码，可以是 HTML，也可以是其他组件：
```html
<navigation-link url="/profile">
    <!-- 使用一个组件添加一个图标 -->
    <font-awesome-icon name="user"></font-awesome-icon>
    Your Profile
</navigation-link>
```

## 命名 `slot`
可以向组件中传入多个 `slot`:
```html
<div class="container">
    <header>
        <!-- 在这里我们需要 header 内容 -->
    </header>
    <main>
        <!-- 在这里我们需要 main 内容 -->
    </main>
    <footer>
        <!-- 在这里我们需要 footer 内容 -->
    </footer>
</div>

<!-- name 属性用于定义除默认 slot 以外的 slot -->
<div class="container">
    <header>
        <slot name="header"></slot>
    </header>
    <main>
        <slot></slot>
    </main>
    <footer>
        <slot name="footer"></slot>
    </footer>
</div>
<!-- 或者在父组件模板的 <template> 元素上使用 slot 特性 -->
<base-layout>
    <template slot="header">
        <h1>这里是一个页面标题</h1>
    </template>

    <p>main 内容的一个段落。</p>
    <p>main 内容的另一个段落。</p>

    <template slot="footer">
        <p>这里是一些联系信息</p>
    </template>
</base-layout>
<!-- 也可以对某个普通元素，直接使用 slot 特性 -->
<base-layout>
    <h1 slot="header">这里是一个页面标题</h1>

    <p>main 内容的一个段落。</p>
    <p>main 内容的另一个段落。</p>

    <p slot="footer">这里是一些联系信息</p>
</base-layout>

<!-- 渲染内容 -->
<div class="container">
    <header>
        <h1>这里是一个页面标题</h1>
    </header>
    <main>
        <p>main 内容的一个段落。</p>
        <p>main 内容的另一个段落。</p>
    </main>
    <footer>
        <p>这里是一些联系信息</p>
    </footer>
</div>
```

### 默认 slot 内容
slot 可以预先提供默认内容：
```html
<button type="submit">
    <slot>Submit</slot>
</button
```

如果父组件模板中，向 slot 位置提供了内容，子组件中默认内容就会被替换。

### 编译时的作用域
在 slot 内部需要访问当前作用域下的数据：
```html
<navigation-link url="/profile">
    Logged in as {{ user.name }}
</navigation-link>
```

slot 中的内容不可以访问 `<navigation-link>` 组件内部的作用域。
父组件模板的内容，全部在父组件作用域内编译；字组件模板的内容，全部在子组件作用域内编译。

# 动态组件
通过向 Vue 的 `<component>` 元素传入 `is` 特性，可以实现动态效果：
```html
<component v-bind:is="currentTabComponent"></component>
```

在上面的示例中， `currentTabComponent` 也可以是以下之一：
- 已注册的注册名称
- 一个组件选项对象

如果需要组件的实例在第一次创建后就缓存起来，可以使用 `<keep-alive>` 元素将动态组件包裹起来：
```html
<keep-alive>
    <component v-bind:is="currentTabComponent"></component>
</keep-alive>
```

## 异步组件
在大型应用程序中，可能需要将应用程序拆分为多个小的分块，并且在实际用到时，才从服务器加载某个组件。为了简化异步拉取机制，Vue将组件定义为一个工厂函数，该函数可以异步解析组件定义对象。Vue 只在真正需要渲染组件的时候，才会去触发工厂函数，并将解析后的记过缓存，用来将来再次渲染。
```js
Vue.component('async-example', function (resolve, reject) {
    setTimeout(function() {
        resolve({template: '<div>This is async</div>'})
    }, 1000)
})
```

配合 webpack 代码分离功能：
```js
Vue.component('async-webpack-example', function (resolve) {
    // 这个特殊的 require 语法
    // 将指示 webpack 自动将构建后的代码，
    // 拆分到不同的 bundle 中，然后通过 Ajax 请求加载。
    require(['./my-async-component'], resolve)
})
```


# DOM 模板解析注意事项
有些 HTML 元素，例如 `<ul>`, `<ol>`, `<table>` 和 `<select>` 这些元素，会对于出现在其内部的元素有所限制；而另一些 HTML 元素，例如 `<li>`, `<tr>` 和 `<option>` 这些元素，只可以出现在前面那些元素的内部。
```html
<table>
    <blog-post></blog-post>
</table>
```

自定义组件 `<blog-post>` 会被当作无效内容，提升到 `table` 元素之外，从而导致最终渲染输出后的错误。`is` 特性提供了一种解决方案：
```html
<table>
<tr is="blog-post"></tr>
</table>
```

但是，以下字符串模板之一的场景中，并不适用：
- 字符串模板 `template: \`\``
- 单文件组件 (`.vue`)
- `<script type="text/x-template"></script>`


# mixin
mixin 是分发 Vue 组件的可复用功能的一种非常灵活的方式。每个 mixin 对象可以包含全部组件选项。当组件使用 mixin 对象时，mixin 对象中的全部选项都会被混入到组件中
```js
var myMixin = {
    created: function () {
        this.hello()
    },
    methods: {
        hello: function () {
            console.log('from mixin')
        }
    }
}

// 定义一个使用 mixin 对象的组件
var component = Vue.extend({
    mixins: [myMixin]
})

var component = new Component() // => "来自 mixin 对象的 hello！"
```

当 mixin 对象和组件自身的选项对象，在二者选项名称相同时，Vue 会选择合适的合并策略。
单个属性合并时，优先使用组件的 data;
具有相同名称的钩子函数，将合并到一个数组中，最终它们会被依次调用。此外，需要注意，mixin 对象中的同名钩子函数，会在组件自身的钩子函数之前调用。
```js
var mixin = {
    created: function () {
        console.log('mixin 对象的钩子函数被调用')
    },
    data: function () {
        return {
        message: 'hello',
        foo: 'abc'
        }
    }
}

new Vue({
    mixins: [mixin],
    data: function () {
        return {
        message: 'goodbye',
        bar: 'def'
        }
    },
    created: function () {
        console.log('组件的钩子函数被调用')
    }
})
```

# 自定义指令
除了核心功能默认内置的指令（`v-model` 和 `v-show`），Vue 也允许注册自定义指令。
如果要在页面加载时，某元素获得焦点（移动端 Safari 上 `autofocus` 不生效）。现在用指令来实现这个功能：
```js
// 注册一个全局自定义指令 `v-focus`
Vue.directive('focus', {
    // 当被绑定的元素插入到 DOM 中时……
    inserted: function (el) {
        // 聚焦元素
        el.focus()
    }
})

// 组件中注册局部指令
 Vue.component("nav-link", {
    directives: {
        focus: {
            // 指令的定义
            inserted: function (el) {
            el.focus()
            }
        }
    },
    template: `<input v-focus type="text"/>`
})
```


## 钩子函数
可以为一个自定义指令提供如下钩子函数：
- `bind`: 只在指令第一次绑定到元素时调用
- `inserted`: 被绑定元素插入父节点时调用
- `update`: 所在组件的VNode 更新时调用
- `componentUpdated`: 指令所在组件的 VNode 全部更新后调用
- `unbind`: 在指令与元素解绑时调用

指令钩子函数会被传入以下参数：
- `el`: 指令绑定的元素，可以直接操作 DOM
- `binding`: 一个对象，包含如下属性：
    - `name`: 指令名，不包含 `v-` 前缀
    - `value`: 指令绑定的值
    - `oldValue`: 指令绑定的前一个值，仅在 `update` 和 `componentUpdated` 钩子函数中可用
    - `expression`: 字符串形式的指令表达式。例如 `v-my-directive="1 + 1"` 中，表达式为 `"1 + 1"`。
    - `arg`: 传给指令的参数，可选。例如：`v-directive:foo` 中的 `foo`
- `vnode`: Vue 编译生成的虚拟节点

> 除了 `el` 之外，其他参数都应该是只读的，切勿修改

```html
<div id="hook-arguments-example" v-demo:foo.a.b="message"></div>
```

```js
new Vue({
    el: '#hook-arguments-example',
    directives: {
        demo: {
            bind: function (el, binding, vnode) {
                var s = JSON.stringify
                el.innerHTML =
                'name: '       + s(binding.name) + '<br>' +
                'value: '      + s(binding.value) + '<br>' +
                'expression: ' + s(binding.expression) + '<br>' +
                'argument: '   + s(binding.arg) + '<br>' +
                'modifiers: '  + s(binding.modifiers) + '<br>' +
                'vnode keys: ' + Object.keys(vnode).join(', ')
            }
        }
    },
    data: {
        message: 'hello!'
    }
})
```

# 渲染函数
template 创建 HTML 在大多数情况下是适用的。但是有些情况下，需要 JavaScript 的完全编程能力，这时候可以使用 `render` 函数。

在 HTML 层，我们定义组件接口：
```html
<anchored-heading :level="1">Hello world!</anchored-heading>
```

当有一个通过 `level` 属性动态生成 heading 标签的组件时：
```html
<script type="text/x-template" id="anchored-heading-template">
    <h1 v-if="level === 1">
        <slot></slot>
    </h1>
    <h2 v-else-if="level === 2">
        <slot></slot>
    </h2>
    <h3 v-else-if="level === 3">
        <slot></slot>
    </h3>
    <h4 v-else-if="level === 4">
        <slot></slot>
    </h4>
    <h5 v-else-if="level === 5">
        <slot></slot>
    </h5>
    <h6 v-else-if="level === 6">
        <slot></slot>
    </h6>
</script>
<script>
    Vue.component('anchored-heading', {
        template: '#anchored-heading-template',
        props: {
            level: {
            type: Number,
            required: true
            }
        }
    })
</script>
```

可以看到，这种场景中使用 `template` 并不是很好的选择，不但代码冗长，而且需要重复使用 `<slot></slot>`.

可以尝试使用 `render` 函数重写该例：
```js
 Vue.component('anchored-heading', {
    render: function(createElement) {
        return createElement( 
            `h${this.level}`,   // tag name 标签名称
            this.$slots.default  // 子组件中的阵列
        )
    },
    props: {
        level: {
            type: Number,
            required: true
        }
    }
})
```

## `createElement`
`createElement` 函数返回的是一个虚拟的DOM，其接受的参数如下：
```js
// @returns {VNode}
createElement(
    // {String | Object | Function}
    // 一个 HTML 标签字符串，组件选项对象，或者
    // 解析上述任何一种的一个 async 异步函数，必要参数。
    'div',

    // {Object}
    // 一个包含模板相关属性的数据对象
    // 这样，您可以在 template 中使用这些属性。可选参数。
    {
        
    },

    // {String | Array}
    // 子节点 (VNodes)，由 `createElement()` 构建而成，
    // 或使用字符串来生成“文本节点”。可选参数。
    [
        '先写一些文字',
        createElement('h1', '一则头条'),
        createElement(MyComponent, {
            props: {
                someProp: 'foobar'
            }
        })
    ]
)
```

## 深入 `data` 对象
`createElement` 中的数据对象可以是如下内容：
```js
{
    // 和`v-bind:class`一样的 API
    // 接收一个字符串、对象或字符串和对象组成的数组
    'class': {
        foo: true,
        bar: false
    },
    // 和`v-bind:style`一样的 API
    // 接收一个字符串、对象或对象组成的数组
    style: {
        color: 'red',
        fontSize: '14px'
    },
    // 正常的 HTML 特性
    attrs: {
        id: 'foo'
    },
    // 组件 props
    props: {
        myProp: 'bar'
    },
    // DOM 属性
    domProps: {
        innerHTML: 'baz'
    },
    // 事件监听器基于 `on`
    // 所以不再支持如 `v-on:keyup.enter` 修饰器
    // 需要手动匹配 keyCode。
    on: {
        click: this.clickHandler
    },
    // 仅对于组件，用于监听原生事件，而不是组件内部使用
    // `vm.$emit` 触发的事件。
    nativeOn: {
        click: this.nativeClickHandler
    },
    // 自定义指令。注意，你无法对 `binding` 中的 `oldValue`
    // 赋值，因为 Vue 已经自动为你进行了同步。
    directives: [
        {
        name: 'my-custom-directive',
        value: '2',
        expression: '1 + 1',
        arg: 'foo',
        modifiers: {
            bar: true
        }
        }
    ],
    // 作用域插槽格式
    // { name: props => VNode | Array<VNode> }
    scopedSlots: {
        default: props => createElement('span', props.text)
    },
    // 如果组件是其他组件的子组件，需为插槽指定名称
    slot: 'name-of-slot',
    // 其他特殊顶层属性
    key: 'myKey',
    ref: 'myRef'
}
```

示例如下：
```js
Vue.component('anchored-heading', {
    render: function (createElement) {
        var headingId = getChildrenTextContent(this.$slots.default)
            .toLowerCase().replace(/\W+/g, '-').replace(/(^\-|\-$)/g, '');
        return createElement(
            'h' + this.level,
            [
                createElement('a', {
                    attrs: {
                        name: headingId,
                        href: '#' + headingId
                    }
                }, this.$slots.default),
                createElement('p', {
                    'class': {
                        clamp: true
                    },
                    style: {
                        'line-height': '200px'
                    },
                    on: {
                        click: function () {alert('click')}
                    }
                }, this.$slots.default)
            ]
        )
    },
    props: {
        level: {
            type: Number, 
            required: true
        }
    }
})
```

## `v-model`
render 函数中没有与 `v-model` 相应的 api，必须自己实现相应的逻辑：
```js
props: ['value'],
render: function (createElement) {
    var self = this
    return createElement('input', {
        domProps: {
            value: self.value
        },
        on: {
            input: function(event) {
                self.$emit('input', event.target.value)
            }
        }
    })
}
```

## 事件和按键修饰符

修饰符    | 前缀
:--------|:---------------
`.passive` | `&`
`.capture` | `!`
`.once`    | `~`
`.capture.once` or  `.once.capture` | `~!`

例如：
```js
on: {
    '!click': this.doClick,
    '~keyup': this.doThisOnce,
    '~!mouseover': this.doMouseOver
}
```

## 函数式组件
函数式组件是指那些无状态，没有 `this` 上下文的组件，标记为 `functional`：
```js
Vue.component('my-component', {
    functional: true,
    // Props 可选
    props: {
        // ...
    },
    // 为了弥补缺少的实例
    // 提供第二个参数作为上下文
    render: function (createElement, context) {
        // ...
    }
})
```

# 插件
插件通常会为 Vue 添加全局功能。插件的范围没有限制，一般有如下几种：
1. 添加全局方法或者属性，如 [vue-custom-element](https://github.com/karol-f/vue-custom-element)
2. 添加全局资源：指令、过滤器、过渡等，如 [vue-touch](https://github.com/vuejs/vue-touch)
3. 通过全局 `mixin` 方法添加一些组件选项，如 [vue-router](https://github.com/vuejs/vue-router)
4. 添加 Vue 实例方法，如添加 `Vue.prototype` 上
5. 一个库，提供自己的API，如 [vue-router](https://github.com/vuejs/vue-router)

Vue.js 的插件应该有一个公共方法 `install`。该方法的第一个参数是 `Vue` 构造器，第二个参数是一个可选选项：
```js
MyPlugin.install = function (Vue, options) {
    // 1. 添加全局方法或属性
    Vue.myGlobalMethod = function () {
        // 逻辑...
    }

    // 2. 添加全局资源
    Vue.directive('my-directive', {
        bind (el, binding, vnode, oldVnode) {
        // 逻辑...
        }
        ...
    })

    // 3. 注入组件
    Vue.mixin({
        created: function () {
        // 逻辑...
        }
        ...
    })

    // 4. 添加实例方法
    Vue.prototype.$myMethod = function (methodOptions) {
        // 逻辑...
    }
}
```

通过全局方法 `Vue.use()` 来使用插件：
```js
Vue.use(MyPlugin)
Vue.use(MyPlugin, { someOption: true })
```

[awesome-vue](https://github.com/vuejs/awesome-vue#components--libraries) 中有很多优秀的插件和库

# 过滤器
Vue 可以自定义过滤器，过滤器可以用在两个地方：双括号插值和 `v-bind` 表达式。过滤器应该添加到 JavaScript 表达式尾部。
```html
<!-- 在双花括号中 -->
{{ message | capitalize }}

<!-- 在 `v-bind` 中 -->
<div v-bind:id="rawId | formatId"></div>
```

可以在一个组件的选项中定义本地的过滤器：
```js
filters: {
    capitalize: function (value) {
        if (!value) return ''
        value = value.toString()
        return value.charAt(0).toUpperCase() + value.slice(1)
    }
}
```

也可以在创建 Vue 实例之前定义全局过滤器：
```js
Vue.filter('capitalize', function (value) {
    if (!value) return ''
    value = value.toString()
    return value.charAt(0).toUpperCase() + value.slice(1)
})

new Vue({
    // ...
})
```

当然我们也可以将过滤器串联使用：
```
{{ message | filterA | filterB }}
```