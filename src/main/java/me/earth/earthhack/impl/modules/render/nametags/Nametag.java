/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.network.NetHandlerPlayClient
 *  net.minecraft.client.network.NetworkPlayerInfo
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 */
package me.earth.earthhack.impl.modules.render.nametags;

import java.util.ArrayList;
import java.util.List;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.modules.render.nametags.Nametags;
import me.earth.earthhack.impl.modules.render.nametags.StackRenderer;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class Nametag
implements Globals {
    private static final ModuleCache<Media> MEDIA = Caches.getModule(Media.class);
    private final Nametags module;
    public final EntityPlayer player;
    public final StackRenderer mainHand;
    public final List<StackRenderer> stacks;
    public final String nameString;
    public final int nameColor;
    public final int nameWidth;
    public int maxEnchHeight;
    public boolean renderDura;
    public static boolean isRendering;

    public Nametag(Nametags module, EntityPlayer player) {
        this.module = module;
        this.player = player;
        this.stacks = new ArrayList<StackRenderer>(6);
        ItemStack mainStack = player.getHeldItemMainhand();
        if (mainStack.isEmpty()) {
            this.mainHand = null;
        } else {
            boolean damageable;
            boolean bl = damageable = mainStack.isItemStackDamageable() && module.durability.getValue() != false;
            if (damageable) {
                this.renderDura = true;
            }
            this.mainHand = new StackRenderer(mainStack, damageable);
            this.calcEnchHeight(this.mainHand);
        }
        for (int i = 3; i > -1; --i) {
            this.addStack((ItemStack)player.inventory.armorInventory.get(i));
        }
        this.addStack(player.getHeldItemOffhand());
        this.nameColor = this.getColor(player);
        this.nameString = this.getName(player);
        this.nameWidth = Managers.TEXT.getStringWidth(this.nameString);
        for (StackRenderer sr : this.stacks) {
            this.calcEnchHeight(sr);
        }
    }

    private void calcEnchHeight(StackRenderer sr) {
        int enchHeight = EnchantmentHelper.getEnchantments((ItemStack)sr.getStack()).size();
        if (this.module.armor.getValue().booleanValue() && enchHeight > this.maxEnchHeight) {
            this.maxEnchHeight = enchHeight;
        }
    }

    private void addStack(ItemStack stack) {
        if (!stack.isEmpty()) {
            boolean damageable;
            boolean bl = damageable = stack.isItemStackDamageable() && this.module.durability.getValue() != false;
            if (damageable) {
                this.renderDura = true;
            }
            this.stacks.add(new StackRenderer(stack, damageable));
        }
    }

    private String getName(EntityPlayer player) {
        int pops;
        NetHandlerPlayClient connection;
        boolean offset;
        String name = player.getDisplayName().getFormattedText().trim();
        String s = this.module.media.getValue() != false ? MEDIA.returnIfPresent(m -> m.convert(name), name) : name;
        StringBuilder builder = new StringBuilder(s);
        boolean bl = offset = builder.toString().replaceAll("\u00c2\u00a7.", "").length() > 0;
        if (this.module.id.getValue().booleanValue()) {
            builder.append(offset ? " " : "").append("ID: ").append(player.getEntityId());
            offset = true;
        }
        if (this.module.gameMode.getValue().booleanValue()) {
            builder.append(offset ? " " : "").append("[").append(player.isCreative() ? "C" : (player.isSpectator() ? "I" : "S")).append("]");
            offset = true;
        }
        if ((connection = mc.getConnection()) != null) {
            NetworkPlayerInfo playerInfo = connection.getPlayerInfo(player.getUniqueID());
            if (this.module.ping.getValue().booleanValue() && playerInfo != null) {
                builder.append(offset ? " " : "").append(playerInfo.getResponseTime()).append("ms");
                offset = true;
            }
        }
        if (this.module.health.getValue().booleanValue()) {
            double health = Math.ceil(EntityUtil.getHealth((EntityLivingBase)player));
            String healthColor = health > 18.0 ? "\u00a7a" : (health > 16.0 ? "\u00a72" : (health > 12.0 ? "\u00a7e" : (health > 8.0 ? "\u00a76" : (health > 5.0 ? "\u00a7c" : "\u00a74"))));
            builder.append(offset ? " " : "").append(healthColor).append(health > 0.0 ? Integer.valueOf((int)health) : "0");
        }
        if (this.module.pops.getValue().booleanValue() && (pops = Managers.COMBAT.getPops((Entity)player)) != 0) {
            builder.append("\u00a7f").append(" -").append(pops);
        }
        return builder.toString();
    }

    private int getColor(EntityPlayer player) {
        BlockPos pos;
        IBlockState state;
        if (Managers.FRIENDS.contains(player)) {
            return -10027009;
        }
        if (this.module.burrow.getValue().booleanValue() && !(state = Nametag.mc.world.getBlockState(pos = PositionUtil.getPosition((Entity)player))).getMaterial().isReplaceable() && state.getBoundingBox((IBlockAccess)Nametag.mc.world, (BlockPos)pos).offset((BlockPos)pos).maxY > player.posY) {
            return -10026905;
        }
        if (Managers.ENEMIES.contains(player)) {
            return -65536;
        }
        if (player.isInvisible()) {
            return -56064;
        }
        if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(player.getUniqueID()) == null) {
            return -1113785;
        }
        if (player.isSneaking() && this.module.sneak.getValue().booleanValue()) {
            return -26368;
        }
        return -1;
    }
}

