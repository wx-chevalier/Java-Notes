package cn.mrcode.stady.monitor_tuning.chapter4;

import org.openjdk.btrace.core.BTraceUtils;
import org.openjdk.btrace.core.annotations.*;
import org.openjdk.btrace.core.types.AnyType;

/**
 * Btrace 获取返回值
 */
@BTrace
public class PrintReturn {
    /**
     * OnMethod 表示设置拦截哪个类 的 哪个方法，location 是具体的位置或则点
     *
     * @param pcn    探测到的类名
     * @param pmn    探测到的方法名称
     * @param result 返回值
     */
    @OnMethod(
            clazz = "cn.mrcode.stady.monitor_tuning.chapter4.Ch4Controller",
            method = "arg1",
            location = @Location(Kind.RETURN)
    )
    public static void anyRead(@ProbeClassName String pcn,
                               @ProbeMethodName String pmn,
                               @Return AnyType result) { // 这里使用 @Return 定义获取返回值
        BTraceUtils.println(pcn + "," + pmn);
        BTraceUtils.println(result);
        BTraceUtils.println();
    }
}
