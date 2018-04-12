package com.hyd.dao.src.classdef;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.database.commandbuilder.helper.CommandBuilderHelper;
import com.hyd.dao.src.AccessType;
import com.hyd.dao.src.ClassDef;
import com.hyd.dao.src.FieldDef;
import com.hyd.dao.src.fx.ConnectionManager;
import com.hyd.dao.util.Str;
import org.apache.commons.lang3.StringUtils;

/**
 * (description)
 * created at 2018/4/12
 *
 * @author yidin
 */
public class HydrogenModelClassBuilder extends ClassDefBuilder {

    public HydrogenModelClassBuilder(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public ClassDef build(String tableName) {
        ClassDef[] result = new ClassDef[1];

        connectionManager.withConnection(connection -> {

            ClassDef classDef = new ClassDef();
            classDef.className = Str.underscore2Class(tableName);

            CommandBuilderHelper helper = CommandBuilderHelper.getHelper(connection);
            ColumnInfo[] columnInfos = helper.getColumnInfos(tableName);

            for (ColumnInfo columnInfo : columnInfos) {
                FieldDef field = new FieldDef();
                field.name = StringUtils.uncapitalize(Str.underscore2Property(columnInfo.getColumnName()));
                field.type = getJavaType(columnInfo.getDataType());
                field.access = AccessType.Private;

                classDef.addFieldIfNotExists(field);
                classDef.addMethod(field.toGetterMethod());
                classDef.addMethod(field.toSetterMethod());
            }

            result[0] = classDef;
        });

        return result[0];
    }

}
