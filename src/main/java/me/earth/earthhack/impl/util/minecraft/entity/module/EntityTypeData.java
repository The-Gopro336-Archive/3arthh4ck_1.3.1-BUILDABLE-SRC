/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.util.minecraft.entity.module;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.util.minecraft.entity.module.EntityTypeModule;

public class EntityTypeData<T extends EntityTypeModule>
extends DefaultData<T> {
    public EntityTypeData(T module) {
        super(module);
        this.register(((EntityTypeModule)module).players, "Targets Players.");
        this.register(((EntityTypeModule)module).monsters, "Targets Monsters.");
        this.register(((EntityTypeModule)module).animals, "Targets Animals.");
        this.register(((EntityTypeModule)module).boss, "Targets Boss Monsters.");
        this.register(((EntityTypeModule)module).animals, "Targets Vehicles.");
        this.register(((EntityTypeModule)module).misc, "Targets Fireballs, ShulkerBullets etc.");
        this.register(((EntityTypeModule)module).misc, "Targets Unknown Entities.");
    }
}

