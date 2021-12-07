/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.anvilaura.AnvilAura;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.misc.nuker.Nuker;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.ArmUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

final class ListenerUpdate
extends ModuleListener<Speedmine, UpdateEvent> {
    private static final ModuleCache<Nuker> NUKER = Caches.getModule(Nuker.class);
    private static final ModuleCache<AutoCrystal> AUTOCRYSTAL = Caches.getModule(AutoCrystal.class);
    private static final ModuleCache<AnvilAura> ANVIL_AURA = Caches.getModule(AnvilAura.class);
    private static final SettingCache<Boolean, BooleanSetting, Nuker> NUKE = Caches.getSetting(Nuker.class, BooleanSetting.class, "Nuke", false);

    public ListenerUpdate(Speedmine module) {
        super(module, UpdateEvent.class, -10);
    }

    private EntityPlayer getPlacePlayer(BlockPos pos) {
        for (EntityPlayer player : ListenerUpdate.mc.world.playerEntities) {
            if (Managers.FRIENDS.contains(player) || player == ListenerUpdate.mc.player) continue;
            BlockPos playerPos = PositionUtil.getPosition((Entity)player);
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                if (!playerPos.offset(facing).equals((Object)pos)) continue;
                return player;
            }
            if (!playerPos.offset(EnumFacing.UP).offset(EnumFacing.UP).equals((Object)pos)) continue;
            return player;
        }
        return null;
    }

    @Override
    public void invoke(UpdateEvent event) {
        ((Speedmine)this.module).checkReset();
        if (PlayerUtil.isCreative((EntityPlayer)ListenerUpdate.mc.player) || NUKER.isEnabled() && NUKE.getValue().booleanValue() || ANVIL_AURA.isEnabled() && ((AnvilAura)ANVIL_AURA.get()).isMining()) {
            return;
        }
        ((IPlayerControllerMP)ListenerUpdate.mc.playerController).setBlockHitDelay(0);
        if (!((Speedmine)this.module).multiTask.getValue().booleanValue() && (((Speedmine)this.module).noReset.getValue().booleanValue() || ((Speedmine)this.module).mode.getValue() == MineMode.Reset) && ListenerUpdate.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            ((IPlayerControllerMP)ListenerUpdate.mc.playerController).setIsHittingBlock(false);
        }
        if (((Speedmine)this.module).pos != null) {
            if ((((Speedmine)this.module).mode.getValue() == MineMode.Smart || ((Speedmine)this.module).mode.getValue() == MineMode.Instant || ((Speedmine)this.module).mode.getValue() == MineMode.Civ) && ListenerUpdate.mc.player.getDistanceSq(((Speedmine)this.module).pos) > (double)MathUtil.square(((Speedmine)this.module).range.getValue().floatValue())) {
                ((Speedmine)this.module).abortCurrentPos();
                return;
            }
            if (((Speedmine)this.module).mode.getValue() == MineMode.Civ && ((Speedmine)this.module).facing != null && !BlockUtil.isAir(((Speedmine)this.module).pos) && !((Speedmine)this.module).isPausing() && ((Speedmine)this.module).delayTimer.passed(((Speedmine)this.module).realDelay.getValue().intValue())) {
                ArmUtil.swingPacket(EnumHand.MAIN_HAND);
                ((Speedmine)this.module).sendStopDestroy(((Speedmine)this.module).pos, ((Speedmine)this.module).facing, false);
            }
            ((Speedmine)this.module).maxDamage = 0.0f;
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = ListenerUpdate.mc.player.inventory.getStackInSlot(i);
                ((Speedmine)this.module).damages[i] = MathUtil.clamp(((Speedmine)this.module).damages[i] + MineUtil.getDamage(stack, ((Speedmine)this.module).pos, ((Speedmine)this.module).onGround.getValue()), 0.0f, Float.MAX_VALUE);
                if (!(((Speedmine)this.module).damages[i] > ((Speedmine)this.module).maxDamage)) continue;
                ((Speedmine)this.module).maxDamage = ((Speedmine)this.module).damages[i];
            }
            if (((Speedmine)this.module).normal.getValue().booleanValue()) {
                int fastSlot = -1;
                for (int i = 0; i < ((Speedmine)this.module).damages.length; ++i) {
                    if (!(((Speedmine)this.module).damages[i] >= ((Speedmine)this.module).limit.getValue().floatValue())) continue;
                    fastSlot = i;
                    if (i == ListenerUpdate.mc.player.inventory.currentItem) break;
                }
                if ((((Speedmine)this.module).damages[ListenerUpdate.mc.player.inventory.currentItem] >= ((Speedmine)this.module).limit.getValue().floatValue() || ((Speedmine)this.module).swap.getValue().booleanValue() && fastSlot != -1) && (!((Speedmine)this.module).checkPacket.getValue().booleanValue() || !((Speedmine)this.module).sentPacket)) {
                    int lastSlot = -1;
                    if (((Speedmine)this.module).swap.getValue().booleanValue()) {
                        lastSlot = ListenerUpdate.mc.player.inventory.currentItem;
                        InventoryUtil.switchTo(fastSlot);
                    }
                    boolean toAir = ((Speedmine)this.module).toAir.getValue();
                    InventoryUtil.syncItem();
                    if (((Speedmine)this.module).sendStopDestroy(((Speedmine)this.module).pos, ((Speedmine)this.module).facing, toAir)) {
                        ((Speedmine)this.module).postSend(toAir);
                    }
                    if (lastSlot != -1) {
                        InventoryUtil.switchTo(lastSlot);
                    }
                }
                return;
            }
            int pickSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE, new Item[0]);
            if (((Speedmine)this.module).damages[ListenerUpdate.mc.player.inventory.currentItem] >= ((Speedmine)this.module).limit.getValue().floatValue() || pickSlot >= 0 && ((Speedmine)this.module).damages[pickSlot] >= ((Speedmine)this.module).limit.getValue().floatValue() && !((Speedmine)this.module).pausing && ((Bind)((Speedmine)this.module).breakBind.getValue()).getKey() == -1) {
                int lastSlot = ListenerUpdate.mc.player.inventory.currentItem;
                EntityPlayer placeTarg = this.getPlacePlayer(((Speedmine)this.module).pos);
                if (placeTarg != null) {
                    BlockPos p = PlayerUtil.getBestPlace(((Speedmine)this.module).pos, placeTarg);
                    if (((Speedmine)this.module).placeCrystal.getValue().booleanValue() && AUTOCRYSTAL.isEnabled() && p != null && BlockUtil.canPlaceCrystal(p, false, false)) {
                        RayTraceResult result = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP, p);
                        if (ListenerUpdate.mc.player.getHeldItemOffhand() != ItemStack.EMPTY && ListenerUpdate.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                            CPacketPlayerTryUseItemOnBlock place = new CPacketPlayerTryUseItemOnBlock(p, result.sideHit, EnumHand.OFF_HAND, (float)result.hitVec.x, (float)result.hitVec.y, (float)result.hitVec.z);
                            CPacketAnimation animation = new CPacketAnimation(EnumHand.OFF_HAND);
                            InventoryUtil.syncItem();
                            ListenerUpdate.mc.player.connection.sendPacket((Packet)place);
                            ListenerUpdate.mc.player.connection.sendPacket((Packet)animation);
                        } else {
                            int crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL, new Item[0]);
                            if (crystalSlot != -1) {
                                InventoryUtil.switchTo(crystalSlot);
                                CPacketPlayerTryUseItemOnBlock place = new CPacketPlayerTryUseItemOnBlock(p, result.sideHit, EnumHand.MAIN_HAND, (float)result.hitVec.x, (float)result.hitVec.y, (float)result.hitVec.z);
                                CPacketAnimation animation = new CPacketAnimation(EnumHand.MAIN_HAND);
                                ListenerUpdate.mc.player.connection.sendPacket((Packet)place);
                                ListenerUpdate.mc.player.connection.sendPacket((Packet)animation);
                                InventoryUtil.switchTo(lastSlot);
                            }
                        }
                    }
                }
                if (((Speedmine)this.module).swap.getValue().booleanValue()) {
                    InventoryUtil.switchTo(pickSlot);
                }
                NetworkUtil.sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, ((Speedmine)this.module).pos, ((Speedmine)this.module).facing), false);
                if (((Speedmine)this.module).swap.getValue().booleanValue()) {
                    InventoryUtil.switchTo(lastSlot);
                }
                if (((Speedmine)this.module).toAir.getValue().booleanValue()) {
                    ListenerUpdate.mc.playerController.onPlayerDestroyBlock(((Speedmine)this.module).pos);
                }
                ((Speedmine)this.module).onSendPacket();
            }
        }
    }
}

