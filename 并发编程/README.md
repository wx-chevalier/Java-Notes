> 在阅读本篇之前，建议先阅读[《并发编程导论](https://ng-tech.icu/Concurrent-Series/#/)》。

# Java 并发编程

![Java 并发编程思维脑图](https://s2.ax1x.com/2019/09/02/nCLmb4.png)

> 更多 Java 应用程序性能调优参阅《[JVM 与性能优化](../JVM%20与性能优化)》

# J.U.C 概览

JSR，全称 Java Specification Requests， 即 Java 规范提案， 主要是用于向 JCP(Java Community Process)提出新增标准化技术规范的正式请求。每次 JAVA 版本更新都会有对应的 JSR 更新，比如在 Java 8 版本中，其新特性 Lambda 表达式对应的是 JSR 335，新的日期和时间 API 对应的是 JSR 310。当然，本文的关注点仅仅是 JSR 166，它是一个关于 Java 并发编程的规范提案，在 JDK 中，该规范由 java.util.concurrent 包实现，是在 JDK 5.0 的时候被引入的；另外 JDK6 引入 Deques、Navigable collections，对应的是 JSR 166x，JDK7 引入 fork-join 框架，用于并行执行任务，对应的是 JSR 166y。

J.U.C. 即 java.util.concurrent 的缩写，该包参考自 EDU.oswego.cs.dl.util.concurrent，是 JSR 166 标准规范的一个实现；

## Executor 框架（线程池、 Callable 、Future）

简单的说，就是一个任务的执行和调度框架，涉及的类如下图所示：

![Executor 类结构](https://s3.ax1x.com/2021/01/29/yiiSds.png)

其中，最顶层是 Executor 接口，它的定义很简单，一个用于执行任务的 execute 方法，如下所示：

```java
public interface Executor {
    void execute(Runnable command);
}
```

另外，我们还可以看到一个 Executors 类，它是一个工具类（有点类似集合框架的 Collections 类），用于创建 ExecutorService、ScheduledExecutorService、ThreadFactory 和 Callable 对象。任务的提交过程与执行过程解耦，用户只需定义好任务提交，具体如何执行，什么时候执行不需要关心；定义好任务（如 Callable 对象），把它提交给 ExecutorService（如线程池）去执行，得到 Future 对象，然后调用 Future 的 get 方法等待执行结果即可。

任务就是实现 Callable 接口或 Runnable 接口的类，其实例就可以成为一个任务提交给 ExecutorService 去执行；其中 Callable 任务可以返回执行结果，Runnable 任务无返回结果；通过 Executors 工具类可以创建各种类型的线程池，如下为常见的四种：

- newCachedThreadPool：大小不受限，当线程释放时，可重用该线程；
- newFixedThreadPool：大小固定，无可用线程时，任务需等待，直到有可用线程；
- newSingleThreadExecutor：创建一个单线程，任务会按顺序依次执行；
- newScheduledThreadPool：创建一个定长线程池，支持定时及周期性任务执行

简单示例如下：

```java
ExecutorService executor = Executors.newCachedThreadPool();//创建线程池
Task task = new Task(); //创建Callable任务
Future<Integer> result = executor.submit(task);//提交任务给线程池执行
result.get()；//等待执行结果; 可以传入等待时间参数，指定时间内没返回的话，直接结束
```

最后我们讨论下批量任务的执行方式：

- 首先定义任务集合，然后定义 Future 集合用于存放执行结果，执行任务，最后遍历 Future 集合获取结果。优点：可以依次得到有序的结果；缺点：不能及时获取已完成任务的执行结果；
- 首先定义任务集合，通过 CompletionService 包装 ExecutorService，执行任务，然后调用其 take()方法去取 Future 对象。优点：及时得到已完成任务的执行结果，缺点：不能依次得到结果。

在方式一中，从集合中遍历的每个 Future 对象并不一定处于完成状态，这时调用 get()方法就会被阻塞住，所以后面的任务即使已完成也不能得到结果；而方式二中，CompletionService 的实现是维护一个保存 Future 对象的 BlockingQueue，只有当这个 Future 对象状态是结束的时候，才会加入到这个 Queue 中，所以调用 take()能从阻塞队列中拿到最新的已完成任务的结果；

## AbstractQueuedSynchronizer （AQS 框架）

AQS 框架是 J.U.C 中实现锁及同步机制的基础，其底层是通过调用 LockSupport.unpark() 和 LockSupport.park() 实现线程的阻塞和唤醒。

# TBD

- https://mp.weixin.qq.com/s/w-C9QkMQhgnAChnRrsrXIw
- https://www.jianshu.com/p/3f6b26ee51ce
- [concurrency-torture-testing-your-code-within-the-java-memory-model](http://zeroturnaround.com/rebellabs/concurrency-torture-testing-your-code-within-the-java-memory-model/)
- https://zhuanlan.zhihu.com/p/91788985
