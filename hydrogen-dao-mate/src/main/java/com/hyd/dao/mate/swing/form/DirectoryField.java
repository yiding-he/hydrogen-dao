package com.hyd.dao.mate.swing.form;

import static javax.swing.BoxLayout.X_AXIS;

import com.hyd.dao.mate.swing.Swing;
import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;

public class DirectoryField extends TextFormField {

    private final JButton chooseButton = new JButton("...");

    public DirectoryField(String labelText) {
        super(labelText);

        Box hbox = new Box(X_AXIS);
        hbox.add(textField);
        hbox.add(createHorizontalStrut(Swing.SMALL_PADDING));
        hbox.add(chooseButton);

        this.add(hbox);

        chooseButton.addActionListener(event -> chooseDirectory());
    }

    private void chooseDirectory() {
        File startDir = new File(textField.getText());
        if (!startDir.exists()) {
            startDir = new File(".");
        }

        String result = Swing.chooseDirectory(startDir);
        if (result != null) {
            textField.setText(result);
        }
    }

    @Override
    protected List<Component> getFunctionComponents() {
        return Arrays.asList(this.textField, this.chooseButton);
    }
}
