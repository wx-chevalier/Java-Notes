# Java 14 新特性介绍

Java 14 计划于 2020 年 3 月 17 日发布。版本 14 包含的 JEP（Java 增强提案）比 Java 12 和 13 的总和还多。那么，对于每天编写和维护代码的 Java 开发人员来说，最重要的是什么呢？

在 Java 14 中，有新的预览语言功能和更新可帮助开发人员进行日常工作。例如，Java 14 引入了 instanceof 模式匹配，这是减少显式强制转换的一种方式。而且，Java 14 引入了记录，这是一种新的结构，用于简洁地声明仅用于聚合数据的类。此外，NullPointerException 消息已得到改进，具有更好的诊断功能，并且开关表达式现在已成为 Java 14 的一部分。文本块是一种可帮助您处理多行字符串值的功能，在引入了两个新的转义序列后，将进行另一轮预览。Java 操作的一部分技术人员可能会感兴趣的另一项更改是 JDK Flight Recorder 中的事件流。Ben Evans 在本文中讨论了该选项。

# Switch Expressions

在 Java 14 中，Switch 表达式变为永久性的。在早期版本中，Switch 表达式是“预览”功能。提醒一下，将特征指定为“预览”以收集反馈，并且根据反馈可能会更改甚至删除这些特征。但预计大多数最终将在 Java 中永久化。

新的 switch 表达式的优点包括由于没有掉落行为，穷举以及由于表达式和复合形式而易于编写，从而减小了错误的范围。作为一个刷新示例，switch 表达式现在可以利用箭头语法，例如在以下示例中：

```java
var log = switch (event) {
    case PLAY -> "User has triggered the play button";
    case STOP, PAUSE -> "User needs a break";
    default -> {
        String message = event.toString();
        LocalDateTime now = LocalDateTime.now();
        yield "Unknown event " + message +
              " logged on " + now;
    }
};
```

# Text Blocks

Java 13 引入了文本块作为预览功能。文本块使使用多行字符串文字更加容易。此功能将通过 Java 14 进行第二轮预览，并进行了一些调整。作为复习，编写带有许多字符串连接和转义序列的代码以提供适当的多行文本格式非常普遍。下面的代码显示了 HTML 格式的示例：

```java
String html = "<HTML>" +
"\n\t" + "<BODY>" +
"\n\t\t" + "<H1>\"Java 14 is here!\"</H1>" +
"\n\t" + "</BODY>" +
"\n" + "</HTML>";
```

使用文本块，您可以简化此过程并使用界定文本块开头和结尾的三个引号来编写更优雅的代码：

```java
String html = """
<HTML>
  <BODY>
    <H1>"Java 14 is here!"</H1>
  </BODY>
</HTML>""";
```

与普通的字符串文字相比，文本块还提供了更高的表达能力。Java 14 中添加了两个新的转义序列。首先，可以使用新的 \s 转义序列来表示单个空格。其次，您可以使用反斜杠 \ 来禁止在行尾插入新行字符。当您想分隔很长的行以简化文本块内的可读性时，这很有用。

```java
String literal =
         "Lorem ipsum dolor sit amet, consectetur adipiscing " +
         "elit, sed do eiusmod tempor incididunt ut labore " +
         "et dolore magna aliqua.";
```

使用文本块中的 \ 转义序列，可以表示如下：

```java
String text = """
                Lorem ipsum dolor sit amet, consectetur adipiscing \
                elit, sed do eiusmod tempor incididunt ut labore \
                et dolore magna aliqua.\
                """;
```

# Pattern Matching for instanceof

Java 14 引入了预览功能，该功能有助于消除对在有条件的 instanceof 检查之前进行显式强制转换的需求。例如，考虑以下代码：

```java
if (obj instanceof Group) {
  Group group = (Group) obj;

  // use group specific methods
  var entries = group.getEntries();
}
```

可以改写为如下：

```java
if (obj instanceof Group group) {
  var entries = group.getEntries();
}
```

由于条件检查断言 obj 是 Group 类型，为什么还要在第一个代码段中用条件块再次说 obj 是 Group 类型？ 这种需求可能会增加错误的范围。较短的语法将删除典型 Java 程序中的许多强制转换。（2011 年一项提出相关语言功能的研究报告指出，所有 casts 中约有 24％遵循条件语句中的 instanceof。）JEP 305 涵盖了此更改，并从 Joshua Bloch 的 Effective Java 书中指出了一个示例，该示例通过以下相等方法进行说明：

```java
@Override public boolean equals(Object o) {
    return (o instanceof CaseInsensitiveString) &&
            ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
}
```

通过删除对 CaseInsensitiveString 的冗余显式转换，可以将前面的代码简化为以下形式：

```java
@Override public boolean equals(Object o) {
    return (o instanceof CaseInsensitiveString cis) &&
            cis.s.equalsIgnoreCase(s);
}
```

这是一个有趣的预览功能，因为它为更广泛的模式匹配打开了大门。模式匹配的思想是为语言功能提供方便的语法，以根据某些条件提取对象的成分。instanceof 运算符就是这种情况，因为条件是类型检查，并且提取操作正在调用适当的方法或访问特定字段。

换句话说，此预览功能仅仅是个开始，您可以期待一种语言功能，它可以帮助进一步减少冗长性，从而减少错误的可能性。

# Records

该版本中还有另一种预览语言功能：记录。像到目前为止提出的其他想法一样，此功能遵循减少 Java 冗长并帮助开发人员编写更简洁的代码的趋势。记录集中在某些域类上，这些类仅用于将数据存储在字段中，并且不声明任何自定义行为。

为了直接解决问题，请使用简单的域类 BankTransaction，该类使用三个字段对交易进行建模：日期，金额和描述。声明类时，您需要担心多个组件：

- The constructor
- Getter methods
- toString()
- hashCode() and equals()

此类组件的代码通常由 IDE 自动生成，并占用大量空间。这是 BankTransaction 类的完整生成的实现：

```java
public class BankTransaction {
    private final LocalDate date;
    private final double amount;
    private final String description;


    public BankTransaction(final LocalDate date,
                           final double amount,
                           final String description) {
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    public LocalDate date() {
        return date;
    }

    public double amount() {
        return amount;
    }

    public String description() {
        return description;
    }

    @Override
    public String toString() {
        return "BankTransaction{" +
                "date=" + date +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankTransaction that = (BankTransaction) o;
        return Double.compare(that.amount, amount) == 0 &&
                date.equals(that.date) &&
                description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, amount, description);
    }
}
```

Java 14 提供了一种消除冗长并明确意图的方法，即您想要的只是一个只将数据与 equals，hashCode 和 toString 方法的实现一起聚合的类。您可以按以下方式重构 BankTransaction：

```java
public record BankTransaction(Date date,
                              double amount,
                              String description) {}
```

使用记录，您可以“自动”获取除构造函数和获取方法外的 equals，hashCode 和 toString 的实现。要尝试该示例，请记住您需要使用预览标志来编译文件：

```java
javac --enable-preview --release 14 BankTransaction.java
```

记录的字段是隐式最终的。这意味着您无法重新分配它们。请注意，但这并不意味着整个记录都是不变的。存储在字段中的对象本身可以是可变的。

# Helpful NullPointerExceptions

有人说抛出 NullPointerExceptions 应该是 Java 中新的“ Hello world”，因为您无法逃避它们。撇开笑话，它们会引起挫败感，因为当代码在生产环境中运行时，它们经常出现在应用程序日志中，这可能使调试变得困难，因为原始代码不容易获得。例如，考虑以下代码：

```java
var name = user.getLocation().getCity().getName();
```

在 Java 14 之前，您可能会收到以下错误：

```java
Exception in thread "main" java.lang.NullPointerException
    at NullPointerExample.main(NullPointerExample.java:5)
```

不幸的是，如果在第 5 行，有一个具有多个方法调用的赋值：`getLocation()` 和 `getCity()`，两者都可能返回 null。实际上，变量 user 也可以为 null。因此，尚不清楚是什么导致了`<strong> NullPointerException </strong>`。

```java
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "Location.getCity()" because the return value of "User.getLocation()" is null
    at NullPointerExample.main(NullPointerExample.java:5)
```
