package com.hyd.dao.mate.generator.code.method;

import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.mate.generator.code.ParamInfo;
import com.hyd.dao.mate.generator.code.RepoMethodReturnType;
import com.hyd.dao.mate.util.Str;

import java.util.List;

/**
 * @author yiding.he
 */
public class QueryListMethodBuilder extends QueryOneMethodBuilder {

    public QueryListMethodBuilder(Dialect dialect, String tableName, String methodName, List<ParamInfo> paramInfoList) {
        super(dialect, tableName, methodName, paramInfoList);
    }

    @Override
    protected String getMethodType() {
        return "List<" + Str.underscore2Class(tableName) + ">";
    }

    @Override
    protected String getDaoMethod() {
        return "query";
    }

    @Override
    protected String getNonArgMethodName() {
        return "queryAll";
    }

    @Override
    protected RepoMethodReturnType getRepoReturnType() {
        return RepoMethodReturnType.Collection;
    }
}
