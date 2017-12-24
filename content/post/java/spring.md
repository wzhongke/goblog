---
title: spring
---


# spring 模块

## Core Container
Core Container由 spring-core, spring-beans, spring-context, spring-context-support 和 spring-expression (Spring Expression Language)模块组成。
spring-core 和 spring-beans 模块提供了框架的基本功能——包括IoC和依赖注入。 `BeanFactory` 是一个精心设计的工厂模式，它消除了对程序单例的需求，并允许将实际程序逻辑中将依赖关系的配置和规范相分离。
spring-context 模块是在 core 和 beans 模块的基础上构建的：这是一种以类似JNDI注册表的框架访问对象方法的风格。Context模块继承了Beans模块的特性，并且增加了对国际化、事件传播、资源加载和透明创建上下文的支持。Context模块也支持Java EE的特性，如EJB，JMX和基本的远程调用。 `ApplicationContext` 接口是Context模块的核心。
spring-context-support 支持将第三方库整合到spring应用的上下文环境中，例如caching(EhCache, Guava, Jcache), mailing(JavaMail), scheduling(CommonJ, Quartz) 和模板引擎(FreeMarker, JasperReports, Velocity).
spring-expression 模块为运行时查询和操作对象图提供了强大的EL表达式。

## AOP 和 代理
spring-aop 模块提供了允许自定义的面向切面编程的AOP方式，比如用方法拦截和切入点来干净地将该分离的功能代码分离开。
spring-aspects 模块提供与 AspectJ 集成
spring-instrument 模块提供了在特定应用服务器中使用的类代理支持和类加载器的实现
spring-instrument-tomcat 模块包含了Tomcat的Spring工具代理

## Messaging
spring-messaging 模块提供了集成Spring消息模块的整合，像Message， MessageChannel，MessageHandler 和其他作为基于消息传递的应用程序

## Data Access/Integration
数据访问/集成 有 JDBC，ORM，OXM，JMS和事务模块
- spring-jdbc 模块提供了一个JDBC的抽象层，不需要进行单调乏味的JDBC编码和解析数据库特定的错误代码。
- spring-tx 模块支持编程和声明式事务管理，用于实现特殊接口的类和所有POJO
- spring-orm 模块提供了整个流行ORM API的数据层框架，如JPA，JDO和Hibernate。
- spring-oxm 模块提供了支持 Object-XML映射的抽象层，像 JAXB，Castor，XML Beans，JiBX 和 XStream
- spring-jms 模块包含了生产消费消息的特性

## Web
Web层包含了 spring-web, spring-webmvc, spring-websocket 和 spring-webmvc-portlet 模块
- spring-web 模块提供了基本的面向Web的集成功能，例如多文件上传功能，使用Servlet监听器对IoC进行初始化，面向Web的应用程序上下文。它还包含一个HTTP客户端和对Spring远程处理的Web相关部分支持。
- spring-webmvc 模块包含了针对web应用程序的 Spring的 MVC 和 REST Web Services 实现。Spring的MVC框架提供了域模型代码和Web表单之间的清晰分离，并且与Spring框架的其他所有功能集成在一起。
- spring-webmvc-portlet 模块（也称为Web-Portlet模块）提供了在Portlet环境中使用的MVC实现，并映射了spring-webmvc的功能

## Test
spring-test 模块支持 unit testing 和 Spring组件同 Junit或者TestNG之间的整合测试。它提供了一致的Spring `ApplicationContext`s加载和缓存上下文。 它还提供了模拟对象，您可以使用它来单独测试您的代码。

## Maven Dependency
使用Maven管理依赖时，我们都不需要明确地引入日志依赖。例如，创建应用上下文并使用依赖注入来配置应用，我们的Maven依赖应该如下：
```xml
<denpendencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>4.3.2.RELEASE</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

使用Maven过程中，很可能会意外地引入不同版本的Spring jar包。比如，在第三方库或者Spring项目中，将传递的依赖关系拉到较旧的版本。如果我们没有明确指定依赖，各种各样的意外问题都可能会出现。
我们可以使用 BOM(bill of materials) 来解决这个问题，我们可以在 `dependencyManagement` 中引入 `spring-framework-bom` 来保证Spring依赖是同一个版本。
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-framework-bom</artifactId>
            <version>4.3.2.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

使用BOM的另一个优点就是，我们无需在依赖中指定 `<version>` 属性:
```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
</dependency>
```

## 日志依赖
Spring引入的唯一一个外部依赖是 commons-logging 包，commons-logging 的运行时发现算法是有问题的。我们可以通过两种方式将 commons-logging 换掉：
1. 在 spring-core 模块中排除依赖关系
    ```xml
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-core</artifactId>
        <version>4.3.2.RELEASE</version>
        <exclusions>
            <exclusion>
                <groupId>commons-logging</groupId>
                <artifactId>commons-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    ```
2. 依靠一个特殊的 commons-logging 依赖，然后用一个空的jar来替换这个库

使用Log4j来作为框架的日志系统。Log4j是稳健效率高的，我们在测试Spring的时候，用的就是Log4j。在Spring中使用Log4j，我们需要在Maven中添加依赖，并且在classpath根目录下加入其配置文件log4j.properties 或者 log4j.xml:
```xml
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.14</version>
</dependency>
```

log4j.properties 如下：
```xml
log4j.rootCategory=INFO, stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %t %c{2}:%L - %m%n
log4j.category.org.springframework.beans.factory=DEBUG
```

# The IoC container
控制反转（IoC，又名依赖注入 DI）是一个过程，对象通过构造参数，或者工厂方法的参数，或者设置构造完成对象的参数来定义他们的依赖关系。然后容器在创建bean时将其依赖的对象注入到bean中。
`org.springframework.beans` 和 `org.springframework.context` 是Spring框架IoC容器的核心包。`BeanFactory` 接口提供了配置框架和基本的功能，`ApplicationContext` 增加了更多企业特定的功能，它完全是`BeanFactory`的超集。
那些构成应用程序骨干并被Spring IoC容器管理的对象，被称为 beans。

## Container Overview
`org.springframework.context.ApplicationContext` 接口代表了Spring IoC容器，它负责初始化，配置，并且组装上述的beans。一般，容器通过XML，或者Java注解，或者Java代码来配置对象。

通常在应用中，会创建`ClassPathXmlApplicationContext` 或者 `FileSystemXmlApplicationContext.` 的实例，来读取XML配置，并根据XML组装对象。

下图是高度抽象的视图，描述了spring是如何工作的。
{%raw%}
<svg xmlns="http://www.w3.org/2000/svg" width="440" height="296" viewBox="7 121 440 296"><defs><marker id="a" markerUnits="userSpaceOnUse" orient="auto" markerWidth="16.236" markerHeight="10.551" viewBox="-1 -1.376 16.236 10.551" refX="15.236" refY="3.899"><path fill="#323232" stroke="#323232" stroke-width="2" d="M2.236 3.9l12-3.9v7.798z"/></marker><marker id="b" markerUnits="userSpaceOnUse" orient="auto" markerWidth="16.236" markerHeight="10.551" viewBox="-1 -1.376 16.236 10.551" refX="15.236" refY="3.899"><path fill="#323232" stroke="#323232" stroke-width="2" d="M2.236 3.9l12-3.9v7.798z"/></marker><marker id="c" markerUnits="userSpaceOnUse" orient="auto" markerWidth="16.236" markerHeight="10.551" viewBox="-1 -1.376 16.236 10.551" refX="15.236" refY="3.899"><path fill="#323232" stroke="#323232" stroke-width="2" d="M2.236 3.9l12-3.9v7.798z"/></marker></defs><path fill="none" d="M7 121h440v296H7V121z"/><path fill="#f5927b" stroke="#323232" stroke-width="2" d="M208 213h198v68H208z"/><text x="88" y="15.375" fill="#323232" font-size="15" font-family="Arial" text-anchor="middle" transform="translate(218 237.625)">Spring 容器</text><path fill="none" stroke="#323232" stroke-width="2" d="M307 197.764V152" marker-start="url(#a)"/><path fill="none" d="M267 141h160v40H267z"/><text x="79" y="15.375" fill="#323232" font-size="15" font-family="微软雅黑" font-weight="bold" text-anchor="middle" transform="translate(267 151.625)">POJOs</text><path fill="none" stroke="#323232" stroke-width="2" d="M192.764 247H93" marker-start="url(#b)"/><path fill="none" d="M27 205h160v40H27z"/><text x="79" y="15.375" fill="#323232" font-size="15" font-family="Arial" font-weight="bold" text-anchor="middle" transform="translate(27 215.625)">配置</text><path fill="#f5927b" stroke="#323232" stroke-width="2" d="M213 339h188v58H213z"/><text x="83" y="15.375" fill="#323232" font-size="15" font-family="Arial" text-anchor="middle" transform="translate(223 358.625)">配置好的，可以使用的系统</text><path fill="none" stroke="#323232" stroke-width="2" d="M307 323.764V281" marker-start="url(#c)"/><path fill="none" d="M267 288h160v40H267z"/><text x="79" y="15.375" fill="#323232" font-size="15" font-family="Arial" font-weight="bold" text-anchor="middle" transform="translate(267 298.625)">生产对象</text></svg>
{%endraw%}

## 配置
Spring配置通常有至少一个被容器管理的bean组成。基于XML配置，是在顶层`<beans/>`中配置 `<bean/>`元素。基于Java配置的，通常是`@Configuration`注解的类中的`@Bean`注解的方法。
基于XML配置的数据如下：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd">
    <import resource="resources/messageSource.xml"/>
    <bean id="example1" class="com.spring.Example1">
        <!-- collaborators and configuration for this bean go here -->
    </bean>
    <bean id="example2" class="com.spring.Example2">
        <!-- collaborators and configuration for this bean go here -->
    </bean>
    <!-- more bean definitions go here -->
</beans>
```

我们可以通过如下方式获取一个`ApplicationContext`:
```java
ApplicationContext context =
new ClassPathXmlApplicationContext(new String[] {"services.xml", "daos.xml"});
Example1 example1 = context.getBean("example1", Example.class);
```

我们可以通过 `ApplicationContext` 接口的 `getBean()` 方法从Spring获取对象，但是这样会产生对Spring APIs的依赖，所以不建议这么做。我们可以通过框架来注入依赖。

### Bean 定义
我们可以使用如下属性来定义一个Bean元素

属性     | 描述
:------- |:----------
class    | bean 的类路径
name     | bean 的名字
scope    | bean 的生命周期
constructor arguments | 注入的构造参数
properties | 注入的属性值
autowiring mode | 自动注入模式
lazy-initialization mode | 懒加载模式
initialization method | 初始化方法
destruction method | 销毁方法

我们还可以将在容器外创建的对象加入到Spring容器中：通过 `ApplicationContext` 的 `getBeanFactory()` 方法获取到 `BeanFactory`，然后通过 `registerSingleton(...)` 或者 `registerBeanDefinition(...)` 来注册到容器中。

### Bean 命名
每个Bean都有一个或多个标识符，标识符在容器中必须唯一。如果我们不提供标识符（id 或 name)，容器会为该Bean生成一个唯一的标识符。

我们可以用 `alias` 为 Bean 定义一个别名：
```xml
<alias name="subsystemA-dataSource" alias="subsystemB-dataSource"/>
```

### Bean 初始化
如果是基于XML配置的容器，我们通过`<bean/>` 标签中的 `class` 属性来指定对象的类型。对于内部静态类，我们需要在 bean 的class 属性中，用 "$" 分隔。如果 `com.example.Foo` 中有一个静态内部类 `Bar`，那么 bean 的 class 属性应该写为： `com.example.Foo$Bar`
构造Bean的方法有： 构造器、静态工厂方法、对象的工厂方法：
```xml
 <!-- 静态工厂方法构造bean -->
<bean id="clientService" class="com.examples.ClientService" factory-method="createInstance"/>

<!-- 对象的工厂方法构造 bean -->
<bean id="clientService2" factory-bean="clientService" factory-method="createClient" />
```

## 依赖
Denpendency Injection 有两种主要的方式：基于构造方法的 和 基于Setter的

### 基于构造方式 
基于构造方式的DI是通过容器调用多个参数的构造器来完成的，每个参数表示一个依赖关系。
如果构造参数中不存在歧义，那么在bean定义构造参数的顺序就是其构造器参数的顺序。对于如下bean：
```java
package x.y;
public class Foo {
    public Foo(Bar bar, Baz baz) {
    // ...
    }
}
```
因为`Foo`构造器的两个参数`Bar`和`Baz`不存在歧义，所以顺序或者类型可以不明确提供。其xml中bean的定义可以写成：
```xml
<bean id="foo" class="x.y.Foo">
    <constructor-arg ref="bar"/>
    <constructor-arg ref="baz"/>
</bean>
<bean id="bar" class="x.y.Bar"/>
<bean id="baz" class="x.y.Baz"/>
```

对于基础类型，Spring不能判断其类型是什么，像`<value>true</value>`，所以我们要提供一些额外的信息。
```java
package examples;
public class ExampleBean {
    ...
    public ExampleBean(int years, String ultimateAnswer) {
        ...
    }
}
```
上述例子中，我们可以使用`type`属性提供类型：
```xml
<bean id="exampleBean" class="examples.ExampleBean">
    <constructor-arg type="int" value="7500000"/>
    <constructor-arg type="java.lang.String" value="42"/>
</bean>
```
还可以使用`index`属性来指定参数的顺序，`index` 是从0开始的:
```xml
<bean id="exampleBean" class="examples.ExampleBean">
    <constructor-arg index="0" value="7500000"/>
    <constructor-arg index="1" value="42"/>
</bean>
```
还可以通过指定构造参数的名字来消除歧义:
```xml
<bean id="exampleBean" class="examples.ExampleBean">
    <constructor-arg name="years" value="7500000"/>
    <constructor-arg name="ultimateAnswer" value="42"/>
</bean>
```

### 基于 Setter 来构造bean
基于 Setter 来构造bean是通过容器调用无参构造或者无参静态工厂方法构造出bean后，通过调用setter方法注入属性实现的。


