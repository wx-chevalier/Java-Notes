# AtomicReference

AtomicReference 和 AtomicInteger 非常类似，不同之处就在于 AtomicInteger 是对整数的封装，而 AtomicReference 则对应普通的对象引用；也就是它可以保证你在修改对象引用时的线程安全性。

AtomicReference 是作用是对”对象”进行原子操作。 提供了一种读和写都是原子性的对象引用变量。原子意味着多个线程试图改变同一个 AtomicReference(例如比较和交换操作)将不会使得 AtomicReference 处于不一致的状态。

# 常用方法

```java
public class SimpleObject {
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "SimpleObject{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public SimpleObject(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
```

## 使用 null 初始值创建新的 AtomicReference

```java
public class AtomicReferenceTest {
    public static void main(String[] args) {
        //1、使用 null 初始值创建新的 AtomicReference。
        AtomicReference<SimpleObject> atomicReference = new AtomicReference<>();
        atomicReference.set(new SimpleObject("test1" , 10));
        SimpleObject simpleObject = atomicReference.get();
        System.out.println("simpleObject  Value: " + simpleObject.toString());
    }
}
```

## 使用给定的初始值创建新的 AtomicReference

```java
public class AtomicReferenceTest {
    public static void main(String[] args) {
        //2、使用给定的初始值创建新的 AtomicReference。
        AtomicReference<SimpleObject> atomicReference1 = new AtomicReference<>(new SimpleObject("test2",20));
        SimpleObject simpleObject1 = atomicReference1.get();
        System.out.println("simpleObject  Value: " + simpleObject1.toString());
    }
}
```

## 如果当前值 == 预期值，则以原子方式将该值设置为给定的更新值。

```java
public class AtomicReferenceTest {
    public static void main(String[] args) {
        //3、如果当前值 == 预期值，则以原子方式将该值设置为给定的更新值。
        SimpleObject test = new SimpleObject("test3" , 30);
        AtomicReference<SimpleObject> atomicReference2 = new AtomicReference<>(test);
        Boolean bool = atomicReference2.compareAndSet(test, new SimpleObject("test4", 40));
        System.out.println("simpleObject  Value: " + bool);
    }
}
```

## 以原子方式设置为给定值，并返回旧值，先获取当前对象，在设置新的对象

```java
public class AtomicReferenceTest {
    public static void main(String[] args) {
        //4、以原子方式设置为给定值，并返回旧值，先获取当前对象，在设置新的对象
        SimpleObject test1 = new SimpleObject("test5" , 50);
        AtomicReference<SimpleObject> atomicReference3 = new AtomicReference<>(test1);
        SimpleObject simpleObject2 = atomicReference3.getAndSet(new SimpleObject("test6",50));
        SimpleObject simpleObject3 = atomicReference3.get();
        System.out.println("simpleObject  Value: " + simpleObject2.toString());
        System.out.println("simpleObject  Value: " + simpleObject3.toString());
    }
}
```
