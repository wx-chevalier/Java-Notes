# CopyOnWrite

Copy-On-Write 简称 COW，是一种用于程序设计中的优化策略。其基本思路是，从一开始大家都在共享同一个内容，当某个人想要修改这个内容的时候，才会真正把内容 Copy 出去形成一个新的内容然后再改，这是一种延时懒惰策略。从 JDK1.5 开始 Java 并发包里提供了两个使用 CopyOnWrite 机制实现的并发容器,它们是 CopyOnWriteArrayList 和 CopyOnWriteArraySet。

CopyOnWrite 容器即写时复制的容器，当我们往一个容器添加元素的时候，不直接往当前容器添加，而是先将当前容器进行 Copy，复制出一个新的容器，然后新的容器里添加元素，添加完元素之后，再将原容器的引用指向新的容器。它是典型的读写分离思想的实践，我们可以对 CopyOnWrite 容器进行并发的读，而不需要加锁。

# 语法应用

# CopyOnWriteArrayList 的内部实现

读的时候不需要加锁，如果读的时候有多个线程正在向 CopyOnWriteArrayList 添加数据，读还是会读到旧的数据，因为写的时候不会锁住旧的 CopyOnWriteArrayList。

```sh

```

# 链接

- https://mp.weixin.qq.com/s/Xv8c9A4E_DOSkI1jBhr-rg
