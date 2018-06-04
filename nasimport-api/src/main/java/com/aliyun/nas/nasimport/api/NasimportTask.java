package com.aliyun.nas.nasimport.api;

import java.io.Serializable;
import java.util.List;

/**
 * Created by junqiao.wjq on 2018/6/3.
 */
public class NasimportTask implements Serializable{
	int taskId;

	int progress;
	List<String> paths;

	public int getTaskId() {
		return taskId;
	}

	public List<String> getPaths() {
		return paths;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void setPaths(List<String> paths) {
		this.paths = paths;
	}

	public int getProgress() {
		return progress;
	}
}
