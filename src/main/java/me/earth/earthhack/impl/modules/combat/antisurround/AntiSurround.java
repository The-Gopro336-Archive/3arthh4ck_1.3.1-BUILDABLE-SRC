package me.earth.earthhack.impl.modules.combat.antisurround;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.ListenerBlockBreak;
import me.earth.earthhack.impl.modules.combat.antisurround.ListenerBlockChange;
import me.earth.earthhack.impl.modules.combat.antisurround.ListenerBlockMulti;
import me.earth.earthhack.impl.modules.combat.antisurround.ListenerDigging;
import me.earth.earthhack.impl.modules.combat.antisurround.ListenerDiggingNoEvent;
import me.earth.earthhack.impl.modules.combat.antisurround.ListenerObby;
import me.earth.earthhack.impl.modules.combat.antisurround.ListenerRender;
import me.earth.earthhack.impl.modules.combat.antisurround.util.AntiSurroundFunction;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperLiquids;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.modules.player.noglitchblocks.NoGlitchBlocks;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;
import me.earth.earthhack.impl.util.helpers.render.BlockESPBuilder;
import me.earth.earthhack.impl.util.helpers.render.IAxisESP;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class AntiSurround
extends ObbyListenerModule<ListenerObby> {
    protected static final ModuleCache<LegSwitch> LEG_SWITCH = Caches.getModule(LegSwitch.class);
    private static final ModuleCache<NoGlitchBlocks> NOGLITCHBLOCKS = Caches.getModule(NoGlitchBlocks.class);
    protected final Setting<Double> range = this.register(new NumberSetting<Double>("Range", 5.25, 0.1, 6.0));
    protected final Setting<Boolean> async = this.register(new BooleanSetting("Asnyc", false));
    protected final Setting<Boolean> instant = this.register(new BooleanSetting("Instant", false));
    protected final Setting<Boolean> persistent = this.register(new BooleanSetting("Persistent", true));
    protected final Setting<Boolean> obby = this.register(new BooleanSetting("Obby", false));
    protected final Setting<Boolean> digSwing = this.register(new BooleanSetting("DigSwing", false));
    protected final Setting<Boolean> newVer = this.register(new BooleanSetting("1.13+", false));
    protected final Setting<Boolean> newVerEntities = this.register(new BooleanSetting("1.13-Entities", false));
    protected final Setting<Boolean> onGround = this.register(new BooleanSetting("OnGround", true));
    protected final Setting<Float> minDmg = this.register(new NumberSetting<Float>("MinDamage", Float.valueOf(5.0f), Float.valueOf(0.0f), Float.valueOf(36.0f)));
    protected final Setting<Integer> itemDeathTime = this.register(new NumberSetting<Integer>("ItemDeathTime", 100, 0, 1000));
    protected final Setting<Boolean> pickaxeOnly = this.register(new BooleanSetting("HoldingPickaxe", false));
    protected final Setting<Boolean> anvil = this.register(new BooleanSetting("Anvil", false));
    protected final Setting<Boolean> drawEsp = this.register(new BooleanSetting("ESP", true));
    protected final Setting<Boolean> preCrystal = this.register(new BooleanSetting("PreCrystal", false));
    protected final ColorSetting color = this.register(new ColorSetting("Color", new Color(255, 255, 255, 75)));
    protected final ColorSetting outline = this.register(new ColorSetting("Outline", new Color(255, 255, 255, 240)));
    protected final Setting<Float> lineWidth = this.register(new NumberSetting<Float>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.0f), Float.valueOf(10.0f)));
    protected final Setting<Float> height = this.register(new NumberSetting<Float>("ESP-Height", Float.valueOf(1.0f), Float.valueOf(-1.0f), Float.valueOf(1.0f)));
    protected final Setting<Boolean> normal = this.register(new BooleanSetting("Normal", true));
    protected final Setting<Float> minMine = new NumberSetting<Float>("MinMine", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(10.0f));
    protected final AtomicBoolean semiActive = new AtomicBoolean();
    protected final AtomicBoolean active = new AtomicBoolean();
    protected volatile long semiActiveTime;
    protected final IAxisESP esp;
    protected int crystalSlot = -1;
    protected int toolSlot = -1;
    protected int obbySlot = -1;
    protected EntityPlayer target;
    protected volatile BlockPos semiPos;
    protected BlockPos crystalPos;
    protected BlockPos playerPos;
    protected BlockPos pos;
    protected boolean hasMined;
    protected boolean isAnvil;
    protected boolean mine;
    protected int ticks;

    public AntiSurround() {
        super("AntiSurround", Category.Combat);
        this.listeners.clear();
        this.listeners.add(this.listener);
        this.listeners.add(new ListenerBlockBreak(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerBlockMulti(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerDigging(this));
        this.listeners.add(new ListenerDiggingNoEvent(this));
        this.attack.setValue(true);
        this.unregister(this.attack);
        this.attack.addObserver(e -> e.setCancelled(true));
        this.attackAny.setValue(false);
        this.unregister(this.attackAny);
        this.unregister(this.attackRange);
        this.unregister(this.attackTrace);
        this.breakDelay.setValue(50);
        this.attackAny.addObserver(e -> e.setCancelled(true));
        this.pop.setValue(Pop.Time);
        this.cooldown.setValue(0);
        this.esp = new BlockESPBuilder().withColor(this.color).withOutlineColor(this.outline).withLineWidth(this.lineWidth).build();
    }

    @Override
    public String getDisplayInfo() {
        EntityPlayer target = this.target;
        if (target != null) {
            return target.getName();
        }
        return null;
    }

    @Override
    protected void onEnable() {
        if (NOGLITCHBLOCKS.returnIfPresent(NoGlitchBlocks::noBreak, false).booleanValue()) {
            ModuleUtil.sendMessage(this, "\u00a7cNoGlitchBlocks - Break is active. This can cause issues with AntiSurround!");
        }
        super.onEnable();
        this.reset();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        this.reset();
    }

    @Override
    protected boolean checkNull() {
        this.packets.clear();
        this.blocksPlaced = 0;
        return AntiSurround.mc.player != null && AntiSurround.mc.world != null;
    }

    @Override
    public boolean execute() {
        if (!this.packets.isEmpty() && this.mine) {
            EnumFacing facing;
            BlockPos pos = this.pos;
            EnumFacing finalFacing = facing = RayTraceUtil.getFacing(RotationUtil.getRotationPlayer(), pos, true);
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                int lastSlot = AntiSurround.mc.player.inventory.currentItem;
                InventoryUtil.switchTo(this.toolSlot);
                if (!this.isAnvil) {
                    PacketUtil.startDigging(pos, finalFacing);
                }
                PacketUtil.stopDigging(pos, finalFacing);
                this.hasMined = false;
                if (this.digSwing.getValue().booleanValue()) {
                    Swing.Packet.swing(EnumHand.MAIN_HAND);
                }
                InventoryUtil.switchTo(lastSlot);
            });
        }
        this.lastSlot = -1;
        boolean execute = false;
        if (!this.packets.isEmpty()) {
            execute = super.execute();
        } else if (!this.post.isEmpty()) {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                int lastSlot = AntiSurround.mc.player.inventory.currentItem;
                this.post.forEach(Runnable::run);
                InventoryUtil.switchTo(lastSlot);
            });
            this.post.clear();
        }
        this.mine = false;
        return execute;
    }

    @Override
    protected ListenerObby createListener() {
        return new ListenerObby(this);
    }

    public boolean holdingCheck() {
        return this.pickaxeOnly.getValue() != false && !(AntiSurround.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe);
    }

    public boolean isActive() {
        return this.isEnabled() && (this.active.get() || this.semiActive.get());
    }

    public void reset() {
        this.semiActive.set(false);
        this.active.set(false);
        this.slot = -1;
        this.crystalSlot = -1;
        this.toolSlot = -1;
        this.obbySlot = -1;
        this.semiPos = null;
        this.target = null;
        this.pos = null;
        this.crystalPos = null;
        this.mine = false;
        this.hasMined = false;
    }

    public boolean onBlockBreak(BlockPos pos, List<EntityPlayer> players, List<Entity> entities) {
        return this.onBlockBreak(pos, players, entities, this::placeSync);
    }

    public boolean onBlockBreak(BlockPos pos, List<EntityPlayer> players, List<Entity> entities, AntiSurroundFunction function) {
        if (LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false).booleanValue()) {
            return false;
        }
        MineSlots slots = HelperLiquids.getSlots(this.onGround.getValue());
        if (slots.getDamage() < this.minMine.getValue().floatValue() && !(this.isAnvil = this.anvilCheck(slots)) || slots.getToolSlot() == -1 || slots.getBlockSlot() == -1) {
            return false;
        }
        int crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0]);
        if (crystalSlot == -1) {
            return false;
        }
        int obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, new Block[0]);
        BlockStateHelper helper = new BlockStateHelper();
        helper.addBlockState(pos, Blocks.AIR.getDefaultState());
        Entity blocking = this.getBlockingEntity(pos, entities);
        if (blocking != null && !(blocking instanceof EntityEnderCrystal)) {
            return false;
        }
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos opposite;
            BlockPos down;
            BlockPos offset = pos.offset(facing);
            if (!AntiSurround.mc.world.getBlockState(offset).func_185904_a().isReplaceable()) continue;
            EntityPlayer found = null;
            AxisAlignedBB offsetBB = new AxisAlignedBB(offset);
            for (EntityPlayer player : players) {
                if (player == null || EntityUtil.isDead(player) || player.equals(AntiSurround.mc.player) || player.equals(RotationUtil.getRotationPlayer()) || Managers.FRIENDS.contains(player) || !player.getEntityBoundingBox().intersects(offsetBB)) continue;
                found = player;
                break;
            }
            if (found == null || BlockUtil.getDistanceSq(down = (opposite = pos.offset(facing.getOpposite())).down()) > MathUtil.square(this.range.getValue()) || !BlockUtil.canPlaceCrystalReplaceable(down, true, this.newVer.getValue(), entities, this.newVerEntities.getValue(), 0L)) continue;
            IBlockState state = AntiSurround.mc.world.getBlockState(down);
            if ((!this.obby.getValue().booleanValue() || obbySlot == -1) && state.getBlock() != Blocks.OBSIDIAN && state.getBlock() != Blocks.BEDROCK) continue;
            helper.addBlockState(down, Blocks.OBSIDIAN.getDefaultState());
            float damage = DamageUtil.calculate(down, found, helper);
            helper.delete(down);
            if (damage < this.minDmg.getValue().floatValue()) continue;
            BlockPos on = null;
            EnumFacing onFacing = null;
            for (EnumFacing off : EnumFacing.values()) {
                on = pos.offset(off);
                if (!(BlockUtil.getDistanceSq(on) <= MathUtil.square(this.range.getValue())) || AntiSurround.mc.world.getBlockState(on).func_185904_a().isReplaceable()) continue;
                onFacing = off.getOpposite();
                break;
            }
            if (onFacing == null) continue;
            function.accept(pos, down, on, onFacing, obbySlot, slots, crystalSlot, blocking, found, true);
            return true;
        }
        return false;
    }

    protected Entity getBlockingEntity(BlockPos pos, List<Entity> entities) {
        Entity blocking = null;
        AxisAlignedBB bb = new AxisAlignedBB(pos);
        for (Entity entity : entities) {
            if (entity == null || EntityUtil.isDead(entity) || !entity.preventEntitySpawning || !entity.getEntityBoundingBox().intersects(bb)) continue;
            if (entity instanceof EntityEnderCrystal) {
                blocking = entity;
                continue;
            }
            return entity;
        }
        return blocking;
    }

    public synchronized boolean placeSync(BlockPos pos, BlockPos down, BlockPos on, EnumFacing onFacing, int obbySlot, MineSlots slots, int crystalSlot, Entity blocking, EntityPlayer found, boolean execute) {
        if (this.active.get() || LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false).booleanValue()) {
            return false;
        }
        this.obbySlot = obbySlot;
        this.slot = slots.getBlockSlot();
        this.toolSlot = slots.getToolSlot();
        this.crystalSlot = crystalSlot;
        this.crystalPos = down;
        this.pos = pos;
        this.target = found;
        this.playerPos = PositionUtil.getPosition(found);
        this.active.set(true);
        this.placeBlock(on, onFacing);
        if (blocking != null) {
            this.attacking = new CPacketUseEntity(blocking);
        }
        if (execute && (blocking != null || this.semiPos == null)) {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, this::execute);
        }
        return true;
    }

    public boolean anvilCheck(MineSlots slots) {
        int slot = slots.getBlockSlot();
        if (slot == -1 || !this.anvil.getValue().booleanValue()) {
            return false;
        }
        ItemStack stack = AntiSurround.mc.player.inventory.getStackInSlot(slot);
        return stack.getItem() instanceof ItemAnvilBlock;
    }
}
