# Preconditions

Preconditions 类提供了一个静态方法列表，用于检查方法或构造函数是否以有效的参数值被调用。如果一个先决条件失败，就会抛出一个定制的异常。Preconditions 类中的每个静态方法都有三个变体：

- 没有参数，抛出的异常没有错误信息。
- 一个额外的 Object 参数作为错误信息。异常会被抛出一个错误信息。
- 一个额外的字符串参数，以及任意数量的额外对象参数作为一个占位符的错误信息。它的行为有点像 printf，但为了 GWT 的兼容性和效率，它只允许使用 %s 指示器

我们来看看如何使用 Preconditions 类。

# checkArgument

Preconditions 类的方法 checkArgument 确保传递给调用方法的参数的真实性。该方法接受一个布尔条件，并在条件为假时抛出一个 IllegalArgumentException。我们可以在不向 checkArgument 方法传递任何额外参数的情况下使用 checkArgument：

```java
@Test
public void whenCheckArgumentEvaluatesFalse_throwsException() {
    int age = -18;

    assertThatThrownBy(() -> Preconditions.checkArgument(age > 0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(null).hasNoCause();
}
```

我们可以通过传递错误信息从 checkArgument 方法中得到一个有意义的错误信息：

```java
@Test
public void givenErrorMsg_whenCheckArgEvalsFalse_throwsException() {
    int age = -18;
    String message = "Age can't be zero or less than zero.";

    assertThatThrownBy(() -> Preconditions.checkArgument(age > 0, message))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(message).hasNoCause();
}
```

我们可以通过传递一个错误信息，从 checkArgument 方法中得到一个有意义的错误信息以及动态数据：

```java
@Test
public void givenTemplateMsg_whenCheckArgEvalsFalse_throwsException() {
    int age = -18;
    String message = "Age should be positive number, you supplied %s.";

    assertThatThrownBy(
      () -> Preconditions.checkArgument(age > 0, message, age))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(message, age).hasNoCause();
}
```

# checkElementIndex

方法 checkElementIndex 检查一个索引是否是列表、字符串或指定大小的数组中的有效索引。元素索引的范围可以从 0 到大小不等。你不需要直接传递一个 list、字符串或数组，你只需要传递它的大小。如果索引不是有效的元素索引，这个方法就会抛出 IndexOutOfBoundsException，否则就会返回一个正在传递给方法的索引。

让我们看看如何使用这个方法，通过在检查 ElementIndex 方法抛出异常时传递一个错误信息来显示它的有意义的错误信息：

```java
@Test
public void givenArrayAndMsg_whenCheckElementEvalsFalse_throwsException() {
    int[] numbers = { 1, 2, 3, 4, 5 };
    String message = "Please check the bound of an array and retry";

    assertThatThrownBy(() ->
      Preconditions.checkElementIndex(6, numbers.length - 1, message))
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessageStartingWith(message).hasNoCause();
}
```

# checkNotNull

方法 checkNotNull 检查作为参数提供的值是否为空。它返回被检查的值。如果传递给这个方法的值是空的，那么就会抛出一个 NullPointerException。接下来，我们将展示如何使用这个方法，通过传递错误信息，从 checkNotNull 方法中获取有意义的错误信息：

```java
@Test
public void givenNullString_whenCheckNotNullWithMessage_throwsException () {
    String nullObject = null;
    String message = "Please check the Object supplied, its null!";

    assertThatThrownBy(() -> Preconditions.checkNotNull(nullObject, message))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(message).hasNoCause();
}
```

我们还可以通过向错误信息传递一个参数，从 checkNotNull 方法中得到一个基于动态数据的有意义的错误信息：

```java
@Test
public void whenCheckNotNullWithTemplateMessage_throwsException() {
    String nullObject = null;
    String message = "Please check the Object supplied, its %s!";

    assertThatThrownBy(
      () -> Preconditions.checkNotNull(nullObject, message,
        new Object[] { null }))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(message, nullObject).hasNoCause();
}
```

# checkPositionIndex

方法 checkPositionIndex 检查作为参数传递给本方法的索引是否是指定大小的列表、字符串或数组中的有效索引。一个位置索引的范围可以从 0（含）到大小（含）。你不需要直接传递列表、字符串或数组，你只需要传递它的大小。

如果传递的索引不在 0 和给定的大小之间，这个方法就会抛出一个 IndexOutOfBoundsException，否则就会返回索引值。让我们看看如何从 checkPositionIndex 方法中得到有意义的错误信息。

```java
@Test
public void givenArrayAndMsg_whenCheckPositionEvalsFalse_throwsException() {
    int[] numbers = { 1, 2, 3, 4, 5 };
    String message = "Please check the bound of an array and retry";

    assertThatThrownBy(
      () -> Preconditions.checkPositionIndex(6, numbers.length - 1, message))
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessageStartingWith(message).hasNoCause();
}
```

# checkState

方法 checkState 检查对象状态的有效性，并且不依赖于方法的参数。例如，一个 Iterator 可能会使用这个方法来检查在调用 remove 之前是否已经调用了 next。如果对象的状态（作为方法参数传递的布尔值）处于无效状态，该方法会抛出一个 IllegalStateException。

让我们看看如何使用这个方法，通过在 checkState 方法抛出异常时传递一个错误信息来显示它的有意义的错误信息。

```java
@Test
public void givenStatesAndMsg_whenCheckStateEvalsFalse_throwsException() {
    int[] validStates = { -1, 0, 1 };
    int givenState = 10;
    String message = "You have entered an invalid state";

    assertThatThrownBy(
      () -> Preconditions.checkState(
        Arrays.binarySearch(validStates, givenState) > 0, message))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith(message).hasNoCause();
}
```
