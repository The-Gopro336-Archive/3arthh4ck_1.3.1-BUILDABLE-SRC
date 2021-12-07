package me.earth.earthhack.impl.modules.player.mcp;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.mcf.MCF;
import me.earth.earthhack.impl.modules.player.mcp.ListenerMiddleClick;
import me.earth.earthhack.impl.modules.player.mcp.ListenerMotion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

public class MiddleClickPearl
extends Module {
    private static final ModuleCache<MCF> MCFRIENDS = Caches.getModule(MCF.class);
    protected final Setting<Boolean> preferMCF = this.register(new BooleanSetting("PrioMCF", false));
    protected final Setting<Boolean> cancelMCF = this.register(new BooleanSetting("CancelMCF", true));
    protected final Setting<Boolean> cancelBlock = this.register(new BooleanSetting("CancelBlock", false));
    protected Runnable runnable;

    public MiddleClickPearl() {
        super("MCP", Category.Player);
        this.listeners.add(new ListenerMiddleClick(this));
        this.listeners.add(new ListenerMotion(this));
    }

    @Override
    public void onEnable() {
        this.runnable = null;
    }

    @Override
    protected void onDisable() {
        this.runnable = null;
    }

    protected boolean prioritizeMCF() {
        return this.preferMCF.getValue() != false && MCFRIENDS.isEnabled() && MiddleClickPearl.mc.objectMouseOver != null && MiddleClickPearl.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && MiddleClickPearl.mc.objectMouseOver.entityHit instanceof EntityPlayer;
    }
}
