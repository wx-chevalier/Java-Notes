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
    @RequestMapping(value = "parse",method = RequestMethod.GET)
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

        //使用Guava的ListeningExecutorService装饰线程池
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
