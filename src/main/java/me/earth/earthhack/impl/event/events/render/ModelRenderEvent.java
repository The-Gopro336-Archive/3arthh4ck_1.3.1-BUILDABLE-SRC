package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

public class ModelRenderEvent
extends Event {
    private final RenderLivingBase<?> renderLiving;
    private final EntityLivingBase entity;
    private final ModelBase model;

    private ModelRenderEvent(RenderLivingBase<?> renderLiving, EntityLivingBase entity, ModelBase model) {
        this.renderLiving = renderLiving;
        this.entity = entity;
        this.model = model;
    }

    public RenderLivingBase<?> getRenderLiving() {
        return this.renderLiving;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }

    public ModelBase getModel() {
        return this.model;
    }

    public static class Post
    extends ModelRenderEvent {
        private final float limbSwing;
        private final float limbSwingAmount;
        private final float ageInTicks;
        private final float netHeadYaw;
        private final float headPitch;
        private final float scale;

        public Post(RenderLivingBase<?> renderLiving, EntityLivingBase entity, ModelBase model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            super(renderLiving, entity, model);
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

    public static class Pre
    extends ModelRenderEvent {
        private final float limbSwing;
        private final float limbSwingAmount;
        private final float ageInTicks;
        private final float netHeadYaw;
        private final float headPitch;
        private final float scale;

        public Pre(RenderLivingBase<?> renderLiving, EntityLivingBase entity, ModelBase model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            super(renderLiving, entity, model);
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
}
