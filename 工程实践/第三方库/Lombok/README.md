# 基于 Lombok 的类生成

Lombok 主要依赖编译时代码生成技术，帮你自动生成基于模板的常用的 Java 代码，譬如最常见的 Getter 与 Setter。之前动态的插入 Getter 与 Setter 主要有两种，一个是像 Intellij 与 Eclipse 这样在开发时动态插入，缺点是这样虽然不用你手动写，但是还是会让你的代码异常的冗长。另一种是通过类似于 Spring 这样基于注解的在运行时利用反射动态添加，不过这样的缺陷是会影响性能，并且有一定局限性。

# 环境配置

## Gradle

我们可以简单地在 build.gradle 文件中添加依赖以引入 Gradle:

```groovy
repositories {
	mavenCentral()
}

dependencies {
	compileOnly 'org.projectlombok:lombok:1.18.8'
	annotationProcessor 'org.projectlombok:lombok:1.18.8'
}
```

对于复杂配置的场景，也可以使用官方的 Gradle 插件：

```groovy
plugins {
  id "io.freefair.lombok" version "4.0.1"
}

buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "io.freefair.gradle:lombok-plugin:4.0.1"
  }
}

apply plugin: "io.freefair.lombok"
```

# 自定义注解原理

Lombok 这款插件正是依靠可插件化的 Java 自定义注解处理 API（JSR 269: Pluggable Annotation Processing API）来实现在 Javac 编译阶段利用“Annotation Processor”对自定义的注解进行预处理后生成真正在 JVM 上面执行的“Class 文件”。其大致执行原理图如下：

![Lombok 执行原理](https://s3.ax1x.com/2021/02/07/yNnQqe.png)

从上面的这个原理图上可以看出 Annotation Processing 是编译器在解析 Java 源代码和生成 Class 文件之间的一个步骤。其中 Lombok 插件具体的执行流程如下：

![执行流程](https://s3.ax1x.com/2021/02/07/yNn3ad.png)

从上面的 Lombok 执行的流程图中可以看出，在 Javac 解析成 AST 抽象语法树之后, Lombok 根据自己编写的注解处理器，动态地修改 AST，增加新的节点（即 Lombok 自定义注解所需要生成的代码），最终通过分析生成 JVM 可执行的字节码 Class 文件。使用 Annotation Processing 自定义注解是在编译阶段进行修改，而 JDK 的反射技术是在运行时动态修改，两者相比，反射虽然更加灵活一些但是带来的性能损耗更加大。

从熟悉 JSR 269: Pluggable Annotation Processing API 的同学可以从工程类结构图中发现 AnnotationProcessor 这个类是 Lombok 自定义注解处理的入口。该类有两个比较重要的方法一个是 init 方法，另外一个是 process 方法。在 init 方法中，先用来做参数的初始化，将 AnnotationProcessor 类中定义的内部类（JavacDescriptor、EcjDescriptor）先注册到 ProcessorDescriptor 类型定义的列表中。其中，内部静态类—JavacDescriptor 在其加载的时候就将 lombok.javac.apt.LombokProcessor 这个类进行对象实例化并注册。在 LombokProcessor 处理器中，其中的 process 方法会根据优先级来分别运行相应的 handler 处理类。Lombok 中的多个自定义注解都分别有对应的 handler 处理类。
