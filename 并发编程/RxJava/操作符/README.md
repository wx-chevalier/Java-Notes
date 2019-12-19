# RxJava 常用操作符

ReactiveX 的每种编程语言的实现都实现了一组操作符的集合。不同的实现之间有很多重叠的部分，也有一些操作符只存在特定的实现中。每种实现都倾向于用那种编程语言中他们熟悉的上下文中相似的方法给这些操作符命名。

本文首先会给出 ReactiveX 的核心操作符列表和对应的文档链接，后面还有一个决策树用于帮助你根据具体的场景选择合适的操作符。最后有一个语言特定实现的按字母排序的操作符列表。

如果你想实现你自己的操作符，可以参考这里：实现自定义操作符`](https://mcxiaoke.gitbooks.io/rxdocs/content/topics/Implementing-Your-Own-Operators.html)

## 创建操作

用于创建 Observable 的操作符

- Create: 通过调用观察者的方法从头创建一个 Observable
- Defer: 在观察者订阅之前不创建这个 Observable，为每一个观察者创建一个新的 Observable
- Empty/Never/Throw: 创建行为受限的特殊 Observable
- From: 将其它的对象或数据结构转换为 Observable
- Interval: 创建一个定时发射整数序列的 Observable
- Just: 将对象或者对象集合转换为一个会发射这些对象的 Observable
- Range: 创建发射指定范围的整数序列的 Observable
- Repeat: 创建重复发射特定的数据或数据序列的 Observable
- Start: 创建发射一个函数的返回值的 Observable
- Timer: 创建在一个指定的延迟之后发射单个数据的 Observable

## 变换操作

这些操作符可用于对 Observable 发射的数据进行变换，详细解释可以看每个操作符的文档

- Buffer: 缓存，可以简单的理解为缓存，它定期从 Observable 收集数据到一个集合，然后把这些数据集合打包发射，而不是一次发射一个
- FlatMap: 扁平映射，将 Observable 发射的数据变换为 Observables 集合，然后将这些 Observable 发射的数据平坦化的放进一个单独的 Observable，可以认为是一个将嵌套的数据结构展开的过程。
- GroupBy: 分组，将原来的 Observable 分拆为 Observable 集合，将原始 Observable 发射的数据按 Key 分组，每一个 Observable 发射一组不同的数据
- Map: 映射，通过对序列的每一项都应用一个函数变换 Observable 发射的数据，实质是对序列中的每一项执行一个函数，函数的参数就是这个数据项
- Scan: 扫描，对 Observable 发射的每一项数据应用一个函数，然后按顺序依次发射这些值
- Window: 窗口，定期将来自 Observable 的数据分拆成一些 Observable 窗口，然后发射这些窗口，而不是每次发射一项。类似于 Buffer，但 Buffer 发射的是数据，Window 发射的是 Observable，每一个 Observable 发射原始 Observable 的数据的一个子集

## 过滤操作

这些操作符用于从 Observable 发射的数据中进行选择

- Debounce: 只有在空闲了一段时间后才发射数据，通俗的说，就是如果一段时间没有操作，就执行一次操作
- Distinct: 去重，过滤掉重复数据项
- ElementAt: 取值，取特定位置的数据项
- Filter: 过滤，过滤掉没有通过谓词测试的数据项，只发射通过测试的
- First: 首项，只发射满足条件的第一条数据
- IgnoreElements: 忽略所有的数据，只保留终止通知(onError 或 onCompleted)
- Last: 末项，只发射最后一条数据
- Sample: 取样，定期发射最新的数据，等于是数据抽样，有的实现里叫 ThrottleFirst
- Skip: 跳过前面的若干项数据
- SkipLast: 跳过后面的若干项数据
- Take: 只保留前面的若干项数据
- TakeLast: 只保留后面的若干项数据

## 组合操作

组合操作符用于将多个 Observable 组合成一个单一的 Observable

- And/Then/When: 通过模式(And 条件)和计划(Then 次序)组合两个或多个 Observable 发射的数据集
- CombineLatest: 当两个 Observables 中的任何一个发射了一个数据时，通过一个指定的函数组合每个 Observable 发射的最新数据（一共两个数据），然后发射这个函数的结果
- Join: 无论何时，如果一个 Observable 发射了一个数据项，只要在另一个 Observable 发射的数据项定义的时间窗口内，就将两个 Observable 发射的数据合并发射
- Merge: 将两个 Observable 发射的数据组合并成一个
- StartWith: 在发射原来的 Observable 的数据序列之前，先发射一个指定的数据序列或数据项
- Switch: 将一个发射 Observable 序列的 Observable 转换为这样一个 Observable：它逐个发射那些 Observable 最近发射的数据
- Zip: 打包，使用一个指定的函数将多个 Observable 发射的数据组合在一起，然后将这个函数的结果作为单项数据发射

## 错误处理

这些操作符用于从错误通知中恢复

- Catch: 捕获，继续序列操作，将错误替换为正常的数据，从 onError 通知中恢复
- Retry: 重试，如果 Observable 发射了一个错误通知，重新订阅它，期待它正常终止

## 辅助操作

一组用于处理 Observable 的操作符

- Delay: 延迟一段时间发射结果数据
- Do: 注册一个动作占用一些 Observable 的生命周期事件，相当于 Mock 某个操作
- Materialize/Dematerialize: 将发射的数据和通知都当做数据发射，或者反过来
- ObserveOn: 指定观察者观察 Observable 的调度程序（工作线程）
- Serialize: 强制 Observable 按次序发射数据并且功能是有效的
- Subscribe: 收到 Observable 发射的数据和通知后执行的操作
- SubscribeOn: 指定 Observable 应该在哪个调度程序上执行
- TimeInterval: 将一个 Observable 转换为发射两个数据之间所耗费时间的 Observable
- Timeout: 添加超时机制，如果过了指定的一段时间没有发射数据，就发射一个错误通知
- Timestamp: 给 Observable 发射的每个数据项添加一个时间戳
- Using: 创建一个只在 Observable 的生命周期内存在的一次性资源

## 条件和布尔操作

这些操作符可用于单个或多个数据项，也可用于 Observable

- All: 判断 Observable 发射的所有的数据项是否都满足某个条件
- Amb: 给定多个 Observable，只让第一个发射数据的 Observable 发射全部数据
- Contains: 判断 Observable 是否会发射一个指定的数据项
- DefaultIfEmpty: 发射来自原始 Observable 的数据，如果原始 Observable 没有发射数据，就发射一个默认数据
- SequenceEqual: 判断两个 Observable 是否按相同的数据序列
- SkipUntil: 丢弃原始 Observable 发射的数据，直到第二个 Observable 发射了一个数据，然后发射原始 Observable 的剩余数据
- SkipWhile: 丢弃原始 Observable 发射的数据，直到一个特定的条件为假，然后发射原始 Observable 剩余的数据
- TakeUntil: 发射来自原始 Observable 的数据，直到第二个 Observable 发射了一个数据或一个通知
- TakeWhile: 发射原始 Observable 的数据，直到一个特定的条件为真，然后跳过剩余的数据

## 算术和聚合操作

这些操作符可用于整个数据序列

- Average: 计算 Observable 发射的数据序列的平均值，然后发射这个结果
- Concat: 不交错的连接多个 Observable 的数据
- Count: 计算 Observable 发射的数据个数，然后发射这个结果
- Max: 计算并发射数据序列的最大值
- Min: 计算并发射数据序列的最小值
- Reduce: 按顺序对数据序列的每一个应用某个函数，然后返回这个值
- Sum: 计算并发射数据序列的和

## 连接操作

一些有精确可控的订阅行为的特殊 Observable

- Connect: 指示一个可连接的 Observable 开始发射数据给订阅者
- Publish: 将一个普通的 Observable 转换为可连接的
- RefCount: 使一个可连接的 Observable 表现得像一个普通的 Observable
- Replay: 确保所有的观察者收到同样的数据序列，即使他们在 Observable 开始发射数据之后才订阅

## 转换操作

- To: 将 Observable 转换为其它的对象或数据结构
- Blocking: 阻塞 Observable 的操作符

## 操作符决策树

几种主要的需求

- 直接创建一个 Observable（创建操作）
- 组合多个 Observable（组合操作）
- 对 Observable 发射的数据执行变换操作（变换操作）
- 从 Observable 发射的数据中取特定的值（过滤操作）
- 转发 Observable 的部分值（条件/布尔/过滤操作）
- 对 Observable 发射的数据序列求值（算术/聚合操作）

Rx 最大的特征之一就是无法预测何时会有数据发射。有些 Observable 会同步的即可发射所有的数据，比如 range ，有些按照一定的时间间隔发射数据、有些根本无法确定到底何时发射数据。例如，鼠标移动时事件和 UDP 数据包到达的时刻。我们需要合适的工具来处理这些无法确定何时发射的事件。

# 链接

- https://mcxiaoke.gitbooks.io/rxdocs/content/Operators.html

- [驯服数据流之 时间平移](http://www.tuicool.com/articles/ABfEfq3)
