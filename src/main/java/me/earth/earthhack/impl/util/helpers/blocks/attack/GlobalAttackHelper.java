/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 */
package me.earth.earthhack.impl.util.helpers.blocks.attack;

import java.util.List;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperUtil;
import me.earth.earthhack.impl.util.helpers.blocks.attack.AttackHelper;
import me.earth.earthhack.impl.util.helpers.blocks.attack.AttackingModule;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;

public class GlobalAttackHelper
implements AttackHelper {
    private static final GlobalAttackHelper INSTANCE = new GlobalAttackHelper();
    private boolean noCrystal;
    private boolean attacked;
    private boolean invalid;

    private GlobalAttackHelper() {
        Bus.EVENT_BUS.register(new EventListener<MotionUpdateEvent>(MotionUpdateEvent.class, Integer.MIN_VALUE){

            @Override
            public void invoke(MotionUpdateEvent event) {
                if (event.getStage() == Stage.POST) {
                    GlobalAttackHelper.this.attacked = false;
                    GlobalAttackHelper.this.noCrystal = false;
                    GlobalAttackHelper.this.invalid = false;
                }
            }
        });
    }

    public static GlobalAttackHelper getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean attackAny(List<Entity> entities, AttackingModule module) {
        if (this.invalid) {
            return false;
        }
        if (this.attacked || this.noCrystal) {
            return true;
        }
        Entity best = null;
        boolean noCrystal = true;
        float minDamage = Float.MAX_VALUE;
        for (Entity entity : entities) {
            if (!(entity instanceof EntityEnderCrystal) || EntityUtil.isDead(entity) || !(entity.getDistanceSq((Entity)RotationUtil.getRotationPlayer()) < 36.0)) continue;
            noCrystal = false;
            if (!HelperUtil.valid(entity, module.getRange(), module.getTrace())) continue;
            float damage = DamageUtil.calculate(entity);
            if (!module.getPop().shouldPop(damage, module.getPopTime()) || !(damage < minDamage)) continue;
            best = entity;
            minDamage = damage;
        }
        if (best != null) {
            PacketUtil.attack(best);
            this.attacked = true;
            return true;
        }
        if (!noCrystal) {
            this.invalid = true;
            return false;
        }
        return true;
    }

    public void setAttacked(boolean attacked) {
        this.attacked = attacked;
    }
}

