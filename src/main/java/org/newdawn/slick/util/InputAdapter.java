package org.newdawn.slick.util;

import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;

public class InputAdapter
implements InputListener {
    private boolean acceptingInput = true;

    @Override
    public void controllerButtonPressed(int controller, int button) {
    }

    @Override
    public void controllerButtonReleased(int controller, int button) {
    }

    @Override
    public void controllerDownPressed(int controller) {
    }

    @Override
    public void controllerDownReleased(int controller) {
    }

    @Override
    public void controllerLeftPressed(int controller) {
    }

    @Override
    public void controllerLeftReleased(int controller) {
    }

    @Override
    public void controllerRightPressed(int controller) {
    }

    @Override
    public void controllerRightReleased(int controller) {
    }

    @Override
    public void controllerUpPressed(int controller) {
    }

    @Override
    public void controllerUpReleased(int controller) {
    }

    @Override
    public void inputEnded() {
    }

    @Override
    public boolean isAcceptingInput() {
        return this.acceptingInput;
    }

    public void setAcceptingInput(boolean acceptingInput) {
        this.acceptingInput = acceptingInput;
    }

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
    public void mousePressed(int button, int x, int y) {
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
    }

    @Override
    public void mouseWheelMoved(int change) {
    }

    @Override
    public void setInput(Input input) {
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
    }

    @Override
    public void inputStarted() {
    }
}
