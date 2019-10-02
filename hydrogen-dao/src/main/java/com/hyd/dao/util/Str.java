package com.hyd.dao.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 处理字符串的类
 */
public class Str {

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
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    public static int countMatches(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    /**
     * 将字段名转换为属性名
     *
     * @param columnName 字段名，如："name", "parent_node"。
     *                   如果字段名属于 Java 关键字，则属性名为对应的全大写；
     *                   如果字段名不包含下划线，则认为不需要转换
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

        return columnName.contains("_") ? underscore2Property(columnName) : columnName;
    }

    public static String underscore2Property(String underscore) {
        return uncapitalize(Stream.of(underscore.toLowerCase().split("_"))
            .map(Str::capitalize)
            .collect(Collectors.joining()));
    }

    public static String underscore2Class(String underscore) {
        return capitalize(underscore2Property(underscore));
    }

    public static String capitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }

        char[] chars = str.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String uncapitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }

        char[] chars = str.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
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
        return str == null || str.trim().length() == 0;
    }

    public static boolean isEmpty(Object obj) {
        return obj == null || isEmptyString(obj.toString());
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

    public static String removeEnd(String s, String end) {
        if (s == null || end == null) {
            return s;
        }

        if (s.length() < end.length()) {
            return s;
        }

        return s.endsWith(end) ? s.substring(0, s.length() - end.length()) : s;
    }

    public static String removeLastAndAfter(String s, String search) {
        if (s == null || search == null) {
            return s;
        }

        int index = s.lastIndexOf(search);
        return index == -1 ? s : s.substring(0, index);
    }

    public static String subStringAfterLast(String s, String search) {
        if (s == null || search == null) {
            return s;
        }

        int index = s.lastIndexOf(search);
        if (index == -1) {
            return s;
        } else if (index + search.length() >= s.length()) {
            return "";
        } else {
            return s.substring(index + search.length());
        }
    }

    public static String defaultIfEmpty(String s, String defaultValue) {
        return isEmptyString(s) ? defaultValue : s;
    }

    public static boolean isAnyEmpty(String... strs) {
        for (String str : strs) {
            if (isEmptyString(str)) {
                return true;
            }
        }

        return false;
    }
}
