# Do Things: A Clojure Crash Course

是时候学习如何真正用 Clojure 做事情了！该死的虽然你肯定听说过 Clojure 超赞的并发支持和其他惊人的特性，但 Clojure 最突出的特点是它是一种 Lisp。在本章中，你将探索构成 Lisp 核心的元素：语法、函数和数据。它们将共同为你打下用 Clojure 表达和解决问题的坚实基础。

打好这些基础后，你就可以编写一些超级重要的代码了。在最后一节中，你将创建一个霍比特人模型，并编写一个函数将其打到任意位置，从而将所有内容串联起来。超级非常重要。在学习本章时，我建议您在 REPL 中键入示例并运行它们。使用新语言编程是一项技能，就像约德尔舞或花样游泳一样，你必须通过练习才能学会。

# Syntax 语法

Clojure 的语法非常简单。与所有 Lisp 一样，它采用统一的结构、少量特殊运算符，以及从 Lisp 诞生地麻省理工学院地下的括号矿井中源源不断运来的括号。

## Forms 表格

所有 Clojure 代码都以统一的结构编写。Clojure 可识别两种结构：

- 数据结构（如数字、字符串、映射和向量）的字面表示法
- 业务

我们使用表单一词来指代有效代码。有时我也会用表达式来指代 Clojure 表单。但不要太在意术语。Clojure 会对每个表单进行求值。这些字面表示都是有效的表单：

```
1
"a string"
["a" "vector" "of" "strings"]
```

当然，您的代码很少会包含自由浮动的字面量，因为它们本身并不做任何事情。相反，您会在操作中使用字面量。操作就是你做事情的方式。所有操作都采用开头括号、操作符、操作数、结尾括号的形式：

```
(operator operand1 operand2 ... operandn)
```

请注意，这里没有逗号。Clojure 使用空白来分隔操作数，并将逗号视为空白。下面是一些操作示例：

```
(+ 1 2 3)
; => 6

(str "It was the panda " "in the library " "with a dust buster")
; => "It was the panda in the library with a dust buster"
```

在第一个操作中，运算符 `+` 将操作数 `1` 、 `2` 和 `3` 相加。在第二个操作中，运算符 `str` 将三个字符串连接起来，形成一个新字符串。这两种形式都是有效的。下面的内容不是表单，因为它没有结尾括号：

```
(+
```

Clojure 的结构统一性可能与您所习惯的不同。在其他语言中，不同的操作可能有不同的结构，这取决于操作符和操作数。例如，JavaScript 使用了大量的 infix 符号、点运算符和括号：

```
1 + 2 + 3
"It was the panda ".concat("in the library ", "with a dust buster")
```

相比之下，Clojure 的结构非常简单一致。无论您使用哪种运算符或操作哪种数据，其结构都是一样的。

## 控制流

让我们来看看三个基本的控制流操作符： if 、 do 和 when 。在本书中，你还会遇到更多的操作符，但这些操作符可以帮助你入门。

### if

这是 `if` 表达式的一般结构：

```
(if boolean-form
  then-form
  optional-else-form)
```

布尔形式只是一种求值为真值或假值的形式。您将在下一节了解真值和假值。下面是几个 if 例子：

```clj
(if true
  "By Zeus's hammer!"
  "By Aquaman's trident!")
; => "By Zeus's hammer!"

(if false
  "By Zeus's hammer!"
  "By Aquaman's trident!")
; => "By Aquaman's trident!"
```

第一个示例返回 `"By Zeus's hammer!"` 是因为布尔形式的求值结果是 `true` ，这是一个真值；第二个示例返回 ` "By ``Aquaman's trident!" ` 是因为布尔形式 `false` 的求值结果是一个假值。

您也可以省略 `else` 分支。如果这样做，并且布尔表达式为 false，Clojure 将返回 `nil` ，就像这样：

```clj
(if false
  "By Odin's Elbow!")
; => nil
```

请注意， `if` 使用操作数位置将操作数与 `then` 和 `else` 分支关联起来：第一个操作数是 `then` 分支，第二个操作数是（可选的） `else` 分支。因此，每个分支只能有一种形式。这与大多数语言不同。例如，您可以用 Ruby 写出这样的代码：

```clj
if true
  doer.do_thing(1)
  doer.do_thing(2)
else
  other_doer.do_thing(1)
  other_doer.do_thing(2)
end
```

要绕过这一明显的限制，可以使用 `do` 操作符。

### do

通过 `do` 操作符，您可以用括号将多个表单包起来，然后运行其中的每个表单。请在您的 REPL 中尝试以下操作：

```clj
(if true
  (do (println "Success!")
      "By Zeus's hammer!")
  (do (println "Failure!")
      "By Aquaman's trident!"))
; => Success!
; => "By Zeus's hammer!"
```

通过该操作符，您可以在 `if` 表达式的每个分支中执行多项操作。在这种情况下，会发生两件事： `Success!` 打印在 REPL 中， `"` 被宙斯的锤子敲击！ `"` 将作为整个 `if` 表达式的值返回。

### when

`when` 运算符就像 `if` 和 `do` 的组合，但没有 `else` 分支。下面是一个例子：

```clj
(when true
  (println "Success!")
  "abra cadabra")
; => Success!
; => "abra cadabra"
```

如果您想在某个条件为真时执行多项操作，并且总是想在条件为假时返回 `nil` ，请使用 `when` 。

### nil、true、false、真实性、等价和布尔表达式

Clojure 有 `true` 和 `false` 值。在 Clojure 中， `nil` 用于表示无值。您可以使用适当命名的 `nil?` 函数检查某个值是否为 `nil` ：

```
(nil? 1)
; => false

(nil? nil)
; => true
```

`nil` 和 `false` 都用于表示逻辑虚假性，而所有其他值在逻辑上都是真实的。Truthy 和 falsey 指的是在布尔表达式中如何处理某个值，例如传递给 `if` 的第一个表达式：

```
(if "bears eat beets"
  "bears beets Battlestar Galactica")
; => "bears beets Battlestar Galactica"

(if nil
  "This won't be the result because nil is falsey"
  "nil is falsey")
; => "nil is falsey"
```

在第一个示例中，字符串 `"bears eat beets"` 被视为真值，因此 `if` 表达式的值为 `"bears beets Battlestar Galactica"` 。第二个示例将一个虚假值显示为虚假值。

Clojure 的相等运算符是 `=` ：

```
(= 1 1)
; => true

(= nil nil)
; => true

(= 1 2)
; => false
```

有些其他语言要求您在比较不同类型的值时使用不同的运算符。例如，您可能需要使用某种专为字符串设计的特殊字符串相等运算符。但在使用 Clojure 内置数据结构时，您不需要任何奇怪或繁琐的操作来测试相等性。

Clojure 使用布尔运算符 `or` 和 `and` 。 `or` 返回第一个真值或最后一个值。 `and` 返回第一个虚假值，如果没有虚假值，则返回最后一个真实值。让我们先看看 `or` ：

```
(or false nil :large_I_mean_venti :why_cant_I_just_say_large)
; => :large_I_mean_venti

(or (= 0 1) (= "yes" "no"))
; => false

(or nil)
; => nil
```

在第一个示例中，返回值是 `:large_I_mean_venti` ，因为它是第一个真值。第二个示例没有真值，因此 `or` 返回最后一个值，即 `false` 。在最后一个示例中，同样不存在真值，因此 `or` 返回最后一个值，即 `nil` 。现在让我们看看 `and` ：

```
(and :free_wifi :hot_coffee)
; => :hot_coffee

(and :feelin_super_cool nil false)
; => nil
```

在第一个示例中， `and` 返回最后一个真值 `:hot_coffee` 。在第二个示例中， `and` 返回 `nil` ，这是第一个假值。

## 使用 def 命名

在 Clojure 中，您可以使用 `def` 将名称绑定到值：

```clj
(def failed-protagonist-names
  ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"])

failed-protagonist-names
; => ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"]
```

在这种情况下，您要将名称 `failed-protagonist-names` 绑定到包含三个字符串的向量上（您将在第 45 页的 "向量 "中了解向量）。

请注意，我使用的是绑定（bind）这个术语，而在其他语言中，你会说你在给变量赋值。其他语言通常会鼓励你对同一个变量进行多次赋值。

例如，在 Ruby 中，您可能会对一个变量执行多次赋值，以积累其值：

```clj
severity = :mild
error_message = "OH GOD! IT'S A DISASTER! WE'RE "
if severity == :mild
  error_message = error_message + "MILDLY INCONVENIENCED!"
else
  error_message = error_message + "DOOOOOOOMED!"
end
```

您可能会想在 Clojure 中做类似的事情：

```clj
(def severity :mild)
(def error-message "OH GOD! IT'S A DISASTER! WE'RE ")
(if (= severity :mild)
  (def error-message (str error-message "MILDLY INCONVENIENCED!"))
  (def error-message (str error-message "DOOOOOOOMED!")))
```

然而，像这样更改与名称相关的值会增加理解程序行为的难度，因为更难知道哪个值与名称相关，或者为什么该值会发生变化。Clojure 有一套处理变化的工具，你将在第 10 章中了解到。在学习 Clojure 的过程中，你会发现很少需要更改名称/值关联。下面是前面代码的一种写法：

```clj
(defn error-message
  [severity]
  (str "OH GOD! IT'S A DISASTER! WE'RE "
       (if (= severity :mild)
         "MILDLY INCONVENIENCED!"
         "DOOOOOOOMED!")))

(error-message :mild)
; => "OH GOD! IT'S A DISASTER! WE'RE MILDLY INCONVENIENCED!"
```

在这里，您将创建一个函数 `error-message` ，该函数接受一个参数 `severity` 并使用该参数来决定返回哪个字符串。然后，您将使用 `:mild` 调用该函数的严重性。您将在第 48 页的 "函数 "中了解创建函数的全部内容；在此期间，您应将 `def` 视为定义常量。在接下来的几章中，您将学习如何通过采用函数式编程范式来克服这一明显的限制。

# 数据结构

Clojure 自带的数据结构不胜枚举，您在大多数情况下都会用到它们。如果你来自面向对象的背景，你会惊讶地发现，这里介绍的这些看似基本的类型可以做很多事情。

Clojure 的所有数据结构都是不可变的，这意味着您无法在原处更改它们。例如，在 Ruby 中，您可以执行以下操作来重新分配索引 0 中失败的主角名称：

```clj
failed_protagonist_names = [
  "Larry Potter",
  "Doreen the Explorer",
  "The Incredible Bulk"
]
failed_protagonist_names[0] = "Gary Potter"

failed_protagonist_names
# => [
#   "Gary Potter",
#   "Doreen the Explorer",
#   "The Incredible Bulk"
# ]
```

Clojure 没有与之对应的功能。你将在第 10 章中进一步了解 Clojure 为什么要这样实现，但现在，学习如何做事情而不需要哲学思考也很有趣。废话不多说，让我们来看看 Clojure 中的数字。

## 数字

Clojure 拥有相当复杂的数值支持。我不会花太多时间纠缠于枯燥的技术细节（如强制和传染），因为那会妨碍我们做事。如果你对这些枯燥的细节感兴趣，在此期间，我们将使用整数和浮点数。我们还将使用 Clojure 可以直接表示的比率。下面分别是一个整数、一个浮点数和一个比率：

```clj
93
1.2
1/5
```

## 字符串

字符串代表文字。字符串的名字来源于古代腓尼基人，他们在一次纱线事故后发明了字母表。下面是一些字符串字面量的示例：

```
"Lord Voldemort"
"\"He who must not be named\""
"\"Great cow of Moscow!\" - Hermes Conrad"
```

请注意，Clojure 只允许用双引号来划分字符串。例如， `'Lord Voldemort'` 就不是一个有效的字符串。还要注意的是，Clojure 没有字符串插值。它只允许通过 `str` 函数进行连接：

```
(def name "Chewbacca")
(str "\"Uggllglglglglglglglll\" - " name)
; => "Uggllglglglglglglglll" - Chewbacca
```

## 映射

映射类似于其他语言中的字典或哈希值。它们是将某些值与其他值关联起来的一种方式。Clojure 中的两种映射是散列映射和排序映射。我将只介绍更基本的哈希映射。让我们来看一些映射字面量的例子。下面是一个空映射：

```
{}
```

在本例中， `:first-name` 和 `:last-name` 是关键字（我将在下一节中介绍这些关键字）：

```
{:first-name "Charlie"
 :last-name "McFishwich"}
```

在这里，我们将 `"string-key"` 与 `+` 函数联系起来：

```
{"string-key" +}
```

地图可以嵌套：

```
{:name {:first "John" :middle "Jacob" :last "Jingleheimerschmidt"}}
```

请注意，map 值可以是任何类型--字符串、数字、映射、向量，甚至函数。Clojure 并不在乎这些！

除了使用映射字面量，您还可以使用 `hash-map` 函数来创建映射：

```
(hash-map :a 1 :b 2)
; => {:a 1 :b 2}
```

您可以使用 `get` 函数查找地图中的值：

```
(get {:a 0 :b 1} :b)
; => 1

(get {:a 0 :b {:c "ho hum"}} :b)
; => {:c "ho hum"}
```

在这两个示例中，我们都向 `get` 询问了给定映射中 `:b` 键的值，第一个示例返回 `1` ，第二个示例返回嵌套映射 `{:c "ho hum"}` 。

如果找不到键， `get` 将返回 `nil` ，您也可以给它一个默认返回值，例如 `"unicorns?"` ：

```
(get {:a 0 :b 1} :c)
; => nil

(get {:a 0 :b 1} :c "unicorns?")
; => "unicorns?"
```

`get-in` 函数可让您查找嵌套地图中的值：

```
(get-in {:a 0 :b {:c "ho hum"}} [:b :c])
; => "ho hum"
```

在映射表中查找值的另一种方法是将映射表视为一个以键为参数的函数：

```
({:name "The Human Coffeepot"} :name)
; => "The Human Coffeepot"
```

使用地图的另一个很酷的功能是将关键字作为查找其值的函数，这就引出了下一个主题--关键字。

## 关键词

要理解 Clojure 关键字，最好先看看它们是如何使用的。如上一节所述，关键字主要用作映射中的键。下面是关键字的更多示例：

```
:a
:rumplestiltsken
:34
:_?
```

关键字可用作函数，在数据结构中查找相应的值。例如，您可以在 map 中查找 `:a` ：

```
(:a {:a 1 :b 2 :c 3})
; => 1
```

这相当于

```
(get {:a 1 :b 2 :c 3} :a)
; => 1
```

您可以提供一个默认值，如 `get` ：

```
(:d {:a 1 :b 2 :c 3} "No gnome knows homes like Noah knows")
; => "No gnome knows homes like Noah knows"
```

将关键字用作函数非常简洁，真正的 Clojurists 总是这样做。你也应该这么做！

## 向量

向量与数组类似，都是 0 索引的集合。例如，下面是一个向量字面：

```
[3 2 1]
```

这里我们要返回一个向量的第 0 个元素：

```
(get [3 2 1] 0)
; => 3
```

下面是另一个通过索引获取的例子：

```
(get ["a" {:name "Pugsley Winterbottom"} "c"] 1)
; => {:name "Pugsley Winterbottom"}
```

可以看到，向量元素可以是任何类型，而且可以混合类型。另外请注意，我们使用的 `get` 函数与在地图中查找值时使用的函数相同。

您可以使用 `vector` 函数创建向量：

```
(vector "creepy" "full" "moon")
; => ["creepy" "full" "moon"]
```

您可以使用 `conj` 函数为向量添加其他元素。元素会被添加到向量的末尾：

```
(conj [1 2 3] 4)
; => [1 2 3 4]
```

向量并不是存储序列的唯一方法；Clojure 还有列表。

## 列表

列表与矢量类似，都是值的线性集合。但也有一些不同之处。例如，不能用 `get` 来检索列表元素。要编写列表文字，只需将元素插入括号，并在开头使用单引号：

```
'(1 2 3 4)
; => (1 2 3 4)
```

请注意，当 REPL 打印出列表时，并不包含单引号。稍后我们将在第 7 章中讨论为什么会出现这种情况。如果要从列表中检索元素，可以使用 `nth` 函数：

```
(nth '(:a :b :c) 0)
; => :a

(nth '(:a :b :c) 2)
; => :c
```

在本书中，我没有详细介绍性能，因为我认为在熟悉一门语言之前，关注性能是没有用的。不过，使用 `nth` 从列表中检索元素比使用 `get` 从向量中检索元素要慢，这一点还是很好理解的。这是因为 Clojure 必须遍历列表中的所有 n 个元素才能找到第 n 个元素，而通过索引访问一个向量元素最多只需要几跳。

列表值可以是任何类型，您可以使用 `list` 函数创建列表：

```
(list 1 "two" {3 4})
; => (1 "two" {3 4})
```

元素被添加到列表的开头：

```
(conj '(1 2 3) 4)
; => (4 1 2 3)
```

什么时候应该使用列表，什么时候应该使用矢量？一个好的经验法则是，如果需要轻松地将项目添加到序列的开头，或者如果要编写一个宏，就应该使用列表。否则，就应该使用矢量。随着学习的深入，你会对何时使用哪种方式有很好的感觉。

## 集合

集合是唯一值的集合。Clojure 有两种集合：散列集合和排序集合。由于散列集使用得更频繁，所以我将重点介绍散列集。下面是散列集的文字符号：

```
#{"kurt vonnegut" 20 :icicle}
```

您也可以使用 `hash-set` 创建一个集合：

```
(hash-set 1 1 2 2)
; => #{1 2}
```

请注意，一个值的多个实例将在集合中成为一个唯一值，因此我们只剩下一个 `1` 和一个 `2` 。如果您尝试将一个值添加到一个已经包含该值的集合中（例如下面代码中的 `:b` ），那么该值仍然只有一个：

```
(conj #{:a :b} :b)
; => #{:a :b}
```

您还可以使用 `set` 函数从现有向量和列表中创建集合：

```
(set [3 3 3 4 4])
; => #{3 4}
```

您可以使用 `contains?` 函数、 `get` 或以集合为参数的关键字函数来检查集合成员资格。 `contains?` 返回 `true` 或 `false` ，而 `get` 和关键字查找会在值存在时返回值，在不存在时返回 `nil` 。

下面是使用 `contains?` ：

```clj
(contains? #{:a :b} :a)
; => true

(contains? #{:a :b} 3)
; => false

(contains? #{nil} nil)
; => true
```

下面是关键字的使用方法：

```
(:a #{:a :b})
; => :a
```

下面是使用 `get` 的方法：

```
(get #{:a :b} :a)
; => :a

(get #{:a nil} nil)
; => nil

(get #{:a :b} "kurt vonnegut")
; => nil
```

请注意，使用 `get` 来测试集合是否包含 `nil` 将始终返回 `nil` ，这会引起混淆。当您专门测试集合成员资格时， `contains?` 可能是更好的选择。

## 简约

您可能已经注意到，到目前为止对数据结构的处理并不包括如何创建新类型或类的描述。这是因为 Clojure 强调简洁性，鼓励你首先使用内置的数据结构。

如果你来自面向对象的背景，你可能会认为这种方法既奇怪又落后。然而，您会发现，您的数据并不一定要与类紧密捆绑在一起才有用、才易懂。这里有一句深受 Clojurists 喜欢的格言，暗示了 Clojure 的哲学：

> 让 100 个函数在一个数据结构上运行，总比让 10 个函数在 10 个数据结构上运行要好。
> -Alan Perlis

在接下来的章节中，您将了解到更多有关 Clojure 哲学的内容。现在，请留意通过坚持使用基本数据结构来获得代码重用性的方法。

我们的 Clojure 数据结构入门课程到此结束。现在是时候深入研究函数，学习如何使用这些数据结构了！

# 函数

人们为 Lisp 疯狂的原因之一是，这些语言可以让你构建行为复杂的程序，但其主要构件--函数--却是如此简单。本节将通过讲解以下内容，让你初步领略 Lisp 函数的美丽和优雅：

- 调用功能
- 函数与宏和特殊形式的区别
- 定义功能
- 匿名功能
- 返回功能

## 调用函数

现在，你已经看过很多函数调用的例子了：

```
(+ 1 2 3 4)
(* 1 2 3 4)
(first [1 2 3 4])
```

请记住，所有 Clojure 操作都有相同的语法：开括号、操作符、操作数、闭括号。函数调用只是操作符为函数或函数表达式（返回函数的表达式）的操作的另一种说法。

这样，您就可以编写一些非常有趣的代码。下面是一个返回 `+` （加法）函数的函数表达式：

```
(or + -)
; => #<core$_PLUS_ clojure.core$_PLUS_@76dace31>
```

返回值是加法函数的字符串表示。由于 `or` 的返回值是第一个真值，而这里的加法函数是真值，因此返回的是加法函数。您还可以将此表达式用作其他表达式中的运算符：

```
((or + -) 1 2 3)
; => 6
```

由于 `(or + -)` 返回 `+` ，因此该表达式的求值结果为 `1` 、 `2` 和 `3` 的和，返回 `6` 。

下面是几个有效的函数调用，每个函数都返回 `6` ：

```
((and (= 1 1) +) 1 2 3)
; => 6

((first [+ 0]) 1 2 3)
; => 6
```

在第一个示例中， `and` 的返回值是第一个虚假值或最后一个真实值。在本例中，返回 `+` 因为它是最后一个真值，然后应用于参数 `1 2 3` ，返回 `6` 。在第二个示例中， `first` 的返回值是序列中的第一个元素，在本例中是 `+` 。

但是，这些都不是有效的函数调用，因为数字和字符串都不是函数：

```
(1 2 3 4)
("test" 1 2 3)
```

如果在 REPL 中运行这些代码，会得到类似下面的结果：

```
ClassCastException java.lang.String cannot be cast to clojure.lang.IFn
user/eval728 (NO_SOURCE_FILE:1)
```

在继续学习 Clojure 的过程中，您可能会多次看到这个错误：<x> cannot be cast to clojure.lang.IFn 只表示您试图将某个东西用作函数，而它并不是。

函数的灵活性并不止于函数表达式！在语法上，函数可以将任何表达式作为参数，包括其他函数。可以将函数作为参数或返回函数的函数称为高阶函数。具有高阶函数的编程语言被称为支持一级函数，因为您可以像对待数字和向量等更熟悉的数据类型一样，将函数视为值。

以 `map` 函数（不要与 map 数据结构混淆）为例。 `map` 通过对集合的每个成员应用一个函数来创建一个新的列表。在这里， `inc` 函数将一个数字递增 1：

```
(inc 1.1)
; => 2.1

(map inc [0 1 2 3])
; => (1 2 3 4)
```

(请注意， `map` 并不返回一个向量，尽管我们提供了一个向量作为参数。你将在第 4 章了解原因。现在，请相信这是没问题的，也是预料之中的）。

Clojure 对一级函数的支持使您可以构建比没有一级函数的语言更强大的抽象。对于不熟悉此类编程的人来说，函数可以让您对数据实例进行一般化操作。例如， `+` 函数抽象了对任何特定数字的加法运算。

相比之下，Clojure（以及所有 Lisps）允许您创建泛化流程的函数。 `map` 允许您通过在任意集合上应用函数（任意函数）来泛化转换集合的过程。

关于函数调用，您需要知道的最后一个细节是，Clojure 在将所有函数参数传递给函数之前，会对它们进行递归评估。下面是 Clojure 如何评估参数也是函数调用的函数调用：

```
(+ (inc 199) (/ 100 (- 7 2)))
(+ 200 (/ 100 (- 7 2))) ; evaluated "(inc 199)"
(+ 200 (/ 100 5)) ; evaluated (- 7 2)
(+ 200 20) ; evaluated (/ 100 5)
220 ; final evaluation
```

函数调用启动了评估流程，在应用 `+` 函数之前，会对所有子表单进行评估。

## 函数调用、宏调用和特殊形式

在上一节中，我们了解到函数调用是以函数表达式作为运算符的表达式。另外两种表达式是宏调用和特殊形式。你已经见过几种特殊形式：定义和 `if` 表达式。

关于宏调用和特殊形式的所有知识，你将在第 7 章中学习到。目前，使特殊形式 "特殊 "的主要特征是，与函数调用不同，它们并不总是对所有操作数进行运算。

以 `if` 为例。这是它的一般结构：

```
(if boolean-form
  then-form
  optional-else-form)
```

现在想象一下，你有一个这样的 `if` 语句：

```
(if good-mood
  (tweet walking-on-sunshine-lyrics)
  (tweet mopey-country-song-lyrics))
```

显然，在这样的 `if` 表达式中，我们希望 Clojure 只评估两个分支中的一个。如果 Clojure 对 `tweet` 两个函数调用都进行了评估，那么你的 Twitter 关注者会感到非常困惑。

特殊表单的另一个不同之处在于不能将其用作函数的参数。一般来说，特殊形式实现了 Clojure 的核心功能，而这些功能无法用函数来实现。Clojure 只有少数几种特殊形式，如此丰富的语言只用这么一小部分构建模块就能实现，实在令人惊叹。

宏与特殊形式类似，它们对操作数的评估方式与函数调用不同，也不能作为参数传递给函数。绕了这么多弯路，现在该学习如何定义函数了！

## 定义功能

函数定义由五个主要部分组成：

- `defn`
- 功能名称
- 描述函数的文档字符串（可选）
- 括号中列出的参数
- 功能机构

下面是一个函数定义和函数调用示例：

```
➊ (defn too-enthusiastic
➋   "Return a cheer that might be a bit too enthusiastic"
➌   [name]
➍   (str "OH. MY. GOD! " name " YOU ARE MOST DEFINITELY LIKE THE BEST "
  "MAN SLASH WOMAN EVER I LOVE YOU AND WE SHOULD RUN AWAY SOMEWHERE"))

(too-enthusiastic "Zelda")
; => "OH. MY. GOD! Zelda YOU ARE MOST DEFINITELY LIKE THE BEST MAN SLASH WOMAN EVER I LOVE YOU AND WE SHOULD RUN AWAY SOMEWHERE"
```

在 ➊ 处， `too-enthusiastic` 是函数的名称，其后在 ➋ 处是描述性 docstring。在 ➌ 处给出了参数 `name` ，在 ➍ 处的函数体接收了参数，并按其表述行事--返回一个可能有点过于热情的欢呼声。

让我们深入了解 docstring、参数和函数体。

#### 文件字符串

docstring 是描述和记录代码的有用方法。您可以在 REPL 中使用 `(doc `fn-name `)` 查看函数的 docstring，例如 `(doc map)` 。如果使用工具为代码生成文档，docstring 也会发挥作用。

#### 参数和实体

Clojure 函数可以定义零个或多个参数。传递给函数的值称为参数，参数可以是任何类型。参数的数量就是函数的 "弧度"。下面是一些具有不同参数的函数定义：

```
(defn no-params
  []
  "I take no parameters!")
(defn one-param
  [x]
  (str "I take one parameter: " x))
(defn two-params
  [x y]
  (str "Two parameters! That's nothing! Pah! I will smoosh them "
  "together to spite you! " x y))
```

在这些示例中， `no-params` 是 0-arity 函数， `one-param` 是 1-arity 函数， `two-params` 是 2-arity 函数。

函数还支持迭代重载。这意味着你可以定义一个函数，使不同的函数体可以根据不同的 "有效性 "运行。下面是多重性函数定义的一般形式。请注意，每个类型定义都用括号括起来，并有一个参数列表：

```
(defn multi-arity
  ;; 3-arity arguments and body
  ([first-arg second-arg third-arg]
     (do-things first-arg second-arg third-arg))
  ;; 2-arity arguments and body
  ([first-arg second-arg]
     (do-things first-arg second-arg))
  ;; 1-arity arguments and body
  ([first-arg]
     (do-things first-arg)))
```

Arity 重载是为参数提供默认值的一种方法。在下面的示例中， `"karate"` 是 `chop-type` 参数的默认参数：

```
(defn x-chop
  "Describe the kind of chop you're inflicting on someone"
  ([name chop-type]
     (str "I " chop-type " chop " name "! Take that!"))
  ([name]
     (x-chop name "karate")))
```

如果使用两个参数调用 `x-chop` ，该函数的工作原理与它不是多重性函数时一样：

```
(x-chop "Kanye West" "slap")
; => "I slap chop Kanye West! Take that!"
```

如果调用 `x-chop` 时只提供一个参数，那么 `x-chop` 实际上将调用自身，并提供第二个参数 `"karate"` ：

```
(x-chop "Kanye East")
; => "I karate chop Kanye East! Take that!"
```

这样用函数本身来定义函数似乎不太寻常。如果是这样，那太好了！你正在学习一种新的方法！

您也可以让每个假说做一些完全无关的事情：

```
(defn weird-arity
  ([]
     "Destiny dressed you this morning, my friend, and now Fear is
     trying to pull off your pants. If you give up, if you give in,
     you're gonna end up naked with Fear just standing there laughing
     at your dangling unmentionables! - the Tick")
  ([number]
     (inc number)))
```

0-arity 主体返回一个明智的引号，而 1-arity 主体则递增一个数字。您很可能不想写这样的函数，因为有两个完全不相关的函数体会让人感到困惑。

Clojure 还允许您通过包含一个其余参数来定义可变性函数，如 "将这些参数的其余部分放入一个名称如下的列表中"。其余参数用一个双引号（ `&` ）表示，如 ➊ 所示：

![img](https://www.braveclojure.com/assets/images/cftbat/do-things/old-man.png)

```
(defn codger-communication
  [whippersnapper]
  (str "Get off my lawn, " whippersnapper "!!!"))

(defn codger
➊   [& whippersnappers]
  (map codger-communication whippersnappers))

(codger "Billy" "Anne-Marie" "The Incredible Bulk")
; => ("Get off my lawn, Billy!!!"
      "Get off my lawn, Anne-Marie!!!"
      "Get off my lawn, The Incredible Bulk!!!")
```

正如您所看到的，当您为可变性函数提供参数时，参数会被视为一个列表。您可以将其余参数与普通参数混合使用，但其余参数必须放在最后：

```
(defn favorite-things
  [name & things]
  (str "Hi, " name ", here are my favorite things: "
       (clojure.string/join ", " things)))

(favorite-things "Doreen" "gum" "shoes" "kara-te")
; => "Hi, Doreen, here are my favorite things: gum, shoes, kara-te"
```

最后，Clojure 还有一种更复杂的参数定义方法，称为 "重构"（destructuring），值得单独列出一小节。

#### 结构调整

重构的基本原理是，它可以让你简洁地将名称绑定到集合中的值上。让我们来看一个基本例子：

```
;; Return the first element of a collection
(defn my-first
  [[first-thing]] ; Notice that first-thing is within a vector
  first-thing)

(my-first ["oven" "bike" "war-axe"])
; => "oven"
```

在这里， `my-first` 函数将符号 `first-thing` 与作为参数传递的向量的第一个元素关联起来。您可以通过将符号 `first-thing` 置于向量中来告诉 `my-first` 执行此操作。

这个向量就像一个巨大的标牌，向 Clojure 表示："嘿！这个函数将接收一个列表或向量作为参数。帮我拆开参数的结构，并为参数的不同部分命名有意义的名称，让我的生活更轻松！"在对向量或列表进行重构时，你可以为任意多个元素命名，也可以使用其余参数：

```
(defn chooser
  [[first-choice second-choice & unimportant-choices]]
  (println (str "Your first choice is: " first-choice))
  (println (str "Your second choice is: " second-choice))
  (println (str "We're ignoring the rest of your choices. "
                "Here they are in case you need to cry over them: "
                (clojure.string/join ", " unimportant-choices))))

(chooser ["Marmalade", "Handsome Jack", "Pigpen", "Aquaman"])
; => Your first choice is: Marmalade
; => Your second choice is: Handsome Jack
; => We're ignoring the rest of your choices. Here they are in case \
     you need to cry over them: Pigpen, Aquaman
```

在这里，其余参数 `unimportant` `-choices` 用于处理用户在第一和第二个选择之后的任何其他选择。

您还可以重组映射。与通过提供一个向量作为参数来告诉 Clojure 去结构化一个向量或列表的方式相同，您也可以通过提供一个映射作为参数来去结构化映射：

```
(defn announce-treasure-location
➊   [{lat :lat lng :lng}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))

(announce-treasure-location {:lat 28.22 :lng 81.33})
; => Treasure lat: 28.22
; => Treasure lng: 81.33
```

让我们更详细地看看 ➊ 这一行。这就像是在告诉 Clojure："哟！Clojure！帮我个忙，将名称 `lat` 与键 `:lat` 对应的值关联起来。对 `lng` 和 `:lng` 做同样的事情，好吗？"

我们经常希望将关键字从地图中分离出来，因此有一种更简短的语法。这样做的结果与上一个示例相同：

```
(defn announce-treasure-location
  [{:keys [lat lng]}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))
```

通过使用 `:as` 关键字，可以保留对原始地图参数的访问。在下面的示例中，使用 `treasure-location` 访问原始映射：

```
(defn receive-treasure-location
  [{:keys [lat lng] :as treasure-location}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng))

  ;; One would assume that this would put in new coordinates for your ship
  (steer-ship! treasure-location))
```

一般来说，您可以将析构看作是指示 Clojure 如何将名称与列表、映射、集合或向量中的值相关联。现在，我们来看看函数中真正发挥作用的部分：函数体！

#### 功能机构

函数体可以包含任何形式的表单。Clojure 会自动返回最后求值的表单。此函数体仅包含三个表单，当您调用该函数时，它会吐出最后一个表单 `"joe"` ：

```
(defn illustrative-function
  []
  (+ 1 304)
  30
  "joe")

(illustrative-function)
; => "joe"
```

下面是另一个使用 `if` 表达式的函数体：

```
(defn number-comment
  [x]
  (if (> x 6)
    "Oh my gosh! What a big number!"
    "That number's OK, I guess"))

(number-comment 5)
; => "That number's OK, I guess"

(number-comment 7)
; => "Oh my gosh! What a big number!"
```

#### 所有功能都是平等的

最后一点：Clojure 没有特权函数。 `+` 只是一个函数， `-` 只是一个函数，而 `inc` 和 `map` 只是函数。它们并不比你自己定义的函数更好。所以，不要让它们给你留下任何口实！

更重要的是，这一事实有助于证明 Clojure 的底层简单性。在某种程度上，Clojure 是非常愚蠢的。当你调用一个函数时，Clojure 会说：" `map` ?当然，随便你！我就应用这个，然后继续"。它并不关心函数是什么，也不关心函数来自哪里；它对所有函数都一视同仁。Clojure 根本不关心加法、乘法或映射。它只关心函数的应用。

当您继续使用 Clojure 编程时，您会发现这种简单性是最理想的。您不必担心处理不同函数的特殊规则或语法。它们的工作原理都是一样的！

## 匿名函数

在 Clojure 中，函数不需要名称。事实上，你会经常使用匿名函数。匿名函数有多神秘？创建匿名函数有两种方法。第一种是使用 `fn` 形式：

```
(fn [param-list]
  function body)
```

看起来很像 `defn` ，不是吗？让我们举几个例子：

```
(map (fn [name] (str "Hi, " name))
     ["Darth Vader" "Mr. Magoo"])
; => ("Hi, Darth Vader" "Hi, Mr. Magoo")

((fn [x] (* x 3)) 8)
; => 24
```

处理 `fn` 的方法与处理 `defn` 的方法几乎完全相同。参数列表和函数体的工作方式完全相同。您可以使用参数重构、其余参数等。您甚至可以将匿名函数与名称关联起来，这并不令人惊讶（如果令人惊讶的话，那就......惊讶吧！）：

```
(def my-special-multiplier (fn [x] (* x 3)))
(my-special-multiplier 12)
; => 36
```

Clojure 还提供了另一种更简洁的匿名函数创建方式。下面就是匿名函数的样子：

```
#(* % 3)
```

哇，看起来好奇怪。继续应用那个看起来很奇怪的函数吧：

```
(#(* % 3) 8)
; => 24
```

下面是一个将匿名函数作为参数传递给 map 的示例：

```
(map #(str "Hi, " %)
     ["Darth Vader" "Mr. Magoo"])
; => ("Hi, Darth Vader" "Hi, Mr. Magoo")
```

这种看似奇怪的匿名函数书写方式得益于一种称为读者宏的功能。你将在第 7 章中了解到有关这些宏的全部内容。现在，只学习如何使用这些匿名函数就可以了。

可以看出，这种语法无疑更紧凑，但也有点奇怪。让我们来分析一下。这种匿名函数看起来很像函数调用，但它前面有一个哈希标记，即 `#` ：

```
;; Function call
(* 8 3)

;; Anonymous function
#(* % 3)
```

这种相似性可以让你更快地看到应用这个匿名函数时会发生什么。"哦，"你可以对自己说，"这个函数将把它的参数乘以 3。

您可能已经猜到，百分号 `%` 表示传递给函数的参数。如果您的匿名函数接收多个参数，您可以这样区分它们： `%1` , `%2` , `%3` 等等。 `%` 等同于 `%1` ：

```
(#(str %1 " and " %2) "cornbread" "butter beans")
; => "cornbread and butter beans"
```

您也可以使用 `%&` 传递其余参数：

```
(#(identity %&) 1 "blarg" :yip)
; => (1 "blarg" :yip)
```

在这种情况下，你对其余参数使用了身份函数。Identity 返回给定的参数，不会对其进行修改。其余参数以列表形式存储，因此函数应用会返回一个包含所有参数的列表。

如果您需要编写一个简单的匿名函数，使用这种样式是最好的，因为它在视觉上非常紧凑。另一方面，如果您要编写一个较长、较复杂的函数，这种样式很容易变得难以阅读。如果是这种情况，请使用 `fn` 。

## 返回函数

现在你已经知道函数可以返回其他函数。返回的函数是闭包，这意味着它们可以访问函数创建时作用域中的所有变量。下面是一个标准示例：

```
(defn inc-maker
  "Create a custom incrementor"
  [inc-by]
  #(+ % inc-by))

(def inc3 (inc-maker 3))

(inc3 7)
; => 10
```

在这里， `inc-by` 处于作用域中，因此即使在 `inc-maker` 以外使用返回函数，返回函数也可以访问它。

# 一个完整的例子

好了是时候把你新学到的知识用于一个崇高的目的了：揍霍比特人！要击中一个霍比特人，首先要对其身体部位进行建模。每个身体部位都包括相对大小，以显示该部位被击中的可能性。为了避免重复，霍比特人模型将只包含左脚、左耳等条目。因此，您需要一个函数来完全对称模型，创建右脚、右耳等。最后，您将创建一个函数，对身体各部分进行迭代，并随机选择命中的部分。在学习过程中，您还会了解到一些新的 Clojure 工具： `let` 表达式、循环和正则表达式。有趣

### 夏尔的下一个超模

对于我们的霍比特人模型，我们将摒弃霍比特人的活泼和调皮等特点，只关注霍比特人的小身板。这就是哈比人模型：

```
(def asym-hobbit-body-parts [{:name "head" :size 3}
                             {:name "left-eye" :size 1}
                             {:name "left-ear" :size 1}
                             {:name "mouth" :size 1}
                             {:name "nose" :size 1}
                             {:name "neck" :size 2}
                             {:name "left-shoulder" :size 3}
                             {:name "left-upper-arm" :size 3}
                             {:name "chest" :size 10}
                             {:name "back" :size 10}
                             {:name "left-forearm" :size 3}
                             {:name "abdomen" :size 6}
                             {:name "left-kidney" :size 1}
                             {:name "left-hand" :size 2}
                             {:name "left-knee" :size 2}
                             {:name "left-thigh" :size 4}
                             {:name "left-lower-leg" :size 3}
                             {:name "left-achilles" :size 1}
                             {:name "left-foot" :size 2}])
```

这是一个地图矢量。每张地图上都有身体部位的名称和相对大小。(我知道只有动漫人物的眼睛才有头部的三分之一大，但就这样吧，好吗？）

霍比特人的右侧明显缺失。让我们来解决这个问题。清单 3-1 是你目前看到的最复杂的代码，它引入了一些新的想法。不过不用担心，因为我们会详细研究它。

```
(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})

(defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (loop [remaining-asym-parts asym-body-parts
         final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      (let [[part & remaining] remaining-asym-parts]
        (recur remaining
               (into final-body-parts
                     (set [part (matching-part part)])))))))
```

1. 3-1.匹配部分和对称体部分函数

当我们在 `asym-hobbit-body-parts` 上调用函数 `symmetrize-body-parts` 时，我们会得到一个完全对称的霍比特人：

```
(symmetrize-body-parts asym-hobbit-body-parts)
; => [{:name "head", :size 3}
      {:name "left-eye", :size 1}
      {:name "right-eye", :size 1}
      {:name "left-ear", :size 1}
      {:name "right-ear", :size 1}
      {:name "mouth", :size 1}
      {:name "nose", :size 1}
      {:name "neck", :size 2}
      {:name "left-shoulder", :size 3}
      {:name "right-shoulder", :size 3}
      {:name "left-upper-arm", :size 3}
      {:name "right-upper-arm", :size 3}
      {:name "chest", :size 10}
      {:name "back", :size 10}
      {:name "left-forearm", :size 3}
      {:name "right-forearm", :size 3}
      {:name "abdomen", :size 6}
      {:name "left-kidney", :size 1}
      {:name "right-kidney", :size 1}
      {:name "left-hand", :size 2}
      {:name "right-hand", :size 2}
      {:name "left-knee", :size 2}
      {:name "right-knee", :size 2}
      {:name "left-thigh", :size 4}
      {:name "right-thigh", :size 4}
      {:name "left-lower-leg", :size 3}
      {:name "right-lower-leg", :size 3}
      {:name "left-achilles", :size 1}
      {:name "right-achilles", :size 1}
      {:name "left-foot", :size 2}
      {:name "right-foot", :size 2}]
```

让我们来分析一下这段代码！

### let

在清单 3-1 中的大量疯狂代码中，您可以看到 `(let ...)` 结构的一种形式。让我们通过一个个示例来加深对 `let` 的理解，然后在熟悉所有片段后，再查看程序中的完整示例。

`let` 将名称与值绑定。您可以将 `let` 视为 let it be 的缩写，这也是披头士关于编程的一首优美歌曲。下面是一个例子：

```
(let [x 3]
  x)
; => 3

(def dalmatian-list
  ["Pongo" "Perdita" "Puppy 1" "Puppy 2"])
(let [dalmatians (take 2 dalmatian-list)]
  dalmatians)
; => ("Pongo" "Perdita")
```

在第一个示例中，您将名称 `x` 与值 `3` 绑定。在第二个示例中，您将名称 `dalmatians` 绑定到表达式 ` (take 2 ``dalmatian ` `-list)` 的结果，即列表 `("Pongo" "Perdita")` 。 `let` 还引入了一个新的作用域：

```
(def x 0)
(let [x 1] x)
; => 1
```

在这里，您首先使用 `def` 将名称 `x` 与值 `0` 绑定。然后， `let` 创建一个新的作用域，将名称 `x` 与值 `1` 绑定。我认为作用域就是指事物的上下文。例如，在 "请清理这些烟头 "这句话中，根据您是在产科病房工作，还是在香烟制造商大会上担任保管员，烟头的含义是不同的。在这段代码中，您是在说："我希望 `x` 在全局上下文中是 `0` ，但在这个 `let` 表达式的上下文中，它应该是 `1` " 。

您可以在 `let` 绑定中引用现有绑定：

```
(def x 0)
(let [x (inc x)] x)
; => 1
```

在这个示例中， `(inc x)` 中的 `x` 指的是由 `(def x 0)` 创建的绑定。由此产生的值是 `1` ，然后将其绑定到由 `let` 创建的新作用域中的名称 `x` 上。在 `let` 窗体的范围内， `x` 指向 `1` 而不是 `0` 。

您也可以在 `let` 中使用其余参数，就像在函数中一样：

```
(let [[pongo & dalmatians] dalmatian-list]
  [pongo dalmatians])
; => ["Pongo" ("Perdita" "Puppy 1" "Puppy 2")]
```

请注意， `let` 表单的值是其主体中最后一个被求值的表单。 `let` 表单遵循第 48 页 "调用函数 "中介绍的所有重组规则。在本例中， `[pongo & dalmatians]` 对 `dalmatian-list` 进行了重构，将字符串 `"Pongo"` 与名称 `pongo` 绑定，并将其余的斑点狗列表与 `dalmatians` 绑定。向量 `[pongo dalmatians]` 是 `let` 中的最后一个表达式，因此它是 `let` 表单的值。

`let` 表单有两个主要用途。首先，它们允许你命名事物，从而提供了清晰度。其次，它们允许您只对表达式求值一次，并重复使用求值结果。当您需要重复使用昂贵函数调用（如网络 API 调用）的结果时，这一点尤为重要。当表达式有副作用时，这一点也很重要。

让我们再来看看对称函数中的 `let` 形式，以便了解究竟发生了什么：

```
(let [[part & remaining] remaining-asym-parts]
  (recur remaining
         (into final-body-parts
               (set [part (matching-part part)]))))
```

这段代码告诉 Clojure："创建一个新的作用域。在该作用域中，将 `part` 与 `remaining-asym-parts` 中的第一个元素关联。将 `remaining` 与 `remaining-asym-parts` 中的其余元素关联"。

至于 `let` 表达式的正文，您将在下一节了解 `recur` 的含义。函数调用

```
(into final-body-parts
  (set [part (matching-part part)]))
```

首先告诉 Clojure："使用 `set` 函数创建一个由 `part` 及其匹配部分组成的集合。然后使用函数 `into` 将该集合的元素添加到向量 `final-body-parts` 中。在这里创建一个集合是为了确保向 `final-body-parts` 添加的元素是唯一的，因为 `part` 和 `(matching-part part)` 有时是相同的，这将在即将介绍正则表达式的章节中为您介绍。下面是一个简化的示例：

```
(into [] (set [:a :a]))
; => [:a]
```

首先， `(set [:a :a])` 返回集合 `#{:a}` ，因为集合不包含重复元素。然后， `(into [] #{:a})` 返回向量 `[:a]` 。

回到 `let` ：请注意， `part` 在 `let` 的正文中被多次使用。如果我们使用原始表达式，而不是使用 `part` 和 `remaining` 这两个名称，就会一团糟！下面是一个例子：

```
(recur (rest remaining-asym-parts)
       (into final-body-parts
             (set [(first remaining-asym-parts) (matching-part (first remaining-asym-parts))])))
```

因此， `let` 是一种为值引入本地名称的便捷方法，有助于简化代码。

### 环

在 `symmetrize-body-parts `函数中，我们使用了 `loop` ，它为 Clojure 提供了另一种递归方法。让我们来看一个简单的例子：

```
(loop [iteration 0]
  (println (str "Iteration " iteration))
  (if (> iteration 3)
    (println "Goodbye!")
    (recur (inc iteration))))
; => Iteration 0
; => Iteration 1
; => Iteration 2
; => Iteration 3
; => Iteration 4
; => Goodbye!
```

第一行， `loop [iteration 0]` ，开始了循环，并引入了一个具有初始值的绑定。在第一次通过循环时， `iteration` 的值为 0。 接下来，它会打印一条短消息。然后，检查 `iteration` 的值。如果值大于 3，就该说再见了。否则，我们 `recur` 。这就好比 `loop` 创建了一个匿名函数，其参数名为 `iteration` ，而 `recur` 允许你在其内部调用该函数，并传递参数 `(inc iteration)` 。

事实上，只要使用一个普通的函数定义，就能达到同样的目的：

```
(defn recursive-printer
  ([]
     (recursive-printer 0))
  ([iteration]
     (println iteration)
     (if (> iteration 3)
       (println "Goodbye!")
       (recursive-printer (inc iteration)))))
(recursive-printer)
; => Iteration 0
; => Iteration 1
; => Iteration 2
; => Iteration 3
; => Iteration 4
; => Goodbye!
```

但正如你所看到的，这样做会比较啰嗦。此外， `loop` 的性能要好得多。在我们的对称功能中，我们将使用 `loop` 来遍历身体部位非对称列表中的每个元素。

### 正则表达式

正则表达式是对文本进行模式匹配的工具。正则表达式的文字符号是将表达式放在哈希标记后的引号中：

```
#"regular-expression"
```

在清单 3-1 中的函数 `matching-part` 中， `clojure.string/replace` 使用正则表达式 `#"^left-"` 匹配以 `"left-"` 开头的字符串，从而将 `"left-"` 替换为 `"right-"` 。卡符 `^` 是正则表达式发出的信号，即只有当文本 `"left-"` 位于字符串开头时，它才会与之匹配，从而确保类似 `"cleft-chin"` 这样的文本不会匹配。您可以使用 `re-find` 测试这一点，它会检查字符串是否与正则表达式描述的模式匹配，如果不匹配，则返回匹配的文本或 `nil` ：

```
(re-find #"^left-" "left-eye")
; => "left-"

(re-find #"^left-" "cleft-chin")
; => nil

(re-find #"^left-" "wongleblart")
; => nil
```

下面是 `matching-part` 使用 regex 将 `"left-"` 替换为 `"right-"` 的几个示例：

```
(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})
(matching-part {:name "left-eye" :size 1})
; => {:name "right-eye" :size 1}]

(matching-part {:name "head" :size 3})
; => {:name "head" :size 3}]
```

请注意，名称 `"head"` 是按原样返回的。

### 对称器

现在，让我们回到完全对称器，对其进行更详细的分析：

```
(def asym-hobbit-body-parts [{:name "head" :size 3}
                             {:name "left-eye" :size 1}
                             {:name "left-ear" :size 1}
                             {:name "mouth" :size 1}
                             {:name "nose" :size 1}
                             {:name "neck" :size 2}
                             {:name "left-shoulder" :size 3}
                             {:name "left-upper-arm" :size 3}
                             {:name "chest" :size 10}
                             {:name "back" :size 10}
                             {:name "left-forearm" :size 3}
                             {:name "abdomen" :size 6}
                             {:name "left-kidney" :size 1}
                             {:name "left-hand" :size 2}
                             {:name "left-knee" :size 2}
                             {:name "left-thigh" :size 4}
                             {:name "left-lower-leg" :size 3}
                             {:name "left-achilles" :size 1}
                             {:name "left-foot" :size 2}])


(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})

➊ (defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
➋   (loop [remaining-asym-parts asym-body-parts
         final-body-parts []]
➌     (if (empty? remaining-asym-parts)
      final-body-parts
➍       (let [[part & remaining] remaining-asym-parts]
➎         (recur remaining
               (into final-body-parts
                     (set [part (matching-part part)])))))))
```

`symmetrize-body-parts` 函数（从 ➊ 开始）采用了函数式编程中常见的一般策略。给定一个序列（在本例中是一个身体部位及其尺寸的向量），函数会不断地将序列分割成头部和尾部。然后处理头部，将其添加到某个结果中，并使用递归继续处理尾部。

我们从 ➋ 开始循环遍历主体部分。序列尾部将与 `remaining-asym-parts` 绑定。最初，它与传递给函数的完整序列绑定： `asym-body-parts` 。我们还创建了一个结果序列 `final-body-parts` ；其初始值为空向量。

如果 `remaining-asym-parts` 在 ➌ 处为空，说明我们已经处理了整个序列，可以返回结果 `final-body-parts` 。否则，我们会在 ➍ 处将列表分割为头部 `part` 和尾部 `remaining` 。

在 ➎ 处，我们使用 `remaining` 和 `(into)` 表达式进行递归，`remaining` 是一个在循环的每次迭代中都会缩短一个元素的列表，而 `(into)` 表达式则建立了对称身体部位的向量。

如果你是编程新手，可能需要花一些时间来理解这段代码。坚持下去！一旦你明白了发生了什么，你就会觉得自己像个百万富翁！

### 更好的对称器，减少

处理序列中的每个元素并生成结果的模式非常常见，以至于有一个内置函数叫做 `reduce` 。 下面是一个简单的例子：

```
;; sum with reduce
(reduce + [1 2 3 4])
; => 10
```

这就好比告诉 Clojure 这样做：

```
(+ (+ (+ 1 2) 3) 4)
```

`reduce` 功能按以下步骤运行：

1. 将给定函数应用于序列的前两个元素。这就是 `(+ 1 2)` 的由来。
2. 对结果和序列的下一个元素应用给定的函数。在本例中，步骤 1 的结果是 `3` ，序列的下一个元素也是 `3` 。因此，最终结果是 `(+ 3 3)` 。
3. 对序列中剩余的每个元素重复步骤 2。

`reduce` 也可以选择初始值。这里的初始值是 `15` ：

```
(reduce + 15 [1 2 3 4])
```

如果您提供了初始值， `reduce` 将首先对初始值和序列的第一个元素而不是序列的前两个元素应用给定的函数。

需要注意的一个细节是，在这些示例中， `reduce` 接收一个元素集合 `[1 2 3 4]` 并返回一个数字。虽然程序员经常这样使用 `reduce` ，但您也可以使用 `reduce` 返回一个比开始时更大的集合，就像我们试图使用 `symmetrize` `-body-parts` 所做的那样。 `reduce` 抽象了 "处理集合并构建结果 "的任务，它与返回结果的类型无关。为了进一步了解 `reduce` 的工作原理，下面是一种实现方法：

```
(defn my-reduce
  ([f initial coll]
   (loop [result initial
          remaining coll]
     (if (empty? remaining)
       result
       (recur (f result (first remaining)) (rest remaining)))))
  ([f [head & tail]]
   (my-reduce f head tail)))
```

我们可以按以下方式重新实现我们的对称器：

```
(defn better-symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (reduce (fn [final-body-parts part]
            (into final-body-parts (set [part (matching-part part)])))
          []
          asym-body-parts))
```

Groovy！使用 `reduce` 的一个显而易见的优势是，您编写的代码总体上更少。您传递给 `reduce` 的匿名函数只专注于处理元素和生成结果。这是因为 `reduce` 处理的是底层机制，即跟踪哪些元素已被处理，并决定是返回最终结果还是递归。

使用 `reduce` 也更具表现力。如果您的代码读者遇到 `loop` ，他们在不阅读全部代码的情况下，无法确定循环到底在做什么。但是，如果他们看到 `reduce` ，他们就会立即知道代码的目的是处理集合中的元素以创建结果。

最后，通过将 `reduce` 过程抽象为一个将另一个函数作为参数的函数，您的程序将变得更加可组合。例如，您可以将 `reduce` 函数作为参数传递给其他函数。您还可以创建一个更通用的 `symmetrize-body-parts` 版本，例如 `expand-body-parts` 。除了身体部位列表外，它还可以使用一个扩展函数，这将使您的模型不仅仅局限于哈比人。例如，您可以使用蜘蛛扩展器来增加眼睛和腿的数量。因为我很邪恶，所以还是让你来写吧。

### 霍比特人的暴力

天啊，这真是勇敢者的 Clojure！为了给你们的工作画上圆满的句号，这里有一个函数，它能确定霍比特人的哪个部位被击中：

```
(defn hit
  [asym-body-parts]
  (let [sym-parts (➊better-symmetrize-body-parts asym-body-parts)
        ➋body-part-size-sum (reduce + (map :size sym-parts))
        target (rand body-part-size-sum)]
    ➌(loop [[part & remaining] sym-parts
           accumulated-size (:size part)]
      (if (> accumulated-size target)
        part
        (recur remaining (+ accumulated-size (:size (first remaining))))))))
```

`hit` 的工作原理是获取一个不对称身体部位的向量，在 ➊ 处将其对称，然后在 ➋ 处将各部位的大小相加。一旦我们将这些大小相加，就好像从 1 到 `body-part-size-sum` 的每个数字都对应着一个身体部位；1 可能对应着左眼，2、3、4 可能对应着头部。这样，当你击中身体部位时（在这个范围内随机选择一个数字），特定身体部位被击中的可能性将取决于身体部位的大小。

![img](https://www.braveclojure.com/assets/images/cftbat/do-things/hobbit-hit-line.png)

图 3-1：身体部位与数字范围相对应，如果目标在该范围内，就会被击中。

最后，我们从这些数字中随机选择一个，然后在 ➌ 处使用 `loop` 查找并返回与数字相对应的身体部位。这个循环通过跟踪我们已经检查过的部件的累积大小，并检查累积大小是否大于目标值来实现这一目的。我把这个过程想象成用一排编号槽将身体部位排成一行。排好身体部位后，我会问自己："我达到目标了吗？如果达到了，就说明刚刚排好的身体部位就是击中的目标。否则，我就继续排列这些部位。

例如，您的部件列表是头部、左眼和左手，如图 3-1 所示。当累积大小超过目标值时，身体部分就会被击中，因此如果目标值小于 3，那么头部就被击中了。同样，如果目标大于或等于 4 且小于 6，左手也会被击中。

下面是 `hit` 函数的一些运行示例：

```
(hit asym-hobbit-body-parts)
; => {:name "right-upper-arm", :size 3}

(hit asym-hobbit-body-parts)
; => {:name "chest", :size 10}

(hit asym-hobbit-body-parts)
; => {:name "left-eye", :size 1}
```

天啊，那个可怜的哈比人你这个怪物
