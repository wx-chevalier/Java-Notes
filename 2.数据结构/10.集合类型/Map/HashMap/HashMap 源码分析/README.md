# HashMap 源码分析

HashMap 由数组和链表组成的，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突而存在的。Java 8 开始 HashMap 的实现是综合利用了数组、链表与红黑树。

![HashMap Table 与 链表](https://assets.ng-tech.icu/superbed/2021/07/16/60f15fd95132923bf833a63f.jpg)

![Java 8 红黑树 HashMap 示意图](https://assets.ng-tech.icu/superbed/2021/07/16/60f18c835132923bf83378cd.jpg)

如果定位到的数组位置不含链表，那么查找、添加等操作很快，仅需一次寻址即可，其时间复杂度为 O(1)；如果定位到的数组包含链表，对于添加操作，其时间复杂度为 O(n)——首先遍历链表，存在即覆盖，不存在则新增；对于查找操作来讲，仍需要遍历链表，然后通过 key 对象的 equals 方法逐一对比查找。从性能上考虑，HashMap 中的链表出现越少，即哈希冲突越少，性能也就越好。所以，在日常编码中，可以使用 HashMap 存取键值映射关系。

# Links

- https://zhuanlan.zhihu.com/p/79219960 HashMap 源码分析（jdk1.8，保证你能看懂）

- https://zhuanlan.zhihu.com/p/21673805 Java 8 系列之重新认识 HashMap

- [hashmap-changes-in-java-8/](https://examples.javacodegeeks.com/core-java/util/hashmap/hashmap-changes-in-java-8/)
