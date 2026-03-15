package cn.mrcode.stady.monitor_tuning.chapter4;

import org.openjdk.btrace.core.BTraceUtils;
import org.openjdk.btrace.core.annotations.*;

@BTrace
public class PrintLine {

    @OnMethod(
            clazz = "cn.mrcode.stady.monitor_tuning.chapter4.Ch4Controller",
            method = "exception",
            location = @Location(value = Kind.LINE, line = -1)
    )
    public static void anyRead(@ProbeClassName String pcn, @ProbeMethodName String pmn, int line) {
        BTraceUtils.println(pcn + "," + pmn + "," + line);
        BTraceUtils.println();
    }
}
