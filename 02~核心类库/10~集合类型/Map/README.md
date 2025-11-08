# Java 中的映射类型

Map 是 Key-Value 对映射的抽象接口，该映射不包括重复的键，即一个键对应一个值；此接口主要有四个常用的实现类，分别是 HashMap、Hashtable、LinkedHashMap 和 TreeMap。

![Map 类之间关系](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/superbed/2021/07/16/60f159c75132923bf8140ec5.jpg)

下面针对各个实现类的特点做一些说明：

- HashMap：它根据键的 hashCode 值存储数据，大多数情况下可以直接定位到它的值，因而具有很快的访问速度，但遍历顺序却是不确定的。HashMap 最多只允许一条记录的键为 null，允许多条记录的值为 null。HashMap 非线程安全，即任一时刻可以有多个线程同时写 HashMap，可能会导致数据的不一致。如果需要满足线程安全，可以用 Collections 的 synchronizedMap 方法使 HashMap 具有线程安全的能力，或者使用 ConcurrentHashMap。

- Hashtable：Hashtable 是遗留类，很多映射的常用功能与 HashMap 类似，不同的是它承自 Dictionary 类，并且是线程安全的，任一时间只有一个线程能写 Hashtable，并发性不如 ConcurrentHashMap，因为 ConcurrentHashMap 引入了分段锁。Hashtable 不建议在新代码中使用，不需要线程安全的场合可以用 HashMap 替换，需要线程安全的场合可以用 ConcurrentHashMap 替换。

- LinkedHashMap：LinkedHashMap 是 HashMap 的一个子类，保存了记录的插入顺序，在用 Iterator 遍历 LinkedHashMap 时，先得到的记录肯定是先插入的，也可以在构造时带参数，按照访问次序排序。

- TreeMap：TreeMap 实现 SortedMap 接口，能够把它保存的记录根据键排序，默认是按键值的升序排序，也可以指定排序的比较器，当用 Iterator 遍历 TreeMap 时，得到的记录是排过序的。如果使用排序的映射，建议使用 TreeMap。在使用 TreeMap 时，key 必须实现 Comparable 接口或者在构造 TreeMap 传入自定义的 Comparator，否则会在运行时抛出 java.lang.ClassCastException 类型的异常。

对于上述四种 Map 类型的类，要求映射中的 key 是不可变对象。不可变对象是该对象在创建后它的哈希值不会被改变。如果对象的哈希值发生变化，Map 对象很可能就定位不到映射的位置了。必须指出的是，虽然容器号称存储的是 Java 对象，但实际上并不会真正将 Java 对象放入容器中，只是在容器中保留这些对象的引用。也就是说，Java 容器实际上包含的是引用变量，而这些引用变量指向了我们要实际保存的 Java 对象。

# 集合中的 key

| 集合类            | key         | value       | super       | 说明         |
| ----------------- | ----------- | ----------- | ----------- | ------------ |
| HashTable         | 不能为 null | 不能为 null | Dictionary  | 线程安全     |
| ConcurrentHashMap | 不能为 null | 不能为 null | AbstractMap | 线程局部安全 |
| TreeMap           | 不能为 null | 可以为 null | AbstractMap | 线程不安全   |
| HashMap           | 可以为 null | 可以为 null | AbstractMap | 线程不安全   |

hash 表需要进行 hash 值运算，key 不能为 null 好理解，如果 map 中 value 为 null 也好理解。表中不好理解的是 HashMap 中 key 可以为 null,看下面代码中对 null 有个特殊处理，索引位置为 0。
