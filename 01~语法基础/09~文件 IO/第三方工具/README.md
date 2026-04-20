# 第三方工具

# FileUtils

FileUtils 是 commons-io 包提供的辅助方法，可以通过如下方式引入：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.5</version>
</dependency>
```

其也是对文件的读取进行了适当封装：

```java
ClassLoader classLoader = getClass().getClassLoader();
File file = new File(classLoader.getResource("fileTest.txt").getFile());
String data = FileUtils.readFileToString(file, "UTF-8");
```

# IOUtils

IOUtils 也是 commons-io 包中提供的工具：

```java
FileInputStream fis = new FileInputStream("src/test/resources/fileToRead.txt");
String data = IOUtils.toString(fis, "UTF-8");
```
