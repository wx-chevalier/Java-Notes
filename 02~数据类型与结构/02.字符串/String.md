# Java 字符串详解

字符串广泛应用 在 Java 编程中，在 Java 中字符串属于对象，Java 提供了 String 类来创建和操作字符串。

# 创建增删

创建字符串最简单的方式如下:

```java
String greeting = "Greeting";
```

和其它对象一样，可以使用关键字和构造方法来创建 String 对象。String 类有 11 种构造方法，这些方法提供不同的参数来初始化字符串，比如提供一个字符数组参数:

```java
public class StringDemo{
   public static void main(String args[]){
      char[] helloArray = { 'r', 'u', 'n', 'o', 'o', 'b'};
      String helloString = new String(helloArray);
      System.out.println( helloString );
   }
}
```

注意，String 类是不可改变的，所以你一旦创建了 String 对象，那它的值就无法改变了。

## 字符串转义

```java
点的转义：. ==> u002E
美元符号的转义：\$ ==> u0024
乘方符号的转义：^ ==> u005E
左大括号的转义：{ ==> u007B
左方括号的转义：[ ==> u005B
左圆括号的转义：( ==> u0028
竖线的转义：| ==> u007C
右圆括号的转义：) ==> u0029
星号的转义：\* ==> u002A
加号的转义：+ ==> u002B
问号的转义：? ==> u003F
反斜杠的转义: ==> u005C
```

譬如我们如果需要从 System.in 中输入 `"C:\"`，在 Java 中的字符串表示的是: `"C:\\"`，而如果要用正则表达式匹配 `"\"` 这个字符的时候，正则表达式要写成 `"\\\\"`，即首先是根据 Java 语言本身的转义字符，转化为普通字符中的 `"\\"`，其就等价于正则表达式中匹配 `"\"` 这个字符。

## 模板字符串

```java
String.format("%s 今年%d 岁","我", "24");
MessageFormat.format("{0}  今年{1} 岁", "我",24);
```

# 索引遍历

## Split | 截取分割

split 方法的结果是一个字符串数组，在 stingObj 中每个出现 separator 的位置都要进行分解：

```java
stringObj.split([separator，[limit]])

// 根据转义字符进行分割
String.split("\\.")
String.split("\\|")

// 使用正则表达式
```

# 字符串操作

## 子字符串

substring(beginIndex,endIndex)，即截取的是 `[beginIndex,endIndex-1]` 这样的字符串。

```java
System.out.println("abcd".substring(0,1));
```

## Compare | 字符串比较

# 不可变性

字符串实际上就是一个 char 数组，并且内部就是封装了一个 char 数组。并且这里 char 数组是被 final 修饰的:

```java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {
    /** The value is used for character storage. */
    private final char value[];
```

并且 String 中的所有的方法，都是对于 char 数组的改变，只要是对它的改变，方法内部都是返回一个新的 String 实例。

## intern

`String.intern()` 方法可以用来处理在 Java 中字符串的重复问题，通过使用 `intern()` 方法，可以节省大量由重复字符串对象消耗的堆内存。如果一个字符串对象包含与另一个字符串相同的内容，但是占用了不同的内存位置，例如 `str1 != str2` 但 `str1.equals(str2) true`，则称其为重复。由于 String 对象在普通 Java 应用程序中消耗大量堆内存，因此使用 `intern()` 方法减少重复,也可以使用 `intern()` 方法实例化 String 对象并将它们存储到 String pool 中以便进一步重用。

![](https://s2.ax1x.com/2019/11/30/QV6461.png)

通过在此对象上调用 `intern()` 方法，可以指示 JVM 将此放入 String pool 中，并且每当其他人创建 abc 时，将返回此对象而不是创建新对象。

```java
public class Test {
    public static void main(String[] args) {
        String s1 = "Test";
        String s2 = s1.intern();
        String s3 = new String("Test");
        String s4 = s3.intern();
        System.out.println(s1==s2); // True
        System.out.println(s1==s3); // False
        System.out.println(s1==s4); // True
        System.out.println(s2==s3); // False
        System.out.println(s2==s4); // True
        System.out.println(s3==s4); // False
    }
}
```

# Links

- [How-the-JVM-compares-your-strings](http://jcdav.is/2016/09/01/How-the-JVM-compares-your-strings/)

- [Java String 对 null 对象的巧妙处理](http://blog.xiaohansong.com/2016/03/13/null-in-java-string/)
