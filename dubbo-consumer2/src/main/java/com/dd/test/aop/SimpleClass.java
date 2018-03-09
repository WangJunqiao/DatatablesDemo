package com.dd.test.aop;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by junqiao.wjq on 2018/3/9.
 */
public class SimpleClass {

	public String foo() {
		System.out.println("this is foo method.......");
		return "hello";
	}

	public static void main(String[] args) {
		//ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("simple.xml");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("simple-aop.xml");
		SimpleClass ins = (SimpleClass) context.getBean("simpleIns");
		ins.foo();
	}
}
