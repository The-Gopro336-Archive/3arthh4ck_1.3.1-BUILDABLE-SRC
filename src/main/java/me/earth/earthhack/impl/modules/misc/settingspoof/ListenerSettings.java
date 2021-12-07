package me.earth.earthhack.impl.modules.misc.settingspoof;

import me.earth.earthhack.impl.core.mixins.network.client.ICPacketClientSettings;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.settingspoof.SettingSpoof;
import net.minecraft.network.play.client.CPacketClientSettings;

final class ListenerSettings
extends ModuleListener<SettingSpoof, PacketEvent.Send<CPacketClientSettings>> {
    public ListenerSettings(SettingSpoof module) {
        super(module, PacketEvent.Send.class, CPacketClientSettings.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketClientSettings> event) {
        ICPacketClientSettings p = (ICPacketClientSettings)event.getPacket();
        p.setLang(((SettingSpoof)this.module).getLanguage(p.getLang()));
        p.setView(((SettingSpoof)this.module).getRenderDistance(p.getView()));
        p.setChatVisibility(((SettingSpoof)this.module).getVisibility(p.getChatVisibility()));
        p.setEnableColors(((SettingSpoof)this.module).getChatColors(p.getEnableColors()));
        p.setModelPartFlags(((SettingSpoof)this.module).getModelParts(p.getModelPartFlags()));
        p.setMainHand(((SettingSpoof)this.module).getHandSide(p.getMainHand()));
    }
}
