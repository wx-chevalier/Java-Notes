# 集合

Vavr 实现了一套新的 Java 集合框架来匹配函数式编程范式，Vavr 提供的集合都是不可变的。在 Java 中使用 Stream，需要显示得将集合转成 Stream 的步骤，而在 Vavr 中则免去了这样的步骤。Vavr 的 List 是不可变的链表，在该链表对象上的操作都会生成一个新的链表对象。使用 Java 8 的代码：

```java
Arrays.asList(1, 2, 3).stream().reduce((i, j) -> i + j);

IntStream.of(1, 2, 3).sum();
```

使用 Vavr 实现相同的功能，则更加直接：

```java
//io.vavr.collection.List
List.of(1, 2, 3).sum();
```

Vavr 的 Stream 是惰性链表，元素只有在必要的时候才会参与计算，因此大部分操作都可以在常量时间内完成。
