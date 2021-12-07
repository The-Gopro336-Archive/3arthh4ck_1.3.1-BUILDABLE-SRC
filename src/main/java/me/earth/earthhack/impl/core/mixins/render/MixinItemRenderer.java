/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.AbstractClientPlayer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.client.renderer.entity.RenderPlayer
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumHandSide
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package me.earth.earthhack.impl.core.mixins.render;

import java.awt.Color;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.handchams.HandChams;
import me.earth.earthhack.impl.modules.render.handchams.modes.ChamsMode;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import me.earth.earthhack.impl.modules.render.viewmodel.ViewModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemRenderer.class})
public abstract class MixinItemRenderer {
    private static final ResourceLocation RESOURCE = new ResourceLocation("textures/rainbow.png");
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final ModuleCache<ViewModel> VIEW_MODEL = Caches.getModule(ViewModel.class);
    private static final ModuleCache<NoRender> NO_RENDER = Caches.getModule(NoRender.class);
    private static final ModuleCache<HandChams> HAND_CHAMS = Caches.getModule(HandChams.class);

    @Shadow
    protected abstract void renderArmFirstPerson(float var1, float var2, EnumHandSide var3);

    @Inject(method={"renderFireInFirstPerson"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderFireInFirstPersonHook(CallbackInfo info) {
        if (NO_RENDER.returnIfPresent(NoRender::noFire, false).booleanValue()) {
            info.cancel();
        }
    }

    @Redirect(method={"renderItemInFirstPerson(F)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ItemRenderer;renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"))
    private void renderItemInFirstPersonHook(ItemRenderer itemRenderer, AbstractClientPlayer player, float drinkOffset, float mapAngle, EnumHand hand, float x, ItemStack stack, float y) {
        float xOffset = VIEW_MODEL.isPresent() ? ((ViewModel)VIEW_MODEL.get()).getX(hand) : 0.0f;
        float yOffset = VIEW_MODEL.isPresent() ? ((ViewModel)VIEW_MODEL.get()).getY(hand) : 0.0f;
        itemRenderer.renderItemInFirstPerson(player, drinkOffset, mapAngle, hand, x + xOffset, stack, y + yOffset);
    }

    @Inject(method={"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", shift=At.Shift.AFTER)})
    private void pushMatrixHook(CallbackInfo info) {
        if (VIEW_MODEL.isEnabled()) {
            float[] scale = VIEW_MODEL.isPresent() ? ((ViewModel)VIEW_MODEL.get()).getScale() : ViewModel.DEFAULT_SCALE;
            float[] translation = VIEW_MODEL.isPresent() ? ((ViewModel)VIEW_MODEL.get()).getTranslation() : ViewModel.DEFAULT_TRANSLATION;
            GL11.glScalef((float)scale[0], (float)scale[1], (float)scale[2]);
            GL11.glRotatef((float)translation[0], (float)translation[1], (float)translation[2], (float)translation[3]);
        }
    }

    @Redirect(method={"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ItemRenderer;renderArmFirstPerson(FFLnet/minecraft/util/EnumHandSide;)V"))
    public void mrHook(ItemRenderer itemRenderer, float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_) {
        if (HAND_CHAMS.isEnabled()) {
            if (((HandChams)MixinItemRenderer.HAND_CHAMS.get()).mode.getValue() == ChamsMode.Normal) {
                Color handColor;
                if (((HandChams)MixinItemRenderer.HAND_CHAMS.get()).chams.getValue().booleanValue()) {
                    handColor = ((HandChams)MixinItemRenderer.HAND_CHAMS.get()).color.getValue();
                    GL11.glPushMatrix();
                    GL11.glPushAttrib((int)1048575);
                    GL11.glDisable((int)3553);
                    GL11.glDisable((int)2896);
                    GL11.glEnable((int)3042);
                    GL11.glBlendFunc((int)770, (int)771);
                    GL11.glColor4f((float)((float)handColor.getRed() / 255.0f), (float)((float)handColor.getGreen() / 255.0f), (float)((float)handColor.getBlue() / 255.0f), (float)((float)handColor.getAlpha() / 255.0f));
                    this.renderArmFirstPerson(p_187456_1_, p_187456_2_, p_187456_3_);
                    GL11.glDisable((int)3042);
                    GL11.glEnable((int)3553);
                    GL11.glPopAttrib();
                    GL11.glPopMatrix();
                }
                if (((HandChams)MixinItemRenderer.HAND_CHAMS.get()).wireframe.getValue().booleanValue()) {
                    handColor = ((HandChams)MixinItemRenderer.HAND_CHAMS.get()).wireFrameColor.getValue();
                    GL11.glPushMatrix();
                    GL11.glPushAttrib((int)1048575);
                    GL11.glDisable((int)3553);
                    GL11.glDisable((int)2896);
                    GL11.glEnable((int)3042);
                    GL11.glPolygonMode((int)1032, (int)6913);
                    GL11.glBlendFunc((int)770, (int)771);
                    GL11.glLineWidth((float)1.5f);
                    GL11.glDisable((int)2929);
                    GL11.glDepthMask((boolean)false);
                    GL11.glColor4f((float)((float)handColor.getRed() / 255.0f), (float)((float)handColor.getGreen() / 255.0f), (float)((float)handColor.getBlue() / 255.0f), (float)((float)handColor.getAlpha() / 255.0f));
                    this.renderArmFirstPerson(p_187456_1_, p_187456_2_, p_187456_3_);
                    GL11.glDisable((int)3042);
                    GL11.glEnable((int)3553);
                    GL11.glPopAttrib();
                    GL11.glPopMatrix();
                }
            } else {
                GL11.glPushAttrib((int)1048575);
                GL11.glDisable((int)2896);
                GL11.glEnable((int)3042);
                GL11.glDisable((int)3008);
                GL11.glBlendFunc((int)770, (int)771);
                GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)0.5f);
                GL11.glDisable((int)3553);
                GL11.glEnable((int)10754);
                GL11.glEnable((int)2960);
                this.renderArmFirstPerson(p_187456_1_, p_187456_2_, p_187456_3_);
                GL11.glEnable((int)3553);
                this.renderEffect(p_187456_1_, p_187456_2_, p_187456_3_);
                GL11.glPopAttrib();
            }
        } else {
            this.renderArmFirstPerson(p_187456_1_, p_187456_2_, p_187456_3_);
        }
    }

    private void renderEffect(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_) {
        float f = (float)Minecraft.getMinecraft().player.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
        Minecraft.getMinecraft().getTextureManager().bindTexture(RESOURCE);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc((int)514);
        GlStateManager.depthMask((boolean)false);
        float f1 = 0.5f;
        GlStateManager.color((float)0.5f, (float)0.5f, (float)0.5f, (float)0.5f);
        for (int i = 0; i < 2; ++i) {
            GlStateManager.disableLighting();
            float f2 = 0.76f;
            GlStateManager.color((float)0.38f, (float)0.19f, (float)0.608f, (float)0.5f);
            GlStateManager.matrixMode((int)5890);
            GlStateManager.loadIdentity();
            float f3 = 0.33333334f;
            GlStateManager.scale((float)0.33333334f, (float)0.33333334f, (float)0.33333334f);
            GlStateManager.rotate((float)(30.0f - (float)i * 60.0f), (float)0.0f, (float)0.0f, (float)1.0f);
            GlStateManager.translate((float)0.0f, (float)(f * (0.001f + (float)i * 0.003f) * 20.0f), (float)0.0f);
            GlStateManager.matrixMode((int)5888);
            RenderPlayer renderPlayer = (RenderPlayer)Minecraft.getMinecraft().getRenderManager().getEntityRenderObject((Entity)Minecraft.getMinecraft().player);
            if (renderPlayer == null) continue;
            if (p_187456_3_ == EnumHandSide.RIGHT) {
                renderPlayer.renderRightArm((AbstractClientPlayer)Minecraft.getMinecraft().player);
                continue;
            }
            renderPlayer.renderLeftArm((AbstractClientPlayer)Minecraft.getMinecraft().player);
        }
        GlStateManager.matrixMode((int)5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode((int)5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.depthFunc((int)515);
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
    }
}

