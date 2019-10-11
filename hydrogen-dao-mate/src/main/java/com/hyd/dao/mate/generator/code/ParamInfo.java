package com.hyd.dao.mate.generator.code;

import com.hyd.dao.database.ColumnInfo;
import com.hyd.dao.mate.util.Str;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author yiding.he
 */
public class ParamInfo {

    public ObjectProperty<ColumnInfo> columnInfo = new SimpleObjectProperty<>();

    public ObjectProperty<Comparator> comparator = new SimpleObjectProperty<>();

    public String getSuggestParamName() {
        String paramName = Str.underscore2Property(columnInfo.get().getColumnName());

        Comparator comparator = this.comparator.get();
        switch (comparator) {
            case Like:
                return paramName + "Keyword";
            case GreaterThan:
            case GreaterOrEqual:
                return "min" + Str.capitalize(paramName);
            case LessThan:
            case LessOrEqual:
                return "max" + Str.capitalize(paramName);
            default:
                return paramName;
        }
    }

    @Override
    public String toString() {
        return columnInfo.get().getColumnName() + " " + comparator.get().getSymbol() + " ?";
    }
}
