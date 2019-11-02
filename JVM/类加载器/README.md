# 类加载器

类的加载是需要类加载器完成的，但是类加载器在 JVM 中的作用可不止这些。在 JVM 中，一个类的唯一性是需要这个类本身和类加载一起才能确定的，每个类加载器都有一个独立的命名空间。

类加载器在 JVM 中的作用有：

- 将类的字节码文件从 JVM 外部加载到内存中
- 确定一个类的唯一性
- 提供隔离特性，为中间件开发者提供便利，例如 Tomcat

# 类的唯一性

不同的类加载器，即使是同一个类字节码文件，最后再 JVM 里的类对象也不是同一个，下面的代码展示了这个结论：

```java
package jvm;

import java.io.IOException;
import java.io.InputStream;

public class ClassLoaderTest{
  public static void main(String []args) {
    ClassLoader myLoader = new ClassLoader() {
      @Override
      public Class<?> loadClass(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
          String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
          InputStream inputStream = getClass().getResourceAsStream(fileName);

          if(inputStream == null){
            return super.loadClass(name);
          }

          try{
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            return defineClass(name, b, 0, b.length);
          } catch(IOException e){
            throw new ClassNotFoundException();
          }
      }
    };

    Object obj = myLoader.loadClass("jvm.ClassLoaderTest").newInstance();
    System.out.println(obj.getClass()); // class jvm.ClassLoaderTest
    System.out.println(obj instanceof jvm.ClassLoaderTest); // false

    ClassLoaderTest classLoaderTest = new ClassLoaderTest();
    System.out.println(classLoaderTest.getClass()); // class jvm.ClassLoaderTest
    System.out.printLn(classLoaderTest instanceof jvm.ClassLoaderTest); //true
  }
}
```

可以看出，代码中使用自定义类加载器（myLoader）加载的 jvm.ClassLoaderTest 类和通过应用程序类加载器加载的类不是同一个类。

# 链接

- https://zhuanlan.zhihu.com/p/81419563 我竟然不再抗拒 Java 的类加载机制了
