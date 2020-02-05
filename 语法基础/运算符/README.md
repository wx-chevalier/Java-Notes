# 运算符

计算机的最基本用途之一就是执行数学运算，作为一门计算机语言，Java 也提供了一套丰富的运算符来操纵变量。我们可以把运算符分成以下几组：

- 算术运算符
- 关系运算符
- 位运算符
- 逻辑运算符
- 赋值运算符
- 其他运算符

当多个运算符出现在一个表达式中，谁先谁后呢？这就涉及到运算符的优先级别的问题。在一个多运算符的表达式中，运算符优先级不同会导致最后得出的结果差别甚大。例如，`（1+3）＋（3+2）*2`，这个表达式如果按加号最优先计算，答案就是 18，如果按照乘号最优先，答案则是 14。再如，`x = 7 + 3 * 2`;这里 x 得到 13，而不是 20，因为乘法运算符比加法运算符有较高的优先级，所以先计算`3 * 2`得到 6，然后再加 7。下表中具有最高优先级的运算符在的表的最上面，最低优先级的在表的底部。

| 类别     | 操作符                                       | 关联性   |
| :------- | :------------------------------------------- | :------- |
| 后缀     | () [] . (点操作符)                           | 左到右   |
| 一元     | + + - ！〜                                   | 从右到左 |
| 乘性     | `*` /％                                      | 左到右   |
| 加性     | + -                                          | 左到右   |
| 移位     | >> >>> <<                                    | 左到右   |
| 关系     | >> = << =                                    | 左到右   |
| 相等     | == !=                                        | 左到右   |
| 按位与   | ＆                                           | 左到右   |
| 按位异或 | ^                                            | 左到右   |
| 按位或   | \|                                           | 左到右   |
| 逻辑与   | &&                                           | 左到右   |
| 逻辑或   | \| \|                                        | 左到右   |
| 条件     | ？：                                         | 从右到左 |
| 赋值     | = + = - = `*` = / =％= >> = << =＆= ^ = \| = | 从右到左 |
| 逗号     | ，                                           | 左到右   |

# 条件运算符

条件运算符也被称为三元运算符。该运算符有 3 个操作数，并且需要判断布尔表达式的值。该运算符的主要是决定哪个值应该赋值给变量。

```java
variable x = (expression) ? value if true : value if false
```

```java
public class Test {
   public static void main(String[] args){
      int a , b;
      a = 10;
      // 如果 a 等于 1 成立，则设置 b 为 20，否则为 30
      b = (a == 1) ? 20 : 30;
      System.out.println( "Value of b is : " +  b );

      // 如果 a 等于 10 成立，则设置 b 为 20，否则为 30
      b = (a == 10) ? 20 : 30;
      System.out.println( "Value of b is : " + b );
   }
}

/**
Value of b is : 30
Value of b is : 20
**/
```

# instanceof 运算符

该运算符用于操作对象实例，检查该对象是否是一个特定类型（类类型或接口类型）。instanceof 运算符使用格式如下：

```java
( Object reference variable ) instanceof  (class/interface type)
```

如果运算符左侧变量所指的对象，是操作符右侧类或接口(class/interface)的一个对象，那么结果为真。下面是一个例子：

```java
String name = "James";
boolean result = name instanceof String; // 由于 name 是 String 类型，所以返回真
```

如果被比较的对象兼容于右侧类型,该运算符仍然返回 true。看下面的例子：

```java
class Vehicle {}

public class Car extends Vehicle {
   public static void main(String[] args){
      Vehicle a = new Car();
      boolean result =  a instanceof Car;
      System.out.println( result);
   }
}

// true
```
