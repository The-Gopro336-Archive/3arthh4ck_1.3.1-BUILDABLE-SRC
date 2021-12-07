package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;

final class LegSwitchData
extends DefaultData<LegSwitch> {
    public LegSwitchData(LegSwitch module) {
        super(module);
        this.register(module.delay, "Delay between between a PlaceBreakProcess.");
        this.register(module.closest, "Targets only the closest player.");
        this.register(module.rotate, "Type of Rotation.");
        this.register(module.minDamage, "Min Damage a crystal needs to deal.");
        this.register(module.maxSelfDamage, "Maximum damage a crystal can deal to you.");
        this.register(module.placeRange, "Range in which crystals should be placed.");
        this.register(module.placeTrace, "Range that crystals are placed through walls with.");
        this.register(module.breakRange, "Only crystals within this range will be broken.");
        this.register(module.breakTrace, "Only crystals within this range will be broken through walls.");
        this.register(module.combinedTrace, "Break/PlaceTrace. Keep this at the BreakTraces value.");
        this.register(module.instant, "Instantly break Crystals.");
        this.register(module.setDead, "Removes crystals after they've been attacked.");
        this.register(module.requireMid, "Takes the middle block between the 2 crystals into account. (Dev Setting)");
        this.register(module.soundRemove, "Uses SoundPackets to remove Crystals.");
        this.register(module.soundStart, "Dev Setting don't touch!");
        this.register(module.newVer, "Takes 1.13+ mechanics into account. For ViaVersion servers.");
        this.register(module.rotationPacket, "Produces an extra Rotation Packet. Might lag you back.");
        this.register(module.coolDown, "Required for some servers where you can't attack instantly after you switched your mainhand item.");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "Attempts to bypass Minecraft mechanics that stop Crystals from blocking Surround. Will deal less damage.";
    }
}
