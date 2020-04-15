package com.hyd.dao.snapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 包含当前连接数据的快照（仅当使用本地连接池时可用）
 */
public class Snapshot {

    /**
     * 数据源 -> 快照
     */
    private static final Map<String, Snapshot> instances = new ConcurrentHashMap<>();

    /**
     * 当前正在执行数据库命令的 Executor 列表
     */
    private final List<ExecutorInfo> executorInfoList = Collections.synchronizedList(new ArrayList<>());

    /**
     * 获得指定数据源的一个快照
     *
     * @param dsName 数据源名称
     *
     * @return 对应的快照
     */
    public static Snapshot getInstance(String dsName) {
        return instances.computeIfAbsent(dsName, __dsName -> new Snapshot());
    }

    ////////////////////////////////////////////////////////////////

    void addExecutorInfo(ExecutorInfo info) {
        executorInfoList.add(info);
    }

    public ExecutorInfo[] getExecutorInfos() {
        return executorInfoList.toArray(new ExecutorInfo[0]);
    }

    void removeInfo(ExecutorInfo executorInfo) {
        executorInfoList.remove(executorInfo);
    }
}
