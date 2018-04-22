package com.hyd.dao.src.code.method;

import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.src.RepoMethodDef;
import com.hyd.dao.src.RepoMethodReturnType;
import com.hyd.dao.src.code.AccessType;
import com.hyd.dao.src.code.CodeBlock;
import com.hyd.dao.src.code.MethodArg;
import com.hyd.dao.src.code.ParamInfo;
import com.hyd.dao.src.fx.Comparator;
import com.hyd.dao.util.Str;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yiding.he
 */
public class QueryOneMethodBuilder extends RepoMethodBuilder {

    public QueryOneMethodBuilder(
            DatabaseType databaseType,
            String tableName, String methodName, List<ParamInfo> paramInfoList) {
        super(tableName, databaseType, paramInfoList, methodName);

    }

    String getMethodType() {
        return Str.underscore2Class(tableName);
    }

    String getNonArgMethodName() {
        return "queryOne";
    }

    RepoMethodReturnType getRepoReturnType() {
        return RepoMethodReturnType.Single;
    }

    String getDaoQueryMethod() {
        return "queryFirst";
    }

    List<MethodArg> getExtraArgs() {
        return Collections.emptyList();
    }

    void afterBodyCreated(CodeBlock body) {

    }

    public RepoMethodDef build() {

        RepoMethodDef repoMethodDef = new RepoMethodDef();
        repoMethodDef.access = AccessType.Public;
        repoMethodDef.name = methodName;
        repoMethodDef.returnType = getRepoReturnType();
        repoMethodDef.type = getMethodType();

        paramInfoList.forEach(paramInfo -> repoMethodDef.args.add(paramInfo2Arg(paramInfo)));
        repoMethodDef.args.addAll(getExtraArgs());


        if (Str.isEmptyString(repoMethodDef.name)) {
            if (paramInfoList.isEmpty()) {
                repoMethodDef.name = getNonArgMethodName();
            } else {
                repoMethodDef.name = "queryBy" + paramInfoList.stream()
                        .map(info -> Str.underscore2Class(info.columnInfo.get().getColumnName()))
                        .distinct()
                        .collect(Collectors.joining("And"));
            }
        }

        repoMethodDef.body = buildBody();
        afterBodyCreated(repoMethodDef.body);

        return repoMethodDef;
    }

    private CodeBlock buildBody() {

        String className = Str.underscore2Class(tableName);
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.addLine("return", "dao." + getDaoQueryMethod() + "(" + className + ".class, ");
        codeBlock.addLine("    SQL.Select(\"*\")");
        codeBlock.addLine("    .From(\"" + tableName + "\")");

        for (int i = 0; i < paramInfoList.size(); i++) {

            ParamInfo paramInfo = paramInfoList.get(i);
            String columnName = paramInfo.columnInfo.get().getColumnName();
            Comparator comparator = paramInfo.comparator.get();

            String where = "    " + (i == 0 ? ".Where" : ".And");
            where += "(\"" + columnName + " " + comparator.getSymbol() + " ?\", ";
            where += getParamValue(paramInfo) + ")";
            codeBlock.addLine(where);
        }

        codeBlock.addLine(");");
        return codeBlock;
    }

    private String getParamValue(ParamInfo paramInfo) {

        Comparator comparator = paramInfo.comparator.get();
        String result = paramInfo.getSuggestParamName();

        if (comparator == Comparator.Like) {
            result = "\"%\" + " + result + " + \"%\"";
        }

        return result;
    }

}
