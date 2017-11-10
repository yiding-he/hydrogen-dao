package com.hyd.dao.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理字符串的类
 */
public class Str extends StringUtils {

    /**
     * 查找指定字符串中包含匹配指定正则表达式的次数
     *
     * @param str   要查找的字符串
     * @param regex 正则表达式
     *
     * @return 字符串中包含匹配指定正则表达式的次数
     */
    public static int count(String str, String regex) {
        if (isEmpty(regex) || isEmpty(str)) {
            return 0;
        }

        int count = 0;
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) count++;
        return count;
    }

    /**
     * 将字段名转换为属性名
     *
     * @param columnName 字段名，如："name", "parent_node"。如果字段名属于 Java 关键字，则属性名为对应的全大写
     *
     * @return 属性名，如："name", "parentNode"
     */
    public static String columnToProperty(String columnName) {

        if (columnName.equalsIgnoreCase("abstract")
                || columnName.equalsIgnoreCase("private")
                || columnName.equalsIgnoreCase("protected")
                || columnName.equalsIgnoreCase("static")
                || columnName.equalsIgnoreCase("void")
                || columnName.equalsIgnoreCase("interface")
                || columnName.equalsIgnoreCase("enum")
                || columnName.equalsIgnoreCase("class")) {

            return columnName.toUpperCase();
        }

        String[] strs = columnName.toLowerCase().split("_");
        String name = strs[0];
        for (int i = 1; i < strs.length; i++) {
            name += capitalize(strs[i]);
        }
        return name;
    }

    /**
     * 将属性名转换为字段名。属性名应符合 JavaBean 命名规范。
     *
     * @param propertyName 属性名，如："name", "parentNode"
     *
     * @return 字段名，如："name", "parent_node"
     */
    public static String propertyToColumn(String propertyName) {
        return propertyName.replaceAll("([A-Z])", "_$1").toLowerCase();
    }

    /**
     * 判断字符串变量是否为空
     *
     * @param str 要判断的字符串
     *
     * @return 如果 str 为 null、为空或仅包含空白字符，则返回 true。
     */
    public static boolean isEmptyString(String str) {
        return str == null || str.matches("\\s*");
    }

    public static boolean isEmpty(Object obj) {
        return obj == null || isEmpty(obj.toString());
    }

    /**
     * 如果字符串变量为 null，则返回空字符串，否则返回字符串本身。
     *
     * @param str 字符串变量
     *
     * @return 替换后的字符串
     */
    public static String n(String str) {
        return str == null ? "" : str;
    }

    /**
     * 在指定字符串中按正则表达式查找
     *
     * @param str   要在其中搜索的字符串
     * @param regex 正则表达式
     *
     * @return 满足表达式的子字符串位置
     */
    public static int find(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(str);
        if (matcher.find()) {
            return matcher.start();
        }
        return -1;
    }

    /**
     * 相对于 String.valueOf，本方法将 null 转变为空字符串。
     *
     * @param o 要转成字符串的对象
     *
     * @return 转换结果
     */
    public static String valueOf(Object o) {
        return o == null ? "" : String.valueOf(o);
    }
}
