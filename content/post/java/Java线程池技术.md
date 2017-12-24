---
title: java线程池技术
date: 2017-07-03 19:01:11
tags: ["java"]
categories: ["java"]
---

服务器在处理客户端请求时，经常面对的是客户端的任务简单，单一。如果针对每个任务，都创建一个线程执行，那么对于成千上万的客户端任务，服务器会创建数以万计的线程。这会使得操作系统频繁地进行线程的上下文切换，增加系统负载，浪费系统资源。
线程池技术很好地解决了这个问题，它预先创建了若干个线程。用这些线程处理客户端提交的任务，避免了频繁的线程创建和销毁的系统开销。

下面是一个简单的线程池接口定义
```java
public interface ThreadPool<Job extends Runnable> {
    // 执行一个Job，这个Job需要实现Runnable
    void execute(Job job);
    void shutdown();
    void addWorkers(int num);
    void removeWorkers(int num);
    int getJobSize();
}
```
<!-- more -->
客户端可以通过execute来将任务提交到线程池。线程池提供了减少/增大工作线程以及关闭线程的方法。这里工作线程代表着一个重复执行Job的线程，每个有客户端提交的Job都将进入到一个工作队列中，等待工作线程处理。
```java
package com.wang.chapter4.threadpool;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 王忠珂 on 2016/11/23.
 */
public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {
    // 线程最大限制数
    private static int maxWorkerNumber = 10;
    // 线程池默认的数量
    private static int defaultWorkerNumbers = 5;
    // 线程池最小数量
    private static int minWorkerNumbers = 1;
    // 工作列表
    private final LinkedList<Job> jobs  = new LinkedList<Job>();
    // 工作者列表
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());
    // 工作者线程的数量
    private int workerNum = defaultWorkerNumbers;
    // 线程编号
    private AtomicLong threadNum = new AtomicLong();

    public DefaultThreadPool() {
        initializeWorkers(defaultWorkerNumbers);
    }

    public DefaultThreadPool(int num) {
        workerNum = num > maxWorkerNumber ? maxWorkerNumber :
                num < minWorkerNumbers ? minWorkerNumbers : num;
        initializeWorkers(workerNum);
    }

    public DefaultThreadPool(int defaultWorkerNumber, int maxWorkerNumber, int minWorkerNumber) {
        this.maxWorkerNumber = maxWorkerNumber;
        this.minWorkerNumbers = minWorkerNumber;
        workerNum = defaultWorkerNumber > maxWorkerNumber ? maxWorkerNumber :
                defaultWorkerNumber < minWorkerNumbers ? minWorkerNumbers : defaultWorkerNumber;
        initializeWorkers(workerNum);
    }

    @Override
    public void execute(Job job) {
        if (job != null) {
            synchronized (jobs) {
                jobs.addLast(job);
                jobs.notify();
            }
        }
    }

    @Override
    public void shutdown() {
        for (Worker worker: workers) {
            worker.shutdown();
        }
    }

    @Override
    public void addWorkers(int num) {
        synchronized (jobs) {
            if (num + this.workerNum > maxWorkerNumber) {
                num = maxWorkerNumber - this.workerNum;
            }
            initializeWorkers(num);
            this.workerNum += num;
        }
    }

    @Override
    public void removeWorkers(int num) {
        synchronized (jobs) {
            if (num >= this.workerNum) {
                throw new IllegalArgumentException("beyond workNum");
            }
            int count = 0;
            while (count < num) {
                Worker worker = workers.get(count);
                if (workers.remove(worker)) {
                    worker.shutdown();
                    count ++;
                }
            }
            this.workerNum -= count;
        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }

    private void initializeWorkers (int num) {
        for (int i=0; i<num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }

    class Worker implements Runnable {
        // 是否工作
        private volatile boolean running = true;
        public void run() {
            while (running){
                Job job = null;
                synchronized (jobs) {
                    while (jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    // 取出一个Job
                    job = jobs.removeFirst();
                }
                if (job != null) {
                    try {
                        job.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void shutdown() {
            running = false;
        }
    }
}


```