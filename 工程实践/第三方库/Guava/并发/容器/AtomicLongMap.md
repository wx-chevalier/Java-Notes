# AtomicLongMap

```java
public class GuavaTest {
    //来自于Google的Guava项目
    AtomicLongMap<String> map = AtomicLongMap.create(); //线程安全，支持并发

    Map<String, Integer> map2 = new HashMap<String, Integer>(); //线程不安全
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(); //为map2增加并发锁

    ConcurrentHashMap<String, Integer> map3 = new ConcurrentHashMap<String, Integer>(); //线程安全，但也要注意使用方式

    private int taskCount = 100;
    CountDownLatch latch = new CountDownLatch(taskCount); //新建倒计时计数器，设置state为taskCount变量值

	public static void main(String[] args) {
	    GuavaTest t = new GuavaTest();
        t.test();
	}

	private void test(){
	    //启动线程
	    for(int i=1; i<=taskCount; i++){
	        Thread t = new Thread(new MyTask("key", 100));
	        t.start();
	    }

	    try {
	        //等待直到state值为0，再继续往下执行
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

	    System.out.println("##### AtomicLongMap #####");
	    for(String key : map.asMap().keySet()){
	        System.out.println(key + ": " + map.get(key));
	    }

        System.out.println("##### HashMap #####");
        for(String key : map2.keySet()){
            System.out.println(key + ": " + map2.get(key));
        }

        System.out.println("##### ConcurrentHashMap #####");
        for(String key : map3.keySet()){
            System.out.println(key + ": " + map3.get(key));
        }
	}

	class MyTask implements Runnable{
	    private String key;
        private int count = 0;

        public MyTask(String key, int count){
            this.key = key;
            this.count = count;
        }

        @Override
        public void run() {
            try {
                for(int i=0; i<count; i++){
                    map.incrementAndGet(key); //key值自增1后，返回该key的值

                    //对map2添加写锁，可以解决线程并发问题
                    lock.writeLock().lock();
                    try{
                        if(map2.containsKey(key)){
                            map2.put(key, map2.get(key)+1);
                        }else{
                            map2.put(key, 1);
                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }finally{
                        lock.writeLock().unlock();
                    }

                    //虽然ConcurrentHashMap是线程安全的，但是以下语句块不是整体同步，导致ConcurrentHashMap的使用存在并发问题
                    if(map3.containsKey(key)){
                        map3.put(key, map3.get(key)+1);
                    }else{
                        map3.put(key, 1);
                    }

                    //TimeUnit.MILLISECONDS.sleep(50); //线程休眠50毫秒
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown(); //state值减1
            }
        }
    }

}

##### AtomicLongMap #####
key: 10000
##### HashMap #####
key: 10000
##### ConcurrentHashMap #####
key: 9311
```
