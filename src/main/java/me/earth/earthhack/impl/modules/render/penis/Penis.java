/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.Cylinder
 *  org.lwjgl.util.glu.Sphere
 */
package me.earth.earthhack.impl.modules.render.penis;

import java.awt.Color;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.penis.ListenerRender;
import me.earth.earthhack.impl.modules.render.penis.PenisData;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Sphere;

public class Penis
extends Module {
    protected final Setting<Float> selfLength = this.register(new NumberSetting<Float>("SelfLength", Float.valueOf(0.8f), Float.valueOf(0.1f), Float.valueOf(2.0f)));
    protected final Setting<Float> friendLength = this.register(new NumberSetting<Float>("FriendLength", Float.valueOf(0.8f), Float.valueOf(0.1f), Float.valueOf(2.0f)));
    protected final Setting<Float> enemyLength = this.register(new NumberSetting<Float>("EnemyLength", Float.valueOf(0.4f), Float.valueOf(0.1f), Float.valueOf(2.0f)));
    protected final Setting<Boolean> uncircumcised = this.register(new BooleanSetting("Uncircumcised", false));
    public final Setting<Color> selfShaftColor = this.register(new ColorSetting("SelfShaftColor", new Color(95, 67, 63, 255)));
    public final Setting<Color> selfTipColor = this.register(new ColorSetting("SelfTipColor", new Color(160, 99, 98, 255)));
    public final Setting<Color> friendShaftColor = this.register(new ColorSetting("FriendShaftColor", new Color(95, 67, 63, 255)));
    public final Setting<Color> friendTipColor = this.register(new ColorSetting("FriendTipColor", new Color(160, 99, 98, 255)));
    public final Setting<Color> enemyShaftColor = this.register(new ColorSetting("EnemyShaftColor", new Color(95, 67, 63, 255)));
    public final Setting<Color> enemyTipColor = this.register(new ColorSetting("EnemyTipColor", new Color(160, 99, 98, 255)));
    private final Cylinder shaft = new Cylinder();
    private final Sphere ball = new Sphere();
    private final Sphere tip = new Sphere();

    public Penis() {
        super("Penis", Category.Render);
        this.listeners.add(new ListenerRender(this));
        this.setData(new PenisData(this));
        this.shaft.setDrawStyle(100012);
        this.ball.setDrawStyle(100012);
        this.tip.setDrawStyle(100012);
    }

    protected void onRender3D() {
        for (EntityPlayer player : Penis.mc.world.playerEntities) {
            Vec3d interpolateEntity = Interpolation.interpolateEntity((Entity)player);
            this.drawPenis(player, interpolateEntity.x, interpolateEntity.y, interpolateEntity.z);
        }
    }

    protected void drawPenis(EntityPlayer player, double x, double y, double z) {
        Color shaftColor;
        float length;
        float f = player == Penis.mc.player ? this.selfLength.getValue().floatValue() : (length = Managers.FRIENDS.contains(player) ? this.friendLength.getValue().floatValue() : this.enemyLength.getValue().floatValue());
        Color color = player == Penis.mc.player ? this.selfShaftColor.getValue() : (shaftColor = Managers.FRIENDS.contains(player) ? this.friendShaftColor.getValue() : this.enemyShaftColor.getValue());
        Color tipColor = player == Penis.mc.player ? this.selfTipColor.getValue() : (Managers.FRIENDS.contains(player) ? this.friendTipColor.getValue() : this.enemyTipColor.getValue());
        GL11.glPushMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable((int)2896);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)2929);
        GL11.glEnable((int)2848);
        GL11.glDepthMask((boolean)false);
        GL11.glTranslated((double)x, (double)y, (double)z);
        GL11.glRotatef((float)(-player.rotationYaw), (float)0.0f, (float)player.height, (float)0.0f);
        GL11.glTranslated((double)(-x), (double)(-y), (double)(-z));
        GL11.glTranslated((double)x, (double)(y + (double)(player.height / 2.0f) - (double)0.225f), (double)z);
        GL11.glColor4f((float)((float)shaftColor.getRed() / 255.0f), (float)((float)shaftColor.getGreen() / 255.0f), (float)((float)shaftColor.getBlue() / 255.0f), (float)1.0f);
        GL11.glTranslated((double)0.0, (double)0.0, (double)0.075f);
        this.shaft.draw(0.1f, 0.11f, length, 25, 20);
        GL11.glColor4f((float)((float)shaftColor.getRed() / 255.0f), (float)((float)shaftColor.getGreen() / 255.0f), (float)((float)shaftColor.getBlue() / 255.0f), (float)1.0f);
        GL11.glTranslated((double)0.0, (double)0.0, (double)0.02500000298023223);
        GL11.glTranslated((double)-0.09000000074505805, (double)0.0, (double)0.0);
        this.ball.draw(0.14f, 10, 20);
        GL11.glTranslated((double)0.16000000149011612, (double)0.0, (double)0.0);
        this.ball.draw(0.14f, 10, 20);
        GL11.glTranslated((double)-0.07000000074505806, (double)0.0, (double)((double)length - (this.uncircumcised.getValue() != false ? 0.15 : 0.0)));
        GL11.glColor4f((float)((float)tipColor.getRed() / 255.0f), (float)((float)tipColor.getGreen() / 255.0f), (float)((float)tipColor.getBlue() / 255.0f), (float)1.0f);
        this.tip.draw(0.13f, 15, 20);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)3553);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}

