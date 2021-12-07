package me.earth.earthhack.impl.modules.combat.autocrystal;

import java.util.Collection;
import java.util.List;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.combat.autocrystal.AbstractBreakHelper;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.BreakData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class HelperBreak
extends AbstractBreakHelper<CrystalData> {
    private static final SettingCache<Float, NumberSetting<Float>, Safety> MD = Caches.getSetting(Safety.class, Setting.class, "MaxDamage", Float.valueOf(4.0f));

    public HelperBreak(AutoCrystal module) {
        super(module);
    }

    @Override
    public BreakData<CrystalData> newData(Collection<CrystalData> data) {
        return new BreakData<CrystalData>(data);
    }

    @Override
    protected CrystalData newCrystalData(Entity crystal) {
        return new CrystalData(crystal);
    }

    @Override
    protected boolean isValid(Entity crystal, CrystalData data) {
        double distance = Managers.POSITION.getDistanceSq(crystal);
        if (distance > (double)MathUtil.square(this.module.breakTrace.getValue().floatValue()) && !Managers.POSITION.canEntityBeSeen(crystal)) {
            return false;
        }
        return distance <= (double)MathUtil.square(this.module.breakRange.getValue().floatValue());
    }

    @Override
    protected boolean calcSelf(Entity crystal, CrystalData data) {
        float selfDamage = this.module.damageHelper.getDamage(crystal);
        data.setSelfDmg(selfDamage);
        if (selfDamage > EntityUtil.getHealth(HelperBreak.mc.player) - 1.0f) {
            Managers.SAFETY.setSafe(false);
            if (!this.module.suicide.getValue().booleanValue()) {
                return true;
            }
        }
        if (selfDamage > MD.getValue().floatValue()) {
            Managers.SAFETY.setSafe(false);
        }
        return false;
    }

    @Override
    protected void calcCrystal(BreakData<CrystalData> data, CrystalData crystalData, Entity crystal, List<EntityPlayer> players) {
        boolean highSelf;
        boolean bl = highSelf = crystalData.getSelfDmg() > this.module.maxSelfBreak.getValue().floatValue();
        if (!this.module.suicide.getValue().booleanValue() && !this.module.overrideBreak.getValue().booleanValue() && highSelf) {
            return;
        }
        float damage = 0.0f;
        boolean killing = false;
        for (EntityPlayer player : players) {
            if (player.getDistanceSq(crystal) > 144.0) continue;
            float playerDamage = this.module.damageHelper.getDamage(crystal, (EntityLivingBase)player);
            if (playerDamage > crystalData.getDamage()) {
                crystalData.setDamage(playerDamage);
            }
            if (playerDamage > EntityUtil.getHealth(player) + 1.0f) {
                killing = true;
                highSelf = false;
            }
            if (!(playerDamage > damage)) continue;
            damage = playerDamage;
        }
        if (this.module.antiTotem.getValue().booleanValue() && crystal.getPosition().down().equals(this.module.antiTotemHelper.getTargetPos())) {
            data.setAntiTotem(crystal);
        }
        if (!highSelf && (!this.module.efficient.getValue().booleanValue() || damage > crystalData.getSelfDmg() || killing)) {
            data.register(crystalData);
        }
    }
}
