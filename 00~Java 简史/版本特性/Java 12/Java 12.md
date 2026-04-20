# Java 12

Java 12 早在 2019 年 3 月 19 日发布，它不是一个长久支持（LTS）版本。

# Switch 表达式 (JEP 325)

在 Java 12 中，对 Switch 表达式的写法进行了改进，虽然是一个语法糖的改进，也让 Switch 的代码编写变得更加优雅。先看一下在 Java 12 之前的 Switch 的写法。

```java
// 通过传入月份，输出月份所属的季节
public static void switchJava12Before(String day) {
    switch (day) {
        case "march":
        case "april":
        case "may":
            System.out.println("春天");
            break;
        case "june":
        case "july":
        case "august":
            System.out.println("夏天");
            break;
        case "september":
        case "october":
        case "november":
            System.out.println("秋天");
            break;
        case "december":
        case "january":
        case "february":
            System.out.println("冬天");
            break;
    }
}
```

上面的例子中，通过传入一个月份，输出这个月份对应的季节。简单的功能却写了大量代码，而且每个操作都需要一个 break 来防止 Case 穿透。由于 Switch 表达式在 Java 12 中并不是一个正式发布的功能，还处于预览测试阶段，所以想要使用 Java 12 去编译运行就需要打开功能预览参数，当然，如果你使用的是 Java 14 以及更高版本，就可以直接跳过这个部分了。

```sh
# 编译时
./bin/javac --enable-preview -source 12 ./Xxx.java
# 运行时
./bin/java --enable-preview Xxx
```

由于 Switch 存在的上述问题，所以在 Java 12 中对 Switch 进行了改进，让其可以使用 case L -> 的方式进行操作，那么在 Java 12 中可以怎么编写这段代码呢？

```java
public static void switchJava12(String day) {
    switch (day) {
        case "march", "april", "may"            -> System.out.println("春天");
        case "june", "july", "august"           -> System.out.println("夏天");
        case "september", "october", "november" -> System.out.println("秋天");
        case "december", "january", "february"  -> System.out.println("冬天");
    }
}
```

通过测试可以得到预期的输出结果。这还不够，在 Switch 的改进中，还支持了使用 Switch 的返回值进行赋值。像下面这样：

```java
String season = switch (day) {
    case "march", "april", "may"            -> "春天";
    case "june", "july", "august"           -> "春天";
    case "september", "october", "november" -> "春天";
    case "december", "january", "february"  -> "春天";
    default -> {
      //throw new RuntimeException("day error")
        System.out.println("day error");
        break "day error";
    }
};
System.out.println("当前季节是:" + season);
```

虽然编写更加简单了，其实这些只不过是语法糖式的更新，编译后和之前并没有太大区别。

# 文件对比 Files.mismatch

在 Java 中对于文件的操作已经在 Java 7 中进行了一次增强，这次 Java 12 又带来了文件对比功能。对比两个文件，如果内容一致，会返回 -1 ，如果内容不同，会返回不同的字节开始位置。

```java
// 创建两个文件
Path pathA = Files.createFile(Paths.get("a.txt"));
Path pathB = Files.createFile(Paths.get("b.txt"));

// 写入相同内容
Files.write(pathA,"abc".getBytes(), StandardOpenOption.WRITE);
Files.write(pathB,"abc".getBytes(), StandardOpenOption.WRITE);
long mismatch = Files.mismatch(pathA, pathB);
System.out.println(mismatch);

// 追加不同内容
Files.write(pathA,"123".getBytes(), StandardOpenOption.APPEND);
Files.write(pathB,"321".getBytes(), StandardOpenOption.APPEND);
mismatch = Files.mismatch(pathA, pathB);
System.out.println(mismatch);

// 删除创建的文件
pathA.toFile().deleteOnExit();
pathB.toFile().deleteOnExit();

// RESULT
// -1
// 3
```

# Compact Number

简化的数字格式可以直接转换数字显示格式，比如 1000 -> 1K，1000000 -> 1M 。

```java
System.out.println("Compact Formatting is:");
NumberFormat upvotes = NumberFormat.getCompactNumberInstance(new Locale("en", "US"), Style.SHORT);

System.out.println(upvotes.format(100));
System.out.println(upvotes.format(1000));
System.out.println(upvotes.format(10000));
System.out.println(upvotes.format(100000));
System.out.println(upvotes.format(1000000));

// 设置小数位数
upvotes.setMaximumFractionDigits(1);
System.out.println(upvotes.format(1234));
System.out.println(upvotes.format(123456));
System.out.println(upvotes.format(12345678));
```

可以得到输出如下：

```
100
1K
10K
100K
1M
1.2K
123.5K
12.3M
```

# JVM 相关更新

## Shenandoah 垃圾收集器

Java 12 增加了 Shenandoah 一个低停顿的垃圾收集器，它可以和 Java 应用程序中的执行线程同时进行，用来收集垃圾进行内容回收，这样就可以让停顿时间更少。

更多关于 Shenandoah 垃圾收集器的介绍可以查看文档：Shenandoah GC 介绍。

## ZGC 并发类卸载

ZGC 垃圾收集器现在支持类卸载，通过卸载不使用的类来释放这些类相关的数据结构，从而减少应用程序的总体占用空间。因为是并发执行，所以不会停止 Java 应用程序线程的执行，也因此对 GC 的暂停时间影响微乎其微。默认情况下启用此功能，但可以使用命令行选项禁用 -XX:-ClassUnloading。

## JVM 常量 API

在包 java.lang.invoke.constant 中定义了一系列的基于值的符号引用，可以用来描述各种可加载常量。可以更容易的对关键类文件和运行时构建的名义描述进行建模，特别是对那些从常量池中加载的常量，也让开发者可以更简单标准的处理可加载常量。

## 默认使用类数据共享（CDS）

这已经不是 JDK 第一次改进 CDS（Class Data Sharing） 功能了，CDS 可以让 JVM 在同一台机器或虚拟机上启动多个应用的速度速度大大增加。原理是在启动应用时共享一些类加载信息，这样启动新进程时就可以使用共享的数据。在 Java 12 之前此功能需要手动开启，Java 12 调整为默认开启。
