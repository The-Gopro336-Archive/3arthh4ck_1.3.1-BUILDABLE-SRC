/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class Confirmer
extends SubscriberImpl {
    private final StopWatch placeTimer = new StopWatch();
    private final StopWatch breakTimer = new StopWatch();
    private BlockPos current;
    private AxisAlignedBB bb;
    private boolean placeConfirmed;
    private boolean breakConfirmed;
    private boolean newVer;
    private boolean valid;
    private int placeTime;

    public Confirmer() {
        this.listeners.add(new ReceiveListener<SPacketSpawnObject>(SPacketSpawnObject.class, e -> {
            SPacketSpawnObject p = (SPacketSpawnObject)e.getPacket();
            if (p.getType() == 51) {
                this.confirmPlace(p.getX(), p.getY(), p.getZ());
            }
        }));
        this.listeners.add(new ReceiveListener<SPacketSoundEffect>(SPacketSoundEffect.class, e -> {
            SPacketSoundEffect p = (SPacketSoundEffect)e.getPacket();
            if (p.getCategory() == SoundCategory.BLOCKS && p.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                this.confirmBreak(p.getX(), p.getY(), p.getZ());
            }
        }));
    }

    public void setPos(BlockPos pos, boolean newVer, int placeTime) {
        this.newVer = newVer;
        if (pos == null) {
            this.current = null;
            this.valid = false;
        } else {
            BlockPos crystalPos;
            this.current = crystalPos = new BlockPos((double)((float)pos.getX() + 0.5f), (double)(pos.getY() + 1), (double)((float)pos.getZ() + 0.5f));
            this.bb = this.createBB(crystalPos, newVer);
            this.valid = true;
            this.placeConfirmed = false;
            this.breakConfirmed = false;
            this.placeTime = placeTime < 50 ? 0 : placeTime;
            this.placeTimer.reset();
        }
    }

    public void confirmPlace(double x, double y, double z) {
        if (this.valid && !this.placeConfirmed) {
            AxisAlignedBB currentBB;
            BlockPos p = new BlockPos(x, y, z);
            if (p.equals((Object)this.current)) {
                this.placeConfirmed = true;
                this.breakTimer.reset();
            } else if (this.placeTimer.passed(this.placeTime) && (currentBB = this.bb) != null && currentBB.intersects(this.createBB(x, y, z, this.newVer))) {
                this.valid = false;
            }
        }
    }

    public void confirmBreak(double x, double y, double z) {
        BlockPos current;
        if (this.valid && !this.breakConfirmed && this.placeConfirmed && (current = this.current) != null && current.distanceSq(x, y, z) < 144.0) {
            if (current.equals((Object)new BlockPos(x, y, z))) {
                this.breakConfirmed = true;
            } else {
                this.valid = false;
            }
        }
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isPlaceConfirmed(int placeConfirm) {
        if (!this.placeConfirmed && this.placeTimer.passed(placeConfirm)) {
            this.valid = false;
            return false;
        }
        return this.placeConfirmed && this.valid;
    }

    public boolean isBreakConfirmed(int breakConfirm) {
        if (this.placeConfirmed && !this.breakConfirmed && this.breakTimer.passed(breakConfirm)) {
            this.valid = false;
            return false;
        }
        return this.breakConfirmed && this.valid;
    }

    private AxisAlignedBB createBB(BlockPos crystalPos, boolean newVer) {
        return this.createBB((float)crystalPos.getX() + 0.5f, crystalPos.getY(), (float)crystalPos.getZ() + 0.5f, newVer);
    }

    private AxisAlignedBB createBB(double x, double y, double z, boolean newVer) {
        return new AxisAlignedBB(x - 1.0, y, z - 1.0, x + 1.0, y + (double)(newVer ? 1 : 2), z + 1.0);
    }

    public static Confirmer createAndSubscribe(EventBus bus) {
        Confirmer confirmer = new Confirmer();
        bus.subscribe(confirmer);
        return confirmer;
    }
}

