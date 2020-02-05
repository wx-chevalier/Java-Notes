# 类型基础

变量就是申请内存来存储值。也就是说，当创建变量的时候，需要在内存中申请空间。内存管理系统根据变量的类型为变量分配存储空间，分配的空间只能用来储存该类型数据。

![代码到内存](https://s2.ax1x.com/2020/02/04/1DmG11.png)

Java 中有两种类型，原始类型（Primitive Type）会被直接映射到 CPU 的基础类型，引用类型（Reference Type）则指向了内存中的对象。

- 原始类型：boolean，char，byte，short，int，long，float，double。

- 包装类型：Boolean，Character，Byte，Short，Integer，Long，Float，Double。

# 原始类型

Java 语言提供了八种基本类型。六种数字类型（四个整数型，两个浮点型），一种字符类型，还有一种布尔型。Java 的基础类型包含了如下几种：

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

实际上，Java 中还存在另外一种基本类型 void，它也有对应的包装类 java.lang.Void，不过我们无法直接对它们进行操作。

## byte

- byte 数据类型是 8 位、有符号的，以二进制补码表示的整数；
- 最小值是 **-128（-2^7）**；
- 最大值是 **127（2^7-1）**；
- 默认值是 **0**；
- byte 类型用在大型数组中节约空间，主要代替整数，因为 byte 变量占用的空间只有 int 类型的四分之一；
- 例子：byte a = 100，byte b = -50。

计算机是用二进制来表示数据的，一个字节也就是 8 个比特位，其中最高位表示符号位（0 正 1 负），故 byte 的取值范围为 1000 0000 到 0111 1111。在 Java 中，是采用补码来表示数据的，正数的补码和原码相同，负数的补码是在原码的基础上各位取反然后加 1。

1000 000 是补码，减一然后按位取反得到其原码 1000 0000。（减一得 0111 1111，再按位取反得 1000 0000）。因为是负数，所以最小的 byte 值为 -2^7=-128。0111 1111 的十进制为 2^7-1=127（等比序列求和）。byte 是一个字节，共有 2^8=256 种可能性，也就是 -128~127。

其他基本数据类型同理，char 没有负值，占两个字节，所以取值范围是 0~2^16-1（65535）。

## short

- short 数据类型是 16 位、有符号的以二进制补码表示的整数
- 最小值是 **-32768（-2^15）**；
- 最大值是 **32767（2^15 - 1）**；
- Short 数据类型也可以像 byte 那样节省空间。一个 short 变量是 int 型变量所占空间的二分之一；
- 默认值是 **0**；
- 例子：short s = 1000，short r = -20000。

## int

- int 数据类型是 32 位、有符号的以二进制补码表示的整数；
- 最小值是 **-2,147,483,648（-2^31）**；
- 最大值是 **2,147,483,647（2^31 - 1）**；
- 一般地整型变量默认为 int 类型；
- 默认值是 **0** ；
- 例子：int a = 100000, int b = -200000。

## long

- long 数据类型是 64 位、有符号的以二进制补码表示的整数；
- 最小值是 **-9,223,372,036,854,775,808（-2^63）**；
- 最大值是 **9,223,372,036,854,775,807（2^63 -1）**；
- 这种类型主要使用在需要比较大整数的系统上；
- 默认值是 **0L**；
- 例子：long a = 100000L，Long b = -200000L。

"L"理论上不分大小写，但是若写成"l"容易与数字"1"混淆，不容易分辩。所以最好大写。

## float

- float 数据类型是单精度、32 位、符合 IEEE 754 标准的浮点数；
- float 在储存大型浮点数组的时候可节省内存空间；
- 默认值是 **0.0f**；
- 浮点数不能用来表示精确的值，如货币；
- 例子：float f1 = 234.5f。

## double

- double 数据类型是双精度、64 位、符合 IEEE 754 标准的浮点数；
- 浮点数的默认类型为 double 类型；
- double 类型同样不能表示精确的值，如货币；
- 默认值是 **0.0d**；
- 例子：double d1 = 123.4。

## boolean

- boolean 数据类型表示一位的信息；
- 只有两个取值：true 和 false；
- 这种类型只作为一种标志来记录 true/false 情况；
- 默认值是 **false**；
- 例子：boolean one = true。

## char

- char 类型是一个单一的 16 位 Unicode 字符；
- 最小值是 **\u0000**（即为 0）；
- 最大值是 **\uffff**（即为 65,535）；
- char 数据类型可以储存任何字符；
- 例子：char letter = 'A';。

## 类型默认值

对于数值类型的基本类型的取值范围，我们无需强制去记忆，因为它们的值都已经以常量的形式定义在对应的包装类中了。请看下面的例子：

```java
public class PrimitiveTypeTest {
    public static void main(String[] args) {
        // byte
        System.out.println("基本类型：byte 二进制位数：" + Byte.SIZE);
        System.out.println("包装类：java.lang.Byte");
        System.out.println("最小值：Byte.MIN_VALUE=" + Byte.MIN_VALUE);
        System.out.println("最大值：Byte.MAX_VALUE=" + Byte.MAX_VALUE);
        System.out.println();

        // short
        System.out.println("基本类型：short 二进制位数：" + Short.SIZE);
        System.out.println("包装类：java.lang.Short");
        System.out.println("最小值：Short.MIN_VALUE=" + Short.MIN_VALUE);
        System.out.println("最大值：Short.MAX_VALUE=" + Short.MAX_VALUE);
        System.out.println();

        // int
        System.out.println("基本类型：int 二进制位数：" + Integer.SIZE);
        System.out.println("包装类：java.lang.Integer");
        System.out.println("最小值：Integer.MIN_VALUE=" + Integer.MIN_VALUE);
        System.out.println("最大值：Integer.MAX_VALUE=" + Integer.MAX_VALUE);
        System.out.println();

        // long
        System.out.println("基本类型：long 二进制位数：" + Long.SIZE);
        System.out.println("包装类：java.lang.Long");
        System.out.println("最小值：Long.MIN_VALUE=" + Long.MIN_VALUE);
        System.out.println("最大值：Long.MAX_VALUE=" + Long.MAX_VALUE);
        System.out.println();

        // float
        System.out.println("基本类型：float 二进制位数：" + Float.SIZE);
        System.out.println("包装类：java.lang.Float");
        System.out.println("最小值：Float.MIN_VALUE=" + Float.MIN_VALUE);
        System.out.println("最大值：Float.MAX_VALUE=" + Float.MAX_VALUE);
        System.out.println();

        // double
        System.out.println("基本类型：double 二进制位数：" + Double.SIZE);
        System.out.println("包装类：java.lang.Double");
        System.out.println("最小值：Double.MIN_VALUE=" + Double.MIN_VALUE);
        System.out.println("最大值：Double.MAX_VALUE=" + Double.MAX_VALUE);
        System.out.println();

        // char
        System.out.println("基本类型：char 二进制位数：" + Character.SIZE);
        System.out.println("包装类：java.lang.Character");
        // 以数值形式而不是字符形式将Character.MIN_VALUE输出到控制台
        System.out.println("最小值：Character.MIN_VALUE="
                + (int) Character.MIN_VALUE);
        // 以数值形式而不是字符形式将Character.MAX_VALUE输出到控制台
        System.out.println("最大值：Character.MAX_VALUE="
                + (int) Character.MAX_VALUE);
    }
}
```

下表列出了 Java 各个类型的默认值：

| **数据类型**           | **默认值** |
| :--------------------- | :--------- |
| byte                   | 0          |
| short                  | 0          |
| int                    | 0          |
| long                   | 0L         |
| float                  | 0.0f       |
| double                 | 0.0d       |
| char                   | 'u0000'    |
| String (or any object) | null       |
| boolean                | false      |

# 引用类型

在 Java 中，引用类型的变量非常类似于 C/C++的指针。引用类型指向一个对象，指向对象的变量是引用变量。这些变量在声明时被指定为一个特定的类型，比如 Employee、Puppy 等。变量一旦声明后，类型就不能被改变了。对象、数组都是引用数据类型，所有引用类型的默认值都是 null；一个引用变量可以用来引用任何与之兼容的类型。

例子：Site site = new Site("Runoob")。
