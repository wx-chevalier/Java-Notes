# Stream API

java.util.Stream 表示了某一种元素的序列，在这些元素上可以进行各种操作。Stream 操作可以是中间操作，也可以是完结操作。完结操作会返回一个某种类型的值，而中间操作会返回流对象本身，并且你可以通过多次调用同一个流操作方法来将操作结果串起来(就像 StringBuffer 的 append 方法一样————译者注)。Stream 是在一个源的基础上创建出来的，例如 java.util.Collection 中的 list 或者 set(map 不能作为 Stream 的源)。Stream 操作往往可以通过顺序或者并行两种方式来执行。

首先以 String 类型的 List 的形式创建流：

```java
List<String> stringCollection = new ArrayList<>();
stringCollection.add("ddd2");
stringCollection.add("aaa2");
stringCollection.add("bbb1");
stringCollection.add("aaa1");
stringCollection.add("bbb3");
stringCollection.add("ccc");
stringCollection.add("bbb2");
stringCollection.add("ddd1");

//直接从数组创建流
int m = Arrays.stream(ints)
              .reduce(Integer.MIN_VALUE, Math::max);
```

# 方法引用

有时候 Lambda 表达式的代码就只是一个简单的方法调用而已，遇到这种情况，Lambda 表达式还可以进一步简化为方法引用(Method References)。一共有四种形式的方法引用：

- 静态方法引用

```java
List<Integer> ints = Arrays.asList(1, 2, 3);
ints.sort(Integer::compare);
```

- 某个特定对象的实例方法

例如前面那个遍历并打印每一个 word 的例子可以写成这样：

```java
words.forEach(System.out::println);
```

- 某个类的实例方法

```java
words.stream().map(word -> word.length()); // lambda
words.stream().map(String::length); // method reference
```

- 构造函数引用

```java
// lambda
words.stream().map(word -> {
    return new StringBuilder(word);
});

// constructor reference
words.stream().map(StringBuilder::new);
```
