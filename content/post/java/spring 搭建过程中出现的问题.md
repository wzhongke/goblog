---
title: spring 搭建过程中出现的问题
date: 2017-01-10 14:07:00
draft: true
---

1. 出现问题：
`org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'sessionFactory' defined in ServletContext resource [/WEB-INF/applicationContext.xml]: Invocation of init method failed; nested exception is java.lang.NoSuchFieldError: TRACE`
可能原因是有jar包冲突，删除冲突jar包后解决

2. XML配置和Java配置混合使用时，在XML中导入Java配置的Bean，使用 `<context:component-scan base-package="com.container"/>` ，而不是定义bean `<bean class="com.container.Bean"/>`

3. tomcat启动时，一直抛 `org.springframework.web.servlet.DispatcherServlet not find.`，运行 maven 的 package 解决。