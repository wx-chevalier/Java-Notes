# 与 Java 共事

> There comes a day in every Clojurist’s life when she must venture forth from the sanctuary of pure functions and immutable data structures into the wild, barbaric Land of Java. This treacherous journey is necessary because Clojure is hosted on the Java Virtual Machine (JVM), which grants it three fundamental characteristics. First, you run Clojure applications the same way you run Java applications. Second, you need to use Java objects for core functionality like reading files and working with dates. Third, Java has a vast ecosystem of useful libraries, and you’ll need to know a bit about Java to use them.

每个 Clojure 使用者都会有这一天：他必须从纯函数，不可变数据结构的避难所进入野生原始的 Java 大陆。这个危险的旅程是有必要的，因为 Clojure 的宿主是 java 虚拟机(JVM),这使 Clojure 具有三个基本特征: 首先，运行 Clojure 应用程序的方法与运行 Java 的一样。第二，你需要使用 Java 对象完成文件读取，日期操作这样的核心功能。第三，Java 生态系统里有大量有用的库，你需要了解一点 Java 才能使用它们。

> In this way, Clojure is a bit like a utopian community plunked down in the middle of a dystopian country. Obviously you’d prefer to interact with other utopians, but every once in a while you need to talk to the locals in order to get things done.

这种运行方式使 Clojure 有点像一个乌托邦社区，突然掉在一个反乌托邦社区里。你更愿意跟乌托邦居民交互，但为了解决问题，偶尔也需要跟本地人交谈。

> This chapter is like a cross between a phrase book and cultural introduction for the Land of Java. You’ll learn what the JVM is, how it runs programs, and how to compile programs for it. This chapter will also give you a brief tour of frequently used Java classes and methods, and explain how to interact with them using Clojure. You’ll learn how to think about and understand Java so you can incorporate any Java library into your Clojure programs.

这章就像 Java 大陆的常用外语手册和文化介绍。你将学习什么是 JVM，它如何运行程序，如何编译 JVM 上运行的程序。还会简单介绍常用的 Java 类和方法，并解释如何用 Clojure 与其交互。你将学习如何思考并理解 Java，以便在 Clojure 程序里使用任何 Java 库。

> To run the examples in this chapter, you’ll need to have the Java Development Kit ( JDK) version 1.6 or later installed on your computer. You can check by running `javac -version` at your terminal. You should see something like `java 1.8.0_40`; if you don’t, visit http://www.oracle.com/ to download the latest JDK.

为了运行这章的例子，你需要安装 1.6 或更高版本的 Java 开发包(JDK)。在终端上运行`javac -version`可以查看。你应该看到类似`java 1.8.0_40`,如果没有，去 http://www.oracle.com/ 下载并安装最新的 JDK。

> The JVM

## JVM

> Developers use the term JVM to refer to a few different things. You’ll hear them say, “Clojure runs on the JVM,” and you’ll also hear, “Clojure programs run in a JVM.” In the first case, JVM refers to an abstraction—the general model of the Java Virtual Machine. In the second, it refers to a process—an instance of a running program. We’ll focus on the JVM model, but I’ll point out when we’re talking about running JVM processes.

开发者用 JVM 指不同的事情。你可能听说，“Clojure 运行在 JVM 上”，也可能听说，”Clojure 程序运行在 JVM 里“。第一种情况，JVM 指的是一种抽象，即 java 虚拟机这个通用模型。第二种情况，JVM 指的是一个进程，即一个正在运行的程序的实例。我们将集中于 JVM 模型，当谈到运行的 JVM 进程时，我会指明。

> To understand the JVM, let’s step back and review how plain ol’ computers work. Deep in the cockles of a computer’s heart is its CPU, and the CPU’s job is to execute operations like _add_ and _unsigned multiply_. You’ve probably heard about programmers encoding these instructions on punch cards, in lightbulbs, in the sacred cracks of a tortoise shell, or _whatever_, but nowadays these operations are represented in assembly language by mnemonics like ADD and MUL. The CPU architecture (x86, ARMv7, and what have you) determines what operations are available as part of the architecture’s instruction set.

为理解 JVM，我们退后一步，看看计算机是如何工作的。计算机的心脏是 CPU，CPU 的工作是执行*add*和*unsigned multiply*这样的指令。你可能听说过，程序员把这些指令按照某种形式编码在穿孔卡片上，现今这些指令用汇编语言的 ADD 和 MUL 这样的助记符表示，CPU 结构(x86, ARMv7 等等)决定了这种结构的 CPU 的操作指令集。

> Because it’s no fun to program in assembly language, people have invented higher-level languages like C and C++, which are compiled into instructions that a CPU will understand. Broadly speaking, the process is:

由于用汇编语言编程不好玩，人们发明了 C 和 C++这样的高级语言，高级语言被编译成 CPU 理解的指令。概括起来，过程如下:

> 1. The compiler reads source code.
> 2. The compiler outputs a file containing machine instructions.
> 3. The CPU executes those instructions.

1. 编译器读取源代码。
2. 编译器输出一个含有机器指令的文件。
3. CPU 执行这些指令。

图 12-1

[![compile](https://morrxy.github.io/2016/08/20/brave-clojure-java/compile.png)](https://morrxy.github.io/2016/08/20/brave-clojure-java/compile.png)

> Notice in Figure 12-1 that, ultimately, you have to translate programs into instructions that a CPU will understand, and the CPU doesn’t care which programming language you use to produce those instructions.

注意，图 12-1 里，程序最终被转换成 CPU 能理解的指令，CPU 不关心用哪种编程语言产生这些指令。

> The JVM is analogous to a computer in that it also needs to translate code into low-level instructions, called Java bytecode. However, as a virtual machine, this translation is implemented as software rather than hardware. A running JVM executes bytecode by translating it on the fly into machine code that its host will understand, a process called _just-in-time compilation_.

JVM 与计算机类似之处是，JVM 也需要把代码转换成被称为 Java 字节码的低级指令。但作为一个虚拟机器，这是个软件转换而不是硬件转换。运行中的 JVM 靠快速把字节码转换成宿主能理解的机器码执行字节码，这个过程叫*即时编译*。

> For a program to run on the JVM, it must get compiled to Java bytecode. Usually, when you compile programs, the resulting bytecode is saved in a _.class_ file. Then you’ll package these files in _Java archive files_ (JAR files). And just like how a CPU doesn’t care which programming language you use to generate machine instructions, the JVM doesn’t care how you create bytecode. It doesn’t care if you use Scala, JRuby, Clojure, or even Java to create Java bytecode. Generally speaking, the process looks like that shown in Figure 12-2.

为了让一个程序在 JVM 上运行，必须把它编译成 Java 字节码。编译结果的字节码通常保存在一个*.class*文件里。然后你把这些文件打包进*Java 档案文件*(JAR 文件)。就像 CPU 不关心用什么语言生成机器码一样，JVM 也不关心你怎么创建字节码。它不关心你创建字节码用的是 Scala, JRuby, Cojure 还是 Java。总的来说，这个过程如图 12-2 所示：

图 12-2

[![jvm-compile](https://morrxy.github.io/2016/08/20/brave-clojure-java/jvm-compile.png)](https://morrxy.github.io/2016/08/20/brave-clojure-java/jvm-compile.png)

> 1. The Java compiler reads source code.
> 2. The compiler outputs bytecode, often to a JAR file.
> 3. JVM executes the bytecode.
> 4. The JVM sends machine instructions to the CPU.

1. Java 编译器读取源代码。
2. 编译器输出字节码，经常输出到一个 JAR 文件里。
3. JVM 执行字节码。
4. JVM 把机器指令发送给 CPU。

> When someone says that Clojure runs on the JVM, one of the things they mean is that Clojure programs get compiled to Java bytecode and JVM processes execute them. From an operations perspective, this means you treat Clojure programs the same as Java programs. You compile them to JAR files and run them using the `java` command. If a client needs a program that runs on the JVM, you could secretly write it in Clojure instead of Java and they would be none the wiser. From the outside, you can’t tell the difference between a Java and a Clojure program any more than you can tell the difference between a C and a C++ program. Clojure allows you to be productive and sneaky.

当某人说 Clojure 在 JVM 上运行，其中一个意思是 Clojure 程序被编译成 Java 字节码，并且 JVM 进程执行被编译的字节码。从运算的角度看，这意味着你对待 Clojure 程序同对待 Java 程序一样。把它们编译成 JAR 文件，然后用`java`命令运行它们。如果一个客户需要一个运行在 JVM 上的程序，你可以偷偷用 Clojure 而不是 Java 写，而它们都不会察觉。从外面看，无法分辨 Clojure 程序和 Java 程序。Clojure 让使你富有生产力。

> Writing, Compiling, and Running a Java Program

## 写，编译，运行 Java 程序

> Let’s look at how a real Java program works. In this section, you’ll learn about the object-oriented paradigm that Java uses. Then, you’ll build a simple pirate phrase book using Java. This will help you feel more comfortable with the JVM, it will prepare you for the upcoming section on Java interop (writing Clojure code that uses Java classes, objects, and methods directly), and it’ll come in handy should a scallywag ever attempt to scuttle your booty on the high seas. To tie all the information together, you’ll take a peek at some of Clojure’s Java code at the end of the chapter.

我们来看一下真正的 Java 程序如何工作。这节你会学习 Java 使用的面向对象范式。然后会用 Java 建立一个简单程序。这会使你对后面的 Java 互操作(直接使用 Java 类，对象和方法的 Clojure 代码)做好准备。为使所有信息结合在一起，这章最后会看一些 Clojure 的 Java 代码。

> Object-Oriented Programming in the World’s Tiniest Nutshell

### 世界上最概括的面相对象编程

> Java is an object-oriented language, so you need to understand how object-oriented programming (OOP) works if you want to understand what’s happening when you use Java libraries or write Java interop code in your Clojure programming. You’ll also find object-oriented terminology in Clojure documentation, so it’s important to learn these concepts. If you’re OOP savvy, feel free to skip this section. For those who need the two-minute lowdown, here it is: the central players in OOP are _classes_, _objects_, and _methods_.

Java 是面相对象的语言，如果你想理解使用 Java 库时候或用 Clojure 与 Java 互操作时候发生了什么，你需要理解面相对象编程使如何工作的。你将在 Clojure 文档里看到面相对象术语，所以学习这些概念很重要。想知道真想吗？就是：OOP 的主要演员是 _类_，_对象_ ，_方法_ 。

> I think of objects as really, really, ridiculously dumb androids. They’re the kind of android that would never inspire philosophical debate about the ethics of forcing sentient creatures into perpetual servitude. These androids only do two things: they respond to commands and they maintain data. In my imagination they do this by writing stuff down on little Hello Kitty clipboards.

我把对象看作非常傻的机器人。这些机器人只干两件事: 对命令做出响应和保持数据。

> Imagine a factory that makes these androids. Both the set of commands the android understands and the set of data it maintains are determined by the factory that makes the android. In OOP terms, the factories correspond to classes, the androids correspond to objects, and the commands correspond to methods. For example, you might have a `ScaryClown` factory (class) that produces androids (objects) that respond to the command (method) `makeBalloonArt`. The android keeps track of the number of balloons it has, and then updates that number whenever the number of balloons changes. It can report that number with `balloonCount` and receive any number of balloons with `receiveBalloons`. Here’s how you might interact with a Java object representing Belly Rubs the Clown:

想象一个生产这些机器人的工厂。机器人理解的命令集和保持的数据都由工厂决定。OOP 术语里，工厂对应类，机器人对应对象，命令对应方法。例如，有一个`ScaryClown`工厂(类)生成机器人(对象)，这种机器人能对命令(方法)`makeBalloonArt`做出响应。这种机器人记录着它们拥有的气球的个数，并且当个数改变时更新那个数字。它能用`balloonCount`报告那个数字，能用`receiveBalloons`接受任意个气球。下面是如何与 Java 对象交互:

```
ScaryClown bellyRubsTheClown = new ScaryClown();
bellyRubsTheClown.balloonCount();
// => 0

bellyRubsTheClown.receiveBalloons(2);
bellyRubsTheClown.balloonCount();
// => 2

bellyRubsTheClown.makeBalloonArt();
// => "Belly Rubs makes a balloon shaped like a clown, because Belly Rubs
// => is trying to scare you and nothing is scarier than clowns."
```

> This example shows you how to create a new object, `bellyRubsTheClown`, using the `ScaryClown` class. It also shows you how to call methods (such as `balloonCount`, `receiveBalloons`, and `makeBalloonArt`) on the object, presumably so you can terrify children.

这个例子演示了如何用`ScaryClown`类创建新对象`bellyRubsTheClown`。也演示了如何调用对象的方法(比如`balloonCount`,`receiveBalloons`和`makeBalloonArt`)。

> One final aspect of OOP that you should know, or at least how it’s implemented in Java, is that you can also send commands to the factory. In OOP terms, you would say that classes also have methods. For example, the built-in class `Math` has many class methods, including `Math.abs`, which returns the absolute value of a number:

你需要了解的 OOP 的最后一点是：你也可以发命令给工厂。OOP 术语里叫类也有方法。例如，內建的`Math`类有很多类方法，包含`Math.abs`,返回一个数字的绝对值:

```
Math.abs(-50)
// => 50
```

> I hope those clowns weren’t too traumatizing for you. Now let’s put your OOP knowledge to work!

现在来实际使用这些知识！

> Ahoy, World

### 喂, 世界

> Go ahead and create a new directory called _phrasebook_. In that directory, create a file called _PiratePhrases.java_, and write the following:

创建一个*phrasebook*目录。在目录里创建一个*PiratePhrases.java*文件，代码如下:

```
public class PiratePhrases
{
    public static void main(String[] args)
    {
        System.out.println("Shiver me timbers!!!");
    }
}
```

> This very simple program will print the phrase “Shiver me timbers!!!” (which is how pirates say “Hello, world!”) to your terminal when you run it. It consists of a class, `PiratePhrases`, and a static method belonging to that class, `main`. Static methods are essentially class methods.

这个程序运行时会往终端打印”“Shiver me timbers!!!”(海盗版的”Hello, world!”)。代码里有一个类`PiratePhrases`,和一个属于这个类的静态方法`main`。静态方法本质上就是类方法。

> In your terminal, compile the `PiratePhrases` source code with the command `javac PiratePhrases.java`. If you typed everything correctly and you’re pure of heart, you should see a file named _PiratePhrases.class_:

在命令行用`javac PiratePhrases.java`编译`PiratePhrases`。输入正确的话，会出现一个文件*PiratePhrases.class*:

```
$ ls
PiratePhrases.class PiratePhrases.java
```

> You’ve just compiled your first Java program, me matey! Now run it with `java PiratePhrases`. You should see this:

你编译了第一个 Java 程序！用`java PiratePhrases`运行。会看到：

```
Shiver me timbers!!!
```

> What’s happening here is you used the Java compiler, `javac`, to create a Java class file, _PiratePhrases.class_. This file is packed with oodles of Java bytecode (well, for a program this size, maybe only one oodle).

这里发生的是，你用 Java 编译器`javac`创建了一个 Java 类文件*PiratePhrases.class*。这个文件里是 Java 字节码。

> When you ran _java PiratePhrases_, the JVM first looked at your classpath for a class named `PiratePhrases`. The classpath is the list of filesystem paths that the JVM searches to find a file that defines a class. By default, the classpath includes the directory you’re in when you run java. Try running `java -classpath /tmp PiratePhrases` and you’ll get an error, even though _PiratePhrases.class_ is right there in your current directory.

当运行*java PiratePhrases*时，JVM 先在类路径里查找名字是`PiratePhrases`的类。类路径是文件系统路径列表，JVM 用它搜索定义类的文件。默认情况下，类路径包含你运行 java 时的目录。尝试一下，运行`java -classpath /tmp PiratePhrases`，会报错，即使*PiratePhrases.class*在当前目录里。

> Note You can have multiple paths on your classpath by separating them with colons if you’re on a Mac or running Linux, or semicolons if you’re using Windows. For example, the classpath `/tmp:/var/maven:.` includes the `/tmp`, `/var/maven`, and `.` directories.

注意，类路径上可以有多个路径，用冒号(Mac 或 Linux)或分号(Windows)隔开。例如，类路径`/tmp:/var/maven:.`包含三个目录，`/tmp`, `/var/maven`, 和 `.`目录。

> In Java, you’re allowed only one public class per file, and the filename must match the class name. This is how `java` knows to try looking in _PiratePhrases.class_ for the `PiratePhrases` class’s bytecode. After `java` found the bytecode for the `PiratePhrases` class, it executed that class’s `main` method. Java’s similar to C in that whenever you say “run something, and use this class as your entry point,” it will always run that class’s `main` method; therefore, that method must be `public`, as you can see in the `PiratePhrases`’s source code.

Java 只允许每个文件一个公开类，而且文件名必须与类名一样。这样`java`才能找到`PiratePhrases`类的字节码`PiratePhrases.class`。`java`找到`PiratePhrases`类的字节码以后，执行类的`main`方法。Java 与 C 类似，无论何时，运行东西，并用类作为入口时，都会运行这个类的`main`方法；因此这个方法必须是`public`的，如上面源码所示。

> In the next section you’ll learn how to handle program code that spans multiple files, and how to use Java libraries.

下节会学习如何处理多个文件的程序代码和如何使用 Java 库。

> Packages and Imports

## 包与引入

> To see how to work with multi-file programs and Java libraries, we’ll compile and run a program. This section has direct implications for Clojure because you’ll use the same ideas and terminology to interact with Java libraries.

为了解多文件程序和 Java 库如何工作，我们将编译并运行一个程序。这节与 Clojure 直接相关，因为你将使用同样的思想和术语与 Java 库交互。

> Let’s start with a couple of definitions:

先来两个定义：

> - **package** Similar to Clojure’s namespaces, packages provide code organization. Packages contain classes, and package names correspond to filesystem directories. If a file has the line `package com.shapemaster` in it, the directory _com/shapemaster_ must exist somewhere on your classpath. Within that directory will be files defining classes.
> - **import** Java allows you to import classes, which basically means that you can refer to them without using their namespace prefix. So if you have a class in _com.shapemaster_ named `Square`, you could write `import com.shapemaster.Square`; or `import com.shapemaster.*;` at the top of a `.java` file to use `Square` in your code instead of `com.shapemaster.Square`.

- **包(package)** 与 Clojure 的命名空间类似，用于组织代码。包包含类，包名与文件系统目录对应。如果一个文件里有一行`package com.shapemaster`,类路径里必然有目录`com/shapemaster`。那个目录里是类文件。
- **引入(import)** Java 允许你引入类，意思是你可以引用它们而无需使用它们的命名空间前缀。如果*com.shapemaster*有个类叫`Square`,你可以在文件最上面写上`import com.shapemaster.Square` 或 `import com.shapemaster.*`，然后就可以直接使用`Square`，不用再写`com.shapemaster.Square`。

> Let’s try using `package` and `import`. For this example, you’ll create a package called `pirate_phrases` that has two classes, `Greetings` and `Farewells`. To start, navigate to your _phrasebook_ and within that directory create another directory, _pirate_phrases_. It’s necessary to create _pirate_phrases_ because Java package names correspond to filesystem directories. Then, create _Greetings.java_ within the _pirate_phrases_ directory:

我们尝试一下`package`和`import`。我们将创建一个叫`pirate_phrases`包，含有两个类，`Greetings`和`Farewells`。找到*phrasebook*目录，在里面创建一个目录*pirate_phrases*。因为 Java 包名与文件系统目录对应，所以目录名是这个。然后在这个目录里创建*Greetings.java*:

```
➊ package pirate_phrases;

public class Greetings
{
    public static void hello()
    {
        System.out.println("Shiver me timbers!!!");
    }
}
```

> At ➊, `package pirate_phrases;` indicates that this class will be part of the `pirate_phrases` package. Now create `Farewells.java` within the `pirate_phrases` directory:

在 ➊ 处，`package pirate_phrases;`指明这个类是`pirate_phrases`包的一部分。在`pirate_phrases`目录里创建`Farewells.java`:

```
package pirate_phrases;

public class Farewells
{
    public static void goodbye()
    {
        System.out.println("A fair turn of the tide ter ye thar, ye magnificent sea friend!!");
    }
}
```

> Now create _PirateConversation.java_ in the _phrasebook_ directory:

在`phrasebook`目录里创建`PirateConversation.java`:

```
import pirate_phrases.*;

public class PirateConversation
{
    public static void main(String[] args)
    {
        Greetings greetings = new Greetings();
        greetings.hello();

        Farewells farewells = new Farewells();
        farewells.goodbye();
    }
}
```

> The first line, `import pirate_phrases.*;`, imports all classes in the `pirate_phrases` package, which contains the `Greetings` and `Farewells` classes.

第一行，`import pirate_phrases.*;`,引入`pirate_phrases`包里的所有类，`Greetings`类和`Farewells`类。

> If you run `javac PirateConversation.java` within the _phrasebook_ directory followed by `java PirateConversation`, you should see this:

如果你在*phrasebook*目录运行`javac PirateConversation.java`，然后运行`java PirateConversation`,你应该看到:

```
Shiver me timbers!!!
A fair turn of the tide ter ye thar, ye magnificent sea friend!!
```

> And thar she blows, dear reader. Thar she blows indeed.
>
> Note that, when you’re compiling a Java program, Java searches your classpath for packages. Try typing the following:

注意，编译 Java 程序时，在类路径里搜索包。试试：

```
cd pirate_phrases
javac ../PirateConversation.java
```

> You’ll get this:

会得到下面结果:

```
../PirateConversation.java:1: error: package pirate_phrases does not exist
import pirate_phrases.*;
^
```

> Boom! The Java compiler just told you to hang your head in shame and maybe weep a little.

Java 编译器报错了。

> Why? It thinks that the `pirate_phrases` package doesn’t exist. But that’s stupid, right? You’re in the `pirate_phrases` directory!

你正在`pirate_phrases`目录里，报错信息怎么说`pirate_phrases`包不存在呢？

> What’s happening here is that the default classpath only includes the current directory, which in this case is `pirate_phrases`. `javac` is trying to find the directory _phrasebook/pirate_phrases/pirate_phrases_, which doesn’t exist. When you run `javac ../PirateConversation.java` from within the phrasebook directory, `javac` tries to find the directory _phrasebook/pirate_phrases_, which does exist. Without changing directories, try running `javac -classpath ../ ../PirateConversation.java`. Shiver me timbers, it works! This works because you manually set the classpath to the parent directory of _pirate_phrases_, which is _phrasebook_. From there, `javac` can successfully find the _pirate_phrases_ directory.

是因为默认类路径只包含当前目录`pirate_phrases`。`javac`找的是*phrasebook/pirate_phrases/pirate_phrases*目录，这个目录不存在。还在这个目录下，试一下`javac -classpath ../ ../PirateConversation.java`，成功了。因为手动设定类路径为父目录*phrasebook*。`javac`从这个目录能找到*pirate_phrases*目录。

> In summary, packages organize code and require a matching directory structure. Importing classes allows you to refer to them without having to prepend the entire class’s package name. `javac` and `java` find packages using the classpath.

总结，包组织代码，并要求与目录匹配。引入类允许你无需使用整个类的报名就能引用它们。`javac`和`java`用类路径查找包。

> JAR Files

## JAR 文件

> JAR files allow you to bundle all your _.class_ files into one single file. Navigate to your _phrasebook_ directory and run the following:

JAR 文件让使你能把所有*.class*文件打包成一个单独文件。进入*phrasebook*目录，运行下面两个命令:

```
jar cvfe conversation.jar PirateConversation PirateConversation.class pirate_phrases/*.class
java -jar conversation.jar
```

> This displays the pirate conversation correctly. You bundled all the class files into _conversation.jar_. Using the `e` flag, you also indicated that the `PirateConversation` class is the _entry point_. The entry point is the class that contains the `main` method that should be executed when the JAR as a whole runs, and `jar` stores this information in the file _META-INF/MANIFEST.MF_ within the JAR file. If you were to read that file, it would contain this line:

结果正确。你把所有类文件打包进*conversation.jar*。用`e`标记指定了`PirateConversation`类是*进入点*。进入点是含有`main`方法的类，当 JAR 作为一个整体运行时，`main`会被运行。`jar`把这个信息储存在 JAR 文件里的*META-INF/MANIFEST.MF*文件里，它会包含这行:

```
Main-Class: PirateConversation
```

> By the way, when you execute JAR files, you don’t have to worry which directory you’re in, relative to the file. You could change to the _pirate_phrases_ directory and run `java -jar ../conversation.jar`, and it would work fine. The reason is that the JAR file maintains the directory structure. You can see its contents with `jar tf conversation.jar`, which outputs this:

顺便提一下，执行 JAR 文件时，不用担心当前在哪个目录。你可以改变目录到*pirate_phrases*,并执行`java -jar ../conversation.jar`,也会执行成功。因为 JAR 文件保持了目录结构。执行`jar tf conversation.jar`可以看到:

```
META-INF/
META-INF/MANIFEST.MF
PirateConversation.class
pirate_phrases/Farewells.class
pirate_phrases/Greetings.class
```

> You can see that the JAR file includes the _pirate_phrases_ directory. One more fun fact about JARs: they’re really just ZIP files with a .jar extension. You can treat them the same as any other ZIP file.

可以看到 JAR 文件包含了*pirate_phrases*目录。一个更有趣的事实是：JAR 是真正的扩展名是*.jar*的 ZIP 文件。你可以像对待 ZIP 文件一样对待它们。

> clojure.jar

## clojure.jar

Now you’re ready to see how Clojure works under the hood! Download [the 1.7.0 stable release](http://repo1.maven.org/maven2/org/clojure/clojure/1.7.0/clojure-1.7.0.zip) and run it:

现在你已经做好准备了，来看看 Clojure 在底层是如何工作的！下载[the 1.7.0 stable release](http://repo1.maven.org/maven2/org/clojure/clojure/1.7.0/clojure-1.7.0.zip) 并运行:

```
java -jar clojure-1.7.0.jar
```

> You should see the most soothing of sights, the Clojure REPL. How did it actually start up? Let’s look at _META-INF/MANIFEST.MF_ in the JAR file:

你应该看到熟悉的老朋友，Clojure REPL。它实际上是如何启动的？看一下 JAR 文件里的*META-INF/MANIFEST.MF*:

```
Manifest-Version: 1.0
Archiver-Version: Plexus Archiver
Created-By: Apache Maven
Built-By: hudson
Build-Jdk: 1.7.0_20
Main-Class: clojure.main
```

> It looks like `clojure.main` is specified as the entry point. Where does this class come from? Well, have a look at _clojure/main.java_ on GitHub at https://github.com/clojure/clojure/blob/master/src/jvm/clojure/main.java:

看起来`clojure.main`被指定为入口。这个类是怎么来的？看一下 Github 上的*clojure/main.java*, [点击查看](https://github.com/clojure/clojure/blob/master/src/jvm/clojure/main.java):

```
/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 *   the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package clojure;

import clojure.lang.Symbol;
import clojure.lang.Var;
import clojure.lang.RT;

public class main{

final static private Symbol CLOJURE_MAIN = Symbol.intern("clojure.main");
final static private Var REQUIRE = RT.var("clojure.core", "require");
final static private Var LEGACY_REPL = RT.var("clojure.main", "legacy-repl");
final static private Var LEGACY_SCRIPT = RT.var("clojure.main", "legacy-script");
final static private Var MAIN = RT.var("clojure.main", "main");

public static void legacy_repl(String[] args) {
    REQUIRE.invoke(CLOJURE_MAIN);
    LEGACY_REPL.invoke(RT.seq(args));
}

public static void legacy_script(String[] args) {
    REQUIRE.invoke(CLOJURE_MAIN);
    LEGACY_SCRIPT.invoke(RT.seq(args));
}

public static void main(String[] args) {
    REQUIRE.invoke(CLOJURE_MAIN);
    MAIN.applyTo(RT.seq(args));
}
}
```

> As you can see, the file defines a class named `main`. It belongs to the package `clojure` and defines a `public static main` method, and the JVM is completely happy to use it as an entry point. Seen this way, Clojure is a JVM program just like any other.

如你所见，这个文件定义了一个名称是`main`的类。它属于`clojure`包，还定义了一个`public static main`方法，JVM 用它作为入口。可以看到，就像其他程序一样，Clojure 是一个 JVM 程序。

> This wasn’t meant to be an in-depth Java tutorial, but I hope that it helped clarify what programmers mean when they talk about Clojure “running on the JVM” or being a “hosted” language. In the next section, you’ll continue to explore the magic of the JVM as you learn how to use additional Java libraries within your Clojure project.

这注定不是一个深入的 Java 教程，但我希望它帮助你澄清，Clojure 运行在 JVM 上，或 Clojure 是一个宿主语言的含义。下节将学习如何在 Clojure 项目里使用 Java 库，你会继续探索 JVM 的魔力。

> Clojure App JARs

## Clojure App JARs

> You now know how Java runs Java JARs, but how does it run Clojure apps bundled as JARs? After all, Clojure applications don’t have classes, do they?

现在你知道了 Java 如何运行 Java JAR 文件，但它如何运行打包成 JAR 文件的 Clojure 应用程序呢？毕竟，Clojure 应用程序没有类，对吗？

> As it turns out, you can make the Clojure compiler generate a class for a namespace by putting the `(:gen-class)` directive in the namespace declaration. (You can see this in the very first Clojure program you created, clojure-noob in Chapter 1. Remember that program, little teapot?) This means that the compiler produces the bytecode necessary for the JVM to treat the namespace as if it defines a Java class.

原来是这样，可以让 Clojure 编译器为命名空间生成一个类，需要做的是把`(:gen-class)`指令放进命名空间声明里(可以在第一章的小应用程序 clojure-noob 里看到)。这意味着编译器为 JVM 生成必要的字节码，以便把这个命名空间当成一个 Java 类。

> You set the namespace of the entry point for your program in the program’s _project.clj_ file, using the `:main` attribute. For _clojure-noob_, you should see `:main ^:skip-aot clojure-noob.core`. When Leiningen compiles this file, it will add a _meta-inf/manifest.mf_ file that contains the entry point to the resulting JAR file.

你需要在`project.clj`文件里为应用程序设置进入点的命名空间，使用`:main`属性。对于*clojure-noob*,你应该看到`:main ^:skip-aot clojure-noob.core`。Leiningen 编译这个文件时，它会往生成得 JAR 文件里添加一个包含这个进入点的*meta-inf/manifest.mf*文件。

> So, if you define a `-main` function in a namespace and include the `(:gen-class)` directive, and also set `:main` in your _project.clj_ file, your program will have everything it needs for Java to run it when it gets compiled as a JAR. You can try this out in your terminal by navigating to your clojure-noob directory and running this:

那么，如果你在一个命名空间里定义了一个`-main`函数并包含了`(:gen-class)`指令，也在*project.clj*文件里设置了`:main`,把你的程序打包成 JAR 文件，Java 就能运行它了。

```
lein uberjar
java -jar target/uberjar/clojure-noob-0.1.0-SNAPSHOT-standalone.jar
```

> You should see two messages printed out: “Cleanliness is next to god­liness” and “I’m a little teapot!” Note that you don’t need Leiningen to run the JAR file; you can send it to friends and neighbors and they can run it as long as they have Java installed.

你应该看到打印了两条消息:”Cleanliness is next to god­liness”和“I’m a little teapot!”。注意你不需要 Leiningen 运行 JAR 文件；你可以把它发给你的朋友和邻居，只要他们装了 Java 就能直接运行。

> Java Interop

## Java 互操作

> One of Rich Hickey’s design goals for Clojure was to create a practical language. For that reason, Clojure was designed to make it easy for you to interact with Java classes and objects, meaning you can use Java’s extensive native functionality and its enormous ecosystem. The ability to use Java classes, objects, and methods is called _Java interop_. In this section, you’ll learn how to use Clojure’s interop syntax, how to import Java packages, and how to use the most frequently used Java classes.

Rich Hickey 设计 Clojure 的一个目的是创建一个实用的语言。因为这个原因，Clojure 被设计的很容易与 Java 类和对象交互，意味着你可以使用 Java 的大量源生功能和它的巨大生态圈。使用 Java 类，对象，方法的能力叫*Java 互操作*。这节，你将学习如何使用 Clojure 的互操作语法，如何引入 Java 包，如何使用最频繁使用的 Java 类。

> Interop Syntax

### 互操作语法

> Using Clojure’s interop syntax, interacting with Java objects and classes is straightforward. Let’s start with object interop syntax.

用 Clojure 的互操作语法与 Java 对象，类互操作很简单。从对象互操作语法开始。

> You can call methods on an object using `(.methodName object)`. For example, because all Clojure strings are implemented as Java strings, you can call Java methods on them:

用`(.methodName object)`可以调用对象的方法。比如，所有 Clojure 字符串都实现为 Java 字符串，可以调用 Java 方法:

```
(.toUpperCase "By Bluebeard's bananas!")
; => "BY BLUEBEARD'S BANANAS!"

➊ (.indexOf "Let's synergize our bleeding edges" "y")
; => 7
```

> These are equivalent to this Java:

与下面得 Java 代码一样：

```
"By Bluebeard's bananas!".toUpperCase()
"Let's synergize our bleeding edges".indexOf("y")
```

> Notice that Clojure’s syntax allows you to pass arguments to Java methods. In this example, at ➊ you passed the argument `"y"` to the `indexOf` method.

用 Clojure 语法可以给 Java 方法传递参数。在 ➊ 处，给`indexOf`方法传递了参数`"y"`。

> You can also call static methods on classes and access classes’ static fields. Observe!

也可以调用类的静态方法或访问类的静态字段。

```
➊ (java.lang.Math/abs -3)
; => 3

➋ java.lang.Math/PI
; => 3.141592653589793
```

> At ➊ you called the `abs` static method on the `java.lang.Math` class, and at ➋ you accessed that class’s `PI` static field.

在 ➊ 处，调用了`java.lang.Math`类的静态`abs`方法，在 ➋ 处，访问了这个类的静态字段`PI`。

> All of these examples (except `java.lang.Math/PI`) use macros that expand to use the dot special form. In general, you won’t need to use the dot special form unless you want to write your own macros to interact with Java objects and classes. Nevertheless, here is each example followed by its macroexpansion:

所有的例子(除了`java.lang.Math/PI`)都被宏展开为使用点特殊形式。通常不需要使用点特殊形式，除非你需要自己写宏与 Java 对象和类交互。下面是宏展开例子:

```
(macroexpand-1 '(.toUpperCase "By Bluebeard's bananas!"))
; => (. "By Bluebeard's bananas!" toUpperCase)

(macroexpand-1 '(.indexOf "Let's synergize our bleeding edges" "y"))
; => (. "Let's synergize our bleeding edges" indexOf "y")

(macroexpand-1 '(Math/abs -3))
; => (. Math abs -3)
```

> This is the general form of the dot operator:

这是点操作符的通用形式:

```
(. object-expr-or-classname-symbol method-or-member-symbol optional-args*)
```

> The dot operator has a few more capabilities, and if you’re interested in exploring it further, you can look at clojure.org’s documentation on Java interop at [http://clojure.org/java_interop#Java%20Interop-The%20Dot%20special%20form](http://clojure.org/java_interop#Java Interop-The Dot special form).

点操作符还有几个功能，如果你感兴趣，可以看[Clojure 的文档](http://clojure.org/java_interop#Java Interop-The Dot special form)。

> Creating and Mutating Objects

### 创建和修改对象

> The previous section showed you how to call methods on objects that already exist. This section shows you how to create new objects and how to interact with them.

前一节学习了如何调用已经存在的对象的方法。这节学习如何创建对象并与之交互。

> You can create a new object in two ways: `(new ClassName optional-args)` and `(ClassName. optional-args)`:

创建对象有两种方法：`(new ClassName optional-args)`和`(ClassName. optional-args)`:

```
(new String)
; => ""

(String.)
; => ""

(String. "To Davey Jones's Locker with ye hardies")
; => "To Davey Jones's Locker with ye hardies"
```

> Most people use the dot version, `(ClassName.)`.

大部分人用`(ClassName.)`。

> To modify an object, you call methods on it like you did in the previous section. To investigate this, let’s use `java.util.Stack`. This class represents a last-in, first-out (LIFO) stack of objects, or just stack. Stacks are a common data structure, and they’re called stacks because you can visualize them as a physical stack of objects, say, a stack of gold coins that you just plundered. When you add a coin to your stack, you add it to the top of the stack. When you remove a coin, you remove it from the top. Thus, the last object added is the first object removed.

同上一节一样，要修改一个对象也调用对象的方法。我们用`java.util.Stack`演示。这个类表示一个后进先出的栈。

> Unlike Clojure data structure, Java stacks are mutable. You can add items to them and remove items, changing the object instead of deriving a new value. Here’s how you might create a stack and add an object to it:

与 Clojure 数据结构不同，Java 栈是可变的。往栈里添加东西或从栈里移除东西，都是修改这个对象而不是生成一个新值。下面是如何创建栈和往栈里加东西:

```
(java.util.Stack.)
; => []

➊ (let [stack (java.util.Stack.)]
  (.push stack "Latest episode of Game of Thrones, ho!")
  stack)
; => ["Latest episode of Game of Thrones, ho!"]
```

> There are a couple of interesting details here. First, you need to create a `let` binding for `stack` like you see at ➊ and add it as the last expression in the let form. If you didn’t do that, the value of the overall expression would be the string `"Latest episode of Game of Thrones, ho!"` because that’s the return value of `push`.

这里有几个细节。首先，在 ➊ 出，你需要给`stack`创建一个`let`绑定。另外需要把`stack`放在`let`的最后，以便返回这个对象，否则整个表达式的结果就是`push`的返回结果`"Latest episode of Game of Thrones, ho!"`。

> Second, Clojure prints the stack with square brackets, the same textual representation it uses for a vector, which could throw you because it’s not a vector. However, you can use Clojure’s `seq` functions for reading a data structure, like `first`, on the stack:

栈不是 vector,但 Clojure 用方括号打印栈，与 vector 的文本表示相同，这可能使你感到迷惑。但你可以在栈上使用 Clojure 的`seq`函数，比如`first`:

```
(let [stack (java.util.Stack.)]
  (.push stack "Latest episode of Game of Thrones, ho!")
  (first stack))
; => "Latest episode of Game of Thrones, ho!"
```

> But you can’t use functions like `conj` and `into` to add elements to the stack. If you do, you’ll get an exception. It’s possible to read the stack using Clojure functions because Clojure extends its abstractions to `java.util.Stack`, a topic you’ll learn about in Chapter 13.

但你不能用`conj`,`into`这样的函数往栈里添加成员。这么做会出现异常。能用 Clojure 的函数读栈的原因是:Clojure 的抽象扩展到了`java.util.Stack`。13 章将学习这个主题。

> Clojure provides the `doto` macro, which allows you to execute multiple methods on the same object more succinctly:

Clojure 提供了`doto`宏，让你更简洁地在同一对象上执行多个方法:

```
(doto (java.util.Stack.)
  (.push "Latest episode of Game of Thrones, ho!")
  (.push "Whoops, I meant 'Land, ho!'"))
; => ["Latest episode of Game of Thrones, ho!" "Whoops, I meant 'Land, ho!'"]
```

> The `doto` macro returns the object rather than the return value of any of the method calls, and it’s easier to understand. If you expand it using `macroexpand-1`, you can see its structure is identical to the `let` expression you just saw in an earlier example:

`doto` 宏返回的是这个对象，而不是任何方法方法调用的返回值。如果用`macroexpand-1`展开，可以看到结果与前面的`let`表达式的结构完全一样:

```
(macroexpand-1
 '(doto (java.util.Stack.)
    (.push "Latest episode of Game of Thrones, ho!")
    (.push "Whoops, I meant 'Land, ho!'")))
; => (clojure.core/let
      [G__2876 (java.util.Stack.)]
      (.push G__2876 "Latest episode of Game of Thrones, ho!")
      (.push G__2876 "Whoops, I meant 'Land, ho!'")
      G__2876)
```

> Convenient!

太方便了！

> Importing

### 导入

> In Clojure, importing has the same effect as it does in Java: you can use classes without having to type out their entire package prefix:

Clojure 里的导入与 Java 里的一样：使用类时候无需再输入完整包前缀:

```
(import java.util.Stack)
(Stack.)
; => []
```

> You can also import multiple classes at once using this general form:

也可以用这个通用形式一次导入多个类:

```
(import [package.name1 ClassName1 ClassName2]
        [package.name2 ClassName3 ClassName4])
```

> Here’s an example:

例如:

```
(import [java.util Date Stack]
        [java.net Proxy URI])

(Date.)
; => #inst "2016-09-19T20:40:02.733-00:00"
```

> But usually, you’ll do all your importing in the `ns` macro, like this:

但通常都是在`ns`宏里使用导入:

```
(ns pirate.talk
  (:import [java.util Date Stack]
           [java.net Proxy URI]))
```

> The two different methods of importing classes have the same results, but the second is usually preferable because it’s convenient for people reading your code to see all the code involving naming in the `ns` declaration.

这两种导入方法的结果一样，但第二种更好。因为阅读代码的人能很方便地在`ns`里看到所有涉及到命名的代码。

> And that’s how you import classes! Pretty easy. To make life even easier, Clojure automatically imports the classes in `java.lang`, including `java.lang.String` and `java.lang.Math`, which is why you were able to use `String` without a preceding package name.

这就是如何导入类，很容易。为方便使用者，Clojure 自动导入`java.lang`里的类，包括`java.lang.String`和`java.lang.Math`。这就是能使用直接`String`的原因。

> Commonly Used Java Classes

## 常用 Java 类

> To round out this chapter, let’s take a quick tour of the Java classes that you’re most likely to use.

看一下常用的 Java 类。

> The System Class

### System 类

> The `System` class has useful class fields and methods for interacting with the environment that your program’s running in. You can use it to get environment variables and interact with the standard input, standard output, and error output streams.

`System`类的字段和方法可以用来与程序运行的环境交互。可以用来获取环境变量，与标准输入流，标准输出流，错误输出流交互。

> The most useful methods and members are `exit`, `getenv`, and `getProperty`. You might recognize `System/exit` from Chapter 5, where you used it to exit the Peg Thing game. `System/exit` terminates the current program, and you can pass it a status code as an argument. If you’re not familiar with status codes, I recommend Wikipedia’s “Exit status” article at http://en.wikipedia.org/wiki/Exit_status.

最有用的方法是`exit`,`getenv`,和`getProperty`。第 5 章，我们用过`System/exit`退出游戏。`System/exit`中止当前程序，可以传给它一个状态码作为参数。如果你对状态码不熟悉，我推荐你看维基百科的[“Exit status”文章](http://en.wikipedia.org/wiki/Exit_status)。

> `System/getenv` will return all of your system’s environment variables as a map:

`System/getenv`返回一个 map,包含了所有系统环境变量值:

```
(System/getenv)
{"USER" "the-incredible-bulk"
 "JAVA_ARCH" "x86_64"}
```

> One common use for environment variables is to configure your program.

环境变量通常用于配置程序。

> The JVM has its own list of properties separate from the computer’s environment variables, and if you need to read them, you can use `System/getProperty`:

JVM 有自己的与计算机环境变量分开的属性列表，可以用`System/getProperty`读取:

```
➊ (System/getProperty "user.dir")
; => "/Users/dabulk/projects/dabook"

➋ (System/getProperty "java.version")
; => "1.7.0_17"
```

> The first call at ➊ returned the directory that the JVM started from, and the second call at ➋ returned the version of the JVM.

➊ 处返回了 JVM 启动目录，➋ 处返回了 JVM 版本。

> The Date Class

### The Date Class

> Java has good tools for working with dates. I won’t go into too much detail about the `java.util.Date` class because the online API documentation (available at http://docs.oracle.com/javase/7/docs/api/java/util/Date.html) is thorough. As a Clojure developer, you should know three features of this `date` class. First, Clojure allows you to represent dates as literals using a form like this:

Java 有很好的日期工具。我不会讲太多`java.util.Date`类的细节，[在线文档](http://docs.oracle.com/javase/7/docs/api/java/util/Date.html)讲的很详细。作为一个 Clojure 开发者，你应该知道`data`类的三个特征。首先，Clojure 允许你用这样的字面量表示日期:

```
#inst "2016-09-19T20:40:02.733-00:00"
```

> Second, you need to use the `java.util.DateFormat` class if you want to customize how you convert dates to strings or if you want to convert strings to dates. Third, if you’re doing tasks like comparing dates or trying to add minutes, hours, or other units of time to a date, you should use the immensely useful `clj-time` library (which you can check out at https://github.com/clj-time/clj-time).

第二，如果你需要自定义日期与字符串的相互转换，你需要`java.util.DateFormat`类。第三，如果你想比较日期，或对一个日期增加分钟，小时等时间单位，你应该使用[clj-time 库](https://github.com/clj-time/clj-time)。

> Files and Input/Output

## 文件与 Input/Output

> In this section you’ll learn about Java’s approach to input/output (IO) and how Clojure simplifies it. The `clojure.java.io` namespace provides many handy functions for simplifying IO (https://clojure.github.io/clojure/clojure.java.io-api.html). This is great because Java IO isn’t exactly straightforward. Because you’ll probably want to perform IO at some point during your programming career, let’s start wrapping your mind tentacles around it.

这节将学习 Java 的 I/O 方法，以及 Clojure 如何实现 I/O。[`clojure.java.io`](https://clojure.github.io/clojure/clojure.java.io-api.html)命名空间提供了许多用于简化 IO 的函数。这很棒，因为 Java 的 IO 不是很直观。开始学习。

> IO involves resources, be they files, sockets, buffers, or whatever. Java has separate classes for reading a resource’s contents, for writings its contents, and for interacting with the resource’s properties.

IO 涉及各种资源，可能是文件，socket，buffer,或其他什么。Java 为资源读取，资源写入，资源属性分别提供了类。

> For example, the `java.io.File` class is used to interact with a file’s properties:

例如，`java.io.FIle`用来读取文件属性:

```
(let [file (java.io.File. "/")]
➊   (println (.exists file))
➋   (println (.canWrite file))
➌   (println (.getPath file)))
; => true
; => false
; => /
```

> Among other tasks, you can use it to check whether a file exists, to get the file’s read/write/execute permissions, and to get its filesystem path, which you can see at ➊, ➋, and ➌, respectively.

➊ 处检查文件是否存在，➋ 处获取文件的读/写/执行权限，➌ 处获取文件路径。

> Reading and writing are noticeably missing from this list of capabilities. To read a file, you could use the `java.io.BufferedReader` class or perhaps `java.io.FileReader`. Likewise, you can use the `java.io.BufferedWriter` or `java.io.FileWriter` class for writing. Other classes are available for reading and writing as well, and which one you choose depends on your specific needs. Reader and writer classes all have the same base set of methods for their interfaces; readers implement `read`, `close`, and more, while writers implement `append`, `write`, `close`, and `flush`. Java gives you a variety of IO tools. A cynical person might say that Java gives you enough rope to hang yourself, and if you find such a person, I hope you give them a hug.

要读文件，可以使用`java.io.BufferedReader`或`java.io.FileReader`类。要写文件，可以使用`java.io.BufferedWriter`或`java.io.FileWriter`类。使用哪一个看你的具体需要。读写类各自都有同样的 interface 方法。读有`read`,`close`和其他的，写有`append`,`write`,`close`和`flush`。Java 提供了各种 IO 工具。爱嘲讽的人可能会说：Java 给了你足够多的上吊用的绳子，如果你发现了这样的人，我希望你给他一个拥抱。

> Either way, Clojure makes reading and writing easier for you because it includes functions that unify reading and writing across different kinds of resources. For example, `spit` writes to a resource, and `slurp` reads from one. Here’s an example of using them to write and read a file:

总之，Clojure 使读写更容易，因为你可以对不同资源的读写使用同样的函数。例如`spit`写入一个资源，`slurp`读取一个资源。读写文件例子:

```
(spit "/tmp/hercules-todo-list"
"- kill dat lion brov
- chop up what nasty multi-headed snake thing")

(slurp "/tmp/hercules-todo-list")

; => "- kill dat lion brov
;     - chop up what nasty multi-headed snake thing"
```

> You can also use these functions with objects representing resources other than files. The next example uses a `StringWriter`, which allows you to perform IO operations on a string:

这些函数也可以作用于表示资源的对象。下面的例子使用了`StringWriter`,它允许你在字符串上执行 IO:

```
(let [s (java.io.StringWriter.)]
  (spit s "- capture cerynian hind like for real")
  (.toString s))
; => "- capture cerynian hind like for real"
```

> You can also read from a `StringReader` using `slurp`:

`slurp`也能从`StringReader`读取:

```
(let [s (java.io.StringReader. "- get erymanthian pig what with the tusks")]
  (slurp s))
; => "- get erymanthian pig what with the tusks"
```

> In addition, you can use the `read` and `write` methods for resources. It doesn’t really make much difference which you use; `spit` and `slurp` are convenient because they work with just a string representing a filesystem path or a URL.

另外，也可以用`read`和`write`读写资源。用哪个没有太大差别，`spit`和`slurp`的方便在于，他们可以用于表示文件系统路径或 URL 的字符串。

> The `with-open` macro is another convenience: it implicitly closes a resource at the end of its body, ensuring that you don’t accidentally tie up resources by forgetting to manually close the resource. The `reader` function is a handy utility that, according to the `clojure.java.io` API docs, “attempts to coerce its argument to an open `java.io.Reader`.” This is convenient when you don’t want to use `slurp`, because you don’t want to try to read a resource in its entirety and you don’t want to figure out which Java class you need to use. You could use `reader` along with `with-open` and the `line-seq` function if you’re trying to read a file one line at a time. Here’s how you could print just the first item of the Hercules to-do list:

`with-open`宏也很方便：在它主体末尾，会暗中关闭资源，确保不会因为你忘记关闭而占用资源。`reader`函数很方便，它把参数强制转成开放的`java.io.Reader`。当不想用`slurp`整个读入一个资源，并且不想搞清楚需要使用哪个 Java 类时，它非常方便。如果想一次读入一行文件，可以用`reader`加上`with-open`和`line-seq`。下面得例子只打印 Hercules to-do list 的第一条。

```
(with-open [todo-list-rdr (clojure.java.io/reader "/tmp/hercules-todo-list")]
  (println (first (line-seq todo-list-rdr))))
; => - kill dat lion brov
```

> That should be enough for you to get started with IO in Clojure. If you’re trying to do more sophisticated tasks, definitely check out the [`clojure.java.io` docs](https://clojure.github.io/clojure/clojure.java.io-api.html), the [`java.nio.file`](https://docs.oracle.com/javase/7/docs/api/java/nio/file/package-summary.html) package docs, or the [`java.io`](http://docs.oracle.com/javase/7/docs/api/java/io/package-summary.html) package docs.

这些足够让你开始在 Clojure 里使用 IO 了。如果任务更复杂，可以看：[`clojure.java.io`](https://clojure.github.io/clojure/clojure.java.io-api.html)，[`java.nio.file`](https://docs.oracle.com/javase/7/docs/api/java/nio/file/package-summary.html) 或 [`java.io`](http://docs.oracle.com/javase/7/docs/api/java/io/package-summary.html)
