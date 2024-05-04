# Clojure 语法

Lisp 方言有一个非常简洁的语法 — 有些人觉得很美的语法。数据和代码的表达形式是一样的，一个列表的列表很自然地在内存里面表达成一个 tree。(a b c)表示一个对函数 a 的调用，而参数是 b 和 c。如果要表示数据，你需要使用 `'(a b c)` o 或者 `(quote (a b c))` 。通常情况下就是这样了，除了一些特殊情况 — 到底有多少特殊情况取决于你所使用的方言。

我们把这些特殊情况称为语法糖。语法糖越多代码写起来越简介，但是同时我们也要学习更多的东西以读懂这些代码。这需要找到一个平衡点。很多语法糖都有对应的函数可以调用。到底语法糖是多了还是少了还是你们自己来判断吧。

下面这个表格简要地列举了 Clojure 里面的一些语法糖， 这些语法糖我们会在后面详细讲解的，所以如果你现在没办法完全理解它们不用担心。

| 作用                                                                                                                     | 语法糖                                                                                            | 对应函数                                                        |
| ------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------- |
| 注释                                                                                                                     | `; _text_` _单行注释_                                                                             | `宏(comment _text_)可以用来写多行注释`                          |
| 字符 (Java `char` 类型)                                                                                                  | `\_char_` `\tab` `\newline` `\space` `\u_unicode-hex-value_`                                      | `(char _ascii-code_)` `(char \u_unicode_` )                     |
| 字符串 (Java `String` 对象)                                                                                              | `"_text_"`                                                                                        | `(str _char1_ _char2_ ...)` 可以把各种东西串成一个字符串        |
| 关键字是一个内部字符串; 两个同样的关键字指向同一个对象; 通常被用来作为 map 的 key                                        | `:_name_`                                                                                         | `(keyword "_name_")`                                            |
| 当前命名空间的关键字                                                                                                     | `::_name_`                                                                                        | N/A                                                             |
| 正则表达式                                                                                                               | `#"_pattern_"`                                                                                    | `(re-pattern _pattern_)`                                        |
| 逗号被当成空白（通常用在集合里面用来提高代码可读性）                                                                     | `,` (逗号）                                                                                       | N/A                                                             |
| 链表(linked list)                                                                                                        | `'(_items_)` (不会 evaluate 每个元素）                                                            | `(list _items_)` 会 evaluate 每个元素                           |
| vector（和数组类似）                                                                                                     | `[_items_]`                                                                                       | `(vector _items_)`                                              |
| set                                                                                                                      | `#{_items_}` 建立一个 hash-set                                                                    | `(hash-set _items_)` `(sorted-set _items_)`                     |
| map                                                                                                                      | `{_key-value-pairs_}` 建立一个 hash-map                                                           | `(hash-map _key-value-pairs_)` `(sorted-map _key-value-pairs_)` |
| 给 symbol 或者集合绑定元数据                                                                                             | `#^{_key-value-pairs_} _object_` 在读入期处理                                                     | `(with-meta _object_ _metadata-map_)` 在运行期处理              |
| 获取 symbol 或者集合的元数据                                                                                             | `^_object_`                                                                                       | `(meta _object_)`                                               |
| 获取一个函数的参数列表（个数不定的）                                                                                     | `& _name_`                                                                                        | N/A                                                             |
| 函数的不需要的参数的默认名字                                                                                             | `_` (下划线)                                                                                      | N/A                                                             |
| 创建一个 java 对象（注意 class-name 后面的点）                                                                           | `(_class-name_. _args_)`                                                                          | `(new _class-name_ _args_)`                                     |
| 调用 java 方法                                                                                                           | `(. _class-or-instance_ _method-name_ _args_)` 或者 `(._method-name_ _class-or-instance_ _args_)` | N/A                                                             |
| 串起来调用多个函数，前面一个函数的返回值会作为后面一个函数的第一个参数；你还可以在括号里面指定额外参数；注意前面的两个点 | `(.. _class-or-object_ (_method1 args_) (_method2 args_) ...)`                                    | N/A                                                             |
| 创建一个匿名函数                                                                                                         | `#(_single-expression_)` 用 `%` (等同于 `%1` ), `%1` , `%2来表示参数`                             | `(fn [_arg-names_] _expressions_)`                              |
| 获取 Ref, Atom 和 Agent 对应的 valuea                                                                                    | `@_ref_`                                                                                          | `(deref _ref_)`                                                 |
| get `Var` object instead of the value of a symbol (var-quote)                                                            | `#'_name_`                                                                                        | `(var _name_)`                                                  |
| syntax quote (使用在宏里面)                                                                                              | ```                                                                                               | none                                                            |
| unquote (使用在宏里面)                                                                                                   | `~_value_`                                                                                        | `(unquote _value_)`                                             |
| unquote splicing (使用在宏里面)                                                                                          | `~@_value_`                                                                                       | none                                                            |
| auto-gensym (在宏里面用来产生唯一的 symbol 名字)                                                                         | `_prefix_#`                                                                                       | `(gensym _prefix_ )`                                            |

对于二元操作符比如 +和\*, Lisp 方言使用前置表达式而不是中止表达式，这和一般的语言是不一样的。比如在 java 里面你可能会写 `a + b + c` , 而在 Lisp 里面它相当于 `(+ a b c) 。这种表达方式的一个好处是如果操作数有多个，那么操作符只用写一次` . 其它语言里面的二元操作符在 lisp 里面是函数，所以可以有多个操作数。

Lisp 代码比其它语言的代码有更多的小括号的一个原因是 Lisp 里面不使用其它语言使用的大括号，比如在 java 里面，方法代码是被包含在大括号里面的，而在 lisp 代码里面是包含在小括号里面的。

比较下面两段简单的 Java 和 Clojure 代码，它们实现相同的功能。它们的输出都是： “edray” 和 “orangeay”.

```java
// This is Java code.
public class PigLatin {

  public static String pigLatin(String word) {
    char firstLetter = word.charAt(0);
    if ("aeiou".indexOf(firstLetter) != -1) return word + "ay";
    return word.substring(1) + firstLetter + "ay";
  }

  public static void main(String args[]) {
    System.out.println(pigLatin("red"));
    System.out.println(pigLatin("orange"));
  }
}
```

```clojure
; This is Clojure code.
; When a set is used as a function, it returns a boolean
; that indicates whether the argument is in the set.
(def vowel? (set "aeiou"))

(defn pig-latin [word] ; defines a function
  ; word is expected to be a string
  ; which can be treated like a sequence of characters.
  (let [first-letter (first word)] ; assigns a local binding
    (if (vowel? first-letter)
      (str word "ay") ; then part of if
      (str (subs word 1) first-letter "ay")))) ; else part of if

(println (pig-latin "red"))
(println (pig-latin "orange"))
```

Clojure 支持所有的常见数据类型比如 booleans ( `true` and `false` ), 数字, 高精度浮点数, 字符(上面表格里面提到过 ) 以及字符串. 同时还支持分数 — 不是浮点数，因此在计算的过程中不会损失精度.

Symbols 是用来给东西命名的. 这些名字是被限制在名字空间里面的，要么是指定的名字空间，要么是当前的名字空间. Symbols 的值是它所代表的名字的值. 要使用 Symbol 的值，你必须把它用引号引起来.

关键字以冒号打头，被用来当作唯一标示符，通常用在 map 里面 (比如 `:red` , `:green` 和 `:blue` ).

和任何语言一样，你可以写出很难懂的 Clojure 代码。遵循一些最佳实践可以避免这个。写一些简短的，专注自己功能的函数可以使函数变得容易读，测试以及重复利用。经常使用“抽取方法”的模式来对你的代码进行重构。高度内嵌的函数是非常难懂得，千万不要这么写， 你可以使用 let 来帮助你。把匿名函数传递给命名函数是非常常见的，但是不要把一个匿名函数传递给另外一个匿名函数， 这样代码就很难懂了。
