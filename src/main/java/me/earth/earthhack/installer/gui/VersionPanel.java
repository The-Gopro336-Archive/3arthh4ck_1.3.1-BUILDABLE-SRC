package me.earth.earthhack.installer.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import me.earth.earthhack.installer.Installer;
import me.earth.earthhack.installer.gui.VersionMouseAdapter;
import me.earth.earthhack.installer.version.Version;
import me.earth.earthhack.installer.version.VersionUtil;

public class VersionPanel
extends JPanel {
    public VersionPanel(Installer handler, List<Version> versions) {
        Object[] columns = new String[]{"name", "forge", "3arthh4ck", "valid"};
        ArrayList<Object[]> data = new ArrayList<Object[]>(versions.size());
        for (Version version : versions) {
            boolean earth = VersionUtil.hasEarthhack(version);
            boolean forge = VersionUtil.hasForge(version);
            boolean valid = VersionUtil.hasLaunchWrapper(version);
            data.add(new Object[]{version.getName(), forge, earth, valid});
        }
        JButton install = new JButton("Install");
        install.setEnabled(false);
        JButton uninstall = new JButton("Uninstall");
        uninstall.setEnabled(false);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> handler.refreshVersions());
        Object[][] t = (Object[][])data.toArray((T[])new Object[0][]);
        JTable jt = new JTable(t, columns);
        jt.setSelectionMode(0);
        jt.addMouseListener(new VersionMouseAdapter(jt, install, uninstall, t));
        install.addActionListener(e -> {
            int row = jt.getSelectedRow();
            if (row >= 0) {
                Version version = (Version)versions.get(row);
                handler.install(version);
            }
        });
        uninstall.addActionListener(e -> {
            int row = jt.getSelectedRow();
            if (row >= 0) {
                Version version = (Version)versions.get(row);
                handler.uninstall(version);
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(install);
        buttonPanel.add(uninstall);
        buttonPanel.add(refresh);
        JButton installAll = new JButton("Install-All");
        installAll.addActionListener(e -> {
            for (Version version : versions) {
                if (!VersionUtil.hasLaunchWrapper(version) || VersionUtil.hasEarthhack(version) || !handler.install(version)) continue;
                return;
            }
        });
        JButton uninstallAll = new JButton("Uninstall-All");
        uninstallAll.addActionListener(e -> {
            for (Version version : versions) {
                if (!VersionUtil.hasEarthhack(version) || !handler.uninstall(version)) continue;
                return;
            }
        });
        JButton updateForge = new JButton("Update-Forge");
        updateForge.addActionListener(e -> handler.update(true));
        JButton updateVanilla = new JButton("Update-Vanilla");
        updateVanilla.addActionListener(e -> handler.update(false));
        JPanel allPanel = new JPanel();
        allPanel.add(installAll);
        allPanel.add(uninstallAll);
        allPanel.add(updateForge);
        allPanel.add(updateVanilla);
        JButton exit = new JButton("Exit");
        exit.addActionListener(e -> System.exit(0));
        JPanel exitPanel = new JPanel();
        exitPanel.add(exit);
        this.setLayout(new BoxLayout(this, 1));
        this.add(new JScrollPane(jt));
        this.add(buttonPanel);
        this.add(allPanel);
        this.add(exitPanel);
    }
}
