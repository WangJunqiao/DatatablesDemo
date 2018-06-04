package com.aliyun.nas.nasimport.slave;

import com.aliyun.nas.nasimport.api.NasimportMasterService;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SlaveMain {
    public static void main(String[] args) {
        //测试常规服务
        ClassPathXmlApplicationContext slaveContext =
                new ClassPathXmlApplicationContext("slave-provider.xml");
        slaveContext.start();
        NasimportSlave slaveService = slaveContext.getBean(NasimportSlave.class);

        while (true) { // find new Master
            try {
                ClassPathXmlApplicationContext masterContext =
                        new ClassPathXmlApplicationContext("fetch-master.xml");
                masterContext.start();
                NasimportMasterService masterService = masterContext.getBean(NasimportMasterService.class);
                slaveService.nasimportMasterService = masterService;
                while (true) {
                    try {
                        masterService.getMasterStatus();
                    } catch (Exception e) {
                        // master failed
                        break;
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (BeansException e) {
                System.out.println("NasimportMasterService not start yet, waiting...");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
