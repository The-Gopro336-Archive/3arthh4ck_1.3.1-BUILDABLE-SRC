package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import java.util.Collection;
import java.util.List;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.BreakData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IBreakHelper<T extends CrystalData> {
    public BreakData<T> newData(Collection<T> var1);

    public BreakData<T> getData(Collection<T> var1, List<Entity> var2, List<EntityPlayer> var3, List<EntityPlayer> var4);
}
