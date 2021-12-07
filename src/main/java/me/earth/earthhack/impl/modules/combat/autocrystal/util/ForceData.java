package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import java.util.Set;
import java.util.TreeSet;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.ForcePosition;

public class ForceData {
    private final Set<ForcePosition> forceData = new TreeSet<ForcePosition>();
    private boolean possibleHighDamage;
    private boolean possibleAntiTotem;

    public boolean hasPossibleHighDamage() {
        return this.possibleHighDamage;
    }

    public void setPossibleHighDamage(boolean possibleHighDamage) {
        this.possibleHighDamage = possibleHighDamage;
    }

    public boolean hasPossibleAntiTotem() {
        return this.possibleAntiTotem;
    }

    public void setPossibleAntiTotem(boolean possibleAntiTotem) {
        this.possibleAntiTotem = possibleAntiTotem;
    }

    public Set<ForcePosition> getForceData() {
        return this.forceData;
    }
}
