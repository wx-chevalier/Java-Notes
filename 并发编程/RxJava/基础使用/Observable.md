# Observable(被观察者)

Observable 即被观察者，它决定什么时候触发事件以及触发怎样的事件。RxJava 使用 create() 方法来创建一个 Observable ，并为它定义事件触发规则：

```
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

## Creating Observable

`create()` 方法是 RxJava 最基本的创造事件序列的方法。基于这个方法， RxJava 还提供了一些方法用来快捷创建事件队列。

### just

`just(T...)`: 将传入的参数依次发送出来。

```
Observable observable = Observable.just("Hello", "Hi", "Aloha");
// 将会依次调用：
// onNext("Hello");
// onNext("Hi");
// onNext("Aloha");
// onCompleted();
```

### from

`from(T[])` / `from(Iterable)` : 将传入的数组或 `Iterable` 拆分成具体对象后，依次发送出来。

```
String[] words = {"Hello", "Hi", "Aloha"};
Observable observable = Observable.from(words);
// 将会依次调用：
// onNext("Hello");
// onNext("Hi");
// onNext("Aloha");
// onCompleted();
```

上面 `just(T...)` 的例子和 `from(T[])` 的例子，都和之前的 `create(OnSubscribe)` 的例子是等价的。

### interval

interval 用于创建一个根据固定的时间间隔发射序列数据的 Observable。
![](http://reactivex.io/documentation/operators/images/interval.c.png)

## Subscribe(订阅)

在创建好了 Observable 之后，即有了待观察对象之后，就需要设置每当这个对象发射消息时候的响应动作。

```
observable.subscribe(observer);
// 或者：
observable.subscribe(subscriber);
```

有人可能会注意到， `subscribe()` 这个方法有点怪：它看起来是『`observalbe` 订阅了 `observer` / `subscriber`』而不是『`observer` / `subscriber` 订阅了 `observalbe`』，这看起来就像『杂志订阅了读者』一样颠倒了对象关系。这让人读起来有点别扭，不过如果把 API 设计成 `observer.subscribe(observable)` / `subscriber.subscribe(observable)` ，虽然更加符合思维逻辑，但对流式 API 的设计就造成影响了，比较起来明显是得不偿失的。
