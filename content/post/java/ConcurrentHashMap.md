---
title: HashMap 和 ConcurrentHashMap
date: 2017-06-17 19:48:32
tags: ["算法"]
categories: ["算法"]
---

## 并发编程为什么使用ConcurrentHashMap
HashMap并不是线程安全的，HashTable虽然是线程安全的，但是HashTable的效率非常低下。

### HashMap不是线程安全的
在多线程环境下，使用HashMap的`put()`会导致程序进入死循环，是因为多线程会导致HashMap的冲突链表形成环形数据。一旦新城环形数据结构，Node的`next`永远不为空，导致死循环。
<!-- more -->
### HashTable效率低下
以下是HashTable的`put()`和`get()`方法的源码。可以看到我们经常用到的`put()`和`get()`方法的同步是对象的同步。在线程竞争激烈的情况下，当一个线程访问HashTable的同步方法时，其他访问同步方法的线程只能进入阻塞或轮询状态。因此，HashTable在多线程下的效率非常低，连读写锁都没有采用。
```
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
### ConcurrentHashMap的锁分段技术
锁分段技术就是容器中使用多把锁，每个锁用于容器中的部分数据。这样当多个线程并发访问不同数据段的数据时，线程就不会竞争锁，提高并发访问效率。
在ConcurrentHashMap的`put()`方法中，对于向非空桶中加入数据时，才使用同步锁。
```
final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();
    int hash = spread(key.hashCode());
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();
        // 定位的桶中没有元素，不需要同步
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            if (casTabAt(tab, i, null,
                         new Node<K,V>(hash, key, value, null)))
                break;                   // no lock when adding to empty bin
        }
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);
        else {
            V oldVal = null;
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    if (fh >= 0) {
                        binCount = 1;
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
                            if (e.hash == hash &&
                                ((ek = e.key) == key ||
                                 (ek != null && key.equals(ek)))) {
                                oldVal = e.val;
                                if (!onlyIfAbsent)
                                    e.val = value;
                                break;
                            }
                            Node<K,V> pred = e;
                            if ((e = e.next) == null) {
                                pred.next = new Node<K,V>(hash, key,
                                                          value, null);
                                break;
                            }
                        }
                    }
                    else if (f instanceof TreeBin) {
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
                if (binCount >= TREEIFY_THRESHOLD)
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
而ConcurrentHashMap的`get()`方法是没有锁的。这是因为`get()`方法中使用的共享变量都定义成`volatile`类型，而`volatile`类型的变量能够在多线程之间保持可见性，能够保证多个线程读取的时候不会读到过期的值。