package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;

final class SpeedMineData
extends DefaultData<Speedmine> {
    public SpeedMineData(Speedmine module) {
        super(module);
        this.register(module.mode, "-Reset nothing special, blocks won't be reset after you stopped mining them.\n-Packet touch a block to mine it later.\n-Smart much better packet. Allows you to decide that you want to go for another block in difference to Mode Packet.\"-Damage normal Mining but the the block will be mined instantly after the damage specified by the Damage Setting is reached.");
        this.register(module.noReset, "Doesn't reset blocks after mining them.");
        this.register(module.limit, "After this amount of damage has been dealt to the block it will be mined.");
        this.register(module.range, "If the currently mined block is outside this range the mining process will be cancelled.");
        this.register(module.multiTask, "If you want to allow multitasking or not.");
        this.register(module.rotate, "Some servers (2b2tpvp) patched Packet Speedmines by requiring you to look at the block. This will silently look at the block.");
        this.register(module.event, "Development Setting for compatibility with BlockTweaks-NoMiningAnimation.");
        this.register(module.display, "Displays the damage dealt to the block in the HUDs-Arraylist.");
        this.register(module.delay, "Delay between clicking a block. Good for Mode-Smart which will stop mining the block when you touch it again, so with this setting you cant accidentally touch it twice.");
        this.register(module.esp, "Renders a Block-ESP around the currently mined block.");
        this.register(module.onGround, "Normally you mine slower when you are not standing on the ground. Most servers don't check this tho, so this setting can be off.");
        this.register(module.placeCrystal, "Places a crystal when the block is about to break if the block being broken is placed at the feet.");
        this.register(module.toAir, "Attempts to set the block mined to air clientsided instead of waiting for the response from the server. Can cause Glitchblocks. Also its recommended to set the Limit Setting to 1 when using this with Mode Smart or Packet.");
        this.register(module.normal, "Ignore code that 3arth doesn't understand.");
        this.register(module.confirm, "Time for the Server to confirm that we broke the block.");
        this.register(module.requireBreakSlot, "Requires damage to be >= limit when using BreakBind");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "All kinds of tweaks around mining blocks.";
    }
}
