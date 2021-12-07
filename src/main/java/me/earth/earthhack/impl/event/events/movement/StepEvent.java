package me.earth.earthhack.impl.event.events.movement;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.event.events.StageEvent;
import net.minecraft.util.math.AxisAlignedBB;

public class StepEvent
extends StageEvent {
    private final AxisAlignedBB bb;
    private float height;

    public StepEvent(Stage stage, AxisAlignedBB bb, float height) {
        super(stage);
        this.height = height;
        this.bb = bb;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        if (this.getStage() == Stage.PRE) {
            this.height = height;
        }
    }

    public AxisAlignedBB getBB() {
        return this.bb;
    }
}
