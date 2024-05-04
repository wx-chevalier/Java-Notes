# Java 互操作

Clojure 程序可以使用所有的 Java 类以及接口。和在 Java 里面一样 `java.lang` 这个包里面的类是默认导入的。你可以手动的用 `import` 函数来导入其它包的类。看例子:

```clj
(import
  '(java.util Calendar GregorianCalendar)
  '(javax.swing JFrame JLabel))
```

同时也可以看下宏 ns 下面的 `[:import](http://xumingming.sinaapp.com/302/clojure-functional-programming-for-the-jvm-clojure-tutorial/#nsMacro) 指令， 我们会在后面介绍的。`

有两种方式可以访问类里面的常量的：

```
(. java.util.Calendar APRIL) ; -> 3
(. Calendar APRIL) ; works if the Calendar class was imported
java.util.Calendar/APRIL
Calendar/APRIL ; works if the Calendar class was imported
```

在 Clojure 代码里面调用 java 的方法是很简单的。因此很多 java 里面已经实现的功能 Clojure 就没有实现自己的了。比如, Clojure 里面没有提供函数来计算一个数的绝对值，因为可以用 `java.lang.Math` 里面的 abs 方法。而另一方面，比如这个类里面还提供了一个 `max` 方法来计算两个数里面比较大的一个, 但是它只接受两个参数，因此 Clojure 里面自己提供了一个可以接受多个参数的 max 函数。

有两种方法可以调用 java 里面的静态方法：

```
(. Math pow 2 4) ; -> 16.0
(Math/pow 2 4)
```

同样也有两种方法来创建一个新的 java 的对象，看下面的例子。这里注意一下我们用 `def` 创建的对象 bind 到一个全局的 binding。这个其实不是必须的。有好几种方式可以得到一个对象的引用比如把它加入一个集合或者把它传入一个函数。

```clj
(import '(java.util Calendar GregorianCalendar))
(def calendar (new GregorianCalendar 2008 Calendar/APRIL 16)) ; April 16, 2008
(def calendar (GregorianCalendar. 2008 Calendar/APRIL 16))
```

同样也有两种方法可以调用 java 对象的方法:

```clj
(. calendar add Calendar/MONTH 2)
(. calendar get Calendar/MONTH) ; -> 5
(.add calendar Calendar/MONTH 2)
(.get calendar Calendar/MONTH) ; -> 7
```

一般来说我们比较推荐使用下面那种用法(.add, .get), 上面那种用法在定义宏的时候用得比较多， 这个等到我们讲到宏的时候再做详细介绍。

方法调用可以用 `..` 宏串起来:

```clj
(. (. calendar getTimeZone) getDisplayName) ; long way
(.. calendar getTimeZone getDisplayName) ; -> "Central Standard Time"
```

还一个宏： `.?.` 在 `clojure.contrib.core` 名字空间里面， 它和上面..这个宏的区别是，在调用的过程中如果有一个返回结果是 nil, 它就不再继续调用了，可以防止出现 `NullPointerException` 异常。

`doto` 函数可以用来调用一个对象上的多个方法。它返回它的第一个参数， 也就是所要调用方法的对象。这对于初始化一个对象的对各属性是非常方便的。 (看下面”Namespaces“那一节的 `JFrame` GUI 对象的例子). 比如:

```clj
(doto calendar
  (.set Calendar/YEAR 1981)
  (.set Calendar/MONTH Calendar/AUGUST)
  (.set Calendar/DATE 1))
(def formatter (java.text.DateFormat/getDateInstance))
(.format formatter (.getTime calendar)) ; -> "Aug 1, 1981"
```

`memfn` 宏可以自动生成代码以使得 java 方法可以当成 clojure 里面的“一等公民”来对待。这个可以用来替代 clojure 里面的匿名方法。当用 `memfn` 来调用 java 里面那些需要参数的方法的时候， 你必须给每个参数指定一个名字，以让 clojure 知道你要调用的方法需要几个参数。这些名字到底是什么不重要，但是它们必须要是唯一的， 因为要用这些名字来生成 Clojure 代码的。下面的代码用了一个 map 方法来从第二个集合里面取 beginIndex 来作为参数调用第一个集合里面的字符串的 substring 方法。大家可以看一下用匿名函数和用 memfn 来直接调用 java 的方法的区别。

```clj
(println (map #(.substring %1 %2)
           ["Moe" "Larry" "Curly"] [1 2 3])) ; -> (oe rry ly)

(println (map (memfn substring beginIndex)
           ["Moe" "Larry" "Curly"] [1 2 3])) ; -> same
```

# 代理

`proxy` 创建一个继承了指定类并且/或者实现了 0 个或者多个接口的类的对象。这对于创建那种必须要实现某个接口才能得到通知的 listener 对象很有用。举一个例子， 大家可以看下面 “Desktop Applications” 那一节的例子。那里我们创建了一个继承 JFrame 类并且实现 ActionListener 接口的类的对象。

# 线程

所有的 Clojure 方法都实现了 `[java.lang.Runnable](http://java.sun.com/javase/6/docs/api/java/lang/Runnable.html)` 接口和 `[java.util.concurrent.Callable](http://java.sun.com/javase/6/docs/api/java/util/concurrent/Callable.html)` 接口。这使得非常容易把 Clojure 里面函数和 java 里面的线程一起使用。比如：

```
(defn delayed-print [ms text]
  (Thread/sleep ms)
  (println text))

; Pass an anonymous function that invokes delayed-print
; to the Thread constructor so the delayed-print function
; executes inside the Thread instead of
; while the Thread object is being created.
(.start (Thread. #(delayed-print 1000 ", World!"))) ; prints 2nd
(print "Hello") ; prints 1st
; output is "Hello, World!"
```

# 异常处理

Clojure 代码里面抛出来的异常都是运行时异常。当然从 Clojure 代码里面调用的 java 代码还是可能抛出那种需要检查的异常的。 `try` , `catch` , `finally` 以及 `throw` 提供了和 java 里面类似的功能:

```clj
(defn collection? [obj]
  (println "obj is a" (class obj))
  ; Clojure collections implement clojure.lang.IPersistentCollection.
  (or (coll? obj) ; Clojure collection?
      (instance? java.util.Collection obj))) ; Java collection?

(defn average [coll]
  (when-not (collection? coll)
    (throw (IllegalArgumentException. "expected a collection")))
  (when (empty? coll)
    (throw (IllegalArgumentException. "collection is empty")))
  ; Apply the + function to all the items in coll,
  ; then divide by the number of items in it.
  (let [sum (apply + coll)]
    (/ sum (count coll))))

(try
  (println "list average =" (average '(2 3))) ; result is a clojure.lang.Ratio object
  (println "vector average =" (average [2 3])) ; same
  (println "set average =" (average #{2 3})) ; same
  (let [al (java.util.ArrayList.)]
    (doto al (.add 2) (.add 3))
    (println "ArrayList average =" (average al))) ; same
  (println "string average =" (average "1 2 3 4")) ; illegal argument
  (catch IllegalArgumentException e
    (println e)
    ;(.printStackTrace e) ; if a stack trace is desired
  )
  (finally
    (println "in finally")))
```

上面代码的输出是这样的：

```clj
obj is a clojure.lang.PersistentList
list average = 5/2
obj is a clojure.lang.LazilyPersistentVector
vector average = 5/2
obj is a clojure.lang.PersistentHashSet
set average = 5/2
obj is a java.util.ArrayList
ArrayList average = 5/2
obj is a java.lang.String
#<IllegalArgumentException java.lang.IllegalArgumentException:
expected a collection>
in finally
```
