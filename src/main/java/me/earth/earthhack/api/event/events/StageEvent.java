package me.earth.earthhack.api.event.events;

import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.api.event.events.Stage;

public class StageEvent
extends Event {
    private final Stage stage;

    public StageEvent(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return this.stage;
    }
}
