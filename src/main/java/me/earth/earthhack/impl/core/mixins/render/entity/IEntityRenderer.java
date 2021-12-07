/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.EntityRenderer
 */
package me.earth.earthhack.impl.core.mixins.render.entity;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={EntityRenderer.class})
public interface IEntityRenderer {
    @Accessor(value="rendererUpdateCount")
    public int getRendererUpdateCount();

    @Accessor(value="rainXCoords")
    public float[] getRainXCoords();

    @Accessor(value="rainYCoords")
    public float[] getRainYCoords();

    @Accessor(value="farPlaneDistance")
    public float getFarPlaneDistance();

    @Accessor(value="fovModifierHandPrev")
    public float getFovModifierHandPrev();

    @Accessor(value="fovModifierHand")
    public float getFovModifierHand();

    @Accessor(value="debugView")
    public boolean isDebugView();
}

