# CyclicBarrier

CyclicBarrier 是加强版的 CountDownLatch，上面讲的是一次性“关门放狗”，而循环栅栏则是集齐了指定数量的线程，在资源都允许的情况下同时执行，然后下一批同样的操作，周而复始。

```java

class CyclicBarrierTaskTest implements Runnable {
    private CyclicBarrier cyclicBarrier;

    private int timeout;

    public CyclicBarrierTaskTest(CyclicBarrier cyclicBarrier, int timeout) {
        this.cyclicBarrier = cyclicBarrier;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        TestCyclicBarrier.print("正在running...");
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
            TestCyclicBarrier.print("到达栅栏处，等待其它线程到达");
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        TestCyclicBarrier.print("所有线程到达栅栏处，继续执行各自线程任务...");
    }
}

public class TestCyclicBarrier {

    public static void print(String str) {
        SimpleDateFormat dfdate = new SimpleDateFormat("HH:mm:ss");
        System.out.println("["+ dfdate.format(new Date()) + "]"
                + Thread.currentThread().getName() + str);
    }

    public static void main(String[] args) {
        int count = 5;

        ExecutorService es = Executors.newFixedThreadPool(count);

        CyclicBarrier barrier = new CyclicBarrier(count, new Runnable() {

            @Override
            public void run() {
                TestCyclicBarrier.print("所有线程到达栅栏处,可以在此做一些处理...");
            }
        });
        for (int i = 0; i < count; i++)
            es.execute(new CyclicBarrierTaskTest(barrier, (i + 1) * 1000));
    }

}

[11:07:00]pool-1-thread-2 正在running...
[11:07:00]pool-1-thread-1 正在running...
[11:07:00]pool-1-thread-5 正在running...
[11:07:00]pool-1-thread-3 正在running...
[11:07:00]pool-1-thread-4 正在running...
[11:07:01]pool-1-thread-1 到达栅栏处，等待其它线程到达
[11:07:02]pool-1-thread-2 到达栅栏处，等待其它线程到达
[11:07:03]pool-1-thread-3 到达栅栏处，等待其它线程到达
[11:07:04]pool-1-thread-4 到达栅栏处，等待其它线程到达
[11:07:05]pool-1-thread-5 到达栅栏处，等待其它线程到达
[11:07:05]pool-1-thread-5 所有线程到达栅栏处,可以在此做一些处理...
[11:07:05]pool-1-thread-1 所有线程到达栅栏处，继续执行各自线程任务...
[11:07:05]pool-1-thread-2 所有线程到达栅栏处，继续执行各自线程任务...
[11:07:05]pool-1-thread-5 所有线程到达栅栏处，继续执行各自线程任务...
[11:07:05]pool-1-thread-3 所有线程到达栅栏处，继续执行各自线程任务...
[11:07:05]pool-1-thread-4 所有线程到达栅栏处，继续执行各自线程任务...
```
