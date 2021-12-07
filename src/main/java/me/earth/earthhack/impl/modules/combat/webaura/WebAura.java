/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityOtherPlayerMP
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.webaura;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autotrap.modes.TrapTarget;
import me.earth.earthhack.impl.modules.combat.webaura.ListenerWebAura;
import me.earth.earthhack.impl.modules.combat.webaura.WebAuraData;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.helpers.blocks.noattack.NoAttackObbyListenerModule;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class WebAura
extends NoAttackObbyListenerModule<ListenerWebAura> {
    protected static final ModuleCache<Freecam> FREECAM = Caches.getModule(Freecam.class);
    protected final Setting<Double> placeRange = this.register(new NumberSetting<Double>("PlaceRange", 6.0, 0.1, 7.5));
    protected final Setting<TrapTarget> target = this.register(new EnumSetting<TrapTarget>("Target", TrapTarget.Closest));
    protected final Setting<Boolean> antiSelfWeb = this.register(new BooleanSetting("AntiSelfWeb", true));
    protected final Setting<Double> targetRange = this.register(new NumberSetting<Double>("Target-Range", 6.0, 0.1, 10.0));
    protected EntityPlayer currentTarget;

    public WebAura() {
        super("WebAura", Category.Combat);
        this.unregister(this.blockingType);
        this.setData(new WebAuraData(this));
    }

    @Override
    public String getDisplayInfo() {
        return this.target.getValue() == TrapTarget.Closest && this.currentTarget != null ? this.currentTarget.getName() : null;
    }

    @Override
    protected ListenerWebAura createListener() {
        return new ListenerWebAura(this);
    }

    @Override
    public boolean entityCheck(BlockPos pos) {
        return this.selfWebCheck(pos);
    }

    @Override
    protected boolean quickEntityCheck(BlockPos pos) {
        return !this.selfWebCheck(pos);
    }

    @Override
    public EntityPlayer getPlayerForRotations() {
        EntityOtherPlayerMP target;
        if (FREECAM.isEnabled() && (target = ((Freecam)FREECAM.get()).getPlayer()) != null) {
            return target;
        }
        return WebAura.mc.player;
    }

    protected boolean selfWebCheck(BlockPos pos) {
        return BlockUtil.getDistanceSq(pos) <= MathUtil.square(this.placeRange.getValue()) && (this.antiSelfWeb.getValue() == false || !this.getPlayerForRotations().getEntityBoundingBox().intersects(new AxisAlignedBB(pos)));
    }
}

