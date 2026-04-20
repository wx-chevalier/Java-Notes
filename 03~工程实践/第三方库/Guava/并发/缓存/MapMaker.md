# MapMaker

我们将 MapMaker 作为一个提供最基本缓存功能的类进行学 习，MapMaker 类使用了流畅的接口 API，允许我们快速的构造 ConcurrentHashMap，我们来看下面的例子：

```java
ConcurrentMap<String, Book> books = new
        MapMaker().concurrencyLevel(2)
        .softValues()
        .makeMap();

// 更为完整的示例
ConcurrentMap<String, Object> mapAll = new MapMaker()
        .concurrencyLevel(8)
        .weakValues()
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .maximumSize(100)
        .makeComputingMap(
          new Function<String, Object>() {
            public Object apply(String key) {
            //绑定获取数据的方法
              return createObject(key);
         }

            private Object createObject(String key) {
                // TODO Auto-generated method stub
                return null;
            }});
```

上面的例子中，我们构造了一个 ConcurrentHashMap，使用 String 类型作为 key，使用 Book 对象作为 value 值，通过对 ConcurrentHashMap 声明的泛型进行指定，我们首先调用了 concurrencyLevel()方法，设置了我们允许在 map 中并发修改的数量，我们还指定了 softValues()方法，这样 map 中的 value 值都包裹在一个 SoftReference(软引用)对象中，可以在内存过低的时候被当作垃圾回收。

其他我们可以指定的方法还包括：weakKeys()和 weakValues()，但是 MapMaker 没有提供 softKeys()，当我们给 keys 或 values 使用 WeakReferences(弱引用)或 SoftReference(软引用)时，如果键值的其中一个被当做垃圾回收，整个键值对就 会从 map 中移除，剩余的部分并不会暴露给客户端。

最后值得注意的一点：MapMaker 中的 softValues()方法在最近的几个 guava 版本中，已经被标注为 Deprecated，MapMaker 中的缓存机制已经被移动到 com.google.common.cache.CacheBuilder 中，MapMaker 中的 softValues()方法也已经被替换为 com.google.common.cache.CacheBuilder#softValues，CacheBuilder 的实现是来自 MapMaker 分支的一个简单增强版 API。
