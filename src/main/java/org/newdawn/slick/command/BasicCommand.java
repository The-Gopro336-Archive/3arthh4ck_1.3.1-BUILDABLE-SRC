/*
 * Decompiled with CFR 0.150.
 */
package org.newdawn.slick.command;

import org.newdawn.slick.command.Command;

public class BasicCommand
implements Command {
    private String name;

    public BasicCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object other) {
        if (other instanceof BasicCommand) {
            return ((BasicCommand)other).name.equals(this.name);
        }
        return false;
    }

    public String toString() {
        return "[Command=" + this.name + "]";
    }
}

