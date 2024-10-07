# Disq

Simple, fast, disk-backed queue and task executor for Java 8.

**Features:**
* `PersistentQueue<T>`: a disk-backed blocking queue;
* `Disq<T>`: a disk-backed task executor.

## Usage

Disq is available through Maven Central repository, just add the following
dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>net.intelie.disq</groupId>
    <artifactId>disq</artifactId>
    <version>0.12</version>
</dependency>
```

Then, you can use it like that:

```java
Processor<String> processor = x -> {
    System.out.println(x);
};

Disq<String> disq = Disq.builder(processor)
        .setDirectory("my_queue")
        .setThreadCount(8)
        .setMaxSize(1024 * 1024 * 1024) //1GB
        .build();

disq.submit("some item");
disq.submit("another item");
```
