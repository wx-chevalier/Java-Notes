# 常见内置工具的使用

```sh
# 查看堆内对象的分布 Top 50（定位内存泄漏）
$ jmap –histo:live $pid | sort-n -r -k2 | head-n 50

# 按线程状态统计线程数(加强版)
$ jstack $pid | grep java.lang.Thread.State:|sort|uniq -c | awk '{sum+=$1; split($0,a,":");gsub(/^[ \t]+|[ \t]+$/, "", a[2]);printf "%s: %s\n", a[2], $1}; END {printf "TOTAL: %s",sum}';

# 查看最消耗 CPU 的 Top10 线程机器堆栈信息
$ show-busy-java-threads
```

# 链接

- https://mp.weixin.qq.com/s/N1rJkE3HA8mJYdqK4tSMEw