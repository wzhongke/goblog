---
title: Hibernate学习笔记
date: 2017-07-23 17:16:32
tags: ["java"]
categories: ["java"]
draft: true
---

Hibernate 是一个高性能的对象/关系型持久化存储和查询的工具。Hibernate 不仅关注于从 Java 类到数据库表的映射（也有 Java 数据类型到 SQL 数据类型的映射），另外也提供了数据查询和检索服务。
<!-- more -->
# 术语
1. [JPA](http://baike.sogou.com/v2503460.htm?fromTitle=JPA): Java Persistence API的简称，中文名Java持久层API。是JDK 5.0注解或XML描述对象－关系表的映射关系，并将运行期的实体对象持久化到数据库中

# Hibernate关键类
1. `SessionFactory`是一个线程安全的、不可更改的，表示数据库和应用domain映射关系。`SessionFactory`的创建代价非常高，对于给定的数据库，相应的`SessionFactory`只应该有一个，可以使用单例模式。
2. `Session`: 一个单线程，短存活的实例。它封装了JDBC。它维护一个“可重复读”的一级缓存。
3. `Transaction`： 事务
4. 使用hibernate5时，resin4不支持@Table，可以将@Entity的name属性换成表名，删除resin的lib目录下可能有冲突的jar包
# 持久类 (domain model)
低版本的Hibernate使用XML映射文件来完成从数据库到Java Bean的映射。随着JPA的兴起和完善，Hibernate开始推荐使用JAP注解来完成映射。对于JPA不支持的特性，Hibernate有自己扩展的注解。
## 映射类型
Hibernate能够表示应用程序数据的Java和JDBC的表示。Hibernate的`type`功能是从数据库中读写数据，该`type`是实现了`org.hibernate.type.Type`接口的类。Hibernate的`type`也定义了如何比较相等，如何复制值等一系列功能。Hibernate的`type`实现了Java类型和SQL数据类型的相互转换。
### 值类型
值类型是没有生命周期的数据，为有生命周期实体类型所有。包含Java的基本类型，嵌套类型和集合类型
### 实体类型
实体是使用唯一标识符与数据库表中的行相关联的域模型类。 由于需要一个唯一的标识符，实体独立存在并定义自己的生命周期。`Contact`类本身就是一个实体的例子。

```sql
create table Contact (
    id integer not null,
    first varchar(255),
    last varchar(255),
    middle varchar(255),
    notes varchar(255),
    starred boolean not null,
    website varchar(255),
    primary key (id)
)
```
```java
@Entity(name = "Contact")
public static class Contact {
    @Id
    private Integer id;
    private Name name;
    private String notes;
    private URL website;
    private boolean starred;
    //Getters and setters are omitted for brevity
}

@Embeddable
public class Name {
    private String first;
    private String middle;
    private String last;
    // getters and setters omitted
}

```

## 命名策略
hibernate经过两步处理将对象名映射为数据库的表名：
- 通过用户指定（`@Column`或`@Table`）或者hibernate自定的策略来生成一个逻辑名称；
- 将逻辑名称映射到数据库的表名，策略为实现`PhysicalNamingStrategy`的接口
> JPA定义的逻辑名就是数据表名
>
### 隐式命名策略

当实体类型没有指定其对应的数据库表名时，Hibernate会采用默认的策略解析出表名，实体属性也类似。
![](http://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/images/domain/naming/implicit_naming_strategy_diagram.svg)
我们可以通过设定`hibernate.implicit_naming_strategy`来指定使用哪种默认的命名策略：
- `default`或`jpa`采用的命名策略是`org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl`
- `legacy-hbm`: Hibernate的命名策略
- 指定一个实现`org.hibernate.boot.model.naming.ImplicitNamingStrategy`接口的命名策略
### PhysicalNamingStrategy
许多组织定义关于数据库对象（表，列，外键等）的命名。 PhysicalNamingStrategy的想法是帮助实现这样的命名规则，而不必通过显式名称将它们硬编码到映射中。
```java
public class MyPhysicalNamingStrategy implements PhysicalNamingStrategy {
    private static final Map<String,String> ABBREVIATIONS = buildAbbreviationMap();

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        // Acme naming standards do not apply to catalog names
        return name;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        // Acme naming standards do not apply to schema names
        return null;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        final List<String> parts = splitAndReplace( name.getText() );
        return jdbcEnvironment.getIdentifierHelper().toIdentifier(
                join( parts ),
                name.isQuoted()
        );
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        final LinkedList<String> parts = splitAndReplace( name.getText() );
        // Acme Corp says all sequences should end with _seq
        if ( !"seq".equalsIgnoreCase( parts.getLast() ) ) {
            parts.add( "seq" );
        }
        return jdbcEnvironment.getIdentifierHelper().toIdentifier(
                join( parts ),
                name.isQuoted()
        );
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        final List<String> parts = splitAndReplace( name.getText() );
        return jdbcEnvironment.getIdentifierHelper().toIdentifier(
                join( parts ),
                name.isQuoted()
        );
    }

    private static Map<String, String> buildAbbreviationMap() {
        TreeMap<String,String> abbreviationMap = new TreeMap<> ( String.CASE_INSENSITIVE_ORDER );
        abbreviationMap.put( "account", "acct" );
        abbreviationMap.put( "number", "num" );
        return abbreviationMap;
    }

    private LinkedList<String> splitAndReplace(String name) {
        LinkedList<String> result = new LinkedList<>();
        for ( String part : StringUtils.splitByCharacterTypeCamelCase( name ) ) {
            if ( part == null || part.trim().isEmpty() ) {
                // skip null and space
                continue;
            }
            part = applyAbbreviationReplacement( part );
            result.add( part.toLowerCase( Locale.ROOT ) );
        }
        return result;
    }

    private String applyAbbreviationReplacement(String word) {
        if ( ABBREVIATIONS.containsKey( word ) ) {
            return ABBREVIATIONS.get( word );
        }

        return word;
    }

    private String join(List<String> parts) {
        boolean firstPass = true;
        String separator = "";
        StringBuilder joined = new StringBuilder();
        for ( String part : parts ) {
            joined.append( separator ).append( part );
            if ( firstPass ) {
                firstPass = false;
                separator = "_";
            }
        }
        return joined.toString();
    }
}


```
spring中指定方式
```xml
<bean id="sessionFactory" class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">
    <property name="dataSource" ref="c3p0DataSource"/>
    <property name="configLocation" value="/hibernate.cfg.xml"/>
    <property name="implicitNamingStrategy">
        <bean class="hibernate.namestrategy.UmisImplicitNamingStrategy" />
    </property>
    <property name="physicalNamingStrategy">
        <bean class="hibernate.namestrategy.UmisPhysicalNamingStrategy" />
    </property>
</bean>
```
## 基本类型
通常，基本值类型将单个数据库列映射到单个非聚合Java类型。Hibernate提供了一些内置的基本类型，当Hibernate使用基本类型注册表解析特定的`org.hibernate.type.Type`。
[点此进入Hibernate基本类型映射表](http://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#basic-provided)

1. `@Basic`注解：基本类型默认是被`javax.persitence.Basic`注解的；`@Basic`定义了两个属性
- `optional(defaults to true)`:标明该元素是否可以为空；
- `fetch(EAGER,LAZY)`：hibernate会忽略该属性。标明是否需要懒加载
2. `@Column(name="")`注解：对于基本类型属性，隐式命名规则将列名设为属性名，如果不能满足需求，可以通过该注解告诉Hibernate使用什么列名。
## 基本类型注册器
对于没有指定的类型，Hibernate使用`org.hibernate.type.BasicTypeRegistry`来决定使用哪种类型。`org.hibernate.type.BasicTypeRegistry`维护了一个`org.hibernate.type.BasicType`的map的类型注册器。
如果Hibernate选择的是不符合需求的`BasicType`，我们可以通过`org.hibernate.annotations.Type`注解来指定一个`BasicType`.
```java
@Entity(name = "Product")
public class Product {

    @Id
    private Integer id;

    private String sku;

    @org.hibernate.annotations.Type( type = "nstring" )
    private String name;

    @org.hibernate.annotations.Type( type = "materialized_nclob" )
    private String description;
}
```
### 自定义 `BasicType`
自定义`BasicType`有两种方式：
- 实现`BasicType`接口并且注册该实例
- 实现`UserType`接口，该方式不需要注册
我们将`java.util.BitSet`映射为`VARCHAR`的例子，说明两种方式：
```java
public class BitSetType
        extends AbstractSingleColumnStandardBasicType<BitSet>
        implements DiscriminatorType<BitSet> {

    public static final BitSetType INSTANCE = new BitSetType();

    public BitSetType() {
        super( VarcharTypeDescriptor.INSTANCE, BitSetTypeDescriptor.INSTANCE );
    }

    @Override
    public BitSet stringToObject(String xml) throws Exception {
        return fromString( xml );
    }

    @Override
    public String objectToSQLString(BitSet value, Dialect dialect) throws Exception {
        return toString( value );
    }

    @Override
    public String getName() {
        return "bitset";
    }

}
```
`AbstractSingleColumnStandardBasicType `需要一个`sqlTypeDescriptor`和一个`javaTypeDescriptor`。因为`sqlTypeDescriptor`是数据类型的`VarcharTypeDescriptor.INSTANCE`，所以我们只需要一个`BitSetTypeDescriptor`将`String`类型转换为`BitSet`类型：
```java
public class BitSetTypeDescriptor extends AbstractTypeDescriptor<BitSet> {

    private static final String DELIMITER = ",";

    public static final BitSetTypeDescriptor INSTANCE = new BitSetTypeDescriptor();

    public BitSetTypeDescriptor() {
        super( BitSet.class );
    }

    @Override
    public String toString(BitSet value) {
        StringBuilder builder = new StringBuilder();
        for ( long token : value.toLongArray() ) {
            if ( builder.length() > 0 ) {
                builder.append( DELIMITER );
            }
            builder.append( Long.toString( token, 2 ) );
        }
        return builder.toString();
    }

    @Override
    public BitSet fromString(String string) {
        if ( string == null || string.isEmpty() ) {
            return null;
        }
        String[] tokens = string.split( DELIMITER );
        long[] values = new long[tokens.length];

        for ( int i = 0; i < tokens.length; i++ ) {
            values[i] = Long.valueOf( tokens[i], 2 );
        }
        return BitSet.valueOf( values );
    }

    @SuppressWarnings({"unchecked"})
    public <X> X unwrap(BitSet value, Class<X> type, WrapperOptions options) {
        if ( value == null ) {
            return null;
        }
        if ( BitSet.class.isAssignableFrom( type ) ) {
            return (X) value;
        }
        if ( String.class.isAssignableFrom( type ) ) {
            return (X) toString( value);
        }
        throw unknownUnwrap( type );
    }

    public <X> BitSet wrap(X value, WrapperOptions options) {
        if ( value == null ) {
            return null;
        }
        if ( String.class.isInstance( value ) ) {
            return fromString( (String) value );
        }
        if ( BitSet.class.isInstance( value ) ) {
            return (BitSet) value;
        }
        throw unknownWrap( value.getClass() );
    }
}
```
当传递一个`BitSet`作为参数传递到`PreparedStatement`时会调用`unwrap`方法，当将JDBC列转换成映射的类型时，会调用`wrap`方法。
`BasicType`需要在启动的时候，注册：
```java
configuration.registerTypeContributor( (typeContributions, serviceRegistry) -> {
    typeContributions.contributeType( BitSetType.INSTANCE );
} );
// 或者通过MetadataBuilder
ServiceRegistry standardRegistry =
    new StandardServiceRegistryBuilder().build();
MetadataSources sources = new MetadataSources( standardRegistry );
MetadataBuilder metadataBuilder = sources.getMetadataBuilder();
metadataBuilder.applyBasicType( BitSetType.INSTANCE );
```
`BitSetType`注册的名字是`bitset`（实例的`getName`方法定义），所以使用的话如下：
```java
@Type( type = "bitset" )
private BitSet bitSet;
```

实现`UserType`接口：
```java
public class BitSetUserType implements UserType {

	public static final BitSetUserType INSTANCE = new BitSetUserType();

    private static final Logger log = Logger.getLogger( BitSetUserType.class );

    @Override
    public int[] sqlTypes() {
        return new int[] {StringType.INSTANCE.sqlType()};
    }

    @Override
    public Class returnedClass() {
        return String.class;
    }

    @Override
    public boolean equals(Object x, Object y)
			throws HibernateException {
        return Objects.equals( x, y );
    }

    @Override
    public int hashCode(Object x)
			throws HibernateException {
        return Objects.hashCode( x );
    }

    @Override
    public Object nullSafeGet(
            ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        String columnName = names[0];
        String columnValue = (String) rs.getObject( columnName );
        log.debugv("Result set column {0} value is {1}", columnName, columnValue);
        return columnValue == null ? null :
				BitSetTypeDescriptor.INSTANCE.fromString( columnValue );
    }

    @Override
    public void nullSafeSet(
            PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if ( value == null ) {
            log.debugv("Binding null to parameter {0} ",index);
            st.setNull( index, Types.VARCHAR );
        }
        else {
            String stringValue = BitSetTypeDescriptor.INSTANCE.toString( (BitSet) value );
            log.debugv("Binding {0} to parameter {1} ", stringValue, index);
            st.setString( index, stringValue );
        }
    }

    @Override
    public Object deepCopy(Object value)
			throws HibernateException {
        return value == null ? null :
            BitSet.valueOf( BitSet.class.cast( value ).toLongArray() );
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value)
			throws HibernateException {
        return (BitSet) deepCopy( value );
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
        return deepCopy( cached );
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
        return deepCopy( original );
    }
}
```
### 枚举类型
使用`@Enumerated`和`@MapKeyEnumerated`注解来将Java中的枚举类型映射到数据库中。可以根据`javax.persistence.EnumType`来使用不同的策略完成映射。
- `ORDINAL`: 根据枚举类中枚举值的位置来映射，从0开始，若为`null`，则映射为`NULL`
- `STRING`: 存储枚举值的名字
```java
@Enumerated(EnumType.ORDINAL)
@Column(name = "phone_type")
private PhoneType type;
```

还可以使用`AttributeConverter`来映射指定的字符串。当然了，也可以使用自定义基本类型来转换，比如继承`AbstractSingleColumnStandardBasicType`。下例中，`Gender`枚举类就映射了`M`和`F`:
```java
@Convert(converter = GenderConverter.class)
private Gender gender;
 
public static enum Gender {
    MALE( 'M' ),
    FEMALE( 'F' );
    private final char code;
    Gender(char code) {
        this.code = code;
    }

    public static Gender fromCode(char code) {
        if ( code == 'M' || code == 'm' ) {
            return MALE;
        }
        if ( code == 'F' || code == 'f' ) {
            return FEMALE;
        }
        throw new UnsupportedOperationException(
                "The code " + code + " is not supported!"
        );
    }
    public char getCode() {
        return code;
    }
}

@Converter
public static class GenderConverter implements AttributeConverter<Gender, Character> {

    @Override
    public Character convertToDatabaseColumn(Gender gender) {
        if (gender == null) return null;
        return gender.getCode();
    }

    @Override
    public Gender convertToEntityAttribute(Character character) {
        if (character == null) return null;
        return Gender.fromCode(character);
    }
}
```
### 映射时间类型
对于`java.util.Date`类型：
```
@Temporal(TemporalType.DATE)
private Date timestamp;    //'2017-07-22'

@Temporal(TemporalType.TIME)
private Date timestamp;   // '16:51:58'

@Temporal(TemporalType.TIMESTAMP)
private Date timestamp;  // '2017-07-22 16:51:58'
```
对于`java.time.*`类型，不用做转换的注解，因为他们是一一对应的。
如果不指定时区，JDBC会使用默认的JVM时区，这在有些应用上是不合适的。可以通过如下方式设置时区：
```
java -Duser.timezone=UTC
```
```java
TimeZone.setDefault( TimeZone.getTimeZone( "UTC" ) );
```
```xml
<property name="hibernate.jdbc.time_zone" value="UTC"/>
```