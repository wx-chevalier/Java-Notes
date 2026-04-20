# RateLimiter

RateLimiter 类是一个构造，它允许我们调节一些处理发生的速度。如果我们创建一个有 N 个许可证的 RateLimiter，这意味着每秒钟最多可以发出 N 个许可证。比方说，我们想把 doSomeLimitedOperation()的执行速度限制在每秒 2 次。我们可以使用它的 create()工厂方法创建一个 RateLimiter 实例。

```java
RateLimiter rateLimiter = RateLimiter.create(2);
```

接下来，为了从 RateLimiter 获得执行许可，我们需要调用 acquire()方法：

```java
rateLimiter.acquire(1);
```

为了检查是否有效，我们将对节制方法进行 2 次后续调用：

```java
long startTime = ZonedDateTime.now().getSecond();
rateLimiter.acquire(1);
doSomeLimitedOperation();
rateLimiter.acquire(1);
doSomeLimitedOperation();
long elapsedTimeSeconds = ZonedDateTime.now().getSecond() - startTime;
```

为了简化我们的测试，我们假设 doSomeLimitedOperation()方法立即完成。在这种情况下，两次对 acquire()方法的调用都不应该阻塞，经过的时间应该小于或低于一秒--因为两个许可证都可以立即获得。

```java
assertThat(elapsedTimeSeconds <= 1);
```

此外，我们可以在一次 acquire()调用中获得所有的许可证。

```java
@Test
public void givenLimitedResource_whenRequestOnce_thenShouldPermitWithoutBlocking() {
    // given
    RateLimiter rateLimiter = RateLimiter.create(100);

    // when
    long startTime = ZonedDateTime.now().getSecond();
    rateLimiter.acquire(100);
    doSomeLimitedOperation();
    long elapsedTimeSeconds = ZonedDateTime.now().getSecond() - startTime;

    // then
    assertThat(elapsedTimeSeconds <= 1);
}
```

例如，如果我们需要每秒发送 100 个字节，这就很有用。我们可以一次发送 100 次一个字节获取一个许可证。另一方面，我们可以一次发送所有 100 个字节，在一次操作中获取所有 100 个许可证。

# 阻塞方式获取权限

现在，让我们考虑一个稍微复杂的例子。我们将创建一个有 100 个许可证的 RateLimiter。然后我们将执行一个需要获取 1000 个许可证的动作。根据 RateLimiter 的规范，这样的动作至少需要 10 秒才能完成，因为我们每秒只能执行 100 个单位的动作。

```java
@Test
public void givenLimitedResource_whenUseRateLimiter_thenShouldLimitPermits() {
    // given
    RateLimiter rateLimiter = RateLimiter.create(100);

    // when
    long startTime = ZonedDateTime.now().getSecond();
    IntStream.range(0, 1000).forEach(i -> {
        rateLimiter.acquire();
        doSomeLimitedOperation();
    });
    long elapsedTimeSeconds = ZonedDateTime.now().getSecond() - startTime;

    // then
    assertThat(elapsedTimeSeconds >= 10);
}
```

注意，我们在这里是如何使用 acquire() 方法的--这是一个阻塞方法，我们在使用它时应该谨慎。当 acquire() 方法被调用时，它会阻塞执行线程，直到有许可。在没有参数的情况下调用 acquire() 方法和以 1 作为参数调用该方法是一样的，它将尝试获取一个许可证。

# 设置超时

RateLimiter API 还有一个非常有用的 acquire()方法，它接受超时和 TimeUnit 作为参数。当没有可用的许可证时，调用这个方法会使它等待指定的时间，然后超时--如果在超时内没有足够的可用许可证。当在给定的超时时间内没有可用的许可证时，它将返回 false。如果获取()成功，则返回 true。

```java
@Test
public void givenLimitedResource_whenTryAcquire_shouldNotBlockIndefinitely() {
    // given
    RateLimiter rateLimiter = RateLimiter.create(1);

    // when
    rateLimiter.acquire();
    boolean result = rateLimiter.tryAcquire(2, 10, TimeUnit.MILLISECONDS);

    // then
    assertThat(result).isFalse();
}
```
