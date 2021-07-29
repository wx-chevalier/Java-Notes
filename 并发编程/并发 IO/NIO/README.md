# 基础概念

标准的 Java IO API，你操作的对象是字节流(byte stream)或者字符流(character stream)，而 NIO，你操作的对象是 channels 和 buffers。数据总是从一个 channel 读到一个 buffer 上，或者从一个 buffer 写到 channel 上。比如一个线程里，可以从一个 channel 读取数据到一个 buffer 上，在 channel 读取数据到 buffer 的时候，线程可以做其他的事情。当数据读取到 buffer 上后，线程可以继续处理它。

Java NIO 有三个核心组件(core components)：Channels、Buffers、Selectors。

## Channel（通道）

Channel 和传统 IO 中的 Stream 流很相似。只不过 Stream 是单向的，要么是 InputStream 只能进行读，要么是 OutputStream 只能进行写。而 Channel 是双向的。以下是常用的几种通道：

- FileChannel: 可以向文件读写数据
- SocketChanel: 以 TCP 来向网络连接的两端读写数据
- ServerSocketChanel: 能够监听客户端发起的 TCP 连接，并为每个 TCP 连接创建一个新的 SocketChannel
- DatagramChannel: 以 UDP 协议来向网络连接的两端读写数据

## Buffer（缓冲区）

Buffer，故名思意，缓冲区，实际上是一个容器，是一个连续数组。Channel 提供从文件、网络读取数据的渠道，但是读取或写入的数据都必须经由 Buffer。

![Buffer（缓冲区）](https://s3.ax1x.com/2021/03/01/6P0q5q.png)

上图描述了客户端向服务端发送数据，然后服务端接收数据的全部过程。客户端发送数据时，必须先将数据存入 Buffer 中，然后将 Buffer 中的内容写入通道。服务端这边接收数据必须通过 Channel 将数据读入到 Buffer 中，然后再从 Buffer 中取出数据来处理。

## Selector（选择器）

Selector 类是 NIO 的核心类，Selector 能够检测多个注册的通道 Channel 上是否有事件发生，如果有，便获取事件然后针对每个事件进行相应的处理。这样一来，只用一个单线程就可以管理多个通道，也就是管理多个连接。这样使得只有在连接真正有读写事件发生时，才会调用函数来进行读写。这种方式不必为每个连接都创建一个线程，不用去维护多个线程，大大地减少了系统开销。

与 Selector 有关的一个关键类是 SelectionKey，一个 SelectionKey 表示一个到达的事件，这 2 个类构成了服务端处理业务的关键逻辑。下图表示单线程运行的 Selector 同时管理多个通道 （连接）。

![多通道](https://s3.ax1x.com/2021/03/01/6PDMXF.png)

Selector 的创建和可以设置监听的事件如下：

```java
Selector selector = Selector.open();  // 创建 Selector
SelectionKey key = channel.register(selector, Selectionkey.OP_READ);   // 将通道注册到 Selector 上
// register 的第二个参数指明监听哪些事件，可选的值有：
// Connect - 连接就绪
// Accept - 接收就绪
// Read - 读就绪
// Write - 写就绪
// 与之对应的检测 Channel 中什么事件或操作已经就绪的函数为：
selectionKey.isConnectable();  // 是否连接就绪
selectionKey.isAcceptable();    // 是否接收就绪
selectionKey.isReadable();      // 是否读就绪
selectionKey.isWritable();        // 是否写就绪
```

# Java NIO 代码示例

下面结合示例代码，进一步理解 Java NIO 中的各项概念。

```java
// 创建 Selector
Selector selector = Selector.open();
// 创建 ServerSocketChannel 并绑定到指定端口
ServerSocketChannel server = ServerSocketChannel.open();

server.bind(new InetSocketAddress("127.0.0.1", 1234));
// 设置 ServerSocketChannel 为 non-blocking
server.configureBlocking(false);
// 将 server channel 注册到 selector 并设置监听 OP_ACCEPT 事件
server.register(selector, SelectionKey.OP_ACCEPT);

while (true) {
    // selector 被 select() 阻塞
    // select() 会把注册的事件添加到 SelectionKeys (只增不减)
    if (selector.select() == 0) {
        continue;
    }
    // 获得 SelectionKey 集合, 每一个 Selectionkey 对应着一个已注册的 Channel
    Set<SelectionKey> keys = selector.selectedKeys();
    for (SelectionKey key : keys) {
        if (key.isAcceptable()) {
	    // 获取与 client 相连的 SocketChannel
	    SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
	    // 同样设置为 non-blocking
	    channel.configureBlocking(false);
            // 这里可以向 client 发送信息
            channel.write(ByteBuffer.wrap(new String("向客户端发送了一条信息!").getBytes()));
            // 将此 channel 的 OP_READ 事件注册到 selector
            channel.register(this.selector, SelectionKey.OP_READ);
	}
	// 如果 channel 可读
	if (key.isReadable()) {
	    SocketChannel channel = (SocketChannel) key.channel();
	    // 创建读缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            channel.read(buffer);
	}
	// 由于 select() 对 SelectionKey 集合只增不减 这里需手动移除 key
	keys.remove(key);
    }
}
```
