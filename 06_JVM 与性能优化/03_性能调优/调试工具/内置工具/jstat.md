# jstat

如下所示为 jstat 的命令格式：

```sh
$ jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]
```

如下表示分析进程 id 为 31736 的 gc 情况，每隔 1000ms 打印一次记录，打印 10 次停止，每 3 行后打印指标头部：

```sh
$ jstat -gc -h3 31736 1000 10
```

# jstat -gc

```sh
$ jstat -gc xxxx
```

其对应的指标含义如下：

| 参数 | 描述                                                   |
| ---- | ------------------------------------------------------ |
| S0C  | 年轻代中第一个 survivor（幸存区）的容量 (字节)         |
| S1C  | 年轻代中第二个 survivor（幸存区）的容量 (字节)         |
| S0U  | 年轻代中第一个 survivor（幸存区）目前已使用空间 (字节) |
| S1U  | 年轻代中第二个 survivor（幸存区）目前已使用空间 (字节) |
| EC   | 年轻代中 Eden（伊甸园）的容量 (字节)                   |
| EU   | 年轻代中 Eden（伊甸园）目前已使用空间 (字节)           |
| OC   | Old 代的容量 (字节)                                    |
| OU   | Old 代目前已使用空间 (字节)                            |
| PC   | Perm(持久代)的容量 (字节)                              |
| PU   | Perm(持久代)目前已使用空间 (字节)                      |
| YGC  | 从应用程序启动到采样时年轻代中 gc 次数                 |
| YGCT | 从应用程序启动到采样时年轻代中 gc 所用时间(s)          |
| FGC  | 从应用程序启动到采样时 old 代(全 gc)gc 次数            |
| FGCT | 从应用程序启动到采样时 old 代(全 gc)gc 所用时间(s)     |
| GCT  | 从应用程序启动到采样时 gc 用的总时间(s)                |

# jstat -gcutil

查看 gc 的统计信息

```sh
$ jstat -gcutil xxxx
```

其对应的指标含义如下：

| 参数 | 描述                                                      |
| ---- | --------------------------------------------------------- |
| S0   | 年轻代中第一个 survivor（幸存区）已使用的占当前容量百分比 |
| S1   | 年轻代中第二个 survivor（幸存区）已使用的占当前容量百分比 |
| E    | 年轻代中 Eden（伊甸园）已使用的占当前容量百分比           |
| O    | old 代已使用的占当前容量百分比                            |
| P    | perm 代已使用的占当前容量百分比                           |
| YGC  | 从应用程序启动到采样时年轻代中 gc 次数                    |
| YGCT | 从应用程序启动到采样时年轻代中 gc 所用时间(s)             |
| FGC  | 从应用程序启动到采样时 old 代(全 gc)gc 次数               |
| FGCT | 从应用程序启动到采样时 old 代(全 gc)gc 所用时间(s)        |
| GCT  | 从应用程序启动到采样时 gc 用的总时间(s)                   |

# jstat -gccapacity

```sh
$ jstat -gccapacity xxxx1
```

其对应的指标含义如下：

| 参数  | 描述                                           |
| ----- | ---------------------------------------------- |
| NGCMN | 年轻代(young)中初始化(最小)的大小 (字节)       |
| NGCMX | 年轻代(young)的最大容量 (字节)                 |
| NGC   | 年轻代(young)中当前的容量 (字节)               |
| S0C   | 年轻代中第一个 survivor（幸存区）的容量 (字节) |
| S1C   | 年轻代中第二个 survivor（幸存区）的容量 (字节) |
| EC    | 年轻代中 Eden（伊甸园）的容量 (字节)           |
| OGCMN | old 代中初始化(最小)的大小 (字节)              |
| OGCMX | old 代的最大容量 (字节)                        |
| OGC   | old 代当前新生成的容量 (字节)                  |
| OC    | Old 代的容量 (字节)                            |
| PGCMN | perm 代中初始化(最小)的大小 (字节)             |
| PGCMX | perm 代的最大容量 (字节)                       |
| PGC   | perm 代当前新生成的容量 (字节)                 |
| PC    | Perm(持久代)的容量 (字节)                      |
| YGC   | 从应用程序启动到采样时年轻代中 gc 次数         |
| FGC   | 从应用程序启动到采样时 old 代(全 gc)gc 次数    |

# 其他命令

1. 查看年轻代对象的信息及其占用量。

```
jstat -gcnewcapacity xxxx1
```

2. 查看老年代对象的信息及其占用量。

```
jstat -gcoldcapacity xxxx1
```

3. 查看年轻代对象的信息

```
jstat -gcnew xxxx1
```

4. 查看老年代对象的信息

```
jstat -gcold xxxx
```

# Links

- https://club.perfma.com/article/316783?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io
