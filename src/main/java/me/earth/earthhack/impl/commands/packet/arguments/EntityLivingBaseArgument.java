/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 */
package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.arguments.AbstractEntityArgument;
import net.minecraft.entity.EntityLivingBase;

public class EntityLivingBaseArgument
extends AbstractEntityArgument<EntityLivingBase> {
    public EntityLivingBaseArgument() {
        super(EntityLivingBase.class);
    }
}

