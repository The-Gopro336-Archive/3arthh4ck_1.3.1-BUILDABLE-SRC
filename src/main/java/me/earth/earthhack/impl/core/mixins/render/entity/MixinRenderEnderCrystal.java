package me.earth.earthhack.impl.core.mixins.render.entity;

import java.awt.Color;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.crystalchams.CrystalChams;
import me.earth.earthhack.impl.modules.render.crystalscale.CrystalScale;
import me.earth.earthhack.impl.modules.render.handchams.modes.ChamsMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RenderEnderCrystal.class})
public abstract class MixinRenderEnderCrystal {
    private static final ModuleCache<CrystalScale> SCALE = Caches.getModule(CrystalScale.class);
    private static final ModuleCache<CrystalChams> CHAMS = Caches.getModule(CrystalChams.class);

    @Redirect(method={"doRender"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderHook(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!SCALE.isPresent()) {
            return;
        }
        float crystalScale = ((CrystalScale)MixinRenderEnderCrystal.SCALE.get()).animate.getValue() != false ? (float)(((CrystalScale)MixinRenderEnderCrystal.SCALE.get()).scaleMap.containsKey(entityIn.getEntityId()) ? ((CrystalScale)MixinRenderEnderCrystal.SCALE.get()).scaleMap.get(entityIn.getEntityId()).getCurrent() : (double)0.1f) : ((CrystalScale)MixinRenderEnderCrystal.SCALE.get()).scale.getValue().floatValue();
        TimeAnimation animation = ((CrystalScale)MixinRenderEnderCrystal.SCALE.get()).scaleMap.get(entityIn.getEntityId());
        if (animation != null) {
            animation.add(Minecraft.getMinecraft().getRenderPartialTicks());
        }
        if (SCALE.isEnabled()) {
            GlStateManager.scale((float)crystalScale, (float)crystalScale, (float)crystalScale);
        }
        if (CHAMS.isEnabled()) {
            if (((CrystalChams)MixinRenderEnderCrystal.CHAMS.get()).mode.getValue() == ChamsMode.Gradient) {
                GL11.glPushAttrib(1048575);
                GL11.glEnable(3042);
                GL11.glDisable(2896);
                GL11.glDisable(3553);
                float alpha = (float)((CrystalChams)MixinRenderEnderCrystal.CHAMS.get()).color.getValue().getAlpha() / 255.0f;
                GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
                modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GL11.glEnable(3553);
                GL11.glBlendFunc(770, 771);
                float f = (float)entityIn.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/rainbow.png"));
                Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
                GlStateManager.enableBlend();
                GlStateManager.depthFunc((int)514);
                GlStateManager.depthMask((boolean)false);
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
                for (int i = 0; i < 2; ++i) {
                    GlStateManager.disableLighting();
                    GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
                    GlStateManager.matrixMode((int)5890);
                    GlStateManager.loadIdentity();
                    GlStateManager.scale((float)0.33333334f, (float)0.33333334f, (float)0.33333334f);
                    GlStateManager.rotate((float)(30.0f - (float)i * 60.0f), (float)0.0f, (float)0.0f, (float)0.5f);
                    GlStateManager.translate((float)0.0f, (float)(f * (0.001f + (float)i * 0.003f) * 20.0f), (float)0.0f);
                    GlStateManager.matrixMode((int)5888);
                    modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                }
                GlStateManager.matrixMode((int)5890);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode((int)5888);
                GlStateManager.enableLighting();
                GlStateManager.depthMask((boolean)true);
                GlStateManager.depthFunc((int)515);
                GlStateManager.disableBlend();
                Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
                GL11.glPopAttrib();
            } else {
                if (((CrystalChams)MixinRenderEnderCrystal.CHAMS.get()).wireframe.getValue().booleanValue()) {
                    Color wireColor = ((CrystalChams)MixinRenderEnderCrystal.CHAMS.get()).wireFrameColor.getValue();
                    GL11.glPushAttrib(1048575);
                    GL11.glEnable(3042);
                    GL11.glDisable(3553);
                    GL11.glDisable(2896);
                    GL11.glBlendFunc(770, 771);
                    GL11.glPolygonMode(1032, 6913);
                    if (((CrystalChams)MixinRenderEnderCrystal.CHAMS.get()).wireWalls.getValue().booleanValue()) {
                        GL11.glDepthMask(false);
                        GL11.glDisable(2929);
                    }
                    GL11.glColor4f((float)wireColor.getRed() / 255.0f, (float)wireColor.getGreen() / 255.0f, (float)wireColor.getBlue() / 255.0f, (float)wireColor.getAlpha() / 255.0f);
                    modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    GL11.glPopAttrib();
                }
                if (((CrystalChams)MixinRenderEnderCrystal.CHAMS.get()).chams.getValue().booleanValue()) {
                    Color chamsColor = ((CrystalChams)MixinRenderEnderCrystal.CHAMS.get()).color.getValue();
                    GL11.glPushAttrib(1048575);
                    GL11.glEnable(3042);
                    GL11.glDisable(3553);
                    GL11.glDisable(2896);
                    GL11.glDisable(3008);
                    GL11.glBlendFunc(770, 771);
                    GL11.glEnable(2960);
                    GL11.glEnable(10754);
                    if (((CrystalChams)MixinRenderEnderCrystal.CHAMS.get()).throughWalls.getValue().booleanValue()) {
                        GL11.glDepthMask(false);
                        GL11.glDisable(2929);
                    }
                    GL11.glColor4f((float)chamsColor.getRed() / 255.0f, (float)chamsColor.getGreen() / 255.0f, (float)chamsColor.getBlue() / 255.0f, (float)chamsColor.getAlpha() / 255.0f);
                    modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    GL11.glPopAttrib();
                }
            }
        } else {
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        if (SCALE.isEnabled()) {
            GlStateManager.scale((float)(1.0f / crystalScale), (float)(1.0f / crystalScale), (float)(1.0f / crystalScale));
        }
    }
}
