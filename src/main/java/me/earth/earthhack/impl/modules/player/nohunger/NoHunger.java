/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 */
package me.earth.earthhack.impl.modules.player.nohunger;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.nohunger.ListenerEntityAction;
import me.earth.earthhack.impl.modules.player.nohunger.ListenerPlayerPacket;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;

public class NoHunger
extends Module {
    protected final Setting<Boolean> sprint = this.register(new BooleanSetting("Sprint", true));
    protected final Setting<Boolean> ground = this.register(new BooleanSetting("Ground", true));
    boolean onGround;

    public NoHunger() {
        super("NoHunger", Category.Player);
        this.listeners.add(new ListenerEntityAction(this));
        this.listeners.addAll(new ListenerPlayerPacket(this).getListeners());
        SimpleData data = new SimpleData(this, "Makes you not get hungry.");
        data.register(this.sprint, "Will cancel sprint packets send to the server.");
        data.register(this.ground, "Will make the server think you are not on the ground, which makes it no apply hunger to you. Will build up falldamage over time so watch out.");
        this.setData(data);
    }

    @Override
    protected void onEnable() {
        if (this.sprint.getValue().booleanValue() && NoHunger.mc.player != null) {
            NoHunger.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)NoHunger.mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    @Override
    protected void onDisable() {
        if (this.sprint.getValue().booleanValue() && NoHunger.mc.player != null && !Managers.ACTION.isSprinting() && NoHunger.mc.player.isSprinting()) {
            NoHunger.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)NoHunger.mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }
    }
}

