# Pattern Matching

模式匹配是函数式编程语言中的概念，目前 Java 中还不支持这个特性，使用 vavr 可以用 Java 写模式匹配的代码。Java 中的 switch...case 语句只能针对常量起作用，而使用模式匹配则可以对另一个函数的返回结果起作用。下面的例子分别给出了使用 if、switch...case、模式匹配三个语法实现同样功能的例子，可以看出，模式匹配有助于减少代码行数。

```java
@Test
public void whenIfWorksAsMatcher_thenCorrect() {
    int input = 3;
    String output;
    if (input == 0) {
        output = "zero";
    }
    if (input == 1) {
        output = "one";
    }
    if (input == 2) {
        output = "two";
    }
    if (input == 3) {
        output = "three";
    } else {
        output = "unknown";
    }

    assertEquals("three", output);
}

@Test
public void whenSwitchWorksAsMatcher_thenCorrect() {
    int input = 2;
    String output;
    switch (input) {
    case 0:
        output = "zero";
        break;
    case 1:
        output = "one";
        break;
    case 2:
        output = "two";
        break;
    case 3:
        output = "three";
        break;
    default:
        output = "unknown";
        break;
    }

    assertEquals("two", output);
}

@Test
public void whenMatchworks_thenCorrect() {
    int input = 2;
    String output = Match(input).of(
      Case($(1), "one"),
      Case($(2), "two"),
      Case($(3), "three"),
      Case($(), "?"));

    assertEquals("two", output);
}
```

原子模式代表了应该被评估以返回一个布尔值的条件。

- $()：一个通配符模式，类似于 switch 语句中的默认情况。它处理没有找到匹配的情况。
- $(value): 这是一种等值模式，将一个值与输入进行简单的等值比较。
- $(predicate): 这是一种条件模式，在这种模式下，一个谓词函数被应用到输入上，然后用结果的布尔值来做决定。

# Option

正如我们在上一节中所看到的，通配符模式$()匹配的默认情况是没有找到匹配的输入。然而，包含通配符模式的另一种选择是将匹配操作的返回值包装在一个 Option 实例中。

```java
@Test
public void whenMatchWorksWithOption_thenCorrect() {
    int i = 10;
    Option<String> s = Match(i)
      .option(Case($(0), "zero"));

    assertTrue(s.isEmpty());
    assertEquals("None", s.toString());
}
```

# Inbuilt Predicates

Vavr 有一些内置的谓词，使我们的代码更容易被人理解。因此，我们最初的例子可以用谓词进一步改进。

```java
@Test
public void whenMatchWorksWithPredicate_thenCorrect() {
    int i = 3;
    String s = Match(i).of(
      Case($(is(1)), "one"),
      Case($(is(2)), "two"),
      Case($(is(3)), "three"),
      Case($(), "?"));

    assertEquals("three", s);
}
```

Vavr 提供了比这更多的谓词。例如，我们可以让我们的条件检查输入的类。

```java
@Test
public void givenInput_whenMatchesClass_thenCorrect() {
    Object obj=5;
    String s = Match(obj).of(
      Case($(instanceOf(String.class)), "string matched"),
      Case($(), "not string"));

    assertEquals("not string", s);
}
```

或输入是否为空。

```java
@Test
public void givenInput_whenMatchesNull_thenCorrect() {
    Object obj=5;
    String s = Match(obj).of(
      Case($(isNull()), "no value"),
      Case($(isNotNull()), "value found"));

    assertEquals("value found", s);
}
```

我们可以使用 contains 风格来代替 equals 风格的匹配值。这样，我们可以用 isIn 谓词来检查一个输入是否存在于一个值的列表中。

```java
@Test
public void givenInput_whenContainsWorks_thenCorrect() {
    int i = 5;
    String s = Match(i).of(
      Case($(isIn(2, 4, 6, 8)), "Even Single Digit"),
      Case($(isIn(1, 3, 5, 7, 9)), "Odd Single Digit"),
      Case($(), "Out of range"));

    assertEquals("Odd Single Digit", s);
}
```

## 谓词组合

我们还可以用谓词来做更多的事情，比如将多个谓词组合成一个单一的匹配情况.为了只在输入通过所有给定的一组谓词时进行匹配，我们可以使用 allOf 谓词来 AND 谓词。一个实际的情况是，我们想检查一个数字是否包含在一个列表中，就像我们在前面的例子中做的那样。问题是，列表中也包含了空值。因此，我们想应用一个过滤器，除了拒绝列表中没有的数字外，还将拒绝空值。

```java
@Test
public void givenInput_whenMatchAllWorks_thenCorrect() {
    Integer i = null;
    String s = Match(i).of(
      Case($(allOf(isNotNull(),isIn(1,2,3,null))), "Number found"),
      Case($(), "Not found"));

    assertEquals("Not found", s);
}
```

为了在输入符合给定组的任何一个时进行匹配，我们可以使用 anyOf 谓词对谓词进行 OR。假设我们根据候选人的出生年份来筛选候选人，并且我们只想要 1990、1991 或 1992 年出生的候选人。如果没有找到这样的候选人，那么我们只能接受 1986 年出生的人，我们希望在我们的代码中也明确这一点。

```java
@Test
public void givenInput_whenMatchesAnyOfWorks_thenCorrect() {
    Integer year = 1990;
    String s = Match(year).of(
      Case($(anyOf(isIn(1990, 1991, 1992), is(1986))), "Age match"),
      Case($(), "No age match"));
    assertEquals("Age match", s);
}
```

最后，我们可以使用 noneOf 方法来确保没有提供的谓词匹配。为了证明这一点，我们可以否定上一个例子中的条件，这样我们就可以得到不在上述年龄组的候选人。

```java
@Test
public void givenInput_whenMatchesNoneOfWorks_thenCorrect() {
    Integer year = 1990;
    String s = Match(year).of(
      Case($(noneOf(isIn(1990, 1991, 1992), is(1986))), "Age match"),
      Case($(), "No age match"));

    assertEquals("No age match", s);
}
```

# 自定义谓词

Vavr 同样支持自定义谓词：

```java
@Test
public void whenMatchWorksWithCustomPredicate_thenCorrect() {
    int i = 3;
    String s = Match(i).of(
      Case($(n -> n == 1), "one"),
      Case($(n -> n == 2), "two"),
      Case($(n -> n == 3), "three"),
      Case($(), "?"));
    assertEquals("three", s);
}
```

如果我们需要更多的参数，我们也可以应用一个函数接口来代替谓词。包含的例子可以这样重写，尽管比较啰嗦，但它给了我们更多的能力来控制谓词的功能。

```java
@Test
public void givenInput_whenContainsWorks_thenCorrect2() {
    int i = 5;
    BiFunction<Integer, List<Integer>, Boolean> contains
      = (t, u) -> u.contains(t);

    String s = Match(i).of(
      Case($(o -> contains
        .apply(i, Arrays.asList(2, 4, 6, 8))), "Even Single Digit"),
      Case($(o -> contains
        .apply(i, Arrays.asList(1, 3, 5, 7, 9))), "Odd Single Digit"),
      Case($(), "Out of range"));

    assertEquals("Odd Single Digit", s);
}
```

在上面的例子中，我们创建了一个 Java 8 BiFunction，它只是检查两个参数之间的 isIn 关系。你也可以使用 Vavr 的 FunctionN 来实现。因此，如果内置的谓词不太符合你的要求，或者你想对整个评估进行控制，那么就使用自定义的谓词。

# Object Decomposition

对象分解是将一个 Java 对象分解为其组成部分的过程。例如，考虑将员工的生物数据与就业信息一起抽象出来的情况。

```java
public class Employee {

    private String name;
    private String id;

    //standard constructor, getters and setters
}
```

我们可以将 Employee 的记录分解成它的组成部分：name 和 id。

```java
@Test
public void givenObject_whenDecomposesJavaWay_thenCorrect() {
    Employee person = new Employee("Carl", "EMP01");

    String result = "not found";
    if (person != null && "Carl".equals(person.getName())) {
        String id = person.getId();
        result="Carl has employee id "+id;
    }

    assertEquals("Carl has employee id EMP01", result);
}
```

我们创建一个雇员对象，然后在应用过滤器之前首先检查它是否为空，以确保我们最终得到的是一个名字为 Carl 的雇员的记录。然后我们再去检索他的 id。Java 的方式是可行的，但它很啰嗦，而且容易出错。通过 Vavr 的模式匹配 API，我们可以忘记不必要的检查，只需关注重要的内容，从而使代码非常紧凑和可读。

```java
@Test
public void givenObject_whenDecomposesVavrWay_thenCorrect() {
    Employee person = new Employee("Carl", "EMP01");

    String result = Match(person).of(
      Case(Employee($("Carl"), $()),
        (name, id) -> "Carl has employee id "+id),
      Case($(),
        () -> "not found"));

    assertEquals("Carl has employee id EMP01", result);
}
```

这两种模式都是从匹配的对象中获取值，并将它们存储到 lambda 参数中。值模式$("Carl")只有当检索到的值与里面的值（即 carl）匹配时才能匹配。另一方面，通配符模式$()可以匹配其位置上的任何值，并将该值检索到 id lambda 参数中。为了使这种分解工作顺利进行，我们需要定义分解模式，也就是形式上所说的未应用模式。这意味着，我们必须教会模式匹配 API 如何分解我们的对象，从而为每个要分解的对象提供一个条目。

```java
@Unapply
static Tuple3<Integer, Integer, Integer> LocalDate(LocalDate date) {
    return Tuple.of(
      date.getYear(), date.getMonthValue(), date.getDayOfMonth());
}

@Test
public void givenObject_whenDecomposesVavrWay_thenCorrect2() {
    LocalDate date = LocalDate.of(2017, 2, 13);

    String result = Match(date).of(
      Case(LocalDate($(2016), $(3), $(13)),
        () -> "2016-02-13"),
      Case(LocalDate($(2016), $(), $()),
        (y, m, d) -> "month " + m + " in 2016"),
      Case(LocalDate($(), $(), $()),
        (y, m, d) -> "month " + m + " in " + y),
      Case($(),
        () -> "(catch all)")
    );

    assertEquals("month 2 in 2017",result);
}
```

# Side Effects

默认情况下，Match 的行为像一个表达式，意味着它返回一个结果。然而，我们可以通过使用在 lambda 中运行的辅助函数来强制它产生一个副作用。它接受一个方法引用或 lambda 表达式并返回 void。考虑一个场景，当一个输入是个位数的偶数时，我们想打印一些东西，当输入是个位数的奇数时，我们想打印另一个东西，当输入不是这些时，我们想抛出一个异常。

```java
public void displayEven() {
    System.out.println("Input is even");
}

public void displayOdd() {
    System.out.println("Input is odd");
}

@Test
public void whenMatchCreatesSideEffects_thenCorrect() {
    int i = 4;
    Match(i).of(
      Case($(isIn(2, 4, 6, 8)), o -> run(this::displayEven)),
      Case($(isIn(1, 3, 5, 7, 9)), o -> run(this::displayOdd)),
      Case($(), o -> run(() -> {
          throw new IllegalArgumentException(String.valueOf(i));
      })));
}
```
