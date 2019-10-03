package com.hyd.dao.src.code.method;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.mate.util.Str;
import com.hyd.dao.mate.util.TypeUtil;
import com.hyd.dao.src.RepoMethodDef;
import com.hyd.dao.src.code.MethodArg;
import com.hyd.dao.src.code.ParamInfo;
import com.hyd.dao.src.fx.Comparator;

import java.util.List;

/**
 * @author yiding.he
 */
public abstract class RepoMethodBuilder {

    DatabaseType databaseType;

    String tableName;

    String methodName;

    List<ParamInfo> paramInfoList;

    /**
     * 构造方法
     *
     * @param tableName     表名
     * @param databaseType  数据库类型
     * @param paramInfoList （可选）用户配置后的参数列表
     * @param methodName    （可选）用户配置的方法名
     */
    public RepoMethodBuilder(
            String tableName,
            DatabaseType databaseType,
            List<ParamInfo> paramInfoList,
            String methodName) {

        this.databaseType = databaseType;
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

        String javaType = TypeUtil.getJavaType(databaseType, columnInfo);
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
