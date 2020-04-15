package com.hyd.dao.mate.generator.code.method;

import com.hyd.dao.database.DatabaseType;
import com.hyd.dao.mate.generator.code.AccessType;
import com.hyd.dao.mate.generator.code.CodeBlock;
import com.hyd.dao.mate.generator.code.MethodArg;
import com.hyd.dao.mate.generator.code.ParamInfo;
import com.hyd.dao.mate.generator.code.RepoMethodDef;
import java.util.List;

/**
 * @author yiding.he
 */
public class InsertMapMethodBuilder extends RepoMethodBuilder {

    /**
     * 构造方法
     *
     * @param tableName     表名
     * @param databaseType  数据库类型
     * @param paramInfoList （可选）用户配置后的参数列表
     * @param methodName    （可选）用户配置的方法名
     */
    public InsertMapMethodBuilder(String tableName, DatabaseType databaseType, List<ParamInfo> paramInfoList, String methodName) {
        super(tableName, databaseType, paramInfoList, methodName);
    }

    @Override
    public RepoMethodDef build() {

        String paramName = "map";

        RepoMethodDef methodDef = new RepoMethodDef();
        methodDef.name = "insert";
        methodDef.access = AccessType.Public;
        methodDef.args.add(new MethodArg("Map<String, Object>", paramName));

        methodDef.body = buildBody(paramName);
        return methodDef;
    }

    private CodeBlock buildBody(String paramName) {
        CodeBlock codeBlock = new CodeBlock();
        codeBlock.addLine("dao.insert(" + paramName + ", \"" + tableName + "\");");
        return codeBlock;
    }
}
