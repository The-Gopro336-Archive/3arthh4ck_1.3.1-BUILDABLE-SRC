package me.earth.earthhack.impl.modules.render.skeleton;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.modules.render.skeleton.ListenerModel;
import me.earth.earthhack.impl.modules.render.skeleton.ListenerRender;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.entity.player.EntityPlayer;

public class Skeleton
extends Module {
    protected final Map<EntityPlayer, float[][]> rotations = new HashMap<EntityPlayer, float[][]>();
    public final Setting<Color> color = this.register(new ColorSetting("Color", new Color(255, 255, 255, 255)));
    public final Setting<Color> friendColor = this.register(new ColorSetting("FriendColor", new Color(50, 255, 50, 255)));
    public final Setting<Color> targetColor = this.register(new ColorSetting("TargetColor", new Color(255, 0, 0, 255)));

    public Skeleton() {
        super("Skeleton", Category.Render);
        this.listeners.add(new ListenerModel(this));
        this.listeners.add(new ListenerRender(this));
        this.setData(new SimpleData(this, "Spooky."));
    }
}
