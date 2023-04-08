# Gradle 概念、对比与配置

在 Grade 中，我们常见的几个关键术语有 Project、Plugin 以及 Task。和 Maven 一样，Gradle 只是提供了构建项目的一个框架，真正起作用的是 Plugin。Gradle 在默认情况下为我们提供了许多常用的 Plugin，其中包括有构建 Java 项目的 Plugin，还有 War，Ear 等。与 Maven 不同的是，Gradle 不提供内建的项目生命周期管理，只是 Java Plugin 向 Project 中添加了许多 Task，这些 Task 依次执行，为我们营造了一种如同 Maven 般项目构建周期。换言之，Project 为 Task 提供了执行上下文，所有的 Plugin 要么向 Project 中添加用于配置的 Property，要么向 Project 中添加不同的 Task。一个 Task 表示一个逻辑上较为独立的执行过程，比如编译 Java 源代码，拷贝文件，打包 Jar 文件，甚至可以是执行一个系统命令或者调用 Ant。另外，一个 Task 可以读取和设置 Project 的 Property 以完成特定的操作。
