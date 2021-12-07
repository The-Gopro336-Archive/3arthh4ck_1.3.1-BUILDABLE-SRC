/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package me.earth.earthhack.impl.util.minecraft.entity.module;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.util.minecraft.entity.module.EntityTypeData;
import net.minecraft.entity.Entity;

public class EntityTypeModule
extends Module {
    public final Setting<Boolean> players = this.register(new BooleanSetting("Players", true));
    public final Setting<Boolean> monsters = this.register(new BooleanSetting("Monsters", false));
    public final Setting<Boolean> animals = this.register(new BooleanSetting("Animals", false));
    public final Setting<Boolean> boss = this.register(new BooleanSetting("Boss", false));
    public final Setting<Boolean> vehicles = this.register(new BooleanSetting("Vehicles", false));
    public final Setting<Boolean> misc = this.register(new BooleanSetting("Other", false));
    public final Setting<Boolean> entities = this.register(new BooleanSetting("Entity", false));

    public EntityTypeModule(String name, Category category) {
        super(name, category);
        this.setData(new EntityTypeData<EntityTypeModule>(this));
    }

    public boolean isValid(Entity entity) {
        if (entity == null) {
            return false;
        }
        switch (((IEntity)entity).getType()) {
            case Animal: {
                return this.animals.getValue();
            }
            case Monster: {
                return this.monsters.getValue();
            }
            case Player: {
                return this.players.getValue();
            }
            case Boss: {
                return this.boss.getValue();
            }
            case Vehicle: {
                return this.vehicles.getValue();
            }
            case Other: {
                return this.misc.getValue();
            }
        }
        return this.entities.getValue();
    }
}

