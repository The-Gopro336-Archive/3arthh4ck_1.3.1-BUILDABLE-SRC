/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.player.norotate;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.player.norotate.ListenerCPacket;
import me.earth.earthhack.impl.modules.player.norotate.ListenerPosLook;

public class NoRotate
extends Module {
    protected final Setting<Boolean> noForceLook = this.register(new BooleanSetting("NoForceLook", false));
    protected final Setting<Boolean> async = this.register(new BooleanSetting("Async", false));
    protected final Setting<Boolean> noSpoof = this.register(new BooleanSetting("NoThrowableSpoof", false));

    public NoRotate() {
        super("NoRotate", Category.Player);
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.addAll(new ListenerCPacket(this).getListeners());
    }
}

