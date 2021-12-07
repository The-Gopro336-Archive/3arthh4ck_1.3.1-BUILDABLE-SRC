package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.impl.event.events.render.ModelRenderEvent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

public class ModelRenderEvent$Post
extends ModelRenderEvent {
    private final float limbSwing;
    private final float limbSwingAmount;
    private final float ageInTicks;
    private final float netHeadYaw;
    private final float headPitch;
    private final float scale;

    public ModelRenderEvent$Post(RenderLivingBase<?> renderLiving, EntityLivingBase entity, ModelBase model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super(renderLiving, entity, model, null);
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.scale = scale;
    }

    public float getLimbSwing() {
        return this.limbSwing;
    }

    public float getLimbSwingAmount() {
        return this.limbSwingAmount;
    }

    public float getAgeInTicks() {
        return this.ageInTicks;
    }

    public float getNetHeadYaw() {
        return this.netHeadYaw;
    }

    public float getHeadPitch() {
        return this.headPitch;
    }

    public float getScale() {
        return this.scale;
    }
}
