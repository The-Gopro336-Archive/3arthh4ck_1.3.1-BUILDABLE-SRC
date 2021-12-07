package me.earth.earthhack.installer.gui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class InstallerFrame {
    private final JFrame frame;

    public InstallerFrame() {
        FlatLightLaf.setup(new FlatDarculaLaf());
        this.frame = new JFrame("3arthh4ck-Installer");
        this.frame.setDefaultCloseOperation(3);
        JPanel panel = new JPanel();
        panel.setSize(550, 400);
        panel.setPreferredSize(new Dimension(550, 400));
        panel.setLayout(null);
        this.frame.setSize(550, 400);
        this.frame.setResizable(false);
        this.frame.getContentPane().add(panel);
        this.frame.pack();
    }

    public void display() {
        this.frame.setVisible(true);
    }

    public void schedule(JPanel panel) {
        SwingUtilities.invokeLater(() -> this.setPanel(panel));
    }

    public void setPanel(JPanel panel) {
        this.frame.setContentPane(panel);
        this.frame.invalidate();
        this.frame.validate();
    }
}
