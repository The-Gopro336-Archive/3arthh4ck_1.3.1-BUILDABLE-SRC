package me.earth.earthhack.impl.commands.abstracts;

import java.util.Objects;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.ItemUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

public abstract class AbstractStackCommand
extends Command
implements Globals {
    protected String stackName;

    public AbstractStackCommand(String name, String stackName) {
        this((String[][])new String[][]{{name}}, stackName);
    }

    public AbstractStackCommand(String[][] args, String stackName) {
        super(args);
        this.stackName = stackName;
    }

    protected abstract ItemStack getStack(String[] var1);

    @Override
    public void execute(String[] args) {
        if (AbstractStackCommand.mc.player == null) {
            ChatUtil.sendMessage("\u00a7cYou need to be ingame for this command.");
            return;
        }
        boolean ghost = !AbstractStackCommand.mc.player.isCreative();
        boolean hotbar = true;
        int slot = InventoryUtil.findHotbarBlock(Blocks.AIR, new Block[0]);
        if (slot == -1) {
            hotbar = false;
            slot = AbstractStackCommand.findBlockNoDrag(Blocks.AIR);
            if (slot == -1) {
                Scheduler.getInstance().schedule(() -> mc.displayGuiScreen(new YesNoNonPausing((result, id) -> {
                    mc.displayGuiScreen(null);
                    if (result) {
                        this.setSlot(args, AbstractStackCommand.mc.player.inventory.currentItem, true, ghost);
                    }
                }, "\u00a7cYour inventory is full.", "Should your MainHand Slot be replaced?", 1337)));
                return;
            }
        }
        this.setSlot(args, slot, hotbar, ghost);
    }

    private void setSlot(String[] args, int slot, boolean hotbar, boolean ghost) {
        EntityPlayerMP player;
        if (AbstractStackCommand.mc.player == null) {
            return;
        }
        ItemStack stack = this.getStack(args);
        if (stack == null) {
            ChatUtil.sendMessage("<" + this.getName() + ">" + "\u00a7c" + " An error occurred.");
            return;
        }
        if (hotbar) {
            slot = InventoryUtil.hotbarToInventory(slot);
        }
        AbstractStackCommand.mc.player.inventoryContainer.putStackInSlot(slot, stack);
        if (AbstractStackCommand.mc.player.isCreative()) {
            AbstractStackCommand.mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(slot, stack));
        } else if (mc.isSingleplayer() && (player = Objects.requireNonNull(mc.getIntegratedServer()).func_184103_al().getPlayerByUUID(AbstractStackCommand.mc.player.getUniqueID())) != null) {
            player.inventoryContainer.putStackInSlot(slot, stack);
            ghost = false;
        }
        ChatUtil.sendMessage("\u00a7aGave you a " + (ghost ? "\u00a7c(ghost) " : "") + "\u00a7a" + this.stackName + ". It's in your " + "\u00a7f" + (slot == 45 ? "Offhand" : (hotbar ? "Hotbar" : "Inventory")) + "\u00a7a" + ".");
    }

    public static int findBlockNoDrag(Block block) {
        for (int i = 9; i < 45; ++i) {
            ItemStack stack = (ItemStack)AbstractStackCommand.mc.player.inventoryContainer.getInventory().get(i);
            if (!ItemUtil.areSame(stack, block)) continue;
            return i;
        }
        return -1;
    }
}
