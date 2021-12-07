package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityEnderCrystal;

public class FakeCrystalRender
implements Globals {
    private final List<EntityEnderCrystal> crystals = new ArrayList<EntityEnderCrystal>();
    private final Setting<Integer> simulate;

    public FakeCrystalRender(Setting<Integer> simulate) {
        this.simulate = simulate;
    }

    public void addFakeCrystal(EntityEnderCrystal crystal) {
        crystal.setShowBottom(false);
        mc.addScheduledTask(() -> {
            Iterator iterator;
            if (FakeCrystalRender.mc.world != null && (iterator = FakeCrystalRender.mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, crystal.getEntityBoundingBox()).iterator()).hasNext()) {
                EntityEnderCrystal entity = (EntityEnderCrystal)iterator.next();
                crystal.innerRotation = entity.innerRotation;
            }
            this.crystals.add(crystal);
        });
    }

    public void onSpawn(EntityEnderCrystal crystal) {
        Iterator<EntityEnderCrystal> itr = this.crystals.iterator();
        while (itr.hasNext()) {
            EntityEnderCrystal fake = itr.next();
            if (!fake.getEntityBoundingBox().intersects(crystal.getEntityBoundingBox())) continue;
            crystal.innerRotation = fake.innerRotation;
            itr.remove();
        }
    }

    public void tick() {
        if (this.simulate.getValue() == 0) {
            this.crystals.clear();
            return;
        }
        Iterator<EntityEnderCrystal> itr = this.crystals.iterator();
        while (itr.hasNext()) {
            EntityEnderCrystal crystal = itr.next();
            crystal.onUpdate();
            if (++crystal.ticksExisted < this.simulate.getValue()) continue;
            crystal.setDead();
            itr.remove();
        }
    }

    public void render(float partialTicks) {
        RenderManager manager = mc.getRenderManager();
        for (EntityEnderCrystal crystal : this.crystals) {
            manager.renderEntityStatic(crystal, partialTicks, false);
        }
    }

    public void clear() {
        this.crystals.clear();
    }
}
