/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.util.EnumHand
 */
package me.earth.earthhack.impl.modules.combat.snowballer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.modules.combat.snowballer.ListenerMotion;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class Snowballer
extends Module {
    protected float[] rotations;
    protected Entity target = null;
    protected Set<Integer> blackList = new HashSet<Integer>();
    protected StopWatch timer = new StopWatch();
    private int lastSlot;
    private boolean shouldThrow;
    final Setting<Float> range = this.register(new NumberSetting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(6.0f)));
    final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 50, 0, 500));
    final Setting<Boolean> swap = this.register(new BooleanSetting("Swap", true));
    final Setting<Boolean> back = this.register(new BooleanSetting("SwapBack", true));
    final Setting<Boolean> blacklist = this.register(new BooleanSetting("Blacklist", true));

    public Snowballer() {
        super("Snowballer", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.timer.reset();
    }

    protected void runPre(MotionUpdateEvent event) {
        if ((this.timer.passed(this.delay.getValue().intValue()) || this.delay.getValue() == 0) && (this.swap.getValue().booleanValue() || InventoryUtil.isHolding(Items.SNOWBALL))) {
            int slot = InventoryUtil.findHotbarItem(Items.SNOWBALL, new Item[0]);
            this.lastSlot = Snowballer.mc.player.inventory.currentItem;
            List<Entity> entities = this.getCrystals(this.range.getValue().floatValue());
            for (Entity entity : entities) {
                if (!RayTraceUtil.canBeSeen(entity, (EntityLivingBase)Snowballer.mc.player) || this.blackList.contains(entity.getEntityId())) continue;
                this.target = entity;
            }
            if (this.target != null) {
                if (this.swap.getValue().booleanValue() && slot != -1 && !InventoryUtil.isHolding(Items.SNOWBALL)) {
                    InventoryUtil.switchTo(slot);
                }
                this.rotations = RotationUtil.getRotations(this.target);
                event.setYaw(this.rotations[0]);
                event.setPitch(this.rotations[1]);
                if (this.blacklist.getValue().booleanValue()) {
                    this.blackList.add(this.target.getEntityId());
                }
                this.target = null;
                this.shouldThrow = true;
            }
        }
    }

    protected void runPost(MotionUpdateEvent event) {
        if (this.shouldThrow && (this.timer.passed(this.delay.getValue().intValue()) || this.delay.getValue() == 0) && InventoryUtil.isHolding(Items.SNOWBALL)) {
            this.shouldThrow = false;
            boolean offhand = Snowballer.mc.player.getHeldItemOffhand().getItem() == Items.SNOWBALL;
            CPacketPlayerTryUseItem packet = new CPacketPlayerTryUseItem(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            Snowballer.mc.player.connection.sendPacket((Packet)packet);
            if (this.swap.getValue().booleanValue() && this.back.getValue().booleanValue()) {
                InventoryUtil.switchTo(this.lastSlot);
            }
            this.timer.reset();
        }
    }

    protected List<Entity> getCrystals(float range) {
        ArrayList loadedEntities = new ArrayList(Snowballer.mc.world.loadedEntityList);
        return loadedEntities.stream().filter(entity -> Snowballer.mc.player.getDistanceSq(entity) <= (double)(range * range)).filter(entity -> entity instanceof EntityEnderCrystal).sorted(Comparator.comparingDouble(entity -> Snowballer.mc.player.getDistanceSq(entity))).collect(Collectors.toList());
    }
}

