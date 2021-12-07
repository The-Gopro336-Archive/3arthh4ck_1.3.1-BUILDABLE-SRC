package me.earth.earthhack.impl.modules.misc.autoregear;

import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.modules.misc.autoregear.ListenerKeyPress;
import me.earth.earthhack.impl.modules.misc.autoregear.ListenerMotion;
import me.earth.earthhack.impl.modules.misc.autoregear.ListenerTick;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.helpers.blocks.BlockPlacingModule;
import me.earth.earthhack.impl.util.helpers.command.CustomCommandModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public class AutoRegear
extends BlockPlacingModule
implements CustomCommandModule {
    private static final String[] ARGS = new String[]{"SAVE", "RESET"};
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 50, 0, 500));
    protected final Setting<Float> range = this.register(new NumberSetting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(8.0f)));
    protected final Setting<Bind> regear = this.register(new BindSetting("Regear", Bind.none()));
    protected final Setting<Boolean> grabShulker = this.register(new BooleanSetting("GrabShulker", false));
    protected final Setting<Boolean> placeShulker = this.register(new BooleanSetting("PlaceShulker", false));
    protected final Setting<Boolean> placeEchest = this.register(new BooleanSetting("PlaceEchest", false));
    protected final Setting<Boolean> steal = this.register(new BooleanSetting("Steal", false));
    protected StopWatch delayTimer = new StopWatch();
    protected boolean shouldRegear;

    public AutoRegear() {
        super("AutoRegear", Category.Misc);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerKeyPress(this));
        this.listeners.add(new ListenerMotion(this));
    }

    @Override
    protected void onEnable() {
        this.shouldRegear = false;
    }

    public void unregisterAll() {
        for (int i = 0; i < 28; ++i) {
            if (this.getSettingFromSlot(i) == null) continue;
            this.unregister(this.getSettingFromSlot(i));
        }
    }

    public void registerInventory() {
        for (int i = 9; i < 45; ++i) {
            int id = Item.REGISTRY.getIDForObject(((ItemStack)AutoRegear.mc.player.inventoryContainer.getInventory().get(i)).getItem());
            this.register(new SimpleRemovingSetting(i - 9 + ":" + id));
        }
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length == 1) {
            this.unregisterAll();
            this.registerInventory();
            ChatUtil.sendMessage("\u00a7aKit saved!");
            return true;
        }
        return false;
    }

    @Override
    public Setting<?> getSettingConfig(String name) {
        if (this.getSetting(name) == null) {
            SimpleRemovingSetting newSetting = new SimpleRemovingSetting(name);
            this.register(newSetting);
            return newSetting;
        }
        return this.getSetting(name);
    }

    public Setting<?> getSettingFromSlot(int slot) {
        for (Setting<?> setting : this.getSettings()) {
            if (!setting.getName().startsWith(Integer.toString(slot))) continue;
            return setting;
        }
        return null;
    }

    private int safetyFactor(BlockPos pos) {
        return this.safety(pos) + this.safety(pos.up());
    }

    private int safety(BlockPos pos) {
        int safety = 0;
        for (EnumFacing facing : EnumFacing.values()) {
            if (AutoRegear.mc.world.getBlockState(pos.offset(facing)).func_185904_a().isReplaceable()) continue;
            ++safety;
        }
        return safety;
    }

    public BlockPos getBlock(Block type) {
        AtomicReference block = new AtomicReference();
        BlockUtil.sphere(this.range.getValue().floatValue(), pos -> {
            if (AutoRegear.mc.world.getBlockState((BlockPos)pos).getBlock() == type) {
                block.set(pos);
            }
            return false;
        });
        return (BlockPos)block.get();
    }

    public BlockPos getShulkerBox() {
        AtomicReference block = new AtomicReference();
        BlockUtil.sphere(this.range.getValue().floatValue(), pos -> {
            if (AutoRegear.mc.world.getBlockState((BlockPos)pos).getBlock() instanceof BlockShulkerBox) {
                block.set(pos);
            }
            return false;
        });
        return (BlockPos)block.get();
    }

    public BlockPos getOptimalPlacePos(boolean shulkerCheck) {
        HashSet positions = new HashSet();
        BlockUtil.sphere(this.range.getValue().floatValue(), pos -> {
            if (AutoRegear.mc.world.getBlockState((BlockPos)pos).getBlock().isReplaceable(AutoRegear.mc.world, (BlockPos)pos) && this.entityCheck((BlockPos)pos) && AutoRegear.mc.world.getBlockState(pos.up()).getBlock().isReplaceable(AutoRegear.mc.world, (BlockPos)pos) && (!shulkerCheck || AutoRegear.mc.world.getBlockState(pos.down()).getBlock().isReplaceable(AutoRegear.mc.world, (BlockPos)pos))) {
                positions.add(pos);
            }
            return false;
        });
        BlockPos position = (BlockPos)positions.stream().sorted(Comparator.comparingInt(pos -> this.safety((BlockPos)pos) * -1)).collect(Collectors.toList()).get(0);
        return position;
    }

    public boolean hasKit() {
        for (int i = 9; i < 45; ++i) {
            NBTTagCompound blockEntityTag;
            NBTTagCompound tagCompound;
            boolean foundExp = false;
            boolean foundCrystals = false;
            boolean foundGapples = false;
            ItemStack stack = (ItemStack)AutoRegear.mc.player.inventoryContainer.getInventory().get(i);
            if (!(stack.getItem() instanceof ItemShulkerBox) || (tagCompound = stack.getTagCompound()) == null || !tagCompound.hasKey("BlockEntityTag", 10) || !(blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag")).hasKey("Items", 9)) continue;
            NonNullList nonNullList = NonNullList.withSize((int)27, (Object)ItemStack.EMPTY);
            ItemStackHelper.loadAllItems((NBTTagCompound)blockEntityTag, (NonNullList)nonNullList);
            for (ItemStack stack1 : nonNullList) {
                if (stack1.getItem() == Items.GOLDEN_APPLE) {
                    foundGapples = true;
                    continue;
                }
                if (stack1.getItem() == Items.EXPERIENCE_BOTTLE) {
                    foundExp = true;
                    continue;
                }
                if (stack1.getItem() != Items.END_CRYSTAL) continue;
                foundCrystals = true;
            }
            if (!foundExp || !foundGapples || !foundCrystals) continue;
            return true;
        }
        return false;
    }
}
