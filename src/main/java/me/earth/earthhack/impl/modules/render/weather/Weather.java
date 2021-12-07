package me.earth.earthhack.impl.modules.render.weather;

import java.util.Random;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.render.entity.IEntityRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class Weather
extends Module {
    private static final Random RANDOM = new Random();
    private static final ResourceLocation RAIN_TEXTURES = new ResourceLocation("textures/environment/rain.png");
    private static final ResourceLocation SNOW_TEXTURES = new ResourceLocation("textures/environment/snow.png");
    protected final Setting<Boolean> snow = this.register(new BooleanSetting("Snow", true));
    protected final Setting<Integer> height = this.register(new NumberSetting<Integer>("Height", 80, 0, 255));
    protected final Setting<Float> strength = this.register(new NumberSetting<Float>("Strength", Float.valueOf(0.8f), Float.valueOf(0.1f), Float.valueOf(2.0f)));

    public Weather() {
        super("Weather", Category.Render);
    }

    public void render(float partialTicks) {
        float f = this.strength.getValue().floatValue();
        EntityRenderer renderer = Weather.mc.entityRenderer;
        renderer.enableLightmap();
        Entity entity = mc.getRenderViewEntity();
        if (entity == null) {
            return;
        }
        WorldClient world = Weather.mc.world;
        int i = MathHelper.floor((double)entity.posX);
        int j = MathHelper.floor((double)entity.posY);
        int k = MathHelper.floor((double)entity.posZ);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.disableCull();
        GlStateManager.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc((int)516, (float)0.1f);
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        int l = MathHelper.floor((double)d1);
        int i1 = 5;
        if (Weather.mc.gameSettings.fancyGraphics) {
            i1 = 10;
        }
        int j1 = -1;
        float f1 = (float)((IEntityRenderer)((Object)renderer)).getRendererUpdateCount() + partialTicks;
        bufferbuilder.setTranslation(-d0, -d1, -d2);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int k1 = k - i1; k1 <= k + i1; ++k1) {
            for (int l1 = i - i1; l1 <= i + i1; ++l1) {
                int i2 = (k1 - k + 16) * 32 + l1 - i + 16;
                double d3 = (double)((IEntityRenderer)((Object)renderer)).getRainXCoords()[i2] * 0.5;
                double d4 = (double)((IEntityRenderer)((Object)renderer)).getRainYCoords()[i2] * 0.5;
                blockpos$mutableblockpos.setPos(l1, 0, k1);
                Biome biome = world.getBiome(blockpos$mutableblockpos);
                int j2 = this.height.getValue();
                int k2 = j - i1;
                int l2 = j + i1;
                if (k2 < j2) {
                    k2 = j2;
                }
                if (l2 < j2) {
                    l2 = j2;
                }
                int i3 = j2;
                if (j2 < l) {
                    i3 = l;
                }
                if (k2 == l2) continue;
                RANDOM.setSeed(l1 * l1 * 3121 + l1 * 45238971 ^ k1 * k1 * 418711 + k1 * 13761);
                blockpos$mutableblockpos.setPos(l1, k2, k1);
                float f2 = biome.getTemperature(blockpos$mutableblockpos);
                if (!this.snow.getValue().booleanValue()) {
                    if (j1 != 0) {
                        if (j1 >= 0) {
                            tessellator.draw();
                        }
                        j1 = 0;
                        mc.getTextureManager().bindTexture(RAIN_TEXTURES);
                        bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                    }
                    double d5 = -((double)(((IEntityRenderer)((Object)renderer)).getRendererUpdateCount() + l1 * l1 * 3121 + l1 * 45238971 + k1 * k1 * 418711 + k1 * 13761 & 0x1F) + (double)partialTicks) / 32.0 * (3.0 + RANDOM.nextDouble());
                    double d6 = (double)((float)l1 + 0.5f) - entity.posX;
                    double d7 = (double)((float)k1 + 0.5f) - entity.posZ;
                    float f3 = MathHelper.sqrt((double)(d6 * d6 + d7 * d7)) / (float)i1;
                    float f4 = ((1.0f - f3 * f3) * 0.5f + 0.5f) * f;
                    blockpos$mutableblockpos.setPos(l1, i3, k1);
                    int j3 = world.getCombinedLight(blockpos$mutableblockpos, 0);
                    int k3 = j3 >> 16 & 0xFFFF;
                    int l3 = j3 & 0xFFFF;
                    bufferbuilder.pos((double)l1 - d3 + 0.5, l2, (double)k1 - d4 + 0.5).tex(0.0, (double)k2 * 0.25 + d5).color(1.0f, 1.0f, 1.0f, f4).lightmap(k3, l3).endVertex();
                    bufferbuilder.pos((double)l1 + d3 + 0.5, l2, (double)k1 + d4 + 0.5).tex(1.0, (double)k2 * 0.25 + d5).color(1.0f, 1.0f, 1.0f, f4).lightmap(k3, l3).endVertex();
                    bufferbuilder.pos((double)l1 + d3 + 0.5, k2, (double)k1 + d4 + 0.5).tex(1.0, (double)l2 * 0.25 + d5).color(1.0f, 1.0f, 1.0f, f4).lightmap(k3, l3).endVertex();
                    bufferbuilder.pos((double)l1 - d3 + 0.5, k2, (double)k1 - d4 + 0.5).tex(0.0, (double)l2 * 0.25 + d5).color(1.0f, 1.0f, 1.0f, f4).lightmap(k3, l3).endVertex();
                    continue;
                }
                if (j1 != 1) {
                    if (j1 >= 0) {
                        tessellator.draw();
                    }
                    j1 = 1;
                    mc.getTextureManager().bindTexture(SNOW_TEXTURES);
                    bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                }
                double d8 = -((float)(((IEntityRenderer)((Object)renderer)).getRendererUpdateCount() & 0x1FF) + partialTicks) / 512.0f;
                double d9 = RANDOM.nextDouble() + (double)f1 * 0.01 * (double)((float)RANDOM.nextGaussian());
                double d10 = RANDOM.nextDouble() + (double)(f1 * (float)RANDOM.nextGaussian()) * 0.001;
                double d11 = (double)((float)l1 + 0.5f) - entity.posX;
                double d12 = (double)((float)k1 + 0.5f) - entity.posZ;
                float f6 = MathHelper.sqrt((double)(d11 * d11 + d12 * d12)) / (float)i1;
                float f5 = ((1.0f - f6 * f6) * 0.3f + 0.5f) * f;
                blockpos$mutableblockpos.setPos(l1, i3, k1);
                int i4 = (world.getCombinedLight(blockpos$mutableblockpos, 0) * 3 + 0xF000F0) / 4;
                int j4 = i4 >> 16 & 0xFFFF;
                int k4 = i4 & 0xFFFF;
                bufferbuilder.pos((double)l1 - d3 + 0.5, l2, (double)k1 - d4 + 0.5).tex(0.0 + d9, (double)k2 * 0.25 + d8 + d10).color(1.0f, 1.0f, 1.0f, f5).lightmap(j4, k4).endVertex();
                bufferbuilder.pos((double)l1 + d3 + 0.5, l2, (double)k1 + d4 + 0.5).tex(1.0 + d9, (double)k2 * 0.25 + d8 + d10).color(1.0f, 1.0f, 1.0f, f5).lightmap(j4, k4).endVertex();
                bufferbuilder.pos((double)l1 + d3 + 0.5, k2, (double)k1 + d4 + 0.5).tex(1.0 + d9, (double)l2 * 0.25 + d8 + d10).color(1.0f, 1.0f, 1.0f, f5).lightmap(j4, k4).endVertex();
                bufferbuilder.pos((double)l1 - d3 + 0.5, k2, (double)k1 - d4 + 0.5).tex(0.0 + d9, (double)l2 * 0.25 + d8 + d10).color(1.0f, 1.0f, 1.0f, f5).lightmap(j4, k4).endVertex();
            }
        }
        if (j1 >= 0) {
            tessellator.draw();
        }
        bufferbuilder.setTranslation(0.0, 0.0, 0.0);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc((int)516, (float)0.1f);
        renderer.disableLightmap();
    }
}
