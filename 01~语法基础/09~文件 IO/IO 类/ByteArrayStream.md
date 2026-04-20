# ByteArray

## ByteArrayInputStream

字节数组输入流在内存中创建一个字节数组缓冲区，从输入流读取的数据保存在该字节数组缓冲区中。创建字节数组输入流对象有以下几种方式。接收字节数组作为参数创建：

```java
ByteArrayInputStream bArray = new ByteArrayInputStream(byte [] a);
```

另一种创建方式是接收一个字节数组，和两个整形变量 off、len，off 表示第一个读取的字节，len 表示读取字节的长度。

```java
ByteArrayInputStream bArray = new ByteArrayInputStream(byte []a,
              int off,
              int len)
```

成功创建字节数组输入流对象后，可以参见以下列表中的方法，对流进行读操作或其他操作。

| 序号 | 方法描述                                                                                        |
| :--- | :---------------------------------------------------------------------------------------------- |
| 1    | **public int read()** 从此输入流中读取下一个数据字节。                                          |
| 2    | **public int read(byte[] r, int off, int len)** 将最多 `len` 个数据字节从此输入流读入字节数组。 |
| 3    | **public int available()** 返回可不发生阻塞地从此输入流读取的字节数。                           |
| 4    | **public void mark(int read)** 设置流中的当前标记位置。                                         |
| 5    | **public long skip(long n)** 从此输入流中跳过 `n` 个输入字节。                                  |

```java
import java.io.*;

public class ByteStreamTest {

   public static void main(String args[])throws IOException {

      ByteArrayOutputStream bOutput = new ByteArrayOutputStream(12);

      while( bOutput.size()!= 10 ) {
         // 获取用户输入值
         bOutput.write(System.in.read());
      }

      byte b [] = bOutput.toByteArray();
      System.out.println("Print the content");
      for(int x= 0 ; x < b.length; x++) {
         // 打印字符
         System.out.print((char)b[x]  + "   ");
      }
      System.out.println("   ");

      int c;

      ByteArrayInputStream bInput = new ByteArrayInputStream(b);

      System.out.println("Converting characters to Upper case " );
      for(int y = 0 ; y < 1; y++ ) {
         while(( c= bInput.read())!= -1) {
            System.out.println(Character.toUpperCase((char)c));
         }
         bInput.reset();
      }
   }
}
```
