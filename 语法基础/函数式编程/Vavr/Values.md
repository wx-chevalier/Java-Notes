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
