# ListeningExecutorService

由于普通的线程池，返回的 Future，功能比较单一；Guava 定义了 ListenableFuture 接口并继承了 JDK concurrent 包下的 Future 接口，ListenableFuture 允许你注册回调方法(callbacks)，在运算（多线程执行）完成的时候进行调用。

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

## 任务处理

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

# SettableFuture

SettableFuture 继承了 AbstractFuture 抽象 类，AbstractFuture 抽象类实现了 ListenableFuture 接口，所以 SettableFuture 类也是 ListenableFuture 接口的一种实现，源码相当的简单，其中只包含了三个方法，一个用于创建 SettableFuture 实例的静态 create()方法；set 方法用于设置 Future 的值，返回是否设置成功，如果 Future 的值已经被设置或任务被取消，会返回 false；setException 与 set 方法类似，用于设置 Future 返回特定的异常信息，返回 exception 是否设置成功。

SettableFuture 类是 ListenableFuture 接口的一种实现，我们可以通过 SettableFuture 设置 Future 的返回 值，或者设置 Future 返回特定的异常信息，可以通过 SettableFuture 内部提供的静态方法 create()创建一个 SettableFuture 实例，下面是一个简单的例子：

```java
SettableFuture sf = SettableFuture.create();
//设置成功后返回指定的信息
sf.set("SUCCESS");
//设置失败后返回特定的异常信息
sf.setException(new RuntimeException("Fails"));
```

通过上面的例子，我们看到，通过 create()方法，我们可以创建一个默认的 ettableFuture 实例，当我们需要为 Future 实例设置一个返 回值时，我们可以通过 set 方法，设置的值就是 Future 实例在执行成功后将要返回的值；另外，当我们想要设置一个异常导致 Future 执行失败，我们 可以通过调用 setException 方法，我们将给 Future 实例设置指定的异常返回。

当我们有一个方法返回 Future 实例时，SettableFuture 会显得更有价值，但是已经有了 Future 的返回值，我们也不需要再去执行异步任 务获取返回值。
