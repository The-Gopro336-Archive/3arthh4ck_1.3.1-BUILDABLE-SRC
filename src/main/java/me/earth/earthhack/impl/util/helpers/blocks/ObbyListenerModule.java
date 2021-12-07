/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.util.helpers.blocks;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.data.ObbyListenerData;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class ObbyListenerModule<T extends ObbyListener<?>>
extends ObbyModule {
    public final Setting<Integer> confirm = this.register(new NumberSetting<Integer>("Confirm", 100, 0, 1000));
    protected final T listener = this.createListener();

    protected ObbyListenerModule(String name, Category category) {
        super(name, category);
        this.listeners.add(this.listener);
        this.setData(new ObbyListenerData<ObbyListenerModule>(this));
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        ((ObbyListener)this.listener).onModuleToggle();
        this.checkNull();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        ((ObbyListener)this.listener).onModuleToggle();
        this.checkNull();
    }

    @Override
    public void placeBlock(BlockPos on, EnumFacing facing, float[] helpingRotations, Vec3d hitVec) {
        super.placeBlock(on, facing, helpingRotations, hitVec);
        ((ObbyListener)this.listener).addCallback(on.offset(facing));
    }

    protected abstract T createListener();
}

