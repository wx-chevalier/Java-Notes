# File Reader & Writer

# File Reader

FileReader 类从 InputStreamReader 类继承而来。该类按字符读取流中数据。可以通过以下几种构造方法创建需要的对象。在给定从中读取数据的 File 的情况下创建一个新 FileReader。

```java
FileReader(File file)
FileReader(FileDescriptor fd)
FileReader(String fileName)
```

```java
import java.io.*;

public class FileRead {
    public static void main(String args[]) throws IOException {
        File file = new File("Hello1.txt");
        // 创建文件
        file.createNewFile();
        // creates a FileWriter Object
        FileWriter writer = new FileWriter(file);
        // 向文件写入内容
        writer.write("This\n is\n an\n example\n");
        writer.flush();
        writer.close();
        // 创建 FileReader 对象
        FileReader fr = new FileReader(file);
        char[] a = new char[50];
        fr.read(a); // 读取数组中的内容
        for (char c : a)
            System.out.print(c); // 一个一个打印字符
        fr.close();
    }
}
```
