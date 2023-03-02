# Dropwizard Metrics

Metrics，谷歌翻译就是度量的意思。当我们需要为某个系统某个服务做监控、做统计，就需要用到 Metrics。

举个栗子，一个图片压缩服务：

1. 每秒钟的请求数是多少（TPS）？
2. 平均每个请求处理的时间？
3. 请求处理的最长耗时？
4. 等待处理的请求队列长度？

又或者一个缓存服务：

1. 缓存的命中率？
2. 平均查询缓存的时间？

基本上每一个服务、应用都需要做一个监控系统，这需要尽量以少量的代码，实现统计某类数据的功能。以 Java 为例，目前最为流行的 metrics 库是来自 Coda Hale 的 [dropwizard/metrics](https://github.com/dropwizard/metrics)，该库被广泛地应用于各个知名的开源项目中。例如 Hadoop，Kafka，Spark，JStorm 中。

# Metric Registries

`MetricRegistry`类是 Metrics 的核心，它是存放应用中所有 metrics 的容器。也是我们使用 Metrics 库的起点。

```
MetricRegistry registry = new MetricRegistry();
```

每一个 metric 都有它独一无二的名字，Metrics 中使用句点名字，如 com.example.Queue.size。当你在 com.example.Queue 下有两个 metric 实例，可以指定地更具体：com.example.Queue.requests.size 和 com.example.Queue.response.size 。使用`MetricRegistry`类，可以非常方便地生成名字。

```
MetricRegistry.name(Queue.class, "requests", "size")
MetricRegistry.name(Queue.class, "responses", "size")
```

# Metrics 数据展示

Metrics 提供了 Report 接口，用于展示 metrics 获取到的统计数据。metrics-core 中主要实现了四种 reporter： JMX, console, SLF4J, 和 CSV。 在本文的例子中，我们使用 ConsoleReporter 。

# 五种 Metrics 类型

## Gauges

最简单的度量指标，只有一个简单的返回值，例如，我们想衡量一个待处理队列中任务的个数，代码如下：

```java
public class GaugeTest {

    public static Queue<String> q = new LinkedList<String>();

    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);

        registry.register(MetricRegistry.name(GaugeTest.class, "queue", "size"),
        new Gauge<Integer>() {

            public Integer getValue() {
                return q.size();
            }
        });

        while(true){
            Thread.sleep(1000);
            q.add("Job-xxx");
        }
    }
}

```

其中第 7 行和第 8 行添加了 ConsoleReporter，可以每秒钟将度量指标打印在屏幕上，理解起来会更清楚。但是对于大多数队列数据结构，我们并不想简单地返回 queue.size()，因为 java.util 和 java.util.concurrent 中实现的#size()方法很多都是 O(n) 的复杂度，这会影响 Gauge 的性能。

## Counters

Counter 就是计数器，Counter 只是用 Gauge 封装了 AtomicLong 。我们可以使用如下的方法，使得获得队列大小更加高效。

```java
public class CounterTest {

    public static Queue<String> q = new LinkedBlockingQueue<String>();

    public static Counter pendingJobs;

    public static Random random = new Random();

    public static void addJob(String job) {
        pendingJobs.inc();
        q.offer(job);
    }

    public static String takeJob() {
        pendingJobs.dec();
        return q.poll();
    }

    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);

        pendingJobs = registry.counter(MetricRegistry.name(Queue.class,"pending-jobs","size"));

        int num = 1;
        while(true){
            Thread.sleep(200);
            if (random.nextDouble() > 0.7){
                String job = takeJob();
                System.out.println("take job : "+job);
            }else{
                String job = "Job-"+num;
                addJob(job);
                System.out.println("add job : "+job);
            }
            num++;
        }
    }
}
```

运行之后的结果大致如下：

```sh
add job : Job-15
add job : Job-16
take job : Job-8
take job : Job-10
add job : Job-19
15-8-1 16:11:31 ============================================
-- Counters ----------------------------------------------
java.util.Queue.pending-jobs.size
             count = 5

```

## Meters

Meter 度量一系列事件发生的速率(rate)，例如 TPS。Meters 会统计最近 1 分钟，5 分钟，15 分钟，还有全部时间的速率。

```java
public class MeterTest {

    public static Random random = new Random();

    public static void request(Meter meter){
        System.out.println("request");
        meter.mark();
    }

    public static void request(Meter meter, int n){
        while(n > 0){
            request(meter);
            n--;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);

        Meter meterTps = registry.meter(MetricRegistry.name(MeterTest.class,"request","tps"));

        while(true){
            request(meterTps,random.nextInt(5));
            Thread.sleep(1000);
        }

    }
}
```

运行结果大致如下：

```java
request
15-8-1 16:23:25 ============================================

-- Meters ------------------------------------------------
com.alibaba.wuchong.metrics.MeterTest.request.tps
             count = 134
         mean rate = 2.13 events/second
     1-minute rate = 2.52 events/second
     5-minute rate = 3.16 events/second
    15-minute rate = 3.32 events/second
```

非常像 Unix 系统中 uptime 和 top 中的 load。

## Histograms

Histogram 统计数据的分布情况。比如最小值，最大值，中间值，还有中位数，75 百分位, 90 百分位, 95 百分位, 98 百分位, 99 百分位, 和 99.9 百分位的值(percentiles)。比如 request 的大小的分布：

```java
public class HistogramTest {
    public static Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);

        Histogram histogram = new Histogram(new ExponentiallyDecayingReservoir());
        registry.register(MetricRegistry.name(HistogramTest.class, "request", "histogram"), histogram);

        while(true){
            Thread.sleep(1000);
            histogram.update(random.nextInt(100000));
        }

    }
}

```
