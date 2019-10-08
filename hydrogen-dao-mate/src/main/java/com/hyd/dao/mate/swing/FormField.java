package com.hyd.dao.mate.swing;

import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

import java.awt.Component;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.*;

public abstract class FormField<V> extends Box {

    protected JLabel label = new JLabel();

    protected Consumer<V> onValueChanged;

    protected JTextField textField() {
        return new JTextField();
    }

    public void setOnValueChanged(Consumer<V> onValueChanged) {
        this.onValueChanged = onValueChanged;
    }

    public FormField(String labelText) {
        super(Y_AXIS);
        label.setText(labelText);

        Box labelBox = new Box(X_AXIS);
        labelBox.add(label);
        labelBox.add(createHorizontalGlue());

        add(labelBox);
        add(createVerticalStrut(Swing.SMALL_PADDING));
    }

    protected void setAutoStretch(boolean autoStretch) {
        if (autoStretch) {
            this.setMaximumSize(new Dimension(
                (int) this.getMaximumSize().getWidth(),
                Integer.MAX_VALUE
            ));
        } else {
            this.setMaximumSize(new Dimension(
                (int) this.getMaximumSize().getWidth(),
                (int) this.getPreferredSize().getHeight()
            ));
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        getFunctionComponents().forEach(c -> c.setEnabled(enabled));
    }

    protected abstract java.util.List<Component> getFunctionComponents();

    public abstract V getValue();

    public abstract void setValue(V value);
}
