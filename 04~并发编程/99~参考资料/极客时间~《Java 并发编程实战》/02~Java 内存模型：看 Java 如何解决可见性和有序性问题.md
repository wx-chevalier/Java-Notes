> DocId: Mqfirvu

# Java 内存模型：Java 如何解决可见性和有序性问题

# Java 内存模型是什么？

我们知道，可见性问题源于 CPU 缓存，而有序性问题则来自编译器优化。要彻底解决这两个问题，最直接的方法就是完全禁用 CPU 缓存和编译器优化。但这样做会严重影响程序的性能。那么，有没有一种方法可以在需要的时候才禁用缓存和优化呢？

答案是肯定的。这就是 Java 内存模型的作用。它为程序员提供了一套工具，让我们可以在需要的时候禁用缓存和编译器优化。具体来说，Java 内存模型通过以下方式实现这一目标：

- 三个关键字：volatile、synchronized 和 final
- 六条 Happens-Before 规则

让我们深入了解这些工具是如何工作的。

# volatile 关键字的困惑

volatile 这个关键字并不是 Java 独有的，在 C 语言中也存在。它最初的含义很简单：告诉编译器，对于被 volatile 修饰的变量，每次读写操作都必须直接与内存交互，不能使用 CPU 缓存。

看起来很直观，对吧？但在实际使用中，它可能会让人感到困惑。让我们看一个例子：

```java
class VolatileExample {
  int x = 0;
  volatile boolean v = false;
  public void writer() {
    x = 42;
    v = true;
  }
  public void reader() {
    if (v == true) {
      // 这里 x 会是多少呢？
    }
  }
}
```

假设有两个线程：线程 A 执行 writer() 方法，线程 B 执行 reader() 方法。根据 volatile 的定义，当线程 B 看到 v 为 true 时，x 的值应该是多少呢？

你可能会认为 x 肯定是 42。但事实上，在 Java 1.5 版本之前，x 的值可能是 42，也可能是 0。只有在 Java 1.5 及以后的版本中，x 才保证是 42。

这是为什么呢？这就涉及到了 Java 1.5 版本对 volatile 语义的增强。这个增强是通过一条 Happens-Before 规则实现的。

# Happens-Before 规则详解

Happens-Before 是一个容易引起误解的概念。它并不是字面意思上的"发生在...之前"，而是表达了一种可见性的保证：如果操作 A Happens-Before 操作 B，那么 A 的结果对 B 是可见的。

Happens-Before 规则约束了编译器的优化行为。虽然编译器仍然可以进行优化，但必须确保优化后的代码仍然遵守 Happens-Before 规则。

这个概念最初来自于一篇名为《Time, Clocks, and the Ordering of Events in a Distributed System》的论文。在这篇论文中，Happens-Before 表示的是一种因果关系。就像在现实世界中，如果 A 事件导致了 B 事件，那么 A 一定在 B 之前发生。

在 Java 中，Happens-Before 本质上是一种可见性保证。即使 A 和 B 发生在不同的线程中，只要 A Happens-Before B，那么 B 就能看到 A 的结果。

Java 内存模型定义了六条 Happens-Before 规则，让我们一一来看：

## 1. 程序顺序规则

这条规则很好理解：在同一个线程中，按照程序的顺序，前面的操作 Happens-Before 后面的操作。

例如，在我们之前的示例代码中：

```java
public void writer() {
  x = 42;
  v = true;
}
```

根据程序顺序规则，`x = 42` Happens-Before `v = true`。这符合我们在单线程中的直觉：前面对变量的修改一定对后面的操作可见。

## 2. volatile 变量规则

这条规则规定：对一个 volatile 变量的写操作 Happens-Before 后续对这个 volatile 变量的读操作。

乍一看，这似乎只是重申了 volatile 变量的基本特性：禁用缓存。但是，当我们将这条规则与下一条规则（传递性）结合起来，就会发现它的强大之处。

## 3. 传递性规则

这条规则看起来很像数学中的传递性：如果 A Happens-Before B，且 B Happens-Before C，那么 A Happens-Before C。

让我们把这条规则应用到我们的例子中：

![Happens-Before 传递性示意图](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/uPic/uvjL4NMTvdRN.png)

从图中我们可以看出：

- 根据程序顺序规则，`x = 42` Happens-Before `v = true`
- 根据 volatile 变量规则，`v = true`（写操作）Happens-Before `v == true`（读操作）
- 根据传递性规则，我们可以得出：`x = 42` Happens-Before `v == true`（读操作）

这意味着什么？这意味着如果线程 B 读到了 `v = true`，那么线程 A 设置的 `x = 42` 对线程 B 来说是可见的。换句话说，在这种情况下，线程 B 一定能看到 `x == 42`。

这就是 Java 1.5 版本对 volatile 语义的增强。这个增强非常重要，Java 1.5 版本的并发工具包（java.util.concurrent）就是依靠这个增强的 volatile 语义来保证可见性的。

## 4. 管程中锁的规则

这条规则规定：一个锁的解锁操作 Happens-Before 后续对这个锁的加锁操作。

要理解这条规则，我们首先需要知道什么是"管程"。管程是一种通用的同步机制，在 Java 中，synchronized 关键字就是对管程的实现。

在 Java 中，管程的锁是隐式的。例如：

```java
synchronized (this) { // 这里自动加锁
  // x 是共享变量，初始值为 10
  if (this.x < 12) {
    this.x = 12;
  }
} // 这里自动解锁
```

在进入 synchronized 块时，Java 会自动加锁；在离开 synchronized 块时，Java 会自动解锁。

结合管程中锁的规则，我们可以这样理解：假设 x 的初始值是 10，线程 A 执行完这段代码后，x 的值变成了 12。当线程 B 进入这段代码时，它能够看到线程 A 对 x 的修改，也就是说，线程 B 能够看到 x == 12。这符合我们的直觉，应该不难理解。

## 5. 线程 start() 规则

这条规则涉及线程的启动。它规定：主线程 A 启动子线程 B 后，子线程 B 能够看到主线程在启动子线程 B 之前的所有操作。

换句话说，如果线程 A 调用了线程 B 的 start() 方法（也就是在线程 A 中启动了线程 B），那么这个 start() 操作 Happens-Before 线程 B 中的任何操作。

我们来看一个具体的例子：

```java
Thread B = new Thread(()->{
  // 主线程调用 B.start() 之前
  // 对共享变量的所有修改，在这里都是可见的
  // 在这个例子中，var == 77
});
// 这里修改共享变量 var
var = 77;
// 主线程启动子线程
B.start();
```

在这个例子中，子线程 B 一定能看到 var == 77，因为这个赋值操作发生在 B.start() 之前。

## 6. 线程 join() 规则

这条规则涉及线程的等待。它规定：如果主线程 A 等待子线程 B 完成（通过调用 B 的 join() 方法），那么当 join() 方法返回时，主线程 A 能够看到子线程 B 的所有操作。

换句话说，如果在线程 A 中调用了线程 B 的 join() 方法，并且这个方法成功返回，那么线程 B 中的任何操作都 Happens-Before 于 join() 方法的返回。

让我们看一个例子：

```java
Thread B = new Thread(()->{
  // 这里修改共享变量 var
  var = 66;
});
// 主线程启动子线程
B.start();
B.join()
// 在调用 B.join() 之后
// 子线程对共享变量的所有修改在这里都是可见的
// 在这个例子中，var == 66
```

在这个例子中，主线程调用 B.join() 之后，一定能看到 var == 66，因为这个赋值操作发生在 B 线程中，而 B.join() 保证了这个操作对主线程可见。

# 被我们忽视的 final

final 关键字在 Java 中常用于声明常量，它的初衷是告诉编译器：这个变量一旦被初始化就不会再改变，你可以尽情地优化。

在 Java 1.5 版本之后，Java 内存模型对 final 类型变量的重排序进行了限制。只要我们正确地构造对象（避免构造过程中的"逸出"），就不会出现问题。

那么，什么是"逸出"呢？让我们看一个例子：

```java
final int x;
// 错误的构造函数
public FinalFieldExample() {
  x = 3;
  y = 4;
  // 这里就是"逸出"，将 this 赋值给了全局变量
  global.obj = this;
}
```

在这个例子中，构造函数将 this 赋值给了全局变量 global.obj，这就是所谓的"逸出"。在这种情况下，其他线程可能通过 global.obj 读取到 x 的值，而这时 x 可能还没有被正确初始化（可能读到 0）。

因此，在使用 final 变量时，我们必须确保对象的构造过程是完整的，不要让半构造的对象被其他线程访问到。这样，我们就能充分利用 final 关键字带来的优化，同时避免出现意外的并发问题。
