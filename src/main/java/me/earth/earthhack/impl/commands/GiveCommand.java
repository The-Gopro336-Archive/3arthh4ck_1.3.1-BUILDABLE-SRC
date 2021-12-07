/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 */
package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.abstracts.AbstractStackCommand;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.addable.ItemAddingModule;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GiveCommand
extends AbstractStackCommand {
    private boolean local = true;
    private int amount;
    private Item item;

    public GiveCommand() {
        super((String[][])new String[][]{{"give"}, {"amount", "local"}, {"item/block"}}, "");
        CommandDescriptions.register(this, "Gives you an Item.");
    }

    @Override
    public void execute(String[] args) {
        int amount;
        if (args.length == 1) {
            ChatUtil.sendMessage("Use this command to give yourself an item.");
            return;
        }
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("local")) {
                boolean bl = this.local = !this.local;
                if (this.local) {
                    Managers.CHAT.sendDeleteMessage("\u00a7aThe Give command now uses localized names, that means that you can use normal names like \u00a7bEnder Chest\u00a7a now.", "giveCommand", 3000);
                } else {
                    Managers.CHAT.sendDeleteMessage("\u00a7aThe Give command now uses ids, that means that you need to use names like \u00a7bminecraft:apple\u00a7a or ids now.", "giveCommand", 3000);
                }
                return;
            }
            ChatUtil.sendMessage("Please specify an item.");
            return;
        }
        try {
            amount = Integer.parseInt(args[1]);
        }
        catch (Exception e) {
            ChatUtil.sendMessage("\u00a7cCould not parse \u00a7f" + args[1] + "\u00a7c" + " to a number!");
            return;
        }
        String conc = CommandUtil.concatenate(args, 2);
        this.item = this.local ? ItemAddingModule.getItemStartingWith(conc, i -> true) : Item.getByNameOrId((String)conc);
        if (this.item == null) {
            ChatUtil.sendMessage("\u00a7cCould not find item \u00a7f" + conc + "\u00a7c" + "! Give command currently uses " + (this.local ? "localized names." : "ids."));
            return;
        }
        this.amount = amount;
        this.stackName = conc;
        super.execute(args);
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        if (args.length <= 1) {
            return super.getPossibleInputs(args);
        }
        PossibleInputs inputs = PossibleInputs.empty();
        if ("local".startsWith(args[1].toLowerCase())) {
            if (args.length > 2) {
                return inputs;
            }
            return inputs.setCompletion(TextUtil.substring("local", args[1].length()));
        }
        if (args.length == 2) {
            if (args[1].isEmpty()) {
                return inputs.setRest("<amount/local> <item/block>");
            }
            return inputs.setRest(" <item/block>");
        }
        String conc = CommandUtil.concatenate(args, 2);
        if (conc.isEmpty()) {
            return inputs.setRest(" <item/block>");
        }
        if (this.local) {
            String s = ItemAddingModule.getItemStartingWithDefault(conc, i -> true);
            if (s != null) {
                inputs.setCompletion(TextUtil.substring(s, conc.length()));
            }
        } else {
            if (args.length == 3 && Character.isDigit(conc.charAt(0))) {
                try {
                    int id = Integer.parseInt(args[2]);
                    Item item = Item.getItemById((int)id);
                    if (item != null) {
                        return inputs.setRest(" <" + item.getItemStackDisplayName(new ItemStack(item)) + ">");
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            for (ResourceLocation location : Item.REGISTRY.getKeys()) {
                if (!TextUtil.startsWith(location.toString(), conc)) continue;
                return inputs.setCompletion(TextUtil.substring(location.toString(), conc.length()));
            }
        }
        return inputs;
    }

    @Override
    protected ItemStack getStack(String[] args) {
        if (this.item == null) {
            ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
            stack.setStackDisplayName("\u00a7cERROR");
            return stack;
        }
        ItemStack stack = new ItemStack(this.item);
        stack.setCount(this.amount);
        return stack;
    }
}

