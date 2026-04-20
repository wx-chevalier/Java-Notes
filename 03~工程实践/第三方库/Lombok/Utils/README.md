### Constructor

源代码

```java
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ConstructorExample<T> {
  private int x, y;

  @NonNull
  private T description;

  @NoArgsConstructor
  public static class NoArgsExample {
    @NonNull
    private String field;
  }
}
```

编译之后：

```java
public class ConstructorExample<T> {
  private int x, y;

  @NonNull
  private T description;

  private ConstructorExample(T description) {
    if (description == null) throw new NullPointerException("description");
    this.description = description;
  }

  public static <T> ConstructorExample<T> of(T description) {
    return new ConstructorExample<T>(description);
  }

  @java.beans.ConstructorProperties({ "x", "y", "description" })
  protected ConstructorExample(int x, int y, T description) {
    if (description == null) throw new NullPointerException("description");
    this.x = x;
    this.y = y;
    this.description = description;
  }

  public static class NoArgsExample {
    @NonNull
    private String field;

    public NoArgsExample() {}
  }
}
```

## Exception:异常处理

### NonNull

源代码：

```java
import lombok.NonNull;

public class NonNullExample extends Something {
  private String name;

  public NonNullExample(@NonNull Person person) {
    super("Hello");
    this.name = person.getName();
  }
}
```

编译之后：

```java
import lombok.NonNull;

public class NonNullExample extends Something {
  private String name;

  public NonNullExample(@NonNull Person person) {
    super("Hello");
    if (person == null) {
      throw new NullPointerException("person");
    }
    this.name = person.getName();
  }
}
```

### SneakyThrows

源代码：

```java
import lombok.SneakyThrows;

public class SneakyThrowsExample implements Runnable {

  @SneakyThrows(UnsupportedEncodingException.class)
  public String utf8ToString(byte[] bytes) {
    return new String(bytes, "UTF-8");
  }

  @SneakyThrows
  public void run() {
    throw new Throwable();
  }
}
```

编译之后：

```java
import lombok.Lombok;

public class SneakyThrowsExample implements Runnable {

  public String utf8ToString(byte[] bytes) {
    try {
      return new String(bytes, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw Lombok.sneakyThrow(e);
    }
  }

  public void run() {
    try {
      throw new Throwable();
    } catch (Throwable t) {
      throw Lombok.sneakyThrow(t);
    }
  }
}
```

## Thread:线程

### Synchronized

源代码：

```java
import lombok.Synchronized;

public class SynchronizedExample {
  private final Object readLock = new Object();

  @Synchronized
  public static void hello() {
    System.out.println("world");
  }

  @Synchronized
  public int answerToLife() {
    return 42;
  }

  @Synchronized("readLock")
  public void foo() {
    System.out.println("bar");
  }
}
```

编译之后：

```java
public class SynchronizedExample {
  private static final Object $LOCK = new Object[0];
  private final Object $lock = new Object[0];
  private final Object readLock = new Object();

  public static void hello() {
    synchronized ($LOCK) {
      System.out.println("world");
    }
  }

  public int answerToLife() {
    synchronized ($lock) {
      return 42;
    }
  }

  public void foo() {
    synchronized (readLock) {
      System.out.println("bar");
    }
  }
}
```

## Utils

### Cleanup

源代码：

```java
import java.io.*;
import lombok.Cleanup;

public class CleanupExample {

  public static void main(String[] args) throws IOException {
    @Cleanup
    InputStream in = new FileInputStream(args[0]);
    @Cleanup
    OutputStream out = new FileOutputStream(args[1]);
    byte[] b = new byte[10000];
    while (true) {
      int r = in.read(b);
      if (r == -1) break;
      out.write(b, 0, r);
    }
  }
}
```

编译之后：

```
import java.io.*;

public class CleanupExample {
	public static void main(String[] args) throws IOException {
		InputStream in = new FileInputStream(args[0]);
		try {
			OutputStream out = new FileOutputStream(args[1]);
			try {
				byte[] b = new byte[10000];
				while (true) {
					int r = in.read(b);
					if (r == -1) break;
					out.write(b, 0, r);
				}
			} finally {
				if (out != null) {
					out.close();
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
}
```

### Log:日志

使用@Log 或者类似注解可以为类自动创建一个 log 对象，其效果如下所示：

```
@CommonsLog
Creates private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(LogExample.class);
@Log
Creates private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogExample.class.getName());
@Log4j
Creates private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LogExample.class);
@Log4j2
Creates private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(LogExample.class);
@Slf4j
Creates private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogExample.class);
@XSlf4j
Creates private static final org.slf4j.ext.XLogger log = org.slf4j.ext.XLoggerFactory.getXLogger(LogExample.class);
```

使用了 Lombok 之后的代码如下：

```java
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Log
public class LogExample {

  public static void main(String... args) {
    log.error("Something's wrong here");
  }
}

@Slf4j
public class LogExampleOther {

  public static void main(String... args) {
    log.error("Something else is wrong here");
  }
}

@CommonsLog(topic = "CounterLog")
public class LogExampleCategory {

  public static void main(String... args) {
    log.error("Calling the 'CounterLog' with a message");
  }
}
```

编译之后的代码如下：

```
public class LogExample {
	private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogExample.class.getName());

	public static void main(String... args) {
		log.error("Something's wrong here");
	}
}

public class LogExampleOther {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogExampleOther.class);

	public static void main(String... args) {
		log.error("Something else is wrong here");
	}
}

public class LogExampleCategory {
	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog("CounterLog");

	public static void main(String... args) {
		log.error("Calling the 'CounterLog' with a message");
	}
}
```

其他可配置的参数为：

- lombok.log.fieldName = an identifier (default: log)
  The generated logger fieldname is by default 'log', but you can change it to a different name with this setting.
- lombok.log.fieldIsStatic = [true | false] (default: true)
  Normally the generated logger is a static field. By setting this key to false, the generated field will be an instance field instead.
- lombok.log.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of any of the various log annotations as a warning or error if configured.
- lombok.log.apacheCommons.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of @lombok.extern.apachecommons.CommonsLog as a warning or error if configured.
- lombok.log.javaUtilLogging.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of @lombok.extern.java.Log as a warning or error if configured.
- lombok.log.log4j.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of @lombok.extern.log4j.Log4j as a warning or error if configured.
- lombok.log.log4j2.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of @lombok.extern.log4j.Log4j2 as a warning or error if configured.
- lombok.log.slf4j.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of @lombok.extern.slf4j.Slf4j as a warning or error if configured.
- lombok.log.xslf4j.flagUsage = [warning | error] (default: not set)
  Lombok will flag any usage of @lombok.extern.slf4j.XSlf4j as a warning or error if configured.
