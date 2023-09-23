package cn.mrcode.stady.monitor_tuning.chapter4;

import org.openjdk.btrace.core.BTraceUtils;
import org.openjdk.btrace.core.annotations.*;
import org.openjdk.btrace.core.types.AnyType;

/**
 * Btrace 拦截构造函数
 */
@BTrace
public class PrintConstructor {
    /**
     * OnMethod 表示设置拦截哪个类 的 哪个方法，location 是具体的位置或则点
     *
     * @param pcn  探测到的类名
     * @param pmn  探测到的方法名称
     * @param args 入参
     */
    @OnMethod(
            clazz = "cn.mrcode.stady.monitor_tuning.chapter2.User", // 注意这里拦截的是 User 类
            method = "<init>" // 设置为构造方法
    )
    public static void anyRead(@ProbeClassName String pcn,
                               @ProbeMethodName String pmn,
                               AnyType[] args) {
        BTraceUtils.println(pcn + "," + pmn);
        BTraceUtils.printArray(args);
        BTraceUtils.println();
    }
}
