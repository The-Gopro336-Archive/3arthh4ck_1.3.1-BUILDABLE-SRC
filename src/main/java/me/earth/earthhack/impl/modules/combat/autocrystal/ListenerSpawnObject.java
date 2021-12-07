/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EntityTracker
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.util.List;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperRotation;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperUtil;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiFriendPop;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.BreakValidity;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalTimeStamp;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.WeaknessSwitch;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.MutableWrapper;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

final class ListenerSpawnObject
extends ModuleListener<AutoCrystal, PacketEvent.Receive<SPacketSpawnObject>> {
    private static final ModuleCache<LegSwitch> LEG_SWITCH = Caches.getModule(LegSwitch.class);
    private static final ModuleCache<AntiSurround> ANTISURROUND = Caches.getModule(AntiSurround.class);
    private static final SettingCache<Float, NumberSetting<Float>, Safety> DMG = Caches.getSetting(Safety.class, Setting.class, "MaxDamage", Float.valueOf(4.0f));

    public ListenerSpawnObject(AutoCrystal module) {
        super(module, PacketEvent.Receive.class, Integer.MIN_VALUE, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        try {
            this.onEvent(event);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void onEvent(PacketEvent.Receive<SPacketSpawnObject> event) {
        if (!((AutoCrystal)this.module).spectator.getValue().booleanValue() && ListenerSpawnObject.mc.player.isSpectator()) {
            return;
        }
        SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        EntityEnderCrystal entity = new EntityEnderCrystal((World)ListenerSpawnObject.mc.world, x, y, z);
        if (((AutoCrystal)this.module).simulatePlace.getValue() != 0) {
            event.addPostEvent(() -> {
                if (ListenerSpawnObject.mc.world == null) {
                    return;
                }
                Entity e = ListenerSpawnObject.mc.world.getEntityByID(packet.getEntityID());
                if (e instanceof EntityEnderCrystal) {
                    ((AutoCrystal)this.module).crystalRender.onSpawn((EntityEnderCrystal)e);
                }
            });
        }
        if (packet.getType() != 51 || !((AutoCrystal)this.module).instant.getValue().booleanValue() || ((AutoCrystal)this.module).isPingBypass() || !((AutoCrystal)this.module).breakTimer.passed(((AutoCrystal)this.module).breakDelay.getValue().intValue()) || ANTISURROUND.returnIfPresent(AntiSurround::isActive, false).booleanValue() || LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false).booleanValue()) {
            return;
        }
        BlockPos pos = new BlockPos(x, y, z);
        CrystalTimeStamp stamp = ((AutoCrystal)this.module).placed.get((Object)pos);
        entity.setShowBottom(false);
        entity.setEntityId(packet.getEntityID());
        entity.setUniqueId(packet.getUniqueId());
        if (!((AutoCrystal)this.module).alwaysCalc.getValue().booleanValue() && stamp != null && stamp.isValid() && (stamp.getDamage() > ((AutoCrystal)this.module).slowBreakDamage.getValue().floatValue() || ((AutoCrystal)this.module).breakTimer.passed(((AutoCrystal)this.module).slowBreakDelay.getValue().intValue()) || pos.down().equals((Object)((AutoCrystal)this.module).antiTotemHelper.getTargetPos()))) {
            float damage = this.checkPos((Entity)entity);
            if (damage <= -1000.0f) {
                MutableWrapper<Boolean> a = new MutableWrapper<Boolean>(false);
                ((AutoCrystal)this.module).rotation = ((AutoCrystal)this.module).rotationHelper.forBreaking((Entity)entity, a);
                event.addPostEvent(() -> {
                    Entity e;
                    if (ListenerSpawnObject.mc.world != null && (e = ListenerSpawnObject.mc.world.getEntityByID(packet.getEntityID())) != null) {
                        ((AutoCrystal)this.module).post.add(((AutoCrystal)this.module).rotationHelper.post(e, a));
                        ((AutoCrystal)this.module).rotation = ((AutoCrystal)this.module).rotationHelper.forBreaking(e, a);
                        ((AutoCrystal)this.module).setCrystal(e);
                    }
                });
                return;
            }
            if (damage < 0.0f) {
                return;
            }
            this.attack(packet, event, entity, stamp.getDamage() <= ((AutoCrystal)this.module).slowBreakDamage.getValue().floatValue());
        } else if (((AutoCrystal)this.module).asyncCalc.getValue().booleanValue() || ((AutoCrystal)this.module).alwaysCalc.getValue().booleanValue()) {
            List<EntityPlayer> players = Managers.ENTITIES.getPlayers();
            if (players == null) {
                return;
            }
            float self = this.checkPos((Entity)entity);
            if (self < 0.0f) {
                return;
            }
            boolean slow = true;
            boolean attack = false;
            for (EntityPlayer player : players) {
                if (player == null || EntityUtil.isDead((Entity)player) || player.getDistanceSq(x, y, z) > 144.0) continue;
                if (Managers.FRIENDS.contains(player)) {
                    if (!((AutoCrystal)this.module).antiFriendPop.getValue().shouldCalc(AntiFriendPop.Break) || !(((AutoCrystal)this.module).damageHelper.getDamage(x, y, z, (EntityLivingBase)player) > EntityUtil.getHealth((EntityLivingBase)player) - 0.5f)) continue;
                    attack = false;
                    break;
                }
                float dmg = ((AutoCrystal)this.module).damageHelper.getDamage(x, y, z, (EntityLivingBase)player);
                if (!(dmg > self) && (!((AutoCrystal)this.module).suicide.getValue().booleanValue() || !(dmg >= ((AutoCrystal)this.module).minDamage.getValue().floatValue())) || !(dmg > ((AutoCrystal)this.module).slowBreakDamage.getValue().floatValue()) && !((AutoCrystal)this.module).shouldDanger() && !((AutoCrystal)this.module).breakTimer.passed(((AutoCrystal)this.module).slowBreakDelay.getValue().intValue())) continue;
                slow = slow && dmg <= ((AutoCrystal)this.module).slowBreakDamage.getValue().floatValue();
                attack = true;
            }
            if (attack) {
                this.attack(packet, event, entity, slow);
            }
        }
        if (((AutoCrystal)this.module).spawnThread.getValue().booleanValue()) {
            ((AutoCrystal)this.module).threadHelper.schedulePacket(event);
        }
    }

    private void attack(SPacketSpawnObject packet, PacketEvent.Receive<?> event, EntityEnderCrystal entityIn, boolean slow) {
        CPacketUseEntity p = new CPacketUseEntity((Entity)entityIn);
        WeaknessSwitch w = HelperRotation.antiWeakness((AutoCrystal)this.module);
        if (w.needsSwitch() && (w.getSlot() == -1 || !((AutoCrystal)this.module).instantAntiWeak.getValue().booleanValue())) {
            return;
        }
        int lastSlot = ListenerSpawnObject.mc.player.inventory.currentItem;
        Runnable runnable = () -> {
            if (w.getSlot() != -1) {
                switch (((AutoCrystal)this.module).antiWeaknessBypass.getValue()) {
                    case None: {
                        InventoryUtil.switchTo(w.getSlot());
                        break;
                    }
                    case Slot: {
                        InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(w.getSlot()));
                        break;
                    }
                    case Pick: {
                        InventoryUtil.bypassSwitch(w.getSlot());
                    }
                }
            }
            if (((AutoCrystal)this.module).breakSwing.getValue() == SwingTime.Pre) {
                Swing.Packet.swing(EnumHand.MAIN_HAND);
            }
            ListenerSpawnObject.mc.player.connection.sendPacket((Packet)p);
            if (((AutoCrystal)this.module).breakSwing.getValue() == SwingTime.Post) {
                Swing.Packet.swing(EnumHand.MAIN_HAND);
            }
            if (w.getSlot() != -1) {
                switch (((AutoCrystal)this.module).antiWeaknessBypass.getValue()) {
                    case None: {
                        InventoryUtil.switchTo(lastSlot);
                        break;
                    }
                    case Slot: {
                        InventoryUtil.switchToBypassAlt(InventoryUtil.hotbarToInventory(w.getSlot()));
                        break;
                    }
                    case Pick: {
                        InventoryUtil.bypassSwitch(w.getSlot());
                    }
                }
            }
        };
        if (w.getSlot() != -1) {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, runnable);
        } else {
            runnable.run();
        }
        ((AutoCrystal)this.module).breakTimer.reset(slow ? (long)((AutoCrystal)this.module).slowBreakDelay.getValue().intValue() : (long)((AutoCrystal)this.module).breakDelay.getValue().intValue());
        event.addPostEvent(() -> {
            Entity entity = ListenerSpawnObject.mc.world.getEntityByID(packet.getEntityID());
            if (entity instanceof EntityEnderCrystal) {
                ((AutoCrystal)this.module).setCrystal(entity);
            }
        });
        if (((AutoCrystal)this.module).simulateExplosion.getValue().booleanValue()) {
            HelperUtil.simulateExplosion((AutoCrystal)this.module, packet.getX(), packet.getY(), packet.getZ());
        }
        if (((AutoCrystal)this.module).pseudoSetDead.getValue().booleanValue()) {
            event.addPostEvent(() -> {
                Entity entity = ListenerSpawnObject.mc.world.getEntityByID(packet.getEntityID());
                if (entity != null) {
                    ((IEntity)entity).setPseudoDead(true);
                }
            });
            return;
        }
        if (((AutoCrystal)this.module).instantSetDead.getValue().booleanValue()) {
            event.setCancelled(true);
            mc.addScheduledTask(() -> {
                Entity entity = ListenerSpawnObject.mc.world.getEntityByID(packet.getEntityID());
                if (entity instanceof EntityEnderCrystal) {
                    ((AutoCrystal)this.module).crystalRender.onSpawn((EntityEnderCrystal)entity);
                }
                if (!event.isCancelled()) {
                    return;
                }
                EntityTracker.updateServerPosition((Entity)entityIn, (double)packet.getX(), (double)packet.getY(), (double)packet.getZ());
                Managers.SET_DEAD.setDead((Entity)entityIn);
            });
        }
    }

    private float checkPos(Entity entity) {
        BreakValidity validity = HelperUtil.isValid((AutoCrystal)this.module, entity);
        switch (validity) {
            case INVALID: {
                return -1.0f;
            }
            case ROTATIONS: {
                float damage = this.getSelfDamage(entity);
                if (damage < 0.0f) {
                    return damage;
                }
                return -1000.0f - damage;
            }
        }
        return this.getSelfDamage(entity);
    }

    private float getSelfDamage(Entity entity) {
        float damage = ((AutoCrystal)this.module).damageHelper.getDamage(entity);
        if (damage > EntityUtil.getHealth((EntityLivingBase)ListenerSpawnObject.mc.player) - 1.0f || damage > DMG.getValue().floatValue()) {
            Managers.SAFETY.setSafe(false);
        }
        return damage > ((AutoCrystal)this.module).maxSelfBreak.getValue().floatValue() || damage > EntityUtil.getHealth((EntityLivingBase)ListenerSpawnObject.mc.player) - 1.0f && ((AutoCrystal)this.module).suicide.getValue() == false ? -1.0f : damage;
    }
}

