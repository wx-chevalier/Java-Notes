package cn.mrcode.stady.monitor_tuning.chapter4;

import org.openjdk.btrace.core.BTraceUtils;
import org.openjdk.btrace.core.annotations.BTrace;
import org.openjdk.btrace.core.annotations.OnMethod;
import org.openjdk.btrace.core.annotations.ProbeClassName;
import org.openjdk.btrace.core.annotations.ProbeMethodName;
import org.openjdk.btrace.core.types.AnyType;

/**
 * Btrace 拦截重载方法
 */
@BTrace
public class PrintSame {
    /**
     * OnMethod 表示设置拦截哪个类 的 哪个方法
     *
     * @param pcn 探测到的类名
     * @param pmn 探测到的方法名称
     */
    @OnMethod(
            clazz = "cn.mrcode.stady.monitor_tuning.chapter4.Ch4Controller",
            method = "same"
    )
    public static void anyRead(@ProbeClassName String pcn,
                               @ProbeMethodName String pmn,
                               Integer id, String name  // 根据参数的个数拦截重载中的哪一个方法
    ) {
        BTraceUtils.println("拦截有 2 个入参的 same 方法");
        BTraceUtils.println(pcn + "," + pmn);
        BTraceUtils.println(id + "," + name);
        BTraceUtils.println();
    }

    @OnMethod(
            clazz = "cn.mrcode.stady.monitor_tuning.chapter4.Ch4Controller",
            method = "same"
    )
    public static void anyRead2(@ProbeClassName String pcn,
                                @ProbeMethodName String pmn,
                                Integer id
    ) {
        BTraceUtils.println("拦截只有一个入参的 same 方法");
        BTraceUtils.println(pcn + "," + pmn);
        BTraceUtils.println(id);
        BTraceUtils.println();
    }
}
