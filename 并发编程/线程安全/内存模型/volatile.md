# volatile

JVM 中每一个变量都有一个主内存，为了保证最佳性能，JVM 允许线程从主内存拷贝一份私有拷贝，然后在线程读取变量的时候从主内存里面读，退出的时候，将修改的值同步到主内存。形象而言，对于变量 t，A 线程对 t 变量修改的值，对 B 线程是可见的。但是 A 获取到 t 的值加 1 之后，突然挂起了，B 获取到的值还是最新的值，volatile 能保证 B 能获取到的 t 是最新的值，因为 A 的 t+1 并没有写到主内存里面去。volatile 变量具备以下特征：

- 可见性，对于 volatile 变量的读，线程总是能读到当前最新的 volatile 值，也就是任一线程对 volatile 变量的写入对其余线程都是立即可见；
- 有序性，禁止编译器和处理器为了提高性能而进行指令重排序；
- 基本不保证原子性，由于存在 long/double 非原子性协议，long/double 在 32 位 x86 的 hotspot 虚拟机下允许没有被 volatile 修饰的变量读写操作划分为两次进行。但是从 JDK9 开始，hotspot 也明确约束所有数据类型访问保持原子性，所以 volatile 变量保证原子性可以基本忽略。

在实际的编程中，要注意，除非是在保证仅有一个线程处于写，而其他线程处于读的状态下的时候，才可以使用 volatile 来保证可见性，而不需要使用原子变量或者锁来保证原子性。

```java
// 原子操作
public static AtomicInteger count = new AtomicInteger();

// 线程协作处理
public static CountDownLatch latch= new CountDownLatch(1000);

// volatile 只能保证可见性，不能保证原子性
public static volatile int countNum = 0;

// 同步处理计算
public static int synNum = 0;

public static void inc() {
    Thread.sleep(1);

    countNum++;
    int c = count.addAndGet(1);
    add();
}

public static synchronized void add(){
    synNum++;
}

public static void main(String[] args) {
    // 同时启动1000个线程，去进行i++计算，看看实际结果
    for (int i = 0; i < 1000; i++) {
        new Thread(()=>{
            Counter.inc();
            latch.countDown();
        }), "thread" + i).start();
    }

    latch.await();
```

# volatile 实现原理

从 Java 内存模型层面来说：Java 内存模型保证了 volatile 变量的可见性，也就是说 JMM 保证新值能马上同步到主内存，同时把其他线程的工作内存中对应的变量副本置为无效，以及每次使用前立即从主内存读取共享变量。将带有 volatile 变量操作的 Java 代码转换成汇编代码后，可以看到多了个 lock 前缀指令；这个 lock 指令是关键，在多核处理器下实现两个重要操作:

- 将当前处理器缓存行的数据写回到系统内存。
- 这个写回内存的操作会使其他处理器里缓存该内存地址的数据失效

在 Java 内存模型中，通过 as-if-serial 和 happens-before(先行先发生) 来保证从重排的正确性，同时对于 volatile 变量有特殊的规则：对一个变量的写操作先行发生于后面对这个变量的读操作，Java 内存模型底层通过内存屏障（Memory Barrier）来进行处理。在 Java 内存模型中，主要有以下 4 种类型的内存屏障：

- LoadLoad 屏障：对于 Load1,LoadLoad,Load2 这样的语句，在 Load2 及后续读取操作前要保证 Load1 要读取的数据读取完毕；
- LoadStore 屏障：对于 Load1,LoadStore,Store2 这样的语句，在 Store2 及后续写入操作前要保证 Load1 要读取的数据读取完毕；
- StoreStore 屏障：对于 Store1,StoreStore,Store2 这样的语句，在 Store2 及后续写入操作前要保证 Store1 的写入操作对其他处理器可见；
- StoreLoad 屏障：对于 Store1,StoreLoad,Load2 这样的语句，在 Load2 及后续读取操作前，Store1 的写入对所有处理器可见。

![Java 内存屏障](https://s3.ax1x.com/2021/01/28/y9MMVS.png)

```java
public class VolatileTest {
    public static volatile int race = 0;
    public static int value = 0;
    public static void increase() {
        race++;
        value++;
    }
    private static final int THREAD_COUNT = 20;
    public static void main(String[] args) {
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(() -> {
               for (int j = 0; j < 10000; j++) {
                   increase();
               }
            });
            threads[i].start();
        }
        while (Thread.activeCount()> 1) {
            Thread.yield();
        }
        System.out.println("race: " + race + " value: " + value);
    }
}
```

通过汇编指令可以看出，被 volatile 修饰有一个 lock 指令前缀，lock 指令的作用是将本地处理器的缓存写入内存，同时将其他处理器的缓存失效，这样其他处理需要数据计算时，必须重新读取主内存的数据，从而达到了变量的可见性的目的；对于禁止指令重排序，同样也是通过整条 lock 指令（lock add1$0x0, (%rsp)）形成一条内存屏障，来禁止指令重排。

- 写屏障与(StoreStore、StoreLoad)屏障的关系：在 volatile 变量写之前加入 StoreSore 屏障保证了 volatile 写之前，写缓冲器中的内容已全部刷回告诉缓存，防止前面的写操作和 volatile 写操作之间发生指令重排，在 volatile 写之后加入 StoreLoad 屏障，保证了后面的读/写操作与 volatile 写操作发生指令重排，所以写屏障同时具有 StoreStore 与 StoreLoad 的功能

- 读屏障与（LoadLoad、LoadStore）屏障的关系：在 volatile 变量读之后加入 LoadLoad 屏障保证了后面其他读操作的无效队列中无效消息已经被刷回到了高速缓存，在 volatile 变量读操作后加入 LoadStore 屏障，保证了后面其他写操作的无效队列中无效消息已经被刷回高速缓存。读屏障同时具有了 LoadLoad，LoadStore 的功能。
