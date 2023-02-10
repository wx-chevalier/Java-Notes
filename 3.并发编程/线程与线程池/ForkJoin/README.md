# ForkJoin

Fork/Join 框架是 Java7 提供了的一个用于并行执行任务的框架，是一个把大任务分割成若干个小任务，最终汇总每个小任务结果后得到大任务结果的框架。我们再通过 Fork 和 Join 这两个单词来理解下 Fork/Join 框架，Fork 就是把一个大任务切分为若干子任务并行的执行，Join 就是合并 这些子任务的执行结果，最后得到这个大任务的结果。比如计算 1+2+。。＋ 10000，可以分割成 10 个子任务，每个子任务分别对 1000 个数进行求和，最终汇总这 10 个子任务的结果。Fork/Join 的运行流程图如下：

![](http://cdn3.infoqstatic.com/statics_s1_20160405-0343u1/resource/articles/fork-join-introduction/zh/resources/21.png)

# 工作窃取算法

工作窃取(work-stealing)算法是指某个线程从其他队列里窃取任务来执行。工作窃取的运行流程图如下：

![工作窃取算法](https://ngte-superbed.oss-cn-beijing.aliyuncs.com/superbed/2021/07/16/60f197135132923bf88e6373.jpg)

假如我们需要做一个比较大的任务，我们可以把这个任务分割为若干互不依赖的子任务，为了减少线程间的竞争，于是把这些 子任务分别放到不同的队列里，并为每个队列创建一个单独的线程来执行队列里的任务，线程和队列一一对应，比如 A 线程负责处理 A 队列里的任务。但是有的线程 会先把自己队列里的任务干完，而其他线程对应的队列里还有任务等待处理。

干完活的线程与其等着，不如去帮其他线程干活，于是它就去其他线程的队列里窃取一个任务来执行。而在这时它们会访问同一个队列，所以为了减少窃取任务线程和被窃取任务线程之间的竞争，通常会使用双端队列，被窃取任务线程永远从双端队列 的头部拿任务执行，而窃取任务的线程永远从双端队列的尾部拿任务执行。

工作窃取算法的优点是充分利用线程进行并行计算，并减少了线程间的竞争，其缺点是在某些情况下还是存在竞争，比如双端队列里只有一个任务时。并且消耗了更多的系统资源，比如创建多个线程和多个双端队列。

# Links

- [concurrency-torture-testing-your-code-within-the-java-memory-model](http://zeroturnaround.com/rebellabs/concurrency-torture-testing-your-code-within-the-java-memory-model/)
- https://www.baeldung.com/java-fork-join
- [聊聊并发(八)——Fork/Join 框架介绍](http://www.infoq.com/cn/articles/fork-join-introduction)
