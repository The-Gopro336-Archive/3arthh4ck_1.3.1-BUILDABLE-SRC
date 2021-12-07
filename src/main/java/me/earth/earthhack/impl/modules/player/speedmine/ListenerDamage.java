package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.misc.DamageBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;

final class ListenerDamage
extends ModuleListener<Speedmine, DamageBlockEvent> {
    private static final ModuleCache<Nuker> NUKER = Caches.getModule(Nuker.class);
    private static final SettingCache<Boolean, BooleanSetting, Nuker> NUKE = Caches.getSetting(Nuker.class, BooleanSetting.class, "Nuke", false);
    private static final ModuleCache<AntiSurround> ANTISURROUND = Caches.getModule(AntiSurround.class);

    public ListenerDamage(Speedmine module) {
        super(module, DamageBlockEvent.class);
    }

    @Override
    public void invoke(DamageBlockEvent event) {
        if (ANTISURROUND.returnIfPresent(AntiSurround::isActive, false).booleanValue()) {
            return;
        }
        ((Speedmine)this.module).checkReset();
        if (!(!MineUtil.canBreak(event.getPos()) || PlayerUtil.isCreative(ListenerDamage.mc.player) || NUKER.isEnabled() && NUKE.getValue().booleanValue())) {
            switch (((Speedmine)this.module).mode.getValue()) {
                case Reset: {
                    this.setPos(event);
                    break;
                }
                case Packet: 
                case Civ: {
                    this.setPos(event);
                    ListenerDamage.mc.player.swingArm(EnumHand.MAIN_HAND);
                    CPacketPlayerDigging start = new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing());
                    CPacketPlayerDigging stop = new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing());
                    if (((Speedmine)this.module).event.getValue().booleanValue()) {
                        ListenerDamage.mc.player.connection.sendPacket(start);
                        ListenerDamage.mc.player.connection.sendPacket(stop);
                    } else {
                        NetworkUtil.sendPacketNoEvent(start, false);
                        NetworkUtil.sendPacketNoEvent(stop, false);
                    }
                    if (((Speedmine)this.module).swingStop.getValue().booleanValue()) {
                        Swing.Packet.swing(EnumHand.MAIN_HAND);
                    }
                    event.setCancelled(true);
                    break;
                }
                case Damage: {
                    this.setPos(event);
                    if (!(((IPlayerControllerMP)((Object)ListenerDamage.mc.playerController)).getCurBlockDamageMP() >= ((Speedmine)this.module).limit.getValue().floatValue())) break;
                    ((IPlayerControllerMP)((Object)ListenerDamage.mc.playerController)).setCurBlockDamageMP(1.0f);
                    break;
                }
                case Smart: {
                    boolean aborted = false;
                    if (((Speedmine)this.module).pos != null && !((Speedmine)this.module).pos.equals(event.getPos())) {
                        ((Speedmine)this.module).abortCurrentPos();
                        aborted = true;
                    }
                    if (!aborted && !((Speedmine)this.module).timer.passed(((Speedmine)this.module).delay.getValue().intValue())) break;
                    if (!aborted && ((Speedmine)this.module).pos != null && ((Speedmine)this.module).pos.equals(event.getPos())) {
                        ((Speedmine)this.module).abortCurrentPos();
                        ((Speedmine)this.module).timer.reset();
                        return;
                    }
                    this.setPos(event);
                    ListenerDamage.mc.player.swingArm(EnumHand.MAIN_HAND);
                    CPacketPlayerDigging packet = new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing());
                    if (((Speedmine)this.module).event.getValue().booleanValue()) {
                        ListenerDamage.mc.player.connection.sendPacket(packet);
                    } else {
                        NetworkUtil.sendPacketNoEvent(packet, false);
                    }
                    event.setCancelled(true);
                    ((Speedmine)this.module).timer.reset();
                    break;
                }
                case Instant: {
                    boolean abortedd = false;
                    if (((Speedmine)this.module).pos != null && !((Speedmine)this.module).pos.equals(event.getPos())) {
                        ((Speedmine)this.module).abortCurrentPos();
                        abortedd = true;
                    }
                    if (!abortedd && !((Speedmine)this.module).timer.passed(((Speedmine)this.module).delay.getValue().intValue())) break;
                    if (!abortedd && ((Speedmine)this.module).pos != null && ((Speedmine)this.module).pos.equals(event.getPos())) {
                        ((Speedmine)this.module).abortCurrentPos();
                        ((Speedmine)this.module).timer.reset();
                        return;
                    }
                    this.setPos(event);
                    ListenerDamage.mc.player.swingArm(EnumHand.MAIN_HAND);
                    CPacketPlayerDigging packet = new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing());
                    if (((Speedmine)this.module).event.getValue().booleanValue()) {
                        ListenerDamage.mc.player.connection.sendPacket(packet);
                    } else {
                        NetworkUtil.sendPacketNoEvent(packet, false);
                    }
                    ((Speedmine)this.module).shouldAbort = true;
                    event.setCancelled(true);
                    ((Speedmine)this.module).timer.reset();
                }
            }
        }
    }

    private void setPos(DamageBlockEvent event) {
        ((Speedmine)this.module).reset();
        ((Speedmine)this.module).pos = event.getPos();
        ((Speedmine)this.module).facing = event.getFacing();
        ((Speedmine)this.module).bb = ListenerDamage.mc.world.getBlockState(((Speedmine)this.module).pos).func_185918_c(ListenerDamage.mc.world, ((Speedmine)this.module).pos).grow(0.002f);
    }
}
