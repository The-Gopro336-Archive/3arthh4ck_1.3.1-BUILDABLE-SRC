package me.earth.earthhack.impl.core.mixins.render;

import java.util.List;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.render.RenderLayersEvent;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderLivingBase.class})
public abstract class MixinRenderLivingBase {
    @Shadow
    protected List<LayerRenderer<?>> field_177097_h;

    @Inject(method={"renderLayers"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderLayersPreHook(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo ci) {
        RenderLayersEvent pre = new RenderLayersEvent((Render)Render.class.cast(this), entitylivingbaseIn, this.field_177097_h, Stage.PRE);
        Bus.EVENT_BUS.post(pre);
        if (pre.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method={"renderLayers"}, at={@At(value="RETURN")})
    public void renderLayersPostHook(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo ci) {
        RenderLayersEvent post = new RenderLayersEvent((Render)Render.class.cast(this), entitylivingbaseIn, this.field_177097_h, Stage.POST);
        Bus.EVENT_BUS.post(post);
    }
}
