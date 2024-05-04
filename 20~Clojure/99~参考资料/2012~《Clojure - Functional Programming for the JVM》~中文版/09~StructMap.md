# StructMap

StructMap 和普通的 map 类似，它的作用其实是用来模拟 java 里面的 javabean， 所以它比普通的 map 的优点就是，它把一些常用的字段抽象到一个 map 里面去，这样你就不用一遍一遍的重复了。并且和 java 类似，他会帮你生成合适的 `equals` 和 `hashCode` 方法。并且它还提供方式让你可以创建比普通 map 里面的 hash 查找要快的字段访问方法(javabean 里面的 getXXX 方法)。

`create-struct` 函数 和 `defstruct` 宏都可以用来定义 StructMap, defstruct 内部调用的也是 `create-struct` 。map 的 key 通常都是用 keyword 来指定的。看例子:

```
(def vehicle-struct (create-struct :make :model :year :color)) ; long way
(defstruct vehicle-struct :make :model :year :color) ; short way
```

`struct` 实例化 StructMap 的一个对象，相当于 java 里面的 new 关键字. 你提供给 struct 的参数的顺序必须和你定义的时候提供的 keyword 的顺序一致，后面的参数可以忽略， 如果忽略，那么对应 key 的值就是 nil。看例子:

```
(def vehicle (struct vehicle-struct "Toyota" "Prius" 2009))
```

`accessor` 函数可以创建一个类似 java 里面的 getXXX 的方法， 它的好处是可以避免 hash 查找， 它比普通的 hash 查找要快。看例子:

```
; Note the use of def instead of defn because accessor returns
; a function that is then bound to "make".
(def make (accessor vehicle-struct :make))
(make vehicle) ; -> "Toyota"
(vehicle :make) ; same but slower
(:make vehicle) ; same but slower
```

在创建一个 StructMap 之后， 你还可以给它添加在定义 struct 的时候没有指定的 key。但是你不能删除定义时候已经指定的 key。
