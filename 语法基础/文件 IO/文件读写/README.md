# 文件读写

# BufferedReader

BufferedReader 是支持同步的，而 Scanner 不支持。如果我们处理多线程程序，BufferedReader 应当使用。BufferedReader 相对于 Scanner 有足够大的缓冲区内存。

Scanner 有很少的缓冲区(1KB 字符缓冲)相对于 BufferedReader(8KB 字节缓冲)，但是这是绰绰有余的。BufferedReader 相对于 Scanner 来说要快一点，因为 Scanner 对输入数据进行类解析，而 BufferedReader 只是简单地读取字符序列。

```java
// 要找 resource 目录下的某个文件
BufferedReader bufferedReaderB = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/B/B1.txt")));
String url = this.getClass().getResource("/userFile.properties").getFile();

// 找某个类
File f = new File(MyClass.class.getProtectionDomain().getCodeSource().getLocation().getPath());
String[] classpathEntries = classpath.split(File.pathSeparator);
```

## 按行读取

```java
public class ReadSelectedLine{
    // 读取文件指定行。
    static void readAppointedLineNumber(File sourceFile, int lineNumber)
         throws IOException {
     FileReader in = new FileReader(sourceFile);
     LineNumberReader reader = new LineNumberReader(in);
     String s = "";
     if (lineNumber <= 0 || lineNumber > getTotalLines(sourceFile)) {
         System.out.println("不在文件的行数范围(1至总行数)之内。");
         System.exit(0);
     }
     int lines = 0;
     while (s != null) {
         lines++;
         s = reader.readLine();
         if((lines - lineNumber) == 0) {
          System.out.println(s);
          System.exit(0);
         }
     }
     reader.close();
     in.close();
    }
    // 文件内容的总行数。
    static int getTotalLines(File file) throws IOException {
     FileReader in = new FileReader(file);
     LineNumberReader reader = new LineNumberReader(in);
     String s = reader.readLine();
     int lines = 0;
     while (s != null) {
         lines++;
         s = reader.readLine();
     }
     reader.close();
     in.close();
     return lines;
    }

    /**
     * 读取文件指定行。
     */
    public static void main(String[] args) throws IOException {
     // 指定读取的行号
     int lineNumber = 2;
     // 读取文件
     File sourceFile = new File("D:/java/test.txt");
     // 读取指定的行
     readAppointedLineNumber(sourceFile, lineNumber);
     // 获取文件的内容的总行数
     System.out.println(getTotalLines(sourceFile));
    }
}
```

## InputStream

在大部分情况下，我们会首先将文件转化为 InputStream 流，然后从其中读取文本：

```java
private String readFromInputStream(InputStream inputStream)
  throws IOException {
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br
      = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        while ((line = br.readLine()) != null) {
            resultStringBuilder.append(line).append("\n");
        }
    }
  return resultStringBuilder.toString();
}
```

对于绝对路径的文件可以直接以绝对路径读取，而对于 Classpath 下的文件，则可以以 getResourceAsStream 方式读取：

```java
// 使用某个类
Class clazz = FileOperationsTest.class;
InputStream inputStream = clazz.getResourceAsStream("/fileTest.txt");
String data = readFromInputStream(inputStream);

// 使用当前的 ClassLoader
ClassLoader classLoader = getClass().getClassLoader();
InputStream inputStream = classLoader.getResourceAsStream("fileTest.txt");
String data = readFromInputStream(inputStream);
```

使用某个类对象的 getResourceAsStream 方法其会根据文件前缀来判断是从根路径还是相对于该类文件的路径开始读取，而使用 ClassLoader 的 getResourceAsStream 方法，则默认是从根路径开始读取。另外值得一提的是，我们务必需要在读取完毕后关闭文件流：

```java
InputStream inputStream = null;
try {
    File file = new File(classLoader.getResource("fileTest.txt").getFile());
    inputStream = new FileInputStream(file);

    //...
}
finally {
    if (inputStream != null) {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

# Files

在 JDK7 之后 NIO 也是得到了极大的更新，我们可以使用 Files 的 readAllBytes 方法来读取文件：

```java
Path path = Paths.get(getClass().getClassLoader()
    .getResource("fileTest.txt").toURI());
byte[] fileBytes = Files.readAllBytes(path);
String data = new String(fileBytes);

// Use Stream
Stream<String> lines = Files.lines(path);
String data = lines.collect(Collectors.joining("\n"));
lines.close();
```

# 链接

- https://www.baeldung.com/java-write-to-file
