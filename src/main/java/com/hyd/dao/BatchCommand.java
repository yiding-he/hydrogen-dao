package com.hyd.dao;


import com.hyd.dao.database.ColumnInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>批处理命令。一个批处理命令包含 SQL 语句和参数列表。</p>
 * <p>下面是一个例子：<br/><code><pre>
 *     BatchCommand cmd = new BatchCommand("delete * from tt_test where id>=? and id<=?");
 *     cmd.addParams("3", "4");
 *     cmd.addParams("100", "200");
 *     cmd.addParams("201", "205");
 *     dao.execute(cmd);
 * </pre></code></p>
 * <p>实际上，因为 select、update 和 delete 语句都可以一次性操作多行记录，而 insert
 * 语句只能一次操作一行。因此批处理语句的主要用途还是批量插入。</p>
 */
public class BatchCommand {

    public static final BatchCommand EMPTY = new BatchCommand("");

    /////////////////////////////////////////////////////////////////

    private String command;

    private List<List<Object>> params = new ArrayList<List<Object>>();

    private ColumnInfo[] columnInfos;               // 参数对应的字段信息，有助于生成 null 参数，非必须

    public ColumnInfo[] getColumnInfos() {
        return columnInfos;
    }

    public void setColumnInfos(ColumnInfo[] columnInfos) {
        this.columnInfos = columnInfos;
    }

    /**
     * 构造函数
     *
     * @param command SQL 语句
     */
    public BatchCommand(String command) {
        this.command = command;
    }

    /**
     * 构造函数
     *
     * @param command SQL 语句
     * @param params  批量参数值
     */
    public BatchCommand(String command, List<List<Object>> params) {
        this.command = command;
        this.params = params;
    }

    /**
     * 获得 SQL 语句
     *
     * @return SQL 语句
     */
    public String getCommand() {
        return command;
    }

    /**
     * 获得所有的参数组
     *
     * @return 所有参数组
     */
    public List<List<Object>> getParams() {
        return params;
    }

    /**
     * 添加一组参数
     *
     * @param params 一组参数
     */
    @SuppressWarnings("unchecked")
    public void addParams(Object... params) {
        if (params.length == 1 && params[0] instanceof List) {
            List<Object> list = (List<Object>) params[0];
            for (Object o : list) {
                if (o == DAO.SYSDATE) {
                    throw new IllegalArgumentException("DAO.SYSDATE cannot be used in batch command.");
                }
            }
            this.params.add(list);
        } else {
            for (Object o : params) {
                if (o == DAO.SYSDATE) {
                    throw new IllegalArgumentException("DAO.SYSDATE cannot be used in batch command.");
                }
            }
            this.params.add(Arrays.asList(params));
        }
    }

}
