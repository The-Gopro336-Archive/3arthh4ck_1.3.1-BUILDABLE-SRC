/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.AxisAlignedBB
 *  org.lwjgl.input.Mouse
 */
package me.earth.earthhack.impl.modules.player.exptweaks;

import java.util.List;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.exptweaks.ListenerMiddleClick;
import me.earth.earthhack.impl.modules.player.exptweaks.ListenerMotion;
import me.earth.earthhack.impl.modules.player.exptweaks.ListenerUseItem;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.input.Mouse;

public class ExpTweaks
extends Module {
    protected final Setting<Boolean> feetExp = this.register(new BooleanSetting("FeetExp", false));
    protected final Setting<Integer> expPackets = this.register(new NumberSetting<Integer>("ExpPackets", 0, 0, 64));
    protected final Setting<Boolean> wasteStop = this.register(new BooleanSetting("WasteStop", false));
    protected final Setting<Integer> stopDura = this.register(new NumberSetting<Integer>("Stop-Dura", 100, 0, 100));
    protected final Setting<Integer> wasteIf = this.register(new NumberSetting<Integer>("WasteIf", 30, 0, 100));
    protected final Setting<Boolean> wasteLoot = this.register(new BooleanSetting("WasteLoot", true));
    protected final Setting<Boolean> packetsInLoot = this.register(new BooleanSetting("PacketsInLoot", true));
    protected final Setting<Double> grow = this.register(new NumberSetting<Double>("Grow", 0.0, 0.0, 5.0));
    protected final Setting<Boolean> middleClickExp = this.register(new BooleanSetting("MiddleClickExp", false));
    protected final Setting<Integer> mcePackets = this.register(new NumberSetting<Integer>("MCE-Packets", 0, 0, 64));
    protected final Setting<Boolean> silent = this.register(new BooleanSetting("Silent", true));
    protected final Setting<Boolean> whileEating = this.register(new BooleanSetting("WhileEating", true));
    protected final Setting<Bind> mceBind = this.register(new BindSetting("MCE-Bind", Bind.none()));
    protected boolean justCancelled;
    protected boolean isMiddleClick;
    protected int lastSlot = -1;

    public ExpTweaks() {
        super("ExpTweaks", Category.Player);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerUseItem(this));
        this.listeners.add(new ListenerMiddleClick(this));
        SimpleData data = new SimpleData(this, "Tweaks for Experience Orbs/Bottles.");
        data.register(this.feetExp, "Will silently look at your feet when you are mending.");
        data.register(this.expPackets, "Sends more packets to make mending faster. 10 is a good value, but can waste exp!");
        data.register(this.wasteStop, "Will stop you from throwing Experience if your Armor has full durability.");
        data.register(this.wasteIf, "Will not use WasteStop if you one of your armor pieces has less durability (%) than this value.");
        data.register(this.wasteLoot, "Wastes Exp when you are standing in Exp Bottles.");
        this.setData(data);
    }

    @Override
    protected void onEnable() {
        this.isMiddleClick = false;
        this.justCancelled = false;
        this.lastSlot = -1;
    }

    @Override
    protected void onDisable() {
        if (this.lastSlot != -1) {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> InventoryUtil.switchTo(this.lastSlot));
            this.lastSlot = -1;
        }
    }

    public boolean isMiddleClick() {
        return this.middleClickExp.getValue() != false && (Mouse.isButtonDown((int)2) && this.mceBind.getValue().getKey() == -1 || KeyBoardUtil.isKeyDown(this.mceBind));
    }

    public boolean isWastingLoot(List<Entity> entities) {
        if (entities != null) {
            AxisAlignedBB bb = RotationUtil.getRotationPlayer().getEntityBoundingBox().grow(this.grow.getValue().doubleValue(), this.grow.getValue().doubleValue(), this.grow.getValue().doubleValue());
            for (Entity entity : entities) {
                if (!(entity instanceof EntityItem) || entity.isDead || ((EntityItem)entity).getItem().getItem() != Items.EXPERIENCE_BOTTLE || !entity.getEntityBoundingBox().intersects(bb)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean isWasting() {
        List<Entity> entities;
        if (this.wasteLoot.getValue().booleanValue() && this.isWastingLoot(entities = Managers.ENTITIES.getEntitiesAsync())) {
            return false;
        }
        boolean empty = true;
        boolean full = false;
        for (int i = 5; i < 9; ++i) {
            ItemStack stack = ExpTweaks.mc.player.inventoryContainer.getSlot(i).getStack();
            if (stack.isEmpty()) continue;
            empty = false;
            float percent = DamageUtil.getPercent(stack);
            if (percent >= (float)this.stopDura.getValue().intValue()) {
                full = true;
                continue;
            }
            if (!(percent <= (float)this.wasteIf.getValue().intValue())) continue;
            return false;
        }
        return empty || full;
    }

    public boolean cancelShrink() {
        boolean just = this.justCancelled;
        this.justCancelled = false;
        return this.isEnabled() && this.wasteStop.getValue() != false && just;
    }
}

