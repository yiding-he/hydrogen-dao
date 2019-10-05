package com.hyd.dao.mate.swing;

import javax.swing.*;
import java.io.File;

import static javax.swing.BoxLayout.X_AXIS;

public class FileField extends FormField<String> {

    private JTextField textField = textField();

    private JButton chooseButton = new JButton("...");

    public FileField(String labelText) {
        super(labelText);

        Box hbox = new Box(X_AXIS);
        hbox.add(textField);
        hbox.add(createHorizontalStrut(Swing.SMALL_PADDING));
        hbox.add(chooseButton);

        this.add(hbox);

        chooseButton.addActionListener(event -> chooseFile());
    }

    private void chooseFile() {
        File startDir = new File(textField.getText());
        if (!startDir.exists()) {
            startDir = new File(".");
        }

        String result = Swing.chooseFile(startDir);
        if (result != null) {
            textField.setText(result);
        }
    }

    @Override
    public String getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(String value) {
        this.textField.setText(value);
    }
}
