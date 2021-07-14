# Condition

Java 里 sychronized 和 Lock & Condtion 都属于管程模型，Condition 在管程模型中代表的就是等待的条件。Condition 在 Lock 的基础上使用，在原来 Lock 的基础上实现了可以基于多种条件来让线程实现同步的效果增加了多个条件后，我们可以更有针对性，也更灵活的协调多种条件下的线程协调。

Condition 从拥有监控方法（wait,notify,notifyAll）的 Object 对象中抽离出来成为独特的对象，高效的让每个对象拥有更多的等待线程。和锁对比起来，如果说用 Lock 代替 synchronized，那么 Condition 就是用来代替 Object 本身的监控方法。Condition 实例跟 Object 本身的监控相似，同样提供 wait()方法让调用的线程暂时挂起让出资源，知道其他线程通知该对象转态变化，才可能继续执行。Condition 实例来源于 Lock 实例，通过 Lock 调用 newCondition()即可。Condition 较 Object 原生监控方法，可以保证通知顺序。

- await()：调用此方法，当前线程进入 Condition 阻塞队列等待，直到被其他线程通知、中断才退出等待，
- await(long time, TimeUnit unit)：调用此方法，当前线程进入 Condition 阻塞队列等待，直到被其他线程通知、中断、或者超时才退出等待，
- awaitNanos(long nanosTimeout)：调用此方法，当前线程进入 Condition 阻塞队列等待，直到被其他线程通知、中断、或者超时才退出等待，
- awaitUninterruptibly()：调用此方法，当前线程进入 Condition 阻塞队列等待，直到被其他线程通知，此方式阻塞的线程会忽略中断。
- signal()：唤醒一个等待对应 Condition 条件的线程，被唤醒的线程首先需要去获取对应的锁，成功获取锁之后线程才能继续执行，
- signalAll()：唤醒所有等待对应 Condition 条件的线程，被唤醒的线程首先需要去获取对应的锁，成功获取锁之后线程才能继续执行，

# 案例-实现消息队列

实现一个消息队列，有多个线程会往该队列里面写消息，同时也会存在多个线程会从消息里面读消息，队列的容量只有 10 个。

- 队列不为满条件：队列里面消息没有满的情况下才能往队里面添加消息。
- 队列不为空条件：消费消息的时候队列里必须有消息才进行消费。
- 加锁：因为是多线程所以需要防止消息被多个线程同时消费，同时也要防止写消息的时候一个线程存的消息被其他线程覆盖，所以队列操作的时候必须加锁。

```java
public class MyQueue {​
    private Lock lock = new ReentrantLock(); // 锁
    private List listQueue = new ArrayList(); // 存储消息的集合
    private Condition notNull = lock.newCondition(); // 队列不为空
    private Condition notFull = lock.newCondition(); // 队列不为满
    ​​
    public void add(String message) {
        lock.lock(); // 操作队列先加锁
        try {
            // 队列满了，通知消费者线程，生产线程阻塞
            while (listQueue.size() >= 10) {
                notNull.signal();
                System.out.println("队列已满" + Thread.currentThread().getName() + "等待");
                notFull.await();
            }​
            // 往队列添加一条消息，同时通知消费者有新消息了
            listQueue.add(message);
            System.out.println(Thread.currentThread().getName() + "生产一条消息");
            notNull.signal(); // 通知消费者线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock(); // 释放锁
        }
    }​​

    public void remove() {
        lock.lock(); // 操作队列先加锁
        try {
            // 队列空了，通知生产线程，消费线程阻塞
            while (listQueue.size() == 0) {
                System.out.println("队列已空" + Thread.currentThread().getName() + "等待");
                notNull.await();
            }
            // 队列删除一条消息，同时通知生产者队列有位置了
            listQueue.get(0);
            listQueue.remove(0);
            System.out.println(Thread.currentThread().getName() + "消费一条消息");
            notFull.signal(); // 同时通知生产者队列
            ​
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }​
}
```

测试类：

```java
public class TestMyQueue {​
    private static MyQueue myQueue = new MyQueue();​
    public static void main(String[] args) throws Exception {
        for (int i = 1; i <= 10; i++) {
            Thread provider = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        myQueue.add("消息");
                    }
                }
            }, "生产线程" + i);​
            Thread consumer = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        myQueue.remove();
                    }
                }
            }, "消费线程" + i);
            provider.start();
            consumer.start();
        }​
    }​
}
```

# 实现原理
