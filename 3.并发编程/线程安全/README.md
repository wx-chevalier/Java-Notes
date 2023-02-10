> 参阅《[Concurrent-Series](https://github.com/wx-chevalier/Concurrent-Series?q=线程安全)》相关章节了解线程安全知识。

# 线程安全

在 Java 中，保证线程安全一般会用两种方式：锁和原子变量。volatile 确保每次操作都能强制同步 CPU 缓存和主存直接的变量。而且在编译期间能阻止指令重排。读写并发情况下 volatile 也不能确保线程安全。

# 锁

![通过加锁的方式实现线程安全](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/superbed/2021/07/22/60f92c625132923bf8d6fd1a.jpg)

采取加锁的方式，默认线程会冲突，访问数据时，先加上锁再访问，访问之后再解锁。通过锁界定一个临界区，同时只有一个线程进入。如上图所示，Thread2 访问 Entry 的时候，加了锁，Thread1 就不能再执行访问 Entry 的代码，从而保证线程安全。下面是 ArrayBlockingQueue 通过加锁的方式实现的 offer 方法，保证线程安全。

```java
public boolean offer(E e) {
    checkNotNull(e);
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        if (count == items.length)
            return false;
        else {
            insert(e);
            return true;
        }
    } finally {
        lock.unlock();
    }
}
```

# 原子变量

原子变量能够保证原子性的操作，意思是某个任务在执行过程中，要么全部成功，要么全部失败回滚，恢复到执行之前的初态，不存在初态和成功之间的中间状态。例如 CAS 操作，要么比较并交换成功，要么比较并交换失败。由 CPU 保证原子性。通过原子变量可以实现线程安全。执行某个任务的时候，先假定不会有冲突，若不发生冲突，则直接执行成功；当发生冲突的时候，则执行失败，回滚再重新操作，直到不发生冲突。

![通过原子变量 CAS 实现线程安全](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/superbed/2021/07/22/60f92eb75132923bf8de26e1.jpg)

如图所示，Thread1 和 Thread2 都要把 Entry 加 1。若不加锁，也不使用 CAS，有可能 Thread1 取到了 myValue=1，Thread2 也取到了 myValue=1，然后相加，Entry 中的 value 值为 2。这与预期不相符，我们预期的是 Entry 的值经过两次相加后等于 3。CAS 会先把 Entry 现在的 value 跟线程当初读出的值相比较，若相同，则赋值；若不相同，则赋值执行失败。一般会通过 while/for 循环来重新执行，直到赋值成功。

代码示例是 AtomicInteger 的 getAndAdd 方法。CAS 是 CPU 的一个指令，由 CPU 保证原子性。

```java
/**
 * Atomically adds the given value to the current value.
 *
 * @param delta the value to add
 * @return the previous value
 */
public final int getAndAdd(int delta) {
    for (;;) {
        int current = get();
        int next = current + delta;
        if (compareAndSet(current, next))
            return current;
    }
}

/**
 * Atomically sets the value to the given updated value
 * if the current value {@code ==} the expected value.
 *
 * @param expect the expected value
 * @param update the new value
 * @return true if successful. False return indicates that
 * the actual value was not equal to the expected value.
 */
public final boolean compareAndSet(int expect, int update) {
    return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
}
```

在高度竞争的情况下，锁的性能将超过原子变量的性能，但是更真实的竞争情况下，原子变量的性能将超过锁的性能。同时原子变量不会有死锁等活跃性问题。
