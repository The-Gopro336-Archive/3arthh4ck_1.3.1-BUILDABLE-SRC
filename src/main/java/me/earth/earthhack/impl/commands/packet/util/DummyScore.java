/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.scoreboard.Score
 *  net.minecraft.scoreboard.ScoreObjective
 *  net.minecraft.scoreboard.Scoreboard
 */
package me.earth.earthhack.impl.commands.packet.util;

import me.earth.earthhack.impl.commands.packet.util.Dummy;
import me.earth.earthhack.impl.commands.packet.util.DummyScoreObjective;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

public class DummyScore
extends Score
implements Dummy {
    public DummyScore() {
        super(new Scoreboard(), (ScoreObjective)new DummyScoreObjective(), "Dummy");
    }
}

