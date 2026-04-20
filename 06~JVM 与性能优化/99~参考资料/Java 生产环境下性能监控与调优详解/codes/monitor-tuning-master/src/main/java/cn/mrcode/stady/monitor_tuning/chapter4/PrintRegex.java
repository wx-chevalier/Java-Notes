package cn.mrcode.stady.monitor_tuning.chapter4;


import org.openjdk.btrace.core.BTraceUtils;
import org.openjdk.btrace.core.annotations.BTrace;
import org.openjdk.btrace.core.annotations.OnMethod;
import org.openjdk.btrace.core.annotations.ProbeClassName;
import org.openjdk.btrace.core.annotations.ProbeMethodName;

/**
 * btrace：拦截该类中的所有方法
 */
@BTrace
public class PrintRegex {

    @OnMethod(
            clazz = "cn.mrcode.stady.monitor_tuning.chapter4.Ch4Controller",
            method = "/.*/"  // 正则语法 /正则表达式/，.*：任意字符，零次或多次
    )
    public static void anyRead(@ProbeClassName String pcn, @ProbeMethodName String pmn) {
        BTraceUtils.println(pcn + "," + pmn);
        BTraceUtils.println();
    }
}
