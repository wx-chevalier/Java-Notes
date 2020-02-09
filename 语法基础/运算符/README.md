# 运算符

计算机的最基本用途之一就是执行数学运算，作为一门计算机语言，Java 也提供了一套丰富的运算符来操纵变量。我们可以把运算符分成以下几组：

- 算术运算符
- 关系运算符
- 位运算符
- 逻辑运算符
- 赋值运算符
- 其他运算符

# 运算符优先级

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

## 左右结合性

所有的数学运算符都认为是从左到右运算的，Java 语言中大部分运算符也是从左到右结合的，只有单目运算符、赋值运算符和三目运算符例外，其中，单目运算符、赋值运算符和三目运算符是从右向左结合的，也就是从右向左运算。乘法和加法是两个可结合的运算，也就是说，这两个运算符左右两边的操作数可以互换位置而不会影响结果。当有多中运算符参与运算的时候，先要考虑优先级，有相同优先级的就看结合性以决定运算顺序。因为这样，所以，如果没有两个相同优先级的运算，就不存在考虑结合性的问题了。一个 **?:** 是体现不出来结合性的请看这个：

```
a?b:c?d:e
```

这个要怎么算？先看优先级，两个一样。再看结合性，右结合，所以先算：

```
c?d:e
```

再算:

```
 a?b:(c?d:e)
```

这就是所谓右结合。如果是左结合的话 就是先算:

```
a?b:c
```

再算:

```
(a?b:c)?d:e
```

实际上，一般结合性的问题都可以用括号来解决。

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

值得注意的是，在判断一个实例引用的类型时，使用的是实际类型，而不是声明的类型。在下面的代码中：

```java
Vehicle v2 = new Car();    // v2 是 Car 类型
```

v2 是 Car 类型，而不是 Vehicle 类型。

```java
class Vehicle {}

public class Car extends Vehicle {
    public static void main(String args[]){
        Car c1 = new Car();

        Vehicle v2 = new Car();    // v2 是 Car 类型
        Vehicle v3 = new Vehicle();

        //Car 是 Vehicle类型, Vehicle 不是 Car 类型
        boolean result1 =  c1 instanceof Vehicle;    // true
        boolean result2 =  v2 instanceof Car;        // true
        boolean result3 =  v2 instanceof Vehicle;    // true
        boolean result4 =  v3 instanceof Car;          // false

        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
        System.out.println(result4);
   }
}
```
