# BufferedReader

BufferedReader 是支持同步的，而 Scanner 不支持。如果我们处理多线程程序，BufferedReader 应当使用。BufferedReader 相对于 Scanner 有足够大的缓冲区内存。

Scanner 有很少的缓冲区(1KB 字符缓冲)相对于 BufferedReader(8KB 字节缓冲)，但是这是绰绰有余的。BufferedReader 相对于 Scanner 来说要快一点，因为 Scanner 对输入数据进行类解析，而 BufferedReader 只是简单地读取字符序列。

## 流构建

```java
// 要找 resource 目录下的某个文件
BufferedReader bufferedReaderB = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/B/B1.txt")));
String url = this.getClass().getResource("/userFile.properties").getFile();

// 找某个类
File f = new File(MyClass.class.getProtectionDomain().getCodeSource().getLocation().getPath());
String[] classpathEntries = classpath.split(File.pathSeparator);
```

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

// 使用 FileReader 辅助创建
BufferedReader reader =
  new BufferedReader(new FileReader("src/main/resources/input.txt"));

// 指定 Buffer 大小
BufferedReader reader =
  new BufferedReader(new FileReader("src/main/resources/input.txt")), 16384);

// 使用辅助函数创建
BufferedReader reader =
  Files.newBufferedReader(Paths.get("src/main/resources/input.txt"))
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

## 按字符读取

我们可以使用 read 函数来读取单个字符：

```java
public String readAllCharsOneByOne(BufferedReader reader) throws IOException {
    StringBuilder content = new StringBuilder();

    int value;
    while ((value = reader.read()) != -1) {
        content.append((char) value);
    }

    return content.toString();
}
```

也可以读取多个字符：

```java
public String readMultipleChars(BufferedReader reader) throws IOException {
    int length;
    char[] chars = new char[length];
    int charsRead = reader.read(chars, 0, length);

    String result;
    if (charsRead != -1) {
        result = new String(chars, 0, charsRead);
    } else {
        result = "";
    }

    return result;
}

// 跳过某些字符
@Test
public void givenBufferedReader_whensSkipChars_thenOk() throws IOException {
    StringBuilder result = new StringBuilder();

    try (BufferedReader reader =
           new BufferedReader(new StringReader("1__2__3__4__5"))) {
        int value;
        while ((value = reader.read()) != -1) {
            result.append((char) value);
            reader.skip(2L);
        }
    }

    assertEquals("12345", result);
}
```

我们可以使用 mark(int readAheadLimit) 和 reset() 方法来标记流中的某些位置，然后再返回。作为一个人为的示例，让我们使用 mark() 和 reset() 忽略流开头的所有空格：

```java
@Test
public void givenBufferedReader_whenSkipsWhitespacesAtBeginning_thenOk()
  throws IOException {
    String result;

    try (BufferedReader reader =
           new BufferedReader(new StringReader("    Lorem ipsum dolor sit amet."))) {
        do {
            reader.mark(1);
        } while(Character.isWhitespace(reader.read()))

        reader.reset();
        result = reader.readLine();
    }

    assertEquals("Lorem ipsum dolor sit amet.", result);
}
```

在上面的示例中，我们使用 mark() 方法标记刚刚读取的位置。给它一个值 1 意味着只有代码会记住一个字符向前的标记。这很方便，因为一旦看到第一个非空白字符，我们就可以返回并重新读取该字符，而无需重新处理整个流。没有标记，我们将在最终字符串中丢失 L。

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

# BufferedWriter

## 追加写

```java
@Test
public void whenAppendToFileUsingFileWriter_thenCorrect()
  throws IOException {

    FileWriter fw = new FileWriter(fileName, true);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write("Spain");
    bw.newLine();
    bw.close();

    assertThat(getStringFromInputStream(
      new FileInputStream(fileName)))
      .isEqualTo("UK\r\n" + "US\r\n" + "Germany\r\n" + "Spain\r\n");
}
```
