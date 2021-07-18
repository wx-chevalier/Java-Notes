# SettableFuture

SettableFuture 继承了 AbstractFuture 抽象 类，AbstractFuture 抽象类实现了 ListenableFuture 接口，所以 SettableFuture 类也是 ListenableFuture 接口的一种实现，源码相当的简单，其中只包含了三个方法，一个用于创建 SettableFuture 实例的静态 create()方法；set 方法用于设置 Future 的值，返回是否设置成功，如果 Future 的值已经被设置或任务被取消，会返回 false；setException 与 set 方法类似，用于设置 Future 返回特定的异常信息，返回 exception 是否设置成功。

SettableFuture 类是 ListenableFuture 接口的一种实现，我们可以通过 SettableFuture 设置 Future 的返回 值，或者设置 Future 返回特定的异常信息，可以通过 SettableFuture 内部提供的静态方法 create()创建一个 SettableFuture 实例，下面是一个简单的例子：

```java
SettableFuture sf = SettableFuture.create();
//设置成功后返回指定的信息
sf.set("SUCCESS");
//设置失败后返回特定的异常信息
sf.setException(new RuntimeException("Fails"));
```

通过上面的例子，我们看到，通过 create()方法，我们可以创建一个默认的 ettableFuture 实例，当我们需要为 Future 实例设置一个返 回值时，我们可以通过 set 方法，设置的值就是 Future 实例在执行成功后将要返回的值；另外，当我们想要设置一个异常导致 Future 执行失败，我们 可以通过调用 setException 方法，我们将给 Future 实例设置指定的异常返回。

当我们有一个方法返回 Future 实例时，SettableFuture 会显得更有价值，但是已经有了 Future 的返回值，我们也不需要再去执行异步任 务获取返回值。
