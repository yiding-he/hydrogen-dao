package com.hyd.dao.mate.generator.code.method;

import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.mate.generator.code.*;
import com.hyd.dao.mate.util.Str;
import java.util.List;

/**
 * @author yiding.he
 */
public class InsertBeanMethodBuilder extends RepoMethodBuilder {

    /**
     * 构造方法
     *
     * @param tableName     表名
     * @param databaseType  数据库类型
     * @param paramInfoList （可选）用户配置后的参数列表
     * @param methodName    （可选）用户配置的方法名
     */
    public InsertBeanMethodBuilder(String tableName, DatabaseType databaseType, List<ParamInfo> paramInfoList, String methodName) {
        super(tableName, databaseType, paramInfoList, methodName);
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
