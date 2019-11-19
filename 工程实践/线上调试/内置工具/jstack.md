# jstack

jstack 命令主要用于调试 java 程序运行过程中的线程堆栈信息，可以用于检测死锁，进程耗用 cpu 过高报警问题的排查。

```sh
$ jstack

Usage:
    jstack [-l] <pid>
    jstack -F [-m] [-l] <pid>
Options:
    -F  强制dump线程堆栈信息. 用于进程hung住， jstack <pid>命令没有响应的情况
    -m  同时打印java和本地(native)线程栈信息，m是mixed mode的简写
    -l  打印锁的额外信息
```

jstack 的典型用法如下：

- qmq 是部署在 tomcat 中的应用名

```sh
$ ps -ef | grep qmq | grep -v grep
```

拿到进程号， 例如上面对应的是 3192

- 第二步找出该进程内最耗费 CPU 的线程，可以使用 ps -Lfp pid 或者 ps -mp pid -o THREAD, tid, time 或者 top -Hp pid。例如用第三个 top -Hp 3192：

```sh
Tasks: 123 total, 0 running, 123 sleeping, 0 stopped, 0 zombie
Cpu(s): 0.3%us, 0.4%sy, 0.0%ni, 99.3%id, 0.0%wa, 0.0%hi, 0.0%si, 0.0%st
Mem: 3922688k total, 3272588k used, 650100k free, 432768k buffers
Swap: 4194296k total, 0k used, 4194296k free, 596488k cached

PID USER PR NI VIRT RES SHR S %CPU %MEM TIME+ COMMAND
3494 tomcat 20 0 4905m 1.1g 11m S 0.3 28.4 0:51.91 java
3551 tomcat 20 0 4905m 1.1g 11m S 0.3 28.4 4:46.32 java
3588 tomcat 20 0 4905m 1.1g 11m S 0.3 28.4 0:07.35 java
3192 tomcat 20 0 4905m 1.1g 11m S 0.0 28.4 0:00.00 java
3194 tomcat 20 0 4905m 1.1g 11m S 0.0 28.4 0:00.82 java
```

- TIME 列就是各个 Java 线程耗费的 CPU 时间，CPU 时间最长的是线程 ID 为 3551 的线程，用 `printf "%x\n" 3551` 得到 ddf

- sudo -u tomcat jstack 3192 | grep ddf

```sh
"New I/O worker #30" daemon prio=10 tid=0x00007f44fd525800 nid=0xde4 runnable [0x00007f4530ddf000]
"DubboResponseTimeoutScanTimer" daemon prio=10 tid=0x00007f44fca88000 nid=0xddf waiting on condition [0x00007f45322e5000]
```

# 线程信息详解

jstack 命令会打印出所有的线程，包括用户自己启动的线程和 jvm 后台线程，我们主要关注的是用户线程，如：

```sh
$ jstack 15525

Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.65-b01 mixed mode):

"elasticsearch[Native][merge][T#1]" #98 daemon prio=5 os_prio=0 tid=0x00007f031c009000 nid=0x4129 waiting on condition [0x00007f02f61ee000]

   java.lang.Thread.State: WAITING (parking)
    at sun.misc.Unsafe.park(Native Method)
    - parking to wait for  <0x00000000eea589f0> (a org.elasticsearch.common.util.concurrent.EsExecutors$ExecutorScalingQueue)
    at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
    at java.util.concurrent.LinkedTransferQueue.awaitMatch(LinkedTransferQueue.java:737)
    at java.util.concurrent.LinkedTransferQueue.xfer(LinkedTransferQueue.java:647)
    at java.util.concurrent.LinkedTransferQueue.take(LinkedTransferQueue.java:1269)
    at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1067)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1127)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
    at java.lang.Thread.run(Thread.java:745)

....
```

线程 dump 信息说明：

- elasticsearch[Native][merge][T#1] 是我们为线程起的名字

- daemon 表示线程是否是守护线程

- prio 表示我们为线程设置的优先级

- os_prio 表示的对应的操作系统线程的优先级，由于并不是所有的操作系统都支持线程优先级，所以可能会出现都置为 0 的情况

- tid 是 java 中为这个线程的 id

- nid 是这个线程对应的操作系统本地线程 id，每一个 java 线程都有一个对应的操作系统线程

- wait on condition 表示当前线程处于等待状态，但是并没列出具体原因

- java.lang.Thread.State: WAITING (parking) 也是表示的处于等待状态，括号中的内容说明了导致等待的原因，例如这里的 parking 说明是因为调用了 LockSupport.park 方法导致等待

## java.lang.Thread.State

一个 Thread 对象可以有多个状态，在 java.lang.Thread.State 中，总共定义六种状态。

- New

线程刚刚被创建，也就是已经 new 过了，但是还没有调用 start()方法，jstack 命令不会列出处于此状态的线程信息。

- RUNNABLE

RUNNABLE 这个名字很具有欺骗性，很容易让人误以为处于这个状态的线程正在运行。事实上，这个状态只是表示，线程是可运行的。我们已经无数次提到过，一个单核 CPU 在同一时刻，只能运行一个线程。

- BLOCKED

线程处于阻塞状态，正在等待一个 monitor lock。通常情况下，是因为本线程与其他线程公用了一个锁。其他在线程正在使用这个锁进入某个 synchronized 同步方法块或者方法，而本线程进入这个同步代码块也需要这个锁，最终导致本线程处于阻塞状态。

- WAITING

等待状态，调用以下方法可能会导致一个线程处于等待状态：Object.wait 不指定超时时间、Thread.join with no timeout、LockSupport.park #java.lang.Thread.State: WAITING (parking)。

例如：对于 wait()方法，一个线程处于等待状态，通常是在等待其他线程完成某个操作。本线程调用某个对象的 wait()方法，其他线程处于完成之后，调用同一个对象的 notify 或者 notifyAll()方法。Object.wait()方法只能够在同步代码块中调用。调用了 wait()方法后，会释放锁。

- TIMED_WAITING

线程等待指定的时间，对于以下方法的调用，可能会导致线程处于这个状态：

1. Thread.sleep #java.lang.Thread.State: TIMED_WAITING (sleeping)

2. Object.wait 指定超时时间 #java.lang.Thread.State: TIMED_WAITING (on object monitor)

3. Thread.join with timeout

4. LockSupport.parkNanos #java.lang.Thread.State: TIMED_WAITING (parking)

5. LockSupport.parkUntil #java.lang.Thread.State: TIMED_WAITING (parking)

- TERMINATED

线程终止。说明，对于 java.lang.Thread.State: WAITING (on object monitor)和 java.lang.Thread.State: TIMED_WAITING (on object monitor)，对于这两个状态，是因为调用了 Object 的 wait 方法(前者没有指定超时，后者指定了超时)，由于 wait 方法肯定要在 syncronized 代码中编写，因此肯定是如类似以下代码导致：

```java
synchronized(obj) {
        // .........
        obj.wait();
        // .........
}
```

## 死锁

在 JAVA 5 中加强了对死锁的检测。线程 Dump 中可以直接报告出 Java 级别的死锁，如下所示：

```java

Found one Java-level deadlock:

=============================

"Thread-1":

waiting to lock monitor 0x0003f334 (object 0x22c19f18, a java.lang.Object),

which is held by "Thread-0"

"Thread-0":

waiting to lock monitor 0x0003f314 (object 0x22c19f20, a java.lang.Object),

which is held by "Thread-1"
```

## nid

每个线程都有一个 tid 和 nid，tid 是 java 中这个线程的编号，而 nid(native id)是对应操作系统线程 id。有的时候，我们会收到报警，说服务器，某个进程占用 CPU 过高，肯定是因为某个 java 线程有耗 CPU 资源的方法。
