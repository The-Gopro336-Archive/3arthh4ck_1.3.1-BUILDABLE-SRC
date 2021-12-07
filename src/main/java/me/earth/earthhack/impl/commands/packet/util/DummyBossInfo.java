/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.BossInfo
 *  net.minecraft.world.BossInfo$Color
 *  net.minecraft.world.BossInfo$Overlay
 */
package me.earth.earthhack.impl.commands.packet.util;

import java.util.UUID;
import me.earth.earthhack.impl.commands.packet.util.Dummy;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo;

public class DummyBossInfo
extends BossInfo
implements Dummy {
    public DummyBossInfo() {
        super(UUID.randomUUID(), (ITextComponent)new TextComponentString("Dummy-Boss"), BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_20);
    }
}

