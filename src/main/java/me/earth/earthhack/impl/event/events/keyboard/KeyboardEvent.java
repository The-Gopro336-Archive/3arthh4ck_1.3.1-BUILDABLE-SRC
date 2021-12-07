package me.earth.earthhack.impl.event.events.keyboard;

public class KeyboardEvent {
    private final boolean eventState;
    private final char character;
    private final int key;

    public KeyboardEvent(boolean eventState, int key, char character) {
        this.eventState = eventState;
        this.key = key;
        this.character = character;
    }

    public boolean getEventState() {
        return this.eventState;
    }

    public int getKey() {
        return this.key;
    }

    public char getCharacter() {
        return this.character;
    }

    public static class Post {
    }
}
