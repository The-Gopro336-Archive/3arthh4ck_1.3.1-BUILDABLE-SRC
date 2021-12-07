/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.selftrap;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.combat.selftrap.ListenerSelfTrap;
import me.earth.earthhack.impl.modules.combat.selftrap.SelfTrapMode;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class SelfTrap
extends ObbyListenerModule<ListenerSelfTrap> {
    protected final Setting<SelfTrapMode> mode = this.register(new EnumSetting<SelfTrapMode>("Mode", SelfTrapMode.Obsidian));
    protected final Setting<Boolean> smart = this.register(new BooleanSetting("Smart", false));
    protected final Setting<Float> range = this.register(new NumberSetting<Float>("SmartRange", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    protected final Setting<Double> placeRange = this.register(new NumberSetting<Double>("PlaceRange", 6.0, 0.0, 7.5));
    protected final Setting<Integer> maxHelping = this.register(new NumberSetting<Integer>("HelpingBlocks", 4, 0, 20));
    protected final Setting<Boolean> autoOff = this.register(new BooleanSetting("Auto-Off", true));
    protected final Setting<Boolean> smartOff = this.register(new BooleanSetting("Smart-Off", true));
    protected final Setting<Boolean> prioBehind = this.register(new BooleanSetting("Prio-Behind", true));
    protected BlockPos startPos;

    public SelfTrap() {
        super("SelfTrap", Category.Combat);
    }

    @Override
    protected void onEnable() {
        EntityPlayer entity = RotationUtil.getRotationPlayer();
        if (entity != null) {
            this.startPos = PositionUtil.getPosition((Entity)entity);
        }
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        this.startPos = null;
    }

    @Override
    public boolean execute() {
        if (this.mode.getValue() != SelfTrapMode.Obsidian) {
            this.attacking = null;
        }
        return super.execute();
    }

    @Override
    protected ListenerSelfTrap createListener() {
        return new ListenerSelfTrap(this);
    }

    @Override
    public EntityPlayer getPlayerForRotations() {
        return RotationUtil.getRotationPlayer();
    }

    @Override
    public EntityPlayer getPlayer() {
        return RotationUtil.getRotationPlayer();
    }

    @Override
    protected boolean entityCheckSimple(BlockPos pos) {
        return true;
    }

    @Override
    public boolean entityCheck(BlockPos pos) {
        return true;
    }

    @Override
    protected boolean quickEntityCheck(BlockPos pos) {
        return false;
    }
}

