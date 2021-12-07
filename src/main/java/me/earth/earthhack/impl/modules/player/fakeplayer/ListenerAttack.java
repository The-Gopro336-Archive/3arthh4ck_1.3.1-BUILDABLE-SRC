package me.earth.earthhack.impl.modules.player.fakeplayer;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.criticals.Criticals;
import me.earth.earthhack.impl.modules.player.fakeplayer.FakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketUseEntity;

final class ListenerAttack
extends ModuleListener<FakePlayer, PacketEvent.Send<CPacketUseEntity>> {
    private static final ModuleCache<Criticals> CRITICALS = Caches.getModule(Criticals.class);

    public ListenerAttack(FakePlayer module) {
        super(module, PacketEvent.Send.class, CPacketUseEntity.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketUseEntity> event) {
        if (event.isCancelled()) {
            return;
        }
        Entity entity = ((ICPacketUseEntity)event.getPacket()).getAttackedEntity();
        if (((FakePlayer)this.module).fakePlayer.equals(entity)) {
            event.setCancelled(true);
            if (CRITICALS.isEnabled() || !ListenerAttack.mc.player.isSprinting() && ListenerAttack.mc.player.fallDistance > 0.0f && !ListenerAttack.mc.player.onGround && !ListenerAttack.mc.player.isOnLadder() && !ListenerAttack.mc.player.isInWater() && !ListenerAttack.mc.player.isPotionActive(MobEffects.BLINDNESS) && !ListenerAttack.mc.player.isRiding()) {
                ListenerAttack.mc.world.playSound(ListenerAttack.mc.player, ListenerAttack.mc.player.posX, ListenerAttack.mc.player.posY, ListenerAttack.mc.player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, ListenerAttack.mc.player.getSoundCategory(), 1.0f, 1.0f);
            } else if ((double)ListenerAttack.mc.player.getCooledAttackStrength(0.5f) > 0.9) {
                ListenerAttack.mc.world.playSound(ListenerAttack.mc.player, ListenerAttack.mc.player.posX, ListenerAttack.mc.player.posY, ListenerAttack.mc.player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, ListenerAttack.mc.player.getSoundCategory(), 1.0f, 1.0f);
            } else {
                ListenerAttack.mc.world.playSound(ListenerAttack.mc.player, ListenerAttack.mc.player.posX, ListenerAttack.mc.player.posY, ListenerAttack.mc.player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, ListenerAttack.mc.player.getSoundCategory(), 1.0f, 1.0f);
            }
        }
    }
}
