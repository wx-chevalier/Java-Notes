# Object

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

# 链接

- https://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247484210&idx=1&sn=9d40e2e4c72f0727c7b7925cbe314fc0&chksm=ebd74233dca0cb2560677c7dc7746bf166195d793860c41ab477431af2cf0a6004477e27b814&scene=21###wechat_redirect
