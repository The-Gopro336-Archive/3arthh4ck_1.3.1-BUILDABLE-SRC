/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.scoreboard.IScoreCriteria
 *  net.minecraft.scoreboard.ScoreObjective
 *  net.minecraft.scoreboard.Scoreboard
 */
package me.earth.earthhack.impl.commands.packet.util;

import me.earth.earthhack.impl.commands.packet.util.Dummy;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

public class DummyScoreObjective
extends ScoreObjective
implements Dummy {
    public DummyScoreObjective() {
        super(new Scoreboard(), "Dummy-Objective", IScoreCriteria.DUMMY);
    }
}

