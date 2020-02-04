# RxJava 介绍

RxJava 是 JVM 的响应式扩展(ReactiveX)，它是通过使用可观察的序列将异步和基于事件的程序组合起来的一个库。RxJava 实现的响应式的流可以保证随着业务逻辑的复杂化而依然保证代码的简洁与可理解性。

```java
Observable.from(1, 2, 3, 4, 5)
.filter((v) -> {
    return v < 4;
})
.subscribe((value) -> {
    System.out.println("Value: " + value);
});

Observable.from(1, 2, 3, 4, 5)
.reduce((seed, value) -> {
    // sum all values from the sequence
    return seed + value;
})
.map((v) -> {
    return "DecoratedValue: " + v;
})
.subscribe((value) -> {
    System.out.println(value);
});
```

# 背景分析

RxJava 与 Java 本身提供的异步模型以及其他响应式编程框架相比而言有以下特点：

- 观察者模式:RxJava 用到了设计模式中的观察者模式。支持数据或事件序列，允许对序列进行组合，并对线程、同步和并发数据结构进行了抽象。
- 轻量:无依赖库、Jar 包小于 1M
- 支持多语言:支持 Java 6+和 Android 2.3+。RxJava 设计初衷就是兼容所有 JVM 语言，目前支持的 JVM 语言有 Groovy,Clojure,JRuby, Kotlin 和 Scala。
- 多线程支持:封装了各种并发实现，如 threads, pools, event loops, fibers, actors。

需要注意的是在 RxJava 的默认规则中，事件的发出和消费都是在同一个线程的。也就是说，如果你只是创建了 Observable 并且为它设定了 Observer，实现出来的只是一个同步的观察者模式。观察者模式本身的目的就是后 台处理，前台回调的异步机制，因此异步对于 RxJava 是至关重要的。而要实现异步，则需要用到 RxJava 的另一个概念: Scheduler 。笔者在最初进行 RxJava 学习的时候以为 RxJava 是自动为每个 Observable 创建一个线程，这种理解就是错误的。可以参考[理解 RxJava 的线程模型](http://colobu.com/2016/07/25/understanding-rxjava-thread-model/)

## 观察者模式

RxJava 的异步实现，是通过一种扩展的观察者模式来实现的。在本部分先简要描述下观察者模式，观察者模式面向的需求是：A 对象(观察者)对 B 对象(被观察者)的某种变化高度敏感，需要在 B 变化的一瞬间做出反应。举个例子，新闻里喜闻乐见的警察抓小偷，警察需要在小偷伸手作案的时候实施抓捕。在这个例子里，警察是观察者，小偷是被观察者，警察需要时刻盯着小偷的一举一动，才能保证不会漏过任何瞬间。程序的观察者模式和这种真正的观察略有不同，观察者不需要时刻盯着被观察者(例如 A 不需要每过 2ms 就检查一次 B 的状态)，而是采用注册(Register)或者称为订阅(Subscribe)的方式，告诉被观察者：我需要你的某某状态，你要在它变化的时候通知我。在 Java 的 Swing 编程或者 Android 编程中的`OnClickListener`都是典型的观察者模式，而对应的`View`就是观察者，二者通`setOnClickListener`方法达成订阅关系。订阅之后用户点击按钮的瞬间，Android Framework 就会将点击事件发送给已经注册的 `OnClickListener` 。采取这样被动的观察方式，既省去了反复检索状态的资源消耗，也能够得到最高的反馈速度。OnClickListener 的模式大致如下图所示：
![](http://ww4.sinaimg.cn/mw1024/52eb2279jw1f2rx42h1wgj20fz03rglt.jpg)
如图所示，通过 `setOnClickListener()` 方法，`Button` 持有 `OnClickListener` 的引用(这一过程没有在图上画出)；当用户点击时，`Button` 自动调用 `OnClickListener` 的 `onClick()` 方法。另外，如果把这张图中的概念抽象出来(`Button` -> 被观察者、`OnClickListener` -> 观察者、`setOnClickListener()` -> 订阅，`onClick()` -> 事件)，就由专用的观察者模式(例如只用于监听控件点击)转变成了通用的观察者模式。如下图：
![](http://ww3.sinaimg.cn/mw1024/52eb2279jw1f2rx4446ldj20ga03p74h.jpg)

## 函数响应式

### 微软的函数响应式框架

函数响应式编程是一个来自 90 年代后期受微软的一名计算机科学家 Erik Meijer 启发的思想，用来设计和开发微软的 Rx 库。Rx 是微软.NET 的一个响应式扩展。Rx 借助可观测的序列提供一种简单的方式来创建异步的，基于事件驱动的程序。开发者可以使用 Observables 模拟异步数据流，使用 LINQ 语法查询 Observables，并且很容易管理调度器的并发。Rx 让众所周知的概念变得易于实现和消费，例如 push 方法。在响应式的世界里，我们不能假装作用户不关注或者是不抱怨它而一味的等待函数的返回结果，网络调用，或者数据库查询的返回结果。我们时刻都在等待某些东西，这就让我们失去了并行处理其他事情的机会，提供更好的用户体验，让我们的软件免受顺序链的影响，而阻塞编程。

| .NET Observable              | 一个返回值 | 多个返回值    |
| ---------------------------- | ---------- | ------------- |
| Pull/Synchronous/Interactive | `T`        | `IEnumerable` |
| Push/Asynchronous/Reactive   | `Task`     | `IObservable` |

### ReactiveX 框架

ReactiveX 并不特指某种编程语言,他应该算是一种编程思维,反应式编程。反应式编程的核心在于,当触发特定行为逻辑后(对于 ReactiveX 而言,就是调用了 subscribe 指令),根据进行指定操作,并根据操作结果执行特定操作。这种编程思维特别适合用于交互式软件上,例如 Android,iOS,通常用户触发某个条件(比如说点击操作)后,我们需要根据用户的操作行为, 可能接下来要执行一系列操作,最后再根据操作结果在 ui 界面上呈现给用户。而 ReactiveX 为我们提供了这种交互流程的封装。

## 优劣对比

RxJava 最大的优势在于其简洁性，参考笔者其他的关于 Java 异步的文章，可以发现在调度过程比较复杂的情况下，异步代码经常会既难写也难被读懂，譬如笔者在[RARF](https://github.com/wx-chevalier/RARF-Java)的 Java 实现中大量使用了 RxJava。RxJava 在 Android 开发中，即一些客户端开发中会起到很大的作用。首先我们从异步序列、数据获取方式、数据传递方式以及增强功能 4 个方面进行比较。

| 异步序列     | 异步序列：通常我们获取一个同步对象，可以这么写`T getData()`；获取一个异步对象，可以这么写`Future getData()`；而获取一个同步序列，可以这么写`Iterable getData()`。那获取一个异步序列呢，Java 没有提供相应方法，RxJava 填充了这一空白，我们可以这么写`Observable getData()`，关于 Observable 的相关介绍稍后会有。                                                      |
| ------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 数据获取方式 | Java 中如果不使用观察者模式，数据都是主动获取，即**Pull**方式，对于列表数据，也是使用 Iterator 轮询获取。RxJava 由于用到了观察者模式，数据是被动获取，由被观察者向观察者发出通知，即**Push**方式，                                                                                                                                                                   |
| 数据传递方式 | 对于同步数据操作，Java 中可以顺序传递结果，即**operation1 -> operation2 -> operation3**。异步操作通常则需要使用 Callback 回调，然后在回调中继续后续操作，即**Callback1 -> Callback2 -> Callback3**，可能会存在很多层嵌套。而 RxJava 同步和异步都是链式调用，即**operation1 -> operation2 -> operation3**，这种做法的好处就是即时再复杂的逻辑都简单明了，不容易出错。 |
| 增强功能     | 比观察者模式功能更强大，在 onNext()回调方法基础上增加了 onCompleted()和 OnError()，当事件执行完或执行出错时回调。此外还可以很方便的切换事件生产和消费的线程。事件还可以组合处理，                                                                                                                                                                                    |

而一个典型的 RxJava 的代码片如下所示，这里使用的是 Retrolambda，有兴趣的朋友可以参阅下。

```java
Observable.from(folders)
    .flatMap((Func1) (folder) -> { Observable.from(file.listFiles()) })
    .filter((Func1) (file) -> { file.getName().endsWith(".png") })
    .map((Func1) (file) -> { getBitmapFromFile(file) })
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe((Action1) (bitmap) -> { imageCollectorView.addImage(bitmap) });
```
