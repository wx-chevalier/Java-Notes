# NIO

Java NIO，被称为新 IO(New IO)，是 Java 1.4 引入的，用来替代 IO API 的，它是基于 IO 复用技术的非阻塞 IO，不是异步 IO。在早期的 JDK1.4 和 1.5update10 版本之前，JDK 的 Selector 基于 select/poll 模型实现；在 JDK1.5 update10 和 Linux2 .6 以上版本，Sun 优化了 Selector 的实现，它在底层使用了 epoll 替换了 select/poll。在 JDK1.7 提供的 AIO 新增了异步的套接字通道，它是真正的异步 IO,在异步 IO 操作的时候可以传递信号变量，当操作完成之后会回调相关的方法，异步 IO 也称为 AIO。

对于网络 IO 相关的基础知识可以参考 [Linux 网络 IO](https://ng-tech.icu/DistributedSystem-Series/#/?q=Linux网络IO) 以及[并发 IO](https://ng-tech.icu/DistributedSystem-Series/#/?q=并发IO) 的相关章节。

# BIO，NIO 与 AIO

## BIO

BIO 编程方式通常是在 JDK1.4 版本之前常用的编程方式。编程实现过程为：首先在服务端启动一个 ServerSocket 来监听网络请求，客户端启动 Socket 发起网络请求，默认情况下 ServerSocket 回建立一个线程来处理此请求，如果服务端没有线程可用，客户端则会阻塞等待或遭到拒绝。且建立好的连接，在通讯过程中，是同步的。在并发处理效率上比较低。大致结构如下：

![BIO 结构](https://s1.ax1x.com/2020/03/23/8o8kDS.md.png)

同步并阻塞，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销，当然可以通过线程池机制改善。BIO 方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，JDK1.4 以前的唯一选择，但程序直观简单易理解。使用线程池机制改善后的 BIO 模型图如下:

![多线程下的 BIO 模型](https://s1.ax1x.com/2020/03/23/8o8BDO.md.png)

## NIO

Unblocking IO（New IO）同步非阻塞的编程方式。NIO 本身是基于事件驱动思想来完成的，其主要想解决的是 BIO 的大并发问题，NIO 基于 Reactor，当 socket 有流可读或可写入 socket 时，操作系统会相应的通知引用程序进行处理，应用再将流读取到缓冲区或写入操作系统。也就是说，这个时候，已经不是一个连接就要对应一个处理线程了，而是有效的请求，对应一个线程，当连接没有数据时，是没有工作线程来处理的。

NIO 的最重要的地方是当一个连接创建后，不需要对应一个线程，这个连接会被注册到多路复用器上面，所以所有的连接只需要一个线程就可以搞定，当这个线程中的多路复用器进行轮询的时候，发现连接上有请求的话，才开启一个线程进行处理，也就是一个请求一个线程模式。在 NIO 的处理方式中，当一个请求来的话，开启线程进行处理，可能会等待后端应用的资源(JDBC 连接等)，其实这个线程就被阻塞了，当并发上来的话，还是会有 BIO 一样的问题。

![NIO 示例](https://s1.ax1x.com/2020/03/23/8T2Av6.png)

同步非阻塞，服务器实现模式为一个请求一个通道，即客户端发送的连接请求都会注册到多路复用器上，多路复用器轮询到连接有 I/O 请求时才启动一个线程进行处理。NIO 方式适用于连接数目多且连接比较短（轻操作）的架构，比如聊天服务器，并发局限于应用中，编程复杂，JDK1.4 开始支持。

- Buffer: ByteBuffer,CharBuffer,ShortBuffer,IntBuffer,LongBuffer,FloatBuffer,DoubleBuffer。

- Channel: SocketChannel,ServerSocketChannel。

- Selector: Selector,AbstractSelector

- SelectionKey: OP_READ,OP_WRITE,OP_CONNECT,OP_ACCEPT

## AIO

Asynchronous IO： 异步非阻塞的编程方式。与 NIO 不同，当进行读写操作时，只须直接调用 API 的 read 或 write 方法即可。这两种方法均为异步的，对于读操作而言，当有流可读取时，操作系统会将可读的流传入 read 方法的缓冲区，并通知应用程序；对于写操作而言，当操作系统将 write 方法传递的流写入完毕时，操作系统主动通知应用程序。即可以理解为，read/write 方法都是异步的，完成后会主动调用回调函数。在 JDK1.7 中，这部分内容被称作 NIO.2，主要在 java.nio.channels 包下增加了下面四个异步通道：

- AsynchronousSocketChannel

- AsynchronousServerSocketChannel

- AsynchronousFileChannel

- AsynchronousDatagramChannel

异步非阻塞，服务器实现模式为一个有效请求一个线程，客户端的 I/O 请求都是由 OS 先完成了再通知服务器应用去启动线程进行处理。AIO 方式使用于连接数目多且连接比较长（重操作）的架构，比如相册服务器，充分调用 OS 参与并发操作，编程比较复杂，JDK7 开始支持。

# 基础概念

标准的 Java IO API，你操作的对象是字节流(byte stream)或者字符流(character stream)，而 NIO，你操作的对象是 channels 和 buffers。数据总是从一个 channel 读到一个 buffer 上，或者从一个 buffer 写到 channel 上。比如一个线程里，可以从一个 channel 读取数据到一个 buffer 上，在 channel 读取数据到 buffer 的时候，线程可以做其他的事情。当数据读取到 buffer 上后，线程可以继续处理它。
Java NIO 有三个核心组件(core components)：

- Channels
- Buffers
- Selectors

# 链接

- https://parg.co/kmG
- https://mp.weixin.qq.com/s/uOPio2rBwcIK7R8O6FNCkA
