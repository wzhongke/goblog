---
title: spring 学习笔记
date: 2018-01-21 16:00:00
tags: ["java"]
---

IoC (Inversion of Control), 也叫 DI (Dependency injection)，对象通过构造函数参数，工厂方法参数或者在工厂方法构造或返回的实例上设置属性来定义他们之间的依赖关系，容器在创建bean时，注入这些依赖。


# Bean
Spring中Bean的定义使用接口 `BeanDefinition` 表示的，它具有如下的属性：
- 一个class名
- 表明bean在容器中的行为 (scope, lifecycle callbacks等)
- 依赖的bean

## 命名 Bean
每个 Bean 都有一个或多个标识，这些标识在 Bean 的容器中必须是唯一的。在基于XML的配置中，可以用 `id` 或者 `name` 属性来指定 Bean 的标识。`id` 属性只可以指定一个，`name` 可以为 Bean 指定多个标识，通过 `,`, `;` 或者空格分隔。

如果 Bean 的定义中，没有指定name或者id，容器会自动为 Bean 生成一个标识。 Bean 的命名惯例同Java变量类似，采用驼峰式。

有时候需要为其他地方定义的bean引入一个别名。 通常，在大型系统中每个子系统都有自己的一组对象定义，需要使用别名。可使用如下方式引入别名：
```xml
<!-- fromName 是其他Bean定义中的名字， toName 是在本定义中的名字 -->
<alias name="fromName" alias="toName">
```

如果要定义一个内部静态类的 Bean，需要用 `$` 符号分隔类和内部类： `com.example.Foo$Bar`

## 构造器初始化 Bean
使用构造器初始化Bean，一般情况下需要提供一个无参构造器。如果 `Foo` 有一个有参构造器，如下：
```java
package examples.instantiating;
public class ExampleBean {
	/** Number of years to calculate the Ultimate Answer */
	private int years;
	/**  The Answer to Life, the Universe, and Everything */
	private String ultimateAnswer;
	public ExampleBean(int years, String ultimateAnswer) {
		this.years = years;
		this.ultimateAnswer = ultimateAnswer;
	}
}

```
我们可以用类型匹配方式构造 `ExampleBean`

```xml
<bean id="exampleBean" class="examples.instantiating.ExampleBean">
    <constructor-arg type="int" value="75000"/>
    <constructor-arg type="java.lang.String" value="42" />
</bean>
```

## 静态工厂方法初始化 Bean
使用静态工厂方法初始化 Bean 时，需要在 `class` 指定含有静态工厂方法的类，并在 `factory-method` 属性中指定工厂方法的名字。
对于含有静态方法的类 `StaticFactoryMethod` :
```java
package examples.instantiating;
public class StaticFactoryMethod {
	private static StaticFactoryMethod instance = new StaticFactoryMethod();

	private StaticFactoryMethod() {}

	public static StaticFactoryMethod createInstance() {
		return instance;
	}
}
```

那么它的XML定义的Bean为：
```xml
 <bean id="staticFactoryMethod" 
    class="examples.instantiating.StaticFactoryMethod" 
    factory-method="createInstance" />
```

## 非静态工厂方法初始化 Bean
非静态工厂方法初始化 Bean 时，需要使用容器中已有的 Bean。如下
```java
package examples.instantiating;

public class NonStaticFactoryMethod {

	private NonStaticFactoryMethod instance1 = new NonStaticFactoryMethod();
	private NonStaticFactoryMethod instance2 = new NonStaticFactoryMethod();

	public NonStaticFactoryMethod getInstance1() {
		return instance1;
	}

	public NonStaticFactoryMethod getInstance2() {
		return instance2;
	}
}
```

XML 配置如下：
```xml
<bean id="nonStaticFactoryMethod" class="examples.instantiating.NonStaticFactoryMethod" />
<bean id="service1" factory-bean="nonStaticFactoryMethod" factory-method="getInstance1" />
<bean id="service2" factory-bean="nonStaticFactoryMethod" factory-method="getInstance2" />
```

# 依赖注入
依赖注入主要有两种方式：基于构造器的依赖注入 和 基于 Setter 的依赖注入

## 基于构造器的依赖注入
对于类 `ExampleBean`，除了使用类型匹配注入方式，还可以采用以下方式。

可以使用参数位置匹配方式构造 `ExampleBean`
```xml
 <!-- 构造器实例化 bean - 参数位置方式匹配 -->
<bean id="exampleBean2" class="examples.instantiating.ExampleBean">
    <constructor-arg index="0" value="75000"/>
    <constructor-arg index="1" value="42" />
</bean>
```

还可以使用参数名匹配方式，使用该方式需要在启用debug flag的情况下编译代码，否则需要在在 `ExampleBean` 的构造器上加上注解：`@ConstructorProperties({"years", "ultimateAnswer"})`
```xml
<!-- 构造器实例化 bean - 参数名方式匹配 -->
<bean id="exampleBean3" class="examples.instantiating.ExampleBean">
    <constructor-arg name="years" value="7500000"/>
    <constructor-arg name="ultimateAnswer" value="42"/>
</bean>
```

## 基于 Setter 的依赖注入
基于 Setter 的依赖注入是容器在调用无参构造器或者无参静态工厂方法后，再调用setter方法将属性注入到Bean中。
```xml
<!-- 基于 setter 方式的注入 -->
<bean id="exampleBean4" class="examples.instantiating.ExampleBean">
    <property name="years" value="750000"/>
    <property name="ultimateAnswer" value="44"/>
</bean>
```

因为我们可以混合使用构造器注入和setter注入，所以比较好的方式是：必须的依赖使用构造器注入；可选的依赖用setter注入。

**singleton-scope 的Bean会在容器创建的时候创建，同web.xml的`<load-on-start>1</load-on-start>`**

> 如果两个用构造方式注入的类有循环依赖，那么会抛出 `BeanCurrentlyInCreationException` 异常，使用setter方式注入，不会有循环依赖的问题。

我们可以用如下方式简化依赖注入：
```xml
<!-- 基于 setter 方式的注入 -->
<bean id="exampleBean5" class="examples.instantiating.ExampleBean"
        p:years="7000"
        p:ultimateAnswer="45">
</bean>
```

### `idref` 元素
`idref`标签允许容器在部署阶段验证引用的bean是真实存在的。使用 `<property name=".." value="...">` 在部署阶段不会做验证。假如`exampleBean6`是`prototype`类型，那么可能会在部署之后才会抛出异常。
```xml
<bean id="targetBean" class="examples.instantiating.ExampleRef"/>

<bean id="exampleBean6" class="examples.instantiating.ExampleBean">
	<property name="ref">
		<idref bean="targetBean" />
	</property>
</bean>
```

### `ref` 其他bean的引用
`ref` 是 `<constructor-arg/>` 或 `<property/>`中的元素，可以指定该bean引用的其他bean。
```xml
    <bean id="exampleBean7" class="examples.instantiating.ExampleBean">
        <property name="ref">
            <ref bean="targetBean"/>
        </property>
    </bean>
```

### 内部bean
我们可以把 `<bean />` 元素写到 `<property />` 或者 `<constructor-arg>` 元素中：
```xml
<bean id="outer" class="examples.instantiating.ExampleBean">
	<property name="target">
		<bean class="examples.instantiating.ExampleRef" />
	</property>
</bean>
```

内部bean没有必要定义id或者name属性，即使定义了，容器也会忽略它们。

## 集合 `Collections`
一个map的key或者value，一个set的值，可以通过下列之一为其赋值：
`bean ref idref list set map props value null`
集合中使用的Javabean是：
```java
public class ComplexObject {

    private Properties adminEmails;

    private List someList;

    private Map someMap;

    private Set someSet;

    private List nullList;
}
```

如果没有类型限制，那么可以向集合中注入任意的类型。如果加上类型限制后，只能向集合中注入相应的类型。比如：`List<String> someList` 只能注入 `String` 类型的值。


### 集合合并
Spring支持集合的合并，一个集合可以定义一个`<list/>`, `<map/>`, `<set/>` 或者 `<props>`，然后子类可以通过继承来合并父类中的集合
```xml
<!-- 合并集合 -->
<bean id="parent" abstract="true" class="examples.instantiating.ComplexObject">
    <property name="adminEmails">
        <props>
            <prop key="administrator">adminstrator@example.com</prop>
            <prop key="support">support@example.com</prop>
        </props>
    </property>
</bean>
<!-- child 的 adminEmails 中的元素为：
    administrator=administrator@example.com
    sales=sales@example.com
    support=support@example.com
-->
<bean id="child" parent="parent">
    <property name="adminEmails">
        <!-- the merge is specified on the child collection definition -->
        <props merge="true">
            <prop key="sales">sales@example.com</prop>
            <prop key="support">support@example.co.uk</prop>
        </props>
    </property>
</bean>
```

```xml
<bean id="moreComplexObject" class="example.ComplexObject">
    <!-- results in a setAdminEmails(java.util.Properties) call -->
    <property name="adminEmails">
        <props>
            <prop key="administrator">administrator@example.org</prop>
            <prop key="support">support@example.org</prop>
            <prop key="development">development@example.org</prop>
        </props>
    </property>
    <!-- results in a setSomeList(java.util.List) call -->
    <property name="someList">
        <list>
            <value>a list element followed by a reference</value>
            <ref bean="myDataSource" />
        </list>
    </property>
    <!-- results in a setSomeMap(java.util.Map) call -->
    <property name="someMap">
        <map>
            <entry key="an entry" value="just some string"/>
            <entry key ="a ref" value-ref="myDataSource"/>
        </map>
    </property>
    <!-- results in a setSomeSet(java.util.Set) call -->
    <property name="someSet">
        <set>
            <value>just some string</value>
            <ref bean="myDataSource" />
        </set>
    </property>
    <!-- 注入 null -->
    <property name="nullList">
        <null/>
    </property>
</bean>
```

### `depends-on` 属性
如果一个bean对于其他bean是有依赖的，但是这个依赖不能通过 `<ref/>` 等方式在配置中体现出来，那么可以使用 `depends-on` 属性来明确指定依赖：
```xml
<bean id="dependsObject" class="examples.instantiating.ComplexObject" depends-on="exampleBean5">
    <property name="someSet">
        <set>
            <ref bean="exampleBean5" />
        </set>
    </property>
</bean>
```

### 懒初始化 bean
默认情况下，容器会在初始化时创建所有的 `singleton` bean。可以通过使用`lazy-init="ture"` 来阻止容器在启动时初始化该bean，这时容器会在该bean第一次被请求时创建。

### 自动注入 Autowiring
Spring 容器可以自动注入引用的bean，有如下优点：
- 自动注入可以很大程度上减少指定属性或者构造器参数
- 当更新对象的引用时，不用修改配置

使用XML配置时，可以通过指定 `<bean/>` 标签的 `autowire` 属性来定义自动注入模式，注入模式有如下 4 种：

模式        | 说明
:----------|:----------
no         | (默认)不自动注入。bean的引用必须用`ref`定义。对于大型应用，建议使用默认值，因为这样能够更清晰和明确地指定对象的引用。
byName     | 根据属性名注入，Spring为要注入的属性寻找与属性名相同的bean，然后将其注入。
byType     | 容器中有且仅有一个bean的名字同属性名一致，才注入属性。如果多于1个，则会抛出异常并终止程序；如果没有匹配的bean，则不设置属性
constructor| 与 `byType` 类似，只是它被用在构造参数上

自动注入也有些限制：
- 在`property` 和 `constructor-arg` 中明确指定依赖会覆盖自动注入的依赖。自动注入不能够注入基本类型、`String`类型和 `Classes`
- 自动注入不如明确注入精确。
- Spring容器生成的文档可能不会包含自动注入的信息
- setter方法或者构造器参数可能会匹配到多个符合条件的bean，Spring容器会在这种情况下抛出异常。

在Spring的XML中，设置`<bean />` 的 `autowire-candidate` 属性为 `false`，容器会将改bean在自动注入中不可用。

### 方法注入
容器中的大多数bean是 singletons 的。当一个 singleton 的bean 引用另一个 singleton 的bean，或者一个非 singleton 的bean 引用另一个非 singleton 的bean，可以将一个bean设置为另一个的属性。但是两个bean的生命周期不同时，就会有问题。假如一个 singleton 的bean A 需要使用一个非 singleton 的bean B，容器只会在初始化的时候创建一个A，并将B注入到A中，而不会每次使用A时，将一个新的B注入到A中。

一个解决方案是放弃部分IoC，让A实现容器的 `ApplicationContextAware` 接口使得容器知道，每次A在调用B时，都会返回一个B的新的实例。
```java
public class MethodInjection implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	public void process () {
		ExampleBean bean = createExampleBean();
		// do something
	}

	protected ExampleBean createExampleBean () {
		// notice the Spring API dependency!
		return this.applicationContext.getBean("exampleBean4", ExampleBean.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
```

以上方式明显依赖了Spring容器中的类，我们还可以使用 Lookup 方法注入。Spring框架使用CGLIB库生成bytecode来动态生成覆盖了对应方法的子类来实现方法注入。使用该方法时，对应的bean不能是 `final`，覆盖的方法也不能是 `final` 的。
该方式不能用在工厂方法中，`@Bean`方式也不会生效。

```java
public class MethodInjectionWithLookup {

	public void process () {
		ExampleBean bean = createExampleBean();
		System.out.println(bean);
		// do something
	}

    // 1. 使用xml注入
	protected ExampleBean createExampleBean () {
		// notice the Spring API dependency!
		return null;
	}

    // 2. 使用 Lookup 注解
	@Lookup("exampleBeanInject")
	protected ExampleBean createExampleBean1 () {
		// notice the Spring API dependency!
		return null;
	}
}
```

使用方式如下：
```xml
<bean id="exampleBeanInject" class="examples.instantiating.ExampleBean" scope="prototype">
    <property name="years" value="750000"/>
    <property name="ultimateAnswer" value="44"/>
</bean>

<bean id="methodInjection" class="examples.instantiating.MethodInjectionWithLookup">
    <lookup-method name="createExampleBean" bean="exampleBeanInject" />
</bean>
```

还使用lookup method injection 实现将容器管理的bean的任意方法替换为另一个方法。详见Spring的[说明文档](https://docs.spring.io/spring/docs/5.0.2.RELEASE/spring-framework-reference/core.html#beans-factory-arbitrary-method-replacement)

## Bean 生命周期
使用Spring容器不仅可以控制依赖，还可以控制所创建的bean的生命周期，Spring提供了6种生命周期，其中4种专门用于Web服务。

 Scope       | 说明
:------------|:-------
singleton    | (默认)，每个Spring IoC容器中一个bean定义对应一个对象实例
prototype    | 一个bean定义对应任意数量的对象实例，每次请求引用bean A 的对象都会创建一个新的A的实例
request      | 每个HTTP request 都有一个根据bean定义所创建的对象实例，即对象实例的生命周期同 request 的生命周期相同
session      | 对象实例同 HTTP Session 具有相同的生命周期
application  | 对象实例同 `ServletContext` 具有相同的生命周期
websocket    | 对象实例同 `WebSocket` 具有相同的生命周期

对于prototype bean 来说，容器只负责初始化配置组装一个 prototype对象实例，然后将实例交给 client，之后就不再跟踪该对象。因此，如果配置了 `destruction` 回调属性，是不生效的。可以使用 post-processor 来处理销毁对象实例的需求。

### 使用AOP代理将短生命周期的bean注入到长生命周期的bean中
我们需要通过注入一个代理对象的方式实现将短生命周期的对象实例注入到较长生命周期的bean中，该代理对象需要具有与较短生命周期相同的公共接口，并把方法调用映射到实际对象上。
该方式一般是通过CGLIB代理来生成代理对象的，该方式只能代理`public`方法调用，非 `public` 方法调用不能委托到真正的对象上。

```xml
<!-- 使用代理的方式 -->
<!-- Scoped beans as dependencies -->
<!-- an prototype-scoped bean exposed as a proxy -->
<bean id="scopedBean" class="examples.instantiating.ExampleBean" scope="prototype">
    <property name="years" value="750000"/>
    <property name="ultimateAnswer" value="44"/>
    <aop:scoped-proxy />
</bean>

<!-- a singleton-scoped bean injected with a proxy to the above bean -->
<bean id="proxy" class="examples.instantiating.ScopeProxy" scope="singleton">
    <property name="bean" ref="scopedBean" />
</bean>
```

测试方法如下：
```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:dispatcher-servlet.xml"})
public class ScopeProxyTest {

	@Autowired
	private ScopeProxy proxy;

	@Test
	public void testProxy () {
		System.out.println(proxy.getBean());
		System.out.println(proxy.getBean());
	}
}
```

### 自定义生命周期
通过实现 `org.springframework.beans.factory.conf.Scope`接口的方式自定义一个生命周期，主要实现如下方法：
```java
public class CustomScope implements Scope {
	/**
	 * 返回作用域范围内的对象
	 */
	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		return null;
	}

	/**
	 * 从作用域范围内移除对象
	 */
	@Override
	public Object remove(String name) {
		return null;
	}

	/**
	 * 对象销毁时，调用其注册的销毁方法
	 */

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
	}

	/**
	 * 获取上下文中的对象
	 */
	@Override
	public Object resolveContextualObject(String key) {
		return null;
	}

	/**
	 * 作用域的标识，不同的作用域返回标识不同
	 */
	@Override
	public String getConversationId() {
		return null;
	}
}
```

可以用java方法在Spring容器中注册自定义的生命周期:
```java
Scope customScope = new CustomScope();
beanFactory.registerScope("custom", customScope);
```

也可以在XML中注册：
```xml
<bean class="org.springframework.beans.factory.config.CustomScopeConfigurer">
    <property name="scopes">
        <map>
            <entry key="thread">
                <bean class="org.springframework.context.support.SimpleThreadScope"/>
            </entry>
        </map>
    </property>
</bean>

<bean id="customScopeBean" class="examples.instantiating.ExampleBean" scope="custom" />
```

## 定制bean的特性
我们可以实现Spring的`InitializingBean` 和 `DisposableBean` 接口来管理bean的声明周期。容器在初始化bean之后，会调用`afterPropertiesSet()`方法，在销毁bean之前会调用`destory()`。但这种方式会将程序同Spring容器耦合。还可以使用注解或XML配置方式来达到相同的目的。

```xml
<bean id="initBean" class="examples.instantiating.ExampleBean" init-method="init"/>
```

```java
@PostConstruct
public void init() {
    // do some initialization work
}
```

销毁对象之前的操作也类似：
```xml
<bean id="initBean" class="examples.instantiating.ExampleBean" destroy-method="destroy"/>
```

```java
@PreDestroy
public void destroy() {
    // do some initialization work
}
```

也可以用 `<beans default-init-method="init" default-destroy-method="destroy"></beans>` 方式来扫描所有的bean的 `init()` 方法和`destroy()`方法。

### Aware
当 `ApplicationContext` 创建一个实现了 `org.springframework.context.ApplicationContextAware` 接口的实例时，实例可以获得该 `ApplicationContext`
```java
public class BeanApplicationContextAware implements ApplicationContextAware, BeanNameAware{

	private String name;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		applicationContext.getApplicationName();
	}

	@Override
	public void setBeanName(String name) {
		// 获取到定义的name
		this.name = name;
		System.out.println(name);
	}

	public String getName() {
		return name;
	}
}
```

还可以通过 `applicationContext` 获取文件资源，发布应用事件，访问 `MessageSource`等。
实现 `BeanNameAware` 可以获取到bean定义时的`id`，如果没有`id`则取第一个`name` 属性。

可用的 Aware 接口如下表：

Name                          | 注入的依赖
:-----------------------------|:----------------------
ApplicationContextAware       | `ApplicationContext`
ApplicationEventPublisherAware| `ApplicationEventPublisher`
BeanClassLoaderAware          | `BeanClassLoader`
BeanFactoryAware              | `BeanFactory`
BeanNameAware                 | bean 的名字
BootstrapContextAware         | 只在 JCA 中使用
LoadTimeWeaverAware           | 
MessageSourceAware            | 配置消息策略
NotificationPublisherAware    | Spring JMX 通知发布者
ServletConfigAware            | `ServletConfig`
ServletContextAware           | `ServletContext`

## Bean 继承定义
我们可以在XML中定义继承关系的bean，这样的子类会继承父类的所有属性值：
```xml
<bean id="inheritedTestBean" abstract="true"
        class="org.springframework.beans.TestBean">
    <property name="name" value="parent"/>
    <property name="age" value="1"/>
</bean>

<bean id="inheritsWithDifferentClass"
        class="org.springframework.beans.DerivedTestBean"
        parent="inheritedTestBean" init-method="initialize">
    <property name="name" value="override"/>
    <!-- the age property value of 1 will be inherited from parent -->
</bean>
```

一个子类bean会继承scope，构造器参数值，属性值和从父类继承的方法。

# 注解
注解注入是在XML注入之前执行的，因此XML配置会覆盖注解相同的配置。
```java
public class Annotation {

	private ExampleBean exampleBean;

	/**
	 * `@Resource` 如果没有name属性指定，那么会先寻找name与属性名相同的bean；如果没有，那么就根据类型寻找
	 */
	@Resource
	private ExampleBean noPrimary;

	/**
	 * `@Autowired` 可以注入Map类型，只要map的key是String类型
	 */
	@Autowired
	private Map<String, ExampleBean> beanMap;

	@Autowired
	private Set<ExampleBean> beanSet;

	public Annotation() {}

	/**
	 * `@Autowired` 可以用在构造器上，也可以用在 setter 方法上，也可以用在属性上
	 * `@Autowired` 是根据类型匹配的，默认的required是false
	 */
	@Autowired(required = false)
	public Annotation(@Nullable ExampleBean exampleBean) {
		this.exampleBean = exampleBean;
	}

	/**
	 * `@Required` 注解用在属性的setter方法上，表示该属性必须已经被注入
	 * `@Qualifier()` 注解可以根据bean的name或者id注入，比 `@Primary` 更灵活
	 */
	@Autowired
	@Required
	public void setExampleBean (@Qualifier("no-primary") ExampleBean exampleBean) {
		this.exampleBean = exampleBean;
	}

	public ExampleBean getExampleBean() {
		return exampleBean;
	}

	public ExampleBean getNoPrimary() {
		return noPrimary;
	}
}
```

对应的xml配置为：
```xml
<!-- @Autowired 注解如果有多个匹配的类型，可以使用primary来注入该bean -->
<bean class="examples.instantiating.ExampleBean" primary="true">
	<constructor-arg type="int" value="75000"/>
	<constructor-arg type="java.lang.String" value="42" />
</bean>

<bean class="examples.instantiating.ExampleBean" id="no-primary" name="noPrimary">
	<constructor-arg type="int" value="25000"/>
	<constructor-arg type="java.lang.String" value="42" />
</bean>
```

## 组件扫描
`@Component` 是spring管理组件的通用注解，`@Repository`、`@Service`、`@Controller` 是 `@Component` 的更为具体的注解。`@Repository` 一般用在持久层（DAO）。
在java中定义扫描组件
```java
@ComponentScan(basePackages = "examples",
	includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*Example.*"),
	excludeFilters = {@Filter(Repository.class), @Filter(AppConfig.class)})
public class AppConfig {

}
```

在xml中定义扫描组件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

	<context:component-scan base-package="example">
        <context:include-filter type="regex"
                                expression=".*Example.*"/>
        <context:exclude-filter type="annotation"
                                expression="org.springframework.stereotype.Repository"/>
    </context:component-scan>
</beans>
```

## `@Bean` 
`@Bean` 一般需要在被 `@Configuration` 注解的类中使用，否则就是 lite 状态的Bean。这时候Spring容器对Bean的管理会缺失。

`@Bean`是用在方法上的注解，一般情况下，生成的Bean的名字同方法名一致。