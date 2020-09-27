package com.hyd.dao.command;

import java.util.Iterator;
import java.util.List;

/**
 * 流式批处理命令。需要进行批处理，但是记录数不可预见的情况下使用。例如从文件中读取并导入数据，
 * 使用 IteratorBatchCommand 可以节省内存使用。
 *
 * @author yidin
 */
public class IteratorBatchCommand {

    public static final int DEFAULT_BATCH_SIZE = 100;

    private final String command;

    private Iterator<List<Object>> params;

    private int batchSize = DEFAULT_BATCH_SIZE;

    public IteratorBatchCommand(String command) {
        this.command = command;
    }

    public IteratorBatchCommand(String command, Iterator<List<Object>> params) {
        this.command = command;
        this.params = params;
    }

    public IteratorBatchCommand(String command, Iterator<List<Object>> params, int batchSize) {
        this.command = command;
        this.params = params;
        this.batchSize = batchSize;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public String getCommand() {
        return command;
    }

    public Iterator<List<Object>> getParams() {
        return params;
    }
}
