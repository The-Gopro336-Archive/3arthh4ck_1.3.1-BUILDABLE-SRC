package me.earth.earthhack.impl.event.events.render;

public class PostRenderEntitiesEvent {
    private final float partialTicks;
    private final int pass;

    public PostRenderEntitiesEvent(float partialTicks, int pass) {
        this.partialTicks = partialTicks;
        this.pass = pass;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public int getPass() {
        return this.pass;
    }
}
