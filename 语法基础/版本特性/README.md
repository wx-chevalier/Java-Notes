# Java 各版本特性介绍

2018 年 3 月 21 日，Oracle 官方宣布 Java 10 正式发布。这是 Java 大版本周期变化后的第一个正式发布版本。需要注意的是 Java 9 和 Java 10 都不是 LTS 版本。和过去的 Java 大版本升级不同，这两个只有半年左右的开发和维护期。而未来的 Java 11，也就是 18.9 LTS，才是 Java 8 之后第一个 LTS 版本。

```sh
/jdk-10/bin$ ./java -version

openjdk version "10" 2018-03-20

OpenJDK Runtime Environment 18.3 (build 10+46)

OpenJDK 64-Bit Server VM 18.3 (build 10+46, mixed mode)
```

这种发布模式已经得到了广泛应用，一个成功的例子就是 Ubuntu Linux 操作系统，在偶数年 4 月的发行版本为 LTS，会有很长时间的支持。如 2014 年 4 月份发布的 14.04 LTS，Canonical 公司和社区支持到 2019 年。类似的，Node.js，Linux kernel，Firefox 也采用类似的发布方式。

从 JDK 10 开始，Java 改为每 6 个月发布一个 feature release，这是 Java 适应云时代技术快速发展的另一个重要举措。过去一个主版本的发布需要 3 年甚至更久，使得很多社区期待但又不用很长时间就可以实现的特性，也不得不等待更久。6 个月一个版本，使得短平快的特性能够及时发布来满足开发者需求，同时对于大的复杂的特性又能够分解成小的可以发布的单元，及时获得社区的使用和反馈。在 6 个月一个版本的基础上，针对需要更加稳定的运行环境的企业客户，我们又提供 LTS 版本。这个新的 release 模式，使得 Java 能够同时满足两种不同类别用户的需求，更加有利于 Java 向前推进。
