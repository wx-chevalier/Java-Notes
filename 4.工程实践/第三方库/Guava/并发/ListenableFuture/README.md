# ListenableFuture

并发是一个困难的问题，但是通过使用功能强大且简单的抽象可以大大简化并发。为了简化问题，Guava 定义了 ListenableFuture 接口并继承了 JDK concurrent 包下的 Future 接口，ListenableFuture 允许你注册回调方法(callbacks)，在运算（多线程执行）完成的时候进行调用。

传统的 Future 表示异步计算的结果：可能已经或可能尚未完成产生结果的计算。Future 可以作为正在进行的计算的句柄，是服务向我们提供结果的承诺。ListenableFuture 允许你在计算完成后或在计算已经完成时立即注册要执行的回调。这个简单的附加功能使它可以有效地支持基本 Future 接口无法支持的许多操作。

# 创建

对应于 JDK 的 ExecutorService.submit(Callable)方法来启动异步计算，Guava 提供了 ListeningExecutorService 接口，该接口在 ExecutorService 返回正常 Future 的任何地方都返回 ListenableFuture。要将 ExecutorService 转换为 ListeningExecutorService，只需使用 MoreExecutors.listeningDecorator(ExecutorService)。

```java
ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

ListenableFuture<Explosion> explosion = service.submit(
    new Callable<Explosion>() {
      public Explosion call() {
        return pushBigRedButton();
      }
    });

Futures.addCallback(
    explosion,
    new FutureCallback<Explosion>() {
      // we want this handler to run immediately after we push the big red button!
      public void onSuccess(Explosion explosion) {
        walkAwayFrom(explosion);
      }
      public void onFailure(Throwable thrown) {
        battleArchNemesis(); // escaped the explosion!
      }
    },
    service);

```

另外，如果你要从基于 FutureTask 的 API 进行转换，则 Guava 提供了 ListenableFutureTask.create(Callable)和 ListenableFutureTask.create(Runnable, V)。与 JDK 不同，ListenableFutureTask 不能直接扩展。如果你更喜欢抽象的方式设置 future 值，而不是实现一种计算该值的方法，请考虑扩展 AbstractFuture 或直接使用 SettableFuture。

如果必须将另一个 API 提供的 Future 转换为 ListenableFuture，则别无选择，只能使用重量级的 JdkFutureAdapters.listenInPoolThread(Future)将 Future 转换为 ListenableFuture。只要有可能，最好修改原始代码以返回

## 添加监听

ListenableFuture 添加的基本操作是 addListener(Runnable, Executor)，它指定当此 Future 表示的计算完成时，指定的 Runnable 将在指定的 Executor 上运行。大多数用户更喜欢使用 Futures.addCallback(ListenableFuture, FutureCallback, Executor)。FutureCallback 实现两种方法：

- onSuccess(V)，如果 Future 成功，则根据其结果执行的操作
- onFailure(Throwable)，如果 Future 失败，则根据失败执行的操作

```java
@RestController
@RequestMapping(value = "testThread")
public class TestThread {
    /**线程池*/
    static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 10, 60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(200),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 数据处理
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "parse", method = RequestMethod.GET)
    public String parse() throws Exception{
        List<String> result = new ArrayList<>();
        List<String> list = new ArrayList<>();

        //模拟原始数据
        for(int i = 0; i < 1211;i ++){
            list.add(i+"-");
            System.out.println("添加原始数据:"+i);
        }

        int size = 50;//切分粒度，每size条数据，切分一块，交由一条线程处理
        int countNum = 0;//当前处理到的位置
        int count = list.size()/size;//切分块数
        int threadNum = 0;//使用线程数
        if(count*size != list.size()){
            count ++;
        }

        final CountDownLatch countDownLatch = new CountDownLatch(count);

        //使用 Guava 的 ListeningExecutorService 装饰线程池
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(threadPoolExecutor);

        while (countNum < count*size){
            //切割不同的数据块，分段处理
            threadNum ++;
            countNum += size;
            MyCallable myCallable = new MyCallable();
            myCallable.setList(ImmutableList.copyOf(
                    list.subList(countNum-size,list.size() > countNum ? countNum : list.size())));

            ListenableFuture listenableFuture = executorService.submit(myCallable);

            //回调函数
            Futures.addCallback(listenableFuture, new FutureCallback<List<String>>() {
                //任务处理成功时执行
                @Override
                public void onSuccess(List<String> list) {
                    countDownLatch.countDown();
                    System.out.println("第h次处理完成");
                    result.addAll(list);
                }

                //任务处理失败时执行
                @Override
                public void onFailure(Throwable throwable) {
                    countDownLatch.countDown();
                    System.out.println("处理失败："+throwable);
                }
            });

        }

        //设置时间，超时了直接向下执行，不再阻塞
        countDownLatch.await(3,TimeUnit.SECONDS);

        result.stream().forEach(s -> System.out.println(s));
        System.out.println("------------结果处理完毕，返回完毕,使用线程数量："+threadNum);

        return "处理完了";
    }
}
```

```java
public class MyCallable implements Callable{

    private List<String> list ;
    @Override
    public Object call() throws Exception {
        List<String> listReturn = new ArrayList<>();
        //模拟对数据处理，然后返回
        for(int i = 0;i < list.size();i++){
            listReturn.add(list.get(i)+"：处理时间："+System.currentTimeMillis()+"---:处理线程："+Thread.currentThread());
        }

        return listReturn;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
```

## 异步操作链

使用 ListenableFuture 的最重要原因是可以拥有复杂的异步操作链：

```java
ListenableFuture<RowKey> rowKeyFuture = indexService.lookUp(query);
AsyncFunction<RowKey, QueryResult> queryFunction =
  new AsyncFunction<RowKey, QueryResult>() {
    public ListenableFuture<QueryResult> apply(RowKey rowKey) {
      return dataService.read(rowKey);
    }
  };
ListenableFuture<QueryResult> queryFuture =
    Futures.transformAsync(rowKeyFuture, queryFunction, queryExecutor);

```

ListenableFuture 可以有效地支持许多其他操作，而单独的 Future 不能支持。不同的执行者可以执行不同的操作，并且单个 ListenableFuture 可以有多个操作在等待它。

| 方法          | 描述                                                                                                                                                                                          |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| transform     | 加一个回调函数                                                                                                                                                                                |
| allAsList     | 返回一个 ListenableFuture ，该 ListenableFuture 返回的 result 是一个 List，List 中的值是每个 ListenableFuture 的返回值，假如传入的其中之一 fails 或者 cancel，这个 Future fails 或者 canceled |
| successAsList | 返回一个 ListenableFuture ，该 Future 的结果包含所有成功的 Future，按照原来的顺序，当其中之一 Failed 或者 cancel，则用 null 替代                                                              |

```java
public class ListeningFutureDemo {

    public static void main(String[] args) {
        final CountDownLatch latch = new CountDownLatch(1);
        final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

        ListenableFuture<String> explosion = service.submit(new Callable<String>() {
            public String call() throws Exception {
                System.out.println("任务线程正在执行...");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "任务线程的结果";
            }
        });

        ListenableFuture<String> first = Futures.transform(explosion, new AsyncFunction<String, String>() {
            public ListenableFuture<String> apply(final String input) throws Exception {
                ListenableFuture<String> temp = service.submit(new Callable<String>() {
                    public String call() throws Exception {
                        System.out.println("第1个回调线程正在执行...");
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return input + " & 第1个回调线程的结果 ";
                    }
                });
                return temp;
            }
        }, service);


        ListenableFuture<String> second = Futures.transform(first, new AsyncFunction<String, String>() {
            public ListenableFuture<String> apply(final String input) throws Exception {
                ListenableFuture<String> temp = service.submit(new Callable<String>() {
                    public String call() throws Exception {
                        System.out.println("第2个回调线程正在执行...");
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return input + " & 第2个回调线程的结果 ";
                    }
                });
                return temp;
            }
        }, service);

        ListenableFuture<String> third = Futures.transform(second, new AsyncFunction<String, String>() {
            public ListenableFuture<String> apply(final String input) throws Exception {
                ListenableFuture<String> temp = service.submit(new Callable<String>() {
                    public String call() throws Exception {
                        System.out.println("第3个回调线程正在执行...");
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return input + " & 第3个回调线程的结果 ";
                    }
                });
                return temp;
            }
        }, service);

        ListenableFuture<String> forth = Futures.transform(third, new AsyncFunction<String, String>() {
            public ListenableFuture<String> apply(final String input) throws Exception {
                ListenableFuture<String> temp = service.submit(new Callable<String>() {
                    public String call() throws Exception {
                        System.out.println("第4个回调线程正在执行...");
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return input + " & 第4个回调线程的结果 ";
                    }
                });
                return temp;
            }
        }, service);

        Futures.addCallback(forth, new FutureCallback<String>() {
            public void onSuccess(String result) {
                latch.countDown();
                System.out.println("结果: " + result);
            }

            public void onFailure(Throwable t) {
                System.out.println(t.getMessage());
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.shutdown();
    }

}
```

## 与 CompletableFuture 之间互相转化

```java
public class ListenableFutureAdapter<T> {

    private final ListenableFuture<T> listenableFuture;
    private final CompletableFuture<T> completableFuture;

    public ListenableFutureAdapter(ListenableFuture<T> listenableFuture) {
        this.listenableFuture = listenableFuture;
        this.completableFuture = new CompletableFuture<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                boolean cancelled = listenableFuture.cancel(mayInterruptIfRunning);
                super.cancel(cancelled);
                return cancelled;
            }
        };

        Futures.addCallback(this.listenableFuture, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                completableFuture.complete(result);
            }

            @Override
            public void onFailure(Throwable ex) {
                completableFuture.completeExceptionally(ex);
            }
        });
    }

    public CompletableFuture<T> getCompletableFuture() {
        return completableFuture;
    }

    public static final <T> CompletableFuture<T> toCompletable(ListenableFuture<T> listenableFuture) {
        ListenableFutureAdapter<T> listenableFutureAdapter = new ListenableFutureAdapter<>(listenableFuture);
        return listenableFutureAdapter.getCompletableFuture();
    }

}
```

# 实践案例

## 异步阻塞

主线程分配一个任务给子线程后，然后继续运行，子线程运算出结果后把结果返回给主线程，在这段代码里主线程依旧是阻塞的。

```java
@Slf4j
public class Test2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final Date date = new Date(1976);
        // 这里可以用lambda表达式，但是贴代码的时候会很不直观，不知道是Runnable还是Callable
        ListenableFutureTask<Object> futureTask = ListenableFutureTask.create(new Runnable() {
            @Override
            public void run() {
                log.info(Thread.currentThread().getName() + " Runnable任务启动....");
                date.setTime(new Date().getTime());
            }
        }, date);

        new Thread(futureTask).start();

        // 睡眠一会 等待子线程执行完
        Thread.sleep(1000L);
        log.info(Thread.currentThread().getName() + "当前时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));

        ListenableFutureTask<Object> futureTask2 = ListenableFutureTask.create(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                log.info(Thread.currentThread().getName() + " Callable任务启动....");
                Thread.sleep(5000L);
                return "当前时间:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        });

        new Thread(futureTask2).start();
        log.info(Thread.currentThread().getName() + futureTask2.get());
        log.info(Thread.currentThread().getName() + "主线程继续执行");

    }

}

16:25:46.665 [Thread-1] INFO com.pipiha.Collections.Concurrency.ListenableFutuTest.Test2 - Thread-1 Runnable任务启动....
16:25:47.663 [main] INFO com.pipiha.Collections.Concurrency.ListenableFutuTest.Test2 - main当前时间2020-05-30 16:25:46
16:25:47.699 [Thread-2] INFO com.pipiha.Collections.Concurrency.ListenableFutuTest.Test2 - Thread-2 Callable任务启动....
16:25:52.792 [main] INFO com.pipiha.Collections.Concurrency.ListenableFutuTest.Test2 - main当前时间:2020-05-30 16:25:52
16:25:52.792 [main] INFO com.pipiha.Collections.Concurrency.ListenableFutuTest.Test2 - main主线程继续执行
```

注意观察运行结果的线程名和日志输出时间。需要注意的是由于没有执行成功的异步回调，实际上我们的主线程依旧是阻塞的，必须等子线程运行完，才能拿到结果。

## 异步非阻塞

MoreExecutors.listeningDecorator 是为了将 JDK 的 ExecutorService 转换为 ListeningExecutorService，ListeningExecutorService 总是会返回 Future。与上一节代码相比增加 Futures.addCallback 方法，该方法会根据子线程运算后的状态，成功或者失败回调不同的逻辑。

```java
@Slf4j
public class Test3 {
    public static void main(String[] args) {

        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

        ListenableFutureTask<Object> futureTask = ListenableFutureTask.create(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                log.info(Thread.currentThread().getName() + " Callable任务启动....");
                Thread.sleep(5000L);
                return "当前时间:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        });

        Futures.addCallback(futureTask, new FutureCallback<Object>() {

            public void onSuccess(Object calCultorResult) {
                log.info(Thread.currentThread().getName() + "子线程执行成功,计算结果{}", calCultorResult);
            }

            public void onFailure(Throwable thrown) {

            }
        }, service);

        new Thread(futureTask).start();
        log.info(Thread.currentThread().getName() + "主线程继续执行");

    }

}
```

分析打印的日志，可以看到主线程在提交任务过后就紧着执行，没有被阻塞而停下来。还可以发现，计算的执行过程是由 pool-1-thread-1 执行的，回调逻辑是由线程池里面的 pool-1-thread-2 处理的。

## 多任务协作

如果我们有有一个大任务比较耗时，拆分成子任务 1 和子任务 2，子任务 2 的执行又依赖于子任务 1 的计算结果。

```java
@Slf4j
public class FutureTest {
    public static void main(String[] args) {

        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

        ListenableFuture<Integer> task1Future = service.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                log.info("任务1开始执行...");
                int washTime = new Random().nextInt(10) + 1;
                Thread.sleep(washTime);
                if (washTime > 7) {
                    throw new RuntimeException("任务1开始因执行时间过长而失败");
                }
                return washTime;
            }
        });

        AsyncFunction<Integer, Boolean> asyncFunction = new AsyncFunction<Integer, Boolean>() {
            public ListenableFuture<Boolean> apply(Integer rowKey) {
                log.info("任务1执行成功，计算结果{}", rowKey);

                ListenableFuture<Boolean> hot = service.submit(new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        log.info("任务2开始执行，返回固定结果true");
                        return true;
                    }
                });
                return hot;
            }
        };

        ListenableFuture<Boolean> queryFuture = Futures.transformAsync(task1Future, asyncFunction, service);

        Futures.addCallback(queryFuture, new FutureCallback<Boolean>() {
            public void onSuccess(Boolean explosion) {
                log.info("任务1，任务2均执行成功");
            }

            public void onFailure(Throwable thrown) {
                log.error("", thrown);
            }
        }, service);

    }

}
```
