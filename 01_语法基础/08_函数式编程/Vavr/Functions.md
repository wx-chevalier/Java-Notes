# Functions

# 函数式接口

Java 8 提供了接受一个参数的函数式接口 Function 和接受两个参数的函数式接口 BiFunction，vavr 则提供了最多可以接受 8 个参数的函数式接口：`Function0、Function1、Function2、Function3、Function4,...,Function8`。

```java
@Test
public void givenVavrFunction_whenWorks_thenCorrect() {
    Function1<Integer, Integer> square = (num) -> num * num;
    int result = square.apply(2);

    assertEquals(4, result);
}

@Test
public void givenVavrBiFunction_whenWorks_thenCorrect() {
    Function2<Integer, Integer, Integer> sum =
      (num1, num2) -> num1 + num2;
    int result = sum.apply(5, 7);

    assertEquals(12, result);
}
```

当没有参数但我们仍然需要一个输出时，在 Java 8 中，我们需要使用一个 Consumer 类型，在 Vavr 中 Function0 是有帮助的。

```java
@Test
public void whenCreatesFunction_thenCorrect0() {
    Function0<String> getClazzName = () -> this.getClass().getName();
    String clazzName = getClazzName.apply();

    assertEquals("com.baeldung.vavr.VavrTest", clazzName);
}
```

五参数函数怎么样，用 Function5 就可以了。

```java
@Test
public void whenCreatesFunction_thenCorrect5() {
    Function5<String, String, String, String, String, String> concat =
      (a, b, c, d, e) -> a + b + c + d + e;
    String finalString = concat.apply(
      "Hello ", "world", "! ", "Learn ", "Vavr");

    assertEquals("Hello world! Learn Vavr", finalString);
}
```

我们也可以结合任何一个函数的静态工厂方法 FunctionN.of，从方法引用中创建一个 Vavr 函数。就像如果我们有以下的 sum 方法。

```java
public int sum(int a, int b) {
    return a + b;
}

@Test
public void whenCreatesFunctionFromMethodRef_thenCorrect() {
    Function2<Integer, Integer, Integer> sum = Function2.of(this::sum);
    int summed = sum.apply(5, 6);

    assertEquals(11, summed);
}
```

# 组合（Composition）

在数学上，函数组合可以用两个函数形成第三个函数，例如函数 f:X->Y 和函数 g:Y->Z 可以组合成 h:g(f(x))，表示 X->Z。这里看个组合的例子。

# Lifting

你是不是常常写这种代码：调用一个函数，判断它的返回值是否符合需求，或者需要 catch 所有异常以防异常情况，甚至是 catch(Throwable t)。Lifting 特性就是为了解决这个问题而存在的，可以在内部处理异常情况，并将异常转换成一个特殊的结果 None，这样函数外部就可以用统一的模式去处理函数结果。

# 柯里化方法（Curring）

柯里化(Currying)指的是将原来接受多个参数的函数变成新的接受一个参数的函数的过程。对于 Java 来说，这是提供默认值的一种方式。

# 方法缓存（Memorization）

这是一种缓存，某个方法只需要执行一次，后面都会返回第一次的结果；但是在实际应用中用到的地方应该不多。
