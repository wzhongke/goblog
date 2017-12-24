---
title: Lambda表达式和Stream
date: 2017-07-20 12:14:12
tags: ["java"]
categories: ["java"]
---

流被设计为与lambda表达式一起使用，这使得日常编程更容易。

# Lambda 表达式
匿名类的一个非常明显的问题是，如果匿名类的实现非常简单，例如只包含一个方法的接口，那么匿名类的语法可能看起来很笨重而且也不清晰。在这些情况下，可以将功能作为参数传递给另一种方法。Lambda表达式就是为此而生，它能够将功能视为方法参数或将代码作为数据。
对于单一方法的实例，相对于匿名类，lambda表达式可以更紧凑地表示。
<!-- more -->
## 使用Lambda表达式的理想情况
假设要创建一个社交网络应用程序，想要创建一个功能，使管理员可以在符合特定条件的社交网络应用程序的成员上执行任何类型的操作（例如发送消息）。
```java
public class Person {
    public enum Sex {
        MALE, FEMALE
    }
    int age;
    String name;
    LocalDate birthday;
    Sex gender;
    String emailAddress;
    // getter和setter方法
}
```
## 1. 创建搜索符合某个特征成员的方法
最简单的方式是创建几个方法，每个方法都负责搜索出满足某个特性的成员，比如性别或者年龄。
```java
public static void printPersonOlderThan(List<Person> roster, int age) {
    for (Person p: roster) {
        if (p.getAge() >= age)
            p.printPerson();
    }
}
```
上面的方法可能使得应用程序变得脆弱，因为修改Person类，比如修改数据类型，就会导致程序无法正常工作。假设要升级程序，需要改变`Person`类的结构，增加了新的属性，也许还会改变衡量ages的数据类型或者算法。就需要根据这些修改重写API。
## 2. 创建一个更通用的搜索方法
下面的方法更为通用，它打印了指定年龄段的成员
```java
public static void printPersonsWithinAgeRange(
    List<Person> roster, int low, int high) {
    for (Person p : roster) {
        if (low <= p.getAge() && p.getAge() < high) {
            p.printPerson();
        }
    }
}
```
如果想要打印指定性别的成员或者指定性别和特定年龄段的成员该怎么办？如果要改变`Person`类，比如添加一些关系状态或者地理位置的属性，要怎么修改？虽然这个方法比`printPersonOlderThan`更为通用，但是为不同可能的搜索请求，创建不同的方法，依然会使得代码脆弱。可以将指定要在其他类中搜索的条件的代码分开。
## 3. 在Local Class中指定搜索情况的代码
下面的方法可以允许你指定搜索环境。
```java
public static void printPersons(
    List<Person> roster, CheckPerson tester) {
    for (Person p : roster) {
        if (tester.test(p)) {
            p.printPerson();
        }
    }
}
```
上面的方法使用了`CheckPerson`的方法`test`检测了`roster`中每个`Person`实例，如果方法返回`true`，那么`printPerson`会被调用。
可以通过实现`CheckPerson`接口来指定搜索条件
```java
interface CheckPerson {
    boolean test(Person p);
}
```
下面的类实现了`CheckPerson`接口，它的`test`方法过滤了年龄在18到25之间的男性
```java
class CheckPersonEligible implements CheckPerson {
    public boolean test(Person p) {
        return p.gender == Person.Sex.MALE &&
                p.getAge() >= 18 &&
                p.getAge() <= 25;
    }
}
```
可以通过新建一个该类的实例，传递给`printPersons`方法：
```java
printPersons(roster, new CheckPersonEligible());
```
虽然这个方式不那么脆弱，如果改变了`Person`的结构，就不必重新方法了，但是还是要有额外的代码：一个新的接口和新的搜索结果的类。因为`CheckPersonEligible`实现了接口，可以用一个匿名类代替这个类，这样可以不用为每次搜索都声明一个新类。
## 4. 使用匿名类
```java
printPersons(
    roster,
    new CheckPerson() {
        public boolean test(Person p) {
            return p.getGender() == Person.Sex.MALE
                && p.getAge() >= 18
                && p.getAge() <= 25;
        }
    }
);
```
这种方法减少了所需的代码量，不用每次执行时都创建一个新类。然而，匿名类的语法庞大。因为`CheckPerson`接口只包含一种方法。在这种情况下，可以使用lambda表达式而不是匿名类。
## 5. 使用Lambda表达式
`CheckPerson`接口是一个函数式接口（functional interface)。函数式接口只包含一个抽象方法。函数式接口可以包含多个`default methods`和`static methods`。因为函数式接口只包含一个抽象方法，可以在实现该方法时省略该方法的名称。
```java
printPersons(
    roster,
    (Person p) -> p.getGender() == Person.Sex.MALE
        && p.getAge() >= 18
        && p.getAge() <= 25
);
```
## 6. 使用标准的函数式接口
`CheckPerson`是一个简单的函数式接口。该方法如此简单，没有必要在程序声明一次。因此，JDK中定义了几个标准的功能接口，可以在`java.util.function`包中找到它们。
可以使用`Predicate<T>`接口替换`CheckPerson`，这个接口有一个方法`boolean test(T t)`
```java
interface Predicate<T> {
    boolean test(T t);
}
```
使用`Predicate<T>`接口替换`CheckPerson`，如下：
```java
printPersonsWithPredicate(
    roster,
    p -> p.getGender() == Person.Sex.MALE
        && p.getAge() >= 18
        && p.getAge() <= 25
);
```
不止有这一种方式使用lambda表达式，下面的方式是推荐的方式
## 7. 在应用中使用Lambda表达式
只有实现一个函数式接口，才能使用lambda表达式。
如果想要使用另一个lambda表达式，该表达式接收一个参数，并且返回`void`，可以使用`Consumer<T>`接口，该接口有一个抽象方法`void accept(T t)`.
我们可以如下定义`Person`的方法：
```
public static void processPersons(List<Person> roster, Predicate<Person> tester, Consumer<Person> block) {
    for (Person p: roster) {
        if (tester.test(p)) {
            block.accept(p  );
        }
    }
}
```
该方法可以用如下方式调用：
```
processPersons(roster, p -> p.getAge() >= 18, p -> p.printPerson());
```
如果你不止打印符合条件的信息，比如想要验证成员的信息或者获取他们的联系方式。需要一个有返回值的抽象方法的函数式接口。`Function<T,R>`包含一个方法`R apply(T t)`，下面的例子展示了通过`mapper`获取数据，并使用`block`处理数据的代码
```
public static void processPersonsWithFunction(List<Person> roster,
          Predicate<Person> tester,
          Function<Person, String> mapper,
          Consumer<String> block) {
    for (Person p: roster) {
        if (tester.test(p)) {
            String data = mapper.apply(p);
            block.accept(data);
        }
    }
}
```
该方法可以用如下方式调用：
```
processPersonsWithFunction(
    roster,
    p -> p.getGender() == Person.Sex.MALE
        && p.getAge() >= 18
        && p.getAge() <= 25,
    p -> p.getEmailAddress(),
    email -> System.out.println(email)
);
```
## 8. 使用泛型
下面使用泛型的方法，可以接收任意数据类型的集合
```
public static <X, Y> void processElements (
        Iterable<X> source,
        Predicate<X> tester,
        Function<X, Y> mapper,
        Consumer<Y> block ) {
    for (X p: source) {
        if (tester.test(p)) {
            Y data = mapper.apply(p);
            block.accept(data);
        }
    }
}
```
上面的方法可以使用如下方式调用
```
processElements(
        roster,
        p ->  p.getAge() >= 18
                && p.getAge() <= 25,
        p -> p.getEmailAddress(),
        email -> System.out.println(email)
        );
```
## 9. 使用接受Lambda表达式作为参数的聚合操作
下面的例子使用聚合操作来打印email
```
roster.stream().
    filter( p ->  p.getAge() >= 18
                && p.getAge() <= 25)
    .map(p -> p.getEmailAddress())
    .forEach( email -> System.out.println(email));
```



# 接口 -- 默认方法
java8中在接口中可以定义默认方法，默认方法同抽象类中的非抽象方法类似，子类可以选择是否覆盖。
这样就可以在接口中添加新的方法，而不用修改原有实现该接口的类。
```
public interface DefaultInterface {
    default String defaultMethod() {
        return "Default method";
    }
}
```
接口的默认方法可以不用加`public`前缀，因为接口中的方法都是`public`的。
还可以定义静态的方法，和类中的静态方法相同，都是与类相关联的，而不是它的实例。
```
public interface DefaultInterface {
    static String defaultMethod() {
        return "Static method";
    }
}
```

默认方法可以向现有接口中添加支持lambda表达式作为参数的方法。

# 函数式接口
`@FunctionalInterface`注解的接口是函数式接口。使用此种接口作为函数参数的方法，传递参数时，可以使用lambda表达式作为参数。
```
public void sort(Comparator<Card> c) {
   Collections.sort(entireDeck, c);
}
// 可以这样调用
deck.sort((firstCart, secondCard) -> firstCart.getRank().value() - secondCard.getRank().value());
```
如果只是创建一个Comparator实例来比较可以从诸如getValue或hashCode之类的方法返回数值的任何对象，我们可以使用`Comparator`接口提供的静态方法`comparing`
```
deck.sort(Comparator.comparing((card) -> card.getRank())); 
// 还可以写成方法引用
deck.sort(Comparator.comparing(Card::getRank));
```
`Comparator`还提供了`comparingDouble`、`thenComparing`等一系列方法来创建`Comparator`实例。
如果要创建一个可以将对象的多个属性进行比较的Comparator实例，如下例：
```
deck.sort(
    (firstCard, secondCard) -> {
        int compare =
            firstCard.getRank().value() - secondCard.getRank().value();
        if (compare != 0)
            return compare;
        else
            return firstCard.getSuit().value() - secondCard.getSuit().value();
    }      
); 
```
我们可以使用`Comparator`接口提供的静态方法来创建实例
```
deck.sort(
    Comparator
        .comparing(Card::getRank)
        .thenComparing(Comparator.comparing(Card::getSuit)));
```
# 方法引用
我们使用lambda表达式来创建匿名方法。但是，有时候使用lambda表达式只是调用了一个方法。这种情况下，通过方法引用现有方法往往更加清晰。使用方法引用，可以使代码更紧凑，更易于阅读。
如果对`Person`的数组根据`age`属性进行排序。或许可以使用下面的代码
```
class PersonAgeComparator implements Comparator<Person> {
    public int compare(Person a, Person b) {
        return a.getBirthday().compareTo(b.getBirthday());
    }
}
Person[] rosterAsArray = roster.toArray(new Person[roster.size()]);
Arrays.sort(rosterAsArray, new PersonAgeComparator());
```
`sort`方法的签名是`static <T> void sort(T[] a, Comparator<? super T> c)`。`Comparator`接口是函数式接口，所以我们可以使用lambda表达式代替`PersonAgeComparator`类：
```
Arrays.sort(rosterAsArray,
        (Person a, Person b) -> a.getBirthday().compareTo(b.getBirthday())
);
```
我们还可以定义`Person`的静态方法`compareByAge`:
```
public static int compareByAge(Person a, Person b) {
    return a.birthday.compareTo(b.birthday);
}
```
那么上边的方法可以写成：
```
Arrays.sort(rosterAsArray,
    (a, b) -> Person.compareByAge(a, b)
);
```
因为lambda表达式调用了一个已经存在的方法，我们可以用方法引用：
```
Arrays.sort(rosterAsArray, Person::compareByAge);
```
使用方法引用由两个条件：
1. 其形式参数列表从`Comparator <Person> .compare`复制，是`(Person，Person)`。也就是说方法引用的方法的参数类型需要同所需要的参数类型是一致的。
2. 它的调用时`Person.compareByAge`
## 方法应用的类型
| 类型   | 示例
:--------|:-----------
|应用静态方法| ContainingClass::staticMethodName
|某个对象的方法| containingObject::instanceMethodName
|引用特定类型的任意对象的实例方法|ContainingType::methodName
|构造器方法|ClassName::new
主要介绍下构造器方法：
同静态方法引用类似，我们可以用`new`来使用构造器引用。以下方法将元素从一个集合复制到另一个集合：
```
public static <T, SOURCE extends Collection<T>, DEST extends Collection<T>>
    DEST transferElements(
        SOURCE sourceCollection,
        Supplier<DEST> collectionFactory) {
        
        DEST result = collectionFactory.get();
        for (T t : sourceCollection) {
            result.add(t);
        }
        return result;
}
```
函数式接口`Supplier`包含一个方法，其签名为`T get()`。可以通过如下方式调用该方法：
```
Set<Person> rosterSetLambda =
    transferElements(roster, () -> { return new HashSet<>(); });
Set<Person> rosterSet = transferElements(roster, HashSet::new);
```
# 聚合操作
聚合操作描述了以下管道的操作，它计算了集合`roster`中所有男性的平均年龄：
```
double average = roster
    .stream()
    .filter(p -> p.getGender() == Person.Sex.MALE)
    .mapToInt(Person::getAge)
    .average()
    .getAsDouble();
```
JDK中包含许多终止操作（如：`average`, `sum`, `min`, `max` 和 `count`），终止操作返回一个对流中数据计算的值。这些操作被称为归纳操作，也有些归纳操作返回一个集合。许多归纳操作执行像计算平局值或者将元素分类的操作。主要有两个方法： `Stream.reduce`类方法；`Stream.collect`类方法
## Stream.reduce 方法
Stream.reduce方法是通用的简化操作，比如下例中的`Stream.sum`归纳操作：
```
Integer totlaAge = roster
    .stream()
    .mapToInt(Person::getAge)
    .sum();
```
使用`Stream.reduce`操作也能实现上述操作：
```
Integer totalAgeReduce = roster
   .stream()
   .map(Person::getAge)
   .reduce(
       0,
       (a, b) -> a + b);
```
`reduce`操作需要两个参数：
* `identity`: 该参数是归纳操作的初始值，如果集合中没有元素，也是默认的返回值。
* `accumulator`: 累加器函数需要两个参数：归纳的一部分结果和流的下一个元素。它返回一个新的部分结果。
## Stream.collect 方法
`collect`方法会改变现有值。
如果要计算一个stream中的平均值，需要两段数据：stream中的元素的总数和元素的和。与`reduce`类似，`collect`方法也只返回一个值。可以创建一个新的数据类型，跟踪元素的总数和这些元素的和：
```
class Averager implements IntConsumer
{
    private int total = 0;
    private int count = 0;
        
    public double average() {
        return count > 0 ? ((double) total)/count : 0;
    }
        
    public void accept(int i) { total += i; count++; }
    public void combine(Averager other) {
        total += other.total;
        count += other.count;
    }
}
```
下面的管道使用了`Average`类和`collect`方法来计算男性成员的平均年龄：
```
Averager averageCollect = roster.stream()
        .filter(p -> p.getGender() == Person.Sex.MALE)
        .map(Person::getAge)
        .collect(Averager::new, Averager::accept, Averager::combine);
System.out.println("Average age of male members: " + averageCollect.average());
```
`collect`方法需要三个参数：
* `supplier`: 该参数需要一个工厂方法，它创建了新的实例。对于`collect`操作来说，它创建了放置结果的容器，就如`Averager`
* `accumulator`: 累加器功能将流元素结合到结果容器。在此示例中，它通过将count变量增加1来修改Averager结果容器，并将总成员变量添加到流元素的值，该元素是表示男性成员年龄的总和。
* `combiner`: 组合器功能需要两个结果容器并合并其内容。
虽然JDK为您提供了平均运算以计算流中元素的平均值，但如果需要从流的元素中计算多个值，则可以使用`collect`操作和自定义类。
`collect`操作非常适合于集合。以下示例将男性成员的名字提取出来：
```
List<String> namesOfMaleMembersCollect = roster
    .stream()
    .filter(p -> p.getGender() == Person.Sex.MALE)
    .map(Person::getName)
    .collect(Collectors.toList());
```
上述例子中，`collect`操作需要一个`Collector`类型的参数。该类中封装了`collect`所需要的三个参数。
`Collectors`类中包含了许多有用的归纳操作，例如将元素累积到集合中并根据各种标准汇总元素。这些归纳操作返回`Collector`的实例，可以用他们作为`collect`操作的参数。
上例中的`Collectors.toList`将流元素累加到List的新实例中。`toList`操作返回了一个`Collector`实例，而不是一个集合。
下例中将`roster`集合元素根据性别归类：
```
Map<Person.Sex, List<Person>> byGender = roster.stream()
        .collect(Collectors.groupingBy(Person::getGender));
```
下例中根据性别将集合中元素的名字归类：
```
 Map<Person.Sex, List<String>> namesByGender =
    roster.stream()
        .collect(
            Collectors.groupingBy(
                Person::getGender,
                Collectors.mapping(
                    Person::getName,
                    Collectors.toList())));
```

以下示例检索每个性别成员的总年龄：
```
Map<Person.Sex, Integer> totalAgeByGender =
    roster.stream()
        .collect(
            Collectors.groupingBy(
                Person::getGender,                      
                Collectors.reducing(
                    0,
                    Person::getAge,
                    Integer::sum)));
```
以下示例检索每个性别成员的平均年龄：
```
Map<Person.Sex, Double> averageAgeByGender = roster
    .stream()
    .collect(
        Collectors.groupingBy(
            Person::getGender,                      
            Collectors.averagingInt(Person::getAge)));
```
## 并行处理流
并行计算包括将问题分解为子问题，同时解决这些问题（并行地，每个子问题在单独的线程中运行），然后将解决方案的结果组合。Java中有fork/join框架可以轻松地在应用程序中实现并行计算，不过需要问题是怎么分解成子问题的。在聚合操作中，Java运行时会自动处理分解和组合问题。
在应用程序中实现并行计算的一个主要难点是使用的集合不是线程安全的，这意味着多线程会导致线程之间的干扰或者内存一致性错误。集合框架提供了同步包装方法，可以通过包装任意集合，让他们成为线程安全的。但是，这会引入线程竞争，使得线程不能并行计算。聚合操作和并行处理流可以并行处理线程不安全的集合，而不用我们做修改。
>并行性并不会自动快于连续执行操作，即使有足够的数据和处理器内核，并行性也不会快。 虽然集合操作能够更轻松地实现并行性，但我们仍要确定应用程序是否适合并行性。
我们可以自己选择串行或者并行执行流。如果需要并行执行流，那么需要使用指定的方法：`Collection.parallelStream`.
```
double average = roster
    .parallelStream()
    .filter(p -> p.getGender() == Person.Sex.MALE)
    .mapToInt(Person::getAge)
    .average()
    .getAsDouble();
```
## 并行归纳
下例是我们串行处理流
```
Map<Person.Sex, List<Person>> byGender =
    roster
        .stream()
        .collect(
            Collectors.groupingBy(Person::getGender));
```
其并行处理如下例：
```
ConcurrentMap<Person.Sex, List<Person>> byGender =
    roster
        .parallelStream()
        .collect(
            Collectors.groupingByConcurrent(Person::getGender));
```
用`groupingByConcurrent`代替了`groupingBy`，返回结果也有`Map`变为`ConcurrentMap`.

