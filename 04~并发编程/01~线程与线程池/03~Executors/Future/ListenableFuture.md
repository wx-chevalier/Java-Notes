# ListenableFuture

ListenableFuture 是 Guava 对原有 Future 的增强，可以用于监听 Future 任务的执行状况，是执行成功还是执行失败，并提供响应的接口用于对不同结果的处理。

```java
ListenableFuture listenable = service.submit(...);

Futures.addCallback(listenable, new FutureCallback<Object>() {
    @Override
    public void onSuccess(Object o) {}

    @Override
    public void onFailure(Throwable throwable) {}
})
```

ListenableFuture 适用场景：

- 如果一个主任务开始执行，然后需要执行各个小任务，并且需要等待返回结果，统一返回给前端，此时 Future 和 ListenableFuture 作用几乎差不多，都是通过 get()方法阻塞等待每个任务执行完毕返回。
- 如果一个主任务开始执行，然后执行各个小任务，主任务不需要等待每个小任务执行完，不需要每个小任务的结果，此时用 ListenableFuture 非常合适，它提供的 FutureCallBack 接口可以对每个任务的成功或失败单独做出响应。
- 如果我们希望各个小任务一旦计算完成就拿到结果展示给用户(push 出去)或者做另外的计算，就必须使用另一个线程不断的查询计算状态。这样做，代码复杂，而且效率低下。
