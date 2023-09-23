package cn.mrcode.stady.monitor_tuning.chapter4;

import org.openjdk.btrace.core.BTraceUtils;
import org.openjdk.btrace.core.annotations.*;

/**
 * Btrace 获取异常; 其实使用的就是拦截的是 异常类 的构造函数
 */
@BTrace
public class PrintOnThrow {
    @TLS   // 表示使用 thred local 变量，多线程状态下，该值才是每个线程自己的值
    static Throwable currentException;

    /**
     * OnMethod 表示设置拦截哪个类 的 哪个方法，location 是具体的位置或则点
     */
    @OnMethod(
            clazz = "java.lang.Throwable",
            method = "<init>"
    )
    public static void onthrow(@Self Throwable self) {
        // 相当于拦截：new Throwable()
        currentException = self;
    }

    @OnMethod(
            clazz = "java.lang.Throwable",
            method = "<init>"
    )
    public static void onthrow1(@Self Throwable self, String s) {
        // 相当于拦截：new Throwable(String msg)
        currentException = self;
    }

    @OnMethod(
            clazz = "java.lang.Throwable",
            method = "<init>"
    )
    public static void onthrow2(@Self Throwable self, String s, Throwable cause) {
        // 相当于拦截：new Throwable(String msg,Throwable cause)
        currentException = self;
    }

    @OnMethod(
            clazz = "java.lang.Throwable",
            method = "<init>"
    )
    public static void onthrow3(@Self Throwable self, Throwable cause) {
        // 相当于拦截：new Throwable(Throwable cause)
        currentException = self;
    }

    /**
     * 当 Throwable 构造函数返回的时候，则打印传入构造函数的异常
     */
    @OnMethod(
            clazz = "java.lang.Throwable",
            method = "<init>",
            location = @Location(Kind.RETURN)
    )
    public static void onthrowreturn() {
        if (currentException != null) {
            // 打印异常堆栈
            BTraceUtils.Threads.jstack(currentException);
            BTraceUtils.println("======================");
            currentException = null;
        }
    }
}
