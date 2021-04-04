# synchronized

在多线程并发编程中 synchronized 一直是元老级角色，很多人都会称呼它为重量级锁。但是，随着 Java SE 1.6 对 synchronized 进行了各种优化之后，有些情况下它就并不那么重了；引入了偏向锁和轻量级锁，对锁的存储结构和升级过程，有效减少获得锁和释放锁带来的性能消耗。synchronized 关键字，同时解决了原子性、可见性、有序性问题:

- 可见性：按照 JMM 规范，对一个变量解锁之前，必须先把此变量同步回主存中，这样解锁后，后续线程就可以访问到被修改后的值。所以被 synchronized 锁住的对象，其值具有可见性。
- 原子性：通过监视器锁，可以保证 synchronized 修饰的代码在同一时间，只能被一个线程访问，在锁未释放之前其它线程无法进入该方法或代码块，保证了操作的原子性。
- 有序性：synchronized 关键字并不禁止指令重排，但是由于程序是以单线程的方式执行的，所以执行的结果是确定的，不会受指令重排的干扰，有序性不再是个问题。

需要注意的是，当我们使用 synchronized 关键字，管理某个状态时，必须对访问这个对象的所有操作，都加上 synchronized 关键字，否则仍然会有并发安全性问题。

# 同步使用

- 对于，普通同步方法，锁是当前实例对象。`public synchronized void test(){...}`
- 对于静态同步方法，锁是当前类的 Class 对象。`public static synchronized void test(...){}`
- 对于对于同步方法块，锁是 synchronized 括号中里配置的对象。`synchronized(instance){...}`

# Links

- https://blog.csdn.net/significantfrank/article/details/80399179 Synchronized 和 Lock 该如何选择

- https://mp.weixin.qq.com/s/w5K8kmNwAcIxB5lb1N93pg synchronized 连环问
