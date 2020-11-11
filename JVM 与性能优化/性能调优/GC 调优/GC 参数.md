# GC 参数与日志

# 参数配置

```sh
-server       -- 启用能够执行优化的编译器，显著提高服务器的性能
```

## 空间设置参数

```sh
-Xmx4000M     -- 堆最大值
-Xms4000M     -- 堆初始大小
-Xmn600M      -- 年轻代大小
-XX:PermSize=200M         -- 持久代初始大小
-XX:MaxPermSize=200M      -- 持久代最大值
-Xss256K                  -- 每个线程的栈大小，JDK 1.5 以后默认是 1M，在 IBM 的 jdk 中还有-Xoss 参数(此时每个线程占用的 stack 空间为 256K 大小)
-XX:LargePageSizeInBytes=128M        -- 内存页的大小不可设置过大，会影响 Perm 的大小
-XX:NewRatio=3 -- 为 Tenured:Young 的初始尺寸比例(设置了大小就不再设置此值)，此时 Young 占用整个 HeapSize 的 1/4 大小。
-XX:SurvivorRatio=1       -- 年轻代中 Eden 区与两个 Survivor 区的比值
```

## 垃圾回收器选择参数

```sh
-XX:+DisableExplicitGC    -- 关闭 System.gc()
-XX:MaxTenuringThreshold=3 -- 一般一个对象在 Young 经过多少次 GC 后会被移动到 OLD 区。
-XX:+UseFastAccessorMethods          -- 原始类型的快速优化
-XX:SoftRefLRUPolicyMSPerMB=0          -- 每兆堆空闲空间中 SoftReference 的存活时间
-XX:+UseAdaptiveSizepollcy -- 收集器自动根据实际情况进行一些比例以及回收算法调整。

-XX:+UseParNewGC          -- 设置年轻代为并行收集
-XX:+UseParallelGC -- 一种较老的并行回收算法。
-XX:+UseParallelOldGC -- 对 Tenured 区域使用并行回收算法。
-XX:ParallelGCThread=10 -- 并行的个数，一般和 CPU 个数相对应。

-XX:+UseConcMarkSweepGC   -- 使用 CMS 内存收集
-XX:+CMSParallelRemarkEnabled        -- 降低标记停顿
-XX:+UseCMSCompactAtFullCollection   -- 在 FULL GC 的时候，对年老代进行压缩，可能会影响性能，但是可以消除碎片
-XX:CMSFullGCsBeforeCompaction=0     -- 此值设置运行多少次 GC 以后对内存空间进行压缩、整理
-XX:+CMSClassUnloadingEnabled        -- 回收动态生成的代理类 SEE：http://stackoverflow.com/questions/3334911/what-does-jvm-flag-cmsclassunloadingenabled-actually-do
-XX:+UseCMSInitiatingOccupancyOnly   -- 使用手动定义初始化定义开始 CMS 收集，禁止 HotSpot 自行触发 CMS GC
-XX:CMSInitiatingOccupancyFraction=80  -- 使用 CMS 作为垃圾回收，使用 80％ 后开始 CMS 收集；在并发 GC 下，由于一边使用，一边 GC，就不能在不够用的时候 GC，默认情况下是在使用了 68%的时候进行 GC，通过该参数可以调整实际的值。
```

## 日志策略参数

```sh
-XX:+PrintGCDetails                    --输出GC日志详情信息
-XX:+PrintGCApplicationStoppedTime     --输出垃圾回收期间程序暂停的时间
-Xloggc:$WEB_APP_HOME/.tomcat/logs/gc.log  --把相关日志信息记录到文件以便分析.
-XX:+HeapDumpOnOutOfMemoryError            --发生内存溢出时生成heapdump文件
-XX:HeapDumpPath=$WEB_APP_HOME/.tomcat/logs/heapdump.hprof  --heapdump文件地址
```

![各平台默认的垃圾回收器](https://s1.ax1x.com/2020/11/11/BXGCy8.jpg)

# TBD

- https://mp.weixin.qq.com/s/e6dKLS8nfJ9bXgvfWum5bA JVM 参数最佳实践：元空间的初始大小和最大大小
