# 类加载

JVM 是虚拟机的一种，它的指令集语言是字节码，字节码构成的文件是 class 文件。平常我们写的 Java 文件，需要编译为 class 文件才能交给 JVM 运行。可以这么说：C 语言代码——>二进制文件——>计算机硬件，就相当于 Java 代码——>字节码文件——>JVM。JVM 将指定的 class 文件读取到内存里，并运行该 class 文件里的 Java 程序的过程，就称之为类的加载；反之，将某个 class 文件的运行时数据从 JVM 中移除的过程，就称之为类的卸载。

class 文件的运行时数据就是 C++对象，也称为 kclass 对象，这些运行时数据在 JDK7 之前是放在永久代（PermGen），JDK8 之后则放在元空间（Metaspace）。

# 链接

- https://time.geekbang.org/column/article/11523

- https://zhuanlan.zhihu.com/p/81419563 我竟然不再抗拒 Java 的类加载机制了