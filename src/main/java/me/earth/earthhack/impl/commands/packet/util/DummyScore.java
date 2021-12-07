package me.earth.earthhack.impl.commands.packet.util;

import me.earth.earthhack.impl.commands.packet.util.Dummy;
import me.earth.earthhack.impl.commands.packet.util.DummyScoreObjective;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.Scoreboard;

public class DummyScore
extends Score
implements Dummy {
    public DummyScore() {
        super(new Scoreboard(), new DummyScoreObjective(), "Dummy");
    }
}
