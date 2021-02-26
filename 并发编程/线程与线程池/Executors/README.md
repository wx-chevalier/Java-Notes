# Executors 详解

并发 API 引入了 ExecutorService 作为一个在程序中直接使用 Thread 的高层次的替换方案。Executos 支持运行异步任务，通常管理一个线程池，这样一来我们就不需要手动去创建新的线程。在不断地处理任务的过程中，线程池内部线程将会得到复用，因此，在我们可以使用一个 Executor Service 来运行和我们想在我们整个程序中执行的一样多的并发任务。

![Executor 类图](https://s2.ax1x.com/2019/09/02/nPC2c9.png)

![Executor 方法图](https://s2.ax1x.com/2019/09/02/nPCRXR.png)

## Executor, ExecutorService 和 Executors

![UML 关系类图](https://s3.ax1x.com/2021/02/26/yx2YgU.png)

正如上面所说，这三者均是 Executor 框架中的一部分。Java 开发者很有必要学习和理解他们，以便更高效的使用 Java 提供的不同类型的线程池。总结一下这三者间的区别，以便大家更好的理解：

- Executor 和 ExecutorService 这两个接口主要的区别是：ExecutorService 接口继承了 Executor 接口，是 Executor 的子接口
- Executor 和 ExecutorService 第二个区别是：Executor 接口定义了 `execute()`方法用来接收一个`Runnable`接口的对象，而 ExecutorService 接口中的 `submit()`方法可以接受`Runnable`和`Callable`接口的对象。
- Executor 和 ExecutorService 接口第三个区别是 Executor 中的 `execute()` 方法不返回任何结果，而 ExecutorService 中的 `submit()`方法可以通过一个 Future 对象返回运算结果。
- Executor 和 ExecutorService 接口第四个区别是除了允许客户端提交一个任务，ExecutorService 还提供用来控制线程池的方法。比如：调用 `shutDown()` 方法终止线程池。
- Executors 类提供工厂方法用来创建不同类型的线程池。比如: `newSingleThreadExecutor()` 创建一个只有一个线程的线程池，`newFixedThreadPool(int numOfThreads)`来创建固定线程数的线程池，`newCachedThreadPool()`可以根据需要创建新的线程，但如果已有线程是空闲的会重用已有线程。

下表列出了 Executor 和 ExecutorService 的区别：

| Executor                                                  | ExecutorService                                                          |
| --------------------------------------------------------- | ------------------------------------------------------------------------ |
| Executor 是 Java 线程池的核心接口，用来并发执行提交的任务 | ExecutorService 是 Executor 接口的扩展，提供了异步执行和关闭线程池的方法 |
| 提供 execute()方法用来提交任务                            | 提供 submit()方法用来提交任务                                            |
| execute()方法无返回值                                     | submit()方法返回 Future 对象，可用来获取任务执行结果                     |
| 不能取消任务                                              | 可以通过 Future.cancel()取消 pending 中的任务                            |
| 没有提供和关闭线程池有关的方法                            | 提供了关闭线程池的方法                                                   |

# Hello World

下面是使用 Executors 的第一个代码示例：

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
executor.submit(() -> {
	String threadName = Thread.currentThread().getName();
	System.out.println("Hello " + threadName);
});
// => Hello pool-1-thread-1
```

Executors 必须显式的停止，否则它们将持续监听新的任务。ExecutorService 提供了两个方法来达到这个目的：shutdwon() 会等待正在执行的任务执行完而，shutdownNow() 会终止所有正在执行的任务并立即关闭 executor。

```java
try {
    System.out.println("attempt to shutdown executor");
    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);
}
catch (InterruptedException e) {
    System.err.println("tasks interrupted");
}
finally {
    if (!executor.isTerminated()) {
        System.err.println("cancel non-finished tasks");
    }
    executor.shutdownNow();
    System.out.println("shutdown finished");
}
```
