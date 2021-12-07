package me.earth.earthhack.impl.modules.combat.autoarmor;

import net.minecraft.inventory.EntityEquipmentSlot;

class AutoArmor$1 {
    static final int[] $SwitchMap$net$minecraft$inventory$EntityEquipmentSlot;

    static {
        $SwitchMap$net$minecraft$inventory$EntityEquipmentSlot = new int[EntityEquipmentSlot.values().length];
        try {
            AutoArmor$1.$SwitchMap$net$minecraft$inventory$EntityEquipmentSlot[EntityEquipmentSlot.OFFHAND.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoArmor$1.$SwitchMap$net$minecraft$inventory$EntityEquipmentSlot[EntityEquipmentSlot.FEET.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoArmor$1.$SwitchMap$net$minecraft$inventory$EntityEquipmentSlot[EntityEquipmentSlot.LEGS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoArmor$1.$SwitchMap$net$minecraft$inventory$EntityEquipmentSlot[EntityEquipmentSlot.CHEST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AutoArmor$1.$SwitchMap$net$minecraft$inventory$EntityEquipmentSlot[EntityEquipmentSlot.HEAD.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
