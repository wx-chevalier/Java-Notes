# Java null

在 Java 中，null 是所有引用类型的默认值。null 既不是对象也不是一种类型，它仅是一种特殊的值，你可以将其赋予任何引用类型，它还仅仅是一个特殊值，并不属于任何类型，用 instanceof 永远返回 false。

不能将 null 赋给基本类型变量，例如 int、double、float、boolean。如果将 null 赋值给包装类 object，然后将 object 赋给各自的基本类型，编译器不会报，但是你将会在运行时期遇到空指针异常。null 可以被转化为任何引用类型,可以调用引用类型中的静态方法，但是不可以调用非静态方法，运行时会报错。

```java
public class NullTest {
    public static void main(String[] args) {
        Object o = (Object) null;
        //int i  = null;
        Integer i = (Integer) null;
        String s = (String) null;

        System.out.println("o: " + o + "i: " + i + "s: " + s);  //o: nulli: nulls: null
        System.out.println(o instanceof Object);  //false
    }
}
```

# 运算

null==null 返回 true，被转换为同种类型的 null，都返回 true，不同类型直接编译报错。用 String 转换后的 null 可以进行字符串运算,这是因为字符串进行连接的时候,编译器对 null 进行了特别的优化,其实就是例化 StringBuilder,在调用 append()方法时对 null 的一个特别处理,当为 null 时，转化为“null”，最后调用 toString()返回一个 String 对象。

用八大基本类型转换后的 null，不可以进行基本类型的运算，否则会出现编译或者运行错误。

```java
c static void main(String[] args) {
        Object o = (Object) null;
        Integer i = (Integer) null;
        Integer j = (Integer) null;
        String s = (String) null;
//        System.out.println(Objects.equals(i, j));
//        System.out.println(i.equals(s));
 //       System.out.println(null == null);
//        i = i + 1;   //运行时空指针

//        System.out.println(2 == null);

        System.out.println("o: " + o + "i: " + i + "s: " + s);
        System.out.println(o instanceof Object);

    }
}
```

# 集合中的 key

| 集合类            | key         | value       | super       | 说明         |
| ----------------- | ----------- | ----------- | ----------- | ------------ |
| HashTable         | 不能为 null | 不能为 null | Dictionary  | 线程安全     |
| ConcurrentHashMap | 不能为 null | 不能为 null | AbstractMap | 线程局部安全 |
| TreeMap           | 不能为 null | 可以为 null | AbstractMap | 线程不安全   |
| HashMap           | 可以为 null | 可以为 null | AbstractMap | 线程不安全   |

hash 表需要进行 hash 值运算，key 不能为 null 好理解，如果 map 中 value 为 null 也好理解。表中不好理解的是 HashMap 中 key 可以为 null,看下面代码中对 null 有个特殊处理，索引位置为 0。
