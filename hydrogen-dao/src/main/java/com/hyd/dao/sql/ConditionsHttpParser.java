package com.hyd.dao.sql;

import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Stream;

/**
 * 将 Http 请求参数解析为 Conditions 对象，用于 SpringMVC 框架的 {@code @ModelAttribute} 方法。
 * 下面是一个例子：
 * <pre>
 *   &#064;ModelAttribute
 *   public Conditions parseConditions(@RequestParam Map<String, String> allParams) {
 *     return ConditionsHttpParser.parse(allParams);
 *   }
 * </pre>
 * 然后就可以在 Controller 中直接获取 Conditions 对象作为参数：
 * <pre>
 *   &#064;GetMapping("/user/list")
 *   public Response listUsers(@ModelAttribute Conditions conditions) {...}
 * </pre>
 */
public class ConditionsHttpParser {

    /**
     * 解析 Http 请求参数，生成 Conditions 对象。
     * 参数中 key 的格式为 $ 分开的两部分，左边是字段名，右边是操作符，如 "status$eq"。
     * 如果没有操作符，则默认为 eq。
     * 对于每个参数都构建一个 Condition 对象，然后添加到 Conditions 中。
     * 如果操作符为 in 或 nin，则该字段的值自动根据 "," 分隔符进行拆分成集合对象。
     * 例如参数为 { status$eq=1, id$in=1,2,3 }，则构建的 Conditions 对象包含下面两个条件：
     * - Condition.of("status", Operator.eq, 1),
     * - Condition.of("id", Operator.in, [1, 2, 3])
     */
    public static Conditions parse(Map<String, String> allParams) {

        Conditions conditions = new Conditions();

        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!StringUtils.hasText(value)) {
                continue;
            }

            if (key.equalsIgnoreCase("pageIndex")) {
                conditions.setPageIndex(Integer.parseInt(value));
            } else if (key.equalsIgnoreCase("pageSize")) {
                conditions.setPageSize(Integer.parseInt(value));
            } else if (key.equalsIgnoreCase("orderBy")) {
                conditions.addOrderBy(value);
            } else {
                conditions.addCondition(parseOperCondition(key, value));
            }
        }

        return conditions;
    }

    private static Condition parseOperCondition(String key, String value) {
        String[] parts = key.split("\\$");
        String propName = parts[0];
        String operatorStr = parts.length > 1 ? parts[1] : "eq";

        Condition condition;
        Operator operator = Operator.valueOf(operatorStr);
        if (operator == Operator.in || operator == Operator.nin) {
            condition = Condition.of(propName, operator, Stream.of(value.split(",")).toList());
        } else {
            condition = Condition.of(propName, operator, value);
        }
        return condition;
    }
}
