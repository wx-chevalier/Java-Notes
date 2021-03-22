# JVM 垃圾回收算法

我们常说的垃圾回收算法可以分为两部分：对象的查找算法与真正的回收方法。不同回收器的实现细节各有不同，但总的来说基本所有的回收器都会关注如下两个方面：找出所有的存活对象以及清理掉所有的其它对象——也就是那些被认为是废弃或无用的对象。

![中世纪垃圾回收概念图](https://s1.ax1x.com/2020/11/07/B598gI.jpg)

Java 虚拟机规范中对垃圾收集器应该如何实现并没有任何规定，因此不同的厂商、不同版本的虚拟机所提供的垃圾收集器都可能会有很大差别，并且一般都会提供参数供用户根据自己的应用特点和要求组合出各个年代所使用的收集器。其中最主流的四个垃圾回收器分别是：通常用于单 CPU 环境的 Serial GC、Throughput/Parallel GC、CMS GC、G1 GC。

> 在[《Java-Series/性能调优](https://github.com/wx-chevalier/Java-Series?q=)》章节中了解更多 GC 优化的案例。

# Links

- https://mp.weixin.qq.com/s/tfyHwbsNCTjvMGTrfQ0qwQ
- https://toutiao.io/k/vdlyqj
- https://mp.weixin.qq.com/s/l42_A6odHwht_UyEbILDzQ?from=groupmessage&isappinstalled=0
- https://mp.weixin.qq.com/s/m02f-omq5TovMxy1gzETuA
