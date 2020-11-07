# JVM 垃圾回收算法

我们常说的垃圾回收算法可以分为两部分：对象的查找算法与真正的回收方法。不同回收器的实现细节各有不同，但总的来说基本所有的回收器都会关注如下两个方面：找出所有的存活对象以及清理掉所有的其它对象——也就是那些被认为是废弃或无用的对象。Java 虚拟机规范中对垃圾收集器应该如何实现并没有任何规定，因此不同的厂商、不同版本的虚拟机所提供的垃圾收集器都可能会有很大差别，并且一般都会提供参数供用户根据自己的应用特点和要求组合出各个年代所使用的收集器。其中最主流的四个垃圾回收器分别是：通常用于单 CPU 环境的 Serial GC、Throughput/Parallel GC、CMS GC、G1 GC。

![中世纪垃圾回收概念图](https://s1.ax1x.com/2020/11/07/B598gI.jpg)

与标记对象的传统算法相比，ZGC 在指针上做标记，在访问指针时加入 Load Barrier（读屏障），比如当对象正被 GC 移动，指针上的颜色就会不对，这个屏障就会先把指针更新为有效地址再返回，也就是，永远只有单个对象读取时有概率被减速，而不存在为了保持应用与 GC 一致而粗暴整体的 Stop The World。

# TBD

- https://mp.weixin.qq.com/s/tfyHwbsNCTjvMGTrfQ0qwQ
- https://toutiao.io/k/vdlyqj
- https://mp.weixin.qq.com/s/l42_A6odHwht_UyEbILDzQ?from=groupmessage&isappinstalled=0
- https://mp.weixin.qq.com/s/m02f-omq5TovMxy1gzETuA
