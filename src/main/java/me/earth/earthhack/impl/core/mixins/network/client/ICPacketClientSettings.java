/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer$EnumChatVisibility
 *  net.minecraft.network.play.client.CPacketClientSettings
 *  net.minecraft.util.EnumHandSide
 */
package me.earth.earthhack.impl.core.mixins.network.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={CPacketClientSettings.class})
public interface ICPacketClientSettings {
    @Accessor(value="lang")
    public void setLang(String var1);

    @Accessor(value="view")
    public void setView(int var1);

    @Accessor(value="chatVisibility")
    public void setChatVisibility(EntityPlayer.EnumChatVisibility var1);

    @Accessor(value="enableColors")
    public void setEnableColors(boolean var1);

    @Accessor(value="modelPartFlags")
    public void setModelPartFlags(int var1);

    @Accessor(value="mainHand")
    public void setMainHand(EnumHandSide var1);

    @Accessor(value="lang")
    public String getLang();

    @Accessor(value="view")
    public int getView();

    @Accessor(value="chatVisibility")
    public EntityPlayer.EnumChatVisibility getChatVisibility();

    @Accessor(value="enableColors")
    public boolean getEnableColors();

    @Accessor(value="modelPartFlags")
    public int getModelPartFlags();

    @Accessor(value="mainHand")
    public EnumHandSide getMainHand();
}

