package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.impl.event.events.render.RenderEntityEvent;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

public class RenderEntityEvent$Post
extends RenderEntityEvent {
    public RenderEntityEvent$Post(Render<Entity> renderer, Entity entity) {
        super(renderer, entity, null);
    }
}
