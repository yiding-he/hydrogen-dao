package com.hyd.dao.src.code.method;

import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.src.RepoMethodReturnType;
import com.hyd.dao.src.code.ParamInfo;
import com.hyd.dao.util.Str;

import java.util.List;

/**
 * @author yiding.he
 */
public class QueryListMethodBuilder extends QueryOneMethodBuilder {

    public QueryListMethodBuilder(DatabaseType databaseType, String tableName, String methodName, List<ParamInfo> paramInfoList) {
        super(databaseType, tableName, methodName, paramInfoList);
    }

    @Override
    String getMethodType() {
        return "List<" + Str.underscore2Class(tableName) + ">";
    }

    @Override
    String getDaoQueryMethod() {
        return "query";
    }

    @Override
    String getNonArgMethodName() {
        return "queryAll";
    }

    @Override
    RepoMethodReturnType getRepoReturnType() {
        return RepoMethodReturnType.Collection;
    }
}
