# try-with-resources

无论是使用文件 IO 流，还是网络 Socket 流，都免不了调用 close() 将流关闭。如果需要操作的流过多，就会导致混乱。一旦忘记将关闭方法放到 finally 中，很有可能出现流未被关闭，占用大量内存空间的问题。

try-catch-finally 方式如下：

```java
AC ac = null;
AC2 ac2 = null;

try {
    ac = new AC();
    ac2 = new AC2();
} catch (Exception e) {

} finally {
    ac.close();
    ac2.close();
}
```

try-with-resources 的语法如下：

```java
try (AC ac = new AC();
        AC2 ac2 = new AC2()) {
} catch (Exception e) {
}
```

可以很明显的看到，try-with-resources 会自动调用类中的 close() 方法，简化了流程，提高了代码的整洁度。

# AutoCloseable

AutoCloseable 是 Java 的内置接口，继承这个接口并且按要求新建 close() 方法，该类就能被 try-with-resources 语法所支持。

```java
public class AC implements AutoCloseable {
    @Override
    public void close() throws Exception {
        System.out.println("Program has been closed pretended.");
    }

    //默认静态方法，在被实例化时执行
    static {
        System.out.println("Program running.");
    }
}

public class AC2 implements AutoCloseable {
    @Override
    public void close() throws Exception {
        System.out.println("Program 2 has been closed pretended.");
    }

    static {
        System.out.println("Program 2 running.");
    }
}
```

AC2 和 AC 在实现上是相同的。我创建两个类的原因是想让大家知道 try-with-resources 可以支持同时进行多个类的关闭。再编写一个主方法，运行测试：

```java
public class Main {
    public static void main(String[] args) {
        try (AC ac = new AC();
             AC2 ac2 = new AC2()) {
            //这里假装执行了有用的代码
            Thread.sleep(2000);
        } catch (Exception e) {
        }
    }
}
```
