package me.earth.earthhack.impl.util.helpers.blocks.data;

import me.earth.earthhack.api.module.data.DefaultData;
import me.earth.earthhack.impl.util.helpers.blocks.BlockPlacingModule;

public class BlockPlacingData<T extends BlockPlacingModule>
extends DefaultData<T> {
    public BlockPlacingData(T module) {
        super(module);
        this.register("Blocks/Place", "Amount of blocks you want to place per Tick.");
        this.register("Delay", "Delay between each Placement.");
        this.register("Rotations", "- None : No Rotations\n- Normal : 1 block/place (Vanilla)\n- Packet : More blocks, might lag back.");
        this.register("Packet", "Prevents GhostBlocks, might seem slower.");
        this.register("Swing", "Client Side Swing.");
        this.register("Cooldown-Bypass", "Bypasses NCP cooldown checks.Can flag inventory checks due to high numbers of inventory actions.");
        this.register("StackPacket", "When not using packet: consumes your the used stack (-1).");
        this.register("Smart-Sneak", "Only sneaks when needed. (Turn off when Surround opens any Gui and tell me. Also turn this off on ViaVersion Servers.");
        this.register("PlaceSwing", "-Always always swings when you place a block (Might spam packets). \n-Never doesn't swing at all. \n-Once only swings once.");
        this.register("Blocking", "-Strict will allow players to hitbox-block your blocks, but never causes desync. (Recommended)\n-PacketFly less strict.\n-NoPacketFly smart and not strict but can be buggy when players PacketFly in to hitbox-block you.\n-All Even less strict than NoPacketFly.\n-Full Stricter NoPacketFly, Checks PacketFly first.\n-Crystals, if you can't use attacking. Can cause kicks.");
        this.register("Raytrace", "-Fast efficient and fast, works on most servers.\n-Resign tries to find the best Raytrace but will fall back to Fast if needed.\n-Force similar to resign but will do more calculations.\n-Smart will only accept the best Raytrace, which can cause blocks not to be placed at all (Legit).");
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    public String getDescription() {
        return "A module that places blocks.";
    }
}
