# 数值类型

# Number

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

| 序号 | 方法与描述                                             |
| :--- | :----------------------------------------------------- |
| 1    | xxxValue() 将 Number 对象转换为 xxx 数据类型的值并返回 |
| 2    | compareTo() 将 number 对象与参数比较                   |
| 3    | equals()判断 number 对象是否与参数相等                 |
| 4    | valueOf() 返回一个 Number 对象指定的内置数据类型       |
| 5    | toString() 以字符串形式返回值                          |
| 6    | parseInt() 将字符串解析为 int 类型                     |
