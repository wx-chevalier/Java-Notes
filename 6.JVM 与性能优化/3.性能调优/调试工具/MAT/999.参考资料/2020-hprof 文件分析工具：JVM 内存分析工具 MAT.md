> [原文地址](https://blog.csdn.net/weixin_39850150/article/details/111250003)

# hprof 文件分析工具：JVM 内存分析工具 MAT

MAT(Memory Analyzer Tools)是一个快速且功能丰富的 Java 堆分析器，可帮助您查找内存泄漏并减少内存消耗。使用 MAT 分析具有数亿个对象的高效堆转储，快速计算对象的保留大小，查看谁阻止垃圾收集器收集对象，运行报告以自动提取泄漏嫌疑者。

# 1 简介

MAT 是一款非常强大的内存分析工具，在 Eclipse 中有相应的插件，同时也有单独的安装包。在进行内存分析时，只要获得了反映当前设备内存映像的 hprof 文件，通过 MAT 打开就可以直观地看到当前的内存信息。

# 2 使用

## 2.1 准备 MAT

下载独立版本的 MAT，下载地址：https://www.eclipse.org/mat/downloads.php，下载后解压。找到 MemoryAnalyzer.ini 文件，该文件里面有个 Xmx 参数，该参数表示最大内存占用量，默认为 1024m，根据堆转储文件大小修改该参数即可。

## 2.2 准备堆转储文件(Heap Dump)

堆转储文件(Heap Dump)是 Java 进程在某个时间内的快照(.hprof 格式)。它在触发快照的时候保存了很多信息，如：Java 对象和类信息(通常在写堆转储文件前会触发一次 Full GC)。堆转储文件信息：

![堆转储文件信息](https://assets.ng-tech.icu/item/20230406154117.png)

- 所有的对象信息，包括对象实例、成员变量、存储于栈中的基本类型值和存储于堆中的其他对象的引用值。
- 所有的类信息，包括 classloader、类名称、父类、静态变量等。
- GC Root 到所有的这些对象的引用路径。
- 线程信息，包括线程的调用栈及此线程的线程局部变量(TLS)。

多种方式获取堆转储文件：

- 通过 jmap 命令可以在 cmd 里执行：jmap -dump:format=b,file= 。
- 如果想在发生内存溢出的时候自动 dump，需要添加下面 JVM 参数：-XX:+HeapDumpOnOutOfMemoryError。
- 使用 Ctrl+Break 组合键主动获取获取，需要添加下面 JVM 参数：-XX:+HeapDumpOnCtrlBreak。
- 使用 HPROF Agent 可以在程序执行结束时或受到 SIGOUT 信号时生成 Dump 文件，配置在虚拟机的参数如下：-agentlib:hprof=heap=dump,format=b。
- 使用 JConsole 获取。
- 使用 Memory Analyzer Tools 的 File -> Acquire Heap Dump 功能获取。

## 2.3 分析堆转储文件

打开 MAT 之后，加载 dump 文件，差不多就下面这样的界面：

![MAT 界面](https://assets.ng-tech.icu/item/20230406154413.png)

常用的两个功能：Histogram、 Leak Suspects。

### 2.3.1 Histogram

![Histogram 示意](https://assets.ng-tech.icu/item/20230406154450.png)

Histogram 可以列出内存中的对象，对象的个数及其内存大小，可以用来定位哪些对象在 Full GC 之后还活着，哪些对象占大部分内存。

- Class Name：类名称，Java 类名。
- Objects：类的对象的数量，这个对象被创建了多少个。
- Shallow Heap：对象本身占用内存的大小，不包含其引用的对象内存，实际分析中作用不大。常规对象(非数组)的 Shallow Size 由其成员变量的数量和类型决定。数组的 Shallow Size 由数组元素的类型(对象类型、基本类型)和数组长度决定。对象成员都是些引用，真正的内存都在堆上，看起来是一堆原生的 byte[], char[], int[]，对象本身的内存都很小。
- Retained Heap：计算方式是将 Retained Set(当该对象被回收时那些将被 GC 回收的对象集合)中的所有对象大小叠加。或者说，因为 X 被释放，导致其它所有被释放对象(包括被递归释放的)所占的 heap 大小。Retained Heap 可以更精确的反映一个对象实际占用的大小。

Retained Heap 例子：一个 ArrayList 对象持有 100 个对象，每一个占用 16 bytes，如果这个 list 对象被回收，那么其中 100 个对象也可以被回收，可以回收 `16*100 + X` 的内存，X 代表 ArrayList 的 shallow 大小。

![Histogram 下钻示意](https://assets.ng-tech.icu/item/20230406154537.png)

在上述列表中选择一个 Class，右键选择 List objects > with incoming references，在新页面会显示通过这个 class 创建的对象信息。

![Path to GCRoots](https://assets.ng-tech.icu/item/20230406154604.png)

继续选择一个对象，右键选择 Path to GC Roots > `****` ，通常在排查**内存泄漏(一般是因为存在无效的引用)**的时候，我们会选择 exclude all phantom/weak/soft etc.references，意思是查看排除虚引用/弱引用/软引用等的引用链，因为被虚引用/弱引用/软引用的对象可以直接被 GC 给回收，我们要看的就是某个对象否还存在 Strong 引用链(在导出 Heap Dump 之前要手动触发 GC 来保证)，如果有，则说明存在内存泄漏，然后再去排查具体引用。

这时会拿到 GC Roots 到该对象的路径，通过对象之间的引用，可以清楚的看出这个对象没有被回收的原因，然后再去定位问题。如果上面对象此时本来应该是被 GC 掉的，简单的办法就是将其中的某处置为 null 或者 remove 掉，使其到 GC Root 无路径可达，处于不可触及状态，垃圾回收器就可以回收了。反之，一个存在 GC Root 的对象是不会被垃圾回收器回收掉的。

### 2.3.2 Leak Suspects

![Leak Suspects](https://assets.ng-tech.icu/item/20230406154849.png)

Leak Suspects 可以自动分析并提示可能存在的内存泄漏，可以直接定位到 Class 及对应的行数。

![java.lang.Class](https://assets.ng-tech.icu/item/20230406154913.png)

比如：这里问题一的描述，列出了一些比较大的实例。点击 Details 可以看到细节信息，另外还可点击 See stacktrace 查看具体的线程栈信息(可直接定位到具体某个类中的方法)。

![详情](https://assets.ng-tech.icu/item/20230406154944.png)

在 Details 详情页面 Shortest Paths To the Accumulation Point 表示 GC root 到内存消耗聚集点的最短路径，如果某个内存消耗聚集点有路径到达 GC root，则该内存消耗聚集点不会被当做垃圾被回收。

> 实战：在某项目中，其中几个 Tomcat 响应特别慢，打开 Java VisualVM 观察 Tomcat(pid xxx)-Visual GC 发现 Spaces-Old 升高，Graphs-GC Time 比较频繁且持续时间长、有尖峰(重启后过段时间又出现了)，最后通过 Leak Suspects 中的 See stacktrace 定位到某个查询接口，仔细排查代码后发现有个 BUG：在特定查询条件下会一次性查询几万的数据出来(因为脏数据)，处理过后恢复正常。

![Visual VM](https://assets.ng-tech.icu/item/20230406155029.png)

### 2.3.3 内存快照对比

为了更有效率的找出内存泄露的对象，一般会获取两个堆转储文件(先 dump 一个，隔段时间再 dump 一个)，通过对比后的结果可以很方便定位。

![内存快照对比](https://assets.ng-tech.icu/item/20230406155106.png)
