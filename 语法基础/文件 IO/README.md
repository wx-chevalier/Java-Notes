# Java IO 基础

Java.io 包几乎包含了所有操作输入、输出需要的类。所有这些流类代表了输入源和输出目标；Java.io 包中的流支持很多种格式，比如：基本类型、对象、本地化字符集等等。一个流可以理解为一个数据的序列。输入流表示从一个源读取数据，输出流表示向一个目标写数据。Java 为 IO 提供了强大的而灵活的支持，使其更广泛地应用到文件传输和网络编程中。

Java 是面向对象的编程语言，对象是对现实实体的抽象表述。所以 Java API 中流(Stream)是对一连串数据的抽象，同时定义了一些操作，write 和 read 等。所以现实实体，只要包含数据和对数据的读写操作都可以表示为流。OutputStream 类和 InputStream 类，是 2 个抽象类，分别对应输出、输入流，所有其它流对象，都是其子类。　比如文件，文件本质是保存在存储设备中的一连串数据，在 Java API 中抽象为 FileOutputStream 类和 FileInputStream 类，文件的读写可以通过对相应流的读写实现的。

![IO 类](http://www.runoob.com/wp-content/uploads/2013/12/iostream2xx.png)

# TBD

- http://tutorials.jenkov.com/java-io/index.html
