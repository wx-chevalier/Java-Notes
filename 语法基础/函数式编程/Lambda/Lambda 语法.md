# Lambda 表达式语法

Lambda 表达式的基本形式为：`(Parameters) -> {Body}`，参数用括号括起来，这和方法的方法是一样的，而 Lambda 表达式主体是用括号括起来的代码块。Lambda 表达式体中可以有局部变量、语句。我们可以在 Lambda 表达式体中使用 break、continue、return；我们甚至可以从 Lambda 表达式体中抛出异常。但是 Lambda 表达式没有名字，因为它代表匿名的内部类，Lambda 表达式的返回类型是由编译器推断的。一个 Lambda 表达式不能像方法一样有一个 throws 子句，且它不能是泛型的，而泛型是在函数接口中定义的。

# 显示与隐式声明

## 参数

一个没有声明其参数类型的 Lambda 表达式被称为隐式 Lambda 表达式。显式 Lambda 表达式是指声明了参数类型的 Lambda 表达式，对于隐式 Lambda 表达式，编译器会推断其参数类型。

```java
public class Main {
  public static void main(String[] args) {
    // 显式
    MyIntegerCalculator myIntegerCalculator = (Integer s1) -> s1 * 2;
    // 隐式
    MyIntegerCalculator myIntegerCalculator = (s1) -> s1 * 2;

    System.out.println("1- Result x2 : " + myIntegerCalculator.calcIt(5));

  }
}
interface MyIntegerCalculator {
  public Integer calcIt(Integer s1);
}
```

我们可以选择省略 Lambda 表达式中的参数类型，如果我们选择省略参数的类型，我们必须省略所有参数的类型。

```java
public class Main {
  public static void main(String[] argv) {
    Processor stringProcessor = (str) -> str.length();
    String name = "Java Lambda";
    int length = stringProcessor.getStringLength(name);
    System.out.println(length);//from   ww w.ja  v a 2  s .  c  om

  }
}

@FunctionalInterface
interface Processor {
  int getStringLength(String str);
}
```

对于单参数 Lambda 表达式，我们可以省略括号，因为我们省略了参数类型。

```java
public class Main {
  public static void main(String[] argv) {
    Processor stringProcessor = str -> str.length();
    String name = "Java Lambda";
    int length = stringProcessor.getStringLength(name);
    System.out.println(length);/*  w  w  w. jav  a2s  .c  o  m*/

  }
}

@FunctionalInterface
interface Processor {
  int getStringLength(String str);
}
```

对于一个没有参数的 Lambda 表达式，我们仍然需要括号。

```java
import java.util.function.BooleanSupplier;

public class Main {
  public static void main(String[] args) {
    BooleanSupplier bs = () -> true;
    System.out.println(bs.getAsBoolean());

    int x = 0, y= 1;
    bs = () -> x > y;
    System.out.println(bs.getAsBoolean());
  }
}
```

你可以在参数声明中使用 final 修饰符来表达显式 lambda。

```java
public class Main {
  public static void main(String[] argv) {
    Processor stringProcessor = (final String str) -> str.length();
    String name = "Java Lambda";
    int length = stringProcessor.getStringLength(name);
    System.out.println(length);/* w w  w . ja  v  a  2s .co m*/

  }
}

@FunctionalInterface
interface Processor {
  int getStringLength(String str);
}
```

## 函数体

Lambda 表达式主体可以是一个块状语句，也可以是一个单一表达式。块语句用大括号括起来，而单个表达式可以没有大括号。lambda 是没有必要返回一个值的。下面两个 Lambda 表达式只是将参数输出到标准输出，并不返回任何东西。

```java
(String msg)->{System.out.println(msg);}// a  block   statement
(String msg)->System.out.println(msg)   //an expression

public class Main {
  public static void main(String[] argv) {
    Processor stringProcessor = (String str) -> str.length();
    String name = "Java Lambda";
    int length = stringProcessor.getStringLength(name);
    System.out.println(length);// www . j a  va 2  s. co m

  }
}

@FunctionalInterface
interface Processor {
  int getStringLength(String str);
}
```

# 类型推导

一个 Lambda 表达式代表一个功能接口的实例。一个 Lambda 表达式可以根据上下文映射到不同的功能接口类型。编译器会推断 Lambda 表达式的类型。

```java
public class Main {
  public static void main(String[] argv) {
    Processor stringProcessor = (String str) -> str.length();
    SecondProcessor secondProcessor = (String str) -> str.length();
    //stringProcessor = secondProcessor; //compile error
    String name = "Java Lambda";
    int length = stringProcessor.getStringLength(name);
    System.out.println(length);

  }
}

@FunctionalInterface
interface Processor {
  int getStringLength(String str);
}

@FunctionalInterface
interface SecondProcessor {
  int noName(String str);
}
```

Processor 或 SecondProcessor 称为目标类型。推断 Lambda 表达式类型的过程称为目标类型。编译器使用以下规则来确定一个 Lambda 表达式是否可以分配到它的目标类型。

- 它必须是一个函数接口
- Lambda 表达式的参数必须与函数式接口中的抽象方法相匹配。
- Lambda 表达式的返回类型与函数式接口中的抽象方法的返回类型兼容。
- 从 Lambda 表达式中抛出的检查异常必须与函数式接口中抽象方法的声明的 throws 子句兼容。

编译器并不总是能够推断出 Lambda 表达式的类型。其中一种情况是将 Lambda 表达式传递给重载方法。在下面的代码中，有两个功能接口。一个是用于 int 值计算，另一个是用于 long 值。在 Main 类中，有称为 engine 的重载方法。一个是期望 IntCalculator，另一个是用于 LongCalculator。在 main 类中，我们必须指明 Lambda 表达式的参数，以指示编译器我们要使用哪个重载函数。

```java
public class Main {
  public static void main(String[] argv) {

    // 指明参数
    engine((IntCalculator) ((x,y)-> x + y));

    // 指明类型
    IntCalculator iCal = (x,y)-> x + y;

    engine((int x,int y)-> x + y);
    engine((long x, long y)-> x * y);
    engine((int x,int y)-> x / y);
    engine((long x,long y)-> x % y);
  }

  private static void engine(IntCalculator calculator){
    int x = 2, y = 4;
    int result = calculator.calculate(x,y);
    System.out.println(result);
  }

  private static void engine(LongCalculator calculator){
    long x = 2, y = 4;
    long result = calculator.calculate(x,y);
    System.out.println(result);
  }
}

@FunctionalInterface
interface IntCalculator{
  int calculate(int x, int y);
}

@FunctionalInterface
interface LongCalculator{
  long calculate(long x, long y);
}
```

# 方法引用

一个 Lambda 表达式表示一个在函数接口中定义的匿名函数，方法引用使用现有方法创建一个 Lambda 表达式。方法引用的一般语法是 `Qualifier::MethodName` 两个连续的冒号作为分隔符。MethodName 是方法的名称，Qualifier 告诉在哪里可以找到方法的引用。方法参考有六种类型。

- TypeName::staticMethod - 类、接口或枚举的静态方法的引用。
- objectRef::instanceMethod - 对实例方法的引用
- ClassName::instanceMethod - 引用类的实例方法
- TypeName.super::instanceMethod - 从对象的超类型中引用一个实例方法。
- ClassName::new - 类的构造函数的引用
- ArrayTypeName::new - 引用指定数组类型的构造函数。

## Static Method References

静态方法引用允许我们使用静态方法作为 lambda 表达式。静态方法可以定义在一个类、一个接口或一个枚举中。下面的代码定义了两个 lambda 表达式。第一个 lambda 表达式 func1 是通过定义一个输入参数 x 并提供 lambda 表达式 body 来创建的。基本上这是创建 lambda 表达式的正常方式。第二个 lambda 表达式 func2 是通过引用 Integer 类的静态方法创建的。

```java
import java.util.function.Function;

public class Main {
  public static void main(String[] argv) {
    // Using  a  lambda  expression
    Function<Integer, String> func1  = x -> Integer.toBinaryString(x);
    System.out.println(func1.apply(10));

    // Using  a  method  reference
    Function<Integer, String> func2  = Integer::toBinaryString;
    System.out.println(func2.apply(10));
  }
}
```

我们可以在静态方法引用中使用重载静态方法。在重载方法的时候我们要多注意方法的签名和对应的功能接口。在下面的列表中，我们有三个版本的来自 Integer 类的 valueOf()。

```java
static Integer valueOf(int i)
static Integer valueOf(String s)
static Integer valueOf(String s, int radix)
```

下面的代码显示了不同的目标功能接口如何与重载的 Integer.valueOf()静态方法一起使用。

```java
import java.util.function.BiFunction;
import java.util.function.Function;
public class Main{

  public static void main(String[] argv){
    // Uses  Integer.valueOf(int)
    Function<Integer, Integer> func1  = Integer::valueOf;

    // Uses  Integer.valueOf(String)
    Function<String, Integer> func2  = Integer::valueOf;

    // Uses  Integer.valueOf(String, int)
    BiFunction<String, Integer,  Integer> func3  = Integer::valueOf;

    System.out.println(func1.apply(7));
    System.out.println(func2.apply("7"));
    System.out.println(func3.apply("101010101010", 2));
  }
}
```

## 实例方法引用

我们可以通过两种方式获取实例方法引用，从对象实例或从类名中获取。基本上我们有以下两种形式：instance::MethodName, ClassName::MethodName。这里的 instance 代表任何对象实例。ClassName 是类的名称，如 String、Integer。`instance`和 ClassName 被称为接收器。更具体地说，`instance` 被称为有界接受者，而 ClassName 被称为无界接受者，我们称 `instance` 为有界接受者，因为接受者与 `instance` 是有界的，而 ClassName 为无界接受者，因为接受者是后来才有界的。

### Bound Instance Method Reference

在他们下面的代码中，我们使用构建系统功能接口 Supplier 作为 lambda 表达式类型。首先我们以正常的方式定义一个 lambda 表达式。lambda 表达式不接受任何参数，并返回一个字符串的长度，然后我们创建一个 String 实例，并使用其 length 方法作为实例方法引用。Bound 意味着我们已经指定了实例。下面的例子展示了如何使用绑定接收器和无参数的方法来创建实例方法引用。

```java
import java.util.function.Supplier;

public class Main{
  public static void main(String[] argv){
    Supplier<Integer> supplier  = () ->  "test".length();
    System.out.println(supplier.get());


    Supplier<Integer> supplier1  = "test"::length;
    System.out.println(supplier1.get());
  }
}
```

下面的例子展示了如何使用绑定接收器和带参数的方法来创建实例方法引用。

```java
import java.util.function.Consumer;

public class Main{
  public static void main(String[] argv){
    Util util = new Util();

    Consumer<String> consumer  = str ->  util.print(str);
    consumer.accept("Hello");


    Consumer<String> consumer1  = util::print;
    consumer1.accept("test");

    util.debug();
  }
}
class Util{
  private int count=0;
  public void print(String s){
    System.out.println(s);
    count++;
  }
  public void debug(){
    System.out.println("count:" + count);
  }
}
```

### Unbound Instance Method Reference

一个未绑定的接收器使用以下语法：`ClassName::instanceMethod`，这与我们用来引用静态方法的语法相同。从下面的代码中我们可以看到，输入类型是 ClassName 的类型。在下面的代码中，我们使用了 `String:length`，所以功能接口的输入类型是 String。Lambda 表达式在使用的时候，得到的是输入。下面的代码使用 String length 方法作为 unbind 实例方法引用。String length 方法通常在字符串值实例上调用，并返回字符串实例的长度。因此输入是 String 类型，输出是 int 类型，这与 Buildin Function 功能接口相匹配。我们每次调用 strLengthFunc 都会传入一个字符串值，长度方法就会从传入的字符串值中调用。

```java
import java.util.function.Function;

public class Main{
  public static void main(String[] argv){
    Function<String,  Integer> strLengthFunc = String::length;
    String name ="test";
    int len = strLengthFunc.apply(name);
    System.out.println("name  = "  +  name + ", length = "  + len);

    name ="testtest";
    len = strLengthFunc.apply(name);
    System.out.println("name  = "  +  name + ", length = "  + len);

  }
}
```

下面的代码定义了一个带有静态方法 append 的类 Util。append 方法接受两个 String 类型的参数，并返回一个 String 类型的结果，然后使用 append 方法创建一个 lambda 表达式，并分配给 Java buildin BiFunction 函数接口。然后 append 方法被用来创建一个 lambda 表达式，并分配给 Java buildin BiFunction 函数接口。append 方法的签名与 BiFunction 功能接口中定义的抽象方法的签名一致。

```java
import java.util.function.BiFunction;

public class Main{
  public static void main(String[] argv){
    BiFunction<String, String, String> strFunc = Util::append;
    String name = "test";
    String s = strFunc.apply(name, "hi");
    System.out.println(s);
  }
}
class Util{
  public static String append(String s1,String s2){
    return s1+s2;
  }
}
```

### Supertype Instance Method References

关键字 super 只在实例上下文中使用，它引用的是被覆盖的方法。我们可以使用下面的语法来创建一个方法引用，引用父类型中的实例方法：`ClassName.super::instanceMethod`。下面的代码定义了一个名为 ParentUtil 的父类。在 ParentUtil 中，有一个名为 append 的方法，它将两个 String 值追加在一起。

然后创建了一个名为 Util 的子类并扩展了 ParentUtil。在 Util 类中，append 方法被覆盖。在 Util 的构造函数中，我们创建了两个 lambda 表达式，一个是使用 Util 的 append 方法，另一个是使用 ParentUtil 类的 append 方法。我们使用 this::append 来引用当前类，而使用 Util.super::append 来引用父类的方法。

```java
import java.util.function.BiFunction;

public class Main{
  public static void main(String[] argv){
    new Util();
  }
}
class Util extends ParentUtil{

  public Util(){
    BiFunction<String,  String,String> strFunc = this::append;
    String name ="test";
    String s=  strFunc.apply(name," hi");
    System.out.println(s);

    strFunc = Util.super::append;
    name ="test";
    s=  strFunc.apply(name," Java Lambda Tutorial");
    System.out.println(s);

  }

  @Override
  public String append(String s1,String s2){
    System.out.println("child append");
    return s1+s2;
  }
}
class ParentUtil{
  public String append(String s1,String s2){
    System.out.println("parent append");
    return s1+s2;
  }
}
```

## Constructor Reference

我们可以使用构造函数来创建一个 Lambda 表达式。使用构造函数引用的语法是：`ClassName::new`。关键字 new 指的是类的构造函数。编译器根据上下文选择构造函数。

```java
import java.util.function.Function;
import java.util.function.Supplier;

public class Main{
  public static void main(String[] argv){
    Supplier<String> func1  = () ->  new String();
    System.out.println("Empty String:"+func1.get());

    Function<String,String> func2  = str ->  new String(str);

    System.out.println(func2.apply("test"));

    Supplier<String> func3  = String::new;
    System.out.println("Empty String:"+func3.get());

    Function<String,String> func4  = String::new;
    System.out.println(func4.apply("test"));
  }
}
```

### Array Constructor References

我们可以使用数组构造函数创建一个数组，如下所示：ArrayTypeName::new。int[]::new 是调用 new int[]，new int[]需要一个 int 类型的值作为数组长度，因此 int[]:new 需要一个 int 类型的输入值。下面的代码使用数组构造函数引用来创建一个 int 数组。

```java
import java.util.Arrays;
import java.util.function.IntFunction;

public class Main{
  public static void main(String[] argv){
    IntFunction<int[]> arrayCreator1 = size ->  new int[size];
    // Creates an  int array of  five  elements
    int[] intArray1  = arrayCreator1.apply(5);
    System.out.println(Arrays.toString(intArray1));

    IntFunction<int[]> arrayCreator2 = int[]::new;
    int[] intArray2 = arrayCreator2.apply(5);
    System.out.println(Arrays.toString(intArray2));
  }
}
```

通过使用 `Function<Integer,ArrayType>`，我们可以在声明中指定数组类型。

```java
import java.util.Arrays;
import java.util.function.Function;

public class Main{
  public static void main(String[] argv){
    Function<Integer, int[]>  arrayCreator3 = int[]::new;
    int[] intArray  = arrayCreator3.apply(5);
    System.out.println(Arrays.toString(intArray));
  }
}
```

在创建二维数组时，我们可以指定第一维的长度。

```java
import java.util.Arrays;
import java.util.function.IntFunction;

public class Main{
  public static void main(String[] argv){
    IntFunction<int[][]> TwoDimArrayCreator  = int[][]::new;
    int[][] intArray = TwoDimArrayCreator.apply(5);
    // Creates an  int[5][]  array
    intArray[0] = new int[5];
    intArray[1] = new int[5];
    intArray[2] = new int[5];
    intArray[3] = new int[5];
    intArray[4] = new int[5];

    System.out.println(Arrays.deepToString(intArray));
  }
}
```

## Generic Method Reference

我们可以在方法引用中通过指定实际类型参数来使用通用方法。其语法如下：`ClassName::<TypeName>methodName`，通用构造函数引用的语法：`ClassName<TypeName>::new`。

```java
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Main{
  public static void main(String[] argv){
    Function<String[],List<String>> asList = Arrays::<String>asList;

    System.out.println(asList.apply(new String[]{"a","b","c"}));
  }
}
```
