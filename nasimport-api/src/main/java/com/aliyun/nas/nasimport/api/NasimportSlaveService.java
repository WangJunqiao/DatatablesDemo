package com.aliyun.nas.nasimport.api;

/**
 * Created by junqiao.wjq on 2018/6/3.
 */
public interface NasimportSlaveService {
	boolean startTask(NasimportTask task);
	boolean isServiceAlive();
}
