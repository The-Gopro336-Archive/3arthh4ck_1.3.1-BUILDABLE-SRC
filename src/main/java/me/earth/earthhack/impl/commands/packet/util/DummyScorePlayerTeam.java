/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.scoreboard.ScorePlayerTeam
 *  net.minecraft.scoreboard.Scoreboard
 */
package me.earth.earthhack.impl.commands.packet.util;

import me.earth.earthhack.impl.commands.packet.util.Dummy;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

public class DummyScorePlayerTeam
extends ScorePlayerTeam
implements Dummy {
    public DummyScorePlayerTeam() {
        super(new Scoreboard(), "Dummy-ScorePlayerTeam");
    }
}

