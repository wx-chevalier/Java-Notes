> [原文地址](https://devinkin.github.io/post/clojureprogramming/chapter1/)

# Clojure 简介

# 关键字

关键字使用一个冒号标识。如果关键字里面包含 / ，表示这个关键字是命名空间限定的。如果一个关键字以两个冒号 :: 开头，表示是当前命名空间的关键字。

```clj
(def pizza {:name "Ramunto's"
            :location "Claremont, NH"
            ::location "43.3734, -72.3365"})
```
