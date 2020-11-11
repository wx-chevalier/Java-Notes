# Java 中的死锁案例

# synchronized 死锁

线程 thread1 先获取锁 locka，然后在同步块里嵌套竞争锁 lockb。而线程 thread2 先获取锁 lockb，然后在同步块里嵌套竞争锁 locka。

```java
package com.app.test;

import org.apache.poi.util.SystemOutLogger;

public class SyncDeadLock{
    private static Object locka = new Object();
    private static Object lockb = new Object();

    public static void main(String[] args){
        new SyncDeadLock().deadLock();
    }

    private void deadLock(){
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (locka){
                    try{
                        System.out.println(Thread.currentThread().getName()+" get locka ing!");
                        Thread.sleep(500);
                        System.out.println(Thread.currentThread().getName()+" after sleep 500ms!");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+" need lockb!Just waiting!");
                    synchronized (lockb){
                        System.out.println(Thread.currentThread().getName()+" get lockb ing!");
                    }
                }
            }
        },"thread1");

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lockb){
                    try{
                        System.out.println(Thread.currentThread().getName()+" get lockb ing!");
                        Thread.sleep(500);
                        System.out.println(Thread.currentThread().getName()+" after sleep 500ms!");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+" need locka! Just waiting!");
                    synchronized (locka){
                        System.out.println(Thread.currentThread().getName()+" get locka ing!");
                    }
                }
            }
        },"thread2");

        thread1.start();
        thread2.start();
    }
}
```

# 异常发生时未正确释放锁

```java
package com.app.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockDeadDemo {

    public static void main(String[] args){
        final DeadLockBean deadLockBean = new DeadLockBean();
        Thread threadA = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    deadLockBean.productDeadLock();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        },"threadA");
        Thread threadB = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(310);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    deadLockBean.productDeadLock();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        },"threadB");
        threadA.start();
        threadB.start();
        try {
            System.out.println("main线程即将被join");
            threadA.join();
            threadB.join();
            System.out.println("main线程从join中恢复");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class DeadLockBean{
        private Lock lock = new ReentrantLock();
        public void productDeadLock() throws Throwable {
            System.out.println(Thread.currentThread().getName() + "   进入了方法！");
            lock.lock();
            try{
                System.out.println(Thread.currentThread().getName() + "   已经执行了！");
                throw new Throwable("人为抛出异常Throwable");//关键代码行1，
                //throw new Exception("人为抛出异常Exception");//关键代码行2，不会死锁，会在catch(Exception e中被捕获)，嵌套lock.unlock()并释放
            }catch(Exception e){
                System.out.println(Thread.currentThread().getName()+"   发生异常catch！");
                //lock.unlock();//关键代码行3，不建议在这里释放，假如发生【关键代码行1】会产生死锁
            }finally{
                System.out.println(Thread.currentThread().getName()+"   发生异常finally！");
                lock.unlock();//关键代码行4，无论发生何种异常，均会释放锁。
            }
            //lock.unlock();//关键代码行5，假如发生不能捕获异常，将跳出方法体，不执行此处
            System.out.println(Thread.currentThread().getName() + "   tryCatch外释放锁！");
        }
    }
}
```

# Executor Saturation Deadlock | 线程饥饿死锁

在线程池中任务如果依赖其他任务的执行，那么就可能出现死锁。对于单 worker 线程的 Executor，如果在一个已经被提交的任务中提交另一个任务到 Executor 中就会发生死锁。

```java
class Task implements Runnable {
  private final ExecutorService executorService;

  public Task(ExecutorService executors) {
    this.executorService = executors;
  }

  @Override
  public void run() {
    System.out.println("Thread enter: " + Thread.currentThread().getName());
    Task2 task2 = new Task2();

    // 在任务中嵌套执行任务,由于线程池大小只有1,所以该线程永远不会被执行,而正在执行的线程也一直无法返回,这就照成了saturation deadlock
    Future<Boolean> future = executorService.submit(task2);
    try {
      Boolean aBoolean = future.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }
}

class Task2 implements Callable<Boolean> {

  @Override
  public Boolean call() throws Exception {
    SleepUtil.sleepSeconds(10);
    return true;
  }
}
```

如果要避免 Saturation Deadlock 的方法就需要尽量提交独立的任务不相互依赖的任务。
