package com.hyd.dao.mate.generator.code;

import java.util.function.Consumer;
import javafx.beans.property.Property;

/**
 * 可以在界面上为 ClassDefBuilder 添加的扩展
 */
public class ClassDefBuilderExt<T> {

    /**
     * 界面元素对应的属性，属性值变更时将刷新生成的代码
     */
    private Property<T> property;

    /**
     * 生成代码之前，需要对 ClassDefBuilder 进行的额外操作
     */
    private Consumer<ClassDefBuilder> consumer;

    public ClassDefBuilderExt(Property<T> property, Consumer<ClassDefBuilder> consumer) {
        this.property = property;
        this.consumer = consumer;
    }

    public Property<T> property() {
        return property;
    }

    public Consumer<ClassDefBuilder> consumer() {
        return consumer;
    }
}
