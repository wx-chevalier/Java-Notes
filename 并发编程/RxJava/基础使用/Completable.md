# Completable

Completable 与 Observable 相似，唯一的例外是前者发出完成信号或错误信号，但不发出任何实际对象。Completable 类包含几种便捷的方法，可用于从不同的反应式源数据中创建或获取它。

# 创建与控制

```java
Completable.fromRunnable(() -> {});

// 自定义 create 方法
@Override
public Completable storeUserDbos(int accountId, List<UserEntity> users) {
    return Completable.create(emitter -> {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>(users.size());
        appendUsersInsertOperation(operations, accountId, users);
        getContentResolver().applyBatch(MessengerContentProvider.AUTHORITY, operations);
        emitter.onComplete();
    });
}

// 从其他类型中创建
Flowable<String> flowable = Flowable
  .just("request received", "user logged in");
Completable flowableCompletable = Completable
  .fromPublisher(flowable);
Completable singleCompletable = Single.just(1)
  .ignoreElement();
```

我们也可以使用 Completable.complete() 来立即结束当前的 Completable：

```java
Completable
  .complete()
  .subscribe(new DisposableCompletableObserver() {
    @Override
    public void onComplete() {
        System.out.println("Completed!");
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }
});
```

# 链式调用

当我们只在乎操作的成功时，我们可以在许多实际用例中采用 Completables 链：

- 全做或者不做，例如执行 PUT 请求以更新远程对象，然后在成功后更新本地数据库

- 事后记录和日记

- 编排几个动作，例如 提取动作完成后运行分析作业

```java
Completable first = Completable
  .fromSingle(Single.just(1));

Completable second = Completable
  .fromRunnable(() -> {});

Throwable throwable = new RuntimeException();

Completable error = Single.error(throwable)
  .ignoreElement();

first
  .andThen(second)
  .test()
  .assertComplete();
```

我们可以根据需要链接多个 Completables。同时，如果至少一个源未能完成，则结果 Completable 也将不会触发 `onComplete()`：

```java
first
  .andThen(second)
  .andThen(error)
  .test()
  .assertError(throwable);
```

此外，如果源之一是无限的或由于某种原因未达到 onComplete，则生成的 Completable 将永远不会触发 onComplete()或 onError()。

## 数组调用

```java
Completable.mergeArray(first, second)
  .test()
  .assertComplete();

Completable.mergeArray(first, second, error)
  .test()
  .assertError(throwable);

// 将 Flowable 转化为 Completable
Completable allElementsCompletable = Flowable
.just("request received", "user logged in")
.flatMapCompletable(message -> Completable
    .fromRunnable(() -> System.out.println(message))
);

allElementsCompletable
  .test()
  .assertComplete();
```
