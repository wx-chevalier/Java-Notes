# Clojure 概述

Clojure 是一个动态类型的，运行在 JVM(JDK5.0 以上），并且可以和 java 代码互操作的函数式语言。这个语言的主要目标之一是使得编写一个有多个线程并发访问数据的程序变得简单。

Clojure 的发音和单词 closure 是一样的。Clojure 之父是这样解释 Clojure 名字来历的

“我想把这就几个元素包含在里面： C (C#), L (Lisp) and J (Java). 所以我想到了 Clojure, 而且从这个名字还能想到 closure;它的域名又没有被占用;而且对于搜索引擎来说也是个很不错的关键词，所以就有了它了.”

很快 Clojure 就会移植到.NET 平台上了. ClojureCLR 是一个运行在 Microsoft 的 CLR 的 Clojure 实现. 在我写这个入门教程的时候 ClojureCLR 已经处于 alpha 阶段了.

在 2011 年 7 月, ClojureScript 项目开始了，这个项目把 Clojure 代码编译成 Javascript 代码：看这里 https://github.com/clojure/clojurescript .

Clojure 是一个开源语言， licence: [Eclipse Public License v 1.0](http://www.eclipse.org/legal/epl-v10.html) (EPL). This is a very liberal license. 关于 EPL 的更多信息看这里: http://www.eclipse.org/legal/eplfaq.php .

运行在 JVM 上面使得 Clojure 代码具有可移植性，稳定性，可靠的性能以及安全性。同时也使得我们的 Clojure 代码可以访问丰富的已经存在的 java 类库：文件 I/O, 多线程, 数据库操作, GUI 编程, web 应用等等等等.

Clojure 里面的每个操作被实现成以下三种形式的一种: 函数(function), 宏(macro)或者 special form. 几乎所有的函数和宏都是用 Clojure 代码实现的，它们的主要区别我们会在后面解释。Special forms 不是用 clojure 代码实现的，而且被 clojure 的编译器识别出来. special forms 的个数是很少的， 而且现在也不能再实现新的 special forms 了. 它们包括: [catch](http://clojure.org/special_forms#try) , [def](http://clojure.org/special_forms#toc1) , [do](http://clojure.org/special_forms#toc3) , [dot](http://clojure.org/java_interop#dot) (‘.’), [finally](http://clojure.org/special_forms#try) , [fn](http://clojure.org/special_forms#toc7) , [if](http://clojure.org/special_forms#toc2) , [let](http://clojure.org/special_forms#toc4) , [loop](http://clojure.org/special_forms#toc9) , [monitor-enter](http://clojure.org/special_forms#toc13) , [monitor-exit](http://clojure.org/special_forms#toc14) , [new](http://clojure.org/java_interop#new) , [quote](http://clojure.org/special_forms#toc5) , [recur](http://clojure.org/special_forms#toc10) , [set!](http://clojure.org/java_interop#set) , [throw](http://clojure.org/special_forms#try) , [try](http://clojure.org/special_forms#try) 和 [var](http://clojure.org/special_forms#toc6) .

Clojure 提供了很多函数来操作序列（sequence), 而序列是集合的逻辑视图。很多东西可以被看作序列：Java 集合, Clojure 的集合, 字符串, 流, 文件系统结构以及 XML 树. 从已经存在的 clojure 集合来创建新的集合的效率是非常高的，因为这里使用了 [persistent data structures](http://en.wikipedia.org/wiki/Persistent_data_structure) 的技术(这对于 clojure 在数据不可更改的情况下，同时要保持代码的高效率是非常重要的)。

Clojure 提供三种方法来安全地共享可修改的数据。所有三种方法的实现方式都是持有一个可以开遍的引用指向一个不可改变的数据。Refs 通过使用 [Software Transactional Memory](http://en.wikipedia.org/wiki/Software_transactional_memory) （STM）来提供对于多块共享数据的同步访问。Atoms 提供对于单个共享数据的同步访问。Agents 提供对于单个共享数据的异步访问。这个我们会在 “引用类型”一节详细讨论。

Clojure 是 [Lisp](<http://en.wikipedia.org/wiki/Lisp_(programming_language)>) 的一个方言. 但是 Clojure 对于传统的 Lisp 有所发展。比如, 传统 Lisp 使用 `car` 来获取链表里面的第一个数据。而 Clojure 使用 `first。有关更多Clojure和Lisp的不同看这里：` http://clojure.org/lisps .

Lisp 的语法很多人很喜欢，很多人很讨厌, 主要因为它大量的使用圆括号以及前置表达式. 如果你不喜欢这些，那么你要考虑一下是不是要学习 Clojure 了 。许多文件编辑器以及 IDE 会高亮显示匹配的圆括号, 所以你不用担心需要去人肉数有没有多加一个左括号，少写一个右括号. 同时 Clojure 的代码还要比 java 代码简洁. 一个典型的 java 方法调用是这样的:

```clj
methodName(arg1, arg2, arg3);
```

而 Clojure 的方法调用是这样的:

```clojure
(function-name arg1 arg2 arg3)
```

左括号被移到了最前面；逗号和分号不需要了. 我们称这种语法叫： “form”. 这种风格是简单而又美丽：Lisp 里面所有东西都是这种风格的.要注意的是 clojure 里面的命名规范是小写单词，如果是多个单词，那么通过中横线连接。

定义函数也比 java 里面简洁。Clojure 里面的 `println` 会在它的每个参数之间加一个空格。如果这个不是你想要的，那么你可以把参数传给 `str` ，然后再传给 `println` .

```clj
// Java
public void hello(String name) {
    System.out.println("Hello, " + name);
}
; Clojure
(defn hello [name]
  (println "Hello," name))
```

Clojure 里面大量之用了延迟计算. 这使得只有在我们需要函数结果的时候才去调用它. “懒惰序列” 是一种集合，我们之后在需要的时候才会计算这个集合理解面的元素. 只使得创建无限集合非常高效.

对 Clojure 代码的处理分为三个阶段：读入期，编译期以及运行期。在读入期，读入期会读取 clojure 源代码并且把代码转变成数据结构，基本上来说就是一个包含列表的列表的列表。。。。在编译期，这些数据结构被转化成 java 的 bytecode。在运行期这些 java bytecode 被执行。函数只有在运行期才会执行。而宏在编译期就被展开成实际对应的代码了。

Clojure 代码很难理解么？想想每次你看到 java 代码里面那些复杂语法比如: `if` , `for` , 以及匿名内部类, 你需要停一下来想想它们到底是什么意思（不是那么的直观），同时如果想要做一个高效的 Java 工程师，我们有一些工具可以利用来使得我们的代码更容易理解。同样的道理，Clojure 也有类似的工具使得我们可以更高效的读懂 clojure 代码。比如： `let` , `apply` , `map` , `filter` , `reduce` 以及匿名函数 … 所有这些我们会在后面介绍.
