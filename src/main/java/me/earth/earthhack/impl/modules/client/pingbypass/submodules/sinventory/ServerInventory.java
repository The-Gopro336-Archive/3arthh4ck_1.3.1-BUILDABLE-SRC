/*
 * Decompiled with CFR 0.150.
 */
package me.earth.earthhack.impl.modules.client.pingbypass.submodules.sinventory;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.module.impl.SimpleSubModule;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypass;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sinventory.ServerInventoryData;

public class ServerInventory
extends SimpleSubModule<PingBypass> {
    public ServerInventory(PingBypass pingBypass) {
        super(pingBypass, "S-Inventory", Category.Client);
        this.register(new NumberSetting<Integer>("Delay", 5, 1, 60));
        this.setData(new ServerInventoryData(this));
    }
}

