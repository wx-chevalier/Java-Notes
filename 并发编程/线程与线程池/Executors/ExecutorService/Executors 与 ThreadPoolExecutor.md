# Executors

Executors 类提供了便利的工厂方法来创建不同类型的 ExecutorServices，分别为：

- newCachedThreadPool 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
- newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
- newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。
- newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。

```java
public static void main(String[] args) {
  // 创建可以容纳3个线程的线程池
  ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

  // 线程池的大小会根据执行的任务数动态分配
  ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

  // 创建单个线程的线程池，如果当前线程在执行任务时突然中断，则会创建一个新的线程替代它继续执行任务
  ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

  // 效果类似于Timer定时器
  ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(
    3
  );

  run(fixedThreadPool);
//	   run(cachedThreadPool); // 执行结果，可以看出 4 个任务交替执行
//	   run(singleThreadPool);
//	   run(scheduledThreadPool);
}
```

# CachedThreadPool

CachedThreadPool 会创建一个缓存区，将初始化的线程缓存起来。会终止并且从缓存中移除已有 60 秒未被使用的线程。如果线程有可用的，就使用之前创建好的线程，如果线程没有可用的，就新创建线程。

- 重用：缓存型池子，先查看池中有没有以前建立的线程，如果有，就 reuse；如果没有，就建一个新的线程加入池中
- 使用场景：缓存型池子通常用于执行一些生存期很短的异步型任务，因此在一些面向连接的 daemon 型 SERVER 中用得不多。
- 超时：能 reuse 的线程，必须是 timeout IDLE 内的池中线程，缺省 timeout 是 60s，超过这个 IDLE 时长，线程实例将被终止及移出池。
- 结束：注意，放入 CachedThreadPool 的线程不必担心其结束，超过 TIMEOUT 不活动，其会自动被终止。

```java
// 线程池的大小会根据执行的任务数动态分配
ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0,                 //core pool size
                                    Integer.MAX_VALUE, //maximum pool size
                                    60L,               //keep alive time
                                    TimeUnit.SECONDS,
                                    new SynchronousQueue<Runnable>());
}
```

# FixedThreadPool

在 FixedThreadPool 中，有一个固定大小的池。如果当前需要执行的任务超过池大小，那么多出的任务处于等待状态，直到有空闲下来的线程执行任务，如果当前需要执行的任务小于池大小，空闲的线程也不会去销毁。

- 重用：fixedThreadPool 与 cacheThreadPool 差不多，也是能 reuse 就用，但不能随时建新的线程
- 固定数目：其独特之处在于，任意时间点，最多只能有固定数目的活动线程存在，此时如果有新的线程要建立，只能放在另外的队列中等待，直到当前的线程中某个线程终止直接被移出池子
- 超时：和 cacheThreadPool 不同，FixedThreadPool 没有 IDLE 机制(可能也有，但既然文档没提，肯定非常长，类似依赖上层的 TCP 或 UDP IDLE 机制之类的)，

所以 FixedThreadPool 多数针对一些很稳定很固定的正规并发线程，多用于服务器，从方法的源代码看，cache 池和 fixed 池调用的是同一个底层池，只不过参数不同：

- fixed 池线程数固定，并且是 0 秒 IDLE(无 IDLE)
- cache 池线程数支持 0-Integer.MAX_VALUE(显然完全没考虑主机的资源承受能力)，60 秒 IDLE

```java
// 创建可以容纳3个线程的线程池
ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, //core pool size
                                      nThreads, //maximum pool size
                                      0L,       //keep alive time
                                      TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
}


```

执行结果：创建了一个固定大小的线程池，容量为 3，然后循环执行了 4 个任务。由输出结果可以看到，前 3 个任务首先执行完，然后空闲下来的线程去执行第 4 个任务。

# SingleThreadExecutor

SingleThreadExecutor 得到的是一个单个的线程，这个线程会保证你的任务执行完成。如果当前线程意外终止，会创建一个新线程继续执行任务，这和我们直接创建线程不同，也和 newFixedThreadPool(1)不同。

```java
// 创建单个线程的线程池，如果当前线程在执行任务时突然中断，则会创建一个新的线程替代它继续执行任务
ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

// 内部实现
public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1,  //core pool size
                                    1,  //maximum pool size
                                    0L, //keep alive time
                                    TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
}
```

# ScheduledThreadPool

ScheduledThreadPool 是一个固定大小的线程池，与 FixedThreadPool 类似，执行的任务是定时执行。

```java
// 效果类似于Timer定时器
ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(3);

public ScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize,      //core pool size
              Integer.MAX_VALUE, //maximum pool size
              0,                 //keep alive time
              TimeUnit.NANOSECONDS,
              new DelayedWorkQueue());
}
```

为了持续的多次执行常见的任务，我们可以利用调度线程池 ScheduledExecutorService 支持任务调度，持续执行或者延迟一段时间后执行：

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());
// 设置延迟 3 秒执行
ScheduledFuture<?> future = executor.schedule(task, 3, TimeUnit.SECONDS);

TimeUnit.MILLISECONDS.sleep(1337);

long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
System.out.printf("Remaining Delay: %sms", remainingDelay);
```

调度一个任务将会产生一个专门的 ScheduleFuture 类型，它除了提供了 Future 的所有方法之外，他还提供了 getDelay()方法来获得剩余的延迟。在延迟消逝后，任务将会并发执行。为了调度任务持续的执行，executors 提供了两个方法 s`cheduleAtFixedRate()` 和 `scheduleWithFixedDelay()`；第一个方法用来以固定频率来执行一个任务，比如，下面这个示例中，每分钟一次：

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());

int initialDelay = 0;
int period = 1;
executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
```

另外，这个方法还接收一个初始化延迟，用来指定这个任务首次被执行等待的时长。需要注意的是，`scheduleAtFixedRate()` 并不考虑任务的实际用时。所以，如果你指定了一个 period 为 1 分钟而任务需要执行 2 分钟，那么线程池为了性能会更快的执行。在这种情况下，你应该考虑使用 scheduleWithFixedDelay()。这个方法的工作方式与上我们上面描述的类似。不同之处在于等待时间 period 的应用是在一次任务的结束和下一个任务的开始之间。

# ThreadPoolExecutor 对象

Executors 创建线程池方法本质上都是使用了 ThreadPoolExecutor，因为这些创建线程池的静态方法都是返回 ThreadPoolExecutor 对象，和我们手动创建 ThreadPoolExecutor 对象的区别就是我们不需要自己传构造函数的参数。ThreadPoolExecutor 的构造函数共有四个，但最终调用的都是同一个：

```java
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler)
```

构造函数参数说明：

- corePoolSize => 线程池核心线程数量
- maximumPoolSize => 线程池最大数量
- keepAliveTime => 空闲线程存活时间
- unit => 时间单位
- workQueue => 线程池所使用的缓冲队列
- threadFactory => 线程池创建线程使用的工厂
- handler => 线程池对拒绝任务的处理策略

![执行逻辑与线程池参数关系](https://s3.ax1x.com/2020/11/25/DaJjDs.png)

执行逻辑说明：

- 判断核心线程数是否已满，核心线程数大小和`corePoolSize`参数有关，未满则创建线程执行任务
- 若核心线程池已满，判断队列是否满，队列是否满和`workQueue`参数有关，若未满则加入队列中
- 若队列已满，判断线程池是否已满，线程池是否已满和`maximumPoolSize`参数有关，若未满创建线程执行任务
- 若线程池已满，则采用拒绝策略处理无法执执行的任务，拒绝策略和`handler`参数有关
