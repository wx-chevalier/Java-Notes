# Guava Service

Guava Service 接口表示一个具有操作状态的对象，并带有启动和停止的方法。例如，Web 服务器，RPC 服务器和计时器可以实现 Service 接口。管理这些服务的状态（需要适当的启动和关闭管理）并非易事，特别是在涉及多线程或日程调度 Schedule 的情况下。Guava 提供了一些框架来为你管理状态逻辑和同步细节。

# 使用 Service

服务 Service 的正常生命周期是：Service.State.NEW 到 Service.State.STARTING 到 Service.State.RUNNING 到 Service.State.STOPPING 到 Service.State.TERMINATED。已停止的服务无法重新启动。如果服务在启动、运行或停止的地方失败，它将进入 Service.State.FAILED 状态。

如果服务是 NEW，则可以使用 startAsync()异步启动服务。因此，你应该将应用程序结构化为在每个服务启动时都有唯一的位置（统一）。使用异步 stopAsync()方法来停止服务也是类似的。但是与 startAsync()不同，多次调用此方法是安全的。这使得处理关闭服务时可能发生的竞争成为可能。

服务还提供了几种方法来等待服务转换完成。

- 异步使用 addListener()。addListener()允许你添加一个 Service.Listener，它将在服务的每个状态转换时调用。注意：如果在添加监听器时服务不是 NEW 新建的，那么任何已经发生的状态转换都不会在监听器上重新触发。
- 同步使用 awaitRunning()。这是不中断的，不会抛出已检查的异常，并在服务启动完成后返回。如果服务启动失败，则会抛出 IllegalStateException。同样，awaitTerminated()等待服务达到终端状态（TERMINATED 或 FAILED）。两种方法都具有重载的允许指定超时时间。

Service 接口是微妙而复杂的。我们不建议直接实现它。相反，请使用 guava 中的抽象基类之一作为实现的基础。每个基类都支持特定的线程模型。

## AbstractIdleService

AbstractIdleService 框架实现了 Service，该服务在处于“运行”状态时不需要执行任何操作，因此在运行时不需要线程；但具有要执行的启动和关闭操作。实现这样的服务与扩展 AbstractIdleService 以及实现 startUp()和 shutDown()方法一样容易。

```java
protected void startUp() {
  servlets.add(new GcStatsServlet());
}
protected void shutDown() {}
```

请注意，对 GcStatsServlet 的任何查询都已经有一个在运行的线程。在服务运行时，我们不需要该服务自行执行任何操作。

## AbstractExecutionThreadService

AbstractExecutionThreadService 在单个线程中执行启动、运行和关闭操作。你必须重写 run()方法，并且它必须响应停止请求。例如，你可以在工作循环中执行操作：

```java
public void run() {
  while (isRunning()) {
    // perform a unit of work
  }
}
```

或者，你可以以任何方式重写，从而使 run()返回。重写 startUp()和 shutDown()是可选的，但是将为你管理服务状态。

```java
protected void startUp() {
  dispatcher.listenForConnections(port, queue);
}
protected void run() {
  Connection connection;
  while ((connection = queue.take() != POISON)) {
    process(connection);
  }
}
protected void triggerShutdown() {
  dispatcher.stopListeningForConnections(queue);
  queue.put(POISON);
}
```

请注意，start()调用你的 startUp()方法，为你创建一个线程，并在该线程中调用 run()。stop()调用 triggerShutdown()方法并等待线程死亡。

## AbstractScheduledService

AbstractScheduledService 在运行时执行一些周期性任务。子类实现 runOneIteration()来指定任务的一次迭代，以及熟悉的 startUp()和 shutDown()方法。

要描述执行日程调度 schedule，你必须实现 scheduler()方法。通常，你将使用 AbstractScheduledService.Scheduler 提供的日程 schedule 之一，newFixedRateSchedule(initialDelay, delay, TimeUnit)或 newFixedDelaySchedule(initialDelay, delay, TimeUnit)，与 ScheduledExecutorService 中熟悉的方法相对应。可以使用 CustomScheduler 来实现自定义日程调度 schedule。

## AbstractService

当你需要执行自己的手动线程管理时，请直接重写 AbstractService。通常，上述实现之一应该可以为你提供良好的服务，但是当你在建模某种提供自己的线程语义作为 Service 时，建议你实现 AbstractService，因为你有自己特定的线程需求。

要实现 AbstractService，必须实现 2 个方法。

- doStart()：doStart()是第一次调用 startAsync()直接调用的，你的 doStart()方法应执行所有的初始化，如果启动成功，则最终调用 notifyStarted()，如果启动失败，则最终调用 notifyFailed()。
- doStop()：doStop()是由第一次调用 stopAsync()直接调用的，你的 doStop()方法应关闭服务，如果关闭成功，则最终调用 notifyStopped()，如果关闭失败，则最终调用 notifyFailed()。

你的 doStart 和 doStop 方法应该是快速的。如果你需要进行昂贵的初始化，例如读取文件、打开网络连接或任何可能阻塞的操作，则应考虑将该工作移至另一个线程。

# 使用 ServiceManager

除了 Service 框架实现之外，Guava 还提供了 ServiceManager 类，它使涉及多个服务实现的某些操作更加容易。使用 Services 集合创建一个新的 ServiceManager。然后，你可以管理它们：

- startAsync()将启动管理下的所有服务。与 Service#startAsync()类似，如果所有服务都是 NEW，则只能调用此方法一次。
- stopAsync()将停止管理下的所有服务。
- addListener 将添加一个 ServiceManager.Listener，它将在主要状态转换时调用。
- awaitHealthy()将等待所有服务达到 RUNNING 状态。
- awaitStopped()将等待所有服务达到终端状态。

或检查它们：

- 如果所有服务都是 RUNNING，则 isHealthy()返回 true。
- servicesByState()返回按状态索引的所有服务的一致快照。
- startupTimes()返回管理下的 Service 到该服务启动所需的时间（以毫秒为单位）的映射。返回的映射保证按启动时间排序。

虽然建议通过 ServiceManager 管理服务生命周期，但是通过其他机制启动的状态转换不会影响其方法的正确性。例如，如果服务是由 startAsync()之外的某种机制启动的，则监听器将在适当的时候被调用，而 awaitHealthy()仍将按预期工作。ServiceManager 强制执行的唯一要求是，在构造 ServiceManager 时，所有 Service 都必须是 NEW。
