---
title: java 多线程
date: 2017-09-05
tags: ["java"]
categories: ["java"]
draft: true
---

## 术语解释
1. CAS(Compare and Swap, 比较并交换)： 
> 解释： CAS操作需要两个输入，一个旧值和一个新值。在操作期间先比较旧值，如果没有变化，才交换成新值，否则不交换。
> 缺点：CAS会存在ABA的问题。如果一个值原来是A，变成了B，又变成了A，那么使用CAS检查时会发现它的值没有变化。
> 解决方案：在变量前边追加版本号，每次变量更新的时候，把版本号加1，那么A->B->A就会编程1A->2B->3C
2. 原子操作是指不可被中断的一个或一系列操作。

## volatile
volatile变量自身具有下列特性：
1. 可见性：对一个volatile变量的读，总能看到线程对这个volatile变量的最后的写入
2. 原子性：对任意单个volatile变量的读/写具有原子性，但类似volatile++这种复合操作不具备原子性。
过多地使用volatile变量会降低程序执行的效率

## synchronized
 Java中每个对象都可以作为锁，具体有：
1. 对于普通同步方法，锁是当前实例对象
2. 对于静态同步方法，锁是当前的类
3. 对于同步方法块，锁是synchronized括号中的对象
**JVM是基于进入和退出Monitor对象来实现方法同步和代码块同步**

# 锁

## 锁的升级与对比
在Java SE1.6中，锁有四种状态：**无锁状态->偏向锁状态->轻量级锁状态->重量级锁状态**。这几种锁会随着竞争而升级，但是不能降级。不能降级的策略是为了提高获得锁和释放锁的效率。
### 偏向锁
偏向锁使用了一种等到竞争出现才释放锁的机制，如果有竞争则升级成轻量级锁。
大多数情况下，锁不仅不存在多线程竞争，而且总是由同一线程多次获得。因此为了降低获取锁的代价，引入偏向锁。当同一线程访问同步块并获得锁时，不需要进行CAS操作来加锁和解锁，只需要测试一下对象头的Mark Word里是否存储着指向当前线程的偏向锁。如果测试成功，表示线程获得了锁。
如果测试失败要再测试一下Mark Word中偏向锁的标志是否设置成1（表示当前是偏向锁）：如果没有，则使用CAS竞争；如果设置了，则尝试使用CAS将对象头的偏向锁执行当前线程。
### 轻量级锁
线程在执行同步块之前，JVM会首先在当前线程的栈桢中创建用于存储锁记录的空间，并将对象头中的Mark Word复制到锁记录中。然后线程尝试使用CAS将对象头中的Mark Word替换为指向锁记录的指针。如果成功，当前线程获得锁。如果失败，表示其他线程竞争锁，当前线程尝试使用自旋来获得锁。
轻量级锁释放时，会使用CAS操作将复制到栈桢中的Mark Word替换回对象头，如果成功，表示没有竞争发生；如果失败，表示当前存在竞争，锁就会膨胀成重量级锁。
### 重量级锁
重量级锁状态下，其他线程试图获取锁时，都会被阻塞，当持有锁的线程释放锁之后，会唤醒被阻塞的线程，这些线程就会进行锁的争夺。
|优点|缺点|使用场景
--------------------
| 锁       | 优点           | 缺点  |  使用场景 |
| -------- |-------------| -----|-------|
| 偏向锁    | 加锁和解锁不需要额外消耗，和执行非同步方法存在纳秒级的差距| 如果线程间存在锁竞争会带来额外的锁撤销的消耗 |适用于只有一个线程访问同步块场景
| 轻量级锁  | 竞争的线程不会阻塞，提高了程序的相应速度|如果始终得不到锁竞争的线程，使用自旋会消耗CPU|追求响应时间，同步块执行速度非常快
| 重量级锁  | 线程竞争不使用自旋，不会消耗CPU |线程阻塞，响应时间慢|追求吞吐量，，同步块执行速度较长

# 线程
## 线程优先级
在Java线程中，通过一个整型成员变量`priority`来控制优先级，范围是从1-10，默认优先级是5。一般情况下优先级高的线程分配时间片的数量要多于优先级低的线程。
针对频繁阻塞的线程需要设置的优先级较高，而需要较多CPU时间的线程，要设置较低的优先级，确保处理器不会被独占。
```
public class Priority {
    private static volatile boolean notStart = true;
    private static volatile boolean notEnd = true;

    public static void main(String [] args) throws InterruptedException {
        List<Job> jobs = new ArrayList<>();
        IntStream.range(0, 10).forEach(item -> {
            int priority = item < 5 ? Thread.MIN_PRIORITY : Thread.MAX_PRIORITY;
            Job job = new Job(priority);
            jobs.add(job);
            Thread thread = new Thread(job, "Thread-" + item);
            thread.setPriority(priority);
            thread.start();
        });
        notStart = false;
        TimeUnit.SECONDS.sleep(10);
        notEnd = false;
        jobs.forEach(j ->
            System.out.println("job priority:" + j.priority + " count:" + j.jobCount)
        );
    }

    static class Job implements Runnable {
        private int priority;
        private long jobCount;
        Job(int priority) {
            this.priority = priority;
        }

        public void run() {
            while (notStart) {
                Thread.yield();
            }

            while (notEnd) {
                Thread.yield();
                jobCount ++;
            }
        }
    }
}
```
运行该实例，可以看到优先级1和10有明显的差距，说明优先级生效了。但是程序正确性不能依赖线程优先级的高低，因为有些操作系统会忽略Java设定的优先级。
```
job priority:1 count:195451
job priority:1 count:195475
job priority:1 count:195460
job priority:1 count:195443
job priority:1 count:195462
job priority:10 count:3726352
job priority:10 count:3729393
job priority:10 count:3710086
job priority:10 count:3733771
job priority:10 count:3731136
```
## Daemon线程
Daemon线程是一种支持型线程，因为它主要被用作后台调度以及支持性工作。当一个Java虚拟机中不存在非Daemon线程时，Java虚拟机将会退出，此时Daemon线程都需要立即终止。
通过调用`Thread.setDaemon(true)`将线程设置为Daemon线程。
> Daemon属性需要在启动线程之前设置才有效，不要依靠finally块中的内容来确保执行关闭或者清理资源。

## 线程使用
### 启动线程
启动线程之前要首先构造线程，在 `Thread` 的构造方法 `public Thread(ThreadGroup group, Runnable target, String name, long stackSize)` 中的参数是最多的。由此可以看到在构造线程时，我们可以定义线程所属的线程组、线程名等信息。
线程对象初始化完成之后，调用其`start()`方法就能启动该线程。 ** 启动线程前最好能为线程设置线程名称，因为在使用jstack分析程序或进行问题排查时，能够多一些启示**

### 线程中断
线程中断可以理解为线程的一个标志位属性，它表示一个运行中的线程是否被其他线程进行了中断操作。线程中断是其他线程通过调用该线程的`interrupt()`方法来中断该线程。
线程可以通过`isInterrupt()`方法来判断是否被中断，并通过调用静态方法`Thread.interrupted()` 对当前线程的中断标志位复位。若线程已经终止，`isInterrupt()` 始终会返回 false
> 对于那些阻塞方法(比如 wait() 和 sleep())而言，当另一个线程调用interrupt()中断该线程时，该线程会从阻塞状态退出并且抛出中断异常。这样，我们就可以捕捉到中断异常，并根据实际情况对该线程从阻塞方法中异常退出而进行一些处理。
>  比如说：线程A获得了锁进入了同步代码块中，但由于条件不足调用 wait() 方法阻塞了。这个时候，线程B执行 threadA.interrupt()请求中断线程A，此时线程A就会抛出InterruptedException，我们就可以在catch中捕获到这个异常并进行相应处理

```java
try {
    // 许多抛出 InterruptedException 的方法在抛出 InterruptedException 前，java虚拟机会先将该线程的中断标志位清楚
    Thread.sleep(10 * 1000);
} catch (InterruptedException e) {
    e.printStackTrace();
    Thread.currentThread().interrupt(); //这样处理比较好，会在抛出异常后，设置中断标志位。
}
```

### 安全终止线程
现在不建议使用 `suspend()`, `resume()` 和 `stop()` 方法控制线程。因为，`suspend()` 方法在调用后，线程不会释放已经占有的资源（锁等），这样容易引发死锁。`stop()` 在终结线程时，不保证线程的资源能够被正确释放。
我们通过中断状态，或者一个布尔变量控制：
```java
class Runner implements Runnable {
    private long i;
    private volatile boolean on = true;

    @Override
    public void run() {
        while (on && !Thread.currentThread().isInterrupted()) {
            i ++;
        }
        System.out.println("i=" + i);
    }

    public void cancel () {
        on = false;
    }
}
```

# 线程间通信
如果线程只是独立地运行，那么可以做的事很少。但是，多个线程能够相互配合完成的工作，才会有巨大的价值。
## volatile 和 synchronized
java 支持多个线程同时访问一个对象或者对象的成员变量，由于每个线程可以拥有这个变量的拷贝（可以提高线程运行的速度），所以线程看到的变量不一定是最新的。
关键字 volatile 用来修饰成员变量，该变量的访问均需从共享内存中获取，而对它的改变必须同步刷新回共享内存，这样才能保证变量对所有线程的可见性。
关键字 synchronized 用来修饰方法或者同步块。它用来确保多个线程在同一时刻，只有一个线程处于方法或同步块中，它保证了线程对变量访问的可见性和排他性。
任意一个对象都有自己的监视器，当这个对象由同步块或者这个对象的同步方法调用时，执行方法的线程必须先获得到该对象的监视器才能进入同步块或者同步方法，而没有获取到监视器的线程将会被阻塞在同步块和同步方法的入口，进入 BLOCKED 状态。
```java
public void syncTest() {
    synchronized (String.class) {
        // do something
    }
}
```

## 等待 / 通知机制
一个线程修改了一个对象的值，而另一个线程感知到变化，然后进行相应的操作，整个过程开始于一个线程，而最终执行的是另一个线程。前者是生产者，后者是消费者。这种模式隔离了“做什么”和“怎么做”，在功能层面实现了解耦。
等待/通知的相关方法是任意Java对象都具备的，因为这些方法定义在 java.lang.Object 上：

方法名称     |  描述 
:-----------|:-------------
notify()    | 通知一个在对象上等待的线程，使其从wait()方法上返回，而返回的前提是该线程获得了锁
notifyAll() | 通知所有等待在该对象上的线程
wait()      | 调用该方法的线程进入WAITING状态，只有等待另外线程的通知或中断才会返回。调用wait()方法后，会释放对象的锁
wait(long)  | 超时等待一段时间，参数是毫秒，如果在这段时间内，没有通知就超时返回
wait(long, int) | 对于超时时间更细粒度的控制，可以达到纳秒

等待 / 通知 机制，是指一个线程A调用了对象O的 wait() 方法进入等待状态，而另一个线程B调用了O的 notify() 或者 notifyAll() 方法，线程A收到通知后从对象O的 wait() 方法返回，进而执行后续操作。上述两个线程通过对象O来完成交互，对象上的 wait() 和 notify/notifyAll() 的关系就如同开关信号，用来完成等待方和通知方的交互工作。

使用 wait()、notify() 和 notifyAll() 需要注意如下细节：
1. 使用wait()、notify() 和 notifyAll() 时需要先对调用对象加锁 
2. 调用wait() 方法后，线程状态由RUNNING变为WAITING，并当前线程放置到对象的等待队列
3. notify() 或 notifyAll() 调用后，等待线程依旧不会从wait() 返回，需要调用notify()  或 notifyAll() 的线程释放锁之后，等待线程才有机会从wait()返回
4. notify()将等待队列中的一个等待线程从等待队列中移动到同步队列中，而notifyAll()则是将等待队列中所有的线程全部移动到同步队列中，被移动的线程从WAITING变为BLOCKED
5. 从wait()方法返回的前提是线程获得了调用对象的锁。

比较经典的用法是 生产者和消费者 模式：
```java
// 消费者
synchronized(object) {
    while (condition) {
        object.wait();
    }
}
// 生产者
synchronized(object) {
    condition = true;
    object.notifyAll();
}
```

## 管道输入 / 输出流
管道输入/输出流用于线程之间的数据传输，其媒介是内存。在Java中的主要实现有： `PipedOutputStream`, `PipedInputStream`, `PipedReader`, `PipedWriter`，前两种面向字节，后两种面向字符。
使用Piped类型的流，必须先进行绑定（调用 connect()）使用方式如下：
```java
public static void main(String [] args) throws IOException {
        PipedWriter out = new PipedWriter();
        PipedReader in = new PipedReader();
        // 连接输入和输出流，否则在使用时会抛出 IOException
        out.connect(in);
        Thread printThread = new Thread(new Print(in), "PrintThread");
        printThread.start();
        int receive;
        while ((receive = System.in.read()) != -1) {
            out .write(receive);
        }
        out.close();
    }

    static class Print implements Runnable {
        private PipedReader in;
        public Print(PipedReader in) {
            this.in = in;
        }
        @Override
        public void run() {
            int receive;
            try {
                while ((receive = in.read()) != -1) {
                    System.out.print((char) receive);
                }
                System.out.println();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
```

## Thread.join()
若一个线程A执行了`thread.join()`：当前线程A等待thread线程终止之后才从`thread.join()`返回。还有两个超时方法：`join(long millis)` 以及 `join(long millis, int nanos)`。
若两个线程相互使用了非超时的`join()`方法，那么会死锁
```java
static class InnerJoin implements Runnable {
    public Thread thread;
    public static volatile int i;
    public void run() {
        while (i < 10) {
            System.out.println( Thread.currentThread().getName() + ": " + i);
            i++;
            try {
                thread.join(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public static void main (String [] args) {
    // 死锁
    InnerJoin innerA = new InnerJoin();
    InnerJoin innerB = new InnerJoin();
    Thread threadA = new Thread(innerA, "thread A");
    Thread threadB = new Thread(innerB, "thread B");
    innerA.thread = threadB;
    innerB.thread = threadA;
    threadA.start();
    threadB.start();
}
```

## ThreadLocal
线程变量（ThreadLocal)，是一个以ThreadLocal对象为键、任意对象为值的存储结构。这个结构被附带在线程上，也就是说一个线程可以根据一个ThreadLocal对象查询到绑定到这个线程上的值。
某一线程的ThreadLocal只能被该线程访问，一般情况下其他线程访问不到,其使用方式如下：
```java
private void testThreadLocal() {
    Thread t = new Thread() {
        ThreadLocal<String> mStringThreadLocal = new ThreadLocal<>() {
            @Override
            protected String initialValue() {
              return Thread.currentThread().getName();
            }
        };

        @Override
        public void run() {
            super.run();
            mStringThreadLocal.set("droidyue.com");
            mStringThreadLocal.get();
        }
    };

    t.start();
}
```

# 锁
## Lock接口
锁是用来控制多个线程访问共享资源的。锁能够防止多个线程同时访问共享资源，但是读写锁等可以允许多个线程并发访问共享资源。使用 synchronized 将会隐式地获取锁，但是它将锁的获取和释放固化了，必须先获取再释放。
Lock使用方式如下：
```java
Lock lock = new ReentrantLock();
lock.lock();
try {

} finally {
    lock.unlock();
}
```

Lock 接口具有不同于 synchronized 关键字不具备的特性：

特性               |  描述
:-----————————————|:----------------
尝试非阻塞地获取锁   | 当前线程尝试获取锁，如果这一时刻锁没有被其他线程获取到，则成功获取并持有锁
能被中断地获取锁     | 与 synchronized 不同，获取到锁的线程能够响应中断，当获取到锁的线程被中断时，中断异常将会抛出，同时锁会被释放
超时获取锁          | 当指定的截止时间之前获取锁，如果截止时间仍无法获取锁，则返回

Lock 接口的 API 如下：
```java


```

