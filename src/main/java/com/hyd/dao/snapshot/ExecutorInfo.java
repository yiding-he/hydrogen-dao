package com.hyd.dao.snapshot;

import com.hyd.dao.database.executor.Executor;

import java.util.Date;

/**
 * 保存 Executor 执行信息。直接调用 Executor 获取数据可能会遇到阻塞。
 */
public class ExecutorInfo {

    private String dsName;

    private String lastCommand;

    private Date lastExecuteTime;

    private boolean closed;

    private Executor executor;

    private Snapshot snapshot;

    public ExecutorInfo(String dsName, Executor executor) {
        this.dsName = dsName;
        this.executor = executor;
        this.executor.setInfo(this);
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    public Executor getExecutor() {
        return executor;
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
