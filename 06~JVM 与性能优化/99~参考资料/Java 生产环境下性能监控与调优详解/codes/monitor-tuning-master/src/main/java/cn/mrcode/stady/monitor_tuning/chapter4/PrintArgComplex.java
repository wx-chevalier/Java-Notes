package cn.mrcode.stady.monitor_tuning.chapter4;
import cn.mrcode.stady.monitor_tuning.chapter2.User;
import org.openjdk.btrace.core.BTraceUtils;
import org.openjdk.btrace.core.annotations.*;

import java.lang.reflect.Field;


@BTrace
public class PrintArgComplex {
	
	
	@OnMethod(
	        clazz="cn.mrcode.stady.monitor_tuning.chapter4.Ch4Controller",
	        method="grg2",
	        location=@Location(Kind.ENTRY)
	)
	public static void anyRead(@ProbeClassName String pcn, @ProbeMethodName String pmn, User user) {
		//print all fields
		BTraceUtils.printFields(user);
		// 第三方包需要获取需要使用类全限定名
		//print one field
		Field filed2 = BTraceUtils.field("cn.mrcode.stady.monitor_tuning.chapter2.User", "name");
		BTraceUtils.println(BTraceUtils.get(filed2, user));
		BTraceUtils.println(pcn+","+pmn);
		BTraceUtils.println();
    }
}
