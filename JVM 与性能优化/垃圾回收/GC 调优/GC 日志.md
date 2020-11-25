# GC 日志

![Young GC 日志条目明细](https://s1.ax1x.com/2020/11/11/BXGVFs.png)

![Full GC 日志条目明细](https://s1.ax1x.com/2020/11/11/BXGlmF.png)

# CMS GC 日志详细分析

```sh
[GC (CMS Initial Mark) [1 CMS-initial-mark: 19498K(32768K)] 36184K(62272K), 0.0018083 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
[CMS-concurrent-mark-start]
[CMS-concurrent-mark: 0.011/0.011 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
[CMS-concurrent-preclean-start]
[CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
[CMS-concurrent-abortable-preclean-start]
 CMS: abort preclean due to time [CMS-concurrent-abortable-preclean: 0.558/5.093 secs] [Times: user=0.57 sys=0.00, real=5.09 secs]
[GC (CMS Final Remark) [YG occupancy: 16817 K (29504 K)][Rescan (parallel) , 0.0021918 secs][weak refs processing, 0.0000245 secs][class unloading, 0.0044098 secs][scrub symbol table, 0.0029752 secs][scrub string table, 0.0006820 secs][1 CMS-remark: 19498K(32768K)] 36316K(62272K), 0.0104997 secs] [Times: user=0.02 sys=0.00, real=0.01 secs]
[CMS-concurrent-sweep-start]
[CMS-concurrent-sweep: 0.007/0.007 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
[CMS-concurrent-reset-start]
[CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
```

CMS 日志分为两个 STW(stop the world)，分别是 init remark（1） 与 remark（7）两个阶段。一般耗时比 YGC 长约 10 倍：

- [GC (CMS Initial Mark) [1 CMS-initial-mark: 19498K(32768K)] 36184K(62272K), 0.0018083 secs][times: user=0.01 sys=0.00, real=0.01 secs] 会 STO(Stop The World)，这时候的老年代容量为 32768K，在使用到 19498K 时开始初始化标记。耗时短。

- [CMS-concurrent-mark-start] 并发标记阶段开始

- [CMS-concurrent-mark: 0.011/0.011 secs] [Times: user=0.02 sys=0.00, real=0.00 secs] 并发标记阶段花费时间。

- [CMS-concurrent-preclean-start] 并发预清理阶段，也是与用户线程并发执行。虚拟机查找在执行并发标记阶段新进入老年代的对象(可能会有一些对象从新生代晋升到老年代，或者有一些对象被分配到老年代)。通过重新扫描，减少下一个阶段”重新标记”的工作，因为下一个阶段会 Stop The World。

- [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
  并发预清理阶段花费时间。

- [CMS-concurrent-abortable-preclean-start] CMS: abort preclean due to time

- [CMS-concurrent-abortable-preclean: 0.558/5.093 secs][times: user=0.57 sys=0.00, real=5.09 secs] 并发可中止预清理阶段，运行在并行预清理和重新标记之间，直到获得所期望的 eden 空间占用率。增加这个阶段是为了避免在重新标记阶段后紧跟着发生一次垃圾清除

- [GC (CMS Final Remark) [YG occupancy: 16817 K (29504 K)][rescan (parallel) , 0.0021918 secs][weak refs processing, 0.0000245 secs][class unloading, 0.0044098 secs][scrub symbol table, 0.0029752 secs][scrub string table, 0.0006820 secs][1 CMS-remark: 19498K(32768K)] 36316K(62272K), 0.0104997 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 会 STW(Stop The World)，收集阶段，这个阶段会标记老年代全部的存活对象，包括那些在并发标记阶段更改的或者新创建的引用对象

- [CMS-concurrent-sweep-start] 并发清理阶段开始，与用户线程并发执行。

- [CMS-concurrent-sweep: 0.007/0.007 secs] [Times: user=0.01 sys=0.00, real=0.01 secs] 并发清理阶段结束，所用的时间。

- [CMS-concurrent-reset-start] 开始并发重置。在这个阶段，与 CMS 相关数据结构被重新初始化，这样下一个周期可以正常进行。

- [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 并发重置所用结束，所用的时间。
