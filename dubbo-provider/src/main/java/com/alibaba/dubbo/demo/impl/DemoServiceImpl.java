package com.alibaba.dubbo.demo.impl;

import com.alibaba.dubbo.demo.DemoService;

import java.util.ArrayList;
import java.util.List;

public class DemoServiceImpl implements DemoService {
    private int sid;

    public List<String> getPermissions(Long id) {
        System.out.println("Somebody is calling this RPC");
        List<String> demo = new ArrayList<String>();
        for (int i = 0; i < 20; i ++) {
            demo.add(String.format("Permission_%d", id + i));
            System.out.println("Put " + i + "th item into list, will sleep for 2s");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Method finished, RPC call will return");
        return demo;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String whoAmI() {
        return "Service " + sid;
    }
}
