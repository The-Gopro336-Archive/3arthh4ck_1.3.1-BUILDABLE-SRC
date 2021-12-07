/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.renderer.entity.layers.LayerArmorBase
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  org.lwjgl.opengl.GL11
 */
package me.earth.earthhack.impl.core.mixins.render;

import java.awt.Color;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.chams.Chams;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LayerArmorBase.class})
public abstract class MixinLayerArmorBase {
    private static final ModuleCache<Chams> CHAMS = Caches.getModule(Chams.class);
    private static final ModuleCache<NoRender> NO_RENDER = Caches.getModule(NoRender.class);

    @Redirect(method={"renderArmorLayer"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderArmorHook(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entityIn instanceof EntityLivingBase) {
            Color color = ((Chams)CHAMS.get()).getArmorVisibleColor(entityIn);
            if (((Chams)CHAMS.get()).shouldArmorChams() && CHAMS.isEnabled()) {
                GL11.glPushMatrix();
                GL11.glEnable((int)32823);
                GL11.glPolygonOffset((float)1.0f, (float)-2000000.0f);
                GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            }
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            if (((Chams)CHAMS.get()).shouldArmorChams() && CHAMS.isEnabled()) {
                GL11.glPolygonOffset((float)1.0f, (float)2000000.0f);
                GL11.glDisable((int)32823);
                GL11.glPopMatrix();
            }
        }
    }

    @Inject(method={"renderArmorLayer"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo ci) {
        if (NO_RENDER.returnIfPresent(m -> !m.isValidArmorPiece(slotIn), false).booleanValue()) {
            ci.cancel();
        }
    }
}

