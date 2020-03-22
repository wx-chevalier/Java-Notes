# Serialization

Java 是面向对象的编程语言，有时需要保存对象，并在下次使用时可以顺利还原该对象。由于这种需求很常见，所以 Java API 对此提供了支持，添加相关程序代码到标准类库中，并将保存和还原的过程称之为“对象序列化”。Java SE7 文档中将与对象序列化的相关内容做了详细表述，将其称为: “Java 对象序列化规范” （Java Object Serialization Specification）。

该机制中，一个对象可以被表示为一个字节序列，该字节序列包括该对象的数据、有关对象的类型的信息和存储在对象中数据的类型。将序列化对象写入文件之后，可以从文件中读取出来，并且对它进行反序列化，也就是说，对象的类型信息、对象的数据，还有对象中的数据类型可以用来在内存中新建对象。

整个过程都是 Java 虚拟机（JVM）独立的，也就是说，在一个平台上序列化的对象可以在另一个完全不同的平台上反序列化该对象。类 ObjectInputStream 和 ObjectOutputStream 是高层次的数据流，它们包含反序列化和序列化对象的方法。ObjectOutputStream 类包含很多写方法来写各种数据类型，但是一个特别的方法例外：

```java
public final void writeObject(Object x) throws IOException
```

上面的方法序列化一个对象，并将它发送到输出流。相似的 ObjectInputStream 类包含如下反序列化一个对象的方法：

```java
public final Object readObject() throws IOException,
                                 ClassNotFoundException
```

该方法从流中取出下一个对象，并将对象反序列化。它的返回值为 Object，因此，你需要将它转换成合适的数据类型。

## 序列化机制

序列化主要有三个用途:

- 对象持久化(persistence)：对象持久化是指延长对象的存在时间。通常状况下，当程序结束时，程序中的对象不再存在。对象持久化是指延长对象的存在时间。通常状况下，当程序结束时，程序中的对象不再存在。如果通过序列化功能，将对象保存到文件中，就可以延长对象的存在时间，在下次程序运行是再恢复该对象。
- 对象复制：通过序列化，将对象保存在内存中，可以再通过此数据得到多个对象的副本。
- 对象传输：通过序列化，将对象转化字节流后，可以通过网络发送给另外的 Java 程序。

默认的序列化机制写到流中的数据有:

- 对象所属的类
- 类的签名
- 所有的非 transient 和非 static 的属性
- 对其他对象的引用也会造成对这些对象的序列化
- 如果多个引用指向一个对象，那么会使用 sharing reference 机制

序列化是将对象的状态信息转换为可存储或传输的形式的过程。我们都知道，Java 对象是保存在 JVM 的堆内存中的，也就是说，如果 JVM 堆不存在了，那么对象也就跟着消失了。
而序列化提供了一种方案，可以让你在即使 JVM 停机的情况下也能把对象保存下来的方案。就像我们平时用的 U 盘一样。把 Java 对象序列化成可存储或传输的形式（如二进制流），比如保存在文件中。这样，当再次需要这个对象的时候，从文件中读取出二进制流，再从二进制流中反序列化出对象。

# 序列化使用

为了演示序列化在 Java 中是怎样工作的，我将使用之前教程中提到的 Employee 类，假设我们定义了如下的 Employee 类，该类实现了 Serializable 接口。

```java
public class Employee implements java.io.Serializable
{
   public String name;
   public String address;
   public transient int SSN;
   public int number;
   public void mailCheck()
   {
      System.out.println("Mailing a check to " + name
                           + " " + address);
   }
}
```

请注意，一个类的对象要想序列化成功，必须满足两个条件：

- 该类必须实现 java.io.Serializable 接口。

- 该类的所有属性必须是可序列化的。如果有一个属性不是可序列化的，则该属性必须注明是短暂的。

如果你想知道一个 Java 标准类是否是可序列化的，请查看该类的文档。检验一个类的实例是否能序列化十分简单，只需要查看该类有没有实现 java.io.Serializable 接口。

## 序列化对象

ObjectOutputStream 类用来序列化一个对象，如下的 SerializeDemo 例子实例化了一个 Employee 对象，并将该对象序列化到一个文件中。该程序执行后，就创建了一个名为 employee.ser 文件（当序列化一个对象到文件时， 按照 Java 的标准约定是给文件一个 .ser 扩展名）。该程序没有任何输出，但是你可以通过代码研读来理解程序的作用。

```java
public class SerializeDemo
{
   public static void main(String [] args)
   {
      Employee e = new Employee();
      e.name = "Reyan Ali";
      e.address = "Phokka Kuan, Ambehta Peer";
      e.SSN = 11122333;
      e.number = 101;
      try
      {
         FileOutputStream fileOut =
         new FileOutputStream("/tmp/employee.ser");
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(e);
         out.close();
         fileOut.close();
         System.out.printf("Serialized data is saved in /tmp/employee.ser");
      }catch(IOException i)
      {
          i.printStackTrace();
      }
   }
}
```

为什么一个类实现了 Serializable 接口，它就可以被序列化呢？在上节的示例中，使用 ObjectOutputStream 来持久化对象，在该类中有如下代码：

```java
private void writeObject0(Object obj, boolean unshared) throws IOException {
      ...
    if (obj instanceof String) {
        writeString((String) obj, unshared);
    } else if (cl.isArray()) {
        writeArray(obj, desc, unshared);
    } else if (obj instanceof Enum) {
        writeEnum((Enum) obj, desc, unshared);
    } else if (obj instanceof Serializable) {
        writeOrdinaryObject(obj, desc, unshared);
    } else {
        if (extendedDebugInfo) {
            throw new NotSerializableException(cl.getName() + "\n"
                    + debugInfoStack.toString());
        } else {
            throw new NotSerializableException(cl.getName());
        }
    }
    ...
}
```

从上述代码可知，如果被写对象的类型是 String，或数组，或 Enum，或 Serializable，那么就可以对该对象进行序列化，否则将抛出 NotSerializableException。

## 反序列化对象

下面的 DeserializeDemo 程序实例了反序列化，/tmp/employee.ser 存储了 Employee 对象。

```java
public class DeserializeDemo
{
   public static void main(String [] args)
   {
      Employee e = null;
      try
      {
         FileInputStream fileIn = new FileInputStream("/tmp/employee.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         e = (Employee) in.readObject();
         in.close();
         fileIn.close();
      }catch(IOException i)
      {
         i.printStackTrace();
         return;
      }catch(ClassNotFoundException c)
      {
         System.out.println("Employee class not found");
         c.printStackTrace();
         return;
      }
      System.out.println("Deserialized Employee...");
      System.out.println("Name: " + e.name);
      System.out.println("Address: " + e.address);
      System.out.println("SSN: " + e.SSN);
      System.out.println("Number: " + e.number);
    }
}
```

readObject() 方法中的 try/catch 代码块尝试捕获 ClassNotFoundException 异常。对于 JVM 可以反序列化对象，它必须是能够找到字节码的类。如果 JVM 在反序列化对象的过程中找不到该类，则抛出一个 ClassNotFoundException 异常。

注意，readObject() 方法的返回值被转化成 Employee 引用。当对象被序列化时，属性 SSN 的值为 111222333，但是因为该属性是短暂的，该值没有被发送到输出流。所以反序列化后 Employee 对象的 SSN 属性为 0。

# serialVersionUID

虚拟机是否允许反序列化，不仅取决于类路径和功能代码是否一致，一个非常重要的一点是两个类的序列化 ID 是否一致，这个所谓的序列化 ID，就是我们在代码中定义的 serialVersionUID。

这是因为，在进行反序列化时，JVM 会把传来的字节流中的 serialVersionUID 与本地相应实体类的 serialVersionUID 进行比较，如果相同就认为是一致的，可以进行反序列化，否则就会出现序列化版本不一致的异常，即是 InvalidCastException。

# 链接

- [《成神之路-基础篇》Java 基础知识——序列化(已完结)](http://www.hollischuang.com/archives/1158)
