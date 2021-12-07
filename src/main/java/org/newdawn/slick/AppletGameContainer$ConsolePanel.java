package org.newdawn.slick;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AppletGameContainer$ConsolePanel
extends Panel {
    TextArea textArea = new TextArea();

    public AppletGameContainer$ConsolePanel(Exception e) {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.black);
        this.setForeground(Color.white);
        Font consoleFont = new Font("Arial", 1, 14);
        Label slickLabel = new Label("SLICK CONSOLE", 1);
        slickLabel.setFont(consoleFont);
        this.add((Component)slickLabel, "First");
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        this.textArea.setText(sw.toString());
        this.textArea.setEditable(false);
        this.add((Component)this.textArea, "Center");
        this.add((Component)new Panel(), "Before");
        this.add((Component)new Panel(), "After");
        Panel bottomPanel = new Panel();
        bottomPanel.setLayout(new GridLayout(0, 1));
        Label infoLabel1 = new Label("An error occured while running the applet.", 1);
        Label infoLabel2 = new Label("Plese contact support to resolve this issue.", 1);
        infoLabel1.setFont(consoleFont);
        infoLabel2.setFont(consoleFont);
        bottomPanel.add(infoLabel1);
        bottomPanel.add(infoLabel2);
        this.add((Component)bottomPanel, "Last");
    }
}
