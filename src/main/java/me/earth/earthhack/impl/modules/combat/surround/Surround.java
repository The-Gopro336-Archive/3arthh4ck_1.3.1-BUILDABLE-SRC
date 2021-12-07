package me.earth.earthhack.impl.modules.combat.surround;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.surround.ListenerBlockChange;
import me.earth.earthhack.impl.modules.combat.surround.ListenerExplosion;
import me.earth.earthhack.impl.modules.combat.surround.ListenerMotion;
import me.earth.earthhack.impl.modules.combat.surround.ListenerMultiBlockChange;
import me.earth.earthhack.impl.modules.combat.surround.ListenerSound;
import me.earth.earthhack.impl.modules.combat.surround.ListenerSpawnObject;
import me.earth.earthhack.impl.modules.combat.surround.SurroundData;
import me.earth.earthhack.impl.modules.combat.surround.modes.Movement;
import me.earth.earthhack.impl.modules.combat.surround.modes.SurroundFreecamMode;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockingType;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class Surround
extends ObbyModule {
    protected static final ModuleCache<Freecam> FREECAM = Caches.getModule(Freecam.class);
    protected static final ModuleCache<BlockLag> BLOCKLAG = Caches.getModule(BlockLag.class);
    protected final Setting<Boolean> center = this.register(new BooleanSetting("Center", true));
    protected final Setting<Movement> movement = this.register(new EnumSetting<Movement>("Movement", Movement.Static));
    protected final Setting<Float> speed = this.register(new NumberSetting<Float>("Speed", Float.valueOf(19.5f), Float.valueOf(0.0f), Float.valueOf(35.0f)));
    protected final Setting<Boolean> noTrap = this.register(new BooleanSetting("NoTrap", false));
    protected final Setting<Boolean> floor = this.register(new BooleanSetting("Floor", false));
    protected final Setting<Integer> extend = this.register(new NumberSetting<Integer>("Extend", 1, 1, 3));
    protected final Setting<Integer> eDelay = this.register(new NumberSetting<Integer>("E-Delay", 100, 0, 1000));
    protected final Setting<Boolean> holeC = this.register(new BooleanSetting("Hole-C", false));
    protected final Setting<Boolean> instant = this.register(new BooleanSetting("Instant", false));
    protected final Setting<Boolean> sound = this.register(new BooleanSetting("Sound", false));
    protected final Setting<Integer> playerExtend = this.register(new NumberSetting<Integer>("PlayerExtend", 0, 0, 4));
    protected final Setting<Boolean> peNoTrap = this.register(new BooleanSetting("PE-NoTrap", false));
    protected final Setting<Boolean> noTrapBlock = this.register(new BooleanSetting("NoTrapBlock", false));
    protected final Setting<Boolean> multiTrap = this.register(new BooleanSetting("MultiTrap", false));
    protected final Setting<Boolean> trapExtend = this.register(new BooleanSetting("TrapExtend", false));
    protected final Setting<Boolean> newVer = this.register(new BooleanSetting("1.13+", false));
    protected final Setting<Boolean> deltaY = this.register(new BooleanSetting("Delta-Y", true));
    protected final Setting<Boolean> centerY = this.register(new BooleanSetting("Center-Y", false));
    protected final Setting<Boolean> predict = this.register(new BooleanSetting("Predict", false));
    protected final Setting<Boolean> async = this.register(new BooleanSetting("Async", false));
    protected final Setting<Boolean> resync = this.register(new BooleanSetting("Resync", false));
    protected final Setting<Boolean> crystalCheck = this.register(new BooleanSetting("Crystal-Check", true));
    protected final Setting<Boolean> burrow = this.register(new BooleanSetting("Burrow", false));
    protected final Setting<Boolean> noSelfExtend = this.register(new BooleanSetting("NoSelfExtend", false));
    protected final Setting<SurroundFreecamMode> freecam = this.register(new EnumSetting<SurroundFreecamMode>("Freecam", SurroundFreecamMode.Off));
    protected final ListenerSound soundObserver = new ListenerSound(this);
    protected final StopWatch extendingWatch = new StopWatch();
    protected Set<BlockPos> targets = new HashSet<BlockPos>();
    protected Set<BlockPos> placed = new HashSet<BlockPos>();
    protected Set<BlockPos> confirmed = new HashSet<BlockPos>();
    protected BlockPos startPos;
    protected boolean setPosition;

    public Surround() {
        super("Surround", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerMultiBlockChange(this));
        this.listeners.add(new ListenerExplosion(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.setData(new SurroundData(this));
    }

    @Override
    protected void onEnable() {
        Managers.SET_DEAD.addObserver(this.soundObserver);
        super.onEnable();
        if (super.checkNull()) {
            this.confirmed.clear();
            this.targets.clear();
            this.placed.clear();
            this.attacking = null;
            this.setPosition = false;
            this.startPos = this.getPlayerPos();
            this.extendingWatch.reset();
            if (this.burrow.getValue().booleanValue() && !BLOCKLAG.isEnabled()) {
                BLOCKLAG.toggle();
            }
        }
    }

    @Override
    protected void onDisable() {
        Managers.SET_DEAD.removeObserver(this.soundObserver);
    }

    protected void center() {
        if (this.center.getValue().booleanValue() && !this.setPosition && this.startPos != null && Surround.mc.world.getBlockState(this.startPos).getBlock() != Blocks.WEB && (this.holeC.getValue().booleanValue() || !HoleUtil.isHole(this.startPos, false)[0])) {
            double x = (double)this.startPos.getX() + 0.5;
            double y = this.centerY.getValue() != false ? (double)this.startPos.getY() : this.getPlayer().posY;
            double z = (double)this.startPos.getZ() + 0.5;
            this.getPlayer().setPosition(x, y, z);
            this.getPlayer().setVelocity(0.0, this.getPlayer().motionY, 0.0);
        } else {
            this.setPosition = true;
        }
    }

    protected boolean updatePosAndBlocks() {
        if (this.check()) {
            Set<BlockPos> blocked = this.createBlocked();
            Set<BlockPos> surrounding = this.createSurrounding(blocked, Surround.mc.world.playerEntities);
            this.placed.retainAll(surrounding);
            this.targets = surrounding;
            return true;
        }
        return false;
    }

    private boolean check() {
        if (FREECAM.isEnabled() && this.freecam.getValue() == SurroundFreecamMode.Off) {
            return false;
        }
        this.slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN, Blocks.ENDER_CHEST);
        if (this.slot == -1) {
            ModuleUtil.disable(this, "\u00a7cDisabled, no Obsidian.");
            return false;
        }
        if (FREECAM.isEnabled() && this.movement.getValue() != Movement.Static) {
            return this.timer.passed(((Integer)this.delay.getValue()).intValue());
        }
        switch (this.movement.getValue()) {
            case None: {
                break;
            }
            case Static: {
                BlockPos currentPos = this.getPlayerPos();
                if (currentPos.equals(this.startPos)) break;
                this.disable();
                return false;
            }
            case Limit: {
                if (!(Managers.SPEED.getSpeed() > (double)this.speed.getValue().floatValue())) break;
                return false;
            }
            case Disable: {
                if (!(Managers.SPEED.getSpeed() > (double)this.speed.getValue().floatValue())) break;
                this.disable();
                return false;
            }
        }
        return this.timer.passed(((Integer)this.delay.getValue()).intValue());
    }

    @Override
    public boolean placeBlock(BlockPos pos) {
        boolean hasPlaced = super.placeBlock(pos);
        if (hasPlaced) {
            this.placed.add(pos);
        }
        return hasPlaced;
    }

    protected Set<BlockPos> createBlocked() {
        HashSet<BlockPos> blocked = new HashSet<BlockPos>();
        BlockPos playerPos = this.getPlayerPos();
        if (HoleUtil.isHole(playerPos, false)[0] || this.center.getValue().booleanValue() || this.extend.getValue() == 1 || !this.extendingWatch.passed(this.eDelay.getValue().intValue())) {
            blocked.add(playerPos);
        } else {
            List unfiltered = new ArrayList<BlockPos>(PositionUtil.getBlockedPositions(this.getPlayer())).stream().sorted(Comparator.comparingDouble(pos -> BlockUtil.getDistanceSq(this.getPlayer(), pos))).collect(Collectors.toList());
            List filtered = new ArrayList(unfiltered).stream().filter(pos -> Surround.mc.world.getBlockState((BlockPos)pos).func_185904_a().isReplaceable() && Surround.mc.world.getBlockState(pos.up()).func_185904_a().isReplaceable()).collect(Collectors.toList());
            if (this.extend.getValue() == 3 && filtered.size() == 2 && unfiltered.size() == 4 && ((BlockPos)unfiltered.get(0)).equals(filtered.get(0)) && ((BlockPos)unfiltered.get(3)).equals(filtered.get(1))) {
                filtered.clear();
                filtered.add(playerPos);
            }
            if (this.extend.getValue() == 2 && filtered.size() > 2 || this.extend.getValue() == 3 && filtered.size() == 3) {
                while (filtered.size() > 2) {
                    filtered.remove(filtered.size() - 1);
                }
            }
            blocked.addAll(filtered);
        }
        if (blocked.isEmpty()) {
            blocked.add(playerPos);
        }
        return blocked;
    }

    protected boolean shouldInstant(boolean sound) {
        return this.instant.getValue() != false && this.rotate.getValue() != Rotate.Normal && (!sound || this.sound.getValue() != false);
    }

    protected boolean isBlockingTrap(BlockPos pos, List<EntityPlayer> players) {
        if (Surround.mc.world.getBlockState(pos.up()).func_185904_a().isReplaceable()) {
            return false;
        }
        EnumFacing relative = this.getFacingRelativeToPlayer(pos, this.getPlayer());
        if (relative != null && !this.trapExtend.getValue().booleanValue() && BlockUtil.canPlaceCrystal(this.getPlayerPos().down().offset(relative, 2), true, this.newVer.getValue())) {
            return false;
        }
        for (EntityPlayer player : players) {
            if (player == null || this.getPlayer().equals(player) || EntityUtil.isDead(player) || Managers.FRIENDS.contains(player) || player.getDistanceSq(pos) > 9.0) continue;
            BlockPos playerPos = PositionUtil.getPosition(player);
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                if (facing == relative || facing.getOpposite() == relative || !pos.offset(facing).equals(playerPos) || !BlockUtil.canPlaceCrystal(pos.offset(facing.getOpposite()).down(), true, this.newVer.getValue())) continue;
                return true;
            }
        }
        return false;
    }

    protected EnumFacing getFacingRelativeToPlayer(BlockPos pos, EntityPlayer player) {
        double x = (double)pos.getX() + 0.5 - player.posX;
        double z = (double)pos.getZ() + 0.5 - player.posZ;
        int compare = Double.compare(Math.abs(x), Math.abs(z));
        if (compare == 0) {
            return null;
        }
        return compare < 0 ? (z < 0.0 ? EnumFacing.NORTH : EnumFacing.SOUTH) : (x < 0.0 ? EnumFacing.WEST : EnumFacing.EAST);
    }

    protected Set<BlockPos> createSurrounding(Set<BlockPos> blocked, List<EntityPlayer> players) {
        HashSet<BlockPos> surrounding = new HashSet<BlockPos>();
        for (BlockPos pos2 : blocked) {
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                BlockPos offset = pos2.offset(facing);
                if (blocked.contains(offset)) continue;
                surrounding.add(offset);
                if (!this.noTrap.getValue().booleanValue()) continue;
                surrounding.add(offset.down());
            }
            if (!this.floor.getValue().booleanValue()) continue;
            surrounding.add(pos2.down());
        }
        for (int i = 0; i < this.playerExtend.getValue(); ++i) {
            HashSet<BlockPos> extendedPositions = new HashSet<BlockPos>();
            Iterator itr = surrounding.iterator();
            while (itr.hasNext()) {
                BlockPos pos3 = (BlockPos)itr.next();
                boolean remove = false;
                for (EntityPlayer player : players) {
                    if (player == null || this.noSelfExtend.getValue().booleanValue() && player == Surround.mc.player || PlayerUtil.isFakePlayer(player) || EntityUtil.isDead(player) || !BlockUtil.isBlocking(pos3, player, (BlockingType)((Object)this.blockingType.getValue()))) continue;
                    for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                        BlockPos offset = pos3.offset(facing);
                        if (blocked.contains(offset)) continue;
                        remove = true;
                        extendedPositions.add(offset);
                        if (!this.peNoTrap.getValue().booleanValue()) continue;
                        extendedPositions.add(offset.down());
                    }
                }
                if (!remove) continue;
                itr.remove();
            }
            surrounding.addAll(extendedPositions);
        }
        if (this.noTrapBlock.getValue().booleanValue()) {
            Set trapBlocks = surrounding.stream().filter(pos -> this.isBlockingTrap((BlockPos)pos, players)).collect(Collectors.toSet());
            if (!this.multiTrap.getValue().booleanValue() && trapBlocks.size() > 1) {
                return surrounding;
            }
            for (BlockPos trap : trapBlocks) {
                EnumFacing r;
                if (this.trapExtend.getValue().booleanValue() && (r = this.getFacingRelativeToPlayer(trap, this.getPlayer())) != null) {
                    surrounding.add(this.getPlayerPos().offset(r, 2));
                }
                surrounding.remove(trap);
            }
        }
        return surrounding;
    }

    protected BlockPos getPlayerPos() {
        return this.deltaY.getValue() != false && Math.abs(this.getPlayer().motionY) > 0.1 ? new BlockPos(this.getPlayer()) : PositionUtil.getPosition(this.getPlayer());
    }

    @Override
    public EntityPlayer getPlayerForRotations() {
        EntityOtherPlayerMP target;
        if (FREECAM.isEnabled() && (target = ((Freecam)FREECAM.get()).getPlayer()) != null) {
            return target;
        }
        return Surround.mc.player;
    }

    @Override
    public EntityPlayer getPlayer() {
        EntityOtherPlayerMP target;
        if (this.freecam.getValue() == SurroundFreecamMode.Origin && FREECAM.isEnabled() && (target = ((Freecam)FREECAM.get()).getPlayer()) != null) {
            return target;
        }
        return Surround.mc.player;
    }
}
