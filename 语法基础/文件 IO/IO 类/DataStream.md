# Data

## DataInputStream

数据输入流允许应用程序以与机器无关方式从底层输入流中读取基本 Java 数据类型。下面的构造方法用来创建数据输入流对象。

```java
DataInputStream dis = new DataInputStream(InputStream in);
```

另一种创建方式是接收一个字节数组，和两个整形变量 off、len，off 表示第一个读取的字节，len 表示读取字节的长度。

| 序号 | 方法描述                                                                                                                                                                                                                                                                             |
| :--- | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1    | **public final int read(byte[] r, int off, int len)throws IOException** 从所包含的输入流中将 `len` 个字节读入一个字节数组中。如果 len 为-1，则返回已读字节数。                                                                                                                       |
| 2    | **Public final int read(byte [] b)throws IOException** 从所包含的输入流中读取一定数量的字节，并将它们存储到缓冲区数组 `b` 中。                                                                                                                                                       |
| 3    | **public final Boolean readBooolean()throws IOException,**public final byte readByte()throws IOException,**public final short readShort()throws IOException** **public final Int readInt()throws IOException**从输入流中读取字节，返回输入流中两个字节作为对应的基本数据类型返回值。 |
| 4    | **public String readLine() throws IOException** 从输入流中读取下一文本行。                                                                                                                                                                                                           |

```java
import java.io.*;

public class Test{
   public static void main(String args[])throws IOException{

      DataInputStream in = new DataInputStream(new FileInputStream("test.txt"));
      DataOutputStream out = new DataOutputStream(new  FileOutputStream("test1.txt"));
      BufferedReader d  = new BufferedReader(new InputStreamReader(in));

      String count;
      while((count = d.readLine()) != null){
          String u = count.toUpperCase();
          System.out.println(u);
          out.writeBytes(u + "  ,");
      }
      d.close();
      out.close();
   }
}
```
