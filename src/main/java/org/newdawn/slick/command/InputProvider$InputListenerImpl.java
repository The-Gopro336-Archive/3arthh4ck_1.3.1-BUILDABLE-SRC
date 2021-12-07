package org.newdawn.slick.command;

import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.ControllerButtonControl;
import org.newdawn.slick.command.ControllerDirectionControl;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.command.MouseButtonControl;
import org.newdawn.slick.util.InputAdapter;

class InputProvider$InputListenerImpl
extends InputAdapter {
    private InputProvider$InputListenerImpl() {
    }

    @Override
    public boolean isAcceptingInput() {
        return true;
    }

    @Override
    public void keyPressed(int key, char c) {
        Command command = (Command)InputProvider.this.commands.get(new KeyControl(key));
        if (command != null) {
            InputProvider.this.firePressed(command);
        }
    }

    @Override
    public void keyReleased(int key, char c) {
        Command command = (Command)InputProvider.this.commands.get(new KeyControl(key));
        if (command != null) {
            InputProvider.this.fireReleased(command);
        }
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        Command command = (Command)InputProvider.this.commands.get(new MouseButtonControl(button));
        if (command != null) {
            InputProvider.this.firePressed(command);
        }
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        Command command = (Command)InputProvider.this.commands.get(new MouseButtonControl(button));
        if (command != null) {
            InputProvider.this.fireReleased(command);
        }
    }

    @Override
    public void controllerLeftPressed(int controller) {
        Command command = (Command)InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.LEFT));
        if (command != null) {
            InputProvider.this.firePressed(command);
        }
    }

    @Override
    public void controllerLeftReleased(int controller) {
        Command command = (Command)InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.LEFT));
        if (command != null) {
            InputProvider.this.fireReleased(command);
        }
    }

    @Override
    public void controllerRightPressed(int controller) {
        Command command = (Command)InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.RIGHT));
        if (command != null) {
            InputProvider.this.firePressed(command);
        }
    }

    @Override
    public void controllerRightReleased(int controller) {
        Command command = (Command)InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.RIGHT));
        if (command != null) {
            InputProvider.this.fireReleased(command);
        }
    }

    @Override
    public void controllerUpPressed(int controller) {
        Command command = (Command)InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.UP));
        if (command != null) {
            InputProvider.this.firePressed(command);
        }
    }

    @Override
    public void controllerUpReleased(int controller) {
        Command command = (Command)InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.UP));
        if (command != null) {
            InputProvider.this.fireReleased(command);
        }
    }

    @Override
    public void controllerDownPressed(int controller) {
        Command command = (Command)InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.DOWN));
        if (command != null) {
            InputProvider.this.firePressed(command);
        }
    }

    @Override
    public void controllerDownReleased(int controller) {
        Command command = (Command)InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.DOWN));
        if (command != null) {
            InputProvider.this.fireReleased(command);
        }
    }

    @Override
    public void controllerButtonPressed(int controller, int button) {
        Command command = (Command)InputProvider.this.commands.get(new ControllerButtonControl(controller, button));
        if (command != null) {
            InputProvider.this.firePressed(command);
        }
    }

    @Override
    public void controllerButtonReleased(int controller, int button) {
        Command command = (Command)InputProvider.this.commands.get(new ControllerButtonControl(controller, button));
        if (command != null) {
            InputProvider.this.fireReleased(command);
        }
    }
}
