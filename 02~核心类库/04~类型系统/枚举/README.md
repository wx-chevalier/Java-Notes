# 枚举类型

枚举类型（Enumerated Type）很早就出现在编程语言中，它被用来将一组类似的值包含到一种类型当中。而这种枚举类型的名称则会被定义成独一无二的类型描述符，在这一点上和常量的定义相似。不过相比较常量类型，枚举类型可以为申明的变量提供更大的取值范围。为了改进 Java 语言在这方面的不足弥补缺陷，5.0 版本 SDK 发布时候，在语言层面上增加了枚举类型。枚举类型的定义也非常的简单，用 enum 关键字加上名称和大括号包含起来的枚举值体即可：

```java
// 定义一周七天的枚举类型
public enum WeekDay { Mon, Tue, Wed, Thu, Fri, Sat, Sun }

// 读取当天的信息
WeekDay today = readToday();

// 根据日期来选择进行活动
switch(today) {
 Mon: do something; break;
 Tue: do something; break;
 Wed: do something; break;
 Thu: do something; break;
 Fri: do something; break;
 Sat: play sports game; break;
 Sun: have a rest; break;
}
```

最直接的益处就是扩大 switch 语句使用范围。5.0 之前，Java 中 switch 的值只能够是简单类型，比如 int、byte、short、char, 有了枚举类型之后，就可以使用对象了。需要注意的是，Java 中的枚举类型实际上会被编译为类文件，值即是这个类型的成员变量，譬如我们丰富下前文的枚举类型：

```java
public enum WeekDay {
  Mon("Monday"),
  Tue("Tuesday"),
  Wed("Wednesday"),
  Thu("Thursday"),
  Fri("Friday"),
  Sat("Saturday"),
  Sun("Sunday");

  private final String day;

  private WeekDay(String day) {
    this.day = day;
  }

  public static void printDay(int i) {
    switch (i) {
      case 1:
        System.out.println(WeekDay.Mon);
        break;
      // ...
        default:
        System.out.println("wrong number!");
    }
  }

  public String getDay() {
    return day;
  }
}
```

# 枚举值关联

我们可以创建如下的枚举类型：

```java
public enum Element {
    H("Hydrogen"),
    HE("Helium"),
    // ...
    NE("Neon");

    public final String label;

    private Element(String label) {
        this.label = label;
    }
}
```

首先，我们注意到声明列表中的特殊语法。这就是枚举类型的构造函数被调用的方式。尽管对枚举类型使用 new 操作符是非法的，但我们可以在声明列表中传递构造器参数。

然后我们声明一个实例变量 label。这里面有几件事需要注意。

- 首先，我们选择了标签标识符而不是名称。尽管成员字段名可以使用，但我们还是选择了 label，以避免与预定义的 Enum.name()方法相混淆。
- 第二，我们的标签字段是 final 的。虽然枚举的字段不一定是 finals 的，但在大多数情况下我们不希望我们的标签发生变化。本着枚举值是恒定的精神，这是有道理的。
- 最后，标签字段是 public 的，所以我们可以直接访问标签。

```java
System.out.println(BE.label);
```

另一方面，该字段可以是私有的，用 getLabel()方法访问。为了简洁起见，本文将继续使用公共字段的样式。Java 为所有枚举类型提供了一个 valueOf(String)方法。因此，我们总是可以根据声明的名称得到一个枚举的值。

```java
assertSame(Element.LI, Element.valueOf("LI"));
```

然而，我们可能也想通过我们的标签字段来查询一个枚举值。要做到这一点，我们可以添加一个静态方法。

```java
public static Element valueOfLabel(String label) {
    for (Element e : values()) {
        if (e.label.equals(label)) {
            return e;
        }
    }
    return null;
}
```

static valueOfLabel()方法遍历元素值，直到找到一个匹配的元素。如果没有找到匹配的，它就返回 null。反之，可以抛出一个异常，而不是返回 null。

```java
assertSame(Element.LI, Element.valueOfLabel("Lithium"));
```

为了提高值查询的效率，我们还可以添加如下的缓存：

```java
public enum Element {
    H("Hydrogen", 1, 1.008f),
    HE("Helium", 2, 4.0026f),
    // ...
    NE("Neon", 10, 20.180f);

    private static final Map<String, Element> BY_LABEL = new HashMap<>();
    private static final Map<Integer, Element> BY_ATOMIC_NUMBER = new HashMap<>();
    private static final Map<Float, Element> BY_ATOMIC_WEIGHT = new HashMap<>();

    static {
        for (Element e : values()) {
            BY_LABEL.put(e.label, e);
            BY_ATOMIC_NUMBER.put(e.atomicNumber, e);
            BY_ATOMIC_WEIGHT.put(e.atomicWeight, e);
        }
    }

    public final String label;
    public final int atomicNumber;
    public final float atomicWeight;

    private Element(String label, int atomicNumber, float atomicWeight) {
        this.label = label;
        this.atomicNumber = atomicNumber;
        this.atomicWeight = atomicWeight;
    }

    public static Element valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    public static Element valueOfAtomicNumber(int number) {
        return BY_ATOMIC_NUMBER.get(number);
    }

    public static Element valueOfAtomicWeight(float weight) {
        return BY_ATOMIC_WEIGHT.get(weight);
    }
}
```
