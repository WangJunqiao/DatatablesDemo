package com.aliyun.nas.nasimport.master;

import com.aliyun.nas.nasimport.api.NasimportTask;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by junqiao.wjq on 2018/6/3.
 */
public class RunningTaskPool {
	static final int FAIL_DURATION = 10000; // 10seconds not update status, treat as failure
	ConcurrentHashMap<Integer, NasimportTask> taskRunning;
	ConcurrentHashMap<Integer, Long> lastUpdateTime;
	FinishedCleaner cleaner;

	public RunningTaskPool() {
		taskRunning = new ConcurrentHashMap<>();
		lastUpdateTime = new ConcurrentHashMap<>();
		cleaner = new FinishedCleaner();
		cleaner.start();
	}

	public int size() {
		return taskRunning.size();
	}

	public void add(NasimportTask task) {
		taskRunning.put(task.getTaskId(), task);
		lastUpdateTime.put(task.getTaskId(), System.currentTimeMillis());
	}

	public void updateProgress(int taskId, int progress) {
		NasimportTask task = taskRunning.get(taskId);
		if (task != null) {
			task.setProgress(progress);
			lastUpdateTime.put(taskId, System.currentTimeMillis());
			if (progress == 100) {
				System.out.println("task " + taskId + " finished");
			}
		}
	}

	public List<NasimportTask> tasksMaybeFailed() {
		List<NasimportTask> failed = new LinkedList<>();
		for (int key : taskRunning.keySet()) {
			Long last = lastUpdateTime.get(key);
			if (last != null && System.currentTimeMillis() - last > FAIL_DURATION) {
				failed.add(taskRunning.get(key));
			}
		}
		return failed;
	}

	class FinishedCleaner extends Thread {
		@Override
		public void run() {
			while (true) {
				for (int key : taskRunning.keySet()) {
					if (taskRunning.get(key).getProgress() == 100) {
						taskRunning.remove(key);
						lastUpdateTime.remove(key);
					}
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
