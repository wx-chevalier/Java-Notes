# Netty

Netty 是一个异步网络库，使 Java NIO 的功能更好用。在《[Concurrent-Series/并发 IO](https://github.com/wx-chevalier/Concurrent-Series?q=)》中我们讨论了 Reactor 模型，Netty 主要基于主从 Reactor 多线程模型发展出来的：

![Netty 模型](https://assets.ng-tech.icu/superbed/2021/07/29/61022c2a5132923bf8d96dbf.jpg)

# Netty 网络分层架构

Nettty 逻辑架构为典型网络分层架构设计，从下到上分别为网络通信层、事件调度层、服务编排层。

![Netty 分层架构](https://assets.ng-tech.icu/superbed/2021/07/29/61022c4a5132923bf8d9c41d.jpg)

- 网络通信层：它执行网络 I/O 操作，核心组件包含 BootStrap、ServerBootStrap、Channel。

  - Channel 通道，提供了基础的 API 用于操作网络 IO，比如 bind、connect、read、write、flush 等等。它以 JDK NIO Channel 为基础，提供了更高层次的抽象，同时屏蔽了底层 Socket 的复杂性。Channel 有多种状态，比如连接建立、数据读写、连接断开。随着状态的变化，Channel 处于不同的生命周期，背后绑定相应的事件回调函数。

- 事件调度层：它的核心组件包含 EventLoopGroup、EventLoop。

  - EventLoop 本质是一个线程池，主要负责接收 Socket I/O 请求，并分配事件循环器来处理连接生命周期中所发生的各种事件。

- 服务编排层：它的职责实现网络事件的动态编排和有序传播。
  - ChannelPipeline 基于责任链模式，方便业务逻辑的拦截和扩展；本质上它是一个双向链表将不同的 ChannelHandler 链接在一块，当 I/O 读写事件发生时, 会依次调用 ChannelHandler 对 Channel(Socket) 读取的数据进行处理。

# Links

- https://juejin.im/post/5df35adb6fb9a0163a4830e7
