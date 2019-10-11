package com.hyd.dao.mate.generator.code.method;

import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.mate.generator.code.*;
import java.util.List;

/**
 * @author yiding.he
 */
public class DeleteMethodBuilder extends QueryOneMethodBuilder {

    public DeleteMethodBuilder(
            DatabaseType databaseType, String tableName, String methodName, List<ParamInfo> paramInfoList) {
        super(databaseType, tableName, methodName, paramInfoList);
    }


    @Override
    CodeBlock buildBody() {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.addLine("dao.execute(SQL.Delete(\"" + tableName + "\")");
        addQueryParameters(codeBlock);
        codeBlock.addLine(");");
        return codeBlock;
    }

    @Override
    void afterBodyCreated(RepoMethodDef repoMethodDef) {

        repoMethodDef.type = null;
    }

    @Override
    String getNonArgMethodName() {
        return "deleteAll";
    }

    @Override
    String getMethodNamePrefix() {
        return "deleteBy";
    }
}
