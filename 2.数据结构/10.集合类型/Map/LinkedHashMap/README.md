# LinkedHashMap

HashMap 是无序的，也就是说，迭代 HashMap 所得到的元素顺序并不是它们最初放置到 HashMap 的顺序。HashMap 的这一缺点往往会造成诸多不便，因为在有些场景中（譬如实现 LRU 的场景），我们确需要用到一个可以保持迭代顺序的 Map。该迭代顺序可以是插入顺序，也可以是访问顺序。因此，根据链表中元素的顺序可以将 LinkedHashMap 分为：保持插入顺序的 LinkedHashMap 和 保持访问顺序的 LinkedHashMap，其中 LinkedHashMap 的默认实现是按插入顺序排序的。

![Map 类族示意图](https://assets.ng-tech.icu/superbed/2021/07/16/60f15b485132923bf81c237b.jpg)

本质上，HashMap 和双向链表合二为一即是 LinkedHashMap。所谓 LinkedHashMap，其落脚点在 HashMap，因此更准确地说，它是一个将所有 Entry 节点链入一个双向链表双向链表的 HashMap。在 LinkedHashMap 中，所有 put 进来的 Entry 都保存在如下面第一个图所示的哈希表中，但由于它又额外定义了一个以 head 为头结点的双向链表(如下面第二个图所示)，因此对于每次 put 进来 Entry，除了将其保存到哈希表中对应的位置上之外，还会将其插入到双向链表的尾部。

![LinkedHashMap 结构示意图](https://assets.ng-tech.icu/superbed/2021/07/16/60f1904f5132923bf851d06f.jpg)

其中，HashMap 与 LinkedHashMap 的 Entry 结构示意图如下图所示：

![Entry 示意图](https://assets.ng-tech.icu/superbed/2021/07/16/60f1907f5132923bf8535dff.jpg)

特别地，由于 LinkedHashMap 是 HashMap 的子类，所以 LinkedHashMap 自然会拥有 HashMap 的所有特性。比如，LinkedHashMap 也最多只允许一条 Entry 的键为 Null(多条会覆盖)，但允许多条 Entry 的值为 Null。此外，LinkedHashMap 也是 Map 的一个非同步的实现。此外，LinkedHashMap 还可以用来实现 LRU (Least recently used, 最近最少使用)算法，这个问题会在下文的特别谈到。

# Links

- [LinkedHashMap 原理解析](http://uule.iteye.com/blog/1522291)
- https://blog.csdn.net/justloveyou_/article/details/71713781 Map 综述（二）：彻头彻尾理解 LinkedHashMap
