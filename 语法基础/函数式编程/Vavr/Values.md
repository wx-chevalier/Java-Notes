# Values

# Option

Option 的主要目标是利用 Java 类型系统来消除我们代码中的空检查。Option 是 Vavr 中的一个对象容器，其最终目标与 Java 8 中的 Option 类似。Vavr 的 Option 实现了 Serializable、Iterable，并且拥有更丰富的 API。由于 Java 中的任何对象引用都可能有一个空值，所以我们在使用它之前通常要用 if 语句来检查空值。这些检查使代码变得健壮而稳定。

```java
@Test
public void givenValue_whenNullCheckNeeded_thenCorrect() {
    Object possibleNullObj = null;
    if (possibleNullObj == null) {
        possibleNullObj = "someDefaultValue";
    }
    assertNotNull(possibleNullObj);
}
```

如果不进行检查，应用程序可能会因为一个简单的 NPE 而崩溃。

```java
@Test(expected = NullPointerException.class)
public void givenValue_whenNullCheckNeeded_thenCorrect2() {
    Object possibleNullObj = null;
    assertEquals("somevalue", possibleNullObj.toString());
}
```

然而，这些检查使代码变得啰嗦，不那么可读，特别是当 if 语句最终被嵌套多次时。Option 解决了这个问题，它完全消除了空值，并在每个可能的情况下用一个有效的对象引用来代替它们。有了 Option，一个空值将评估为一个 None 的实例，而一个非空值将评估为一个 Some 的实例。

```java
@Test
public void givenValue_whenCreatesOption_thenCorrect() {
    Option<Object> noneOption = Option.of(null);
    Option<Object> someOption = Option.of("val");

    assertEquals("None", noneOption.toString());
    assertEquals("Some(val)", someOption.toString());
}
```

请注意，在调用 toString 之前，我们不需要做检查，也不需要像以前那样处理 NullPointerException。Option 的 toString 在每次调用中都会给我们返回有意义的值。在本节的第二个片段中，我们需要一个 null 检查，在尝试使用变量之前，我们会给它分配一个默认值。Option 可以在一行中处理这个问题，即使有一个 null。

```java
@Test
public void givenNull_whenCreatesOption_thenCorrect() {
    String name = null;
    Option<String> nameOption = Option.of(name);

    assertEquals("baeldung", nameOption.getOrElse("baeldung"));
}

@Test
public void givenNonNull_whenCreatesOption_thenCorrect() {
    String name = "baeldung";
    Option<String> nameOption = Option.of(name);

    assertEquals("baeldung", nameOption.getOrElse("notbaeldung"));
}
```

# Try

在 Vavr 中，Try 是一个可能导致异常的计算的容器。就像 Option 包装一个可空对象，这样我们就不必显式地用 if 检查来处理空值一样，Try 包装一个计算，这样我们就不必显式地用 try-catch 块来处理异常。

```java
@Test(expected = ArithmeticException.class)
public void givenBadCode_whenThrowsException_thenCorrect() {
    int i = 1 / 0;
}
```

如果没有 try-catch 块，应用程序会崩溃。为了避免这种情况，你需要用 try-catch 块来包装语句。通过 Vavr，我们可以将同样的代码包裹在一个 Try 实例中，并得到一个结果。

```java
@Test
public void givenBadCode_whenTryHandles_thenCorrect() {
    Try<Integer> result = Try.of(() -> 1 / 0);

    assertTrue(result.isFailure());
}
```

计算是否成功，可以在代码中的任何一点选择检查。在上面的代码段中，我们选择简单地检查成功或失败。我们也可以选择返回一个默认值。

```java
@Test
public void givenBadCode_whenTryHandles_thenCorrect2() {
    Try<Integer> computation = Try.of(() -> 1 / 0);
    int errorSentinel = result.getOrElse(-1);

    assertEquals(-1, errorSentinel);
}

@Test(expected = ArithmeticException.class)
public void givenBadCode_whenTryHandles_thenCorrect3() {
    Try<Integer> result = Try.of(() -> 1 / 0);
    result.getOrElseThrow(ArithmeticException::new);
}
```

## Try 异常处理

Vavr 库给我们提供了一个特殊的容器，它表示一个可能导致异常或成功完成的计算。将操作封装在 Try 对象中，我们得到的结果要么是 Success，要么是 Failure。然后我们可以根据这个类型执行进一步的操作。

```java
public class VavrTry {
    private HttpClient httpClient;

    public Try<Response> getResponse() {
        return Try.of(httpClient::call);
    }

    // standard constructors
}
```

需要注意的是一个返回类型为 `Try<Response>` 的方法。当一个方法返回这样的结果类型时，我们需要正确处理，并且要记住，这个结果类型可能是 Success 或 Failure，所以我们需要在编译时明确处理。

### 处理成功的结果

让我们写一个测试用例，在 httpClient 返回成功结果的情况下使用我们的 Vavr 类。方法 getResponse()返回的是 `Try<Resposne>` 对象。因此我们可以调用 map() 方法，只有当 Try 为 Success 类型时，才会对 Response 执行操作。

```java
@Test
public void givenHttpClient_whenMakeACall_shouldReturnSuccess() {
    // given
    Integer defaultChainedResult = 1;
    String id = "a";
    HttpClient httpClient = () -> new Response(id);

    // when
    Try<Response> response = new VavrTry(httpClient).getResponse();
    Integer chainedResult = response
      .map(this::actionThatTakesResponse)
      .getOrElse(defaultChainedResult);
    Stream<String> stream = response.toStream().map(it -> it.id);

    // then
    assertTrue(!stream.isEmpty());
    assertTrue(response.isSuccess());
    response.onSuccess(r -> assertEquals(id, r.id));
    response.andThen(r -> assertEquals(id, r.id));

    assertNotEquals(defaultChainedResult, chainedResult);
}
```

函数 actionThatTakesResponse()只是简单地将 Response 作为参数，并返回一个 id 字段的 hashCode。

```java
public int actionThatTakesResponse(Response response) {
    return response.id.hashCode();
}
```

如果 Try 里面有 Success，它就返回 Try 的值，否则就返回 defaultChainedResult。我们的 httpClient 执行成功，因此 isSuccess 方法返回 true。然后我们可以执行 onSuccess()方法，对 Response 对象进行操作。Try 也有一个方法 andThen，当 Try 的值是 Success 时，它就会接受一个 Consumer 来消费这个值。我们可以把我们的 Try 响应当作一个流。要做到这一点，我们需要使用 toStream()方法将其转换为一个 Stream，然后所有在 Stream 类中可用的操作都可以用来对该结果进行操作。如果我们想在 Try 类型上执行一个操作，我们可以使用 transform() 方法，将 Try 作为一个参数，并对其进行操作，而不需要拆开封闭的值。

```java
public int actionThatTakesTryResponse(Try<Response> response, int defaultTransformation){
    return response.transform(responses -> response.map(it -> it.id.hashCode())
      .getOrElse(defaultTransformation));
}
```

# Lazy

Lazy 是一个容器，它代表了一个懒惰计算的值，即计算被推迟到需要结果的时候。此外，被评估的值会被缓存或记忆，并在每次需要时再次返回，而不需要重复计算。

```java
@Test
public void givenFunction_whenEvaluatesWithLazy_thenCorrect() {
    Lazy<Double> lazy = Lazy.of(Math::random);
    assertFalse(lazy.isEvaluated());

    double val1 = lazy.get();
    assertTrue(lazy.isEvaluated());

    double val2 = lazy.get();
    assertEquals(val1, val2, 0.1);
}
```

在上面的例子中，我们正在评估的函数是 Math.random。请注意，在第二行中，我们检查了值，发现函数还没有被执行。这是因为我们仍然没有对返回值表现出兴趣。在第三行代码中，我们通过调用 Lazy.get 来显示对计算值的兴趣。此时，函数执行，Lazy.evaluated 返回 true。

我们还可以继续通过再次尝试获取值来确认 Lazy 的记忆位。如果再次执行我们提供的函数，我们肯定会得到一个不同的随机数。然而，Lazy 再次懒惰地返回最初计算的值，正如最后的断言所确认的那样。

### 处理异常场景

让我们写一个例子，当我们的 HttpClient 执行时，会抛出 ClientException。与前面的例子相比，我们的 getOrElse 方法将返回 defaultChainedResult，因为 Try 将是 Failure 类型。

```java
@Test
public void givenHttpClientFailure_whenMakeACall_shouldReturnFailure() {
    // given
    Integer defaultChainedResult = 1;
    HttpClient httpClient = () -> {
        throw new ClientException("problem");
    };

    // when
    Try<Response> response = new VavrTry(httpClient).getResponse();
    Integer chainedResult = response
        .map(this::actionThatTakesResponse)
        .getOrElse(defaultChainedResult);
     Option<Response> optionalResponse = response.toOption();

    // then
    assertTrue(optionalResponse.isEmpty());
    assertTrue(response.isFailure());
    response.onFailure(ex -> assertTrue(ex instanceof ClientException));
    assertEquals(defaultChainedResult, chainedResult);
}
```

方法 getReposnse() 返回 Failure，因此方法 isFailure 返回 true，我们可以对返回的响应执行 onFailure() 回调，看到异常是 ClientException 类型。我们可以在返回的响应上执行 onFailure() 回调，看到异常是 ClientException 类型。Try 类型的对象可以使用 toOption() 方法映射到 Option 类型。

当我们不想在所有代码库中携带我们的 Try 结果，但我们有一些方法使用 Option 类型来处理一个显式的缺失值时，它是有用的。当我们将 Failure 映射到 Option 时，那么方法 isEmpty()将返回 true。当 Try 对象是 Success 类型时，对它调用 toOption 将使 Option 被定义，因此方法 isDefined() 将返回 true。

### Pattern Matching

当我们的 httpClient 返回一个 Exception 时，我们可以对该 Exception 的类型进行模式匹配。然后根据该 Exception 的类型，在 recover() 方法中，我们可以决定是否要从该 Exception 中恢复，将 Failure 转化为 Success，或者将计算结果保留为 Failure。

```java
@Test
public void givenHttpClientThatFailure_whenMakeACall_shouldReturnFailureAndNotRecover() {
    // given
    Response defaultResponse = new Response("b");
    HttpClient httpClient = () -> {
        throw new RuntimeException("critical problem");
    };

    // when
    Try<Response> recovered = new VavrTry(httpClient).getResponse()
      .recover(r -> Match(r).of(
          Case(instanceOf(ClientException.class), defaultResponse)
      ));

    // then
    assertTrue(recovered.isFailure());

```

只有当 Exception 的类型是 ClientException 时，recover()方法内部的模式匹配才会将 Failure 变成 Success。否则，它将把它作为一个 Failure()。我们看到我们的 httpClient 抛出了 RuntimeException，因此我们的恢复方法不会处理这种情况，因此 isFailure() 返回 true。如果我们想从恢复的对象中获取结果，但在关键故障的情况下，我们可以使用 getOrElseThrow()方法来实现。

```java
recovered.getOrElseThrow(throwable -> {
    throw new RuntimeException(throwable);
});
```

有些错误是至关重要的，当它们发生时，我们希望通过在调用堆栈中较高的位置抛出异常来明确地发出信号，让调用者决定进一步的异常处理。在这种情况下，像上面的例子一样重新抛出异常是非常有用的。当我们的客户端抛出一个非关键异常时，我们在 recover() 方法中的模式匹配将把我们的 Failure 变成 Success。我们正在从两种类型的异常 ClientException 和 IllegalArgumentException 中恢复。

```java
@Test
public void givenHttpClientThatFailure_whenMakeACall_shouldReturnFailureAndRecover() {
    // given
    Response defaultResponse = new Response("b");
    HttpClient httpClient = () -> {
        throw new ClientException("non critical problem");
    };

    // when
    Try<Response> recovered = new VavrTry(httpClient).getResponse()
      .recover(r -> Match(r).of(
        Case(instanceOf(ClientException.class), defaultResponse),
        Case(instanceOf(IllegalArgumentException.class), defaultResponse)
       ));

    // then
    assertTrue(recovered.isSuccess());
}
```

# Either

在函数式编程的世界里，函数值或对象不能被修改（即以正常形式）；在 Java 术语中，它被称为不可变的变量。Either 代表两种可能的数据类型的值。一个 Either 要么是左，要么是右。按照惯例，左表示失败的情况结果，右表示成功。

让我们考虑一个用例，在这个用例中，我们需要创建一个方法，该方法接受一个输入，并根据输入返回一个字符串或一个整数。我们可以用两种方式实现这个方法。要么我们的方法可以返回一个带有代表成功/失败结果的键的映射，要么它可以返回一个固定大小的 List/Array，其中位置表示一个结果类型。

```java
public static Map<String, Object> computeWithoutEitherUsingMap(int marks) {
    Map<String, Object> results = new HashMap<>();
    if (marks < 85) {
        results.put("FAILURE", "Marks not acceptable");
    } else {
        results.put("SUCCESS", marks);
    }
    return results;
}

public static void main(String[] args) {
    Map<String, Object> results = computeWithoutEitherUsingMap(8);

    String error = (String) results.get("FAILURE");
    int marks = (int) results.get("SUCCESS");
}

public static Object[] computeWithoutEitherUsingArray(int marks) {
    Object[] results = new Object[2];
    if (marks < 85) {
        results[0] = "Marks not acceptable";
    } else {
        results[1] = marks;
    }
    return results;
}
```

我们可以看到，这两种方式都需要相当大的工作量，而且最后的效果不是很美观，使用起来也不安全。现在让我们看看如何利用 Vavr 的 Either 工具来实现同样的结果。

```java
private static Either<String, Integer> computeWithEither(int marks) {
    if (marks < 85) {
        return Either.left("Marks not acceptable");
    } else {
        return Either.right(marks);
    }
}
```

此外，Either 还提供了一个非常方便的类似 monadic 的 API 来处理这两种情况。

```java
computeWithEither(80)
  .right()
  .filter(...)
  .map(...)
  // ...
```

按照惯例，Either 的左属性代表失败的情况，右属性代表成功。但是，根据我们的需要，我们可以使用投影来改变这种情况：Vavr 中的 Either 并不偏向左或右。如果我们向右投射，如果 Either 为左，则 filter()、map()等操作将没有效果。

```java
computeWithEither(90).right()
  .filter(...)
  .map(...)
  .getOrElse(Collections::emptyList);
```
