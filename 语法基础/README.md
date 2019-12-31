# Java 语法基础

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

# 关键字

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
