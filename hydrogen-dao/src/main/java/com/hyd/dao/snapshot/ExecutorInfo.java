package com.hyd.dao.snapshot;

import java.util.Date;

/**
 * 保存 Executor 执行信息。因为直接调用 Executor 获取数据可能会遇到阻塞，因此
 * 在 Executor 中包含一个 ExecutorInfo 对象以避免阻塞。当
 */
public class ExecutorInfo {

    private String dsName;          // Executor 属于哪个数据源

    private String lastCommand;     // 最近执行的 SQL 语句

    private Date lastExecuteTime;   // 最近执行 SQL 语句的时间

    private boolean closed;         // Executor 是否已关闭

    private Snapshot snapshot;      // 关联的快照（用于主动移除自己）

    public ExecutorInfo(String dsName) {
        this.dsName = dsName;
        this.snapshot = Snapshot.getInstance(dsName);
        this.snapshot.addExecutorInfo(this);
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
        this.snapshot.removeInfo(this);
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }

    public Date getLastExecuteTime() {
        return lastExecuteTime;
    }

    public void setLastExecuteTime(Date lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
    }

    public String toString() {
        return "ExecutorInfo{" +
                "dsName='" + dsName + '\'' +
                ", lastCommand='" + lastCommand + '\'' +
                ", lastExecuteTime=" + lastExecuteTime +
                '}';
    }

}
