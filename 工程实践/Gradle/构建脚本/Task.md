# Gradle 中 Task 详解

Gradle 的 Project 从本质上说只是含有多个 Task 的容器，一个 Task 与 Ant 的 Target 相似，表示一个逻辑上的执行单元。
让我们来看看 Gradle API 中任务的表示：org.gradle.api.Task 接口。

![](https://lippiouyang.gitbooks.io/gradle-in-action-cn/content/images/dag25.png)

我们可以通过很多种方式定义 Task，所有的 Task 都存放在 Project 的 TaskContainer 中。让我们来看一个最简单的 Task，创建一个 build.gradle 文件，内容如下：

```groovy
task helloWorld << {
   println "Hello World!"
}
```

这里的“<<”表示向 helloWorld 中加入执行代码——其实就是 groovy 代码。Gradle 向我们提供了一整套 DSL，所以在很多时候我们写的代码似乎已经脱离了 groovy，但是在底层依然是执行的 groovy。比如上面的 task 关键字，其实就是一个 groovy 中的方法，而大括号之间的内容则表示传递给 task()方法的一个闭包。除了“<<”之外，我们还很多种方式可以定义一个 Task，我们将在本系列后续的文章中讲到。
在与 build.gradle 相同的目录下执行：

```
gradle helloWorld
```

命令行输出如下：

```
:helloWorld
Hello World!
BUILD SUCCESSFUL
Total time: 2.544 secs
```

在默认情况下，Gradle 将当前目录下的 build.gradle 文件作为项目的构建文件。在上面的例子中，我们创建了一个名为 helloWorld 的 Task，在执行 gradle 命令时，我们指定执行这个 helloWorld Task。这里的 helloWorld 是一个 DefaultTask 类型的对象，这也是定义一个 Task 时的默认类型，当然我们也可以显式地声明 Task 的类型，甚至可以自定义一个 Task 类型。

# 内置任务

Gradle 在默认情况下为我们提供了几个常用的 Task，比如查看 Project 的 Properties、显示当前 Project 中定义的所有 Task 等。可以通过一下命令查看 Project 中所有的 Task：

```
gradle tasks
```

输出如下：

```groovy
:tasks

------------------------------------------------------------
All tasks runnable from root project
------------------------------------------------------------

Build Setup tasks
-----------------
setupBuild - Initializes a new Gradle build. [incubating]
wrapper - Generates Gradle wrapper files. [incubating]

Help tasks
----------
dependencies - Displays all dependencies declared in root project 'gradle-blog'.
dependencyInsight - Displays the insight into a specific dependency in root project 'gradle-blog'.
help - Displays a help message
projects - Displays the sub-projects of root project 'gradle-blog'.
properties - Displays the properties of root project 'gradle-blog'.
tasks - Displays the tasks runnable from root project 'gradle-blog'.

Other tasks
-----------
copyFile
helloWorld

To see all tasks and more detail, run with --all.

BUILD SUCCESSFUL

Total time: 2.845 secs
```

上面的 other tasks 中列举出来的 task 即是我们自定义的 tasks。

# 任务定义

Grade 提供了非常灵活的 Task 定义方式，可以适用于不同的应用场景或者编程风格。

```groovy
task(hello) << {
    println "hello"
}

task(copy, type: Copy) {
    from(file('srcDir'))
    into(buildDir)
}

//也可以用字符串作为任务名

task('hello') <<
{
    println "hello"
}

task('copy', type: Copy) {
    from(file('srcDir'))
    into(buildDir)
}

//Defining tasks with alternative syntax

tasks.create(name: 'hello') << {
    println "hello"
}

tasks.create(name: 'copy', type: Copy) {
    from(file('srcDir'))
    into(buildDir)
}
```

上述代码中使用了 `<<` 符号用来表征 `{}` 声明的动作是追加在某个任务的末尾，如果使用全声明即是：

```groovy
task printVersion {
    // 任务的初始声明可以添加 first 和 last 动作
    doFirst {
        println "Before reading the project version"
    }

    doLast {
        println "Version: $version"
    }
}
```

## Default tasks（默认任务）

Gradle 允许在脚本中配置默认的一到多个任务来响应没有带参数的 `gradle` 命令：

```groovy
defaultTasks 'clean', 'run'

task clean << {
    println 'Default Cleaning!'
}

task run << {
    println 'Default Running!'
}

task other << {
    println "I'm not a default task!"
}
```

**gradle -q**命令的输出：

```groovy
> gradle -q
Default Cleaning!
Default Running!
```

# Locating tasks | 任务定位

# Task Dependence | 任务依赖

# Task LifeCycle
