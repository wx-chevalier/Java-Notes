# 对象

对象是根据类创建的。在 Java 中，使用关键字 new 来创建一个新的对象。创建对象需要以下三步：

- **声明**：声明一个对象，包括对象名称和对象类型。
- **实例化**：使用关键字 new 来创建一个对象。
- **初始化**：使用 new 创建对象时，会调用构造方法初始化对象。

下面是一个创建对象的例子：

```java
public class Puppy {

  public Puppy(String name) {
    //这个构造器仅有一个参数：name
    System.out.println("小狗的名字是 : " + name);
  }

  public static void main(String[] args) {
    // 下面的语句将创建一个Puppy对象
    Puppy myPuppy = new Puppy("tommy");
  }
}
```

通过已创建的对象来访问成员变量和成员方法，如下所示：

```java
/* 实例化对象 */
Object referenceVariable = new Constructor();
/* 访问类中的变量 */
referenceVariable.variableName;
/* 访问类中的方法 */
referenceVariable.methodName();
```

# 类型判断
