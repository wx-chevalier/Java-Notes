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

```

# 函数

# 一个完整的例子
