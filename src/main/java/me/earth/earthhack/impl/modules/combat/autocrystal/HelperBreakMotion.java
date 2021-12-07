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
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalDataMotion;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class HelperBreakMotion
extends AbstractBreakHelper<CrystalDataMotion> {
    private static final SettingCache<Float, NumberSetting<Float>, Safety> MD = Caches.getSetting(Safety.class, Setting.class, "MaxDamage", Float.valueOf(4.0f));

    public HelperBreakMotion(AutoCrystal module) {
        super(module);
    }

    @Override
    public BreakData<CrystalDataMotion> newData(Collection<CrystalDataMotion> data) {
        return new BreakData<CrystalDataMotion>(data);
    }

    @Override
    protected CrystalDataMotion newCrystalData(Entity crystal) {
        return new CrystalDataMotion(crystal);
    }

    @Override
    protected boolean isValid(Entity crystal, CrystalDataMotion data) {
        EntityPlayer e;
        double distance = Managers.POSITION.getDistanceSq(crystal);
        if (distance >= (double)MathUtil.square(this.module.breakRange.getValue().floatValue()) || distance >= (double)MathUtil.square(this.module.breakTrace.getValue().floatValue()) && !Managers.POSITION.canEntityBeSeen(crystal)) {
            data.invalidateTiming(CrystalDataMotion.Timing.PRE);
        }
        if ((distance = (e = RotationUtil.getRotationPlayer()).getDistanceSq(crystal)) >= (double)MathUtil.square(this.module.breakRange.getValue().floatValue()) || distance >= (double)MathUtil.square(this.module.breakTrace.getValue().floatValue()) && !e.canEntityBeSeen(crystal)) {
            data.invalidateTiming(CrystalDataMotion.Timing.POST);
        }
        return data.getTiming() != CrystalDataMotion.Timing.NONE;
    }

    @Override
    protected boolean calcSelf(Entity crystal, CrystalDataMotion data) {
        boolean breakCase = true;
        switch (data.getTiming()) {
            case BOTH: {
                breakCase = false;
            }
            case PRE: {
                float preDamage = this.module.damageHelper.getDamage(crystal);
                data.setSelfDmg(preDamage);
                if (preDamage > EntityUtil.getHealth(HelperBreakMotion.mc.player) - 1.0f && !this.module.suicide.getValue().booleanValue()) {
                    data.invalidateTiming(CrystalDataMotion.Timing.PRE);
                }
                if (breakCase) break;
            }
            case POST: {
                float postDamage = this.module.damageHelper.getDamage(crystal, RotationUtil.getRotationPlayer().getEntityBoundingBox());
                data.setPostSelf(postDamage);
                if (postDamage > EntityUtil.getHealth(HelperBreakMotion.mc.player) - 1.0f) {
                    Managers.SAFETY.setSafe(false);
                    if (!this.module.suicide.getValue().booleanValue()) {
                        data.invalidateTiming(CrystalDataMotion.Timing.POST);
                    }
                }
                if (!(postDamage > MD.getValue().floatValue())) break;
                Managers.SAFETY.setSafe(false);
                break;
            }
        }
        return data.getTiming() == CrystalDataMotion.Timing.NONE;
    }

    @Override
    protected void calcCrystal(BreakData<CrystalDataMotion> data, CrystalDataMotion crystalData, Entity crystal, List<EntityPlayer> players) {
        boolean highPostSelf;
        boolean highPreSelf = crystalData.getSelfDmg() > this.module.maxSelfBreak.getValue().floatValue();
        boolean bl = highPostSelf = crystalData.getPostSelf() > this.module.maxSelfBreak.getValue().floatValue();
        if (!this.module.suicide.getValue().booleanValue() && !this.module.overrideBreak.getValue().booleanValue() && highPreSelf && highPostSelf) {
            crystalData.invalidateTiming(CrystalDataMotion.Timing.PRE);
            crystalData.invalidateTiming(CrystalDataMotion.Timing.POST);
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
                highPreSelf = false;
                highPostSelf = false;
                killing = true;
            }
            if (!(playerDamage > damage)) continue;
            damage = playerDamage;
        }
        if (this.module.antiTotem.getValue().booleanValue() && crystal.getPosition().down().equals(this.module.antiTotemHelper.getTargetPos())) {
            data.setAntiTotem(crystal);
        }
        if (highPreSelf) {
            crystalData.invalidateTiming(CrystalDataMotion.Timing.PRE);
        }
        if (highPostSelf) {
            crystalData.invalidateTiming(CrystalDataMotion.Timing.POST);
        }
        if (crystalData.getTiming() != CrystalDataMotion.Timing.NONE && (!this.module.efficient.getValue().booleanValue() || damage > crystalData.getSelfDmg()) || killing) {
            data.register(crystalData);
        }
    }
}
