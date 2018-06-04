package com.alibaba.dubbo.consumer;

import com.alibaba.dubbo.demo.DemoService;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Consumer {
    public static void main(String[] args) {
        //测试常规服务
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("consumer.xml");
        context.start();
        System.out.println("consumer start");
        DemoService demoService3 = context.getBean("demoService", DemoService.class);
        for (int  i = 0; i < 10; i ++) {
            System.out.println(demoService3.whoAmI());
        }

        for (int i = 0; i < 1000; i ++) {
            try {
                DemoService demoService = context.getBean("demoService", DemoService.class);

            } catch (BeanCreationException e) {
                System.out.println("Retry in 2s");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        DemoService demoService2 = context.getBean("demoService2", DemoService.class);

        System.out.println("Start to call RPC...");
        System.out.println(demoService2.getPermissions(9999L));
        System.out.println("Call RPC finished");
    }
}
