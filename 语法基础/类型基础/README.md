# 类型基础

Java 中有两种类型，原始类型（Primitive Type）会被直接映射到 CPU 的基础类型，引用类型（Reference Type）则指向了内存中的对象。Java 的基础类型包含了如下几种：

```java
// boolean (true|false)
var result = true;
var anotherResult = false;

// char (character)
var firstLetter = 'j';

// int (signed 32 bits integer)
var numberOfLegs = 2;

// double (64 bits floating point)
var cost = 3.78;

// long and float
// 一些更特殊的类型，它们需要后缀（L或f）长（64位整数）并浮点（32位浮点数）
var longValue = 123L;
var floatValue = 123.5f;

// byte and short
// 还有字节（带符号的8位整数）和短（带符号的16位短整数），它们仅在定义对象时占用较少的内存
record CompactHeader(byte tag, short version) {}
short value = 12;
var result = value + value;
```
