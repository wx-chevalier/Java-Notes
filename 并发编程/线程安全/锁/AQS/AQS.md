# AQS

Java 中的大部分同步类（Lock、Semaphore、ReentrantLock 等）都是基于 AbstractQueuedSynchronizer（简称为 AQS）实现的。AQS 是一种提供了原子式管理同步状态、阻塞和唤醒线程功能以及队列模型的简单框架。通过上文我们已经了解，ReentrantLock 支持公平锁和非公平锁，并且 ReentrantLock 的底层就是由 AQS 来实现的。那么 ReentrantLock 是如何通过公平锁和非公平锁与 AQS 关联起来呢？ 我们着重从这两者的加锁过程来理解一下它们与 AQS 之间的关系。

非公平锁源码中的加锁流程如下：

```java
// java.util.concurrent.locks.ReentrantLock#NonfairSync

// 非公平锁
static final class NonfairSync extends Sync {
	...
	final void lock() {
		if (compareAndSetState(0, 1))
			setExclusiveOwnerThread(Thread.currentThread());
		else
			acquire(1);
		}
  ...
}
```

这块代码的含义为：

- 若通过 CAS 设置变量 State（同步状态）成功，也就是获取锁成功，则将当前线程设置为独占线程。
- 若通过 CAS 设置变量 State（同步状态）失败，也就是获取锁失败，则进入 Acquire 方法进行后续处理。

第一步很好理解，但第二步获取锁失败后，后续的处理策略是怎么样的呢？这块可能会有以下思考：

- 某个线程获取锁失败的后续流程是什么呢？有以下两种可能：

  - 将当前线程获锁结果设置为失败，获取锁流程结束。这种设计会极大降低系统的并发度，并不满足我们实际的需求。所以就需要下面这种流程，也就是 AQS 框架的处理流程。
  - 存在某种排队等候机制，线程继续等待，仍然保留获取锁的可能，获取锁流程仍在继续。

- 对于问题 1 的第二种情况，既然说到了排队等候机制，那么就一定会有某种队列形成，这样的队列是什么数据结构呢？
- 处于排队等候机制中的线程，什么时候可以有机会获取锁呢？
- 如果处于排队等候机制中的线程一直无法获取锁，还是需要一直等待吗，还是有别的策略来解决这一问题？

带着非公平锁的这些问题，再看下公平锁源码中获锁的方式：

```java
// java.util.concurrent.locks.ReentrantLock#FairSync

static final class FairSync extends Sync {
  ...
	final void lock() {
		acquire(1);
	}
  ...
}
```

结合公平锁和非公平锁的加锁流程，虽然流程上有一定的不同，但是都调用了 Acquire 方法，而 Acquire 方法是 FairSync 和 UnfairSync 的父类 AQS 中的核心方法。

# 框架概览

首先，我们通过下面的架构图来整体了解一下 AQS 框架：

![AQS 框架图](https://s3.ax1x.com/2021/01/29/yPhQnH.png)

上图中有颜色的为 Method，无颜色的为 Attribution。总的来说，AQS 框架共分为五层，自上而下由浅入深，从 AQS 对外暴露的 API 到底层基础数据。当有自定义同步器接入时，只需重写第一层所需要的部分方法即可，不需要关注底层具体的实现流程。当自定义同步器进行加锁或者解锁操作时，先经过第一层的 API 进入 AQS 内部方法，然后经过第二层进行锁的获取，接着对于获取锁失败的流程，进入第三层和第四层的等待队列处理，而这些处理方式均依赖于第五层的基础数据提供层。

AQS 核心思想是，如果被请求的共享资源空闲，那么就将当前请求资源的线程设置为有效的工作线程，将共享资源设置为锁定状态；如果共享资源被占用，就需要一定的阻塞等待唤醒机制来保证锁分配。这个机制主要用的是 CLH 队列的变体实现的，将暂时获取不到锁的线程加入到队列中。CLH 即 Craig、Landin and Hagersten 队列，是单向链表，AQS 中的队列是 CLH 变体的虚拟双向队列（FIFO），AQS 是通过将每条请求共享资源的线程封装成一个节点来实现锁的分配。

![CLH 变体队列](https://s3.ax1x.com/2021/01/29/yP4CPP.png)

AQS 使用一个 Volatile 的 int 类型的成员变量来表示同步状态，通过内置的 FIFO 队列来完成资源获取的排队工作，通过 CAS 完成对 State 值的修改。

## AQS 数据结构

先来看下 AQS 中最基本的数据结构——Node，Node 即为上面 CLH 变体队列中的节点。

![AQS Node](https://s3.ax1x.com/2021/01/29/yP4man.png)

解释一下几个方法和属性值的含义：

| 方法和属性值 | 含义                                                                                             |
| :----------- | :----------------------------------------------------------------------------------------------- |
| waitStatus   | 当前节点在队列中的状态                                                                           |
| thread       | 表示处于该节点的线程                                                                             |
| prev         | 前驱指针                                                                                         |
| predecessor  | 返回前驱节点，没有的话抛出 npe                                                                   |
| nextWaiter   | 指向下一个处于 CONDITION 状态的节点（由于本篇文章不讲述 Condition Queue 队列，这个指针不多介绍） |
| next         | 后继指针                                                                                         |

线程两种锁的模式：

| 模式      | 含义                           |
| :-------- | :----------------------------- |
| SHARED    | 表示线程以共享的模式等待锁     |
| EXCLUSIVE | 表示线程正在以独占的方式等待锁 |

waitStatus 有下面几个枚举值：

| 枚举      | 含义                                             |
| :-------- | :----------------------------------------------- |
| 0         | 当一个 Node 被初始化的时候的默认值               |
| CANCELLED | 为 1，表示线程获取锁的请求已经取消了             |
| CONDITION | 为-2，表示节点在等待队列中，节点线程等待唤醒     |
| PROPAGATE | 为-3，当前线程处在 SHARED 情况下，该字段才会使用 |
| SIGNAL    | 为-1，表示线程已经准备好了，就等资源释放了       |

## 同步状态 State

在了解数据结构后，接下来了解一下 AQS 的同步状态——State。AQS 中维护了一个名为 state 的字段，意为同步状态，是由 Volatile 修饰的，用于展示当前临界资源的获锁情况。

```java
// java.util.concurrent.locks.AbstractQueuedSynchronizer

private volatile int state;
```

下面提供了几个访问这个字段的方法：

| 方法名                                                             | 描述                    |
| :----------------------------------------------------------------- | :---------------------- |
| protected final int getState()                                     | 获取 State 的值         |
| protected final void setState(int newState)                        | 设置 State 的值         |
| protected final boolean compareAndSetState(int expect, int update) | 使用 CAS 方式更新 State |

这几个方法都是 Final 修饰的，说明子类中无法重写它们。我们可以通过修改 State 字段表示的同步状态来实现多线程的独占模式和共享模式（加锁过程）。

![独占模式与共享模式](https://s3.ax1x.com/2021/01/29/yPHAMV.png)

对于我们自定义的同步工具，需要自定义获取同步状态和释放状态的方式，也就是 AQS 架构图中的第一层：API 层。

# AQS 重要方法与 ReentrantLock 的关联

# AQS 应用

## ReentrantLock 的可重入应用

ReentrantLock 的可重入性是 AQS 很好的应用之一，在了解完上述知识点以后，我们很容易得知 ReentrantLock 实现可重入的方法。在 ReentrantLock 里面，不管是公平锁还是非公平锁，都有一段逻辑。

```java
// java.util.concurrent.locks.ReentrantLock.FairSync#tryAcquire

if (c == 0) {
	if (!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
		setExclusiveOwnerThread(current);
		return true;
	}
}
else if (current == getExclusiveOwnerThread()) {
	int nextc = c + acquires;
	if (nextc < 0)
		throw new Error("Maximum lock count exceeded");
	setState(nextc);
	return true;
}

// java.util.concurrent.locks.ReentrantLock.Sync#nonfairTryAcquire

if (c == 0) {
	if (compareAndSetState(0, acquires)){
		setExclusiveOwnerThread(current);
		return true;
	}
}
else if (current == getExclusiveOwnerThread()) {
	int nextc = c + acquires;
	if (nextc < 0) // overflow
		throw new Error("Maximum lock count exceeded");
	setState(nextc);
	return true;
}
```

从上面这两段都可以看到，有一个同步状态 State 来控制整体可重入的情况。State 是 Volatile 修饰的，用于保证一定的可见性和有序性。

```java
// java.util.concurrent.locks.AbstractQueuedSynchronizer

private volatile int state;
```

- State 初始化的时候为 0，表示没有任何线程持有锁。
- 当有线程持有该锁时，值就会在原来的基础上+1，同一个线程多次获得锁是，就会多次+1，这里就是可重入的概念。
- 解锁也是对这个字段-1，一直到 0，此线程对锁释放。

## J.U.C 中的应用场景
