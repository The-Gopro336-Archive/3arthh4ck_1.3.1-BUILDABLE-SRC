/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.client.renderer.texture.ITextureObject
 *  net.minecraft.client.renderer.texture.SimpleTexture
 *  net.minecraft.client.shader.Framebuffer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.EXTFramebufferObject
 */
package me.earth.earthhack.impl.modules.render.chams;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.imageio.ImageIO;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.chams.ChamsData;
import me.earth.earthhack.impl.modules.render.chams.ListenerModelPost;
import me.earth.earthhack.impl.modules.render.chams.ListenerModelPre;
import me.earth.earthhack.impl.modules.render.chams.ListenerRender;
import me.earth.earthhack.impl.modules.render.chams.ListenerRenderEntity;
import me.earth.earthhack.impl.modules.render.chams.ListenerRenderLayers;
import me.earth.earthhack.impl.modules.render.chams.mode.ChamsMode;
import me.earth.earthhack.impl.util.minecraft.EntityType;
import me.earth.earthhack.impl.util.render.GlShader;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.render.image.GifConverter;
import me.earth.earthhack.impl.util.render.image.GifImage;
import me.earth.earthhack.impl.util.render.image.ImageUtil;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.EXTFramebufferObject;

public class Chams
extends Module {
    public static final ResourceLocation GALAXY_LOCATION = new ResourceLocation("earthhack:textures/client/galaxy.jpg");
    protected int texID = -1;
    public final Setting<ChamsMode> mode = this.register(new EnumSetting<ChamsMode>("Mode", ChamsMode.Normal));
    protected final Setting<Boolean> self = this.register(new BooleanSetting("Self", false));
    protected final Setting<Boolean> players = this.register(new BooleanSetting("Players", true));
    protected final Setting<Boolean> animals = this.register(new BooleanSetting("Animals", false));
    protected final Setting<Boolean> monsters = this.register(new BooleanSetting("Monsters", false));
    protected final Setting<Boolean> texture = this.register(new BooleanSetting("Texture", false));
    protected final Setting<Boolean> xqz = this.register(new BooleanSetting("XQZ", true));
    protected final Setting<Boolean> armor = this.register(new BooleanSetting("Armor", true));
    protected final Setting<Float> z = new NumberSetting<Float>("Z", Float.valueOf(-2000.0f), Float.valueOf(-5000.0f), Float.valueOf(5000.0f));
    protected final Setting<Float> mixFactor = this.register(new NumberSetting<Float>("MixFactor", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(1.0f)));
    protected final Setting<String> image = this.register(new StringSetting("Image", "None!"));
    protected final Setting<Color> color = this.register(new ColorSetting("Color", new Color(255, 255, 255, 255)));
    protected final Setting<Color> wallsColor = this.register(new ColorSetting("WallsColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> friendColor = this.register(new ColorSetting("FriendColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> friendWallColor = this.register(new ColorSetting("FriendWallsColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> enemyColor = this.register(new ColorSetting("EnemyColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> enemyWallsColor = this.register(new ColorSetting("EnemyWallsColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> armorColor = this.register(new ColorSetting("ArmorColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> armorFriendColor = this.register(new ColorSetting("ArmorFriendColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> armorEnemyColor = this.register(new ColorSetting("ArmorEnemyColor", new Color(255, 255, 255, 255)));
    protected boolean force;
    protected boolean hasImageChammed;
    protected boolean renderLayers;
    protected boolean renderModels;
    protected final GlShader fireShader = GlShader.createShader("chams");
    protected final GlShader galaxyShader = GlShader.createShader("stars");
    protected final GlShader waterShader = GlShader.createShader("water");
    protected final GlShader alphaShader = GlShader.createShader("alpha");
    protected final GlShader imageShader = GlShader.createShader("image");
    protected final long initTime = System.currentTimeMillis();
    protected boolean gif = false;
    protected GifImage gifImage;
    protected DynamicTexture dynamicTexture;

    public Chams() {
        super("Chams", Category.Render);
        try {
            this.gifImage = GifConverter.readGifImage(new FileInputStream("D:/DesktopHDD/project/felix.gif"));
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        this.listeners.add(new ListenerModelPre(this));
        this.listeners.add(new ListenerModelPost(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerRenderEntity(this));
        this.listeners.add(new ListenerRenderLayers(this));
        this.image.addObserver(e -> this.loadImage((String)e.getValue()));
        this.setData(new ChamsData(this));
        mc.getTextureManager().loadTexture(GALAXY_LOCATION, (ITextureObject)new SimpleTexture(GALAXY_LOCATION));
    }

    public void loadImage(String value) {
        try {
            File file = new File(value);
            if (value.endsWith(".gif")) {
                GifImage image = GifConverter.readGifImage(new FileInputStream(file));
                this.gif = true;
                this.gifImage = image;
            } else if (value.endsWith(".png")) {
                this.gif = false;
                BufferedImage image = ImageIO.read(file);
                image = ImageUtil.createFlipped(image);
                this.dynamicTexture = ImageUtil.cacheBufferedImage(image, "png");
            } else if (value.endsWith(".jpg") || value.endsWith(".jpeg")) {
                this.gif = false;
                BufferedImage image = ImageIO.read(file);
                image = ImageUtil.createFlipped(image);
                this.dynamicTexture = ImageUtil.cacheBufferedImage(image, "jpg");
            }
        }
        catch (IOException | NoSuchAlgorithmException exception) {
            // empty catch block
        }
    }

    public boolean isValid(Entity entity, ChamsMode modeIn) {
        return this.isEnabled() && modeIn == this.mode.getValue() && this.isValid(entity);
    }

    public boolean isValid(Entity entity) {
        Entity renderEntity = RenderUtil.getEntity();
        if (entity == null) {
            return false;
        }
        if (!this.self.getValue().booleanValue() && entity.equals((Object)renderEntity)) {
            return false;
        }
        if (this.players.getValue().booleanValue() && entity instanceof EntityPlayer) {
            return true;
        }
        if (!this.monsters.getValue().booleanValue() || !EntityType.isMonster(entity) && !EntityType.isBoss(entity)) {
            return this.animals.getValue() != false && (EntityType.isAngry(entity) || EntityType.isAnimal(entity));
        }
        return true;
    }

    protected Color getVisibleColor(Entity entity) {
        if (Managers.FRIENDS.contains(entity)) {
            return this.friendColor.getValue();
        }
        if (Managers.ENEMIES.contains(entity)) {
            return this.enemyColor.getValue();
        }
        return this.color.getValue();
    }

    protected Color getWallsColor(Entity entity) {
        if (Managers.FRIENDS.contains(entity)) {
            return this.friendWallColor.getValue();
        }
        if (Managers.ENEMIES.contains(entity)) {
            return this.enemyWallsColor.getValue();
        }
        return this.wallsColor.getValue();
    }

    public Color getArmorVisibleColor(Entity entity) {
        if (Managers.FRIENDS.contains(entity)) {
            return this.armorFriendColor.getValue();
        }
        if (Managers.ENEMIES.contains(entity)) {
            return this.armorEnemyColor.getValue();
        }
        return this.armorColor.getValue();
    }

    protected void checkSetupFBO() {
        Framebuffer fbo = mc.getFramebuffer();
        if (fbo.depthBuffer > -1) {
            this.setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    protected void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT((int)fbo.depthBuffer);
        int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT((int)36161, (int)stencilDepthBufferID);
        EXTFramebufferObject.glRenderbufferStorageEXT((int)36161, (int)34041, (int)Chams.mc.displayWidth, (int)Chams.mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36128, (int)36161, (int)stencilDepthBufferID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36096, (int)36161, (int)stencilDepthBufferID);
    }

    public boolean shouldArmorChams() {
        return this.armor.getValue();
    }

    public boolean isImageChams() {
        return this.mode.getValue() == ChamsMode.Image;
    }

    public float getAlpha() {
        return (float)this.color.getValue().getAlpha() / 255.0f;
    }
}

