package cn.mrcode.stady.monitor_tuning.chapter2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class MemoryController {
    private List<User> users = new ArrayList<>();

    /**
     * 堆内存溢出
     * <pre>
     *   为了更快的看到效果，限制最大和最小内存：
     *   -Xmx32M -Xms32M
     *   记得需要在启动的时候添加启动参数
     * </pre>
     */
    @GetMapping("/heap")
    public void heap() {
        int i = 0;
        while (true) {
            users.add(new User(i++, UUID.randomUUID().toString()));
        }
    }

    private List<Class> classList = new ArrayList<>();

    /**
     * 非堆内存溢出
     * <pre>
     *   为了更快的看到效果，限制非堆最大和最小内存：
     *   -XX:MetaspaceSize=32M -XX:MaxMetaspaceSize=32M
     *   记得需要在启动的时候添加启动参数
     * </pre>
     */
    @GetMapping("/nonheap")
    public void nonheap() {
        while (true) {
            // 持有创建好的类是为了防止被垃圾回收器回收掉
            classList.addAll(Metaspace.createClasses());
        }
    }
}
