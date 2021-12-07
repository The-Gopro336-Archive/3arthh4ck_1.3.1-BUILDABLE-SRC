package me.earth.earthhack.impl.modules.combat.autothirtytwok;

import net.minecraft.util.math.BlockPos;

public class Auto32k$DispenserData {
    private BlockPos dispenserPos;
    private BlockPos redStonePos;
    private BlockPos hopperPos;
    private BlockPos helpingPos;
    private boolean isPlaceable = false;

    public Auto32k$DispenserData() {
    }

    public Auto32k$DispenserData(BlockPos pos) {
        this.hopperPos = pos;
    }

    public void setPlaceable(boolean placeable) {
        this.isPlaceable = placeable;
    }

    public boolean isPlaceable() {
        return this.dispenserPos != null && this.hopperPos != null && this.redStonePos != null && this.helpingPos != null;
    }

    public BlockPos getDispenserPos() {
        return this.dispenserPos;
    }

    public void setDispenserPos(BlockPos dispenserPos) {
        this.dispenserPos = dispenserPos;
    }

    public BlockPos getRedStonePos() {
        return this.redStonePos;
    }

    public void setRedStonePos(BlockPos redStonePos) {
        this.redStonePos = redStonePos;
    }

    public BlockPos getHopperPos() {
        return this.hopperPos;
    }

    public void setHopperPos(BlockPos hopperPos) {
        this.hopperPos = hopperPos;
    }

    public BlockPos getHelpingPos() {
        return this.helpingPos;
    }

    public void setHelpingPos(BlockPos helpingPos) {
        this.helpingPos = helpingPos;
    }
}
