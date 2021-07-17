# ConcurrentHashMap

ConcurrentHashMap 是 J.U.C(java.util.concurrent 包)的重要成员，它是 HashMap 的一个线程安全的、支持高效并发的版本。HashMap 不是线程安全的。也就是说，在多线程环境下，操作 HashMap 会导致各种各样的线程安全问题，比如在 HashMap 扩容重哈希时出现的死循环问题，脏读问题等。HashMap 的这一缺点往往会造成诸多不便，虽然在并发场景下 HashTable 和由同步包装器包装的 `HashMap(Collections.synchronizedMap(Map<K,V> m))` 可以代替 HashMap，但是它们都是通过使用一个全局的锁来同步不同线程间的并发访问，因此会带来不可忽视的性能问题。

庆幸的是，JDK 为我们解决了这个问题，它为 HashMap 提供了一个线程安全的高效版本：ConcurrentHashMap。在 ConcurrentHashMap 中，无论是读操作还是写操作都能保证很高的性能：在进行读操作时(几乎)不需要加锁，而在写操作时通过锁分段技术只对所操作的段加锁而不影响客户端对其它段的访问。特别地，在理想状态下，ConcurrentHashMap 可以支持 16 个线程执行并发写操作（如果并发级别设为 16），及任意数量线程的读操作。

从 JDK6 到 JDK8，实现线程安全的思想也已经完全变了，它摒弃了 Segment（锁段）的概念，而是启用了一种全新的方式实现,利用 CAS 算法。它沿用了与它同时期的 HashMap 版本的思想，底层依然由“数组”+链表+红黑树的方式思想，但是为了做到并发，又增加了很多辅助的类，例如 TreeBin，Traverser 等对象内部类。
