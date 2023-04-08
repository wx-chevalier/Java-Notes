# Vavr

Java 8 引入了函数式编程范式，思路是：将函数作为其他函数的参数传递，其实在 Java 8 之前，Java 也支持类似的功能，但是需要使用接口实现多态，或者使用匿名类实现。不管是接口还是匿名类，都有很多模板代码，因此 Java 8 引入了 Lambda 表达式，正式支持函数式编程。

比方说，我们要实现一个比较器来比较两个对象的大小，在 Java 8 之前，只能使用下面的代码：

```java
Compartor<Apple> byWeight = new Comparator<Apple>() {
  public int compare(Apple a1, Apple a2) {
    return a1.getWeight().compareTo(a2.getWeight());
  }
}
```

上面的代码使用 Lambda 表达式可以写成下面这样（IDEA 会提示你做代码的简化）

```java
Comparator<Apple> byWeight = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
```

受限于 Java 标准库的通用性要求和二进制文件大小，Java 标准库对函数式编程的 API 支持相对比较有限。函数的声明只提供了 Function 和 BiFunction 两种，流上所支持的操作的数量也较少。基于这些原因，你也许需要 vavr 来帮助你更好得使用 Java 8 进行函数式开发。如下图所示，vavr 提供了不可变的集合框架；更好的函数式编程特性；元组。vavr 是在尝试让 Java 拥有跟 Scala 类似的语法。

# 扩展支持

```xml
<dependency>
  <groupId>io.vavr</groupId>
  <artifactId>vavr-jackson</artifactId>
  <version>0.10.3</version>
</dependency>
```

首先注册 VavrModule 的实例：

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new VavrModule());
```

然后就可以进行序列化操作了：

```java
String json = mapper.writeValueAsString(List.of(1));
// = [1]
List<Integer> restored = mapper.readValue(json, new TypeReference<List<Integer>>() {});
// = List(1)
```
