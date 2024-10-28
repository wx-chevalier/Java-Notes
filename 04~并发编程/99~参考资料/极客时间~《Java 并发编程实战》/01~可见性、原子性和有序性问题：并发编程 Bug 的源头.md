> DocId: bLPfTszBt9c

# 并发编程中的三大挑战：可见性、原子性和有序性

在计算机技术不断进步的今天，CPU、内存和 I/O 设备的性能都在持续提升。然而，这些组件之间的速度差异仍然是一个长期存在的问题。为了充分发挥 CPU 的高速处理能力，同时解决这些组件之间的速度不匹配，计算机系统的各个层面都采取了一系列措施：

- CPU 层面：引入了缓存机制，用于缓解与内存之间的速度差异
- 操作系统层面：创建了进程和线程的概念，通过时间片轮转来平衡 CPU 与 I/O 设备之间的速度差异
- 编译器层面：优化指令执行顺序，以更高效地利用缓存

这些措施虽然提高了系统的整体性能，但同时也给并发编程带来了新的挑战。让我们深入探讨这些挑战。

## 挑战一：缓存引发的可见性问题

在单核 CPU 时代，所有线程都在同一个 CPU 上运行，CPU 缓存与内存之间的数据一致性相对容易维护。因为所有线程都使用同一个 CPU 的缓存，一个线程对缓存的修改会立即被其他线程看到。

![单核 CPU 的缓存与内存关系](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/uPic/XuBwWhSGLJ4k.png)

然而，在多核 CPU 时代，情况变得复杂了。每个 CPU 核心都有自己的缓存，当多个线程在不同的 CPU 核心上运行时，它们操作的是不同的 CPU 缓存。这就导致了可见性问题：一个线程对共享变量的修改，其他线程可能无法立即看到。

![多核 CPU 的缓存与内存关系](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/uPic/nlfYzNKEd2sC.png)

让我们通过一个具体的例子来理解这个问题：

```java
public class Counter {
  private long count = 0;

  private void increment10K() {
    for (int i = 0; i < 10000; i++) {
      count += 1;
    }
  }

  public static long getTotalCount() {
    final Counter counter = new Counter();

    // 创建两个线程，各自增加计数器10000次
    Thread t1 = new Thread(counter::increment10K);
    Thread t2 = new Thread(counter::increment10K);

    // 启动两个线程
    t1.start();
    t2.start();

    // 等待两个线程执行完毕
    try {
      t1.join();
      t2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return counter.count;
  }
}
```

在这个例子中，我们创建了两个线程，每个线程都将 count 增加 10000 次。理论上，最终的 count 值应该是 20000。但是，由于可见性问题，实际结果可能小于 20000。

这是因为每个线程可能在自己的 CPU 缓存中操作 count 值，而不是直接在主内存中操作。当线程将更新后的值写回主内存时，可能会覆盖其他线程的更新，导致一些增量操作丢失。

![count 变量在 CPU 缓存和内存中的分布](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/uPic/zMRRBqlhO1xO.png)

## 挑战二：线程切换导致的原子性问题

现代操作系统通过时间片轮转来实现多任务处理。每个线程被分配一小段执行时间（例如 50 毫秒），之后操作系统会切换到另一个线程。这种机制虽然提高了 CPU 的利用率，但也带来了新的挑战。

![线程切换示意图](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/uPic/EApAFXG2I9LM.png)

在高级编程语言中，看似简单的操作（如 count += 1）在 CPU 层面可能需要多条指令来完成。如果在这些指令执行过程中发生了线程切换，就可能导致操作的原子性被破坏。

让我们详细分析 count += 1 这个操作：

1. 从内存加载 count 的当前值到 CPU 寄存器
2. 在寄存器中将值加 1
3. 将新值写回内存

如果在这些步骤之间发生了线程切换，可能会导致意外的结果：

![非原子操作的执行路径](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/uPic/xLuhAGYoqLDN.png)

假设初始时 count = 0，线程 A 执行完步骤 1 后被切换出去，此时线程 B 完整执行了 count += 1 的操作，将 count 值增加到 1。然后线程 A 被重新调度，继续执行步骤 2 和 3，但是它使用的是切换前读取的旧值 0，最终将 count 设置为 1。这样，虽然两个线程都执行了 count += 1，但最终结果却是 1 而不是预期的 2。

这就是所谓的原子性问题：一个本应是不可分割的操作，在实际执行中被分割并穿插了其他操作，导致了意外的结果。

## 挑战三：编译优化引起的有序性问题

为了提高程序的执行效率，编译器和处理器常常会对指令进行重新排序。虽然这种优化在单线程环境下不会影响程序的正确性，但在多线程环境中可能导致意外的行为。

一个典型的例子是使用双重检查锁定模式（Double-Checked Locking）来创建单例对象：

```java
public class Singleton {
  private static Singleton instance;

  public static Singleton getInstance() {
    if (instance == null) {
      synchronized(Singleton.class) {
        if (instance == null) {
          instance = new Singleton();
        }
      }
    }
    return instance;
  }
}
```

这段代码看似没有问题，但由于指令重排，new Singleton() 这个操作可能被分解并重排为：

1. 分配内存空间
2. 将内存地址赋值给 instance 变量
3. 初始化 Singleton 对象

如果步骤 2 和 3 的顺序被调换，可能导致其他线程访问到一个未完全初始化的对象。

![双重检查创建单例的异常执行路径](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/uPic/eE32sjWVHalI.png)

假设线程 A 执行到步骤 2，此时 instance 不再是 null，但对象还未初始化。如果此时线程 B 调用 getInstance()，它会发现 instance 不为 null，直接返回这个未初始化的对象，可能导致程序崩溃或产生难以预料的行为。

这种由于指令重排导致的问题就是有序性问题的一个典型例子。在并发编程中，我们不能假设代码会按照我们看到的顺序执行，需要使用特定的同步机制来确保关键操作的顺序。

总结来说，可见性、原子性和有序性这三个问题是并发编程中最基本也是最关键的挑战。理解这些问题的本质，对于编写正确、高效的并发程序至关重要。在实际开发中，我们需要使用各种同步机制和并发工具来应对这些挑战，确保多线程程序的正确性和性能。
