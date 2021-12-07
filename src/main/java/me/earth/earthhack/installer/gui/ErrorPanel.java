package me.earth.earthhack.installer.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ErrorPanel
extends JPanel {
    public ErrorPanel(Throwable throwable) {
        JTextArea text = new JTextArea();
        text.setEditable(false);
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        text.setText(sw.toString().replace("\t", "   "));
        text.setCaretPosition(0);
        JScrollPane scroller = new JScrollPane(text);
        JButton button = new JButton("Close");
        button.addActionListener(e -> System.exit(0));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(button);
        this.setLayout(new BoxLayout(this, 1));
        this.add(scroller);
        this.add(buttonPanel);
    }
}
