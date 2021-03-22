# synchronized

在多线程并发编程中 synchronized 一直是元老级角色，很多人都会称呼它为重量级锁。但是，随着 Java SE 1.6 对 synchronized 进行了各种优化之后，有些情况下它就并不那么重了；引入了偏向锁和轻量级锁，对锁的存储结构和升级过程，有效减少获得锁和释放锁带来的性能消耗。synchronized 关键字，同时解决了原子性、可见性、有序性问题:

- 可见性：按照 JMM 规范，对一个变量解锁之前，必须先把此变量同步回主存中，这样解锁后，后续线程就可以访问到被修改后的值。所以被 synchronized 锁住的对象，其值具有可见性。
- 原子性：通过监视器锁，可以保证 synchronized 修饰的代码在同一时间，只能被一个线程访问，在锁未释放之前其它线程无法进入该方法或代码块，保证了操作的原子性。
- 有序性：synchronized 关键字并不禁止指令重排，但是由于程序是以单线程的方式执行的，所以执行的结果是确定的，不会受指令重排的干扰，有序性不再是个问题。

需要注意的是，当我们使用 synchronized 关键字，管理某个状态时，必须对访问这个对象的所有操作，都加上 synchronized 关键字，否则仍然会有并发安全性问题。

# 同步使用

- 对于，普通同步方法，锁是当前实例对象。`public synchronized void test(){...}`

- 对于静态同步方法，锁是当前类的 Class 对象。`public static synchronized void test(...){}`

- 对于对于同步方法块，锁是 synchronized 括号中里配置的对象。`synchronized(instance){...}`

# 实现原理

## Monitor

synchronized 关键字依赖于内部的 intrinsic lock 或者所谓的 monitor lock。每个对象都有一个与之关联的固有锁。按照惯例，线程必须在访问对象之前获取对象的监视器锁，然后在完成对它们的锁定后释放该监视器锁。据说线程在获得锁和释放锁之间拥有该锁。只要一个线程拥有监视器锁，其他任何线程都无法获得相同的锁。另一个线程在尝试获取锁时将阻塞。当线程释放锁时，将在该动作与任何随后的相同锁获取之间建立 happens-before 关系。

```java
public class SynchronizedDemo {

  // 同步方法
  public synchronized void syncMethod() {
    System.out.println("Hello World");
  }

  // 同步代码块
  public void syncBlock() {
    synchronized (this) {
      System.out.println("Hello World");
    }
  }
}
```

以上的示例代码，编译后的字节码为:

```java
public synchronized void syncMethod();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_SYNCHRONIZED
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #3                  // String Hello World
         5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return

  public void syncBlock();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=3, args_size=1
         0: ldc           #5                  // class com/hollis/SynchronizedTest
         2: dup
         3: astore_1
         4: monitorenter
         5: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         8: ldc           #3                  // String Hello World
        10: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        13: aload_1
        14: monitorexit
        15: goto          23
        18: astore_2
        19: aload_1
        20: monitorexit
        21: aload_2
        22: athrow
        23: return
```

从 JVM 规范中可以看到 synchronized 在 JVM 里的实现原理，JVM 基于进入和退出 Monitor 对象来实现方法同步和代码块同步，但两者的实现细节不一样。代码块同步是使用 monitorenter 和 monitorexit 指令实现的，而方法同步是使用另外一种方式实现的，细节在 JVM 规范里并没有详细说明。但是，方法的同步同样可以使用这两个指令来实现。

monitorenter 指令是在编译后插入到同步代码块的开始位置，而 monitorexit 是插入到方法结束处和异常处，JVM 要保证每个 monitorenter 必须有对应的 monitorexit 与之配对。任何对象都有一个 monitor 与之关联，当且一个 monitor 被持有后，它将处于锁定状态。线程执行到 monitorenter 指令时，将会尝试获取对象所对应的 monitor 的所有权，即尝试获得对象的锁。

## Java 对象头

synchronized 用的锁是存在 Java 对象头里的。如果对象是数组类型，则虚拟机用 3 个字宽（Word）存储对象头，如果对象是非数组类型，则用 2 字宽存储对象头。在 32 位虚拟机中，1 字宽等于 4 字节，即 32bit，如下图所示：

![Java 对象头的长度](https://s3.ax1x.com/2021/01/29/yPKLCQ.png)

Java 对象头里的 Mark Word 里默认存储对象的 HashCode、分代年龄和锁标记位。32 位 JVM 的 Mark Word 的默认存储结构如下表所示。

![Java 对象头的存储结构](https://s3.ax1x.com/2021/01/29/yPMSbV.png)

在运行期间，Mark Word 里存储的数据会随着锁标志位的变化而变化。Mark Word 可能变化为存储以下 4 种数据：

![Mark Word的状态变化](https://s3.ax1x.com/2021/01/29/yPMEvR.png)

在 64 位虚拟机下，Mark Word 是 64bit 大小的，其存储结构如下所示：

![Mark Word的存储结构](https://s3.ax1x.com/2021/01/29/yPQJW4.png)

# Links

- https://blog.csdn.net/significantfrank/article/details/80399179 Synchronized 和 Lock 该如何选择

- https://mp.weixin.qq.com/s/w5K8kmNwAcIxB5lb1N93pg synchronized 连环问
