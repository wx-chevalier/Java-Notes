# Number & Math

一般地，当需要使用数字的时候，我们通常使用内置数据类型，如：byte、int、long、double 等。然而，在实际开发过程中，我们经常会遇到需要使用对象，而不是内置数据类型的情形。为了解决这个问题，Java 语言为每一个内置数据类型提供了对应的包装类。所有的包装类（Integer、Long、Byte、Double、Float、Short）都是抽象类 Number 的子类。

![Number 与子类结构](https://s2.ax1x.com/2020/02/05/1rNd8x.md.png)

这种由编译器特别支持的包装称为装箱，所以当内置数据类型被当作对象使用的时候，编译器会把内置类型装箱为包装类。相似的，编译器也可以把一个对象拆箱为内置类型。Number 类属于 java.lang 包。下面是一个使用 Integer 对象的实例：

```java
public class Test{

   public static void main(String args[]){
      Integer x = 5;
      x =  x + 10;
      System.out.println(x);
   }
}

// 15
```

当 x 被赋为整型值时，由于 x 是一个对象，所以编译器要对 x 进行装箱。然后，为了使 x 能进行加运算，所以要对 x 进行拆箱。

| 序号 | 方法与描述                                               |
| :--- | :------------------------------------------------------- |
| 1    | xxxValue() 将 Number 对象转换为 xxx 数据类型的值并返回。 |
| 2    | compareTo() 将 number 对象与参数比较。                   |
| 3    | equals()判断 number 对象是否与参数相等。                 |
| 4    | valueOf() 返回一个 Number 对象指定的内置数据类型         |
| 5    | toString() 以字符串形式返回值。                          |
| 6    | parseInt() 将字符串解析为 int 类型。                     |

# 浮点数

## 浮点数的比较

在[编程语言原理](https://ngte-pl.gitbook.io/i/?q=浮点数)的相关章节中我们讨论了计算机系统中浮点数的底层表示，那么在实际的编程中我们也需要注意浮点数的比较。譬如下面三种比较方式结果都不会符合预期：

```java
// 使用简单类型比较
float a = 0.7f - 0.6f;
float b = 0.8f - 0.7f;

if (a == b) {
    System.out.println("true");
} else {
    System.out.println("false"); // false
}

// 使用封装类型比较
Float m = Float.valueOf(a);
Float n = Float.valueOf(b);

if (m.equals(n)) {
    System.out.println("true");
} else {
    System.out.println("false"); // false
}

// 使用 BigDecimal 比较
BigDecimal x = new BigDecimal(0.8f);
BigDecimal y = new BigDecimal("0.8");

if (x.equals(y)) {
    System.out.println("true");
} else {
    System.out.println("false"); // false
}
```

我们需要切换到如下的比较方式：

```java
double diff = 1e-6;
if (Math.abs(a - b) < diff) {
    System.out.println("true"); // true
} else {
    System.out.println("false");
}

BigDecimal a1 = new BigDecimal("0.8");
BigDecimal b1 = new BigDecimal("0.7");
BigDecimal c1 = new BigDecimal("0.6");

if (a1.subtract(b1).equals(b1.subtract(c1))) {
    System.out.println("true"); // true
} else {
    System.out.println("false");
}
```

在实际的项目中，货币之类的精确表示使用整型来存储计算，表示上进行数制的互相转化。
