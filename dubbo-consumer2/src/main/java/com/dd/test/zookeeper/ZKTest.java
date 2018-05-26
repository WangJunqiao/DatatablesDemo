package com.dd.test.zookeeper;

import com.alibaba.dubbo.remoting.zookeeper.ZookeeperClient;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * Created by junqiao.wjq on 2018/5/16.
 */
public class ZKTest {
	public static void main(String[] args) throws IOException {
		ZooKeeper zk = new ZooKeeper("localhost:3000",
				2, new Watcher() {
			// 监控所有被触发的事件
			public void process(WatchedEvent event) {
				System.out.println("已经触发了" + event.getType() + "事件！");
			}
		});

	}
}
