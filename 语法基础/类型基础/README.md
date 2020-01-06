# 类型基础

Java 中有两种类型，原始类型（Primitive Type）会被直接映射到 CPU 的基础类型，引用类型（Reference Type）则指向了内存中的对象。

# 原始类型

Java 的基础类型包含了如下几种：

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

如果不存在精度损失，将精度转换为 double 或 float，则可以进行自动转换。您可以使用强制转换为相反方向的转换，将剃掉补充位：

```java
// 自动转换
int intValue = 13;
long longValue = intValue;

// 强制转换
long longValue = 1_000_000_000_000L;
int intValue = (int) longValue;
System.out.println(intValue);
```

# Objects

所有其他类型都是对象，有两种特殊类型，String 和 arrays 是对象，但编译器认为它们是内置的类型。字符串的定义如下：

```java
var text = "hello";
System.out.println("hello".length());
System.out.println("hello".toUpperCase());
System.out.println("hello".toLowerCase());
System.out.println("hello".charAt(0));
System.out.println("hello".indexOf('l'));
System.out.println("hello".indexOf('o'));
```

数组的定义如下：

```java
var intArray = new int[] {2, 3};

System.out.println(intArray[0]);
intArray[0] = 42;

// intArray[-1] = 42;   // throws IndexOutOfBoundsException
var clonedArray = intArray.clone();
var arrayLength = intArray.length;
System.out.println(arrayLength);

System.out.println(intArray);
System.out.println(intArray.equals(clonedArray));
var matrix = new double[][] { { 2.0, 3.0}, { 4.0, 5.0 } };
```

由于基本类型和数组几乎没有方法，因此，如果要使用它们，则必须使用静态方法。静态方法是在可以使用语法调用的某个类型上声明的函数 `SomeWhere.methodName(arg0, arg1, arg2)`。

```java
var resultAsInt = java.lang.Integer.parseInt("42");
System.out.println(resultAsInt);

var text = java.util.Arrays.toString(intArray);
System.out.println(text);

var intList = List.of(2, 3);
```
