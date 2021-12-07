/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.player.suicide;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.modules.player.suicide.SuicideMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

final class ListenerSpawnObject
extends ModuleListener<Suicide, PacketEvent.Receive<SPacketSpawnObject>> {
    public ListenerSpawnObject(Suicide module) {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        EntityEnderCrystal crystal;
        block8: {
            block7: {
                if (((SPacketSpawnObject)event.getPacket()).getType() != 51 || !((Suicide)this.module).instant.getValue().booleanValue() || !((Suicide)this.module).breakTimer.passed(((Suicide)this.module).breakDelay.getValue().intValue()) || ((Suicide)this.module).mode.getValue() == SuicideMode.Command) {
                    return;
                }
                crystal = new EntityEnderCrystal((World)ListenerSpawnObject.mc.world, ((SPacketSpawnObject)event.getPacket()).getX(), ((SPacketSpawnObject)event.getPacket()).getY(), ((SPacketSpawnObject)event.getPacket()).getZ());
                if (RotationUtil.getRotationPlayer().getDistanceSq((Entity)crystal) >= (double)MathUtil.square(((Suicide)this.module).breakRange.getValue().floatValue()) || RotationUtil.getRotationPlayer().getDistanceSq((Entity)crystal) >= (double)MathUtil.square(((Suicide)this.module).trace.getValue().floatValue()) && !RotationUtil.getRotationPlayer().canEntityBeSeen((Entity)crystal)) break block7;
                if (!((Suicide)this.module).rotate.getValue().booleanValue()) break block8;
                if (RotationUtil.isLegit((Entity)crystal, new Entity[]{crystal})) break block8;
            }
            return;
        }
        if (!((Suicide)this.module).instantCalc.getValue().booleanValue()) {
            if (((Suicide)this.module).placed.remove((Object)new BlockPos(crystal.posX, crystal.posY - 1.0, crystal.posZ))) {
                PacketUtil.attack(((SPacketSpawnObject)event.getPacket()).getEntityID());
                ((Suicide)this.module).breakTimer.reset();
            }
            return;
        }
        float damage = DamageUtil.calculate((Entity)crystal);
        if (damage > ((Suicide)this.module).minInstant.getValue().floatValue()) {
            PacketUtil.attack(((SPacketSpawnObject)event.getPacket()).getEntityID());
            ((Suicide)this.module).breakTimer.reset();
        }
    }
}

