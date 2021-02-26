package com.hyd.dao.mate.generator.code.method;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.mate.generator.code.Comparator;
import com.hyd.dao.mate.generator.code.MethodArg;
import com.hyd.dao.mate.generator.code.ParamInfo;
import com.hyd.dao.mate.generator.code.RepoMethodDef;
import com.hyd.dao.mate.util.Str;

import java.util.List;

/**
 * @author yiding.he
 */
public abstract class RepoMethodBuilder {

    Dialect dialect;

    String tableName;

    String methodName;

    List<ParamInfo> paramInfoList;

    /**
     * 构造方法
     *
     * @param tableName     表名
     * @param dialect       数据库类型
     * @param paramInfoList （可选）用户配置后的参数列表
     * @param methodName    （可选）用户配置的方法名
     */
    public RepoMethodBuilder(
        String tableName,
        Dialect dialect,
        List<ParamInfo> paramInfoList,
        String methodName) {

        this.dialect = dialect;
        this.paramInfoList = paramInfoList;
        this.tableName = tableName;
        this.methodName = methodName;
    }

    public abstract RepoMethodDef build();

    MethodArg paramInfo2Arg(ParamInfo paramInfo) {

        Comparator comparator = paramInfo.comparator.get();
        ColumnInfo columnInfo = paramInfo.columnInfo.get();

        return new MethodArg(
            getArgType(columnInfo, comparator),
            paramInfo.getSuggestParamName()
        );
    }

    private String getArgType(ColumnInfo columnInfo, Comparator comparator) {

        String javaType = dialect.getJavaType(columnInfo);
        if (comparator == Comparator.In) {
            return "List<" + javaType + ">";
        } else {
            return javaType;
        }
    }

    String beanClassName() {
        return Str.underscore2Class(tableName);
    }
}
