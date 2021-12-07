package org.newdawn.slick.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.newdawn.slick.Input;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.Control;
import org.newdawn.slick.command.ControllerButtonControl;
import org.newdawn.slick.command.ControllerDirectionControl;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.command.MouseButtonControl;
import org.newdawn.slick.util.InputAdapter;

public class InputProvider {
    private HashMap commands;
    private ArrayList listeners = new ArrayList();
    private Input input;
    private HashMap commandState = new HashMap();
    private boolean active = true;

    public InputProvider(Input input) {
        this.input = input;
        input.addListener(new InputListenerImpl());
        this.commands = new HashMap();
    }

    public List getUniqueCommands() {
        ArrayList<Command> uniqueCommands = new ArrayList<Command>();
        for (Command command : this.commands.values()) {
            if (uniqueCommands.contains(command)) continue;
            uniqueCommands.add(command);
        }
        return uniqueCommands;
    }

    public List getControlsFor(Command command) {
        ArrayList<Control> controlsForCommand = new ArrayList<Control>();
        for (Map.Entry entry : this.commands.entrySet()) {
            Control key = (Control)entry.getKey();
            Command value = (Command)entry.getValue();
            if (value != command) continue;
            controlsForCommand.add(key);
        }
        return controlsForCommand;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public void addListener(InputProviderListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(InputProviderListener listener) {
        this.listeners.remove(listener);
    }

    public void bindCommand(Control control, Command command) {
        this.commands.put(control, command);
        if (this.commandState.get(command) == null) {
            this.commandState.put(command, new CommandState());
        }
    }

    public void clearCommand(Command command) {
        List controls = this.getControlsFor(command);
        for (int i = 0; i < controls.size(); ++i) {
            this.unbindCommand((Control)controls.get(i));
        }
    }

    public void unbindCommand(Control control) {
        Command command = (Command)this.commands.remove(control);
        if (command != null && !this.commands.keySet().contains(command)) {
            this.commandState.remove(command);
        }
    }

    private CommandState getState(Command command) {
        return (CommandState)this.commandState.get(command);
    }

    public boolean isCommandControlDown(Command command) {
        return this.getState(command).isDown();
    }

    public boolean isCommandControlPressed(Command command) {
        return this.getState(command).isPressed();
    }

    protected void firePressed(Command command) {
        this.getState(command).down = true;
        this.getState(command).pressed = true;
        if (!this.isActive()) {
            return;
        }
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((InputProviderListener)this.listeners.get(i)).controlPressed(command);
        }
    }

    protected void fireReleased(Command command) {
        this.getState(command).down = false;
        if (!this.isActive()) {
            return;
        }
        for (int i = 0; i < this.listeners.size(); ++i) {
            ((InputProviderListener)this.listeners.get(i)).controlReleased(command);
        }
    }

    private class InputListenerImpl
    extends InputAdapter {
        private InputListenerImpl() {
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

    private class CommandState {
        private boolean down;
        private boolean pressed;

        private CommandState() {
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
    }
}
