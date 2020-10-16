# SPI

SPI：Service Provider Interface，是 JDK 内置的一种服务提供机制。许多开发框架都使用了 Java 的 SPI 机制，如 java.sql.Driver 的 SPI 实现（mysql 驱动、oracle 驱动等）、common-logging 的日志接口实现、dubbo 的扩展实现等等。

面向的对象的设计里，我们一般推荐模块之间基于接口编程，模块之间不对实现类进行硬编码。一旦代码里涉及具体的实现类，就违反了可拔插的原则，如果需要替换一种实现，就需要修改代码。在实际过程中，API 的实现是封装在 jar 包中的，所以当需要更换一种实现时，要生成新的 jar 包来替换以前的类。为了实现在模块装配的时候不用在程序里动态指明，这就需要一种服务发现机制。java spi 就是提供这样的一个机制：为某个接口寻找服务实现的机制。通过它就可以实现，不修改原来 jar 的情况下，为 api 新增一种实现。这有点类似 IOC 的思想，将装配的控制权移到了程序之外。

# SPI 缺陷

ServiceLoader 缺少一些有用的特性：

- 缺少实例的维护 ServiceLoader 每次 load 后，都会生成一份实例，也就是我们理解的 prototype；

- 无法获取指定的实例，在 Spring 中可以通过 beanFactory.getBean("id") 获取一个实例，但是 ServiceLoader 不支持，只能一次获取所有的接口实例；

- 不支持排序 ServiceLoader 返回的接口实例没有进行排序，随着新的实例加入，会出现排序不稳定的情况；

- 无法获的所有实现的类型 无法通过接口获取所有的接口实例类型；

- 作用域缺失，没有定义 singleton 和 prototype 的定义，不利于用户进行自由定制。

# TBD

- https://cxis.me/2017/04/17/Java%E4%B8%ADSPI%E6%9C%BA%E5%88%B6%E6%B7%B1%E5%85%A5%E5%8F%8A%E6%BA%90%E7%A0%81%E8%A7%A3%E6%9E%90/
