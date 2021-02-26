package com.hyd.dao.mate.generator.code.method;

import com.hyd.dao.database.dialects.Dialect;
import com.hyd.dao.mate.generator.code.*;
import com.hyd.dao.mate.util.Str;

import java.util.List;

/**
 * @author yiding.he
 */
public class InsertBeanMethodBuilder extends RepoMethodBuilder {

    public InsertBeanMethodBuilder(String tableName, Dialect dialect, List<ParamInfo> paramInfoList, String methodName) {
        super(tableName, dialect, paramInfoList, methodName);
    }

    @Override
    public RepoMethodDef build() {

        String beanClassName = beanClassName();
        String paramName = Str.uncapitalize(beanClassName);

        RepoMethodDef methodDef = new RepoMethodDef();
        methodDef.name = "insert";
        methodDef.access = AccessType.Public;
        methodDef.args.add(new MethodArg(beanClassName, paramName));

        methodDef.body = buildBody(paramName);
        return methodDef;
    }

    private CodeBlock buildBody(String paramName) {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.addLine("dao.insert(" + paramName + ", \"" + tableName + "\");");
        return codeBlock;
    }
}
