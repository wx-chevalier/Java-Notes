# Flowable

在以前的 RxJava 版本中，只有一个基类可以处理可感知背压的源和不可感知背压的源 Observable。RxJava 2 在这两种源之间引入了明显的区别-背压感知源现在使用专用类 Flowable 表示。Observable 不支持背压，因此，我们应该将其用于仅消耗而无法影响的资源。

# 创建与控制

```java
// Simple Flowable
Flowable<Integer> integerFlowable = Flowable.just(1, 2, 3, 4);

// Flowable from Observable
Observable<Integer> integerObservable = Observable.just(1, 2, 3);
Flowable<Integer> integerFlowable = integerObservable
  .toFlowable(BackpressureStrategy.BUFFER);

// from FlowableOnSubscribe
FlowableOnSubscribe<Integer> flowableOnSubscribe
 = flowable -> flowable.onNext(1);
Flowable<Integer> integerFlowable = Flowable
  .create(flowableOnSubscribe, BackpressureStrategy.BUFFER);
```

# BackpressureStrategy

诸如 toFlowable() 或 create() 之类的某些方法采用 BackpressureStrategy 作为参数。BackpressureStrategy 是一个枚举，它定义了我们将应用于 Flowable 的背压行为。它可以缓存或丢弃事件，或者根本不执行任何行为，在最后一种情况下，我们将负责使用反压运算符定义它。BackpressureStrategy 与 RxJava 先前版本中存在的 BackpressureMode 相似。

RxJava 2 提供了五种不同的策略。

## Buffer

如果我们使用 BackpressureStrategy.BUFFER，则源将缓冲所有事件，直到订阅者可以使用它们：

```java
public void thenAllValuesAreBufferedAndReceived() {
    List testList = IntStream.range(0, 100000)
      .boxed()
      .collect(Collectors.toList());

    Observable observable = Observable.fromIterable(testList);
    TestSubscriber<Integer> testSubscriber = observable
      .toFlowable(BackpressureStrategy.BUFFER)
      .observeOn(Schedulers.computation()).test();

    testSubscriber.awaitTerminalEvent();

    List<Integer> receivedInts = testSubscriber.getEvents()
      .get(0)
      .stream()
      .mapToInt(object -> (int) object)
      .boxed()
      .collect(Collectors.toList());

    assertEquals(testList, receivedInts);
}
```

这类似于在 Flowable 上调用 onBackpressureBuffer() 方法，但是它不允许显式定义缓冲区大小或 onOverflow 操作。

## Drop

我们可以使用 BackpressureStrategy.DROP 丢弃无法消耗的事件，而不是对其进行缓冲。同样，这类似于在 Flowable 上使用 onBackpressureDrop()：

```java
public void whenDropStrategyUsed_thenOnBackpressureDropped() {

    Observable observable = Observable.fromIterable(testList);
    TestSubscriber<Integer> testSubscriber = observable
      .toFlowable(BackpressureStrategy.DROP)
      .observeOn(Schedulers.computation())
      .test();
    testSubscriber.awaitTerminalEvent();
    List<Integer> receivedInts = testSubscriber.getEvents()
      .get(0)
      .stream()
      .mapToInt(object -> (int) object)
      .boxed()
      .collect(Collectors.toList());

    assertThat(receivedInts.size() < testList.size());
    assertThat(!receivedInts.contains(100000));
 }
```

## Latest

使用 BackpressureStrategy.LATEST 将强制源仅保留最新事件，从而在使用者无法跟上时覆盖任何先前的值：

```java
public void whenLatestStrategyUsed_thenTheLastElementReceived() {

    Observable observable = Observable.fromIterable(testList);
    TestSubscriber<Integer> testSubscriber = observable
      .toFlowable(BackpressureStrategy.LATEST)
      .observeOn(Schedulers.computation())
      .test();

    testSubscriber.awaitTerminalEvent();
    List<Integer> receivedInts = testSubscriber.getEvents()
      .get(0)
      .stream()
      .mapToInt(object -> (int) object)
      .boxed()
      .collect(Collectors.toList());

    assertThat(receivedInts.size() < testList.size());
    assertThat(receivedInts.contains(100000));
 }
```

当我们查看代码时，BackpressureStrategy.LATEST 和 BackpressureStrategy.DROP 看起来非常相似。但是，BackpressureStrategy.LATEST 将覆盖我们的 Subscriber 无法处理的元素，并仅保留最新的元素。另一方面，BackpressureStrategy.DROP 将丢弃无法处理的元素，这意味着不一定会发出最新元素。

## Error

当我们使用 BackpressureStrategy.ERROR 时，我们只是在说我们不希望发生背压。 因此，如果使用者跟不上源，就应该抛出 MissingBackpressureException：

```java
public void whenErrorStrategyUsed_thenExceptionIsThrown() {
    Observable observable = Observable.range(1, 100000);
    TestSubscriber subscriber = observable
      .toFlowable(BackpressureStrategy.ERROR)
      .observeOn(Schedulers.computation())
      .test();

    subscriber.awaitTerminalEvent();
    subscriber.assertError(MissingBackpressureException.class);
}
```

## Missing

如果使用 BackpressureStrategy.MISSING，则源将推送元素而不会丢弃或缓冲。在这种情况下，下游将必须处理溢出：

```java
public void whenMissingStrategyUsed_thenException() {
    Observable observable = Observable.range(1, 100000);
    TestSubscriber subscriber = observable
      .toFlowable(BackpressureStrategy.MISSING)
      .observeOn(Schedulers.computation())
      .test();
    subscriber.awaitTerminalEvent();
    subscriber.assertError(MissingBackpressureException.class);
}
```

在我们的测试中，我们同时针对 ERROR 和 MISSING 策略都缺少 MissingbackpressureException。 因为当源的内部缓冲区溢出时，它们都将引发此类异常。但是，值得注意的是，两者都有不同的目的。当我们完全不期望背压时，应该使用前一种，并且我们希望源抛出异常以防万一。如果我们不想在创建 Flowable 时指定默认行为，则可以使用后一种方法。
