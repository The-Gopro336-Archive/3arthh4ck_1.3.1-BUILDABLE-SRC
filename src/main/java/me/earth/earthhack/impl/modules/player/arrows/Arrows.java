package me.earth.earthhack.impl.modules.player.arrows;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.modules.player.arrows.ListenerKeyboard;
import me.earth.earthhack.impl.modules.player.arrows.ListenerMotion;
import me.earth.earthhack.impl.modules.player.arrows.ListenerUseItem;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemSpectralArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;

public class Arrows
extends RegisteringModule<Boolean, SimpleRemovingSetting> {
    protected static final PotionType SPECTRAL = new PotionType(new PotionEffect[0]);
    protected static final Set<PotionType> BAD_TYPES = Sets.newHashSet(PotionTypes.EMPTY, PotionTypes.WATER, PotionTypes.MUNDANE, PotionTypes.THICK, PotionTypes.AWKWARD, PotionTypes.HEALING, PotionTypes.STRONG_HEALING, PotionTypes.STRONG_HARMING, PotionTypes.HARMING);
    protected final Setting<Boolean> shoot = this.register(new BooleanSetting("Shoot", false));
    protected final Setting<Boolean> cycle = this.register(new BooleanSetting("Cycle-Shoot", true));
    protected final Setting<Boolean> autoRelease = this.register(new BooleanSetting("Auto-Release", false));
    protected final Setting<Integer> releaseTicks = this.register(new NumberSetting<Integer>("Release-Ticks", 3, 0, 20));
    protected final Setting<Integer> maxTicks = this.register(new NumberSetting<Integer>("Max-Ticks", 10, 0, 20));
    protected final Setting<Boolean> tpsSync = this.register(new BooleanSetting("Tps-Sync", true));
    protected final Setting<Integer> cancelTime = this.register(new NumberSetting<Integer>("Cancel-Time", 0, 0, 500));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Cycle-Delay", 250, 0, 500));
    protected final Setting<Integer> shootDelay = this.register(new NumberSetting<Integer>("Shoot-Delay", 500, 0, 500));
    protected final Setting<Integer> minDura = this.register(new NumberSetting<Integer>("Min-Potion", 0, 0, 1000));
    protected final Setting<Bind> cycleButton = this.register(new BindSetting("Cycle-Bind", Bind.none()));
    protected final Setting<Boolean> keyCycle = this.register(new BooleanSetting("Bind-Cycle-BlackListed", true));
    protected final Setting<Boolean> preCycle = this.register(new BooleanSetting("Fast-Cycle", false));
    protected final Setting<Boolean> fastCancel = this.register(new BooleanSetting("Fast-Cancel", false));
    protected final Set<PotionType> cycled = new HashSet<PotionType>();
    protected final StopWatch cycleTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected boolean fast;

    public Arrows() {
        super("Arrows", Category.Player, "Add_Potion", "potion", SimpleRemovingSetting::new, s -> "Black/Whitelist " + s.getName() + " potion arrows.");
        this.listType.setValue(ListType.BlackList);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerUseItem(this));
        this.listeners.add(new ListenerKeyboard(this));
        SimpleData data = new SimpleData(this, "Cycles through your arrows. Not compatible with AntiPotion.");
        this.setData(data);
    }

    @Override
    protected void onEnable() {
        this.fast = false;
    }

    @Override
    public String getInput(String input, boolean add) {
        if (add) {
            String potionName = Arrows.getPotionNameStartingWith(input);
            if (potionName != null) {
                return TextUtil.substring(potionName, input.length());
            }
            return "";
        }
        return super.getInput(input, false);
    }

    @Override
    public String getDisplayInfo() {
        ItemStack stack = this.findArrow();
        if (!stack.isEmpty()) {
            return stack.getItem().getItemStackDisplayName(stack).replace("Arrow of ", "").replace(" Arrow", "");
        }
        return null;
    }

    protected boolean badStack(ItemStack stack) {
        return this.badStack(stack, true, Collections.emptySet());
    }

    protected boolean badStack(ItemStack stack, boolean checkType, Set<PotionType> cycled) {
        PotionType type = PotionUtils.getPotionFromItem((ItemStack)stack);
        if (stack.getItem() instanceof ItemSpectralArrow) {
            type = SPECTRAL;
        }
        if (cycled.contains(type)) {
            return true;
        }
        if (checkType) {
            if (BAD_TYPES.contains(type)) {
                return true;
            }
        } else if (this.keyCycle.getValue().booleanValue() || type.getEffects().isEmpty() && this.isValid("none")) {
            return false;
        }
        if (stack.getItem() instanceof ItemSpectralArrow) {
            return !this.isValid("Spectral") || Arrows.mc.player.isGlowing();
        }
        boolean inValid = true;
        for (PotionEffect e : type.getEffects()) {
            if (!this.isValid(I18n.format((String)e.getPotion().getName(), (Object[])new Object[0]))) {
                return true;
            }
            PotionEffect eff = Arrows.mc.player.getActivePotionEffect(e.getPotion());
            if (eff != null && eff.getDuration() >= this.minDura.getValue()) continue;
            inValid = false;
        }
        if (!checkType && !this.keyCycle.getValue().booleanValue()) {
            return false;
        }
        return inValid;
    }

    public void cycle(boolean recursive, boolean key) {
        if (!InventoryUtil.validScreen() || key && !this.cycleTimer.passed(this.delay.getValue().intValue())) {
            return;
        }
        int firstSlot = -1;
        int secondSlot = -1;
        ItemStack arrow = null;
        if (this.isArrow(Arrows.mc.player.getHeldItem(EnumHand.OFF_HAND))) {
            firstSlot = 45;
        }
        if (this.isArrow(Arrows.mc.player.getHeldItem(EnumHand.MAIN_HAND))) {
            if (firstSlot == -1) {
                firstSlot = InventoryUtil.hotbarToInventory(Arrows.mc.player.inventory.currentItem);
            } else if (!this.badStack(Arrows.mc.player.getHeldItem(EnumHand.MAIN_HAND), key, this.cycled)) {
                secondSlot = InventoryUtil.hotbarToInventory(Arrows.mc.player.inventory.currentItem);
                arrow = Arrows.mc.player.getHeldItem(EnumHand.MAIN_HAND);
            }
        }
        if (!this.badStack(Arrows.mc.player.inventory.getItemStack(), key, this.cycled)) {
            secondSlot = -2;
            arrow = Arrows.mc.player.inventory.getItemStack();
        }
        if (firstSlot == -1 || secondSlot == -1) {
            for (int i = 0; i < Arrows.mc.player.inventory.getSizeInventory(); ++i) {
                ItemStack stack = Arrows.mc.player.inventory.getStackInSlot(i);
                if (!this.isArrow(stack)) continue;
                if (firstSlot == -1) {
                    firstSlot = InventoryUtil.hotbarToInventory(i);
                    continue;
                }
                if (this.badStack(stack, key, this.cycled)) continue;
                secondSlot = InventoryUtil.hotbarToInventory(i);
                arrow = stack;
                break;
            }
        }
        if (firstSlot == -1) {
            return;
        }
        if (secondSlot == -1) {
            if (!recursive && !this.cycled.isEmpty()) {
                this.cycled.clear();
                this.cycle(true, key);
            }
            return;
        }
        PotionType type = PotionUtils.getPotionFromItem((ItemStack)arrow);
        if (arrow.getItem() instanceof ItemSpectralArrow) {
            type = SPECTRAL;
        }
        this.cycled.add(type);
        int finalFirstSlot = firstSlot;
        int finalSecondSlot = secondSlot;
        Item inFirst = InventoryUtil.get(finalFirstSlot).getItem();
        Item inSecond = InventoryUtil.get(finalSecondSlot).getItem();
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
            if (InventoryUtil.get(finalFirstSlot).getItem() == inFirst && InventoryUtil.get(finalSecondSlot).getItem() == inSecond) {
                if (finalSecondSlot == -2) {
                    InventoryUtil.click(finalFirstSlot);
                } else {
                    InventoryUtil.click(finalSecondSlot);
                    InventoryUtil.click(finalFirstSlot);
                    InventoryUtil.click(finalSecondSlot);
                }
            }
        });
        this.cycleTimer.reset();
    }

    protected ItemStack findArrow() {
        if (this.isArrow(Arrows.mc.player.getHeldItem(EnumHand.OFF_HAND))) {
            return Arrows.mc.player.getHeldItem(EnumHand.OFF_HAND);
        }
        if (this.isArrow(Arrows.mc.player.getHeldItem(EnumHand.MAIN_HAND))) {
            return Arrows.mc.player.getHeldItem(EnumHand.MAIN_HAND);
        }
        for (int i = 0; i < Arrows.mc.player.inventory.getSizeInventory(); ++i) {
            ItemStack stack = Arrows.mc.player.inventory.getStackInSlot(i);
            if (!this.isArrow(stack)) continue;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    protected boolean isArrow(ItemStack stack) {
        return stack.getItem() instanceof ItemArrow;
    }

    public static String getPotionNameStartingWith(String name) {
        Potion potion = Arrows.getPotionStartingWith(name);
        if (potion == SpecialPot.SPECTRAL) {
            return "Spectral";
        }
        if (potion == SpecialPot.NONE) {
            return "None";
        }
        if (potion != null) {
            return I18n.format((String)potion.getName(), (Object[])new Object[0]);
        }
        return null;
    }

    public static Potion getPotionStartingWith(String name) {
        if (name == null) {
            return null;
        }
        name = name.toLowerCase();
        Iterator iterator = Potion.REGISTRY.iterator();
        while (iterator.hasNext()) {
            Potion potion = (Potion)iterator.next();
            if (!I18n.format((String)potion.getName(), (Object[])new Object[0]).toLowerCase().startsWith(name)) continue;
            return potion;
        }
        if ("spectral".startsWith(name)) {
            return SpecialPot.SPECTRAL;
        }
        if ("none".startsWith(name)) {
            return SpecialPot.NONE;
        }
        return null;
    }

    private static final class SpecialPot
    extends Potion {
        public static final SpecialPot SPECTRAL = new SpecialPot();
        public static final SpecialPot NONE = new SpecialPot();

        private SpecialPot() {
            super(false, 0);
        }
    }
}
