---
title: 深入浅出 nodejs
date: 2019-08-18 12:00:00
---

# String replace
```java
/**
*   不是正则替换，效率是不是会低些
*/
public static String replace(String inString, String oldPattern, @Nullable String newPattern) {
    if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
        return inString;
    }
    int index = inString.indexOf(oldPattern);
    if (index == -1) {
        // no occurrence -> can return input as-is
        return inString;
    }

    int capacity = inString.length();
    // 为什么 + 16
    if (newPattern.length() > oldPattern.length()) {
        capacity += 16;
    }
    StringBuilder sb = new StringBuilder(capacity);

    int pos = 0;  // our position in the old string
    int patLen = oldPattern.length();
    while (index >= 0) {
        sb.append(inString.substring(pos, index));
        sb.append(newPattern);
        pos = index + patLen;
        index = inString.indexOf(oldPattern, pos);
    }

    // append any characters to the right of a match
    sb.append(inString.substring(pos));
    return sb.toString();
}
```

# Spring 自定义标签
在配置较为复杂或许要更多丰富控制的时候，Spring的标准配置 bean 会比较笨拙。一个折中的方案是通过扩展 Spring 的 Schema。扩展 Spring 自定义标签配置一般有以下几个步骤：
- 创建一个需要扩展的组件
- 定义一个 XSD 文件描述组件内容
- 创建一个文件，实现 BeanDefinitionParser 接口，用来解析 XSD 文件中的定义和组件定义
- 创建一个 Handler 文件，扩展 NamespaceHandlerSupport，将组件注册到 Spring 容器
- 编写 Spring.handlers 和 Spring.schemas 文件

