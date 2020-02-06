# 类与对象

类是一个模板，它描述一类对象的行为和状态。对象是类的一个实例（对象不是找个女朋友），有状态和行为。例如，一条狗是一个对象，它的状态有：颜色、名字、品种；行为有：摇尾巴、叫、吃等。看看周围真实的世界，会发现身边有很多对象，车，狗，人等等。所有这些对象都有自己的状态和行为。

拿一条狗来举例，它的状态有：名字、品种、颜色，行为有：叫、摇尾巴和跑。对比现实对象和软件对象，它们之间十分相似。软件对象也有状态和行为。软件对象的状态就是属性，行为通过方法体现。在软件开发中，方法操作对象内部状态的改变，对象的相互调用也是通过方法来完成。

Java 作为一种面向对象语言，支持以下基本概念：

- 多态
- 继承
- 封装
- 抽象
- 类
- 对象
- 实例
- 方法
- 重载

# 变量

类可以看成是创建 Java 对象的模板。通过下面一个简单的类来理解下 Java 中类的定义：

```java
public class Dog {
  String breed;
  int age;
  String color;

  void barking() {}

  void hungry() {}

  void sleeping() {}
}
```

一个类可以包含以下类型变量：

- **局部变量**：在方法、构造方法或者语句块中定义的变量被称为局部变量。变量声明和初始化都是在方法中，方法结束后，变量就会自动销毁。

```java
public class  ClassName{
    public void printNumber（）{
        int a;
    }
    // 其他代码
}
```

- **成员变量**：成员变量是定义在类中，方法体之外的变量。这种变量在创建对象的时候实例化。成员变量可以被类中方法、构造方法和特定类的语句块访问。

```java
public class  ClassName{
    int a;
    public void printNumber（）{
        // 其他代码
    }
}
```

- **类变量**：类变量也声明在类中，方法体之外，但必须声明为 static 类型。静态成员属于整个类，可通过对象名或类名来调用。

```java
public class  ClassName{
    static int a;
    public void printNumber（）{
        // 其他代码
    }
}
```

一个类可以拥有多个方法，在上面的例子中：barking()、hungry()和 sleeping()都是 Dog 类的方法。

|          | ** 成员变量**  | **局部变量**              | **静态变量**       |
| -------- | -------------- | ------------------------- | ------------------ |
| 定义位置 | 在类中,方法外  | 方法中,或者方法的形式参数 | 在类中,方法外      |
| 初始化值 | 有默认初始化值 | 无,先定义,赋值后才能使用  | 有默认初始化值     |
| 调用方式 | 对象调用       | ---                       | 对象调用，类名调用 |
| 存储位置 | 堆中           | 栈中                      | 方法区             |
| 生命周期 | 与对象共存亡   | 与方法共存亡              | 与类共存亡         |
| 别名     | 实例变量       | ---                       | 类变量             |

```java
package hello;

//首先要知道变量应该是赋值以后才能使用的，但是有些不必人为赋值就有默认初始值，但是有些要人为定义初始值
//所以有些直接使用的并不是没有赋值，而是系统自定义了初始值，所以不会报错

public class Variable {
    public String instance = "实例变量";
    public static String variable = "静态变量";    //静态变量的定义方式
    public static String CONST = "静态常量";    //静态常量的定义方式
    public static void main(String[] args) {
        String local = "局部变量";    //类似这个就是局部变量，不可用访问修饰符修饰，没有默认初始值
        System.out.println(local);    //局部变量就是在方法或语句块中的变量
        Global global = new Global();    //类似这个就是实例变量，也称全局变量
        System.out.println(global.instance);    //实例变量就必须先把类new一个出来才能使用,因为他是在类中,方法外的
        System.out.println(variable);    //来瞅瞅静态变量，也叫类变量，静态变量的访问方式1(在自己类的时候)
        System.out.println(Global.variable);    //静态变量的访问方法2(不在自己类的时候)
    }
}

class Global{
    public String instance = "实例变量";    //实例变量在一个类的里面,语句块的外面
    public static String variable = "静态变量";
    Global(){    //在类的内部使用自己的实例变量：要么老老实实new一个出来,就像上面那个
        //第二种方法就是在函数里面使用实例变量，注意，如果方法是静态方法参照方法1
        System.out.println(instance);
        System.out.println(variable);
    }
    public void Instance() {
        System.out.println(instance);    //静态变量访问方法1(在自己类的时候),静态常量和静态变量访问相同
        System.out.println(Variable.CONST);
    }
}
```

# finalize() 方法

Java 允许定义这样的方法，它在对象被垃圾收集器析构(回收)之前调用，这个方法叫做 finalize()，它用来清除回收对象。例如，你可以使用 finalize() 来确保一个对象打开的文件被关闭了。在 finalize() 方法里，你必须指定在对象销毁时候要执行的操作。

finalize() 一般格式是：

```java
protected void finalize()
{
   // 在这里终结代码
}
```

关键字 protected 是一个限定符，它确保 finalize() 方法不会被该类以外的代码调用。当然，Java 的内存回收可以由 JVM 来自动完成。如果你手动使用，则可以使用上面的方法。

```java
public class FinalizationDemo {
  public static void main(String[] args) {
    Cake c1 = new Cake(1);
    Cake c2 = new Cake(2);
    Cake c3 = new Cake(3);

    c2 = c3 = null;
    System.gc(); //调用Java垃圾收集器
  }
}

class Cake extends Object {
  private int id;
  public Cake(int id) {
    this.id = id;
    System.out.println("Cake Object " + id + "is created");
  }

  protected void finalize() throws java.lang.Throwable {
    super.finalize();
    System.out.println("Cake Object " + id + "is disposed");
  }
}

/**
$ javac FinalizationDemo.java
$ java FinalizationDemo
Cake Object 1is created
Cake Object 2is created
Cake Object 3is created
Cake Object 3is disposed
Cake Object 2is disposed
**/
```
