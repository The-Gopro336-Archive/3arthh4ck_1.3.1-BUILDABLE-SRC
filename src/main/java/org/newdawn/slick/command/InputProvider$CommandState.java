package org.newdawn.slick.command;

class InputProvider$CommandState {
    private boolean down;
    private boolean pressed;

    private InputProvider$CommandState() {
    }

    public boolean isPressed() {
        if (this.pressed) {
            this.pressed = false;
            return true;
        }
        return false;
    }

    public boolean isDown() {
        return this.down;
    }

    static boolean access$202(InputProvider$CommandState x0, boolean x1) {
        x0.down = x1;
        return x0.down;
    }

    static boolean access$302(InputProvider$CommandState x0, boolean x1) {
        x0.pressed = x1;
        return x0.pressed;
    }
}
