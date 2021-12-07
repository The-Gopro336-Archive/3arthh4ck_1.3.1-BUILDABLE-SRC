package me.earth.earthhack.impl.util.helpers.blocks.noattack;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.helpers.blocks.noattack.NoAttackObbyListener;
import net.minecraft.util.math.BlockPos;

public abstract class NoAttackObbyListenerModule<T extends NoAttackObbyListener<?>>
extends ObbyListenerModule<T> {
    protected NoAttackObbyListenerModule(String name, Category category) {
        super(name, category);
        this.unregister(this.attack);
        this.unregister(this.pop);
        this.unregister(this.popTime);
        this.unregister(this.cooldown);
        this.unregister(this.breakDelay);
    }

    @Override
    public boolean execute() {
        this.attacking = null;
        return super.execute();
    }

    @Override
    public boolean entityCheck(BlockPos pos) {
        return this.entityCheckSimple(pos);
    }
}
