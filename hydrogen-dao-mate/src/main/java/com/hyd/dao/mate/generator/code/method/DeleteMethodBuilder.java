package com.hyd.dao.mate.generator.code.method;

import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.mate.generator.code.CodeBlock;
import com.hyd.dao.mate.generator.code.ParamInfo;
import com.hyd.dao.mate.generator.code.RepoMethodDef;

import java.util.List;

/**
 * @author yiding.he
 */
public class DeleteMethodBuilder extends QueryOneMethodBuilder {

    public DeleteMethodBuilder(
        Dialect dialect, String tableName, String methodName, List<ParamInfo> paramInfoList) {
        super(dialect, tableName, methodName, paramInfoList);
    }


    @Override
    protected CodeBlock buildBody() {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.addLine("dao.execute(SQL.Delete(\"" + tableName + "\")");
        addQueryParameters(codeBlock);
        codeBlock.addLine(");");
        return codeBlock;
    }

    @Override
    protected void afterBodyCreated(RepoMethodDef repoMethodDef) {

        repoMethodDef.type = null;
    }

    @Override
    protected String getNonArgMethodName() {
        return "deleteAll";
    }

    @Override
    protected String getMethodNamePrefix() {
        return "deleteBy";
    }
}
