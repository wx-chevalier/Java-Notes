# Gradle Java 实践

Gradle 使用了一种约定俗成的方法来构建基于 JVM 的项目，它借用了 Apache Maven 的一些约定。特别是，它对源文件和资源使用相同的默认目录结构，并与 Maven 兼容的资源库一起工作。Java 中所谓的 Plugin 就是一个定义了一系列 Properties 与 Tasks 的集合。如果希望使用 Java plugin，只需要在 build.gradle 中加入这句话：

```groovy
apply plugin: 'java'
```

Gradle 和 Maven 一样，采用了约定优于配置的方式对 Java 项目布局，并且布局方式是和 Maven 一样的，此外，Gradle 还可以方便的自定义布局。在 Gradle 中，一般把这些目录叫做 source set：

![gradle source set](https://s2.ax1x.com/2019/12/17/QINmpF.png)。

这里要注意，每个 plugin 的 source set 可能都不一样。同样的，Java plugin 还定义好了一堆 task，让我们可以直接使用，比如：clean、test、build 等等。这些 task 都是围绕着 Java plugin 的构建生命周期的：

![javaPluginTask](https://s2.ax1x.com/2019/12/17/QIN1Tx.png)

图中每一块都是一个 task，箭头表示 task 执行顺序/依赖，比如执行 task jar，那么必须先执行 task compileJava 和 task processResources。另外可以看到，Gradle 的 Java plugin 构建生命周期比较复杂，但是也表明了更加灵活，而且，在项目中，一般只使用其中常用的几个：clean test check build 等等。

Gradle 构建过程中，所有的依赖都表现为配置，比如说系统运行时的依赖是 runtime，Gradle 里有一个依赖配置叫 runtime，那么系统运行时会加载这个依赖配置以及它的相关依赖。这里说的有点绕，可以简单理解依赖和 maven 类似，只不过 Gradle 用 configuration 实现，所以更灵活，有更多选择。下图是依赖配置关系图以及和 task 调用的关系图：

![javaPluginConfigurations](https://s2.ax1x.com/2019/12/17/QIN1Tx.png)

可以看到，基本和 Maven 是一样的。其实 Gradle 里面这些依赖(scope)都是通过 configuration 来实现的。
