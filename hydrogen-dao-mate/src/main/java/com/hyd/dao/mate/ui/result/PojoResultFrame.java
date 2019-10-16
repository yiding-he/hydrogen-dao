package com.hyd.dao.mate.ui.result;

import com.hyd.dao.mate.swing.Swing;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class PojoResultFrame extends PojoResultLayout {

    public PojoResultFrame(String pojoName, String pojoCode) {
        this.setTitle(pojoName + ".java");
        this.textArea.setText(pojoCode);
        this.textArea.setFont(new Font("DialogInput", Font.PLAIN, 12));
        this.copyButton.addActionListener(event -> copyCode());
    }

    private void copyCode() {
        StringSelection stringSelection = new StringSelection(this.textArea.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        Swing.alertInfo(this, "拷贝完成", "代码已复制到剪贴板。");
    }
}
