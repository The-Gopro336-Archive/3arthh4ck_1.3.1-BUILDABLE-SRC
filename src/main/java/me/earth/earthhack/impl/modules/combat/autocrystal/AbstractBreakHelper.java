package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.util.Collection;
import java.util.List;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiFriendPop;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.BreakData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.IBreakHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractBreakHelper<T extends CrystalData>
implements IBreakHelper<T>,
Globals {
    protected final AutoCrystal module;

    public AbstractBreakHelper(AutoCrystal module) {
        this.module = module;
    }

    protected abstract T newCrystalData(Entity var1);

    protected abstract boolean isValid(Entity var1, T var2);

    protected abstract boolean calcSelf(Entity var1, T var2);

    protected abstract void calcCrystal(BreakData<T> var1, T var2, Entity var3, List<EntityPlayer> var4);

    @Override
    public BreakData<T> getData(Collection<T> dataSet, List<Entity> entities, List<EntityPlayer> players, List<EntityPlayer> friends) {
        BreakData<T> data = this.newData(dataSet);
        for (Entity crystal : entities) {
            T crystalData;
            if (!(crystal instanceof EntityEnderCrystal) || EntityUtil.isDead(crystal) || this.calcSelf(crystal, crystalData = this.newCrystalData(crystal)) || !this.isValid(crystal, crystalData) || this.module.antiFriendPop.getValue().shouldCalc(AntiFriendPop.Break) && this.checkFriendPop(crystal, friends)) continue;
            this.calcCrystal(data, crystalData, crystal, players);
        }
        return data;
    }

    protected boolean checkFriendPop(Entity entity, List<EntityPlayer> friends) {
        for (EntityPlayer friend : friends) {
            float fDamage = this.module.damageHelper.getDamage(entity, (EntityLivingBase)friend);
            if (!(fDamage > EntityUtil.getHealth(friend) - 1.0f)) continue;
            return true;
        }
        return false;
    }
}
