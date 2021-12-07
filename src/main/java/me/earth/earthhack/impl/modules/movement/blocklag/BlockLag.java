package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLagData;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLagPages;
import me.earth.earthhack.impl.modules.movement.blocklag.ListenerExplosion;
import me.earth.earthhack.impl.modules.movement.blocklag.ListenerMotion;
import me.earth.earthhack.impl.modules.movement.blocklag.ListenerSpawnObject;
import me.earth.earthhack.impl.modules.movement.blocklag.ListenerVelocity;
import me.earth.earthhack.impl.modules.movement.blocklag.mode.BlockLagStage;
import me.earth.earthhack.impl.modules.movement.blocklag.mode.OffsetMode;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class BlockLag
extends DisablingModule {
    protected static final ModuleCache<Freecam> FREECAM = Caches.getModule(Freecam.class);
    protected final Setting<BlockLagPages> pages = this.register(new EnumSetting<BlockLagPages>("Page", BlockLagPages.Offsets));
    protected final Setting<OffsetMode> offsetMode = this.register(new EnumSetting<OffsetMode>("Mode", OffsetMode.Smart));
    protected final Setting<Double> vClip = this.register(new NumberSetting<Double>("V-Clip", -9.0, -256.0, 256.0));
    protected final Setting<Double> minDown = this.register(new NumberSetting<Double>("Min-Down", 3.0, 0.0, 1337.0));
    protected final Setting<Double> maxDown = this.register(new NumberSetting<Double>("Max-Down", 10.0, 0.0, 1337.0));
    protected final Setting<Double> minUp = this.register(new NumberSetting<Double>("Min-Up", 3.0, 0.0, 1337.0));
    protected final Setting<Double> maxUp = this.register(new NumberSetting<Double>("Max-Up", 10.0, 0.0, 1337.0));
    protected final Setting<Integer> delay = this.register(new NumberSetting<Integer>("Delay", 100, 0, 1000));
    protected final Setting<Boolean> skipZero = this.register(new BooleanSetting("SkipZero", true));
    protected final Setting<Boolean> fallback = this.register(new BooleanSetting("Fallback", true));
    protected final Setting<Boolean> air = this.register(new BooleanSetting("Air", false));
    protected final Setting<Boolean> discrete = this.register(new BooleanSetting("Discrete", true));
    protected final Setting<Boolean> rotate = this.register(new BooleanSetting("Rotate", false));
    protected final Setting<Boolean> anvil = this.register(new BooleanSetting("Anvil", false));
    protected final Setting<Boolean> echest = this.register(new BooleanSetting("E-Chest", false));
    protected final Setting<Boolean> beacon = this.register(new BooleanSetting("Beacon", false));
    protected final Setting<Boolean> allowUp = this.register(new BooleanSetting("Allow-Up", false));
    protected final Setting<Boolean> onGround = this.register(new BooleanSetting("OnGround", true));
    protected final Setting<Boolean> conflict = this.register(new BooleanSetting("Conflict", true));
    protected final Setting<Boolean> noVoid = this.register(new BooleanSetting("NoVoid", false));
    protected final Setting<Boolean> evade = this.register(new BooleanSetting("Evade", false));
    protected final Setting<Boolean> freecam = this.register(new BooleanSetting("Freecam", false));
    protected final Setting<Boolean> highBlock = this.register(new BooleanSetting("HighBlock", false));
    protected final Setting<Boolean> bypass = this.register(new BooleanSetting("Bypass", false));
    protected final Setting<Double> bypassOffset = this.register(new NumberSetting<Double>("BypassOffset", 0.032, 0.001, 0.1));
    protected final Setting<Boolean> wait = this.register(new BooleanSetting("Wait", true));
    protected final Setting<Boolean> placeDisable = this.register(new BooleanSetting("PlaceDisable", false));
    protected final Setting<BlockLagStage> stage = this.register(new EnumSetting<BlockLagStage>("Stage", BlockLagStage.All));
    protected final Setting<Boolean> deltaY = this.register(new BooleanSetting("Delta-Y", true));
    protected final Setting<Boolean> attack = this.register(new BooleanSetting("Attack", false));
    protected final Setting<Boolean> instantAttack = this.register(new BooleanSetting("Instant-Attack", false));
    protected final Setting<Boolean> antiWeakness = this.register(new BooleanSetting("AntiWeakness", false));
    protected final Setting<Boolean> attackBefore = this.register(new BooleanSetting("Attack-Before", false));
    protected final Setting<Pop> pop = this.register(new EnumSetting<Pop>("Pop", Pop.None));
    protected final Setting<Integer> popTime = this.register(new NumberSetting<Integer>("Pop-Time", 500, 0, 500));
    protected final Setting<Integer> cooldown = this.register(new NumberSetting<Integer>("Cooldown", 500, 0, 500));
    protected final Setting<Boolean> scaleExplosion = this.register(new BooleanSetting("Scale-Explosion", false));
    protected final Setting<Boolean> scaleVelocity = this.register(new BooleanSetting("Scale-Velocity", false));
    protected final Setting<Boolean> scaleDown = this.register(new BooleanSetting("Scale-Down", false));
    protected final Setting<Integer> scaleDelay = this.register(new NumberSetting<Integer>("Scale-Delay", 250, 0, 1000));
    protected final Setting<Double> scaleFactor = this.register(new NumberSetting<Double>("Scale-Factor", 1.0, 0.1, 10.0));
    protected final StopWatch scaleTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected double motionY;
    protected BlockPos startPos;

    public BlockLag() {
        super("BlockLag", Category.Movement);
        this.setData(new BlockLagData(this));
        this.listeners.add(new ListenerMotion(this));
        Bus.EVENT_BUS.register(new ListenerVelocity(this));
        Bus.EVENT_BUS.register(new ListenerExplosion(this));
        Bus.EVENT_BUS.register(new ListenerSpawnObject(this));
        new PageBuilder<BlockLagPages>(this, this.pages).addPage(v -> v == BlockLagPages.Offsets, (Setting<?>)this.offsetMode, (Setting<?>)this.discrete).addPage(v -> v == BlockLagPages.Misc, (Setting<?>)this.rotate, (Setting<?>)this.deltaY).addPage(v -> v == BlockLagPages.Attack, (Setting<?>)this.attack, (Setting<?>)this.cooldown).addPage(v -> v == BlockLagPages.Scale, (Setting<?>)this.scaleExplosion, (Setting<?>)this.scaleFactor).register(Visibilities.VISIBILITY_MANAGER);
    }

    @Override
    protected void onEnable() {
        this.timer.setTime(0L);
        super.onEnable();
        if (BlockLag.mc.world == null || BlockLag.mc.player == null) {
            return;
        }
        this.startPos = this.getPlayerPos();
        if (this.singlePlayerCheck(this.startPos)) {
            this.disable();
        }
    }

    protected void attack(Packet<?> attacking, int slot) {
        if (slot != -1) {
            InventoryUtil.switchTo(slot);
        }
        NetworkUtil.send(attacking);
        Swing.Packet.swing(EnumHand.MAIN_HAND);
    }

    protected double getY(Entity entity, OffsetMode mode) {
        if (mode == OffsetMode.Constant) {
            double y = entity.posY + this.vClip.getValue();
            if (this.evade.getValue().booleanValue() && Math.abs(y) < 1.0) {
                y = -1.0;
            }
            return y;
        }
        double d = this.getY(entity, this.minDown.getValue(), this.maxDown.getValue(), true);
        if (Double.isNaN(d) && Double.isNaN(d = this.getY(entity, -this.minUp.getValue().doubleValue(), -this.maxUp.getValue().doubleValue(), false)) && this.fallback.getValue().booleanValue()) {
            return this.getY(entity, OffsetMode.Constant);
        }
        return d;
    }

    protected double getY(Entity entity, double min, double max, boolean add) {
        if (min > max && add || max > min && !add) {
            return Double.NaN;
        }
        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;
        boolean air = false;
        double lastOff = 0.0;
        BlockPos last = null;
        double off = min;
        while (add ? off < max : off > max) {
            BlockPos pos = new BlockPos(x, y - off, z);
            if (!this.noVoid.getValue().booleanValue() || pos.getY() >= 0) {
                if (this.skipZero.getValue().booleanValue() && Math.abs(y) < 1.0) {
                    air = false;
                    last = pos;
                    lastOff = y - off;
                } else {
                    IBlockState state = BlockLag.mc.world.getBlockState(pos);
                    if (!this.air.getValue().booleanValue() && !state.func_185904_a().blocksMovement() || state.getBlock() == Blocks.AIR) {
                        if (air) {
                            if (add) {
                                return this.discrete.getValue() != false ? (double)pos.getY() : y - off;
                            }
                            return this.discrete.getValue() != false ? (double)last.getY() : lastOff;
                        }
                        air = true;
                    } else {
                        air = false;
                    }
                    last = pos;
                    lastOff = y - off;
                }
            }
            off = add ? (off = off + 1.0) : (off = off - 1.0);
        }
        return Double.NaN;
    }

    protected double applyScale(double value) {
        if (value < BlockLag.mc.player.posY && this.scaleDown.getValue() == false || this.scaleExplosion.getValue() == false && this.scaleVelocity.getValue() == false || this.scaleTimer.passed(this.scaleDelay.getValue().intValue()) || this.motionY == 0.0) {
            return value;
        }
        value = value < BlockLag.mc.player.posY ? (value -= this.motionY * this.scaleFactor.getValue()) : (value += this.motionY * this.scaleFactor.getValue());
        return this.discrete.getValue() != false ? Math.floor(value) : value;
    }

    protected BlockPos getPlayerPos() {
        return this.deltaY.getValue() != false && Math.abs(BlockLag.mc.player.motionY) > 0.1 ? new BlockPos(BlockLag.mc.player) : PositionUtil.getPosition(BlockLag.mc.player);
    }

    protected boolean isInsideBlock() {
        double x = BlockLag.mc.player.posX;
        double y = BlockLag.mc.player.posY + 0.2;
        double z = BlockLag.mc.player.posZ;
        return BlockLag.mc.world.getBlockState(new BlockPos(x, y, z)).func_185904_a().blocksMovement() || !BlockLag.mc.player.collidedVertically;
    }

    protected boolean singlePlayerCheck(BlockPos pos) {
        if (mc.isSingleplayer()) {
            EntityPlayerMP player = mc.getIntegratedServer().func_184103_al().getPlayerByUUID(BlockLag.mc.player.getUniqueID());
            if (player == null) {
                this.disable();
                return true;
            }
            player.getEntityWorld().setBlockState(pos, this.echest.getValue() != false ? Blocks.ENDER_CHEST.getDefaultState() : Blocks.OBSIDIAN.getDefaultState());
            BlockLag.mc.world.setBlockState(pos, this.echest.getValue() != false ? Blocks.ENDER_CHEST.getDefaultState() : Blocks.OBSIDIAN.getDefaultState());
            return true;
        }
        return false;
    }
}
