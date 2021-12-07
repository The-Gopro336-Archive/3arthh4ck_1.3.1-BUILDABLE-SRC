package org.newdawn.slick.gui;

import org.lwjgl.Sys;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

public class TextField
extends AbstractComponent {
    private static final int INITIAL_KEY_REPEAT_INTERVAL = 400;
    private static final int KEY_REPEAT_INTERVAL = 50;
    private int width;
    private int height;
    protected int x;
    protected int y;
    private int maxCharacter = 10000;
    private String value = "";
    private Font font;
    private Color border = Color.white;
    private Color text = Color.white;
    private Color background = new Color(0.0f, 0.0f, 0.0f, 0.5f);
    private int cursorPos;
    private boolean visibleCursor = true;
    private int lastKey = -1;
    private char lastChar = '\u0000';
    private long repeatTimer;
    private String oldText;
    private int oldCursorPos;
    private boolean consume = true;

    public TextField(GUIContext container, Font font, int x, int y, int width, int height, ComponentListener listener) {
        this(container, font, x, y, width, height);
        this.addListener(listener);
    }

    public TextField(GUIContext container, Font font, int x, int y, int width, int height) {
        super(container);
        this.font = font;
        this.setLocation(x, y);
        this.width = width;
        this.height = height;
    }

    public void setConsumeEvents(boolean consume) {
        this.consume = consume;
    }

    public void deactivate() {
        this.setFocus(false);
    }

    @Override
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public void setBackgroundColor(Color color) {
        this.background = color;
    }

    public void setBorderColor(Color color) {
        this.border = color;
    }

    public void setTextColor(Color color) {
        this.text = color;
    }

    @Override
    public void render(GUIContext container, Graphics g) {
        if (this.lastKey != -1) {
            if (this.input.isKeyDown(this.lastKey)) {
                if (this.repeatTimer < System.currentTimeMillis()) {
                    this.repeatTimer = System.currentTimeMillis() + 50L;
                    this.keyPressed(this.lastKey, this.lastChar);
                }
            } else {
                this.lastKey = -1;
            }
        }
        Rectangle oldClip = g.getClip();
        g.setWorldClip(this.x, this.y, this.width, this.height);
        Color clr = g.getColor();
        if (this.background != null) {
            g.setColor(this.background.multiply(clr));
            g.fillRect(this.x, this.y, this.width, this.height);
        }
        g.setColor(this.text.multiply(clr));
        Font temp = g.getFont();
        int cpos = this.font.getWidth(this.value.substring(0, this.cursorPos));
        int tx = 0;
        if (cpos > this.width) {
            tx = this.width - cpos - this.font.getWidth("_");
        }
        g.translate(tx + 2, 0.0f);
        g.setFont(this.font);
        g.drawString(this.value, this.x + 1, this.y + 1);
        if (this.hasFocus() && this.visibleCursor) {
            g.drawString("_", this.x + 1 + cpos + 2, this.y + 1);
        }
        g.translate(-tx - 2, 0.0f);
        if (this.border != null) {
            g.setColor(this.border.multiply(clr));
            g.drawRect(this.x, this.y, this.width, this.height);
        }
        g.setColor(clr);
        g.setFont(temp);
        g.clearWorldClip();
        g.setClip(oldClip);
    }

    public String getText() {
        return this.value;
    }

    public void setText(String value) {
        this.value = value;
        if (this.cursorPos > value.length()) {
            this.cursorPos = value.length();
        }
    }

    public void setCursorPos(int pos) {
        this.cursorPos = pos;
        if (this.cursorPos > this.value.length()) {
            this.cursorPos = this.value.length();
        }
    }

    public void setCursorVisible(boolean visibleCursor) {
        this.visibleCursor = visibleCursor;
    }

    public void setMaxLength(int length) {
        this.maxCharacter = length;
        if (this.value.length() > this.maxCharacter) {
            this.value = this.value.substring(0, this.maxCharacter);
        }
    }

    protected void doPaste(String text) {
        this.recordOldPosition();
        for (int i = 0; i < text.length(); ++i) {
            this.keyPressed(-1, text.charAt(i));
        }
    }

    protected void recordOldPosition() {
        this.oldText = this.getText();
        this.oldCursorPos = this.cursorPos;
    }

    protected void doUndo(int oldCursorPos, String oldText) {
        if (oldText != null) {
            this.setText(oldText);
            this.setCursorPos(oldCursorPos);
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        if (this.hasFocus()) {
            if (key != -1) {
                if (key == 47 && (this.input.isKeyDown(29) || this.input.isKeyDown(157))) {
                    String text = Sys.getClipboard();
                    if (text != null) {
                        this.doPaste(text);
                    }
                    return;
                }
                if (key == 44 && (this.input.isKeyDown(29) || this.input.isKeyDown(157))) {
                    if (this.oldText != null) {
                        this.doUndo(this.oldCursorPos, this.oldText);
                    }
                    return;
                }
                if (this.input.isKeyDown(29) || this.input.isKeyDown(157)) {
                    return;
                }
                if (this.input.isKeyDown(56) || this.input.isKeyDown(184)) {
                    return;
                }
            }
            if (this.lastKey != key) {
                this.lastKey = key;
                this.repeatTimer = System.currentTimeMillis() + 400L;
            } else {
                this.repeatTimer = System.currentTimeMillis() + 50L;
            }
            this.lastChar = c;
            if (key == 203) {
                if (this.cursorPos > 0) {
                    --this.cursorPos;
                }
                if (this.consume) {
                    this.container.getInput().consumeEvent();
                }
            } else if (key == 205) {
                if (this.cursorPos < this.value.length()) {
                    ++this.cursorPos;
                }
                if (this.consume) {
                    this.container.getInput().consumeEvent();
                }
            } else if (key == 14) {
                if (this.cursorPos > 0 && this.value.length() > 0) {
                    this.value = this.cursorPos < this.value.length() ? this.value.substring(0, this.cursorPos - 1) + this.value.substring(this.cursorPos) : this.value.substring(0, this.cursorPos - 1);
                    --this.cursorPos;
                }
                if (this.consume) {
                    this.container.getInput().consumeEvent();
                }
            } else if (key == 211) {
                if (this.value.length() > this.cursorPos) {
                    this.value = this.value.substring(0, this.cursorPos) + this.value.substring(this.cursorPos + 1);
                }
                if (this.consume) {
                    this.container.getInput().consumeEvent();
                }
            } else if (c < '\u007f' && c > '\u001f' && this.value.length() < this.maxCharacter) {
                this.value = this.cursorPos < this.value.length() ? this.value.substring(0, this.cursorPos) + c + this.value.substring(this.cursorPos) : this.value.substring(0, this.cursorPos) + c;
                ++this.cursorPos;
                if (this.consume) {
                    this.container.getInput().consumeEvent();
                }
            } else if (key == 28) {
                this.notifyListeners();
                if (this.consume) {
                    this.container.getInput().consumeEvent();
                }
            }
        }
    }

    @Override
    public void setFocus(boolean focus) {
        this.lastKey = -1;
        super.setFocus(focus);
    }
}
