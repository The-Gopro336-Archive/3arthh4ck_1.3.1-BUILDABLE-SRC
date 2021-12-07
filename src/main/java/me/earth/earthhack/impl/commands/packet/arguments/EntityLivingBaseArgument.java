package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.impl.commands.packet.arguments.AbstractEntityArgument;
import net.minecraft.entity.EntityLivingBase;

public class EntityLivingBaseArgument
extends AbstractEntityArgument<EntityLivingBase> {
    public EntityLivingBaseArgument() {
        super(EntityLivingBase.class);
    }
}
