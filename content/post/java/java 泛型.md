---
title: java 泛型
date: 2017-09-17 19:42:25
tag: ["java"]
categories: ['java']
---

在java中使用泛型，可以避免使用`Object`或者强制类型转换。泛型最适用于集合类，比如`List`。
使用泛型编写代码增加了其可复用性，可以被许多不同类型的对象使用。举例来说，不想对`String`和`File`的集合分别编程，可以使用`ArrayList`来处理各种类型的集合。

使用泛型编程有三种技术层次：
1. 只知道如何使用泛型，而不知道它们为什么可以这么使用；
2. 当在使用泛型的过程中，遇到一些不能解决的问题，就需要了解泛型的具体使用法则；
3. 可以自己实现泛型和其方法。

**只有那些涉及到很多类型的通用代码，才适合用泛型来处理**。

## 定义泛型类
泛型类是含有一个或多个类型变量（如下例中`T`）的类。
```java
public class Pair<T> {
    private T first;
    private T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst () {return this.first;}
    public T getSecond () {return this.second;}
    public void setFirst(T newValue) { first = newValue; }
    public void setSecond(T newValue) { second = newValue; }
}
```
当然了，我们可以定义多个类型变量，像 `Pair<T,U>`。

## 定义泛型方法
可以在方法上中使用类型参数，该方法即可以在泛型类中定义，也可以在普通类中定义。其中`T`是在修饰符(`public static`)后，在返回类型前。
```java
class ArrayAlg {
    public static <T> T getMiddle(T... a) {
        return a[a.length/2];
    }
}
```
下面的方式避免了返回结果是 `Object`：
```java
public static <T> T convertXmlStrToObject(Class<T> clazz, String xmlStr) {
    // do something
}
```

该方法可以用如下方式调用：
```java
String middle = ArrayAlg.<String>getMiddle("1", "2", "4");
// 因为编译器可以根据参数类型推断出 T 的类型，所以类型参数可以省略
String middle2 = ArrayAlg.getMiddle("1", "2", "4");
```

像上述调用，依靠编译器推断类型时，尽量使用同一类型。 像`Number middle = GenericMethod.getMiddle(3.14, 1729, 0);` 这种调用，编译器会将参数自动装箱成`(Double, Integer, Integer)`, 然后找到这些类型所共有的父类——`Number`或`Comparable`。 所以可以将结果赋给 `Number` 或者 `Comparable`. 否则会报错：
> ```
  Error:(10, 56) java: 不兼容的类型: 推断类型不符合上限
    推断: java.lang.Number&java.lang.Comparable<? extends java.lang.Number&java.lang.Comparable<?>>
    上限: java.lang.Double,java.lang.Object
  ```

## 类型变量的界限
当我们想要寻找数组中最小值时，就需要泛型变量是继承了`Comparable`的，这时候就可以使用如下方法对类型变量进行类型限制。
```java
public static <T extends Comparable> T min (T[] a) {
    if (a == null || a.length == 0) return null;
    T smallest = a[0];
    for (int i=1; i<a.length; i++) {
        if (smallest.compareTo(a[i]) > 0) smallest = a[i];
    }
    return smallest;
}
```
虽然`extends`一般用在类上，而不是接口。但是类型变量界限使用的是关键字`extends`，只是因为它更适合表达子类型概念，这样就不用新增一个关键字来表示类型变量界限。
当类型变量有多个界定，用`&`分割： `T extends Comparable & Serializable`。 类似java中的继承，可以有任意多个接口限定，但是只能有一个类限定。如果有类限定，那么这个类限定的位置必须是界限列表的第一个。

## 泛型代码和java虚拟机
在java虚拟机中并没有泛型类型，所有的对象都是普通的类。编译器会对泛型类或方法进行类型擦除。

### 类型擦除
当定义一个泛型后，对应的原始类型会被虚拟机自动创建。原始类型的类名同其对应的泛型的类型是一致的，只是将类型参数移除，并且用它们的限定类型代替类型参数。
```java
public class Interval<T extends Comparable & Serializable, U> implements Serializable {
    private T lower;
    private T upper;
    private U other;
    public Interval(T first, U second) {
        if (first.compareTo(second) <= 0) { lower = first; upper = second; }
        else { lower = second; upper = first; }
    }
}
```
上列中，类型擦除后，`T` 使用其第一个限定界限`Comparable`代替；`U`因为没有限定，所以用`Object`代替。类型擦除之后的代码如下：
```java
public class Interval implements Serializable {
    private Comparable lower;
    private Comparable upper;
    private Object other;
    public Interval(Comparable first, Obejct second) {
        // do something
    }
}
```

### 泛型表达式的擦除TY
当调用一个泛型方法时，编译器会在返回类型擦除之后，插入类型转换的代码。
```java
Pair<Employee> pairs = new Pair<>();
Employee pair = pairs.getFirst();
```
`getFirst()`方法返回的类型经过类型擦除之后是 `Object`，编译器自动插入了`Employee`的类型转换： `Employee pair = (Employee)pairs.getFirst()`.
当然了，对于直接访问泛型属性，编译器的处理也是相似的。

### 泛型方法的擦除
泛型方法的擦除方式同泛型类类似。 `public static <T extends Comparable> T min (T[] a)`方法擦除后是 `public static Comparable T min(Comparable[] a)`.
但是，当我们在继承泛型类时，只是擦除类型，泛型方法会有问题。例如下面的例子
```java
class DateInterval extends Pair<LocalDate> {
    public void setSecond(LocalDate second) {
        // do something
    }
}
```
上述类执行类型擦除之后，如下：
```java
class DateInterval extends Pair { // after erasure 
    public void setSecond(LocalDate second) {
        // do something
    }
}
```
如果泛型类`Pair<LocalDate>`中，恰好有一个方法，类型擦除后是 `public void setSecond(Object second)`。显然，这两个方法因为参数签名不一致，是两个不同的方法。但是，这两个方式不应该不是一个方法。
```java
Pair<LocalDate> pair = new DateInterval(. . .);; // OK--assignment to superclass
pair.setSecond(aDate);
```
上述方法中，我们想要调用的是`DateInterval`中的`setSecond(LocalDate second)`。但是，类型擦除后，调用的应该会是`setSecond(Object second)`。为了修正这个问题，编译器会为`DateInterval`生成一个桥接(bridge method)方法： `public void setSecond(Object second) { setSecond((Date) second); }`

概括来说，Java中泛型的类型擦除会遵循以下原则：
- 虚拟机中并没有泛型类，只有普通类
- 所有的类型参数都会被其界限替代
- 合成桥接方法以保持多态
- 需要的时候，会插入类型转换

## 使用泛型的限制
大多数的限制是类型擦除导致的。

### 类型参数不能是原始类型
不能将原始类型作为参数类型，只能有`Pair<Double>`，而不能用`Pair<double>`。因为类型擦除之后，`Pair`类是`Object`类型的属性，不能用来存储`double`的值。

### 运行时类型查询仅适用于原始类型
类型查询仅适用于原始类型，如`a instanceof Pair<String>`是错误的，只能判断`a`是否是`Pair`的实例，而不能判断`a`是否是`Pair<String>`类型的。
```java
Pair<String> stringPair = ...;
Pair<Empolyee> employeePair = ...;
stringPair.getClass() == employeePair.getClass() // true, 他们是相等的，getClass都会返回 Pair.class
```

### 不能创建泛型类型的数组
不能实例化泛型类型的数组，`Pair<String>[] table = new Pair<String>[10];` 是错误的。
当上述代码执行类型擦除之后，`table`的类型是`Pair[]`，我们可以将它转换成`Object[]`。
但是，数组会记住其元素的类型，如果存储一个错误类型的元素，会抛出一个`ArraySotreException`异常。 如`objectArr[0]="hello";`
只有数组的创建是不合法的，我们还是可以声明`Pair<String>[]` 类型的数组，但是不能用`new Pair<String>[10]`来将其初始化。
> 可以声明通配符类型数组，然后将其转换成对应的类型
> ```java
> Pair<String>[] table = (Pair<String>[]) new Pair<?>[10];
> ```
> 但是这种方式是不安全的，如果想调用`Pair`中的方法，会抛出`ClassCastException`.
> 如果想用泛型的数组，可以使用`ArrayList`，它是安全并且有效的。

### 可使用可变参方法
Java不支持泛型数组的初始化，但是可以使用可变参数作为方法的参数。代码可以正常运行，但是会有警告。
```java
@SafeVarargs
public static <T> void addAll(T... ts) {
    for (t: ts) {
        this.add(t);
    }
}
```

### 不能实例化泛型变量
不能用`new T()`方法创建实例。这是因为类型擦除会将`T`变成`Object`。显然`new Object()`并不是我们想要的结果。
我们可以通过其他的方式来创建泛型实例：
```java
public static <T> Pair<T> makePair(Class<T> cl){
    try { return new Pair<>(cl.newInstance(), cl.newInstance()); }
    catch (Exception ex) { return null; }
}
// 调用方式如下：
Pair<String> p = Pair.makePair(String.class);
```

### 泛型类的静态变量或方法中不能使用类型变量
我们不能在泛型类中的静态变量或者方法使用类型变量，下面的方式是错误的：
```java
public class Singleton<T> {
    private static T singleInstance; // Error
    public static T getSingleInstance() { // Error
  
    if (singleInstance == null) construct new instance of T
    return singleInstance;
    }
}
```
如果这样的方式是可以的，那么我们可以定义一个`Singleton<Random>`来共享一个随机数生成器，`Singleton<File>`来共享一个文件处理器。但这肯定是不可能的，类型擦除之后，只有一个`Singleton`类和一个`singleInstance`属性。

### 不能抛出或者捕获泛型类的实例
定义一个继承`Throwable`的泛型类是不合法的：
```java
public class GenericException<T> extends Exception {} // Error
```
但是我们可以用在异常处理中使用泛型：
```java
public static <T extends Throwable> void doWork(T t) throws T  {// OK
    try {
        // do work
    } catch (Throwable realCause) {
        t.initCause(realCause);
        throw t;
    }
}
```

### 注意类型擦除后方法相同签名
泛型类型被擦除之后，可能会导致方法签名一直。如果`Pair`类中添加`eqauls`方法：
```java
public class Pair<T> {
    public boolean equals(T value) {
        return first.equals(value) && second.equals(value);
    }
    ...
}
```
当`Pair<T>`类型擦除之后，其`equals`方法变为 `boolean equals(Object o)` 同`Object`中的`equals`方法一直。
解决方法是命名成其他名字。
为了防止擦除带来的方法冲突，我们强加了一个限制。即类或类型变量不能同时是同一个接口的不同参数化的两种子类型。
```java
class Employee implements Comparable<Employee> { . . . }
class Manager extends Employee implements Comparable<Manager>{ . . . } // Error
```
上例子中，`Manager` 需要实现`Comparable<Manager>`和`Comparable<Employee>`，这两个接口就是同一接口的两个不同参数化的接口。
虽然类型擦除之后，看起来是合法的：
```java
class Employee implements Comparable { . . . }
class Manager extends Employee implements Comparable { . . . }
```
但是桥接方法是冲突的，实现`Comparable<X>`接口的类，会有一个桥接方法：
```java
public int comparTo(Object other) {return compareTo((X) other);}
```

## 泛型类中的继承规则
在使用泛型类型的时候，需要了解一些继承的规则。如果有两个类： `Employee`和`Manager`，`Manager`是`Employee`子类。`Pair<Manager>`也是`Pair<Employee>`的子类么？
实际上，在`Pair<S>`和`Pair<T>`之间没有任何的关系，不管`S`和`T`是什么关系。

## 通配符类型
通配符类型是指可以有不同的类型参数。通配符类型使用方式为`Pair<? extends Employee>`，它表示类型参数是`Employee`的子类的任何通用`Pair`类型，比如： `Pair<Manager>`。
我们可以把`Pair<Manager>`看做是`Pair<? extends Employee>` 的子类。因此，我们可以把`Pair<Manager>`的实例传入下面的方法中：
```java
public static void print(Pair<? extends Employee> p) {
    // do something
}
```

### 通配符类型的上界限
通配符的界限同类型变量的界限相似，不过通配符界限可以指定一个下限： `? super Manager`。该通配符表示所有`Manager`的父类。
含有父类界限的通配符与上述的通配符类型正好相反。我们可以为方法提供参数，但是不能使用其返回值。比如`Pair<? super Manager>`的方法可以表述如下：
```java
void setFirst(? super Manager);
? super Manager getFirst();
```
编译器不能知道`setFirst`参数准确的类型，因此不能接受具有`Employee`或`Object`类型的参数调用。该方法只能传递`Manager`或者其子类型的对象。对于调用`getFirst`将会返回一个`Object`对象，因为我们不知道它的返回类型是什么。
```java
public static void minmaxBonus(Manager [] a, Pair<? super Manager> result) {
    if (a.length == 0) return ;
    Manager min = a[0];
    Manager max = a[0];
    for (int i=1; i<a.length; i++) {
        if (min.getBonus() > a[i].getBonus()) min = a[i];
        if (max.getBonus() < a[i].getBonus()) max = a[i];
    }
    result.setFirst(min);
    result.setSecond(max);
}
```
其类图如下所示：

 <img src="https://yuml.me/diagram/nofunky/class/%5BPair(raw)%5D%5E-%5BPair%3C%3F%3E%5D%2C%5BPair%3C%3F%3E%5D%5E-%5BPair%3C%3F%20super%20Manager%3E%5D%2C%5BPair%3C%3F%20super%20Manager%3E%5D%5E-%5BPair%3CEmployee%3E%5D%2C%5BPair%3C%3F%20super%20Manager%3E%5D%5E-%5BPair%3CObject%3E%5D" >

 一般来说，**父类界定的通配符类型可以`set`对象，而子类型界定的通配符类型可以让你`get`对象**

 `Comparable`接口本身就是一个泛型：
```java
public interface Comparable<T> {
    public int compareTo(T other);
}
```
使用`T extends Comparable`方式，经过类型擦除后是：
```java
public interface Comparable {
    public int compareTo(Object other);
}
```
所以，对于`ArrayAlg`类中的`min`方法，我们可以将其定义为：
```java
public static <T extends Comparable<T>> T min(T[] a)
```
这样比`T extends Comparable`更为全面，而且对于大多数类来说都能很好的工作。但是对于`LocalDate`对象来说，并不适用。`LocalDate`实现了`ChronoLocalDate`接口，`ChronoLocalDate`接口继承了`Comparable<ChronoLocalDate>`，因此`LocalDate`实现了`Comparable<ChronoLocalDate>`而不是`Comparable<LocalDate>`。
在这种情况下，使用下面的方法更适用：
```java
public static <T extends Comparable<? super T>> T min(T[] a) {}
```
这样经过类型擦除之后：
```java
public int compareTo(LocalDate other);
```

虽然这样看起来很繁琐，但是这样声明能够消除调用方法时对参数的不必要限制。

### 无界限通配符
无界限通配符，如`Pair<?>`。无界限通配符与原始类型`Pair`一样。但是，这两个类型是不同的，`Pair<?>`的方法如下：
```java
? getFirst();
void setFirst(?);
```
`getFirst`方法的返回值只能是`Object`，而`setFirst`方法不能被调用，即使是`Object`作为参数也不行，当然`null`是可以的。这是`Pair<?>`与原始的`Pair`类型的不同。

无界限通配符一般会用来做非常简单的操作，例如，判断值是不是`null`:
```java
public static boolean hasNulls(Pair<?> p) {
    return p.getFirst() == null || p.getSecond() == null;
}
```

### 通配符捕获
我们有一个交换`Pair`元素的方法：
```java
public static void swap(Pair<?> p)
```
通配符不是一个类型变量，我们不能用`?`作为一个类型，也就是下面的代码是不合法的：
```java
? t = p.getFirst(); // Error
p.setFirst(p.getSecond());
p.setSecond(t);
```
我们在交换元素时，必须要将其中一个元素保存成临时变量。为了解决这个问题，我们可以编写一个辅助方法`swapHelper`:
```java
public static <T> void swapHelper (Pair<T> p) {
    T t = p.getFirst();
    p.setFirst(p.getSecond());
    p.setSecond(t);
}
```

`swapHelper`是一个泛型方法，而`swap`不是，现在我们可以在`swap`中调用`swapHelper`:
```java
public static void swap(Pair<?> p) { swapHelper(p);}
```

在上述方法中，`swapHelper`中的`T`捕获了`swap`中的通配符类型。编译器不知道通配符是什么类型，但它是一种确定的类型，当T表示该类型时，<T> swapHelper的定义是完美的。
当然，在这个例子中，我们没有必要使用`Pair<?>`，可以直接定义`<T> void swapHelper`。但是，如果某个方法中像如下定义：
```java
public static void maxmin(Manager[] a, Pair<? super Manager> result) {
    PairAlg.swap(result); // swapHepler 捕获了通配符类型
} 
```

下边的例子给了前边所描述的内容：
```java

public class GenericMethod {
    public static void main(String[] args) {
        Manager ceo = new Manager("Gus Greedy", 800000, 2003, 12, 15);
        Manager cfo = new Manager("Sid Sneaky", 600000, 2003, 12, 15);
        Pair<Manager> buddies = new Pair<>(ceo, cfo);
        printBuddies(buddies);

        ceo.setBonus(1000000);
        cfo.setBonus(500000);
        Manager[] managers = {ceo, cfo};

        Pair<Employee> result = new Pair<>();
        minmaxBonus(managers, result);
        System.out.println("first: " + result.getFirst().getName()
            + ", second: " + result.getSecond().getName());
        maxminBonus(managers, result);
        System.out.println("first: " + result.getFirst().getName()
            + ", second: " + result.getSecond().getName());
    }

    public static void printBuddies(Pair<? extends Employee> p) {
        Employee first = p.getFirst();
        Employee second = p.getSecond();
        System.out.println(first.getName() + " and " + second.getName() + " are buddies.");
    }

    public static void minmaxBonus(Manager[] a, Pair<? super Manager> result) {
        if (a.length == 0) return;
        Manager min = a[0];
        Manager max = a[0];
        (Continues)
            .8 Wildcard Types 449
        Listing 8.3 (Continued)
        for (int i = 1; i < a.length; i++) {
            if (min.getBonus() > a[i].getBonus()) min = a[i];
            if (max.getBonus() < a[i].getBonus()) max = a[i];
        }
        result.setFirst(min);
        result.setSecond(max);
    }

    public static void maxminBonus(Manager[] a, Pair<? super Manager> result) {
        minmaxBonus(a, result);
        PairAlg.swapHelper(result); // OK--swapHelper captures wildcard type
    }
}

class PairAlg {
    public static boolean hasNulls(Pair<?> p) {
        return p.getFirst() == null || p.getSecond() == null;
    }

    public static void swap(Pair<?> p) {
        swapHelper(p);
    }

    public static <T> void swapHelper(Pair<T> p) {
        T t = p.getFirst();
        p.setFirst(p.getSecond());
        p.setSecond(t);
    }
}

```