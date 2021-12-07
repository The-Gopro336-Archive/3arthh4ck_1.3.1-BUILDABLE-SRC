package me.earth.earthhack.impl.modules.combat.autoarmor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmorData;
import me.earth.earthhack.impl.modules.combat.autoarmor.ListenerEntityProperties;
import me.earth.earthhack.impl.modules.combat.autoarmor.ListenerGameLoop;
import me.earth.earthhack.impl.modules.combat.autoarmor.ListenerSetSlot;
import me.earth.earthhack.impl.modules.combat.autoarmor.ListenerTick;
import me.earth.earthhack.impl.modules.combat.autoarmor.ListenerWorldClient;
import me.earth.earthhack.impl.modules.combat.autoarmor.modes.ArmorMode;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.DesyncClick;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.WindowClick;
import me.earth.earthhack.impl.modules.player.exptweaks.ExpTweaks;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Mouse;

public class AutoArmor
extends Module {
    private static final ModuleCache<ExpTweaks> EXP_TWEAKS = Caches.getModule(ExpTweaks.class);
    private static final ModuleCache<XCarry> XCARRY = Caches.getModule(XCarry.class);
    private static final ModuleCache<Suicide> SUICIDE = Caches.getModule(Suicide.class);
    protected final Setting<ArmorMode> mode = this.register(new EnumSetting<ArmorMode>("Mode", ArmorMode.Blast));
    protected final Setting<Boolean> fast = this.register(new BooleanSetting("Fast", true));
    protected final Setting<Boolean> safe = this.register(new BooleanSetting("Safe", true));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 50, 0, 500));
    protected final Setting<Boolean> autoMend = this.register(new BooleanSetting("AutoMend", false));
    protected final Setting<Integer> helmet = this.register(new NumberSetting<Integer>("Helmet%", 80, 1, 100));
    protected final Setting<Integer> chest = this.register(new NumberSetting<Integer>("Chest%", 85, 1, 100));
    protected final Setting<Integer> legs = this.register(new NumberSetting<Integer>("Legs%", 84, 1, 100));
    protected final Setting<Integer> boots = this.register(new NumberSetting<Integer>("Boots%", 80, 1, 100));
    protected final Setting<Boolean> curse = this.register(new BooleanSetting("CurseOfBinding", false));
    protected final Setting<Float> closest = this.register(new NumberSetting<Float>("Closest", Float.valueOf(12.0f), Float.valueOf(0.0f), Float.valueOf(30.0f)));
    protected final Setting<Float> maxDmg = this.register(new NumberSetting<Float>("MaxDmg", Float.valueOf(1.5f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
    protected final Setting<Boolean> newVer = this.register(new BooleanSetting("1.13+", false));
    protected final Setting<Boolean> newVerEntities = this.register(new BooleanSetting("1.13-Entities", false));
    protected final Setting<Boolean> bedCheck = this.register(new BooleanSetting("BedCheck", false));
    protected final Setting<Boolean> noDesync = this.register(new BooleanSetting("NoDesync", false));
    protected final Setting<Boolean> illegalSync = this.register(new BooleanSetting("Illegal-Sync", false));
    protected final Setting<Boolean> screenCheck = this.register(new BooleanSetting("CheckScreen", true));
    protected final Setting<Integer> desyncDelay = this.register(new NumberSetting<Integer>("DesyncDelay", 2500, 0, 5000));
    protected final Setting<Integer> checkDelay = this.register(new NumberSetting<Integer>("CheckDelay", 250, 0, 5000));
    protected final Setting<Integer> propertyDelay = this.register(new NumberSetting<Integer>("PropertyDelay", 500, 0, 5000));
    protected final Setting<Boolean> dragTakeOff = this.register(new BooleanSetting("Drag-Mend", false));
    protected final Setting<Boolean> prioLow = this.register(new BooleanSetting("Prio-Low", true));
    protected final Setting<Float> prioThreshold = this.register(new NumberSetting<Float>("Prio-Threshold", Float.valueOf(40.0f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    protected final Setting<Boolean> putBack = this.register(new BooleanSetting("Put-Back", false));
    protected final Setting<Boolean> doubleClicks = this.register(new BooleanSetting("Double-Clicks", false));
    protected final Setting<Boolean> wasteLoot = this.register(new BooleanSetting("Waste-Loot", false));
    protected final Setting<Boolean> takeOffLoot = this.register(new BooleanSetting("TakeOff-Loot", false));
    protected final Setting<Boolean> noDuraDesync = this.register(new BooleanSetting("NoDuraDesync", true));
    protected final Setting<Integer> removeTime = this.register(new NumberSetting<Integer>("Remove-Time", 250, 0, 1000));
    protected final Map<Integer, DesyncClick> desyncMap = new ConcurrentHashMap<Integer, DesyncClick>();
    protected final Queue<WindowClick> windowClicks = new LinkedList<WindowClick>();
    protected final StopWatch propertyTimer = new StopWatch();
    protected final StopWatch desyncTimer = new StopWatch();
    protected final DiscreteTimer timer = new GuardTimer();
    protected Set<Integer> queuedSlots = new HashSet<Integer>();
    protected EntityEquipmentSlot lastType;
    protected final Setting<?>[] damages = new Setting[]{this.helmet, this.chest, this.legs, this.boots};
    protected WindowClick putBackClick;
    protected boolean stackSet;

    public AutoArmor() {
        super("AutoArmor", Category.Combat);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerEntityProperties(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerSetSlot(this));
        this.setData(new AutoArmorData(this));
        this.timer.reset(this.delay.getValue().intValue());
    }

    @Override
    protected void onEnable() {
        this.windowClicks.clear();
        this.queuedSlots.clear();
        this.putBackClick = null;
    }

    @Override
    protected void onDisable() {
        this.windowClicks.clear();
        this.queuedSlots.clear();
        this.putBackClick = null;
    }

    @Override
    public String getDisplayInfo() {
        return this.mode.getValue().name();
    }

    public boolean isActive() {
        return this.isEnabled() && !this.windowClicks.isEmpty();
    }

    public void resetTimer() {
        this.timer.reset(this.delay.getValue().intValue());
    }

    public WindowClick queueClick(int slot, ItemStack inSlot, ItemStack inDrag) {
        return this.queueClick(slot, inSlot, inDrag, slot);
    }

    public WindowClick queueClick(int slot, ItemStack inSlot, ItemStack inDrag, int target) {
        WindowClick click = new WindowClick(slot, inSlot, inDrag, target);
        this.queueClick(click);
        click.setFast(this.fast.getValue());
        return click;
    }

    public void queueClick(WindowClick click) {
        this.windowClicks.add(click);
    }

    protected void runClick() {
        if (InventoryUtil.validScreen() && AutoArmor.mc.playerController != null) {
            if (this.timer.passed(this.delay.getValue().intValue())) {
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
                    Managers.NCP.startMultiClick();
                    WindowClick windowClick = this.windowClicks.poll();
                    while (windowClick != null) {
                        if (this.safe.getValue().booleanValue() && !windowClick.isValid()) {
                            this.windowClicks.clear();
                            this.queuedSlots.clear();
                            Managers.NCP.releaseMultiClick();
                            return;
                        }
                        windowClick.runClick(AutoArmor.mc.playerController);
                        this.desyncMap.put(windowClick.getSlot(), new DesyncClick(windowClick));
                        this.timer.reset(this.delay.getValue().intValue());
                        if (!windowClick.isDoubleClick()) {
                            Managers.NCP.releaseMultiClick();
                            return;
                        }
                        windowClick = this.windowClicks.poll();
                    }
                });
            }
        } else {
            this.windowClicks.clear();
            this.queuedSlots.clear();
        }
    }

    protected ItemStack setStack() {
        if (!this.stackSet) {
            ItemStack drag = AutoArmor.mc.player.inventory.getItemStack();
            if (!drag.isEmpty()) {
                int slot = AutoArmor.findItem(Items.AIR, XCARRY.isEnabled(), this.queuedSlots);
                if (slot != -1) {
                    ItemStack inSlot = InventoryUtil.get(slot);
                    this.queueClick(slot, drag, inSlot);
                    this.queuedSlots.add(slot);
                    this.stackSet = true;
                    return inSlot;
                }
                return null;
            }
            this.stackSet = true;
            return drag;
        }
        return null;
    }

    public static boolean curseCheck(ItemStack stack, boolean check) {
        return !check || !EnchantmentHelper.hasBindingCurse((ItemStack)stack);
    }

    boolean canAutoMend() {
        if (SUICIDE.returnIfPresent(Suicide::shouldTakeOffArmor, false).booleanValue()) {
            return this.takeOffLoot.getValue() != false || AutoArmor.mc.world.getEntitiesWithinAABB(EntityItem.class, RotationUtil.getRotationPlayer().getEntityBoundingBox()).isEmpty();
        }
        if (!this.autoMend.getValue().booleanValue() || this.screenCheck.getValue().booleanValue() && AutoArmor.mc.currentScreen != null || (!Mouse.isButtonDown(1) || !InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE)) && (!EXP_TWEAKS.isEnabled() || !EXP_TWEAKS.returnIfPresent(ExpTweaks::isMiddleClick, false).booleanValue()) || this.wasteLoot.getValue().booleanValue() && EXP_TWEAKS.returnIfPresent(e -> e.isWastingLoot(AutoArmor.mc.world.loadedEntityList), false).booleanValue() || !this.takeOffLoot.getValue().booleanValue() && !AutoArmor.mc.world.getEntitiesWithinAABB(EntityItem.class, RotationUtil.getRotationPlayer().getEntityBoundingBox()).isEmpty()) {
            return false;
        }
        EntityPlayer closestPlayer = EntityUtil.getClosestEnemy();
        if (closestPlayer != null && closestPlayer.getDistanceSq(AutoArmor.mc.player) < (double)MathUtil.square(this.closest.getValue().floatValue() * 2.0f)) {
            for (Entity entity : AutoArmor.mc.world.loadedEntityList) {
                double damage;
                if (!(entity instanceof EntityEnderCrystal) || entity.isDead || !(AutoArmor.mc.player.getDistanceSq(entity) <= 144.0) || !((damage = this.getDamageNoArmor(entity.posX, entity.posY, entity.posZ)) > (double)EntityUtil.getHealth(AutoArmor.mc.player) + 1.0) && !(damage > (double)this.maxDmg.getValue().floatValue())) continue;
                return false;
            }
            BlockPos middle = PositionUtil.getPosition();
            int maxRadius = Sphere.getRadius(10.0);
            for (int i = 1; i < maxRadius; ++i) {
                double damage;
                BlockPos pos = middle.add(Sphere.get(i));
                if (!BlockUtil.canPlaceCrystal(pos, false, this.newVer.getValue(), AutoArmor.mc.world.loadedEntityList, this.newVerEntities.getValue(), 0L) && (!this.bedCheck.getValue().booleanValue() || !BlockUtil.canPlaceBed(pos, this.newVer.getValue())) || !((damage = this.getDamageNoArmor((double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5)) > (double)EntityUtil.getHealth(AutoArmor.mc.player) + 1.0) && !(damage > (double)this.maxDmg.getValue().floatValue())) continue;
                return false;
            }
        }
        return true;
    }

    private double getDamageNoArmor(double x, double y, double z) {
        double distance = AutoArmor.mc.player.getDistance(x, y, z) / 12.0;
        if (distance > 1.0) {
            return 0.0;
        }
        double density = DamageUtil.getBlockDensity(new Vec3d(x, y, z), AutoArmor.mc.player.getEntityBoundingBox(), AutoArmor.mc.world, true, true, false, false);
        double densityDistance = distance = (1.0 - distance) * density;
        float damage = DamageUtil.getDifficultyMultiplier((float)((densityDistance * densityDistance + distance) / 2.0 * 7.0 * 12.0 + 1.0));
        damage = CombatRules.getDamageAfterAbsorb((float)damage, (float)3.0f, (float)2.0f);
        PotionEffect resistance = AutoArmor.mc.player.getActivePotionEffect(MobEffects.RESISTANCE);
        if (resistance != null) {
            damage = damage * (float)(25 - (resistance.getAmplifier() + 1) * 5) / 25.0f;
        }
        return Math.max(damage, 0.0f);
    }

    public static EntityEquipmentSlot fromSlot(int slot) {
        switch (slot) {
            case 5: {
                return EntityEquipmentSlot.HEAD;
            }
            case 6: {
                return EntityEquipmentSlot.CHEST;
            }
            case 7: {
                return EntityEquipmentSlot.LEGS;
            }
            case 8: {
                return EntityEquipmentSlot.FEET;
            }
        }
        ItemStack stack = InventoryUtil.get(slot);
        return AutoArmor.getSlot(stack);
    }

    public static int fromEquipment(EntityEquipmentSlot equipmentSlot) {
        switch (equipmentSlot) {
            case OFFHAND: {
                return 45;
            }
            case FEET: {
                return 8;
            }
            case LEGS: {
                return 7;
            }
            case CHEST: {
                return 6;
            }
            case HEAD: {
                return 5;
            }
        }
        return -1;
    }

    public static EntityEquipmentSlot getSlot(ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor)((Object)stack.getItem());
                return armor.getEquipmentSlot();
            }
            if (stack.getItem() instanceof ItemElytra) {
                return EntityEquipmentSlot.CHEST;
            }
        }
        return null;
    }

    public static int findItem(Item item, boolean xCarry, Set<Integer> blackList) {
        ItemStack stack;
        int i;
        ItemStack drag = AutoArmor.mc.player.inventory.getItemStack();
        if (!drag.isEmpty() && drag.getItem() == item && !blackList.contains(-2)) {
            return -2;
        }
        for (i = 9; i < 45; ++i) {
            stack = InventoryUtil.get(i);
            if (stack.getItem() != item || blackList.contains(i)) continue;
            return i;
        }
        if (xCarry) {
            for (i = 1; i < 5; ++i) {
                stack = InventoryUtil.get(i);
                if (stack.getItem() != item || blackList.contains(i)) continue;
                return i;
            }
        }
        return -1;
    }
}
