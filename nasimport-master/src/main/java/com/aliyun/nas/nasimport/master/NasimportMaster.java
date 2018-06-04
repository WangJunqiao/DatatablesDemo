package com.aliyun.nas.nasimport.master;


import com.aliyun.nas.nasimport.api.NasimportMasterService;
import com.aliyun.nas.nasimport.api.NasimportSlaveService;
import com.aliyun.nas.nasimport.api.NasimportTask;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NasimportMaster extends Thread implements NasimportMasterService {

	RunningTaskPool runningTaskPool;
	NasimportSlaveService nasimportSlaveService;
	MasterStatus masterStatus = new MasterStatus();

	LinkedList<NasimportTask> mockTasks = new LinkedList<>();

	public NasimportMaster() {
		// this maybe first master, or last master is down.
		// Load data from shared storage if necessary.
		masterStatus.masterId = System.currentTimeMillis();
		masterStatus.status = "RUNNING";
		for (int i = 0; i < 50; i ++) {
			NasimportTask task = new NasimportTask();
			List<String> paths = new ArrayList<>();
			for (int j = 0; j < 100; j ++) {
				paths.add("/root/" + i + "/" + j);
			}
			task.setTaskId(i);
			task.setProgress(0);
			task.setPaths(paths);
			mockTasks.add(task);
		}
		runningTaskPool = new RunningTaskPool();
	}


	@Override
	public void run() {
		while (mockTasks.size() > 0) {
			NasimportTask task = mockTasks.getFirst();
			try {
				if (nasimportSlaveService.startTask(task)) {
					System.out.println("task " + task.getTaskId() + " dispatched successfully");
					runningTaskPool.add(task);
					mockTasks.removeFirst();
					continue;
				}
				for (NasimportTask tmp : runningTaskPool.tasksMaybeFailed()) {
					System.out.println("task " + tmp.getTaskId() + " failed, retry later");
					mockTasks.addFirst(tmp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		while (runningTaskPool.size() != 0) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println("All tasks finished!");
	}

	@Override
	public MasterStatus getMasterStatus() {
		// TODO: add other status check
		return this.masterStatus;
	}

	@Override
	public boolean updateTaskProgress(int taskId, int progress) {
		runningTaskPool.updateProgress(taskId, progress);
		return true;
	}
}
