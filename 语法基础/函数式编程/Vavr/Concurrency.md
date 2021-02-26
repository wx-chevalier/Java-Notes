# 并发编程

# Future

Core Java 为异步计算提供了一个基本的 API：Future。CompletableFuture 是其最新的实现之一。Vavr 提供了它对 Future API 的新功能替代。在本文中，我们将讨论新的 API，并展示如何利用它的一些新功能。Future 可以处于两种状态之一：

- Pending: 计算正在进行
- Completd：计算成功完成，有结果，有异常失败，或被取消。

与核心的 Java Future 相比，主要的优势在于我们可以轻松地注册回调，并以非阻塞的方式编译操作。

## 基础操作

```java
String initialValue = "Welcome to ";
Future<String> resultFuture = Future.of(() -> someComputation());
```

我们可以通过简单地调用 get()或 getOrElse()方法从 Future 中提取值。

```java
String result = resultFuture.getOrElse("Failed to get underlying value.");
```

get() 和 getOrElse() 的区别在于，get()是最简单的解决方案，而 getOrElse()可以让我们在没有能够检索到 Future 里面的值的情况下，返回一个任何类型的值。建议使用 getOrElse()，这样我们就可以处理在试图从 Future 中检索值时发生的任何错误。为了简单起见，我们在接下来的几个例子中只使用 get()。请注意，如果需要等待结果，get()方法会阻塞当前线程。

另一种方法是调用非阻塞的 getValue()方法，它返回一个 Option<Try<T>>，只要计算尚未完成，这个 Option<Try<T>>就会是空的。然后我们可以提取计算结果，它就在 Try 对象中。

```java
Option<Try<String>> futureOption = resultFuture.getValue();
Try<String> futureTry = futureOption.get();
String result = futureTry.get();
```

有时我们需要在从 Future 中检索值之前，检查 Future 是否包含一个值。我们可以简单地通过使用：

```java
resultFuture.isEmpty();
```

需要注意的是，isEmpty()方法是阻塞的，它将阻塞线程，直到它的操作结束。Future 使用一个 ExecutorService 来异步运行它们的计算。默认的 ExecutorService 是 Executors.newCachedThreadPool()。我们可以通过传递一个我们选择的实现来使用另一个 ExecutorService。

```java
@Test
public void whenChangeExecutorService_thenCorrect() {
    String result = Future.of(newSingleThreadExecutor(), () -> HELLO)
      .getOrElse(error);

    assertThat(result)
      .isEqualTo(HELLO);
}
```

该 API 支持与 java.util.CompletableFuture 集成。因此，如果我们想执行只有核心 Java API 支持的操作，我们可以很容易地将一个 Future 转换为一个 CompletableFuture。

```java
@Test
public void whenConvertToCompletableFuture_thenCorrect()
  throws Exception {

    CompletableFuture<String> convertedFuture = Future.of(() -> HELLO)
      .toCompletableFuture();

    assertThat(convertedFuture.get())
      .isEqualTo(HELLO);
}
```

## 回调与异常处理

API 提供了 onSuccess()方法，一旦 Future 成功完成，就会执行一个动作。同样，onFailure()方法也会在 Future 失败时执行。

```java
Future<String> resultFuture = Future.of(() -> appendData(initialValue))
  .onSuccess(v -> System.out.println("Successfully Completed - Result: " + v))
  .onFailure(v -> System.out.println("Failed - Result: " + v));
```

onComplete() 方法接受一个动作，一旦 Future 完成执行，无论 Future 是否成功，该动作都将被运行。andThen()方法与 onComplete() 类似：它只是保证回调按照特定的顺序执行。

```java
Future<String> resultFuture = Future.of(() -> appendData(initialValue))
  .andThen(finalResult -> System.out.println("Completed - 1: " + finalResult))
  .andThen(finalResult -> System.out.println("Completed - 2: " + finalResult));
```

当一个 Future 失败时，我们可以用几种方法来处理这个错误。例如，我们可以利用 recover() 方法来返回另一个结果，如错误信息。

```java
@Test
public void whenFutureFails_thenGetErrorMessage() {
    Future<String> future = Future.of(() -> "Hello".substring(-1))
      .recover(x -> "fallback value");

    assertThat(future.get())
      .isEqualTo("fallback value");
}
```

或者，我们可以使用 recoverWith()返回另一个 Future 计算的结果。

```java
@Test
public void whenFutureFails_thenGetAnotherFuture() {
    Future<String> future = Future.of(() -> "Hello".substring(-1))
      .recoverWith(x -> Future.of(() -> "fallback value"));

    assertThat(future.get())
      .isEqualTo("fallback value");
}
```

方法 fallbackTo() 是处理错误的另一种方法。它在一个 Future 上被调用，并接受另一个 Future 作为参数。如果第一个 Future 成功，那么它就返回它的结果，否则，如果第二个 Future 成功，那么它就返回它的结果。否则，如果第二个 Future 成功，则返回其结果。如果两个 Future 都失败了，那么 failpt()方法返回一个 Throwable 的 Future，它保存了第一个 Future 的错误。

```java
@Test
public void whenBothFuturesFail_thenGetErrorMessage() {
    Future<String> f1 = Future.of(() -> "Hello".substring(-1));
    Future<String> f2 = Future.of(() -> "Hello".substring(-2));

    Future<String> errorMessageFuture = f1.fallbackTo(f2);
    Future<Throwable> errorMessage = errorMessageFuture.failed();

    assertThat(
      errorMessage.get().getMessage())
      .isEqualTo("String index out of range: -1");
}
```

## 其他属性

```java
// 阻塞、取消、获取
resultFuture.await();
resultFuture.cancel();
resultFuture.executorService();

// 属性
@Test
public void whenDivideByZero_thenCorrect() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0)
      .await();

    assertThat(resultFuture.isCompleted()).isTrue();
    assertThat(resultFuture.isSuccess()).isFalse();
    assertThat(resultFuture.isFailure()).isTrue();
}

// 捕获异常
@Test
public void whenDivideByZero_thenGetThrowable2() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0)
      .await();

    assertThat(resultFuture.getCause().get().getMessage())
      .isEqualTo("/ by zero");
}

@Test
public void whenDivideByZero_thenGetThrowable1() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0);

    assertThatThrownBy(resultFuture::get)
      .isInstanceOf(ArithmeticException.class);
}
```

其他转换操作：

```java
@Test
public void whenCallMap_thenCorrect() {
    Future<String> futureResult = Future.of(() -> "from Baeldung")
      .map(a -> "Hello " + a)
      .await();

    assertThat(futureResult.get())
      .isEqualTo("Hello from Baeldung");
}
```

如果我们将一个返回 Future 的函数传递给 map()方法，我们可能最终会得到一个嵌套的 Future 结构。为了避免这种情况，我们可以利用 flatMap()方法。

```java
@Test
public void whenCallFlatMap_thenCorrect() {
    Future<Object> futureMap = Future.of(() -> 1)
      .flatMap((i) -> Future.of(() -> "Hello: " + i));

    assertThat(futureMap.get()).isEqualTo("Hello: 1");
}

@Test
public void whenTransform_thenCorrect() {
    Future<Object> future = Future.of(() -> 5)
      .transformValue(result -> Try.of(() -> HELLO + result.get()));

    assertThat(future.get()).isEqualTo(HELLO + 5);
}

@Test
public void whenCallZip_thenCorrect() {
    Future<String> f1 = Future.of(() -> "hello1");
    Future<String> f2 = Future.of(() -> "hello2");

    assertThat(f1.zip(f2).get())
      .isEqualTo(Tuple.of("hello1", "hello2"));
}
```
