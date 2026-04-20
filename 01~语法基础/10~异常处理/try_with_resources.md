# try-with-resources

Try-with-resources 是 Java7 出现的一个新的异常处理机制，它能够很容易地关闭在 try-catch 语句块中使用的资源。传统的关闭资源方式是利用 Try-Catch-Finally 管理资源（旧的代码风格） 即在 Java7 以前程序中使用的资源需要被明确地关闭。无论是使用文件 IO 流，还是网络 Socket 流，都免不了调用 close() 将流关闭。如果需要操作的流过多，就会导致混乱。一旦忘记将关闭方法放到 finally 中，很有可能出现流未被关闭，占用大量内存空间的问题。

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

## IO 案例

```java
private static void printFile() throws IOException {
    InputStream input = null;
    try{
        input = new FileInputStream("d:\\hello.txt");
        int data = input.read();
        while(data != -1){
            System.out.print((char) data);
            data = input.read();
        }
    } finally {
        if(input != null){
            input.close();
        }
    }
}
```

以上程序 try 语句块中有 3 处能抛出异常，finally 语句块中有一处会抛出异常。以上程序 try 语句块中有 3 处能抛出异常，finally 语句块中有一处会抛出异常。不论 try 语句块中是否有异常抛出，finally 语句块始终会被执行。这意味着，不论 try 语句块中发生什么，InputStream 都会被关闭，或者说都会试图被关闭。如果关闭失败，close()方法也可能会抛出异常。

假设 try 语句块抛出一个异常，然后 finally 语句块被执行。同样假设 finally 语句块也抛出了一个异常。那么哪个异常会根据调用栈往外传播？即使 try 语句块中抛出的异常与异常传播更相关，最终还是 finally 语句块中抛出的异常会根据调用栈向外传播。在 Java7 以后，对于上面的例子可以用 try-with-resource 结构这样写：

```java
private static void printFileJava7() throws IOException {
    try(FileInputStream input = new FileInputStream("d:\\hello.txt")) {
        int data = input.read();
        while(data != -1){
            System.out.print((char) data);
            data = input.read();
        }
    }
}
```

这就是 try-with-resource 结构的用法。FileInputStream 类型变量就在 try 关键字后面的括号中声明。而且一个 FileInputStream 类型被实例化并被赋给了这个变量。

当 try 语句块运行结束时，FileInputStream 会被自动关闭。这是因为 FileInputStream 实现了 java 中的 java.lang.AutoCloseable 接口。所有实现了这个接口的类都可以在 try-with-resources 结构中使用。

当 try-with-resources 结构中抛出一个异常，同时 FileInputStream 被关闭时（调用了其 close 方法）也抛出一个异常，try-with-resources 结构中抛出的异常会向外传播，而 FileInputStream 被关闭时抛出的异常被抑制了。这与文章开始处利用旧风格代码的例子（在 finally 语句块中关闭资源）相反。

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
