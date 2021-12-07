package me.earth.earthhack.impl.util.minecraft.entity.module;

import me.earth.earthhack.impl.util.minecraft.entity.EntityType;

class EntityTypeModule$1 {
    static final int[] $SwitchMap$me$earth$earthhack$impl$util$minecraft$entity$EntityType;

    static {
        $SwitchMap$me$earth$earthhack$impl$util$minecraft$entity$EntityType = new int[EntityType.values().length];
        try {
            EntityTypeModule$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$entity$EntityType[EntityType.Animal.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EntityTypeModule$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$entity$EntityType[EntityType.Monster.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EntityTypeModule$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$entity$EntityType[EntityType.Player.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EntityTypeModule$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$entity$EntityType[EntityType.Boss.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EntityTypeModule$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$entity$EntityType[EntityType.Vehicle.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EntityTypeModule$1.$SwitchMap$me$earth$earthhack$impl$util$minecraft$entity$EntityType[EntityType.Other.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
