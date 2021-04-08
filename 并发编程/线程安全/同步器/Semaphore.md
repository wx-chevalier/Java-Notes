# Semaphore

锁和同步块同时只能允许单个线程访问共享资源，这个明显有些单调，部分场景其实可以允许多个线程访问，这个时候信号量实例就派上用场了。信号量逻辑上维持了一组许可证，线程调用 acquire()阻塞直到许可证可用后才能执行。执行 release() 意味着释放许可证，实际上信号量并没有真正的许可证，只是采用了计数功能来实现这个功能。

# 案例-多资源竞争

举个例子，如下代码，十个线程竞争三个资源，一开始有三个线程可以直接运行，剩下的七个线程只能阻塞等到其它线程使用资源完毕才能执行；

```java
public class SemaphoreTest {

    public static void print(String str){
        SimpleDateFormat dfdate = new SimpleDateFormat("HH:mm:ss");
        System.out.println("[" + dfdate.format(new Date()) + "]" + Thread.currentThread().getName() + str);
    }

    public static void main(String[] args) {
        // 线程数目
        int threadCount = 10;
        // 资源数目
        Semaphore semaphore = new Semaphore(3);

        ExecutorService es = Executors.newFixedThreadPool(threadCount);

        // 启动若干线程
        for (int i = 0; i < threadCount; i++)
            es.execute(new ConsumeResourceTask((i + 1) * 1000, semaphore));
    }
}

class ConsumeResourceTask implements Runnable {
    private Semaphore semaphore;
    private int sleepTime;

    public ConsumeResourceTask(int sleepTime, Semaphore semaphore) {
        this.sleepTime = sleepTime;
        this.semaphore = semaphore;
    }

    public void run() {
        try {
            //获取资源
            semaphore.acquire();
            SemaphoreTest.print(" 占用一个资源...");
            TimeUnit.MILLISECONDS.sleep(sleepTime);
            SemaphoreTest.print(" 资源使用结束，释放资源");
            //释放资源
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

[10:30:11]pool-1-thread-1 占用一个资源...
[10:30:11]pool-1-thread-2 占用一个资源...
[10:30:11]pool-1-thread-3 占用一个资源...
[10:30:12]pool-1-thread-1 资源使用结束，释放资源
[10:30:12]pool-1-thread-4 占用一个资源...
[10:30:13]pool-1-thread-2 资源使用结束，释放资源
[10:30:13]pool-1-thread-5 占用一个资源...
[10:30:14]pool-1-thread-3 资源使用结束，释放资源
[10:30:14]pool-1-thread-8 占用一个资源...
[10:30:16]pool-1-thread-4 资源使用结束，释放资源
[10:30:16]pool-1-thread-6 占用一个资源...
[10:30:18]pool-1-thread-5 资源使用结束，释放资源
[10:30:18]pool-1-thread-9 占用一个资源...
[10:30:22]pool-1-thread-8 资源使用结束，释放资源
[10:30:22]pool-1-thread-7 占用一个资源...
[10:30:22]pool-1-thread-6 资源使用结束，释放资源
[10:30:22]pool-1-thread-10 占用一个资源...
[10:30:27]pool-1-thread-9 资源使用结束，释放资源
[10:30:29]pool-1-thread-7 资源使用结束，释放资源
[10:30:32]pool-1-thread-10 资源使用结束，释放资源
```
