# Observable（被观察者）

在 ReactiveX 中，一个观察者(Observer)订阅一个可观察对象(Observable)。观察者对 Observable 发射的数据或数据序列作出响应。这种模式可以极大地简化并发操作，因为它创建了一个处于待命状态的观察者哨兵，在未来某个时刻响应 Observable 的通知，不需要阻塞等待 Observable 发射数据。

![Observable 事件流](https://s2.ax1x.com/2019/12/19/QqTJHS.png)

Observable 即被观察者，它决定什么时候触发事件以及触发怎样的事件。RxJava 使用 create() 方法来创建一个 Observable ，并为它定义事件触发规则：

```java
//匿名类方式
Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
    @Override
    public void call(Subscriber<? super String> subscriber) {
        subscriber.onNext("Hello");
        subscriber.onNext("Hi");
        subscriber.onNext("Aloha");
        subscriber.onCompleted();
    }
});


//lambda方式
Observable observable = Observable.create((subscriber)->{
        subscriber.onNext("Hello");
        subscriber.onNext("Hi");
        subscriber.onNext("Aloha");
        subscriber.onCompleted();
});
```

可以看到，这里传入了一个 `OnSubscribe` 对象作为参数。`OnSubscribe` 会被存储在返回的 `Observable` 对象中，它的作用相当于一个计划表，当 `Observable` 被订阅的时候，`OnSubscribe` 的 `call()` 方法会自动被调用，事件序列就会依照设定依次触发(对于上面的代码，就是观察者`Subscriber` 将会被调用三次 `onNext()` 和一次 `onCompleted()`)。这样，由被观察者调用了观察者的回调方法，就实现了由被观察者向观察者的事件传递，即观察者模式。

# 背景知识

在很多软件编程任务中，或多或少你都会期望你写的代码能按照编写的顺序，一次一个的顺序执行和完成。但是在 ReactiveX 中，很多指令可能是并行执行的，之后他们的执行结果才会被观察者捕获，顺序是不确定的。为达到这个目的，你定义一种获取和变换数据的机制，而不是调用一个方法。在这种机制下，存在一个可观察对象(Observable)，观察者(Observer)订阅(Subscribe)它，当数据就绪时，之前定义的机制就会分发数据给一直处于等待状态的观察者哨兵。

这种方法的优点是，如果你有大量的任务要处理，它们互相之间没有依赖关系。你可以同时开始执行它们，不用等待一个完成再开始下一个（用这种方式，你的整个任务队列能耗费的最长时间，不会超过任务里最耗时的那个）。有很多术语可用于描述这种异步编程和设计模式，在在本文里我们使用这些术语：一个观察者订阅一个可观察对象 (An observer subscribes to an Observable)。通过调用观察者的方法，Observable 发射数据或通知给它的观察者。在其它的文档和场景里，有时我们也将 Observer 叫做 Subscriber、Watcher、Reactor。这个模型通常被称作 Reactor 模式。

# 创建 Observable

`create()` 方法是 RxJava 最基本的创造事件序列的方法。基于这个方法， RxJava 还提供了一些方法用来快捷创建事件队列。

## just

`just(T...)`: 将传入的参数依次发送出来。

```java
Observable observable = Observable.just("Hello", "Hi", "Aloha");
// 将会依次调用：
// onNext("Hello");
// onNext("Hi");
// onNext("Aloha");
// onCompleted();
```

## from

`from(T[])` / `from(Iterable)` : 将传入的数组或 `Iterable` 拆分成具体对象后，依次发送出来。

```java
String[] words = {"Hello", "Hi", "Aloha"};
Observable observable = Observable.from(words);
// 将会依次调用：
// onNext("Hello");
// onNext("Hi");
// onNext("Aloha");
// onCompleted();
```

上面 `just(T...)` 的例子和 `from(T[])` 的例子，都和之前的 `create(OnSubscribe)` 的例子是等价的。

## interval

interval 用于创建一个根据固定的时间间隔发射序列数据的 Observable。

![](http://reactivex.io/documentation/operators/images/interval.c.png)

# Subscribe(订阅)

在创建好了 Observable 之后，即有了待观察对象之后，就需要设置每当这个对象发射消息时候的响应动作。

```java
observable.subscribe(observer);
// 或者：
observable.subscribe(subscriber);
```

有人可能会注意到， `subscribe()` 这个方法有点怪：它看起来是`observalbe` 订阅了 `observer` / `subscriber`而不是`observer` / `subscriber` 订阅了 `observalbe`，这看起来就像杂志订阅了读者一样颠倒了对象关系。这让人读起来有点别扭，不过如果把 API 设计成 `observer.subscribe(observable)` / `subscriber.subscribe(observable)` ，虽然更加符合思维逻辑，但对流式 API 的设计就造成影响了，比较起来明显是得不偿失的。

# Single
