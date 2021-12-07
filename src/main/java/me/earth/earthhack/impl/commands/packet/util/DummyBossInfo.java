package me.earth.earthhack.impl.commands.packet.util;

import java.util.UUID;
import me.earth.earthhack.impl.commands.packet.util.Dummy;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo;

public class DummyBossInfo
extends BossInfo
implements Dummy {
    public DummyBossInfo() {
        super(UUID.randomUUID(), new TextComponentString("Dummy-Boss"), BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_20);
    }
}
