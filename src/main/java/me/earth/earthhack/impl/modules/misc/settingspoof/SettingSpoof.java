package me.earth.earthhack.impl.modules.misc.settingspoof;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.modules.misc.settingspoof.ChatVisibilityTranslator;
import me.earth.earthhack.impl.modules.misc.settingspoof.HandTranslator;
import me.earth.earthhack.impl.modules.misc.settingspoof.ListenerSettings;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.util.EnumHandSide;

public class SettingSpoof
extends Module {
    protected final Setting<Boolean> spoofLanguage = this.register(new BooleanSetting("Spoof-Language", false));
    protected final Setting<String> language = this.register(new StringSetting("Language", "en_us"));
    protected final Setting<Boolean> spoofRender = this.register(new BooleanSetting("Spoof-RenderDistance", false));
    protected final Setting<Integer> renderDist = this.register(new NumberSetting<Integer>("RenderDistance", 32, -1, 128));
    protected final Setting<Boolean> spoofColors = this.register(new BooleanSetting("Spoof-ChatColors", false));
    protected final Setting<Boolean> chatColors = this.register(new BooleanSetting("ChatColors", true));
    protected final Setting<Boolean> spoofChat = this.register(new BooleanSetting("Spoof-Chat", false));
    protected final Setting<ChatVisibilityTranslator> chat = this.register(new EnumSetting<ChatVisibilityTranslator>("Chat", ChatVisibilityTranslator.Full));
    protected final Setting<Boolean> spoofModel = this.register(new BooleanSetting("Spoof-Model", false));
    protected final Setting<Integer> model = this.register(new NumberSetting<Integer>("Model", 0, 0, 64));
    protected final Setting<Boolean> spoofHand = this.register(new BooleanSetting("Spoof-Hand", false));
    protected final Setting<HandTranslator> hand = this.register(new EnumSetting<HandTranslator>("Hand", HandTranslator.Right));
    protected final Setting<Boolean> send = this.register(new BooleanSetting("Send", true));

    public SettingSpoof() {
        super("SettingSpoof", Category.Misc);
        this.listeners.add(new ListenerSettings(this));
        this.send.addObserver(e -> {
            e.setValue(true);
            this.sendPacket();
        });
    }

    public void sendPacket() {
        if (SettingSpoof.mc.player != null) {
            String lang = this.getLanguage(SettingSpoof.mc.gameSettings.language);
            int render = this.getRenderDistance(SettingSpoof.mc.gameSettings.renderDistanceChunks);
            EntityPlayer.EnumChatVisibility vis = this.getVisibility(SettingSpoof.mc.gameSettings.chatVisibility);
            boolean chatColors = this.getChatColors(SettingSpoof.mc.gameSettings.chatColours);
            int mask = 0;
            for (EnumPlayerModelParts enumplayermodelparts : SettingSpoof.mc.gameSettings.getModelParts()) {
                mask |= enumplayermodelparts.getPartMask();
            }
            int modelParts = this.getModelParts(mask);
            EnumHandSide handSide = this.getHandSide(SettingSpoof.mc.gameSettings.mainHand);
            NetworkUtil.sendPacketNoEvent(new CPacketClientSettings(lang, render, vis, chatColors, modelParts, handSide));
        }
    }

    public String getLanguage(String languageIn) {
        return this.spoofLanguage.getValue() != false ? this.language.getValue() : languageIn;
    }

    public int getRenderDistance(int renderDistIn) {
        return this.spoofRender.getValue() != false ? this.renderDist.getValue() : renderDistIn;
    }

    public EntityPlayer.EnumChatVisibility getVisibility(EntityPlayer.EnumChatVisibility enumChatVisibilityIn) {
        return this.spoofChat.getValue() != false ? this.chat.getValue().getVisibility() : enumChatVisibilityIn;
    }

    public boolean getChatColors(boolean chatColorsIn) {
        return this.spoofChat.getValue() != false ? this.chatColors.getValue() : chatColorsIn;
    }

    public int getModelParts(int modelPartsIn) {
        return this.spoofModel.getValue() != false ? this.model.getValue() : modelPartsIn;
    }

    public EnumHandSide getHandSide(EnumHandSide enumHandSideIn) {
        return this.spoofHand.getValue() != false ? this.hand.getValue().getHandSide() : enumHandSideIn;
    }
}
