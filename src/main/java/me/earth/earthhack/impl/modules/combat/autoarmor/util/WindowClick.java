/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.PlayerControllerMP
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.item.ItemStack
 */
package me.earth.earthhack.impl.modules.combat.autoarmor.util;

import java.util.function.Consumer;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

public class WindowClick
implements Globals {
    private final int slot;
    private final int drag;
    private final int target;
    private final ItemStack inSlot;
    private final ItemStack inDrag;
    private final Consumer<PlayerControllerMP> action;
    private boolean doubleClick;
    private Runnable post;
    private boolean fast;

    public WindowClick(int slot, ItemStack inSlot, ItemStack inDrag) {
        this(slot, inSlot, inDrag, slot);
    }

    public WindowClick(int slot, ItemStack inSlot, ItemStack inDrag, int target) {
        this(slot, inSlot, inDrag, target, p -> p.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)WindowClick.mc.player));
    }

    public WindowClick(int slot, ItemStack inSlot, ItemStack inDrag, int target, Consumer<PlayerControllerMP> action) {
        this(slot, inSlot, -2, inDrag, target, action);
    }

    public WindowClick(int slot, ItemStack inSlot, int dragSlot, ItemStack inDrag, int target, Consumer<PlayerControllerMP> action) {
        this.slot = slot;
        this.inSlot = inSlot;
        this.inDrag = inDrag;
        this.target = target;
        this.action = action;
        this.drag = dragSlot;
    }

    public void runClick(PlayerControllerMP controller) {
        ItemStack stack = null;
        ItemStack drag = null;
        if (this.slot != -1 && this.slot != -999) {
            stack = InventoryUtil.get(this.slot);
            drag = WindowClick.mc.player.inventory.getItemStack();
        }
        this.action.accept(controller);
        if (!(this.slot == -1 || this.slot == -999 || !this.fast || InventoryUtil.equals(stack, WindowClick.mc.player.inventory.getItemStack()) && InventoryUtil.equals(drag, InventoryUtil.get(this.slot)))) {
            InventoryUtil.put(this.slot, WindowClick.mc.player.inventory.getItemStack());
            WindowClick.mc.player.inventory.setItemStack(stack);
        }
        if (this.post != null) {
            this.post.run();
        }
    }

    public boolean isValid() {
        ItemStack stack;
        if (WindowClick.mc.player != null && InventoryUtil.equals(stack = InventoryUtil.get(this.drag), this.inDrag)) {
            if (this.slot < 0) {
                return true;
            }
            stack = InventoryUtil.get(this.slot);
            return InventoryUtil.equals(stack, this.inSlot);
        }
        return false;
    }

    public int getSlot() {
        return this.slot;
    }

    public int getTarget() {
        return this.target;
    }

    public boolean isDoubleClick() {
        return this.doubleClick;
    }

    public void setDoubleClick(boolean doubleClick) {
        this.doubleClick = doubleClick;
    }

    public void addPost(Runnable post) {
        this.post = post;
    }

    public void setFast(boolean fast) {
        this.fast = fast;
    }
}

