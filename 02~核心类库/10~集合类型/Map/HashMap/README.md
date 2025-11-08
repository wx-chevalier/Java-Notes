# HashMap

HashMap 是 Java Collection Framework 的重要成员，也是 Map 族(如下图所示)中我们最为常用的一种。简单地说，HashMap 是基于哈希表的 Map 接口的实现，以 Key-Value 的形式存在，即存储的对象是 Entry (同时包含了 Key 和 Value) 。在 HashMap 中，其会根据 hash 算法来计算 key-value 的存储位置并进行快速存取。特别地，HashMap 最多只允许一条 Entry 的键为 Null(多条会覆盖)，但允许多条 Entry 的值为 Null。此外，HashMap 是 Map 的一个非同步的实现。

![Map 类型](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/superbed/2021/07/16/60f15b485132923bf81c237b.jpg)

HashMap 实现了 Map 接口，并继承 AbstractMap 抽象类，其中 Map 接口定义了键值映射规则。和 AbstractCollection 抽象类在 Collection 族的作用类似，AbstractMap 抽象类提供了 Map 接口的骨干实现，以最大限度地减少实现 Map 接口所需的工作。HashMap 在 JDK 中的定义为：

```java
public class HashMap<K,V>
    extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable{
...
}
```
