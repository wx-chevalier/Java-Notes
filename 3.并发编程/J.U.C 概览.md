# J.U.C 概览

JSR，全称 Java Specification Requests，即 Java 规范提案，主要是用于向 JCP(Java Community Process)提出新增标准化技术规范的正式请求。每次 JAVA 版本更新都会有对应的 JSR 更新，比如在 Java 8 版本中，其新特性 Lambda 表达式对应的是 JSR 335，新的日期和时间 API 对应的是 JSR 310。当然，本文的关注点仅仅是 JSR 166，它是一个关于 Java 并发编程的规范提案，在 JDK 中，该规范由 java.util.concurrent 包实现，是在 JDK 5.0 的时候被引入的；另外 JDK6 引入 Deques、Navigable collections，对应的是 JSR 166x，JDK7 引入 fork-join 框架，用于并行执行任务，对应的是 JSR 166y。

J.U.C. 即 java.util.concurrent 的缩写，该包参考自 EDU.oswego.cs.dl.util.concurrent，是 JSR 166 标准规范的一个实现；

# Executor 框架（线程池、 Callable 、Future）

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

# AbstractQueuedSynchronizer（AQS 框架）

AQS 框架是 J.U.C 中实现锁及同步机制的基础，其底层是通过调用 LockSupport.unpark() 和 LockSupport.park() 实现线程的阻塞和唤醒。AbstractQueuedSynchronizer 是一个抽象类，主要是维护了一个 int 类型的 state 属性和一个非阻塞、先进先出的线程等待队列；其中 state 是用 volatile 修饰的，保证线程之间的可见性，队列的入队和出对操作都是无锁操作，基于自旋锁和 CAS 实现；另外 AQS 分为两种模式：独占模式和共享模式，像 ReentrantLock 是基于独占模式模式实现的，CountDownLatch、CyclicBarrier 等是基于共享模式。

![AQS 简单示意](https://s3.ax1x.com/2021/01/29/yin3lR.png)

非公平锁的 lock 方法的实现：

```java
final void lock() {
      // CAS 操作，如果 State 为 0（表示当前没有其它线程占有该锁），则将它设置为1
      if (compareAndSetState(0, 1))
          setExclusiveOwnerThread(Thread.currentThread());
      else
          acquire(1);
}
```

首先是不管先后顺序，直接尝试获取锁（非公平的体现)，成功的话，直接独占访问；如果获取锁失败，则调用 AQS 的 acquire 方法，在该方法内部会调用 tryAcquire 方法再次尝试获取锁以及是否可重入判断，如果失败，则挂起当前线程并加入到等待队列；

## 可重入

什么是可重入锁，不可重入锁呢？"重入"字面意思已经很明显了，就是可以重新进入。可重入锁，就是说一个线程在获取某个锁后，还可以继续获取该锁，即允许一个线程多次获取同一个锁。比如 synchronized 内置锁就是可重入的，如果 A 类有 2 个 synchornized 方法 method1 和 method2，那么 method1 调用 method2 是允许的。显然重入锁给编程带来了极大的方便。假如内置锁不是可重入的，那么导致的问题是：1 个类的 synchornized 方法不能调用本类其他 synchornized 方法，也不能调用父类中的 synchornized 方法。与内置锁对应，JDK 提供的显示锁 ReentrantLock 也是可以重入的。

一个线程获取多少次锁，就必须释放多少次锁。这对于内置锁也是适用的，每一次进入和离开 synchornized 方法(代码块)，就是一次完整的锁获取和释放。

# Locks & Condition（锁和条件变量）

先看一下 Lock 接口提供的主要方法，如下：

- lock() 等待获取锁
- lockInterruptibly() 可中断等待获取锁，synchronized 无法实现可中断等待
- tryLock() 尝试获取锁，立即返回 true 或 false
- tryLock(long time, TimeUnit unit) 指定时间内等待获取锁
- unlock() 释放锁
- newCondition() 返回一个绑定到此 Lock 实例上的 Condition 实例

关于 Lock 接口的实现，我们主要是关注以下几个类：

- ReentrantLock：可重入锁，所谓的可重入锁，也叫递归锁，是指一个线程获取锁后，再次获取该锁时，不需要重新等待获取。ReentrantLock 分为公平锁和非公平锁，公平锁指的是严格按照先来先得的顺序排队等待去获取锁，而非公平锁每次获取锁时，是先直接尝试获取锁，获取不到，再按照先来先得的顺序排队等待。

- ReentrantReadWriteLock：可重入读写锁，指的是没有线程进行写操作时，多个线程可同时进行读操作，当有线程进行写操作时，其它读写操作只能等待。即“读-读能共存，读-写不能共存，写-写不能共存”。在读多于写的情况下，读写锁能够提供比排它锁更好的并发性和吞吐量。

Condition 对象是由 Lock 对象创建的，一个 Lock 对象可以创建多个 Condition，其实 Lock 和 Condition 都是基于 AQS 实现的。Condition 对象主要用于线程的等待和唤醒，在 JDK 5 之前，线程的等待唤醒是用 Object 对象的 wait/notify/notifyAll 方法实现的，使用起来不是很方便；在 JDK5 之后，J.U.C 包提供了 Condition，其中：

- Condition.await 对应于 Object.wait；
- Condition.signal 对应于 Object.notify；
- Condition.signalAll 对应于 Object.notifyAll；

使用 Condition 对象有一个比较明显的好处是一个锁可以创建多个 Condition 对象，我们可以给某类线程分配一个 Condition，然后就可以唤醒特定类的线程。

# Synchronizers（同步器）

J.U.C 中的同步器主要用于协助线程同步，有以下四种：

## 闭锁 CountDownLatch

闭锁主要用于让一个主线程等待一组事件发生后继续执行，这里的事件其实就是指 CountDownLatch 对象的 countDown 方法。注意其它线程调用完 countDown 方法后，是会继续执行的，具体如下图所示：

![CountDownLatch](https://s3.ax1x.com/2021/01/29/yiMwhF.png)

在 CountDownLatch 内部，包含一个计数器，一开始初始化为一个整数（事件个数），发生一个事件后，调用 countDown 方法，计数器减 1，await 用于等待计数器为 0 后继续执行当前线程。

## 栅栏 CyclicBarrier

栅栏主要用于等待其它线程，且会阻塞自己当前线程，所有线程必须同时到达栅栏位置后，才能继续执行；且在所有线程到达栅栏处，可以触发执行另外一个预先设置的线程，具体如下图所示：

![CyclicBarrier](https://s3.ax1x.com/2021/01/29/yiQ34O.png)

在上图中，T1、T2、T3 每调用一次 await，计数减减，且在它们调用 await 方法的时候，如果计数不为 0，会阻塞自己的线程；另外，TA 线程会在所有线程到达栅栏处（计数为 0）的时候，才开始执行。

## 信号量 Semaphore

信号量主要用于控制访问资源的线程个数，常常用于实现资源池，如数据库连接池，线程池。在 Semaphore 中，acquire 方法用于获取资源，有的话，继续执行（使用结束后，记得释放资源），没有资源的话将阻塞直到有其它线程调用 release 方法释放资源；

![Semaphore](https://s3.ax1x.com/2021/01/29/yiQUKA.png)

## 交换器 Exchanger

交换器主要用于线程之间进行数据交换；当两个线程都到达共同的同步点（都执行到 exchanger.exchange 的时刻）时，发生数据交换，否则会等待直到其它线程到达；

![Exchanger](https://s3.ax1x.com/2021/01/29/yiQoPU.png)

# Atomic Variables（原子变量）

原子变量主要是方便程序员在多线程环境下，无锁的进行原子操作；原子类是基于 Unsafe 实现的包装类，核心操作是 CAS 原子操作；所谓的 CAS 操作，即 compare and swap，指的是将预期值与当前变量的值比较(compare)，如果相等则使用新值替换(swap)当前变量，否则不作操作；我们可以摘取一段 AtomicInteger 的源码，如下：

```java
public final boolean compareAndSet(int expect, int update) {
    return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
}
```

在 compareAndSwapInt 方法中，valueOffset 是内存地址，expect 是预期值，update 是更新值，如果 valueOffset 地址处的值与预期值相等，则将 valueOffset 地址处的值更新为 update 值。现代 CPU 已广泛支持 CAS 指令；在 Java 中，有四种原子更新方式，如下：

- 原子方式更新基本类型：AtomicInteger、 AtomicLong 等
- 原子方式更新数组：AtomicIntegerArray、 AtomicLongArray 等
- 原子方式更新引用：AtomicReference、 AtomicReferenceFieldUpdater…
- 原子方式更新字段：AtomicIntegerFieldUpdater、 AtomicStampedReference(解决 CAS 的 ABA 问题)

# BlockingQueue（阻塞队列）

阻塞队列提供了可阻塞的入队和出对操作，如果队列满了，入队操作将阻塞直到有空间可用，如果队列空了，出队操作将阻塞直到有元素可用；

在 Java 中，主要有以下类型的阻塞队列：

- ArrayBlockingQueue：一个由数组结构组成的有界阻塞队列。
- LinkedBlockingQueue：一个由链表结构组成的有界阻塞队列。
- PriorityBlockingQueue：一个支持优先级排序的无界阻塞队列。
- DelayQueue：一个支持延时获取元素的无界阻塞队列。
- SynchronousQueue：一个不存储元素的阻塞队列。
- LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。
- LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。

# Concurrent Collections（并发容器）

说到并发容器，不得不提同步容器，在 JDK5 之前，为了线程安全，我们一般都是使用同步容器，同步容器主要有以下缺点：

- 同步容器对所有容器状态的访问都串行化，严重降低了并发性；
- 某些复合操作，仍然需要加锁来保护
- 迭代期间，若其它线程并发修改该容器，会抛出 ConcurrentModificationException 异常，即快速失败机制

对于复合操作，我们可以举个例子, 因为比较容易被忽视，如下代码：

```java
public static Integer getLast(Vector<Integer> list){
    int lastIndex = list.size() - 1;
    if(lastIndex < 0) return null;
    return list.get(lastIndex);
}
```

在以上代码中，虽然 list 集合是 Vector 类型，但该方法仍然不是原子操作，因为在 list.size()和 list.get(lastIndex)之间，可能已经发生了很多事。

## ConcurrentHashMap

ConcurrentHashMap 是采用分离锁技术，在同步容器中，是一个容器一个锁，但在 ConcurrentHashMap 中，会将 hash 表的数组部分分成若干段，每段维护一个锁；这些段可以并发的进行写操作，以达到高效的并发访问，如下图示例：

![ConcurrentHashMap 分段锁](https://s3.ax1x.com/2021/01/30/yFJgXD.md.png)

另外，性能是我们比较关心的，我们可以与同步容器做个对比，如下图所示：

![并发容器与同步容器对比](https://s3.ax1x.com/2021/01/30/yFYdv8.png)

## CopyOnWriteArrayList/Set

也叫拷贝容器，指的是写数据的时候，重新拷贝一份进行写操作，完成后，再将原容器的引用指向新的拷贝容器。适用情况：当读操作远远大于写操作的时候，考虑用这个并发集合。

# Fork/Join 并行计算框架

Fork/Join 框架的核心是 ForkJoinPool 类，实现了工作窃取算法（对那些处理完自身任务的线程，会从其它线程窃取任务执行）并且能够执行 ForkJoinTask 任务。适用场景：大任务能被递归拆分成多个子任务的应用；如下图，位于图上部的 Task 依赖于位于其下的 Task 的执行，只有当所有的子任务都完成之后，调用者才能获得 Task 0 的返回结果。其实这是一种分而治之的思想：

![Fork/Join 示意图](https://s3.ax1x.com/2021/01/30/yFY65n.png)

其实对于使用 Fork/Join 框架的开发人员来说，主要任务还是在于任务划分，可以参考如下伪代码：

```java
if (任务足够小){
  直接执行该任务;
}else{
  将任务拆分成多个子任务;
  执行这些子任务并等待结果;
}
```

# TimeUnit 枚举

TimeUnit 是 java.util.concurrent 包下面的一个枚举类，TimeUnit 提供了可读性更好的线程暂停操作。在 JDK5 之前，一般我们暂停线程是这样写的：

```java
Thread.sleep（2400000）// 可读性差
```

在 JDK5 之后，我们可以这样写：

```java
TimeUnit.SECONDS.sleep(4);
TimeUnit.MINUTES.sleep(4);
TimeUnit.HOURS.sleep(1);
TimeUnit.DAYS.sleep(1);
```

另外，TimeUnit 还提供了便捷方法用于把时间转换成不同单位，例如，如果你想把秒转换成毫秒，你可以使用下面代码

```java
TimeUnit.SECONDS.toMillis(44);// 44,000
```
