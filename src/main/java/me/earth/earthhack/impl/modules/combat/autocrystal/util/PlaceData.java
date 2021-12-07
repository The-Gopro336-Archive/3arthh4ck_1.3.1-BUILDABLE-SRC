package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.AntiTotemData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.ForceData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.ForcePosition;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PositionData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class PlaceData {
    private final Map<EntityPlayer, ForceData> force = new HashMap<EntityPlayer, ForceData>();
    private final Map<EntityPlayer, List<PositionData>> corr = new HashMap<EntityPlayer, List<PositionData>>();
    private final Map<BlockPos, PositionData> obby = new HashMap<BlockPos, PositionData>();
    private final Map<BlockPos, PositionData> liquidObby = new HashMap<BlockPos, PositionData>();
    private final List<PositionData> liquid = new ArrayList<PositionData>();
    private final List<PositionData> invalid = new ArrayList<PositionData>();
    private final Set<PositionData> data = new TreeSet<PositionData>();
    private final Set<AntiTotemData> antiTotem = new TreeSet<AntiTotemData>();
    private final float minDamage;
    private EntityPlayer target;

    public PlaceData(float minDamage) {
        this.minDamage = minDamage;
    }

    public void setTarget(EntityPlayer target) {
        this.target = target;
    }

    public void addAntiTotem(AntiTotemData data) {
        this.antiTotem.add(data);
    }

    public void addCorrespondingData(EntityPlayer player, PositionData data) {
        List list = this.corr.computeIfAbsent(player, v -> new ArrayList());
        list.add(data);
    }

    public void confirmHighDamageForce(EntityPlayer player) {
        ForceData data = this.force.computeIfAbsent(player, v -> new ForceData());
        data.setPossibleHighDamage(true);
    }

    public void confirmPossibleAntiTotem(EntityPlayer player) {
        ForceData data = this.force.computeIfAbsent(player, v -> new ForceData());
        data.setPossibleAntiTotem(true);
    }

    public void addForceData(EntityPlayer player, ForcePosition forceIn) {
        ForceData data = this.force.computeIfAbsent(player, v -> new ForceData());
        data.getForceData().add(forceIn);
    }

    public void addAllCorrespondingData() {
        for (AntiTotemData antiTotemData : this.antiTotem) {
            for (EntityPlayer player : antiTotemData.getAntiTotems()) {
                List<PositionData> corresponding = this.corr.get(player);
                if (corresponding == null) continue;
                corresponding.forEach(antiTotemData::addCorrespondingData);
            }
        }
    }

    public float getMinDamage() {
        return this.minDamage;
    }

    public EntityPlayer getTarget() {
        return this.target;
    }

    public Set<AntiTotemData> getAntiTotem() {
        return this.antiTotem;
    }

    public Set<PositionData> getData() {
        return this.data;
    }

    public Map<BlockPos, PositionData> getAllObbyData() {
        return this.obby;
    }

    public Map<EntityPlayer, ForceData> getForceData() {
        return this.force;
    }

    public List<PositionData> getLiquid() {
        return this.liquid;
    }

    public Map<BlockPos, PositionData> getLiquidObby() {
        return this.liquidObby;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PlaceData:\n");
        for (PositionData data : this.data) {
            builder.append("Position: ").append(data.getPos()).append("\n");
        }
        return builder.toString();
    }
}
