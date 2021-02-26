package com.hyd.dao.mate.generator.code.method;

import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.mate.generator.code.*;
import com.hyd.dao.mate.util.Str;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yiding.he
 */
public class QueryOneMethodBuilder extends RepoMethodBuilder {

    public QueryOneMethodBuilder(
        Dialect dialect,
            String tableName, String methodName, List<ParamInfo> paramInfoList) {
        super(tableName, dialect, paramInfoList, methodName);

    }

    protected String getMethodType() {
        return Str.underscore2Class(tableName);
    }

    protected String getNonArgMethodName() {
        return "queryOne";
    }

    protected RepoMethodReturnType getRepoReturnType() {
        return RepoMethodReturnType.Single;
    }

    protected String getDaoMethod() {
        return "queryFirst";
    }

    protected List<MethodArg> getExtraArgs() {
        return Collections.emptyList();
    }

    protected void afterBodyCreated(RepoMethodDef repoMethodDef) {

    }

    protected String getMethodNamePrefix() {
        return "queryBy";
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
                repoMethodDef.name = getMethodNamePrefix() + paramInfoList.stream()
                        .map(info -> Str.underscore2Class(info.columnInfo.get().getColumnName()))
                        .distinct()
                        .collect(Collectors.joining("And"));
            }
        }

        repoMethodDef.body = buildBody();
        afterBodyCreated(repoMethodDef);

        return repoMethodDef;
    }

    protected CodeBlock buildBody() {

        String className = Str.underscore2Class(tableName);
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.addLine("return", "dao." + getDaoMethod() + "(" + className + ".class, ");
        codeBlock.addLine("    SQL.Select(\"*\")");
        codeBlock.addLine("    .From(\"" + tableName + "\")");

        addQueryParameters(codeBlock);

        codeBlock.addLine(");");
        return codeBlock;
    }

    protected void addQueryParameters(CodeBlock codeBlock) {
        for (int i = 0; i < paramInfoList.size(); i++) {

            ParamInfo paramInfo = paramInfoList.get(i);
            String columnName = paramInfo.columnInfo.get().getColumnName();
            Comparator comparator = paramInfo.comparator.get();

            String where = "    " + (i == 0 ? ".Where" : ".And");
            where += "(\"" + columnName + " " + comparator.getSymbol() + " ?\", ";
            where += getParamValue(paramInfo) + ")";
            codeBlock.addLine(where);
        }
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
