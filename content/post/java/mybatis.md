---
title: mybatis
date: 2017-07-20 16:05:42
categories: ["sql"]
tags: ["sql"]
---

# MyBatis是什么
MyBatis是定制化SQL、存储过程以及高级映射的持久层框架。MyBatis 可以对配置和原生Map使用简单的 XML 或注解，将接口和 Java 的 POJOs(Plain Old Java Objects,普通的 Java对象)映射成数据库中的记录。
<!-- more -->
# 开始入门
## maven导入MyBatis包
在`pom.xml`的`dependencies`下添加如下依赖：
```
<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.4</version>
</dependency>
```

MyBatis是中心是`SqlSessionFactory`实例，该实例通过`SqlSessionFactoryBuilder`获得。`SqlSessionFactoryBuilder`可以通过XML配置或使用`Configuration`构建。
## 使用XML构建`SqlSessionFactory`
XML配置文件包含了MyBatis系统的核心配置，包含数据库连接实例的数据源和决定事务作用域和控制方式的事务管理器。下面是配置中比较关键的部分：
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="${driver}"/>
        <property name="url" value="${url}"/>
        <property name="username" value="${username}"/>
        <property name="password" value="${password}"/>
      </dataSource>
    </environment>
  </environments>
  <mappers>
    <mapper resource="mybatis/mapper/PersonMapper.xml"/>
  </mappers>
</configuration>
```
要注意XML头部的声明，该声明用来验证XML文档正确性。`environment`元素中包含了事务管理和连接池的配置。`mappers`元素包含一组`	mapper`映射器。
我们可以通过Mybatis的一个`Resources`工具类从classpath或其他位置加载配置文件。
```
String resource = "mybatis.xml";
InputStream confStream = Resources.getResourceAsStream(resource);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(confStream);
```
## 不使用XML构建`SqlSessionFactory`
如果不想依赖配置构建程序，那么可以使用Java程序构建configuration，MyBatis提供了完全不用XML配置的类：
```
DataSource dataSource = BlogDataSourceFactory.getBlogDataSource();
TransactionFactory transactionFactory = new JdbcTransactionFactory();
Environment environment = new Environment("development", transactionFactory, dataSource);
Configuration configuration = new Configuration(environment);
configuration.addMapper(BlogMapper.class);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
```
## 从`SqlSessionFactory`中获取`SqlSession`
`SqlSession`中包含了面向数据库执行SQL命令所有的方法，可以通过`SqlSession`实例来直接执行已经映射的SQL语句：
```
try (SqlSession session = sqlSessionFactory.openSession()) {
	Person person = session.selectOne("mybatis.mapper.PersonMapper.selectPerson", 101);
}
```
上例中需要`PersonMapper.xml`配置项：
```
<mapper namespace="mybatis.mapper.PersonMapper">
    <select id="selectPerson" resultType="mybatis.dao.Person" parameterType="int">
        SELECT * FROM Person WHERE id = #{id}
    </select>
</mapper>
```
在一个XML映射文件中，可以定义任意多个映射语句，而且具有很好的自解释性。在命名空间`mybatis.mapper.PersonMapper`中定义了一个名为`selectBlog`的映射语句，这样就可以像上例中那样调用查询语句。
我们还可以不使用XML配置，使用注解来实现同样的功能
```
public interface PersonMapper {
    @Select("SELECT * FROM blog WHERE id = #{id}")
    Person selectPerson(int id);
}
```
对于简单的语句，用Java注解简洁清晰；但是对于复杂的语句，就显得有些混乱，那么可以使用XML映射语句。这两种方式可以自由移植和切换。
## 作用域和生命周期
>如果使用依赖注入的框架，如spring，则可以直接忽略他们的生命周期。因为这些框架可以创建线程安全的、基于事务的`SqlSession`和映射器，并将他们注入到bean中。
### `SqlSessionFactoryBuilder`
该类可以被实例化、使用和丢弃，一旦创建了`SqlSessionFactory`，就可以不再用它了。因此`SqlSessionFactoryBuilder`实例最佳作用域是局部变量。可以重用`SqlSessionFactoryBuilder`来创建多个`SqlSessionFactory`实例。但是不要让其一直存在，避免一直占有资源。
### `SqlSessionFactory`
`SqlSessionFactory`实例一旦创建，就应该在运行期间一直存在，对其清除和重建是浪费资源的。因此`SqlSessionFactory`的最佳作用域是应用级的，可以用单例模式或者静态单例模式创建
### `SqlSession`
每个线程都应该有它自己的 SqlSession 实例。SqlSession 的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。绝对不能将 SqlSession 实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。也绝不能将`SqlSession`实例的引用放在任何类型的管理作用域中。简单概括来说，就是使用之前，创建；使用完之后，马上关闭。`SqlSession`实现了`Closeable`，因此可以使用自动关闭的特性：
```
try (SqlSession session = sqlSessionFactory.openSession()) {
	Person person = session.selectOne("mybatis.mapper.PersonMapper.selectPerson", 101);
}
```
### 映射器实例
映射器是用来绑定映射语句的接口，该实例是从`SqlSession`中获取的，所以映射器的作用域同`SqlSession`相同，其最佳作用域是方法作用域。

# XML映射配置文件
MyBatis配置文件的层次结构如下
```
-- configuration
   -- properties
   -- settings
   -- typeAliases
   -- typeHandlers
   -- objectFactory
   -- plugins
   -- environments
      -- environment
         -- transactionManager
         -- dataSource
   -- databaseIdProvider
   -- mappers
```
## `properties`
这些属性相当于定义的变量，可以在Java属性文件中配置，也可以通过properties元素子元素传递：
```
<properties resource="jdbc.properties">
    <property name="username" value="dev_user"/>
    <property name="password" value="F2Fa3!33TYyg"/>
</properties>
```
中期的属性可以在这个配置文件中使用：
```
<dataSource type="POOLED">
    <property name="driver" value="${driver}"/>
    <property name="url" value="${url}"/>
    <property name="username" value="${username}"/>
    <property name="password" value="${password}"/>
</dataSource>
```
上例中`username`和`password`会由`properties`元素中相应的值来替换，`driver`和`url`属性取自`jdbc.properties`。
如果在多个地方配置了属性，那么会按照下面对顺序来加载：
1. 首先读取`properties`元素体内指定的属性
2. 然后根据`properties`中的`resource`属性读取类路径下属性文件或根据`url`属性指定读取属性文件，并覆盖已读取的同名属性。
3. 最后读取作为方法参数传递的属性，并覆盖已读取的同名属性。
从MyBatis3.4.2开始，可以用占位符指定一个默认值。：
```
<dataSource type="POOLED">
  <property name="username" value="${username:ut_user}"/> <!-- 如果'username'不存在, username 取值为'ut_user' -->
</dataSource>
```
这个特性默认是关闭的，需要在`properties`下，用指定的属性来开启此特性
```
<properties>
<property name="org.apache.ibatis.parsing.PropertyParser.enable-default-value" value="true"/>
</properties>
```
## settings
这是MyBatis中极为重要的调整设置，他们会改变MyBatis的运行时行为：

设置参数		| 描述								   | 取值		|默认值
:-----------|:-------------------------------------|:----------|:-------
cacheEnabled| 该配置影响所有映射器中配置的缓存的全局开关|true/false	| true
lazyLoadingEnabled| 	延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置`fetchType`属性来覆盖该项的开关状态.|true/false| false
aggressiveLazyLoading|当开启时，任何方法的调用都会加载该对象的所有属性。否则，每个属性会按需加载（参考`lazyLoadTriggerMethods`).|true\false|false (true in ≤3.4.1)
multipleResultSetsEnabled|	是否允许单一语句返回多结果集（需要兼容驱动）|	true\false	|true
useColumnLabel|使用列标签代替列名。不同的驱动在这方面会有不同的表现， 具体可参考相关驱动文档或通过测试这两种不同的模式来观察所用驱动的结果|	true\false	|true
useGeneratedKeys|允许 JDBC 支持自动生成主键，需要驱动兼容。 如果设置为 true 则这个设置强制使用自动生成主键，尽管一些驱动不能兼容但仍可正常工作|true/false| false
autoMappingBehavior|指定 MyBatis 应如何自动映射列到字段或属性。 NONE 表示取消自动映射；PARTIAL 只会自动映射没有定义嵌套结果集映射的结果集。 FULL 会自动映射任意复杂的结果集（无论是否嵌套）|NONE,PARTIAL,FULL|PARTIAL
autoMappingUnknownColumnBehavior|指定发现自动映射目标未知列（或者未知属性类型）的行为|NONE, WARNING, FAILING|NONE
defaultExecutorType|配置默认的执行器。SIMPLE 就是普通的执行器；REUSE 执行器会重用预处理语句（prepared statements）； BATCH 执行器将重用语句并执行批量更新|SIMPLE REUSE BATCH|SIMPLE

```xml
<settings>
    <!-- 该配置影响的所有映射器中配置的缓存的全局开关。 -->
    <setting name="cacheEnabled" value="true"/>
    <!-- 延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置fetchType属性来覆盖该项的开关状态。-->
    <setting name="lazyLoadingEnabled" value="false"/>
    <!-- 是否允许单一语句返回多结果集（需要兼容驱动）。 -->
    <setting name="multipleResultSetsEnabled" value="true"/>
    <!-- 使用列标签代替列名。不同的驱动在这方面会有不同的表现， 具体可参考相关驱动文档或通过测试这两种不同的模式来观察所用驱动的结果。 -->
    <setting name="useColumnLabel" value="true"/>
    <!-- 允许 JDBC 支持自动生成主键，需要驱动兼容。 如果设置为 true 则这个设置强制使用自动生成主键，尽管一些驱动不能兼容但仍可正常工作（比如 Derby）。  -->
    <setting name="useGeneratedKeys" value="false"/>
    <!-- 指定 MyBatis 应如何自动映射列到字段或属性。
        NONE 表示取消自动映射；
        PARTIAL 只会自动映射没有定义嵌套结果集映射的结果集。
        FULL 会自动映射任意复杂的结果集（无论是否嵌套）。-->
    <setting name="autoMappingBehavior" value="PARTIAL"/>
    <!-- 指定发现自动映射目标未知列（或者未知属性类型）的行为。
            NONE: 不做任何反应
            WARNING: 输出提醒日志 ('org.apache.ibatis.session.AutoMappingUnknownColumnBehavior' 的日志等级必须设置为 WARN)
            FAILING: 映射失败 (抛出 SqlSessionException) -->
    <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/>
    <!--    配置默认的执行器。
                SIMPLE 就是普通的执行器；
                REUSE 执行器会重用预处理语句（prepared statements）；
                BATCH 执行器将重用语句并执行批量更新。 -->
    <setting name="defaultExecutorType" value="SIMPLE"/>
    <!-- 设置超时时间，它决定驱动等待数据库响应的秒数。(任意正整数) -->
    <setting name="defaultStatementTimeout" value="25"/>
    <!-- 为驱动的结果集获取数量（fetchSize）设置一个提示值。此参数只可以在查询设置中被覆盖。 -->
    <setting name="defaultFetchSize" value="100"/>
    <!-- 允许在嵌套语句中使用分页（RowBounds）。 If allow, set the false. -->
    <setting name="safeRowBoundsEnabled" value="false"/>
    <!-- 是否开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN 到经典 Java 属性名 aColumn 的类似映射。 -->
    <setting name="mapUnderscoreToCamelCase" value="false"/>
    <!-- MyBatis 利用本地缓存机制（Local Cache）防止循环引用（circular references）和加速重复嵌套查询。
       默认值为 SESSION，这种情况下会缓存一个会话中执行的所有查询。
       若设置值为 STATEMENT，本地会话仅用在语句执行上， 对相同 SqlSession 的不同调用将不会共享数据。 -->
    <setting name="localCacheScope" value="SESSION"/>
    <!-- 当没有为参数提供特定的 JDBC 类型时，为空值指定 JDBC 类型。 某些驱动需要指定列的 JDBC 类型，
        多数情况直接用一般类型即可，比如 NULL、VARCHAR 或 OTHER。-->
    <setting name="jdbcTypeForNull" value="OTHER"/>
    <!-- 指定哪个对象的方法触发一次延迟加载。 -->
    <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
    <!-- 指定 MyBatis 增加到日志名称的前缀。 -->
    <setting name="logPrefix" value="mybatis" />
</settings>
```
## typeAliases
类型名是为Java类型设置一个短名字，只和XML配置有关，仅用来减少类完全限定名的冗余。比如：
```xml
<typeAliases>
  <typeAlias alias="Author" type="domain.blog.Author"/>
  <typeAlias alias="Blog" type="domain.blog.Blog"/>
  <typeAlias alias="Comment" type="domain.blog.Comment"/>
  <typeAlias alias="Post" type="domain.blog.Post"/>
  <typeAlias alias="Section" type="domain.blog.Section"/>
  <typeAlias alias="Tag" type="domain.blog.Tag"/>
</typeAliases>
```
这样，就可以在使用`domain.blog.Blog`的地方用`Blog`替换。
还可以通过指定包名，来批量指定别名：
```
<typeAliases>
  <package name="domain.blog"/>
</typeAliases>
```
这样每个在包`domain.blog`中的Java对象，在没有注解的情况下，会使用该对象的首字母小写的类名来作为它的别名。如`domain.blog.Author`的别名为`author`；如果有注解，那么以注解值为准。
```java
@Alias("author")
public class Author {
    ...
}
```
## typeHandlers
无论在处理预处理语句中设置一个参数时，还是从结果集中取出一个值时，都会用到类型处理器将获取的值以合适的方式转换成Java类型。这就是类型处理器。
我们可以重写类型处理器或创建自己的类型处理器来处理不支持的或非标准的类型。具体做法：实现`org.apache.ibatis.type.TypeHandler`接口，或者继承类`org.apache.ibatis.type.BaseTypeHandler`，然后可以选择性地将它映射到一个JDBC类型：
```java
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ExampleTypeHandler extends BaseTypeHandler<String> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, s);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getString(columnName);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex);
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return callableStatement.getString(columnIndex);
    }
}
```
这个类需要在MyBatis的配置文件中配置：
```xml
<typeHandlers>
  <typeHandler handler="org.mybatis.example.ExampleTypeHandler"/>
</typeHandlers>
```
使用这个的类型处理器将会覆盖已经存在的处理`Java`的`String`类型属性和`VARCHAR`参数及结果的类型处理器。
> MyBatis不会探测数据库元信息来决定使用哪种数据类型，所以必须在参数和结果映射中指明那个是`VARCHAR`类型的字段，以便能够绑定到正确的类型处理器上。

通过类型处理器的泛型，MyBatis 可以得知该类型处理器处理的 Java 类型，不过这种行为可以通过两种方法改变：
- 在类型处理器的配置元素（typeHandler element）上增加一个 javaType 属性（比如：javaType="String"）；
- 在类型处理器的类上（TypeHandler class）增加一个 @MappedTypes 注解来指定与其关联的 Java 类型列表。 如果在 javaType 属性中也同时指定，则注解方式将被忽略。

可以通过两种方式来指定被关联的 JDBC 类型：
- 在类型处理器的配置元素上增加一个 jdbcType 属性（比如：jdbcType="VARCHAR"）；
- 在类型处理器的类上（TypeHandler class）增加一个 @MappedJdbcTypes 注解来指定与其关联的 JDBC 类型列表。 如果在 jdbcType 属性中也同时指定，则注解方式将被忽略。

我们还可以让MyBatis自动查找类型处理器，该方式只能通过注解方式来指定JDBC类型：
```xml
<!-- mybatis-config.xml -->
<typeHandlers>
  <package name="org.mybatis.example"/>
</typeHandlers>
```
## 处理枚举类
若想映射枚举类型`Enum`，则需要从`EnumTypeHandler`或者`EnumOrdinalTypeHandler`中选一个来使用。

## 对象工厂
MyBatis 每次创建结果对象的新实例时，它都会使用一个对象工厂（ObjectFactory）实例来完成。 默认的对象工厂需要做的仅仅是实例化目标类，要么通过默认构造方法，要么在参数映射存在的时候通过参数构造方法来实例化。 如果想覆盖对象工厂的默认行为，则可以通过创建自己的对象工厂来实现。比如：
```java
public class ExampleObjectFactory extends DefaultObjectFactory {
  public Object create(Class type) {
    return super.create(type);
  }
  public Object create(Class type, List<Class> constructorArgTypes, List<Object> constructorArgs) {
    return super.create(type, constructorArgTypes, constructorArgs);
  }
  public void setProperties(Properties properties) {
    super.setProperties(properties);
  }
  public <T> boolean isCollection(Class<T> type) {
    return Collection.class.isAssignableFrom(type);
  }
}
```
```xml
<!-- mybatis-config.xml -->
<objectFactory type="org.mybatis.example.ExampleObjectFactory">
  <property name="someProperty" value="100"/>
</objectFactory>
```
## 插件
MyBatis允许你在已映射语句执行过程中的某一个点进行拦截调用。MyBatis允许使用插件拦截的调用有：
- `Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)`
- `ParameterHandler (getParameterObject, setParameters)`
- `ResultSetHandler (handleResultSets, handleOutputParameters)`
- `StatementHandler (prepare, parameterize, batch, update, query)`

假设你想做的不仅仅是监控方法的调用，那么你应该很好的了解正在重写的方法的行为。 因为如果试图修改或重写已有方法，你很可能在破坏 MyBatis的核心模块。 这些都是更低层的类和方法，所以使用插件的时候要特别当心。
使用插件，只需要实现`Interceptor`接口，并指定想要拦截的方法签名即可。
```java
@Intercepts({
    @Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
    )
})
public class ExamplePlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
```
配置文件中需要增加：
```xml
<plugins>
  <plugin interceptor="org.mybatis.example.ExamplePlugin">
    <property name="someProperty" value="100"/>
  </plugin>
</plugins>
```
上面的插件将会拦截在`Executor`实例中所有的 “update” 方法调用，这里的`Executor`是负责执行低层映射语句的内部对象。
## 配置环境
每个数据库应该对应至少一个`SqlSessionFactory`实例。
MyBatis中有两种类型的事务管理器（`type="[JDBC|MANAGED]"`)
- JDBC: 这个配置就是直接使用了JDBC的提交和回滚设置，它依赖于从数据源得到的连接来管理事物作用域
- MANAGED：这个配置将管理事务的整个生命周期的任务交给了容器。默认情况下它会关闭连接，然而一些容器并不希望这样，因此需要将 closeConnection 属性设置为 false 来阻止它默认的关闭行为。例如:
```xml
<transactionManager type="MANAGED">
  <property name="closeConnection" value="false"/>
</transactionManager>
```
> 如果使用 Spring + MyBatis，则没有必要配置事务管理器， 因为 Spring 模块会使用自带的管理器来覆盖前面的配置。
### dataSource 数据源
有三种内建的数据源类型 (`type="[UNPOLLED|POOLED|JNDI]"`)
1. UNPOOLED：每次被请求时打开和关闭连接
2. POOLED: 利用数据库连接池，避免了创建新的连接实例时所需要的初始化和认证时间，有比较多的属性可以配置：
  - `poolMaximumActiveConnections`: 最大连接数量
  - `poolMaximumIdleConnections`: 最大空闲连接数
  - `poolMaximumCheckoutTime`: 连接池中的连接被检出时间
  - `poolTimeToWait`:  这是一个底层设置，如果获取连接花费的相当长的时间，它会给连接池打印状态日志并重新尝试获取一个连接
  - `poolPingQuery`: 发送到数据库的侦测查询，用来检验连接是否处在正常工作秩序中并准备接受请求
  - `poolPingEnabled`: 是否启用侦测查询。默认值`false`，启用是必须设置一个可执行的SQL语句作为`poolPingQuery`的值
  - `poolPingConnectionsNotUsedFor`: 配置 poolPingQuery 的使用频度。
3. JNDI: 在EJB或应用服务器类容器中使用

# Mapper XML映射文件
XML SQL映射文件中有一下几个顶级元素：
- `cache`: 给定命名空间的缓存配置
- `cache-ref`: 其他命名空间缓存配置的引用。
- `resultMap`: 是最复杂也是最强大的元素，用来描述如何从数据库结果集中来加载对象。
- `sql`: 可以被其他语句引用的重用块
- `insert`: 插入语句
- `update`: 更新语句
- `delete`: 删除语句
- `select`: 查询语句
## `select`
查询是数据库操作中应用最频繁的语句。MyBatis简单的`select`非常简单：
```xml

```