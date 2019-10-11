package com.hyd.dao.mate.generator.code.method;

import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.mate.generator.code.*;
import com.hyd.dao.mate.util.Str;
import java.util.Arrays;
import java.util.List;

/**
 * @author yiding.he
 */
public class QueryPageMethodBuilder extends QueryOneMethodBuilder {

    public QueryPageMethodBuilder(
            DatabaseType databaseType, String tableName,
            String methodName, List<ParamInfo> paramInfoList) {
        super(databaseType, tableName, methodName, paramInfoList);
    }

    @Override
    String getMethodType() {
        return "Page<" + Str.underscore2Class(tableName) + ">";
    }

    @Override
    String getDaoMethod() {
        return "queryPage";
    }

    @Override
    String getNonArgMethodName() {
        return "queryAllPage";
    }

    @Override
    RepoMethodReturnType getRepoReturnType() {
        return RepoMethodReturnType.Page;
    }

    @Override
    List<MethodArg> getExtraArgs() {
        return Arrays.asList(
                new MethodArg("int", "pageSize"),
                new MethodArg("int", "pageIndex")
        );
    }

    @Override
    void afterBodyCreated(RepoMethodDef methodDef) {
        methodDef.body.addLine(-1, ", pageSize, pageIndex", true);
    }
}
