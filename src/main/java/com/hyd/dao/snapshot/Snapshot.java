package com.hyd.dao.snapshot;

import java.util.HashMap;
import java.util.Vector;

/**
 * 包含当前连接数据的快照（仅当使用本地连接池时可用）
 */
public class Snapshot {

    private static HashMap<String, Snapshot> instances = new HashMap<String, Snapshot>();

    /**
     * 获得一个快照
     *
     * @param dsName 数据源名称
     *
     * @return 对应的快照
     */
    public static Snapshot getInstance(String dsName) {
        Snapshot result = instances.get(dsName);
        if (result == null) {
            result = new Snapshot();
            instances.put(dsName, result);
        }
        return result;
    }

    private Vector<ExecutorInfo> executor_infos = new Vector<ExecutorInfo>();

    public void addExecutorInfo(ExecutorInfo info) {
        info.setSnapshot(this);
        executor_infos.add(info);
    }

    public ExecutorInfo[] getExecutorInfos() {
        return executor_infos.toArray(new ExecutorInfo[executor_infos.size()]);
    }

    public void removeInfo(ExecutorInfo executorInfo) {
        executor_infos.remove(executorInfo);
    }
}
