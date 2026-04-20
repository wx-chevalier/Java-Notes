# Java 泛型

泛型即是把类型明确的工作推迟到创建对象或调用方法的时候才去明确的特殊的类型，Java 泛型设计原则是只要在编译时期没有出现警告，那么运行时期就不会出现 ClassCastException 异常。泛型常见的相关术语有：

- `ArrayList<E>`中的**E 称为类型参数变量**
- `ArrayList<Integer>`中的**Integer 称为实际类型参数**
- **整个称为 ArrayList<E>泛型类型**
- **整个 ArrayList<Integer>称为参数化的类型 ParameterizedType**

早期 Java 是使用 Object 来代表任意类型的，但是向下转型有强转的问题，这样程序就不太安全。在没有泛型的时候，Collection、Map 集合对元素的类型是没有任何限制的。本来我的 Collection 集合装载的是全部的 Dog 对象，但是外边把 Cat 对象存储到集合中，是没有任何语法错误的。把对象扔进集合中，集合是不知道元素的类型是什么的，仅仅知道是 Object。因此在 `get()` 的时候，返回的是 Object；外边获取该对象，还需要强制转换。

在引入了泛型之后，代码更加简洁，程序更加健壮，可读性和稳定性也得到了极大的提升。

# Links

— https://blog.csdn.net/s10461/article/details/53941091
