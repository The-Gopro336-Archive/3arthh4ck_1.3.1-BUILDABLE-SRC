/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.core.ducks.entity;

import me.earth.earthhack.impl.commands.packet.util.Dummy;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.entity.EntityType;

public interface IEntity
extends Dummy {
    public boolean inWeb();

    public EntityType getType();

    public long getDeathTime();

    public boolean isPseudoDead();

    public void setPseudoDead(boolean var1);

    public StopWatch getPseudoTime();

    public long getTimeStamp();

    @Override
    default public boolean isDummy() {
        return false;
    }

    public void setDummy(boolean var1);
}

