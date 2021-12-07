/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import java.util.Set;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PositionData;
import net.minecraft.entity.player.EntityPlayer;

public class ForcePosition
extends PositionData {
    private final PositionData data;

    public ForcePosition(PositionData data) {
        super(data.getPos(), data.getMaxLength());
        this.data = data;
    }

    public PositionData getData() {
        return this.data;
    }

    @Override
    public boolean usesObby() {
        return this.data.usesObby();
    }

    @Override
    public float getMaxDamage() {
        return this.data.getMaxDamage();
    }

    @Override
    public void setDamage(float damage) {
        this.data.setDamage(damage);
    }

    @Override
    public float getSelfDamage() {
        return this.data.getSelfDamage();
    }

    @Override
    public void setSelfDamage(float selfDamage) {
        this.data.setSelfDamage(selfDamage);
    }

    @Override
    public EntityPlayer getTarget() {
        return this.data.getTarget();
    }

    @Override
    public void setTarget(EntityPlayer target) {
        this.data.setTarget(target);
    }

    @Override
    public EntityPlayer getFacePlacer() {
        return this.data.getFacePlacer();
    }

    @Override
    public void setFacePlacer(EntityPlayer facePlace) {
        this.data.setFacePlacer(facePlace);
    }

    @Override
    public Set<EntityPlayer> getAntiTotems() {
        return this.data.getAntiTotems();
    }

    @Override
    public void addAntiTotem(EntityPlayer player) {
        this.data.addAntiTotem(player);
    }

    @Override
    public boolean isBlocked() {
        return this.data.isBlocked();
    }

    @Override
    public float getMinDiff() {
        return this.data.getMinDiff();
    }

    @Override
    public void setMinDiff(float minDiff) {
        this.data.setMinDiff(minDiff);
    }

    @Override
    public boolean isForce() {
        return true;
    }

    @Override
    public void addForcePlayer(EntityPlayer player) {
        this.data.addForcePlayer(player);
    }

    @Override
    public boolean isLiquid() {
        return this.data.isLiquid();
    }

    @Override
    public int compareTo(PositionData o) {
        int c;
        if (o instanceof ForcePosition && (c = Float.compare(this.getMinDiff(), o.getMinDiff())) != 0) {
            return c;
        }
        return super.compareTo(o);
    }
}

