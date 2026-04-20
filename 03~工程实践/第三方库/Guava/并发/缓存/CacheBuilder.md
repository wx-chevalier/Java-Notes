# Cache Builder

# 基础使用

使用 Cache 时，我们优先读取缓存，当缓存不存在时，则从实际的数据存储获取，如 DB、磁盘、网络等，即 get-if-absent-compute。guava 提供了 CacheLoader 机制，允许我们通过设置 Loader 来自动完成这一过程。如：

```java
Cache<String, User> cache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES);
user = cache.get(name, () -> {
    User value = query(key); // from databse, disk, etc.
    return value;
});

LoadingCache<Key, Graph> graphs = CacheBuilder.newBuilder()
 .maximumSize(1000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .removalListener(MY_LISTENER)
        .build(
            new CacheLoader<Key, Graph>() {
                public Graph load(Key key) throws AnyException {
                    return createExpensiveGraph(key);
                }
        });
```

## Null 处理

不过需要注意一点的是，CacheLoader 不允许返回的数据为 NULL，否则会抛出异常：CacheLoader returned null for key。所以我们需要保证查找的数据必须存在，或者抛出异常外部处理。在某些情况下，我们的数据可能确实不在，比如用户管理模块，我们在新增数据前，要查询原来是否已经存在该用户，那么这时候抛出异常也不合适，此时可以使用 Optional 来优化 CacheLoader：

```java
LoadingCache<String, Optional<User>> cache = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(
     new CacheLoader<String, Optional<User>>() {
        @Override
        public Optional<User> load(String name) throws Exception {
        User value = query(key);//from databse, disk, etc.
        return Optional.ofNullable(value);
        }
    }
);
```

这样我们保证了 CacheLoader 返回值不为 NULL，而业务数据是否存在，只需要判断 Optional.ifPresent()就行了，同时 Optional 的其他函数在业务逻辑中也是非常有用的。

## Callable

所有类型的 Guava Cache，不管有没有自动加载功能，都支持 get(K, Callable)方法。这个方法返回缓存中相应的值，或者用给定的 Callable 运算并把结果加入到缓存中。在整个加载方法完成前，缓存项相关的可观察状态都不会更改。这个方法简便地实现了模式”如果有缓存则返回；否则运算、缓存、然后返回”。

```java
Cache<Key, Graph> cache = CacheBuilder.newBuilder()
    .maximumSize(1000)
        .build(); // look Ma, no CacheLoader
...
try {
    // If the key wasn't in the "easy to compute" group, we need to
    // do things the hard way.
    cache.get(key, new Callable<Key, Graph>() {
        @Override
       public Value call() throws AnyException {
            return doThingsTheHardWay(key);
    }
    });

} catch (ExecutionException e) {
    throw new OtherException(e.getCause());
}
```

## 显式插入

使用 cache.put(key, value)方法可以直接向缓存中插入值，这会直接覆盖掉给定键之前映射的值。使用 Cache.asMap()视图提供的任何方法也能修改缓存。但请注意，asMap 视图的任何方法都不能保证缓存项被原子地加载到缓存中。进一步说，asMap 视图的原子运算在 Guava Cache 的原子加载范畴之外，所以相比于 Cache.asMap().putIfAbsent(K,V)，Cache.get(K, Callable<V>) 应该总是优先使用。

# 缓存回收

一个残酷的现实是，我们几乎一定没有足够的内存缓存所有数据。你你必须决定：什么时候某个缓存项就不值得保留了？Guava Cache 提供了三种基本的缓存回收方式：基于容量回收、定时回收和基于引用回收。

## 基于容量的回收（size-based eviction）

如果要规定缓存项的数目不超过固定值，只需使用 CacheBuilder.maximumSize(long)。缓存将尝试回收最近没有使用或总体上很少使用的缓存项。注意：在缓存项的数目达到限定值之前，缓存就可能进行回收操作；通常来说，这种情况发生在缓存项的数目逼近限定值时。

另外，不同的缓存项有不同的“权重”（weights）——例如，如果你的缓存值，占据完全不同的内存空间，你可以使用 CacheBuilder.weigher(Weigher)指定一个权重函数，并且用 CacheBuilder.maximumWeight(long)指定最大总重。在权重限定场景中，除了要注意回收也是在重量逼近限定值时就进行了，还要知道重量是在缓存创建时计算的，因此要考虑重量计算的复杂度。

```java
LoadingCache<Key, Graph> graphs = CacheBuilder.newBuilder()
        .maximumWeight(100000)
        .weigher(new Weigher<Key, Graph>() {
            public int weigh(Key k, Graph g) {
                return g.vertices().size();
            }
        })
        .build(
            new CacheLoader<Key, Graph>() {
                public Graph load(Key key) { // no checked exception
                    return createExpensiveGraph(key);
                }
            });
```

## 定时回收（Timed Eviction）

CacheBuilder 提供两种定时回收的方法：

- expireAfterAccess(long, TimeUnit)：缓存项在给定时间内没有被读/写访问，则回收。请注意这种缓存的回收顺序和基于大小回收一样。
- expireAfterWrite(long, TimeUnit)：缓存项在给定时间内没有被写访问（创建或覆盖），则回收。如果认为缓存数据总是在固定时候后变得陈旧不可用，这种回收方式是可取的。

## 显式清除

任何时候，你都可以显式地清除缓存项，而不是等到它被回收：

- 个别清除：Cache.invalidate(key)
- 批量清除：Cache.invalidateAll(keys)
- 清除所有缓存项：Cache.invalidateAll()

# 刷新

刷新和回收不太一样。正如 LoadingCache.refresh(K)所声明，刷新表示为键加载新值，这个过程可以是异步的。在刷新操作进行时，缓存仍然可以向其他线程返回旧值，而不像回收操作，读缓存的线程必须等待新值加载完成。

如果刷新过程抛出异常，缓存将保留旧值，而异常会在记录到日志后被丢弃[swallowed]。重载 CacheLoader.reload(K, V)可以扩展刷新时的行为，这个方法允许开发者在计算新值时使用旧的值。

```java
LoadingCache<Key, Graph> graphs = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .refreshAfterWrite(1, TimeUnit.MINUTES)
        .build(
            new CacheLoader<Key, Graph>() {
                public Graph load(Key key) { // no checked exception
                    return getGraphFromDatabase(key);
                }
         public ListenableFuture<Key, Graph> reload(final Key key, Graph prevGraph) {
                    if (neverNeedsRefresh(key)) {
                        return Futures.immediateFuture(prevGraph);
                    }else{
                        // asynchronous!
                        ListenableFutureTask<Key, Graph> task=ListenableFutureTask.create(new Callable<Key, Graph>() {
                        public Graph call() {
                                return getGraphFromDatabase(key);
                            }
                        });
                        executor.execute(task);
                       return task;
                    }
                }
            });
```

CacheBuilder.refreshAfterWrite(long, TimeUnit)可以为缓存增加自动定时刷新功能。和 expireAfterWrite 相反，refreshAfterWrite 通过定时刷新可以让缓存项保持可用，但请注意：缓存项只有在被检索时才会真正刷新（如果 CacheLoader.refresh 实现为异步，那么检索不会被刷新拖慢）。因此，如果你在缓存上同时声明 expireAfterWrite 和 refreshAfterWrite，缓存并不会因为刷新盲目地定时重置，如果缓存项没有被检索，那刷新就不会真的发生，缓存项在过期时间后也变得可以回收。

# 其他特性

## 统计

CacheBuilder.recordStats()用来开启 Guava Cache 的统计功能。统计打开后，Cache.stats()方法会返回 CacheStats 对象以提供如下统计信息：

- hitRate()：缓存命中率；
- averageLoadPenalty()：加载新值的平均时间，单位为纳秒；
- evictionCount()：缓存项被回收的总数，不包括显式清除。

此外，还有其他很多统计信息。这些统计信息对于调整缓存设置是至关重要的，在性能要求高的应用中我们建议密切关注这些数据。

## asMap 视图

asMap 视图提供了缓存的 ConcurrentMap 形式，但 asMap 视图与缓存的交互需要注意：

- cache.asMap()包含当前所有加载到缓存的项。因此相应地，cache.asMap().keySet()包含当前所有已加载键;
- asMap().get(key)实质上等同于 cache.getIfPresent(key)，而且不会引起缓存项的加载。这和 Map 的语义约定一致。

所有读写操作都会重置相关缓存项的访问时间，包括 Cache.asMap().get(Object)方法和 Cache.asMap().put(K, V)方法，但不包括 Cache.asMap().containsKey(Object)方法，也不包括在 Cache.asMap()的集合视图上的操作。比如，遍历 Cache.asMap().entrySet()不会重置缓存项的读取时间。

## 中断

缓存加载方法（如 Cache.get）不会抛出 InterruptedException。我们也可以让这些方法支持 InterruptedException，但这种支持注定是不完备的，并且会增加所有使用者的成本，而只有少数使用者实际获益。详情请继续阅读。

Cache.get 请求到未缓存的值时会遇到两种情况：当前线程加载值；或等待另一个正在加载值的线程。这两种情况下的中断是不一样的。等待另一个正在加载值的线程属于较简单的情况：使用可中断的等待就实现了中断支持；但当前线程加载值的情况就比较复杂了：因为加载值的 CacheLoader 是由用户提供的，如果它是可中断的，那我们也可以实现支持中断，否则我们也无能为力。

如果用户提供的 CacheLoader 是可中断的，为什么不让 Cache.get 也支持中断？从某种意义上说，其实是支持的：如果 CacheLoader 抛出 InterruptedException，Cache.get 将立刻返回（就和其他异常情况一样）；此外，在加载缓存值的线程中，Cache.get 捕捉到 InterruptedException 后将恢复中断，而其他线程中 InterruptedException 则被包装成了 ExecutionException。

原则上，我们可以拆除包装，把 ExecutionException 变为 InterruptedException，但这会让所有的 LoadingCache 使用者都要处理中断异常，即使他们提供的 CacheLoader 不是可中断的。如果你考虑到所有非加载线程的等待仍可以被中断，这种做法也许是值得的。但许多缓存只在单线程中使用，它们的用户仍然必须捕捉不可能抛出的 InterruptedException 异常。即使是那些跨线程共享缓存的用户，也只是有时候能中断他们的 get 调用，取决于那个线程先发出请求。

对于这个决定，我们的指导原则是让缓存始终表现得好像是在当前线程加载值。这个原则让使用缓存或每次都计算值可以简单地相互切换。如果老代码（加载值的代码）是不可中断的，那么新代码（使用缓存加载值的代码）多半也应该是不可中断的。

如上所述，Guava Cache 在某种意义上支持中断。另一个意义上说，Guava Cache 不支持中断，这使得 LoadingCache 成了一个有漏洞的抽象：当加载过程被中断了，就当作其他异常一样处理，这在大多数情况下是可以的；但如果多个线程在等待加载同一个缓存项，即使加载线程被中断了，它也不应该让其他线程都失败（捕获到包装在 ExecutionException 里的 InterruptedException），正确的行为是让剩余的某个线程重试加载。为此，我们记录了一个 bug。然而，与其冒着风险修复这个 bug，我们可能会花更多的精力去实现另一个建议 AsyncLoadingCache，这个实现会返回一个有正确中断行为的 Future 对象。
