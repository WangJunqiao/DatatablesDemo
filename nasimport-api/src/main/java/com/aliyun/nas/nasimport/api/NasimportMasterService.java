package com.aliyun.nas.nasimport.api;

import java.io.Serializable;

/**
 * Created by junqiao.wjq on 2018/6/3.
 */
public interface NasimportMasterService {
	class MasterStatus implements Serializable{
		public long masterId;
		public String status;
	}

	MasterStatus getMasterStatus();
	boolean updateTaskProgress(int taskId, int progress);
}
