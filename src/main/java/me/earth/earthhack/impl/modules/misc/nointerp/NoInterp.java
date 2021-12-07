/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityFlying
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.math.MathHelper
 */
package me.earth.earthhack.impl.modules.misc.nointerp;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class NoInterp
extends Module {
    private final Setting<Boolean> silent = this.register(new BooleanSetting("Silent", true));
    private final Setting<Boolean> setRotations = this.register(new BooleanSetting("Fast-Rotations", false));
    private final Setting<Boolean> noDeathJitter = this.register(new BooleanSetting("NoDeathJitter", true));
    private final Setting<Boolean> onlyPlayers = this.register(new BooleanSetting("OnlyPlayers", false));

    public NoInterp() {
        super("NoInterp", Category.Misc);
        this.setData(new SimpleData(this, "Makes the client more accurate. The Packets module is recommended when using this."));
    }

    public boolean isSilent() {
        return this.silent.getValue();
    }

    public boolean shouldFixDeathJitter() {
        return this.noDeathJitter.getValue();
    }

    public static void handleNoInterp(NoInterp noInterp, Entity entity, int posIncrements, double x, double y, double z, float yaw, float pitch) {
        IEntityNoInterp entityNoInterp = (IEntityNoInterp)entity;
        if (!entityNoInterp.isNoInterping()) {
            return;
        }
        if (noInterp.setRotations.getValue().booleanValue()) {
            entity.setPosition(x, y, z);
            entity.rotationYaw = yaw % 360.0f;
            entity.rotationPitch = pitch % 360.0f;
        } else {
            entity.setPosition(x, y, z);
        }
        entityNoInterp.setPosIncrements(posIncrements);
    }

    public static double noInterpX(NoInterp noInterp, Entity entity) {
        if (noInterp != null && noInterp.isEnabled() && noInterp.isSilent() && entity instanceof IEntityNoInterp && ((IEntityNoInterp)entity).isNoInterping()) {
            return ((IEntityNoInterp)entity).getNoInterpX();
        }
        return entity.posX;
    }

    public static double noInterpY(NoInterp noInterp, Entity entity) {
        if (noInterp != null && noInterp.isEnabled() && noInterp.isSilent() && entity instanceof IEntityNoInterp && ((IEntityNoInterp)entity).isNoInterping()) {
            return ((IEntityNoInterp)entity).getNoInterpY();
        }
        return entity.posY;
    }

    public static double noInterpZ(NoInterp noInterp, Entity entity) {
        if (noInterp != null && noInterp.isEnabled() && noInterp.isSilent() && entity instanceof IEntityNoInterp && ((IEntityNoInterp)entity).isNoInterping()) {
            return ((IEntityNoInterp)entity).getNoInterpZ();
        }
        return entity.posZ;
    }

    public static boolean update(NoInterp module, Entity entity) {
        IEntityNoInterp noInterp;
        if (module == null || !module.isEnabled() || !module.silent.getValue().booleanValue() || EntityUtil.isDead(entity)) {
            return false;
        }
        if (!(entity instanceof IEntityNoInterp) || !(noInterp = (IEntityNoInterp)entity).isNoInterping()) {
            return false;
        }
        if (noInterp.getPosIncrements() > 0) {
            double x = noInterp.getNoInterpX() + (entity.posX - noInterp.getNoInterpX()) / (double)noInterp.getPosIncrements();
            double y = noInterp.getNoInterpY() + (entity.posY - noInterp.getNoInterpY()) / (double)noInterp.getPosIncrements();
            double z = noInterp.getNoInterpZ() + (entity.posZ - noInterp.getNoInterpZ()) / (double)noInterp.getPosIncrements();
            entity.prevPosX = noInterp.getNoInterpX();
            entity.prevPosY = noInterp.getNoInterpY();
            entity.prevPosZ = noInterp.getNoInterpZ();
            entity.lastTickPosX = noInterp.getNoInterpX();
            entity.lastTickPosY = noInterp.getNoInterpY();
            entity.lastTickPosZ = noInterp.getNoInterpZ();
            noInterp.setNoInterpX(x);
            noInterp.setNoInterpY(y);
            noInterp.setNoInterpZ(z);
            noInterp.setPosIncrements(noInterp.getPosIncrements() - 1);
        }
        if (entity instanceof EntityLivingBase) {
            float limbSwingAmount;
            double yDiff;
            double zDiff;
            EntityLivingBase base = (EntityLivingBase)entity;
            double xDiff = noInterp.getNoInterpX() - entity.prevPosX;
            float diff = MathHelper.sqrt((double)(xDiff * xDiff + (zDiff = noInterp.getNoInterpZ() - entity.prevPosZ) * zDiff + (yDiff = entity instanceof EntityFlying ? noInterp.getNoInterpY() - entity.prevPosY : 0.0) * yDiff)) * 4.0f;
            if (diff > 1.0f) {
                diff = 1.0f;
            }
            base.prevLimbSwingAmount = limbSwingAmount = noInterp.getNoInterpSwingAmount();
            noInterp.setNoInterpPrevSwing(limbSwingAmount);
            noInterp.setNoInterpSwingAmount(limbSwingAmount + (diff - limbSwingAmount) * 0.4f);
            base.limbSwingAmount = noInterp.getNoInterpSwingAmount();
            float limbSwing = noInterp.getNoInterpSwing() + base.limbSwingAmount;
            noInterp.setNoInterpSwing(limbSwing);
            base.limbSwing = limbSwing;
        }
        return true;
    }
}

