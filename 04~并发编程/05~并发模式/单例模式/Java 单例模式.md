# Java 单例

# 非延迟加载单例类

```java
public class Singleton {
　　private Singleton(){}
　　private static final Singleton instance = new Singleton();
　　public static Singleton getInstance() {
　　　　return instance;
　　}
}
```

# 简单的同步延迟加载

```java
public class Singleton {

　　private static Singleton instance = null;

　　public static synchronized Singleton getInstance() {
　　　　if (instance == null)
　　　　　　instance ＝ new Singleton();
　　　　return instance;
　　}
}
```

# 双重检查成例延迟加载

```java
public class Singleton {

　　private static volatile Singleton instance = null;

　　public static Singleton getInstance() {
　　　　if (instance == null) {
　　　　　　　　synchronized (Singleton.class) {
　　　　　　　　　　　　if (instance == null) {
　　　　　　　　　　　　　　　　instance ＝ new Singleton();
　　　　　　　　　　　　}
　　　　　　　　}
　　　　}
　　　　return instance;
　　}

}
```

# 静态内部类

最方便且线程安全的懒加载单例构造方式是使用静态内部类。这种方法结合了延迟初始化和线程安全，同时保持了较高的性能和简洁的代码。以下是这种方法的实现：

```java
public class Singleton {
    private Singleton() {}

    private static class SingletonHolder {
        private static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
```

这种实现方式的优点：

1. 延迟加载：单例实例只在第一次调用 `getInstance()` 方法时才会被创建。

2. 线程安全：利用了 JVM 的类加载机制来保证线程安全，不需要额外的同步。

3. 性能高：比双重检查锁定（DCL）更简单，且没有同步开销。

4. 简洁：代码简单易懂，易于维护。

5. 防止反射攻击：可以在私有构造函数中添加逻辑来防止反射创建多个实例。

使用方式：

```java
Singleton singleton = Singleton.getInstance();
```

这种方法的工作原理：

- 当 `Singleton` 类被加载时，静态内部类 `SingletonHolder` 并不会被初始化。
- 只有当调用 `getInstance()` 方法时，`SingletonHolder` 才会被加载和初始化。
- 类的初始化是由 JVM 在内部进行同步的，所以这种方式是线程安全的。

这种实现方式结合了懒加载、线程安全和简洁性，是一种非常推荐的单例模式实现方式。它避免了双重检查锁定的复杂性，同时保持了良好的性能和延迟初始化的特性。

需要注意的是，如果涉及到序列化，还需要额外实现 `readResolve()` 方法来保证反序列化时不会创建新的实例。此外，如果需要防止反射攻击，可以在构造函数中添加相应的检查逻辑。

# 类加载器延迟加载

```java
public class Singleton {
　　private static class Holder {
　　  static final Singleton instance = new Singleton();
　　}
　　public static Singleton getInstance() {
　　　　return Holder.instance;
　　}
}
```

# 枚举模式实现单例

Java 中最完善的单例模式写法通常被认为是使用枚举实现的单例模式。这种方法不仅能确保线程安全，还能防止反射攻击和序列化问题。以下是一个完善的枚举单例模式的实现：

```java
public enum EnumSingleton {
    INSTANCE;

    private Resource resource;

    EnumSingleton() {
        resource = new Resource();
    }

    public Resource getResource() {
        return resource;
    }

    public void doSomething() {
        // 单例的方法
    }

    private static class Resource {
        // 资源类的实现
    }
}
```

这种实现方式的优点：

1. 线程安全：枚举的实例创建是线程安全的，由 JVM 保证。

2. 防止反射攻击：Java 不允许通过反射创建枚举实例。

3. 序列化安全：枚举提供了自己的序列化机制，可以防止多实例的产生。

4. 实现简单：代码简洁，易于理解和维护。

5. 延迟加载：枚举实例在第一次使用时才会被初始化。

6. 性能高效：比使用双重检查锁定等方法更高效。

使用方式：

```java
EnumSingleton singleton = EnumSingleton.INSTANCE;
singleton.doSomething();
Resource resource = singleton.getResource();
```

这种实现方式结合了枚举的优势和单例模式的需求，被认为是实现 Java 单例模式的最佳实践。它解决了传统单例实现中的线程安全问题、反射攻击问题和序列化问题，同时保持了代码的简洁性和可读性。

虽然枚举实现的单例模式被广泛认为是 Java 中最完善的单例模式实现，但它也存在一些潜在的缺点或限制：

1. 灵活性受限：

   - 枚举常量在类加载时就被初始化，无法实现真正的懒加载。
   - 不能动态改变单例的实例化过程，如根据运行时条件选择不同的实现。

2. 继承限制：

   - 枚举类不能继承其他类（虽然可以实现接口）。
   - 如果单例需要继承某个基类，枚举实现就不适用。

3. 可能的性能影响：

   - 在某些 JVM 实现中，枚举可能比静态常量的性能稍差。
   - 如果单例包含大量数据或初始化开销大，可能会影响启动性能。

4. 序列化的特殊处理：

   - 虽然枚举提供了序列化保护，但如果需要自定义序列化过程，实现起来可能比较复杂。

5. 测试难度：

   - 枚举单例可能使单元测试变得困难，特别是需要模拟或替换单例实例时。

6. 不支持参数化构造：

   - 枚举构造函数不能有参数，这限制了单例的初始化选项。

7. 可能的过度使用：

   - 由于实现简单，可能导致开发者过度使用单例模式，而忽视其他更合适的设计模式。

8. 与依赖注入框架的兼容性：

   - 某些依赖注入框架可能不能很好地处理枚举单例。

9. 跨 ClassLoader 问题：

   - 在使用多个 ClassLoader 的环境中（如某些应用服务器），可能会出现多个实例。

10. 不适用于需要延迟初始化的场景：
    - 如果单例的初始化成本很高，而且不是每次都需要使用，枚举的即时初始化可能会造成资源浪费。

尽管存在这些潜在的缺点，枚举实现的单例模式在大多数情况下仍然是一个很好的选择。选择使用哪种单例实现方式应该基于具体的应用需求、性能要求和设计约束来决定。在某些特殊情况下，可能需要考虑其他实现方式或设计模式。

# Links

https://blog.csdn.net/u011595939/article/details/79972371#4%E9%9D%99%E6%80%81%E5%86%85%E9%83%A8%E7%B1%BB%E6%96%B9%E5%BC%8F%E6%8E%A8%E8%8D%90
