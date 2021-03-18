# 命令行应用

# Hello World

一个 Java 程序可以认为是一系列对象的集合，而这些对象通过调用彼此的方法来协同工作：

- 对象：对象是类的一个实例，有状态和行为。例如，一条狗是一个对象，它的状态有：颜色、名字、品种；行为有：摇尾巴、叫、吃等。
- 类：类是一个模板，它描述一类对象的行为和状态。
- 方法：方法就是行为，一个类可以有很多方法。逻辑运算、数据修改以及所有动作都是在方法中完成的。
- 实例变量：每个对象都有独特的实例变量，对象的状态由这些实例变量的值决定。

```java
public class HelloWorld {

  public static void main(String[] args) {
    System.out.println("Hello World");
  }
}
```

# 基本语法

一个完整的 Java，源程序应该包括下列部分：

- package 语句，该部分至多只有一句，必须放在源程序的第一句。由于 Java 编译器为每个类生成一个字节码文件，且文件名与类名相同因此同名的类有可能发生冲突。为了解决这一问题，Java 提供包来管理类名空间，包实 提供了一种命名机制和可见性限制机制。
- import 语句，该部分可以有若干 import 语句或者没有，必须放在所有的类定义之前。
- public classDefinition，公共类定义部分，至多只有一个公共类的定义，Java 语言规定该 Java 源程序的文件名必须与该公共类名完全一致。
- classDefinition，类定义部分，可以有 0 个或者多个类定义。
- interfaceDefinition，接口定义部分，可以有 0 个或者多个接口定义。

典型的例子如下所示：

```java
package javawork.helloworld;
/*把编译生成的所有．class文件放到包javawork.helloworld中*/
import java awt.*;
//告诉编译器本程序中用到系统的AWT包
import javawork.newcentury;
/*告诉编译器本程序中用到用户自定义的包javawork.newcentury*/
 public class HelloWorldApp{...｝
/*公共类HelloWorldApp的定义，名字与文件名相同*/
class TheFirstClass｛...｝;
//第一个普通类TheFirstClass的定义
interface TheFirstInterface{......}
/*定义一个接口TheFirstInterface*/
```

Java 的主方法入口：所有的 Java 程序由 `public static void main(String []args)` 方法开始执行。值得注意的是，Java 是大小写敏感的，这就意味着标识符 Hello 与 hello 是不同的。对于所有的类来说，类名的首字母应该大写。如果类名由若干单词组成，那么每个单词的首字母应该大写，例如 MyFirstJavaClass。所有的方法名都应该以小写字母开头。如果方法名含有若干单词，则后面的每个单词首字母大写。源文件名必须和类名相同。当保存文件的时候，你应该使用类名作为文件名保存（切记 Java 是大小写敏感的），文件名的后缀为 .java。（如果文件名和类名不相同则会导致编译错误）。

## 源文件声明规则

当在一个源文件中定义多个类，并且还有 import 语句和 package 语句时，要特别注意这些规则。

- 一个源文件中只能有一个 public 类
- 一个源文件可以有多个非 public 类
- 源文件的名称应该和 public 类的类名保持一致。例如：源文件中 public 类的类名是 Employee，那么源文件应该命名为 Employee.java。
- 如果一个类定义在某个包中，那么 package 语句应该在源文件的首行。
- 如果源文件包含 import 语句，那么应该放在 package 语句和类定义之间。如果没有 package 语句，那么 import 语句应该在源文件中最前面。
- import 语句和 package 语句对源文件中定义的所有类都有效。在同一源文件中，不能给不同的类不同的包声明。

类有若干种访问级别，并且类也分不同的类型：抽象类和 final 类等。除了上面提到的几种类型，Java 还有一些特殊的类，如：内部类、匿名类。这里还值得讨论的是，为什么 Java 文件中只允许包含一个 public 类呢？因为 Java 程序是从一个 public 类的 main 函数开始执行的，(其实是 main 线程)，就像 C 程序 是从 main() 函数开始执行一样。只能有一个 public 类是为了给类装载器提供方便。一个 public 类只能定义在以它的类名为文件名的文件中。

每个编译单元(文件)都只有一个 public 类。因为每个编译单元都只能有一个公共接口，用 public 类来表现。该接口可以按照要求包含众多的支持包访问权限的类。如果有一个以上的 public 类，编译器就会报错。并且 public 类的名称必须与文件名相同(严格区分大小写)。当然一个编译单元内也可以没有 public 类。

## 标识符

Java 所有的组成部分都需要名字。类名、变量名以及方法名都被称为标识符。关于 Java 标识符，有以下几点需要注意：

- 所有的标识符都应该以字母（A-Z 或者 a-z）,美元符（`$`）、或者下划线（`_`）开始
- 首字符之后可以是字母（A-Z 或者 a-z）,美元符（`$`）、下划线（`_`）或数字的任何字符组合
- 关键字不能用作标识符
- 标识符是大小写敏感的
- 合法标识符举例：`age、\$salary、_value、__1_value`
- 非法标识符举例：123abc、-salary

## 修饰符

像其他语言一样，Java 可以使用修饰符来修饰类中方法和属性。主要有两类修饰符：

- 访问控制修饰符: default, public, protected, private
- 非访问控制修饰符: final, abstract, static, synchronized

修饰符用来定义类、方法或者变量，通常放在语句的最前端。我们通过下面的例子来说明：

```java
public class ClassName {
   // ...
}
private boolean myFlag;
static final double weeks = 9.5;
protected static final int BOXWIDTH = 42;
public static void main(String[] arguments) {
   // 方法体
}
```

## 关键字

| 类别                 | 关键字                         | 说明                 |
| -------------------- | ------------------------------ | -------------------- |
| 访问控制             | private                        | 私有的               |
| protected            | 受保护的                       |                      |
| public               | 公共的                         |                      |
| 类、方法和变量修饰符 | abstract                       | 声明抽象             |
| class                | 类                             |                      |
| extends              | 扩充,继承                      |                      |
| final                | 最终值,不可改变的              |                      |
| implements           | 实现（接口）                   |                      |
| interface            | 接口                           |                      |
| native               | 本地，原生方法（非 Java 实现） |                      |
| new                  | 新,创建                        |                      |
| static               | 静态                           |                      |
| strictfp             | 严格,精准                      |                      |
| synchronized         | 线程,同步                      |                      |
| transient            | 短暂                           |                      |
| volatile             | 易失                           |                      |
| 程序控制语句         | break                          | 跳出循环             |
| case                 | 定义一个值以供 switch 选择     |                      |
| continue             | 继续                           |                      |
| default              | 默认                           |                      |
| do                   | 运行                           |                      |
| else                 | 否则                           |                      |
| for                  | 循环                           |                      |
| if                   | 如果                           |                      |
| instanceof           | 实例                           |                      |
| return               | 返回                           |                      |
| switch               | 根据值选择执行                 |                      |
| while                | 循环                           |                      |
| 错误处理             | assert                         | 断言表达式是否为真   |
| catch                | 捕捉异常                       |                      |
| finally              | 有没有异常都执行               |                      |
| throw                | 抛出一个异常对象               |                      |
| throws               | 声明一个异常可能被抛出         |                      |
| try                  | 捕获异常                       |                      |
| 包相关               | import                         | 引入                 |
| package              | 包                             |                      |
| 基本类型             | boolean                        | 布尔型               |
| byte                 | 字节型                         |                      |
| char                 | 字符型                         |                      |
| double               | 双精度浮点                     |                      |
| float                | 单精度浮点                     |                      |
| int                  | 整型                           |                      |
| long                 | 长整型                         |                      |
| short                | 短整型                         |                      |
| 变量引用             | super                          | 父类,超类            |
| this                 | 本类                           |                      |
| void                 | 无返回值                       |                      |
| 保留关键字           | goto                           | 是关键字，但不能使用 |
| const                | 是关键字，但不能使用           |                      |
| null                 | 空                             |                      |

我们可以通过以下的案例来大概看下核心的语法：

```java
// To starts, run jshell --enable-preview which is a program able to interpret Java syntax
// then cut and paste the following lines to see how it works
// To exit jshell type /exit

// # A record is a user defined type
// here Light is defined as containing two components: a color (typed as a String) and
// an intensity (typed as a 64 bits floating number double)
record Light(String color, double intensity) {}

// In Java, there is strong division between primitive types like double that are written in lower case and
// objects like String or Light that have a name that starts with an uppercase letter.

// A primitive type is stored as value while an object is stored as
// a reference (the address of the object in memory)
// In Java, `var` create a new variable
var maxIntensity = 1.0;   // it's a value
var colorName = "black";  // it's a reference to String somewhere in memory

// you can also indicate the type instead of `var`
// if you are using var, you are asking the compiler to find the type for you
String colorName = "black";


// System.out.println()
// To print a value in Java we have a weird incantation `System.out.println()` that we will detail later
System.out.println(maxIntensity);

// Primitive types and objects can be printed using the same incantation
// We will see later its exact meaning
System.out.println(colorName);

// ## Concatenation with +
// If we want to print a text followed by a value, we use the operator `+`
System.out.println("the value of colorName is " + colorName);

// To create an object in memory, we use the operator `new` followed by the value of each record components
// the following instruction create a Light with "blue" as color and 1.0 as intensity
var blueLight = new Light("blue", 1.0);

// To interact with an object in Java, we use methods, that are functions attached to an object.
// to call a method, we use the operator `.` followed by the name of the method and its arguments
// A record automatically declares methods to access its components so Light declares two methods
// color() and intensity()

// By example to get the intensity of the object blueLight
var blueLightIntensity = blueLight.intensity();
System.out.println(blueLightIntensity);

// ## toString()
// By default a record knows how to transform itself into a String
// in Java, the method to transform an object to a String is named toString()
System.out.println(blueLight.toString());

// In fact, println() calls toString() if the argument is an object
// so when using println(), calling explicitly toString() is not necessary
System.out.println(blueLight);

// Let's create another Light
var redLight = new Light("red", 1.0);

// ## equals()
// In Java, you can ask if two objects are equals, using the method equals(Object)
// the return value is a boolean (a primitive type that is either true or false)
System.out.println(blueLight.equals(redLight));

// Let's create another red light
var anotherRedLight = new Light("red", 1.0);
System.out.println(redLight.equals(anotherRedLight));

// ## hashCode()
// You can also ask have an integer summary (a hash) of any object
// This is used to speed up data structures (hash table)
// Two objects that are equals() must have the same hashCode()
System.out.println(redLight.hashCode());
System.out.println(anotherRedLight.hashCode());


// # Summary
// A `record` has components that are the parameters used to create an object
// To create an object we use the operator `new` followed by the arguments of the
// record components in the same order
// To interact with an object, we are using methods that are functions that you
// call on an object using the operator `.`
// A Record defines methods to access the value of a component, and also
// `toString()` to get the textual representation of an object and
// `equals()` to test if two objects are equals.
```

# 命令行应用

## 选择执行类

通过反射的语法，我们能够动态地选择执行类：

```java
public class MainApplication {
  /**
   * Calls main method of the class provided by the user.
   * @param args  Accepts the classname as the first parameter. The rest are passed as argument as args.
   */
  public static void main(String[] args) throws Exception {
    String[] arguments;
    if (args.length < 1) {
      throw new IllegalArgumentException("Requires at least one argument - name of the main class");
    } else {
      arguments = Arrays.copyOfRange(args, 1, args.length);
      Class mainClass = Class.forName(args[0]);
      Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
      Object[] methodArgs = new Object[1];
      methodArgs[0] = arguments;
      mainMethod.invoke(mainClass, methodArgs);
    }
  }
}
```

然后在运行 jar 的时候动态指定主类名即可：

```sh
$ java -jar target/target.jar io.dapr.examples.actors.DemoActorClient
```
