package com.aliyun.nas.nasimport.slave;

import com.aliyun.nas.nasimport.api.NasimportTask;

/**
 * Created by junqiao.wjq on 2018/6/3.
 */
public class TaskRunner extends Thread {
	NasimportTask task;
	Callback callback;
	volatile int progress;

	public TaskRunner(NasimportTask task, Callback callback) {
		this.callback = callback;
		this.task = task;
		this.progress = 0;
	}

	@Override
	public void run() {
		System.out.println("task " + task.getTaskId() + " start to run");
		for (int i = 0; i < 5; i ++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			progress = i * 2;
			this.callback.updateProgress(this.task.getTaskId(), progress);
		}
		this.callback.updateProgress(this.task.getTaskId(), 100);
		this.callback.finished();
	}

	interface Callback {
		void updateProgress(int taskId, int progress);
		void finished();
	}
}
