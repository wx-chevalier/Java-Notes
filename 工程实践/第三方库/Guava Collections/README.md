# Guava Collections

Guava 项目包含了谷歌在基于 Java 的项目中依赖的几个 Google 核心库：集合，缓存，原语支持，并发库，常见注释，字符串处理，I/O 等。

- 基本工具类：让使用 Java 语言更令人愉悦

  - 使用和避免 null：null 有语言歧义， 会产生令人费解的错误， 反正他总是让人不爽。很多 Guava 的工具类在遇到 null 时会直接拒绝或出错，而不是默默地接受他们。
  - 前提条件：更容易的对你的方法进行前提条件的测试。
  - 常见的对象方法： 简化了 Object 常用方法的实现， 如 hashCode() 和 toString()。
  - 排序： Guava 强大的 "fluent Comparator"比较器， 提供多关键字排序。
  - Throwable 类： 简化了异常检查和错误传播。

- 集合类：集合类库是 Guava 对 JDK 集合类的扩展， 这是 Guava 项目最完善和为人所知的部分。

  - Immutable collections（不变的集合）： 防御性编程， 不可修改的集合，并且提高了效率。
  - New collection types(新集合类型)：JDK collections 没有的一些集合类型，主要有：multisets，multimaps，tables， bidirectional maps 等等
  - Powerful collection utilities（强大的集合工具类）： java.util.Collections 中未包含的常用操作工具类
  - Extension utilities（扩展工具类）：给 Collection 对象添加一个装饰器还是实现迭代器? 我们可以更容易的实现这些。

- 缓存：本地缓存，可以很方便的操作缓存对象，并且支持各种缓存失效行为模式。

- Functional idioms（函数式）：简洁, Guava 实现了 Java 的函数式编程，可以显著简化代码。

- Concurrency（并发）：强大,简单的抽象,让我们更容易实现简单正确的并发性代码

  - ListenableFuture（可监听的 Future）: Futures,用于异步完成的回调。
  - Service: 控制事件的启动和关闭，为你管理复杂的状态逻辑。

- Strings：一个非常非常有用的字符串工具类: 提供 splitting，joining， padding 等操作。

- Primitives：扩展 JDK 中未提供的对原生类型（如 int、char 等）的操作， 包括某些类型的无符号的变量。

- Ranges：Guava 一个强大的 API，提供 Comparable 类型的范围处理， 包括连续和离散的情况。

- I/O：简化 I/O 操作, 特别是对 I/O 流和文件的操作, for Java 5 and 6。

- Hashing：提供比 Object.hashCode() 更复杂的 hash 方法, 提供 Bloom filters。

- EventBus：基于发布-订阅模式的组件通信，但是不需要明确地注册在委托对象中。

- Math：优化的 math 工具类，经过完整测试。

- Reflection：Guava 的 Java 反射机制工具类。

# Todos

- https://www.baeldung.com/category/guava/
