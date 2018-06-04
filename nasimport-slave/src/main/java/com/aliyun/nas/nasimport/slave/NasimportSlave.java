package com.aliyun.nas.nasimport.slave;

import com.aliyun.nas.nasimport.api.NasimportMasterService;
import com.aliyun.nas.nasimport.api.NasimportSlaveService;
import com.aliyun.nas.nasimport.api.NasimportTask;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by junqiao.wjq on 2018/6/3.
 */
public class NasimportSlave implements NasimportSlaveService, TaskRunner.Callback {
	NasimportMasterService nasimportMasterService;
	TaskRunner taskRunner = null;

	Lock lock = new ReentrantLock();

	@Override
	public boolean startTask(NasimportTask task) {
		if (lock.tryLock()) {
			try {
				if (taskRunner == null) {
					taskRunner = new TaskRunner(task, this);
					taskRunner.start();
					return true;
				}
			} finally {
				lock.unlock();
			}
		}
		return false;
	}

	@Override
	public boolean isServiceAlive() {
		return true;
	}

	@Override
	public void updateProgress(int taskId, int progress) {
		nasimportMasterService.updateTaskProgress(taskId, progress);
	}

	@Override
	public void finished() {
		this.taskRunner = null;
	}
}
