# 基础概念

标准的 Java IO API，你操作的对象是字节流(byte stream)或者字符流(character stream)，而 NIO，你操作的对象是 channels 和 buffers。数据总是从一个 channel 读到一个 buffer 上，或者从一个 buffer 写到 channel 上。比如一个线程里，可以从一个 channel 读取数据到一个 buffer 上，在 channel 读取数据到 buffer 的时候，线程可以做其他的事情。当数据读取到 buffer 上后，线程可以继续处理它。
Java NIO 有三个核心组件(core components)：

- Channels
- Buffers
- Selectors
