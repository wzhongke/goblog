---
title: netty 实战
date: 2020-04-15 18:51:00
tags: ["java"]
categories: ["java"]
---

# 异步和事件驱动
Netty是一款**异步的事件驱动的网络**应用程序框架，支持快速地开发可维护的**高性能的面向协议的服务器和客户端**

高性能的系统不仅要求超一流的编程技巧，还需要网络编程、多线程处理和并发的专业知识。

一个既是异步的又是事件驱动的系统会表现出一种特殊的、对我们来说极具价值的行为：它可以以任意的顺序响应在任意的时间点产生的事件。

Netty 的设计底蕴：
- 非阻塞网络调用使得我们可以不必等待一个操作的完成。完全异步的I/O正是基于这个特性构建的，并且更进一步：异步方法会立即返回，并且在它完成时，会直接或者在稍后的某个时间点通知用户。
- 选择器使得我们能够通过较少的线程便可监视许多连接上的事件。


Netty通过触发事件将Selector从应用程序中抽象出来，消除了所有本来将需要手动编写的派发代码。在内部，将会为每个Channel分配一个EventLoop，用以处理所有事件，包括：
- 注册感兴趣的事件；
- 将事件派发给ChannelHandler；
- 安排进一步的动作

# netty 源码

```java
// 是 2 的指数
private static boolean isPowerOfTwo(int val) {
    return (val & -val) == val;
}
```

`MultithreadEventExecutorGroup` 类中存贮着如下内容:
- `EventExecutor[] children`: EventExecutor
- `Set<EventExecutor> readonlyChildren`
- `AtomicInteger terminatedChildren`
- `Promise<?> terminationFuture`
- `EventExecutorChooserFactory.EventExecutorChooser chooser`

# `NioEventLoopGroup`
以下代码初始化了 `NioEventLoopGroup` 类，netty 做了很多工作
```java
EventLoopGroup bossGroup = new NioEventLoopGroup();
```

跟踪 `new NioEventLoopGroup()`:

```java
public NioEventLoopGroup(int nThreads, Executor executor, final SelectorProvider selectorProvider,
                         final SelectStrategyFactory selectStrategyFactory) {
    super(nThreads, executor, selectorProvider, selectStrategyFactory, RejectedExecutionHandlers.reject());
}
```

发现其只是调用了父类的初始化方法，`chooserFactory` 使用的是 `DefaultEventExecutorChooserFactory`:

```java
protected MultithreadEventLoopGroup(int nThreads, Executor executor, Object... args) {
    // 线程数默认使用的是 CPU 核的2倍
    super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, args);
}

protected MultithreadEventExecutorGroup(int nThreads, Executor executor,
                                        EventExecutorChooserFactory chooserFactory, Object... args) {
    if (nThreads <= 0) {
        throw new IllegalArgumentException(String.format("nThreads: %d (expected: > 0)", nThreads));
    }

    if (executor == null) {
        // 使用默认的 线程工厂 初始化执行器
        executor = new ThreadPerTaskExecutor(newDefaultThreadFactory());
    }

    children = new EventExecutor[nThreads];

    for (int i = 0; i < nThreads; i ++) {
        boolean success = false;
        try {
            // 初始化执行器，返回的是 NioEventLoop 实例
            children[i] = newChild(executor, args);
            success = true;
        } catch (Exception e) {
            // TODO: Think about if this is a good exception type
            throw new IllegalStateException("failed to create a child event loop", e);
        } finally {
            if (!success) {
                for (int j = 0; j < i; j ++) {
                    children[j].shutdownGracefully();
                }

                for (int j = 0; j < i; j ++) {
                    EventExecutor e = children[j];
                    try {
                        while (!e.isTerminated()) {
                            e.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                        }
                    } catch (InterruptedException interrupted) {
                        // Let the caller handle the interruption.
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    // 工作线程选择器
    chooser = chooserFactory.newChooser(children);

    // 关闭监听器
    final FutureListener<Object> terminationListener = new FutureListener<Object>() {
        @Override
        public void operationComplete(Future<Object> future) throws Exception {
            if (terminatedChildren.incrementAndGet() == children.length) {
                terminationFuture.setSuccess(null);
            }
        }
    };

    for (EventExecutor e: children) {
        e.terminationFuture().addListener(terminationListener);
    }

    Set<EventExecutor> childrenSet = new LinkedHashSet<EventExecutor>(children.length);
    Collections.addAll(childrenSet, children);
    readonlyChildren = Collections.unmodifiableSet(childrenSet);
}
```

`newChild` 方法创建 `NioEventLoop extends SingleThreadEventLoop` 对象

```java
protected EventLoop newChild(Executor executor, Object... args) throws Exception {
    EventLoopTaskQueueFactory queueFactory = args.length == 4 ? (EventLoopTaskQueueFactory) args[3] : null;
    return new NioEventLoop(this, executor, (SelectorProvider) args[0],
        ((SelectStrategyFactory) args[1]).newSelectStrategy(), (RejectedExecutionHandler) args[2], queueFactory);
}
```

# `ServerBootstrap()`

```java
ServerBootstrap b = new ServerBootstrap();
// bossGroup 是 parentGroup, workerGroup 是子group
b.group(bossGroup, workerGroup)
	// 初始化 channelFactory，新建 channel 时使用 NioServerSocketChannel 类
	.channel(NioServerSocketChannel.class)
	.childHandler(handler)
	// option 提供给NioServerSocketChannel用来接收进来的连接
	.option(ChannelOption.SO_BACKLOG, 128)
	// childOption 是对父管道ServerChannel接收到的连接的配置
	.childOption(ChannelOption.SO_KEEPALIVE, true);

// 绑定端口并开始接受请求
ChannelFuture f = b.bind(port).sync();
```

## `channel`

`channel(NioServerSocketChannel.class)` 定义了使用哪个 channel，主要内容是初始化了一个 channelFactory:

```java
public B channel(Class<? extends C> channelClass) {
    return channelFactory(new ReflectiveChannelFactory<C>(
            ObjectUtil.checkNotNull(channelClass, "channelClass")
    ));
}
```

`ReflectiveChannelFactory` 主要有两个内容：
```java
// 构造函数，定义了该工厂初始化哪个类
public ReflectiveChannelFactory(Class<? extends T> clazz) {
    ObjectUtil.checkNotNull(clazz, "clazz");
    try {
        this.constructor = clazz.getConstructor();
    } catch (NoSuchMethodException e) {
        throw new IllegalArgumentException("Class " + StringUtil.simpleClassName(clazz) +
                " does not have a public non-arg constructor", e);
    }
}
// 使用默认构造器生成 channel 的实例
@Override
public T newChannel() {
    try {
        return constructor.newInstance();
    } catch (Throwable t) {
        throw new ChannelException("Unable to create Channel from class " + constructor.getDeclaringClass(), t);
    }
}
```

`childHandler`、`option`、`childOption` 只是将参数保存到 `ServerBootstrap`

## `bind(port)`
`bind(port)` 的主要代码在 `AbstractBootstrap.doBind()` 中
```java
private ChannelFuture doBind(final SocketAddress localAddress) {
	// 使用 ReflectiveChannelFactory 初始化 channel，
    final ChannelFuture regFuture = initAndRegister();
    final Channel channel = regFuture.channel();
    if (regFuture.cause() != null) {
        return regFuture;
    }

    if (regFuture.isDone()) {
        // At this point we know that the registration was complete and successful.
        ChannelPromise promise = channel.newPromise();
        doBind0(regFuture, channel, localAddress, promise);
        return promise;
    } else {
        // Registration future is almost always fulfilled already, but just in case it's not.
        final PendingRegistrationPromise promise = new PendingRegistrationPromise(channel);
        regFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Throwable cause = future.cause();
                if (cause != null) {
                    // Registration on the EventLoop failed so fail the ChannelPromise directly to not cause an
                    // IllegalStateException once we try to access the EventLoop of the Channel.
                    promise.setFailure(cause);
                } else {
                    // Registration was successful, so set the correct executor to use.
                    // See https://github.com/netty/netty/issues/2586
                    promise.registered();

                    doBind0(regFuture, channel, localAddress, promise);
                }
            }
        });
        return promise;
    }
}
```

## 初始化 channel
```java
final ChannelFuture initAndRegister() {
    Channel channel = null;
    try {
    	// 使用工厂方法初始化 channel，也即是初始化 `NioServerSocketChannel`
        channel = channelFactory.newChannel();
        init(channel);
    } catch (Throwable t) {
        if (channel != null) {
            // channel can be null if newChannel crashed (eg SocketException("too many open files"))
            channel.unsafe().closeForcibly();
            // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
            return new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE).setFailure(t);
        }
        // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
        return new DefaultChannelPromise(new FailedChannel(), GlobalEventExecutor.INSTANCE).setFailure(t);
    }

    // 在 NioEventLoop (MultithreadEventLoopGroup) 中注册 channel
    ChannelFuture regFuture = config().group().register(channel);
    if (regFuture.cause() != null) {
        if (channel.isRegistered()) {
            channel.close();
        } else {
            channel.unsafe().closeForcibly();
        }
    }

    // If we are here and the promise is not failed, it's one of the following cases:
    // 1) If we attempted registration from the event loop, the registration has been completed at this point.
    //    i.e. It's safe to attempt bind() or connect() now because the channel has been registered.
    // 2) If we attempted registration from the other thread, the registration request has been successfully
    //    added to the event loop's task queue for later execution.
    //    i.e. It's safe to attempt bind() or connect() now:
    //         because bind() or connect() will be executed *after* the scheduled registration task is executed
    //         because register(), bind(), and connect() are all bound to the same thread.

    return regFuture;
}
```

`channelFactory.newChannel()` 使用默认构造器创建了 `NioServerSocketChannel` 对象
初始化 `NioServerSocketChannel`，打开 serverSocketChannel，并在其父类中初始化 `pipeline` 以及 初始化 `NioServerSocketChannelConfig`:
```java
protected AbstractChannel(Channel parent) {
    this.parent = parent;
    id = newId();
    // 不同子类实现各自的 AbstractUnsafe，是消息收发的实现。nio 中在 `AbstractNioMessageChannel` 类中
    unsafe = newUnsafe();
    // 初始化 pipeline
    pipeline = newChannelPipeline();
}
```

初始化 `ChannelPipeline`:
```java
protected DefaultChannelPipeline(Channel channel) {
    this.channel = ObjectUtil.checkNotNull(channel, "channel");
    succeededFuture = new SucceededChannelFuture(channel, null);
    voidPromise =  new VoidChannelPromise(channel, true);
    // pipeline 的头和尾
    tail = new TailContext(this);
    head = new HeadContext(this);

    head.next = tail;
    tail.prev = head;
}
```

在 `init(channel)` 中主要在 pipeline 中添加了如下的 channel：

```java
p.addLast(new ChannelInitializer<Channel>() {
    @Override
    public void initChannel(final Channel ch) {
        final ChannelPipeline pipeline = ch.pipeline();
        ChannelHandler handler = config.handler();
        if (handler != null) {
            pipeline.addLast(handler);
        }

        ch.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                pipeline.addLast(new ServerBootstrapAcceptor(
                        ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
            }
        });
    }
});
```

```java
// SingleThreadEventLoop 注册 channel
public ChannelFuture register(Channel channel) {
    return register(channel, new DefaultChannelPromise(channel, this));
}

public ChannelFuture register(final Channel channel, final ChannelPromise promise) {
    if (channel == null) {
        throw new NullPointerException("channel");
    }
    if (promise == null) {
        throw new NullPointerException("promise");
    }
    // AbstractNioMessageChannel.NioMessageUnsafe
    channel.unsafe().register(this, promise);
    return promise;
}

// AbstractChannel.AbstractUnsafe
private void register0(ChannelPromise promise) {
    try {
        // check if the channel is still open as it could be closed in the mean time when the register
        // call was outside of the eventLoop
        if (!promise.setUncancellable() || !ensureOpen(promise)) {
            return;
        }
        boolean firstRegistration = neverRegistered;
        doRegister();
        neverRegistered = false;
        registered = true;

        // Ensure we call handlerAdded(...) before we actually notify the promise. This is needed as the
        // user may already fire events through the pipeline in the ChannelFutureListener.
        pipeline.invokeHandlerAddedIfNeeded();

        safeSetSuccess(promise);
        // pipeline 中传播 注册的消息
        pipeline.fireChannelRegistered();
        // Only fire a channelActive if the channel has never been registered. This prevents firing
        // multiple channel actives if the channel is deregistered and re-registered.
        if (isActive()) {
            if (firstRegistration) {
                pipeline.fireChannelActive();
            } else if (config().isAutoRead()) {
                // This channel was registered before and autoRead() is set. This means we need to begin read
                // again so that we process inbound data.
                //
                // See https://github.com/netty/netty/issues/4805
                beginRead();
            }
        }
    } catch (Throwable t) {
        // Close the channel directly to avoid FD leak.
        closeForcibly();
        closeFuture.setClosed();
        safeSetFailure(promise, t);
    }
}
```

`channel.unsafe().register(this, promise);` 是在 `AbstractNioMessageChannel.NioMessageUnsafe` 中注册了 channel

```java
// AbstractNioChannel 注册 channel
protected void doRegister() throws Exception {
    boolean selected = false;
    for (;;) {
        try {
            selectionKey = javaChannel().register(((NioEventLoop) eventLoop().unwrap()).selector, 0, this);
            return;
        } catch (CancelledKeyException e) {
            if (!selected) {
                // Force the Selector to select now as the "canceled" SelectionKey may still be
                // cached and not removed because no Select.select(..) operation was called yet.
                ((NioEventLoop) eventLoop().unwrap()).selectNow();
                selected = true;
            } else {
                // We forced a select operation on the selector before but the SelectionKey is still cached
                // for whatever reason. JDK bug ?
                throw e;
            }
        }
    }
}
```

`ServerBootstrap` 主要的逻辑在 `AbstractBootstrap` 类中

`AbstractBootstrap.doBind()` 完成了绑定端口的操作

并通过映射新建了 `NioServerSocketChannel` 类，初始化的过程中监听 socket，使用 `new DefaultChannelPipeline(this)` 初始化 pipeline，并将自己添加到 `pipeline` 中。

一个 channel 对应一个 pipeline

`DefaultChannelPipeline.addLast0()` 将 channelHandler 加到 pipeline 中




