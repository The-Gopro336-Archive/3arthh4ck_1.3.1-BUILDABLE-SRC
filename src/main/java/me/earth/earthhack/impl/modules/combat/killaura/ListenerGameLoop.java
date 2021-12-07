package me.earth.earthhack.impl.modules.combat.killaura;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

final class ListenerGameLoop
extends ModuleListener<KillAura, GameLoopEvent> {
    public ListenerGameLoop(KillAura module) {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event) {
        boolean multi;
        EntityPlayer from = RotationUtil.getRotationPlayer();
        if (ListenerGameLoop.mc.world == null || from == null) {
            return;
        }
        boolean k = DamageUtil.isSharper(ListenerGameLoop.mc.player.getHeldItemMainhand(), 1000);
        boolean bl = multi = ((KillAura)this.module).multi32k.getValue() != false && k;
        if ((((KillAura)this.module).target != null || multi) && ((KillAura)this.module).shouldAttack() && (!((KillAura)this.module).rotate.getValue().booleanValue() || RotationUtil.isLegit(((KillAura)this.module).target, new Entity[0])) && (!((KillAura)this.module).delay.getValue().booleanValue() || ((KillAura)this.module).t2k.getValue().booleanValue() && k) && ((KillAura)this.module).cps.getValue().floatValue() > 20.0f && ((KillAura)this.module).timer.passed((long)(1000.0 / (double)((KillAura)this.module).cps.getValue().floatValue()))) {
            if (multi) {
                int packets = 0;
                for (EntityPlayer player : ListenerGameLoop.mc.world.playerEntities) {
                    if (!((KillAura)this.module).isValid(player) || !((KillAura)this.module).isInRange(from, (Entity)player)) continue;
                    PacketUtil.attack(player);
                    if (++packets < ((KillAura)this.module).packets.getValue()) continue;
                    break;
                }
            } else {
                for (int i = 0; i < ((KillAura)this.module).packets.getValue(); ++i) {
                    PacketUtil.attack(((KillAura)this.module).target);
                }
                if (((KillAura)this.module).swing.getValue() == Swing.Client || ((KillAura)this.module).swing.getValue() == Swing.Full) {
                    Swing.Client.swing(EnumHand.MAIN_HAND);
                }
            }
            ((KillAura)this.module).timer.reset((long)(1000.0 / (double)((KillAura)this.module).cps.getValue().floatValue()));
        }
    }
}
