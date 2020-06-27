---
title: HashMap 和 ConcurrentHashMap
date: 2017-06-17 19:48:32
tags: ["算法"]
categories: ["算法"]
---

## 并发编程为什么使用ConcurrentHashMap
HashMap并不是线程安全的，HashTable虽然是线程安全的，但是HashTable的效率非常低下。
<!-- more -->

### HashMap不是线程安全的
The HashMap class is roughly equivalent to Hashtable, except that it is unsynchronized and permits nulls.

Iteration over collection views requires time proportional to the "capacity" of the HashMap instance (the number of buckets) plus its size (the number of key-value mappings).  Thus, it's very important not to set the initial capacity too high (or the load factor too low) if iteration performance is important.

When the number of entries in the hash table exceeds the product of the load factor and the current capacity, the hash table is <i>rehashed</i> (that is, internal data structures are rebuilt) so that the hash table has approximately twice the number of buckets.

Note that this implementation is not synchronized.</strong> If multiple threads access a hash map concurrently, and at least one of the threads modifies the map structurally, it <i>must</i> be synchronized externally.  (A structural modification is any operation that adds or deletes one or more mappings; merely changing the value associated with a key that an instance already contains is not a structural modification.)  This is typically accomplished by synchronizing on some object that naturally encapsulates the map.

If no such object exists, the map should be "wrapped" using the {@link Collections#synchronizedMap Collections.synchronizedMap} method.  This is best done at creation time, to prevent accidental unsynchronized access to the map:

```java
Map m = Collections.synchronizedMap(new HashMap(...));
```

## HashMap 源码
HashMap 的构造方法如下：

```java
/**
* 存储桶的 table，在第一次使用时初始化，根据需要调整大小，大小是2的整数次幂
*/
transient Node<K,V>[] table;

public HashMap() {
    this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
}
public HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
}

public HashMap(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal initial capacity: " +
                                            initialCapacity);
    if (initialCapacity > MAXIMUM_CAPACITY)
        initialCapacity = MAXIMUM_CAPACITY;
    if (loadFactor <= 0 || Float.isNaN(loadFactor))
        throw new IllegalArgumentException("Illegal load factor: " +
                                            loadFactor);
    this.loadFactor = loadFactor;
    // resize 时下一个 size 的大小， The next size value at which to resize (capacity * load factor).
    this.threshold = tableSizeFor(initialCapacity);
}
```

将键值对放入到 HashMap 的方法如下：
```java
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}

static final int hash(Object key) {
    int h;
    // 通过亦或的方式将 hash 值高位信息传递到低位
    // 因为该表使用2的幂次掩码，所以仅在当前掩码上方位变化的哈希集将始终发生冲突。 
    //（众所周知的示例是在小表中包含连续整数的Float键集。）因此，我们应用了一种变换，将向下传播较高位的影响。
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

`putVal` 方法主要分为四部分：
1. 第一次使用的时候初始化 table
2. 如果当前 hash 的下标没有值，则放入
3. 如果当前 hash 下标用的是 `TreeNode`，则放到红黑树中
4. 如果当前 hash 下标用的是链表，则放入到链表中
5. 是否需要调整大小 `resize`

### 计算当前 table 长度
```java
// 第一次使用的时候初始化 table
if ((tab = table) == null || (n = tab.length) == 0)
    n = (tab = resize()).length;
Node<K,V> p = tab[i = (n - 1) & hash]
if (p == null)
    // 如果当前 key 散列的下标没有值，则将当前的键值对放入
    tab[i] = newNode(hash, key, value, null);
else {
    Node<K,V> e; K k;
    if (p.hash == hash &&
        ((k = p.key) == key || (key != null && key.equals(k))))
        // hash 相等，key 相等（地址相等） 或者 equals 返回为相等，则认为 key 相等，修改 value
        e = p;
    else if (p instanceof TreeNode)
        // 如果桶是用 TreeNode 形式，将键值放到 TreeNode 中，如果有相等的key，则返回 Node
        e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
    else {
        // 放到链表中
        for (int binCount = 0; ; ++binCount) {
            if ((e = p.next) == null) {
                p.next = newNode(hash, key, value, null);
                if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                    treeifyBin(tab, hash); // 如果链表大小超过阈值，则将链表转换成 TreeNode
                break;
            }
            if (e.hash == hash &&
                ((k = e.key) == key || (key != null && key.equals(k))))
                break;
            p = e;
        }
    }
    if (e != null) { // existing mapping for key
        V oldValue = e.value;
        if (!onlyIfAbsent || oldValue == null)
            e.value = value;
        afterNodeAccess(e); // 留给子类覆盖
        return oldValue;
    }
}
++modCount;
if (++size > threshold) // 超过阈值，调整大小
    resize();
afterNodeInsertion(evict);
return null;
```

### 调整大小 resize
`resize` 函数的作用是初始化 `table` 或者将 `table` 的大小加倍。

如果为空，则根据字段阈值中保持的初始容量目标进行分配。因为我们使用的是2的幂，所以每个bin中的元素会保持相同的索引，或者在新表中以2的幂偏移。
```java
Node<K,V>[] oldTab = table;
int oldCap = (oldTab == null) ? 0 : oldTab.length;
int oldThr = threshold;
int newCap, newThr = 0;
if (oldCap > 0) {
    // 当目前的容量已经达到最大值时，不再扩容，否则将阈值加倍
    if (oldCap >= MAXIMUM_CAPACITY) {
        threshold = Integer.MAX_VALUE;
        return oldTab;
    }
    else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                oldCap >= DEFAULT_INITIAL_CAPACITY)
        newThr = oldThr << 1; // double threshold
}
// 初始化时使用，即 oldCap == 0
else if (oldThr > 0) // initial capacity was placed in threshold
    newCap = oldThr;
else {               // zero initial threshold signifies using defaults
    newCap = DEFAULT_INITIAL_CAPACITY;
    newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
}
if (newThr == 0) {
    float ft = (float)newCap * loadFactor;
    newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                (int)ft : Integer.MAX_VALUE);
}
threshold = newThr;
Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
// 初始化 table
table = newTab;
```

上述代码计算了新的阈值和大小，并重新分配了 `table`，之后需要将原有的值迁移到新分配的 `table` 中:
```java
if (oldTab != null) {
    for (int j = 0; j < oldCap; ++j) {
        Node<K,V> e;
        if ((e = oldTab[j]) != null) { // 如果桶中有内容
            oldTab[j] = null;       // 将引用置为空，便于垃圾回收，非必须
            if (e.next == null)   // 如果只有一个元素
                newTab[e.hash & (newCap - 1)] = e;
            else if (e instanceof TreeNode)  // 如果是 TreeNode，将 TreeNode 中的键值对重新散列到hash表中
                ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
            else { // preserve order 使用原来的顺序将 键值对 重新散列到 hash 表中
                Node<K,V> loHead = null, loTail = null;
                Node<K,V> hiHead = null, hiTail = null;
                Node<K,V> next;
                do {
                    next = e.next;
                    if ((e.hash & oldCap) == 0) {
                        if (loTail == null)
                            loHead = e;
                        else
                            loTail.next = e;
                        loTail = e;
                    }
                    else {
                        if (hiTail == null)
                            hiHead = e;
                        else
                            hiTail.next = e;
                        hiTail = e;
                    }
                } while ((e = next) != null);
                if (loTail != null) {
                    loTail.next = null;
                    newTab[j] = loHead;
                }
                if (hiTail != null) {
                    hiTail.next = null;
                    newTab[j + oldCap] = hiHead;
                }
            }
        }
    }
}
```

**jdk1.8以前**在多线程环境下，使用HashMap的`put()`会导致程序进入死循环，是因为多线程会导致HashMap的冲突链表形成环形数据。一旦新城环形数据结构，Node的`next`永远不为空，导致死循环。

<!-- more -->
### HashTable效率低下
以下是HashTable的`put()`和`get()`方法的源码。可以看到我们经常用到的`put()`和`get()`方法的同步是对象的同步。在线程竞争激烈的情况下，当一个线程访问HashTable的同步方法时，其他访问同步方法的线程只能进入阻塞或轮询状态。因此，HashTable在多线程下的效率非常低，连读写锁都没有采用。
```java
public synchronized V put(K key, V value) {
    // Make sure the value is not null
    if (value == null) {
        throw new NullPointerException();
    }
	...
    addEntry(hash, key, value, index);
    return null;
}

public synchronized V get(Object key) {
    Entry<?,?> tab[] = table;
    int hash = key.hashCode();
    ...
    return null;
}
```

## ConcurrentHashMap的锁分段技术
锁分段技术就是容器中使用多把锁，每个锁用于容器中的部分数据。这样当多个线程并发访问不同数据段的数据时，线程就不会竞争锁，提高并发访问效率。

在ConcurrentHashMap的`put()`方法中，对于向非空桶中加入数据时，才使用同步锁。

`ConcurrentHashMap` 键值都不允许为 `null`

```java
final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();
    int hash = spread(key.hashCode());
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {  // 定位的桶中没有元素，不需要同步
            if (casTabAt(tab, i, null,
                         new Node<K,V>(hash, key, value, null)))  // 使用乐观锁，失败后会在 for 循环中继续尝试
                break;                   // no lock when adding to empty bin
        }
        else if ((fh = f.hash) == MOVED) // 在 MOVED 状态，即 resize 
            tab = helpTransfer(tab, f);
        else {
            V oldVal = null;
            synchronized (f) {  // 在该桶上加锁，就是 tab[i]
                if (tabAt(tab, i) == f) { // 再次确认该位置是 f
                    if (fh >= 0) {
                        binCount = 1;
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
                            if (e.hash == hash &&
                                ((ek = e.key) == key ||
                                 (ek != null && key.equals(ek)))) { // 当值存在时，根据 key 修改 value
                                oldVal = e.val;
                                if (!onlyIfAbsent)
                                    e.val = value;
                                break;
                            }
                            Node<K,V> pred = e;
                            if ((e = e.next) == null) { // 将键值添加到链表的末尾
                                pred.next = new Node<K,V>(hash, key,
                                                          value, null);
                                break;
                            }
                        }
                    }
                    else if (f instanceof TreeBin) {  // 如果是红黑树
                        Node<K,V> p;
                        binCount = 2;
                        if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                       value)) != null) {
                            oldVal = p.val;
                            if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                }
            }
            if (binCount != 0) {
                if (binCount >= TREEIFY_THRESHOLD) // 桶大小超过阈值时转换成红黑树
                    treeifyBin(tab, i);
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
    addCount(1L, binCount);
    return null;
}
```

而ConcurrentHashMap的 `get()` 方法是没有锁的。这是因为 `get()` 方法中使用的共享变量都定义成`volatile`类型，而`volatile`类型的变量能够在多线程之间保持可见性，能够保证多个线程读取的时候不会读到过期的值。