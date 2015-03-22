package com.hyd.dao.snapshot;

import java.util.*;

/**
 * 包含当前连接数据的快照（仅当使用本地连接池时可用）
 */
public class Snapshot {

    /**
     * 数据源 -> 快照
     */
    private static HashMap<String, Snapshot> instances = new HashMap<String, Snapshot>();

    /**
     * 获得指定数据源的一个快照
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

    ////////////////////////////////////////////////////////////////

    /**
     * 当前正在执行数据库命令的 Executor 列表
     */
    private List<ExecutorInfo> executor_infos = Collections.synchronizedList(new ArrayList<ExecutorInfo>());

    public void addExecutorInfo(ExecutorInfo info) {
        executor_infos.add(info);
    }

    public ExecutorInfo[] getExecutorInfos() {
        return executor_infos.toArray(new ExecutorInfo[executor_infos.size()]);
    }

    public void removeInfo(ExecutorInfo executorInfo) {
        executor_infos.remove(executorInfo);
    }
}
