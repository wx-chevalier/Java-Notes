# Math

Java 的 Math 包含了用于执行基本数学运算的属性和方法，如初等指数、对数、平方根和三角函数。Math 的方法都被定义为 static 形式，通过 Math 类可以在主函数中直接调用。

```java
public class Test {
    public static void main (String []args)
    {
        System.out.println("90 度的正弦值：" + Math.sin(Math.PI/2));
        System.out.println("0度的余弦值：" + Math.cos(0));
        System.out.println("60度的正切值：" + Math.tan(Math.PI/3));
        System.out.println("1的反正切值：" + Math.atan(1));
        System.out.println("π/2的角度值：" + Math.toDegrees(Math.PI/2));
        System.out.println(Math.PI);
    }
}

/**
90 度的正弦值：1.0
0度的余弦值：1.0
60度的正切值：1.7320508075688767
1的反正切值：0.7853981633974483
π/2的角度值：90.0
3.141592653589793
**/
```

下面的表中列出的是 Math 类常用的一些方法：

| 序号 | 方法与描述                                                                                                                                                                                                        |
| :--- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 7    | [abs()](https://www.runoob.com/java/number-abs.html) 返回参数的绝对值                                                                                                                                             |
| 8    | [ceil()](https://www.runoob.com/java/number-ceil.html) 返回大于等于( >= )给定参数的的最小整数，类型为双精度浮点型                                                                                                 |
| 9    | [floor()](https://www.runoob.com/java/number-floor.html) 返回小于等于（<=）给定参数的最大整数                                                                                                                     |
| 10   | [rint()](https://www.runoob.com/java/number-rint.html) 返回与参数最接近的整数。返回类型为 double                                                                                                                  |
| 11   | [round()](https://www.runoob.com/java/number-round.html) 它表示**四舍五入**，算法为 **Math.floor(x+0.5)**，即将原来的数字加上 0.5 后再向下取整，所以，Math.round(11.5) 的结果为 12，Math.round(-11.5) 的结果为-11 |
| 12   | [min()](https://www.runoob.com/java/number-min.html) 返回两个参数中的最小值                                                                                                                                       |
| 13   | [max()](https://www.runoob.com/java/number-max.html) 返回两个参数中的最大值                                                                                                                                       |
| 14   | [exp()](https://www.runoob.com/java/number-exp.html) 返回自然数底数 e 的参数次方                                                                                                                                  |
| 15   | [log()](https://www.runoob.com/java/number-log.html) 返回参数的自然数底数的对数值                                                                                                                                 |
| 16   | [pow()](https://www.runoob.com/java/number-pow.html) 返回第一个参数的第二个参数次方                                                                                                                               |
| 17   | [sqrt()](https://www.runoob.com/java/number-sqrt.html) 求参数的算术平方根                                                                                                                                         |
| 18   | [sin()](https://www.runoob.com/java/number-sin.html) 求指定 double 类型参数的正弦值                                                                                                                               |
| 19   | [cos()](https://www.runoob.com/java/number-cos.html) 求指定 double 类型参数的余弦值                                                                                                                               |
| 20   | [tan()](https://www.runoob.com/java/number-tan.html) 求指定 double 类型参数的正切值                                                                                                                               |
| 21   | [asin()](https://www.runoob.com/java/number-asin.html) 求指定 double 类型参数的反正弦值                                                                                                                           |
| 22   | [acos()](https://www.runoob.com/java/number-acos.html) 求指定 double 类型参数的反余弦值                                                                                                                           |
| 23   | [atan()](https://www.runoob.com/java/number-atan.html) 求指定 double 类型参数的反正切值                                                                                                                           |
| 24   | [atan2()](https://www.runoob.com/java/number-atan2.html) 将笛卡尔坐标转换为极坐标，并返回极坐标的角度值                                                                                                           |
| 25   | [toDegrees()](https://www.runoob.com/java/number-todegrees.html) 将参数转化为角度                                                                                                                                 |
| 26   | [toRadians()](https://www.runoob.com/java/number-toradians.html) 将角度转换为弧度                                                                                                                                 |
| 27   | [random()](https://www.runoob.com/java/number-random.html) 返回一个随机数                                                                                                                                         |

# floor,round 和 ceil

**Math.floor** 是向下取整，**Math.ceil** 是向上取整，**Math.round** 是四舍五入取整：

- 1、参数的小数点后第一位小于 5，运算结果为参数整数部分。
- 2、参数的小数点后第一位大于 5，运算结果为参数整数部分绝对值 +1，符号（即正负）不变。
- 3、参数的小数点后第一位等于 5，正数运算结果为整数部分 +1，负数运算结果为整数部分。

通过下表可以看到各个方法的实例：

| 参数 | Math.floor | Math.round | Math.ceil |
| :--- | :--------- | :--------- | :-------- |
| 1.4  | 1          | 1          | 2         |
| 1.5  | 1          | 2          | 2         |
| 1.6  | 1          | 2          | 2         |
| -1.4 | -2         | -1         | -1        |
| -1.5 | -2         | -1         | -1        |
| -1.6 | -2         | -2         | -1        |

```java
public class Main {
  public static void main(String[] args) {
    double[] nums = { 1.4, 1.5, 1.6, -1.4, -1.5, -1.6 };
    for (double num : nums) {
      test(num);
    }
  }

  private static void test(double num) {
    System.out.println("Math.floor(" + num + ")=" + Math.floor(num));
    System.out.println("Math.round(" + num + ")=" + Math.round(num));
    System.out.println("Math.ceil(" + num + ")=" + Math.ceil(num));
  }
}

/**
Math.floor(1.4)=1.0
Math.round(1.4)=1
Math.ceil(1.4)=2.0
Math.floor(1.5)=1.0
Math.round(1.5)=2
Math.ceil(1.5)=2.0
Math.floor(1.6)=1.0
Math.round(1.6)=2
Math.ceil(1.6)=2.0
Math.floor(-1.4)=-2.0
Math.round(-1.4)=-1
Math.ceil(-1.4)=-1.0
Math.floor(-1.5)=-2.0
Math.round(-1.5)=-1
Math.ceil(-1.5)=-1.0
Math.floor(-1.6)=-2.0
Math.round(-1.6)=-2
Math.ceil(-1.6)=-1.0
**/
```
