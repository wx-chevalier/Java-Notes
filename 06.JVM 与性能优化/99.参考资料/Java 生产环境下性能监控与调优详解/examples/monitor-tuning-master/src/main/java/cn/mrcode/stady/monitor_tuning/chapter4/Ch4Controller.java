package cn.mrcode.stady.monitor_tuning.chapter4;

import cn.mrcode.stady.monitor_tuning.chapter2.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ch4")
public class Ch4Controller {
    /**
     * Btrace 获取参数
     *
     * @param name
     * @return
     */
    @RequestMapping("arg1")
    public String arg1(@RequestParam("name") String name) {
        return "hello," + name;
    }

    /**
     * Btrace 获取复杂参数
     *
     * @param user
     * @return
     */
    @RequestMapping("/arg2")
    public User grg2(User user) {
        return user;
    }

    /**
     * Btrace 构造函数拦截
     *
     * @param user
     * @return
     */
    @RequestMapping("/constructor")
    public User constructor(User user) {
        return user;
    }

    /**
     * btrace 拦截重载方法
     *
     * @param id
     * @return
     */
    @RequestMapping("/same1")
    public String same(Integer id) {
        return id + "";
    }

    @RequestMapping("/same2")
    public String same(Integer id, String name) {
        return id + "," + name;
    }


    /**
     * btrace 获取异常
     *
     * @return
     */
    @RequestMapping("/exception")
    public String exception() {
        try {
            System.out.println("start");
            System.out.println(1 / 0);
            System.out.println("end");
        } catch (Exception e) {
            // 模拟在一个多层调用中，把异常吞掉了
        }
        return "success";
    }
}
