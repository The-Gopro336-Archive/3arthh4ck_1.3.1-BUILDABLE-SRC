/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick;

import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;

public abstract class BasicGame
implements Game,
InputListener {
    private static final int MAX_CONTROLLERS = 20;
    private static final int MAX_CONTROLLER_BUTTONS = 100;
    private String title;
    protected boolean[] controllerLeft = new boolean[20];
    protected boolean[] controllerRight = new boolean[20];
    protected boolean[] controllerUp = new boolean[20];
    protected boolean[] controllerDown = new boolean[20];
    protected boolean[][] controllerButton = new boolean[20][100];

    public BasicGame(String title) {
        this.title = title;
    }

    @Override
    public void setInput(Input input) {
    }

    @Override
    public boolean closeRequested() {
        return true;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public abstract void init(GameContainer var1) throws SlickException;

    @Override
    public void keyPressed(int key, char c) {
    }

    @Override
    public void keyReleased(int key, char c) {
    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
    }

    @Override
    public void mousePressed(int button, int x, int y) {
    }

    @Override
    public void controllerButtonPressed(int controller, int button) {
        this.controllerButton[controller][button] = true;
    }

    @Override
    public void controllerButtonReleased(int controller, int button) {
        this.controllerButton[controller][button] = false;
    }

    @Override
    public void controllerDownPressed(int controller) {
        this.controllerDown[controller] = true;
    }

    @Override
    public void controllerDownReleased(int controller) {
        this.controllerDown[controller] = false;
    }

    @Override
    public void controllerLeftPressed(int controller) {
        this.controllerLeft[controller] = true;
    }

    @Override
    public void controllerLeftReleased(int controller) {
        this.controllerLeft[controller] = false;
    }

    @Override
    public void controllerRightPressed(int controller) {
        this.controllerRight[controller] = true;
    }

    @Override
    public void controllerRightReleased(int controller) {
        this.controllerRight[controller] = false;
    }

    @Override
    public void controllerUpPressed(int controller) {
        this.controllerUp[controller] = true;
    }

    @Override
    public void controllerUpReleased(int controller) {
        this.controllerUp[controller] = false;
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
    }

    @Override
    public abstract void update(GameContainer var1, int var2) throws SlickException;

    @Override
    public void mouseWheelMoved(int change) {
    }

    @Override
    public boolean isAcceptingInput() {
        return true;
    }

    @Override
    public void inputEnded() {
    }

    @Override
    public void inputStarted() {
    }
}

