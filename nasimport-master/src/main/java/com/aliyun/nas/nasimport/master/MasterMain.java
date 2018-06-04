package com.aliyun.nas.nasimport.master;

import com.aliyun.nas.nasimport.api.NasimportSlaveService;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by junqiao.wjq on 2018/6/3.
 */
public class MasterMain {
	public static void main(String[] args) {
		//测试常规服务
		ClassPathXmlApplicationContext masterContext =
				new ClassPathXmlApplicationContext("master-provider.xml");
		masterContext.start();
		System.out.println("Nasimport Master start");
		NasimportMaster master = masterContext.getBean(NasimportMaster.class);
		master.start();

		while (true) {
			try {
				ClassPathXmlApplicationContext slaveContext =
						new ClassPathXmlApplicationContext("fetch-slave.xml");
				slaveContext.start();
				NasimportSlaveService slaveService = slaveContext.getBean(NasimportSlaveService.class);
				master.nasimportSlaveService = slaveService;

				while (true) {
					try {
						slaveService.isServiceAlive();
					} catch (Exception e) {
						break;
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (BeansException e) {
				System.out.println("NasimportSlaveService is not ready yet, waiting...");
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
