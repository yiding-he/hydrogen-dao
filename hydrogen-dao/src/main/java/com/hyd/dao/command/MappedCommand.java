package com.hyd.dao.command;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 带参数的 SQL 语句及参数。SQL 语句中的参数以 #xxx# 的格式存在。例如：
 *
 * <pre>
 *     String sql = "select * from USER where USERNAME=#username# and ROLE in (#roles#)";
 *     Map&lt;String, Object&gt; params = new HashMap&lt;String, Object&gt;();
 *     params.put("username", "admin");
 *     params.put("roles", new int[]{1, 2, 3, 4});
 *     dao.query(new MappedCommand(sql, params));
 * </pre>
 *
 * created at 2015/3/6
 *
 * @author Yiding
 */
public class MappedCommand {

    private String statement;

    private Map<String, Object> params;

    public MappedCommand() {
        this.params = new HashMap<String, Object>();
    }

    public MappedCommand(String statement) {
        this.statement = statement;
        this.params = new HashMap<String, Object>();
    }

    public MappedCommand(String statement, Map<String, Object> params) {
        this.statement = statement;
        this.params = params;
    }

    public String getStatement() {
        return statement;
    }

    public MappedCommand setStatement(String statement) {
        this.statement = statement;
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public MappedCommand setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public MappedCommand setParam(String name, Object value) {
        this.params.put(name, value);
        return this;
    }

    public MappedCommand setParam(String name, Object... values) {
        this.params.put(name, values);
        return this;
    }

    public Command toCommand() {
        Command command = new Command();
        Pattern pattern = Pattern.compile("#(\\S+)#");
        Matcher matcher = pattern.matcher(this.statement);

        StringBuffer sb = new StringBuffer();
        List<Object> paramList = new ArrayList<Object>();

        while (matcher.find()) {
            String name = matcher.group(1);
            Object value = this.params.get(name);

            if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                String holders = "";
                for (int i = 0; i < length; i++) {
                    paramList.add(Array.get(value, i));
                    holders += "?,";
                }
                if (holders.endsWith(",")) {
                    holders = holders.substring(0, holders.length() - 1);
                }
                matcher.appendReplacement(sb, holders);
            } else if (value instanceof Collection) {
                String holders = "";
                for (Object item : (Collection) value) {
                    paramList.add(item);
                    holders += "?,";
                }
                if (holders.endsWith(",")) {
                    holders = holders.substring(0, holders.length() - 1);
                }
                matcher.appendReplacement(sb, holders);
            } else {
                paramList.add(value);
                matcher.appendReplacement(sb, "?");
            }
        }

        matcher.appendTail(sb);
        command.setParams(paramList);
        command.setStatement(sb.toString());
        return command;
    }
}
