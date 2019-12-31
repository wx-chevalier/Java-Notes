# SPI 使用

Java SPI 的限定如下：

- 当服务的提供者，提供了接口的一种具体实现后，在 jar 包的 META-INF/services/目录中创建一个以“接口全限定名”为命名的文件，内容为实现类的全限定名。

- SPI 所在的 jar 放在主程序的 classpath 中

- 外部程序通过 java.util.ServiceLoader 动态装载实现模块，它通过扫描 META-INF/services 目录下的配置文件找到实现类的全限定名，把类加载到 JVM。注意：SPI 的实现类必须带一个不带参数的构造方法

该示例主要为了展示如何使用 SPI，接口是数字操作接口，普通的 API 的实现类是加法操作；两个 SPI 实现类分别是减法操作和乘法操作。INumOperate 接口的代码如下：

```java
package com.demo.api;
/**
 * 数字操作接口
 *
 */
public interface INumOperate {

    public int operator(int a, int b);
}
```

普通的 API 实现，加法操作，代码如下：

```java
 package com.demo.api.impl;
import com.demo.api.INumOperate;
/**
 * 数字相加
 *
 */
public class NumPlusOperateImpl implements INumOperate {
    @Override
    public int operator(int a, int b) {
        int r = a + b;
        System.out.println("[实现类机制]加法，结果：" + r);
        return r;
    }
}
```

实现乘法的 SPI，在语法结构上和普通 api 实现一模一样，如下：

```java
package com.demo.spi.impl;

import com.demo.api.INumOperate;

/**
 * 数字相乘
 *
 */
public class NumMutliOperateImpl implements INumOperate {

    @Override
    public int operator(int a, int b) {
        int r = a * b;
        System.out.println("[SPI机制]乘法，结果：" + r);
        return r;
    }
}

package com.demo.spi.impl;

import com.demo.api.INumOperate;

/**
 * 数字相减
 *
 */
public class NumSubtractOperateImpl implements INumOperate {

    @Override
    public int operator(int a, int b) {
        int r = a - b;
        System.out.println("[SPI机制]减法，结果：" + r);
        return r;
    }

}
```

在 META-INFO/services 目录下（如果没有改目录，手工新建即可），新建一个以 com.demo.api.INumOperate 命名的文件，文件内容指明两个 SPI 的实现类的全限定名称，如下：

```java
com.demo.spi.impl.NumMutliOperateImpl
com.demo.spi.impl.NumSubtractOperateImpl
```

main 函数如下，主程序中没有显示指明 SPI 的实现，而是通过 ServiceLoader 动态加载实现类：

```java
package com.demo;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.demo.api.INumOperate;
import com.demo.api.impl.NumPlusOperateImpl;

/**
 * 主程序
 *
 */
public class Main {

    public static void main(String[] args) {
        int a = 9;
        int b = 3;

        // 普通的实现类机制，加法
        INumOperate plus = new NumPlusOperateImpl();
        plus.operator(a, b);

        // SPI机制，寻找所有的实现类，顺序执行
        ServiceLoader<INumOperate> loader = ServiceLoader.load(INumOperate.class); // 查找SPI实现类，并加载到jvm
        Iterator<INumOperate> iter = loader.iterator();
        while (iter.hasNext()) {
            INumOperate op = iter.next();
            op.operator(a, b);
        }
    }
}
```
