package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.impl.event.events.render.RenderEntityEvent;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

public class RenderEntityEvent$Pre
extends RenderEntityEvent {
    private final double posX;
    private final double posY;
    private final double posZ;
    private final float entityYaw;
    private final float partialTicks;

    public RenderEntityEvent$Pre(Render<Entity> renderer, Entity entity, double posX, double posY, double posZ, float entityYaw, float partialTicks) {
        super(renderer, entity, null);
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.entityYaw = entityYaw;
        this.partialTicks = partialTicks;
    }

    public double getPosX() {
        return this.posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public double getPosZ() {
        return this.posZ;
    }

    public float getEntityYaw() {
        return this.entityYaw;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}
