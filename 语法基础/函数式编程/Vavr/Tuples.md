# Tuples

在 Java 中没有直接对应的元组数据结构。元组是函数式编程语言中的一个常见概念。元组是不可变的，可以以类型安全的方式容纳多个不同类型的对象。Vavr 将 Tuple 带到了 Java 8 中。Tuples 的类型有 Tuple1、Tuple2 到 Tuple8，取决于它们要取的元素数量。目前有一个上限是 8 个元素。我们像 `tuple._n` 那样访问元组的元素，其中 n 类似于数组中索引的概念。

```java
public void whenCreatesTuple_thenCorrect1() {
    Tuple2<String, Integer> java8 = Tuple.of("Java", 8);
    String element1 = java8._1;
    int element2 = java8._2();

    assertEquals("Java", element1);
    assertEquals(8, element2);
}
```

元组的作用在于存储一组固定的任何类型的对象，这些对象最好作为一个单元来处理，并且可以传递。一个比较明显的用例是在 Java 中从一个函数或方法中返回多个对象。

```java
@Test
public void whenCreatesTuple_thenCorrect2() {
    Tuple3<String, Integer, Double> java8 = Tuple.of("Java", 8, 1.8);
    String element1 = java8._1;
    int element2 = java8._2();
    double element3 = java8._3();

    assertEquals("Java", element1);
    assertEquals(8, element2);
    assertEquals(1.8, element3, 0.1);
}
```
