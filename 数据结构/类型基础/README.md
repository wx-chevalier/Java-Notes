# 类型基础

变量就是申请内存来存储值。也就是说，当创建变量的时候，需要在内存中申请空间。内存管理系统根据变量的类型为变量分配存储空间，分配的空间只能用来储存该类型数据。

![代码到内存](https://s2.ax1x.com/2020/02/04/1DmG11.png)

Java 中有两种类型，原始类型（Primitive Type）会被直接映射到 CPU 的基础类型，引用类型（Reference Type）则指向了内存中的对象。

- 原始类型：boolean，char，byte，short，int，long，float，double。

- 包装类型：Boolean，Character，Byte，Short，Integer，Long，Float，Double。
