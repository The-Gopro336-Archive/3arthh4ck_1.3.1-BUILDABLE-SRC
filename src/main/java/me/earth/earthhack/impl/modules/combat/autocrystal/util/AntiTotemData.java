package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import java.util.Set;
import java.util.TreeSet;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PositionData;
import net.minecraft.entity.player.EntityPlayer;

public class AntiTotemData
extends PositionData {
    private final Set<PositionData> corresponding = new TreeSet<PositionData>();

    public AntiTotemData(PositionData data) {
        super(data.getPos(), data.getMaxLength(), data.getAntiTotems());
    }

    public void addCorrespondingData(PositionData data) {
        this.corresponding.add(data);
    }

    public Set<PositionData> getCorresponding() {
        return this.corresponding;
    }

    @Override
    public int compareTo(PositionData o) {
        if (Math.abs(o.getSelfDamage() - this.getSelfDamage()) < 1.0f && o instanceof AntiTotemData) {
            EntityPlayer player = this.getFirstTarget();
            EntityPlayer other = ((AntiTotemData)o).getFirstTarget();
            if (other == null) {
                if (player == null) {
                    return super.compareTo(o);
                }
                return -1;
            }
            if (player == null) {
                return 1;
            }
            return Double.compare(player.getDistanceSq(this.getPos()), other.getDistanceSq(o.getPos()));
        }
        return super.compareTo(o);
    }

    public EntityPlayer getFirstTarget() {
        return this.getAntiTotems().stream().findFirst().orElse(null);
    }
}
