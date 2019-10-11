package com.hyd.dao.mate.ui.main.pojo.generate;

import com.hyd.dao.mate.swing.FormPanel;
import com.hyd.dao.mate.swing.Swing;
import com.hyd.dao.mate.swing.form.*;
import javax.swing.*;

public class PojoConfigLayout extends JPanel {

    protected final TextField pojoName = new TextField("POJO 类名：");

    protected final ComboBoxField convertType = new ComboBoxField("属性名转换方式：");

    protected final CheckBoxField lombok = new CheckBoxField("添加 lombok 的 @Data 注解：");

    protected final CheckBoxField mybatisPlus = new CheckBoxField("添加 Mybatis Plus 注解：");

    protected final JButton generateButton = new JButton("生成代码");

    public PojoConfigLayout() {
        setBorder(BorderFactory.createEmptyBorder(0, Swing.PADDING, 0, 0));

        FormPanel formPanel = new FormPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("POJO 配置"));
        formPanel.addFormField(pojoName);
        formPanel.addFormField(convertType);
        formPanel.addFormField(lombok);
        formPanel.addFormField(mybatisPlus);
        formPanel.addButton(generateButton);

        add(formPanel);
        Swing.fillWith(this, formPanel, Swing.TOP, Swing.LEFT, Swing.RIGHT);
    }
}
