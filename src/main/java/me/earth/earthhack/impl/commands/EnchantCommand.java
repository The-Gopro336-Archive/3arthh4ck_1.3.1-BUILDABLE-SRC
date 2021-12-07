package me.earth.earthhack.impl.commands;

import java.util.Iterator;
import java.util.Objects;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.util.text.translation.I18n;

public class EnchantCommand
extends Command
implements Globals {
    public EnchantCommand() {
        super(new String[][]{{"enchant"}, {"level"}, {"enchantment"}});
    }

    @Override
    public void execute(String[] args) {
        short level;
        if (args.length == 1) {
            ChatUtil.sendMessage("\u00a7cPlease specify a level!");
            return;
        }
        if (EnchantCommand.mc.player == null) {
            ChatUtil.sendMessage("\u00a7cYou need to be ingame to use this command!");
            return;
        }
        try {
            level = (short)Integer.parseInt(args[1]);
        }
        catch (Exception e) {
            ChatUtil.sendMessage("\u00a7cCould not parse level \u00a7f" + args[1] + "\u00a7c" + "!");
            return;
        }
        ItemStack stack = EnchantCommand.mc.player.inventory.getCurrentItem();
        if (stack.isEmpty()) {
            ChatUtil.sendMessage("\u00a7cYou need to be holding an item for this command!");
            return;
        }
        if (args.length > 2) {
            String conc = CommandUtil.concatenate(args, 2);
            Enchantment enchantment = EnchantCommand.getEnchantment(conc);
            if (enchantment == null) {
                ChatUtil.sendMessage("\u00a7cCould find Enchantment \u00a7f" + conc + "\u00a7c" + "!");
                return;
            }
            stack.addEnchantment(enchantment, level);
            this.setStack(stack);
            return;
        }
        Iterator iterator = Enchantment.REGISTRY.iterator();
        while (iterator.hasNext()) {
            Enchantment enchantment = (Enchantment)iterator.next();
            if (enchantment.isCurse()) continue;
            stack.addEnchantment(enchantment, level);
        }
        this.setStack(stack);
    }

    private void setStack(ItemStack stack) {
        int slot = EnchantCommand.mc.player.inventory.currentItem + 36;
        if (EnchantCommand.mc.player.isCreative()) {
            EnchantCommand.mc.player.connection.sendPacket(new CPacketCreativeInventoryAction(slot, stack));
        } else if (mc.isSingleplayer()) {
            EntityPlayerMP player = Objects.requireNonNull(mc.getIntegratedServer()).func_184103_al().getPlayerByUUID(EnchantCommand.mc.player.getUniqueID());
            if (player != null) {
                player.inventoryContainer.putStackInSlot(slot, stack);
            }
        } else {
            ChatUtil.sendMessage("\u00a7cNot Creative and not Singleplayer: Enchantments are \u00a7bghost \u00a7cenchantments!");
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        if (args.length > 1) {
            PossibleInputs inputs = PossibleInputs.empty();
            if (args.length == 2) {
                inputs.setRest(" <enchantment>");
                if (args[1].isEmpty()) {
                    return inputs.setRest("<level>");
                }
                return inputs;
            }
            if (args[2].isEmpty()) {
                return inputs.setRest("<enchantment>");
            }
            String conc = CommandUtil.concatenate(args, 2);
            String s = EnchantCommand.getEnchantmentStartingWith(conc);
            if (s != null) {
                inputs.setCompletion(TextUtil.substring(s, conc.length()));
            }
            return inputs;
        }
        return super.getPossibleInputs(args);
    }

    public static String getEnchantmentStartingWith(String prefix) {
        Enchantment enchantment = EnchantCommand.getEnchantment(prefix);
        if (enchantment != null) {
            return I18n.translateToLocal((String)enchantment.getName());
        }
        return null;
    }

    public static Enchantment getEnchantment(String prefix) {
        prefix = prefix.toLowerCase();
        Iterator iterator = Enchantment.REGISTRY.iterator();
        while (iterator.hasNext()) {
            Enchantment enchantment = (Enchantment)iterator.next();
            String s = I18n.translateToLocal((String)enchantment.getName());
            if (!s.toLowerCase().startsWith(prefix)) continue;
            return enchantment;
        }
        return null;
    }
}
