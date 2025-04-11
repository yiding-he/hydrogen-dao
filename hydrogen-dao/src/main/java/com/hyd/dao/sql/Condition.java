package com.hyd.dao.sql;

import com.hyd.dao.DAOException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.List;

import static org.springframework.util.StringUtils.uncapitalize;

@Data
@NoArgsConstructor
public class Condition {

    @FunctionalInterface
    public interface Getter<T, R> extends Serializable {

        @SuppressWarnings("unused")
        R apply(T t);
    }

    public static <T, R> Condition of(Getter<T, R> getter, Operator operator, List<Object> values) {
        var condition = new Condition();
        condition.setOperator(operator);
        condition.setValues(values);
        parseGetter(getter, condition);
        condition.adjust();
        return condition;
    }

    public static <T, R> Condition of(Getter<T, R> getter, Operator operator, Object... values) {
        return of(getter, operator, List.of(values));
    }

    public static Condition of(String propName, Operator operator, List<Object> values) {
        var condition = new Condition();
        condition.setPropName(propName);
        condition.setOperator(operator);
        condition.setValues(values);
        condition.adjust();
        return condition;
    }

    public static Condition of(String propName, Operator operator, Object... values) {
        return of(propName, operator, List.of(values));
    }

    /**
     * 解析 lambda 表达式，得到原始的 JavaBean 类名和属性名并注入到 Condition 对象
     */
    private static <T, R> void parseGetter(Getter<T, R> getter, Condition condition) {
        try {
            var lambdaClass = getter.getClass();
            var writeReplaceMethod = lambdaClass.getDeclaredMethod("writeReplace");
            writeReplaceMethod.setAccessible(true);
            var serializedForm = (SerializedLambda) writeReplaceMethod.invoke(getter);
            condition.setPropName(extractPropFromGetter(serializedForm.getImplMethodName()));
        } catch (Exception e) {
            throw DAOException.wrap(e);
        }
    }

    private static String extractPropFromGetter(String getterMethodName) {
        if (getterMethodName.startsWith("is")) {
            return uncapitalize(getterMethodName.substring(2));
        } else if (getterMethodName.startsWith("get")) {
            return uncapitalize(getterMethodName.substring(3));
        } else {
            throw new IllegalArgumentException("Invalid getter method name: " + getterMethodName);
        }
    }

    private String propName;

    private Operator operator;

    private List<Object> values;

    public String getColumnName() {
        return this.propName.replaceAll("([A-Z]+)", "_$1").toLowerCase();
    }

    /**
     * 根据 operator 对 values 进行调整
     */
    private void adjust() {
        if (this.operator == Operator.like && !this.values.isEmpty()) {
            this.values = List.of("%" + this.values.get(0) + "%");
        }
    }
}
