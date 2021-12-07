/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.ItemStackHelper
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemShulkerBox
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntityShulkerBox
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 */
package me.earth.earthhack.impl.modules.misc.tooltips;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.core.ducks.entity.ITileEntityShulkerBox;
import me.earth.earthhack.impl.core.mixins.block.ITileEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.tooltips.ListenerLogout;
import me.earth.earthhack.impl.modules.misc.tooltips.ListenerPostToolTip;
import me.earth.earthhack.impl.modules.misc.tooltips.ListenerRender2D;
import me.earth.earthhack.impl.modules.misc.tooltips.ListenerTick;
import me.earth.earthhack.impl.modules.misc.tooltips.ListenerToolTip;
import me.earth.earthhack.impl.modules.misc.tooltips.ListenerWorldClient;
import me.earth.earthhack.impl.modules.misc.tooltips.ToolTipsData;
import me.earth.earthhack.impl.modules.misc.tooltips.util.TimeStack;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ToolTips
extends Module {
    public static final ResourceLocation SHULKER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
    public static final ResourceLocation MAP = new ResourceLocation("textures/map/map_background.png");
    protected final Setting<Boolean> shulkers = this.register(new BooleanSetting("Shulkers", true));
    protected final Setting<Boolean> maps = this.register(new BooleanSetting("Maps", true));
    protected final Setting<Boolean> shulkerSpy = this.register(new BooleanSetting("ShulkerSpy", true));
    protected final Setting<Boolean> own = this.register(new BooleanSetting("Own", true));
    protected final Setting<Bind> peekBind;
    protected final Map<String, TimeStack> spiedPlayers;

    public ToolTips() {
        super("ToolTips", Category.Misc);
        this.peekBind = this.register(new BindSetting("PeekBind", Bind.fromKey(ToolTips.mc.gameSettings.keyBindSneak.getKeyCode())));
        this.spiedPlayers = new ConcurrentHashMap<String, TimeStack>();
        this.listeners.add(new ListenerToolTip(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerRender2D(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerPostToolTip(this));
        this.setData(new ToolTipsData(this));
    }

    public boolean drawShulkerToolTip(ItemStack stack, int x, int y, String name) {
        NBTTagCompound blockEntityTag;
        NBTTagCompound tagCompound;
        if (stack != null && stack.getItem() instanceof ItemShulkerBox && (tagCompound = stack.getTagCompound()) != null && tagCompound.hasKey("BlockEntityTag", 10) && (blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag")).hasKey("Items", 9)) {
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            mc.getTextureManager().bindTexture(SHULKER_GUI_TEXTURE);
            Render2DUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
            Render2DUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 57, 500);
            Render2DUtil.drawTexturedRect(x, y + 70, 0, 160, 176, 8, 500);
            GlStateManager.disableDepth();
            Managers.TEXT.drawStringWithShadow(name == null ? stack.getDisplayName() : name, x + 8, y + 6, -1);
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            NonNullList nonNullList = NonNullList.withSize((int)27, (Object)ItemStack.EMPTY);
            ItemStackHelper.loadAllItems((NBTTagCompound)blockEntityTag, (NonNullList)nonNullList);
            for (int i = 0; i < nonNullList.size(); ++i) {
                int iX = x + i % 9 * 18 + 8;
                int iY = y + i / 9 * 18 + 18;
                ItemStack itemStack = (ItemStack)nonNullList.get(i);
                ToolTips.mc.getRenderItem().zLevel = 501.0f;
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, iX, iY);
                mc.getRenderItem().renderItemOverlayIntoGUI(ToolTips.mc.fontRenderer, itemStack, iX, iY, null);
                ToolTips.mc.getRenderItem().zLevel = 0.0f;
            }
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            return true;
        }
        return false;
    }

    public void displayInventory(ItemStack stack, String name) {
        try {
            Item item = stack.getItem();
            TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
            ItemShulkerBox shulker = (ItemShulkerBox)item;
            ((ITileEntity)entityBox).setBlockType(shulker.getBlock());
            entityBox.setWorld((World)ToolTips.mc.world);
            ItemStackHelper.loadAllItems((NBTTagCompound)stack.getTagCompound().getCompoundTag("BlockEntityTag"), ((ITileEntityShulkerBox)entityBox).getItems());
            entityBox.readFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
            entityBox.setCustomName(name == null ? stack.getDisplayName() : name);
            ToolTips.mc.player.displayGUIChest((IInventory)entityBox);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ItemStack getStack(String name) {
        TimeStack stack = this.spiedPlayers.get(name.toLowerCase());
        if (stack != null) {
            return stack.getStack();
        }
        return null;
    }

    public Set<String> getPlayers() {
        return this.spiedPlayers.keySet();
    }
}

