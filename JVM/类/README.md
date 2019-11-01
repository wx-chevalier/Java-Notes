# 类加载

JVM是虚拟机的一种，它的指令集语言是字节码，字节码构成的文件是class文件。平常我们写的Java文件，需要编译为class文件才能交给JVM运行。可以这么说：C语言代码——>二进制文件——>计算机硬件，就相当于Java代码——>字节码文件——>JVM。JVM将指定的class文件读取到内存里，并运行该class文件里的Java程序的过程，就称之为类的加载；反之，将某个class文件的运行时数据从JVM中移除的过程，就称之为类的卸载。

class文件的运行时数据就是C++对象，也称为kclass对象，这些运行时数据在JDK7之前是放在永久代（PermGen），JDK8之后则放在元空间（Metaspace）。

# 类的生命周期


# 链接

- https://time.geekbang.org/column/article/11523
